package com.example.thermoshaker.util.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.ByteUtil;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.debug.DebugInterface;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.licheedev.myutils.LogPlus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HardUpDialog {
    public final static String MSG = HardUpDialog.class.getName();
    private static final String TAG = "HardUpDialog";
    byte[] ReceArray;
    Context context;
    Activity activity;
    private  ProgressBar pB_Level_up;
    private TextView tv_msg;
    private int leverState = 0;//0是没有升级或者升级失败，1是升级中 2是升级结束
    MyApplication app = MyApplication.getInstance();
    File file;
    public HardUpDialog(Context context, Activity activity, String file_path ) {
        this.context = context;
        this.activity = activity;
        file = new File(file_path);
        init();
    }


    /* 接收广播 */
    private BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bin = intent.getByteArrayExtra("serialport");
            if (bin != null) {
                ReceArray = bin;
            }
        }
    };

    protected void finalizeHan() throws Throwable {
        if (recevier != null) {
            app.unregisterReceiver(recevier);
            recevier = null;
        }
    }
    private void init() {
        /* 注册广播 */
        app.registerReceiver(recevier, new IntentFilter(MSG));
        CustomkeyDialog seniorDialog = new CustomkeyDialog.Builder(context)
                .view(R.layout.update_layout_dialog)
                .style(R.style.CustomDialog)
                .build();
        seniorDialog.show();
        /* 屏蔽键盘 */
        seniorDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        TextView title = seniorDialog.findViewById(R.id.title);
        tv_msg = seniorDialog.findViewById(R.id.tv_msg);
        title.setText(context.getString(R.string.firmwareupdate));
        pB_Level_up =seniorDialog.findViewById(R.id.pB_Level_up);
        pB_Level_up.setVisibility(View.VISIBLE);
        seniorDialog.findViewById(R.id.btn_next).setVisibility(View.GONE);
        seniorDialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(leverState==1){
                    ToastUtil.show(context,"正在升级，请勿重复点击");
                    return;
                }else if(leverState==2){
                    seniorDialog.dismiss();
                }else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hardUp(file);
                            pB_Level_up.setProgress(0);
                        }
                    }).start();
                }

            }
        });
        seniorDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seniorDialog.dismiss();
                try {
                    finalizeHan();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });





    }
    final Handler handler = new Handler();
    public void changeState(String text){

        handler.post(new Runnable() {
            @Override
            public void run() {
                tv_msg.setText(text);

            }
        });
    }

    public void hardUp(File file) {
        leverState = 1;
        app.isLever = true;
        Intent intentUart = new Intent(UartServer.MSG);
        long filecontent = file.length();
        if (!file.exists() || file.length() < 100){
            return;
        }
        pB_Level_up.setProgress(0);
        Log.i(TAG, "******  send upgrade order ******** ");
        intentUart.putExtra("serialport", new UartClass(MSG, UartType.OT_KEY_IAP_BYTE));
        MyApplication.getInstance().sendBroadcast(intentUart);
        changeState("正在准备升级");
        Log.d(TAG,readArray(10000)[0]+ "\n"+  Integer.toHexString(readArray(10000)[0] & 0xFF));
//        if (readArray(1000)[0] == 0) // 6
//        {
//            Log.i(TAG, "******  send upgrade order failed******** ");
//            return;
//        }

        // 第一帧命令,因为要擦除flash，所以需要很长时间
        Log.i(TAG, "******  first hardup_grade order ******** ");
        changeState("开始升级擦除数据");

        // 第一帧命令,因为要擦除flash，所以需要很长时间
        intentUart.putExtra("serialport",
                new UartClass(MSG, sendYmodemStartPacket(filecontent), false, 6000));
        MyApplication.getInstance().sendBroadcast(intentUart);
        Log.d(TAG,readArray(6000)[0]+ "\n"+  Integer.toHexString(readArray(6000)[0] & 0xFF));
        changeState("正在升级");

//        if (readArray(10000)[0] == (byte) 0x01) // 6
//        {
//            byte bytttt = readArray(10000)[0];
//            Log.d(TAG,bytttt+ "\n"+  Integer.toHexString(readArray(10000)[0] & 0xFF));
//            Log.i(TAG, "******  first hardup_grade order failed******** ");
//            return;
//
//
//        }
        int packetCnt = (filecontent % 64) == 0 ? (int) (filecontent / 64)
                : (int) (filecontent / 64) + 1;
        pB_Level_up.setMax(packetCnt);


        int fileReadCount = 0;
        byte[] data = new byte[64];
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            int available = fileStream.available();
            int i1 = available / 64;
            if(available % 64!=0){
                i1++;
            }
//            Log.d("zheshiyitiaochakanrizhi",i1+"   "+available+"  "+available % 64);
            for (int j = 1; j <= i1; j++) {

                int read = fileStream.read(data);
                if(read==0){

                }else {
                    for (int i = read; i < 64; i++) // 读取字节若不足64 空余字节填充0x1A
                        data[i] = (byte) 0xFF;
                }
                Log.i("zheshiyitiaochakanrizhi", j*64+"******  send packet "+j +"   "+ data.length + "******** "+ ByteUtil.bytesToHexString(data));
                    intentUart.putExtra("serialport", new UartClass(MSG, sendYmodemPacket(data, 64,(j-1)*64), false));
                    MyApplication.getInstance().sendBroadcast(intentUart);
                    if (readArray(300)[0] != (byte) 0x00){

                        leverState = 0;

                        Log.d(TAG,readArray(300)[0]+ "\n"+  Integer.toHexString(readArray(300)[0] & 0xFF));
                        Log.i(TAG, "******  send packet " +" failed ******** ");
                        changeState(context.getString(R.string.update_failed));

                        break;
                }
                    if(leverState==0){
                        return;
                    }

                pB_Level_up.setProgress(j);

            }
            byte[] lastbyte = new byte[64];
            for (int i = 0; i < 64; i++){
                lastbyte[i] = (byte) 0xFF;
            }
            intentUart.putExtra("serialport", new UartClass(MSG, sendYmodemPacketLast(lastbyte, 64), false));
            MyApplication.getInstance().sendBroadcast(intentUart);


        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        app.isLever = false;
        changeState(context.getString(R.string.dialog_hardup_stop));
         leverState = 2;


    }


    /* 读取数组 */
    public byte[] readArray(int timeOver) {
        byte[] ret = new byte[] { 0 };
        try {
            for (int i = 0; i < timeOver / 20; i++) {
                if (ReceArray != null)
                    if (ReceArray.length > 0) {
                        ret[0] = ReceArray[0];
                        ReceArray = null;
                        break;
                    }
                Thread.sleep(20);
            }
        } catch (Exception e) {
        }
        return ret;
    }
    // 写数据
    private byte[] sendYmodemPacket(byte[] data,
                                    int dataSize,int beginByte) {

        byte[] ret = new byte[72];
            ret[0] = 0x5a;
            ret[1] = 0x01;
            Utils.intTobyteArray(beginByte,ret,2);
            ret[4] = 0x00;
            ret[5] = 0x00;
            int sum = (ret[1]& 0xFF )+(ret[2]& 0xFF )+(ret[3]& 0xFF );
            ret[6] = (byte) (sum);
            int hou=0;
            for (int i = 0; i < dataSize; i++) {
                ret[7 + i] = data[i];
                hou += data[i]& 0xFF ;
            }
//            Log.i("chakanshurudecanshu", hou+"        "+ByteUtil.bytesToHexString(data));
            ret[71] = (byte) (hou >> 8);

        return ret;



    }
    // 写数据最后一帧数据
    private byte[] sendYmodemPacketLast(byte[] data,
                                    int dataSize) {

        byte[] ret = new byte[72];
        ret[0] = 0x5a;
        ret[1] = 0x01;
        ret[2] = 0x00;
        ret[3] = 0x00;
        ret[4] = 0x00;
        ret[5] = 0x00;
        ret[6] = 0x01;
        for (int i = 0; i < dataSize; i++) {
            ret[7 + i] = data[i];

        }

        ret[71] = 0x00;

        return ret;



    }

    // 发送头一帧数据，包括文件名和文件大小
    private byte[] sendYmodemStartPacket(long fileStream) {
        try {
            Log.d(TAG,fileStream+"  "+fileStream/512);
            byte[] ret = new byte[7];
            ret[0] = 0x5a;
            ret[1] = 0x00;
            ret[2] = 0x00;
            ret[3] = 0x00;
            ret[4] = 0x00;
            ret[5] = (byte) (fileStream/512);

            ret[6] = (byte) (fileStream/512);

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

package com.example.thermoshaker.util.dialog;

import static org.greenrobot.eventbus.EventBus.TAG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MainType;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.debug.DebugInterface;
import com.example.thermoshaker.serial.uart.running.TdfileRunInterface;
import com.example.thermoshaker.ui.adapter.MyAdapter;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.example.thermoshaker.util.key.FloatingKeyboard;
import com.flyco.tablayout.SlidingTabLayout;
import com.licheedev.myutils.LogPlus;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FactoryDialog {
    public final static String MSG = FactoryDialog.class.getName();
    private static final String TAG = "FactoryDialog";

    Context context;
    Activity activity;
    private FloatingKeyboard keyboardview;
    private final int hanMsg = 1;
    MyApplication app = MyApplication.getInstance();

    //温度
    private EditText[] editTexts = new EditText[5];
    private TextView factory_temperature_text2,factory_temperature_text1;
    private EditText ed_deviceNo;
    public FactoryDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        init();
        QueryAdjust();
    }

    /* 时间定时器 */
    public Handler han = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            TdfileRunInterface runningClass = app.runningClass;
            factory_temperature_text1.setText(runningClass.getBlockTemp1AStr() + " ℃");
            factory_temperature_text1.setText(runningClass.getLidModuleTempStr() + " ℃");

            han.sendEmptyMessageDelayed(hanMsg, app.appClass.getTdRecordTime());
            return false;
        }
    });

    private void QueryAdjust() {

        MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(MSG, UartType.ASK_TEMPADJ_BYTE)));
        han.sendEmptyMessageDelayed(hanMsg, app.appClass.getTdRecordTime());


    }
    /* 接收广播 */
    private BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* 如果打开串口则接收数据 */
            try {
                byte[] bin = intent.getByteArrayExtra("serialport");
                if (bin != null) {
                    if (app.adjustClass.analysis(bin)) {
                        getadjust();
                    } else if (app.systemClass.analysis(bin)) {
                        String numStr = app.systemClass.getDeviceNum();
                        app.appClass.setMachineNumber(numStr);
                        ed_deviceNo.setText(numStr);
                    }
                }
            } catch (Exception e) {
                LogPlus.d(TAG, "recevier! " + e.getMessage());
            }
        }
    };
    private void init() {
        /* 注册广播 */
        app.registerReceiver(recevier, new IntentFilter(MSG));
        ArrayList<View> viewList = new ArrayList<>();
        String[] title={context.getString(R.string.Temperaturecorrection)+"",context.getString(R.string.Localcorrelation)+""};



        CustomkeyDialog seniorDialog = new CustomkeyDialog.Builder(context)
                .view(R.layout.debug_dialog_layout)
                .style(R.style.CustomDialog)
                .build();
        seniorDialog.show();
        /* 屏蔽键盘 */
        seniorDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        Button btnCancel = seniorDialog.findViewById(R.id.btnCancel);
        keyboardview = seniorDialog.findViewById(R.id.keyboardview);

        keyboardview.setfocusCurrent(seniorDialog.getWindow());
        keyboardview.setFinishAction(MSG);
        keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
        keyboardview.setPreviewEnabled(false);
        ViewPager vp_debug = seniorDialog.findViewById(R.id.vp_debug);
        SlidingTabLayout stb_Debug = seniorDialog.findViewById(R.id.stb_Debug);

        viewList.add(LayoutInflater.from(context).inflate(R.layout.functionmodule_dialog_vp_layout,null,false));
        viewList.add(LayoutInflater.from(context).inflate(R.layout.localcorrelation_dialog_vp_layout,null,false));
        vp_debug.setAdapter(new MyAdapter(viewList));
        stb_Debug.setViewPager(vp_debug,title);



        Resources resources = context.getResources();
        editTexts[0] = viewList.get(0).findViewById(R.id.factory_temperature_edit1);
        editTexts[1] = viewList.get(0).findViewById(R.id.factory_temperature_edit9);
        editTexts[2] = viewList.get(0).findViewById(R.id.factory_temperature_edit17);
        editTexts[3] = viewList.get(0).findViewById(R.id.factory_temperature_edit25);
        editTexts[4] = viewList.get(0).findViewById(R.id.factory_temperature_edit33);
        for (int i = 0; i < 5; i++) {
               int  edit_id = resources.getIdentifier("factory_temperature_edit" + (i+1), "id", context.getPackageName());


            int finalI = i;
            editTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View arg0, boolean arg1) {
                    if (arg1) {
                        keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
                        keyboardview.show(arg0);

                    } else {
                        keyboardview.hide();

                        String str = editTexts[finalI].getText().toString().trim();
                        editTexts[finalI].setText(str + "");
                    }
                }
            });
        }


        factory_temperature_text1 = viewList.get(0).findViewById(R.id.factory_temperature_text1);
        factory_temperature_text2 = viewList.get(0).findViewById(R.id.factory_temperature_text2);
        Button factory_temperature_btn_write = viewList.get(0).findViewById(R.id.factory_temperature_btn_write);
        factory_temperature_btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_POS_BYTE)));

                adjustUpdate();
            }
        });

        ed_deviceNo = viewList.get(1).findViewById(R.id.ed_deviceNo);
//        ed_deviceNo.setEnabled(false);
        ed_deviceNo.setText(app.appClass.getMachineNumber());
        keyboardview.registerEditText(ed_deviceNo);
        ed_deviceNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
                    keyboardview.show(arg0);

                } else {
                    keyboardview.hide();

                    String str = ed_deviceNo.getText().toString().trim();
                    ed_deviceNo.setText(str + "");
                }
            }
        });
        Button btnWrInput = viewList.get(1).findViewById(R.id.btnWrInput);
        btnWrInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNumber();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seniorDialog.dismiss();
            }
        });
        List<MainType.CompanyEnum> listOem = new ArrayList<>();
        Button btn_one = viewList.get(1).findViewById(R.id.btn_one);
        btn_one.setText(MainType.CompanyEnum.NULL.name());
        Button btn_two = viewList.get(1).findViewById(R.id.btn_two);
        btn_two.setText(MainType.CompanyEnum.himedia.name());
        btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,btn_one.getText().toString().trim()+"");

                setCompanyLogo("bootanimation.zip", "/td/bootanimation.zip", "nologo",
                        0);
            }
        });
        btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,btn_two.getText().toString().trim()+"");
                setCompanyLogo("himedia/bootanimation.zip", "/td/himedia/bootanimation.zip", "himedia",
                        0);
            }
        });

    }






    /* 写设备编号 */
    public void writeNumber() {
        try {
            app.systemClass.setDeviceNum(ed_deviceNo.getText().toString().trim());
            Intent intent1 = new Intent(UartServer.MSG);
            intent1.putExtra("serialport", new UartClass(null, app.systemClass.output()));
            app.sendBroadcast(intent1);
            intent1.putExtra("serialport", new UartClass(MSG, UartType.ASK_SYSTEM_BYTE));
            app.sendBroadcast(intent1);

        } catch (Exception e) {
            LogPlus.d(TAG, "writeNumber! " + e.getMessage());
        }
    }
    /* 校准 */
    public void adjustUpdate() {
        try {
            String str0 = "";
            String str1 = "";
            String str2 = "";
            String str3 = "";
            String str4= "";

            int pos = 0;
            str0 = editTexts[0].getText().toString();
            str1 = editTexts[1].getText().toString();
            str2 = editTexts[2].getText().toString();
            str3 = editTexts[3].getText().toString();
            str4 = editTexts[4].getText().toString();

                        if (app.adjustClass.setTemp95Adj(pos, Float.valueOf(str0)) == false) {
                            ToastUtil.show(context, "95℃-" + str0 + " Out of range!");
                            return;
                        }

                        if (app.adjustClass.setTemp72Adj(pos, Float.valueOf(str1)) == false) {
                            ToastUtil.show(context, "72℃-" + str1 + " Out of range!");

                            return;
                        }

                        if (app.adjustClass.setTemp55Adj(pos, Float.valueOf(str2)) == false) {
                            ToastUtil.show(context,"55℃-" + str2 + " Out of range!");

                            return;
                        }

                        if (app.adjustClass.setTemp31Adj(pos, Float.valueOf(str3)) == false) {
                            ToastUtil.show(context, "31℃-" + str3 + " Out of range!");

                            return;
                        }
                        if (app.adjustClass.setTemp04Adj(pos, Float.valueOf(str4)) == false) {
                            ToastUtil.show(context,"4℃-" + str4 + " Out of range!");

                            return;
                        }
            Intent intent1 = new Intent(UartServer.MSG);
            intent1.putExtra("serialport", new UartClass(null, app.adjustClass.output()));
            app.sendBroadcast(intent1);

            Intent intent2 = new Intent(UartServer.MSG);
            intent2.putExtra("serialport", new UartClass(MSG, UartType.ASK_TEMPADJ_BYTE));
            app.sendBroadcast(intent2);

        } catch (Exception e) {
            ToastUtil.show(context,e.getMessage()+"");
        }
    }

    /**
     * 设置值
     */
    public void getadjust() {
         editTexts[0].setText(app.adjustClass.getTemp95AdjStr(0));
        editTexts[1].setText(app.adjustClass.getTemp72AdjStr(0));
        editTexts[2].setText(app.adjustClass.getTemp55AdjStr(0));
        editTexts[3].setText(app.adjustClass.getTemp31AdjStr(0));
        editTexts[4].setText(app.adjustClass.getTemp04AdjStr(0));
        }

    /* 设置公司logo,动画源路径，动画目标路径，公司名称，公司枚举，公司图标 */
    private void setCompanyLogo(String stris, String strpath, String name, int companyRes) {
        MyApplication app = MyApplication.getInstance();
        try {
            InputStream is = app.getAssets().open(stris);
            String path = app.getFilesDir().getPath() + strpath;
            File file = new File(path);

            /* 判断文件是否存在或是否为空 */
            if (!file.exists() || file.length() < 100)
                FileUtils.copyInputStreamToFile(is, file);

            boolean bool = app.exec("mount -o remount,rw /system;cp " + path
                    + " /system/media/bootanimation.zip;mount -o remount,ro /system");
            Log.d(TAG, bool + "    bool");

            boolean exec = app.exec("mount -o remount,rw /system;chmod 777 /system/media/bootanimation.zip");
            Log.d(TAG, exec + "    exec");


        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage() + "");
        }

    }








}

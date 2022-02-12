package com.example.thermoshaker.serial.uart.upgrade;


import static com.example.thermoshaker.serial.DataUtils.HexToByteArr;
import static com.example.thermoshaker.serial.DataUtils.byteMergerAll;
import static com.example.thermoshaker.serial.DataUtils.crc16;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;

import java.io.File;
import java.io.FileInputStream;


public class DialogHardUp extends Dialog {
    private static final String TAG = "DialogHardUp";

    private DialogHardUp(Context context) {
        super(context, R.style.CustomDialog);
    }

    public static class Builder {
        private DialogHardUp mdialog;
        private View view;
        private TextView tv_msg;
        private ProgressBar pB_Level_up;
        private MyApplication MyApp;
        private Context context;
        byte[] ReceArray;
        private boolean isUp = false;
        private boolean isSucceed = false;

        private File file;

        private final int PROGRESSBAR_FLAG = 0;
        private final int UPGRADE_INFO_FAILED = 1;
        private final int UPGRADE_INFO_SUCCEED = 2;
        private final int UPGRADE_INFO_RUNNING = 3;
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESSBAR_FLAG: {
                        pB_Level_up.setProgress(msg.arg1);
                        break;
                    }
                    case UPGRADE_INFO_FAILED: {
                        tv_msg.setText(R.string.dialog_hardup_fail);
                        break;
                    }
                    case UPGRADE_INFO_SUCCEED: {
                        Log.i(TAG, "***** STOP ******");
                        tv_msg.setText(R.string.dialog_hardup_stop);
                        pB_Level_up.setProgress(100);
                        mdialog.findViewById(R.id.btn_sure).setVisibility(View.VISIBLE);
                        mdialog.findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
                        file = null;
                        break;
                    }
                    case UPGRADE_INFO_RUNNING: {
                        tv_msg.setText(R.string.dialog_hardup_running);
                        MyApp.baudrate = 115200;
                        break;
                    }
                }
            }
        };

        public Builder(final Context context,MyApplication myApplication ,String file_path) {
            mdialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mdialog = new DialogHardUp(context);
            this.MyApp = myApplication;
            this.context = context;
            file = new File(file_path);
            view = LayoutInflater.from(context).inflate(R.layout.update_layout_dialog, null);
            Window dialogWindow = mdialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);

            dialogWindow.setAttributes(lp);
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mdialog.setContentView(view);
            TextView title = mdialog.findViewById(R.id.title);
            title.setText(context.getString(R.string.firmwareupdate));

            tv_msg = mdialog.findViewById(R.id.tv_msg);
            pB_Level_up = mdialog.findViewById(R.id.pB_Level_up);
            mdialog.findViewById(R.id.btn_sure).setVisibility(View.GONE);
            mdialog.findViewById(R.id.btn_next).setVisibility(View.GONE);
            mdialog.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            mdialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUp) {
                        isUp = true;
//                        MyApp.debug_flag = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hardUp(file);
                                } catch (Exception e) {
                                    Log.d(TAG,e.toString());
                                    mdialog.cancel();

                                }
                            }
                        }).start();
                    }
                    if (isSucceed) {
                        mdialog.cancel();
                    }
                }
            });
            mdialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isUp) {
                        mdialog.cancel();
                    }
                    if (isSucceed) {
                        mdialog.cancel();
                    }
                }
            });
            mdialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            //布局位于状态栏下方
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            //全屏
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            //隐藏导航栏
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    if (Build.VERSION.SDK_INT >= 19) {
                        uiOptions |= 0x00001000;
                    } else {
                        uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                    }
                    mdialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                }
            });



        }


        public DialogHardUp create() {
            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
            mdialog.setCanceledOnTouchOutside(false);   //用户可以点击外部来关闭 Dialog
            mdialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Log.i(TAG, "dialog is exit");
                    file = null;
//                    MyApp.debug_flag = false;
                }
            });
            return mdialog;
        }

        public void hardUp(File file) throws Exception {
            Log.i(TAG, "******  start hardup_grade ******** ");
            int progress = 0;
            long filecontent = file.length();

            Message message = new Message();
            message.what = PROGRESSBAR_FLAG;
            message.arg1 = progress;
            handler.sendMessage(message);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "******  send upgrade order ******** ");

            byte[] b1 = {(byte) 0xAA};
            byte b2 = ControlParam.head_order;
            byte b3 = ControlParam.OT_UPDATE_APP;
            byte[] temp = {b2, b3};
            byte[] b5 = HexToByteArr(crc16(temp));
            byte[] b7 = {(byte) 0x55};
            byte[] All = byteMergerAll(b1, temp, b5, b7);
            if (CommandDateUtil.sendDataUpdate(All,1000) == null) {
                //Toast.makeText(context,"upgrade is failed",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "******  send upgrade order failed******** ");
                Message msg = new Message();
                msg.what = UPGRADE_INFO_FAILED;
                handler.sendMessage(msg);
                isUp = false;
                return;
            }

            {
                Message msg = new Message();
                msg.what = UPGRADE_INFO_RUNNING;
                handler.sendMessage(msg);
            }

            Log.i(TAG, "******  start 0x31 ******** ");
            //发送准备就绪
            if (CommandDateUtil.sendDataUpdate(new byte[]{0x31},1000)[0] == 0) {
                //Toast.makeText(context,"upgrade is failed",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "******  start 0x31 failed******** ");
                Message msg = new Message();
                msg.what = UPGRADE_INFO_FAILED;
                handler.sendMessage(msg);
                isUp = false;
                return;
            }

            //发送数据包
            progress++;
            Message message1 = new Message();
            message1.what = PROGRESSBAR_FLAG;
            message1.arg1 = progress;
            handler.sendMessage(message1);

            byte SOH = 0x01; /* start of 128-byte data packet */
            byte STX = 0x02; /* start of 1024-byte data packet */
            byte EOT = 0x04; /* end of transmission */
            byte ACK = 0x06; /* acknowledge */

            // const byte NAK = 0x15; /* negative acknowledge */
            // byte CRC16 = 0x43;/* 'C' == 0x43, request 16-bit CRC */
            int crcSize = 2;
            byte PacketHead = 2;
            // int dataSize = Convert.ToInt32(cbx_PageSize.Text);
            int dataSize = 1024;

            /* header: 3 bytes */
            // int proprassVal = 0;
            byte packetNumber = 0;
            byte invertedPacketNumber = (byte) 255;
            /* data: 128 or 1024 bytes */
            byte[] data = new byte[dataSize];
            /* footer: 2 bytes */
            byte[] CRC = new byte[crcSize];

            /* get the file */
            // FileStream fileStream = new FileStream(@filePath, FileMode.Open,
            // FileAccess.Read);
            // FileInputStream fileStream = new FileInputStream(filePath);

            int packetCnt = (filecontent % dataSize) == 0 ? (int) (filecontent / dataSize)
                    : (int) (filecontent / dataSize) + 1;

            // progressBar1.Maximum = packetCnt;
            float stepNum = 99f / packetCnt;

            progress++;
            Message message2 = new Message();
            message2.what = PROGRESSBAR_FLAG;
            message2.arg1 = progress;
            handler.sendMessage(message2);

            // 第一帧命令,因为要擦除flash，所以需要很长时间
            Log.i(TAG, "******  first hardup_grade order ******** ");
            if (CommandDateUtil.sendDataUpdate(sendYmodemStartPacket(SOH, "Firmware.bin", filecontent),10000)[0] != (byte) ACK) {
                //Toast.makeText(context,"debug is failed",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "******  first hardup_grade order failed******** ");
                Message msg = new Message();
                msg.what = UPGRADE_INFO_FAILED;
                handler.sendMessage(msg);
                isUp = false;

                return;
            }

            /* send packets with a cycle until we send the last byte */
            int fileReadCount = 0;
            FileInputStream fileStream = null;
            try {
                fileStream = new FileInputStream(file);
                do {
                    /* 读入缓冲区中的总字节数。如果当前的字节数没有所请求那么多，则总字节数可能小于所请求的字节数；如果已到达流的末尾，则为零。 */
                    fileReadCount = fileStream.read(data, 0, dataSize);
                    if (fileReadCount == 0) {
                        break;// 数据包读取完毕
                    } else // 读取字节大于128 选择1024 字节传输
                    {
                        PacketHead = STX;
                        dataSize = 1024;
                        for (int i = fileReadCount; i < dataSize; i++) // 读取字节若不足1024 空余字节填充0x1A
                            data[i] = (byte) 0xFF;
                    }

                    /* calculate packetNumber */
                    packetNumber++;
                    if (packetNumber > 255)
                        packetNumber = 0;
                    /* calculate invertedPacketNumber */
                    invertedPacketNumber = (byte) (255 - packetNumber);

                    /* calculate CRC */
                    Crc16Ccitt crc16Ccitt = new Crc16Ccitt(Crc16Ccitt.InitialCrcValue.Zeros);
                    CRC = crc16Ccitt.ComputeChecksumBytes(data);
                    /* send the packet */
                    Log.i(TAG, "******  send packet " + stepNum + "******** ");
                    if (CommandDateUtil.sendDataUpdate(sendYmodemPacket(PacketHead, packetNumber,
                            invertedPacketNumber, data, dataSize, CRC, crcSize),1000)[0] != (byte) ACK) {
                        //Toast.makeText(context, "debug is failed", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "******  send packet " + stepNum + " failed ******** ");
                        Message msg = new Message();
                        msg.what = UPGRADE_INFO_FAILED;
                        handler.sendMessage(msg);
                        isUp = false;
                        return;
                    }

                    progress += stepNum;
                    if (progress > 100)
                        progress = 100;
                    Message message3 = new Message();
                    message3.what = PROGRESSBAR_FLAG;
                    message3.arg1 = progress;
                    handler.sendMessage(message3);

                } while (fileReadCount == dataSize);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.i(TAG, "******  send EOT ******** ");
            if (CommandDateUtil.sendDataUpdate(new byte[]{EOT},1000)[0] != (byte) ACK) {
                //Toast.makeText(context, "upgrade is failed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "******  send EOT failed******** ");
                Message msg = new Message();
                msg.what = UPGRADE_INFO_FAILED;
                handler.sendMessage(msg);
                isUp = false;
                return;
            }

            /* send closing packet */
            packetNumber = 0;
            invertedPacketNumber = (byte) 255;
            data = new byte[dataSize];
            CRC = new byte[crcSize];
            Log.i(TAG, "******  send close file ******** ");
            if (CommandDateUtil.sendDataUpdate(sendYmodemClosingPacket(SOH, packetNumber, invertedPacketNumber, data, CRC, crcSize),1000)[0] != (byte) ACK) {
                //Toast.makeText(context, "upgrade is failed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "******  send close file failed******** ");
                Message msg = new Message();
                msg.what = UPGRADE_INFO_FAILED;
                handler.sendMessage(msg);
                isUp = false;
                return;
            }

            Message message4 = new Message();
            message4.what = UPGRADE_INFO_SUCCEED;
            handler.sendMessage(message4);
            isSucceed = true;
        }

        // 写数据
        private byte[] sendYmodemPacket(byte PacketHead, byte packetNumber, byte invertedPacketNumber, byte[] data,
                                        int dataSize, byte[] CRC, int crcSize) {
            byte[] ret = new byte[1 + 1 + 1 + dataSize + crcSize];
            ret[0] = PacketHead;
            ret[1] = packetNumber;
            ret[2] = invertedPacketNumber;
            for (int i = 0; i < dataSize; i++) {
                ret[3 + i] = data[i];
            }
            int pos = 3 + dataSize;
            for (int j = 0; j < crcSize; j++) {
                ret[pos + j] = CRC[j];
            }
            return ret;

        }

        // 发送头一帧数据，包括文件名和文件大小
        private byte[] sendYmodemStartPacket(byte SOH, String name, long fileStream) {
            try {
                String fileName = name;
                String fileSize = String.valueOf(fileStream);
                int StartdataSize = 128;
                int StartcrcSize = 2;
                byte StartpacketNumber = 0;
                byte StartinvertedPacketNumber = (byte) 255;
                byte[] data = new byte[StartdataSize];

                /* footer: 2 bytes */
                byte[] StartCRC = new byte[StartcrcSize];

                /* add filename to data */
                int i;
                for (i = 0; i < fileName.length() && (fileName.toCharArray()[i] != 0); i++) {
                    data[i] = (byte) fileName.toCharArray()[i];
                }
                data[i] = 0;

                /* add filesize to data */
                int j;
                for (j = 0; j < fileSize.length() && (fileSize.toCharArray()[j] != 0); j++) {
                    data[(i + 1) + j] = (byte) fileSize.toCharArray()[j];
                }
                data[(i + 1) + j] = 0;

                /* fill the remaining data bytes with 0 */
                for (int k = ((i + 1) + j) + 1; k < StartdataSize; k++) {
                    data[k] = 0;
                }

                /* calculate CRC */
                Crc16Ccitt crc16Ccitt = new Crc16Ccitt(Crc16Ccitt.InitialCrcValue.Zeros);
                StartCRC = crc16Ccitt.ComputeChecksumBytes(data);

                /* send the packet */
                return sendYmodemPacket(SOH, StartpacketNumber, StartinvertedPacketNumber, data, StartdataSize, StartCRC,
                        StartcrcSize);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // 最后一帧
        private byte[] sendYmodemClosingPacket(byte SOH, byte packetNumber, byte invertedPacketNumber, byte[] data,
                                               byte[] CRC, int crcSize) {
            int dataSize = 128;

            /* calculate CRC */
            Crc16Ccitt crc16Ccitt = new Crc16Ccitt(Crc16Ccitt.InitialCrcValue.Zeros);
            CRC = crc16Ccitt.ComputeChecksumBytes(data);

            /* send the packet */
            return sendYmodemPacket(SOH, packetNumber, invertedPacketNumber, data, dataSize, CRC, crcSize);

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

    }

    @Override
    public void show() {
        if (this.getWindow() != null) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            super.show();
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }
}

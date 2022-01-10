package com.example.thermoshaker.util.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.util.BroadcastManager;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.licheedev.myutils.LogPlus;

import java.io.File;
import java.security.Provider;

public class BioHeartService extends Service {
    private static final String TAG = "BioHeartService";
    //起始符
    static byte[] b1 = {(byte) 0xAA};
    //b2 通讯体类型
    //b3 通讯命令
    //b4 数据长度
    //b5 具体数据
    //b6 校验码
    static byte[] b7 = {(byte) 0x55};
    private RunningThread runningThread;
    public SerialPortManager mSerialPortManagerBio;
    //错误重复次数
    private final int REPEAT_TIME = 10;
    private byte[] res;
    private Intent intent = new Intent();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runningThread = new RunningThread();
        runningThread.start();
        mSerialPortManagerBio = new SerialPortManager();
        File file = new File("/dev/ttyS3");
        OpenSerialPort(new Device("ttyS3", "uart",file));
        mSerialPortManagerBio.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                res = bytes;
                Log.d(TAG,"onDataReceived：  "+DataUtils.ByteArrToHex(bytes));
            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.d(TAG,"onDataSent：  "+DataUtils.ByteArrToHex(bytes));

            }
        });
    }
    /**
     * 每次通过startService()方法启动Service时都会被回调。
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("===", "onStartCommand invoke");
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 打开串口
     */
    private void OpenSerialPort(Device device) {
        mSerialPortManagerBio = new SerialPortManager();

        // 打开串口
         mSerialPortManagerBio.openSerialPort(device.getFile(), 57600);


    }

    private class RunningThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                writeNewData();
            }

        }
    }



    private void writeNewData() {
        byte b2 = ControlParam.head_query;
        byte b3 = ControlParam.ASK_RUNDATA;
        byte[] temp = {b2, b3};
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        mSerialPortManagerBio.sendBytes(All);
        for (int i = 0; i < 5; i++) {
            SystemClock.sleep(3000);

            if(res!=null){

            }else {
                mSerialPortManagerBio.sendBytes(All);
            }
        }

        Log.d(TAG, "发送失败：" + "the mechine return datafile is error");
        BroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(BaseActivity.ERROR_ACTION);

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        /* 停止线程 */
        if (runningThread != null) {
            runningThread.interrupt();
            runningThread = null;
        }
    }
}

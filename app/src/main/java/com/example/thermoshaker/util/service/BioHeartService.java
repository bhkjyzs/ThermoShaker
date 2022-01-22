package com.example.thermoshaker.util.service;

import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;
import static com.example.thermoshaker.serial.DataUtils.HexToByteArr;
import static com.example.thermoshaker.serial.DataUtils.byteMergerAll;
import static com.example.thermoshaker.serial.DataUtils.crc16;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.DataUtils;

import com.licheedev.myutils.LogPlus;

import java.io.InputStream;
import java.io.OutputStream;

public class BioHeartService extends Service {
    private static final String LOG_TAG = "BioHeartService";

    MyApplication myApp;
    private RunningThread runningThread;

    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        System.out.println("onCreate BioHeartService");
        super.onCreate();
        runningThread = new RunningThread();
        runningThread.start();
        myApp = (MyApplication) getApplication();
        initSerial();

    }

    public void initSerial(){

        mSerialPort = myApp.getSerialPort();

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();

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
     * 服务销毁时的回调
     */
    @Override
    public void onDestroy() {
        Log.d("===", "onDestroy invoke");
        Content.running_flag = false;

        /* 停止线程 */
        if (runningThread != null) {
            runningThread.interrupt();
            runningThread = null;
        }

        closeSerialPort();
        super.onDestroy();
    }


    private class RunningThread extends Thread {
        @Override
        public void run() {
            while (Content.running_flag && !isInterrupted()) {
                writeNewData();
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogPlus.d("===", "thread,id：" + Thread.currentThread().getName());
            }
        }
    }


    private Intent intent = new Intent();
    private Bundle bundle = new Bundle();
    private byte[] temp;
    private FileRunProgram runParam;


    //起始符
    static byte[] b1 = {(byte) 0xAA};
    //b2 通讯体类型
    //b3 通讯命令
    //b4 数据长度
    //b5 具体数据
    //b6 校验码
    static byte[] b7 = {(byte) 0x55};

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private SerialPort mSerialPort; // 串口的类

    //错误重复次数
    private final int REPEAT_TIME = 10;


    //直接处理数据，不经过FutureTask。
    private void writeNewData() {
        intent.setAction("receiveParam");
        byte b2 = ControlParam.head_query;
        byte b3 = ControlParam.ASK_RUNDATA;
        byte[] b4 = {b2, b3};
        byte[] b5 = HexToByteArr(crc16(b4));
        byte[] data = byteMergerAll(b1, b4, b5, b7);

        temp = sendData(data);
        if (temp != null) {
            //处理数据
            runParam = CommandDateUtil.bioRunParam(temp);
//            bundle.putSerializable("runParam", runParam);
//            intent.putExtra("runParam", bundle);
//            sendBroadcast(intent);
            temp = null;
        } else {


            LogPlus.d("===", "the mechine return datafile is error");
        }
    }
    private byte[] sendData(byte[] data) {
        for (int i = 0; i < REPEAT_TIME; i++) {
            byte[] res = receiveData(data);
            if (null != res) {
                LogPlus.d("bioservice", "发送成功：" + i);
                Content.COMMUNICATION_NORMAL = true;
                return res;
            }else {
                LogPlus.d("bioservice", "数据为空");

            }
        }

        Content.COMMUNICATION_NORMAL = false;
        LogPlus.d("bioservice", "发送失败：" + "the mechine return datafile is error");
//        intent.setAction(MyBaseActivity.ERROR_ACTION);
//        sendBroadcast(intent);

        return null;
    }


    /* 接收数据 */
    public byte[] receiveData(byte[] data) {
        //等待机器处理数据并返回
        byte[] ReceArray = null;
        try {
            if (null != mOutputStream) {
                mOutputStream.write(data);
                mOutputStream.flush();
            }
            SystemClock.sleep(500);
            for (int i = 0; i < 10; i++) {
                if (null != mInputStream && mInputStream.available() <= 0) {
                    SystemClock.sleep(50);
                } else {
                    break;
                }
            }
            byte[] readData = new byte[1024];
            if (null != mInputStream && mInputStream.available() > 0) {
                int size = mInputStream.read(readData); //阻塞读取
                if (size > 0) {
                    LogPlus.d("===", "接收数据"+ByteArrToHex(readData));

                    byte[] res = DataUtils.isChecked(readData, size);
                    if (res != null) {
                        LogPlus.d("===", "*** the data is ok: " + ByteArrToHex(res));
                        return res;
                    } else {
                        LogPlus.d("===", "*** the data is destroyed: ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReceArray;
    }


    /* 关闭串口 */
    public void closeSerialPort() {
        try {
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
            }

            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
        } catch (Exception e) {
        }
    }
}

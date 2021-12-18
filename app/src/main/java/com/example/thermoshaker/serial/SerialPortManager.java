package com.example.thermoshaker.serial;

import android.annotation.SuppressLint;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import com.example.thermoshaker.model.Device;
import com.example.thermoshaker.serial.message.LogManager;
import com.example.thermoshaker.serial.message.SendMessage;
import com.licheedev.myutils.LogPlus;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;
import static com.example.thermoshaker.serial.DataUtils.HexToByteArr;
import static com.example.thermoshaker.serial.DataUtils.byteMergerAll;
import static com.example.thermoshaker.serial.DataUtils.crc16;


/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class SerialPortManager {

    private static final String TAG = "SerialPortManager";

    private BufferedInputStream mInputStream;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;
    private RunningThread runningThread;
    //错误重复次数
    private final int REPEAT_TIME = 10;
    private static class InstanceHolder {

        public static  SerialPortManager sManager = new  SerialPortManager();

    }

    public static  SerialPortManager instance() {
        return InstanceHolder.sManager;
    }

    public SerialPort mSerialPort;

    private SerialPortManager() {

    }

    /**
     * 打开串口
     *
     * @param device
     * @return
     */
    public SerialPort open(Device device) {
        return open(device.getPath(), device.getBaudrate());
    }

    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @return
     */
    public SerialPort open(String devicePath, String baudrateString) {
        if (mSerialPort != null) {
            close();
        }

        try {
            File device = new File(devicePath);
            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate);


            mInputStream = new BufferedInputStream(mSerialPort.getInputStream());
            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
            runningThread = new RunningThread();
            runningThread.start();
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

            return mSerialPort;
        } catch (Throwable tr) {
            LogPlus.e("打开串口失败",tr);
            close();
            return null;
        }
    }

    private class RunningThread extends Thread {
        @Override
        public void run() {
//            while (true){
//                writeNewData();
//            }

        }
    }
    //起始符
    static byte[] b1 = {(byte) 0xAA};
    //b2 通讯体类型
    //b3 通讯命令
    //b4 数据长度
    //b5 具体数据
    //b6 校验码
    static byte[] b7 = {(byte) 0x55};
    @SuppressLint("CheckResult")
    private void writeNewData() {
        byte b2 = ControlParam.head_query;
        byte b3 = ControlParam.ASK_RUNDATA;
        byte[] temp = {b2, b3};
        byte[] b5 = HexToByteArr(crc16(temp));
        byte[] datas =  byteMergerAll(b1, temp, b5, b7);
//        sendData(datas);

    }

    private byte[] sendData(byte[] data) {
        for (int i = 0; i < REPEAT_TIME; i++) {
            byte[] res = receiveData(data);
            if (null != res) {
                Log.d(TAG, "发送成功：" + i);

                return res;
            }else {
                Log.d(TAG, "数据为空");
            }
        }

//        MyApp.COMMUNICATION_NORMAL = false;
//        Log.d("bioservice", "发送失败：" + "the mechine return datafile is error");
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
                    Log.d(TAG, "接收数据"+ByteArrToHex(readData));

                    byte[] res = DataUtils.isChecked(readData, size);
                    if (res != null) {
                        Log.d(TAG, "*** the data is ok: " + ByteArrToHex(res));
                        return res;
                    } else {
                        Log.d(TAG, "*** the data is destroyed: ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReceArray;
    }


    /**
     * 关闭串口
     */
    public void close() {

        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /* 停止线程 */
        if (runningThread != null) {
            runningThread.interrupt();
            runningThread = null;
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }




}

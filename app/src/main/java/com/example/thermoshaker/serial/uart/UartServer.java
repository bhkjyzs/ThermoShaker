package com.example.thermoshaker.serial.uart;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.serialport.SerialPort;


import com.example.thermoshaker.base.MyApplication;
import com.licheedev.myutils.LogPlus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;


;

/**
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月24日 下午12:55:45
 * @串口通信服务
 */
public class UartServer extends Service {
    private final static String TAG = UartServer.class.getSimpleName();
    public final static String MSG = UartServer.class.getName();

    private SerialPort mSerialPort; // 串口的类
    private OutputStream mOutputStream; // 写串口
    private InputStream mInputStream; // 读串口
    private mSessionThread mThread; // 会话线程

    private String path = ""; // 设备的路径
    private String pathSlave = ""; // 从设备的路径
    private int baudrate = 57600; // 串口波特率
    private int baudrateSlave = 38400; // 从串口波特率
    private Boolean isOpen = false; // 是否打开串口

    private ConcurrentLinkedQueue<UartClass> valQueueConcurrent = new ConcurrentLinkedQueue<UartClass>(); // 值队列
    // private int timeOver = 1000; // 不要和主延时差太多,避免值队列拥堵
    private int ErrorNum = 5;

    /* 接收广播 */
    private BroadcastReceiver Message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* 如果打开串口则接收数据 */
            try {
                if (isOpen) {
                    int size = valQueueConcurrent.size();
                    if (size > 50) {
                        valQueueConcurrent.clear();
                       
                        LogPlus.d("serialport", "\nvalQueue clear\n");

                    } else if (size > 0) {
                        /* 加入一个数据间隔 */
                        valQueueConcurrent.offer(new UartClass(null, null));
                    }

                    valQueueConcurrent.offer((UartClass) intent.getSerializableExtra("serialport"));
                }

            } catch (Exception e) {
                LogPlus.d(TAG, "BroadcastReceiver* " + e.getMessage());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        /* 注册广播 */
        registerReceiver(Message, new IntentFilter(MSG));

        /* 无论是否打开串口都要启动线程 */
        mThread = new mSessionThread();
        mThread.start();

        LogPlus.d(TAG, "over 4");
    }

    /* 每一次开始服务时执行 */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* 接收参数 */
        if (intent.getBooleanExtra("REOPEN", false)) {
            path = intent.getStringExtra("DEVICE");
            baudrate = intent.getIntExtra("BAUDRATE", 57600);
            pathSlave = intent.getStringExtra("DEVICE1");
//            baudrateSlave = intent.getIntExtra("BAUDRATE1", 38400);

            if (isOpen)
                closeSerialPort();
        }

        LogPlus.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /* 打开串口 */
    public Boolean openSerialPort(String path, int baudrate) {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            LogPlus.d(TAG, "openSerialPort");
            return true;
        } catch (Exception e) {
            LogPlus.d(TAG, "openSerialPort* " + e.getMessage());
        }
        return false;



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
            isOpen = false;
            isOpen = false;
            MyApplication.getInstance().appClass.setUartReady(false);
            LogPlus.d(TAG, "closeSerialPort");
        } catch (Exception e) {
            LogPlus.d(TAG, "closeSerialPort* " + e.getMessage());
        }
    }

    /* 发送进程 */
    private class mSessionThread extends Thread {
        UartClass val = null;
        UserHandle user = android.os.Process.myUserHandle();
        long count = 0;

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                try {
                    if (!isOpen) {
                        /* 查询数据 */
                        byte[] data = {(byte) 0xaa, (byte) 0x4e, (byte) 0xe1, 0, 0, 0};
                        byte[] data_iap = {0x31};
                        short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
                        data[data.length - 3] = (byte) (crc16 >> 8);
                        data[data.length - 2] = (byte) crc16;
                        data[data.length - 1] = (byte) 0x55;

                        /* 第一次测试 */
                        if (path != null && !path.equals("")) {
                            openSerialPort(path, baudrate);
                            clearData();
                            mOutputStream.write(data);
                            mOutputStream.flush();
                            if (receiveData() == null) {
                                /* 升级模式 */
                                mOutputStream.write(data_iap);
                                mOutputStream.flush();
                                if (receiveData() == null) {
                                    closeSerialPort();
                                } else {
                                    MyApplication.getInstance().appClass.setUartSerialStr(path);
                                    isOpen = true;
                                }
                            } else {
                                MyApplication.getInstance().appClass.setUartSerialStr(path);
                                isOpen = true;
                            }

                            /* 第二次测试 */
                            if (!isOpen) {
                                openSerialPort(path, baudrateSlave);
                                clearData();
                                mOutputStream.write(data);
                                mOutputStream.flush();
                                if (receiveData() == null) {
                                    /* 升级模式 */
                                    mOutputStream.write(data_iap);
                                    mOutputStream.flush();
                                    if (receiveData() == null) {
                                        closeSerialPort();
                                    } else {
                                        MyApplication.getInstance().appClass.setUartSerialStr(path);
                                        isOpen = true;
                                    }
                                } else {
                                    MyApplication.getInstance().appClass.setUartSerialStr(path);
                                    isOpen = true;
                                }

                                /* 第三次测试 */
                                if (!isOpen) {
                                    if (pathSlave != null && !pathSlave.equals("")) {
                                        openSerialPort(pathSlave, baudrate);
                                        clearData();
                                        mOutputStream.write(data);
                                        mOutputStream.flush();
                                        if (receiveData() == null) {
                                            /* 升级模式 */
                                            mOutputStream.write(data_iap);
                                            mOutputStream.flush();
                                            if (receiveData() == null) {
                                                closeSerialPort();
                                            } else {
                                                MyApplication.getInstance().appClass.setUartSerialStr(pathSlave);
                                                isOpen = true;
                                            }
                                        } else {
                                            MyApplication.getInstance().appClass.setUartSerialStr(pathSlave);
                                            isOpen = true;
                                        }

                                        /* 第四次测试 */
                                        if (!isOpen) {
                                            openSerialPort(pathSlave, baudrateSlave);
                                            clearData();
                                            mOutputStream.write(data);
                                            mOutputStream.flush();
                                            if (receiveData() == null) {
                                                /* 升级模式 */
                                                mOutputStream.write(data_iap);
                                                mOutputStream.flush();
                                                if (receiveData() == null) {
                                                    closeSerialPort();
                                                } else {
                                                    MyApplication.getInstance().appClass.setUartSerialStr(pathSlave);
                                                    isOpen = true;
                                                }
                                            } else {
                                                MyApplication.getInstance().appClass.setUartSerialStr(pathSlave);
                                                isOpen = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        sleep(1000);
                        continue;
                    }

                    /* 取队列数据 */
                    if (valQueueConcurrent.size() > 0) {
                        val = valQueueConcurrent.poll();
                    } else {
                        sleep(200);
                        continue;
                    }

                    if (val == null || val.getData() == null) {
                        sleep(200);
                        continue;
                    } else {
                        /* 空清数据 */
                        if (mInputStream.available() > 0) {
                            mInputStream.skip(mInputStream.available());
                        }

                        /* 发送数据 */
                        mOutputStream.write(val.getData());
                        mOutputStream.flush();
                        String str = "-----------" + MyApplication.getInstance().AppDateFormat.format(new Date())
                                + "-------------" + count + "\n" + printString(val.getData()) + "\n\n";
//                        Log.d(TAG,str+"");
                        /* 接收数据 */
                        byte[] ReceArray = null;
                        int dely = val.getTime() / 20;
                        for (int i = 0; i < dely; i++) {
                            if (mInputStream.available() > 0) {
                                sleep(100);
                                ReceArray = new byte[mInputStream.available()];
                                mInputStream.read(ReceArray);
                                break;
                            }
                            sleep(20);
                        }

                        /* 处理数据 */
                        if (ReceArray == null) {
                            str += "read null\n";
                            /* 通信无返回 */
                            if (ErrorNum < 1) {
                                ErrorNum = 5;

                                MyApplication app = MyApplication.getInstance();
                                File file = new File("/storage/emulated/0/Alarms/");
                                if (file.list().length < 200) {
                                    // 保存当前通讯内容
                                    try {
                                        StringBuilder strRet = new StringBuilder();
                                        LinkedList<String> strQueue = app.strQueue;
                                        for (int i = 0; i < strQueue.size(); i++) {
                                            strRet.append(strQueue.get(i));
                                        }

                                        /* 写入文件 */
                                        File path = new File("/storage/emulated/0/Alarms/GLP_" + count + ".txt");
                                        if (path.exists()) {
                                            path = new File("/storage/emulated/0/Alarms/GLP_" + count + "A.txt");
                                            if (path.exists()) {
                                                path = new File("/storage/emulated/0/Alarms/GLP_" + count + "AA.txt");
                                            }
                                        }
                                        FileOutputStream fos = new FileOutputStream(path);
                                        fos.write(strRet.toString().getBytes());
                                        fos.flush();
                                        fos.close();
                                        app.exec("sync");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                app.appClass.setUartReady(false);
                            } else {
                                ErrorNum--;
                            }
                        } else if (val.getAddress() != null) {
                            ErrorNum = 5;

                            /* 地址不为空 */
                            sendBroadcastAsUser(new Intent(val.getAddress()).putExtra("serialport", ReceArray), user);
                            str += printString(ReceArray) + "\n";

                        } else {
                            ErrorNum = 5;

                            str += "Address null\n";
                        }

                        /* 记录通信数据 */
                        MyApplication.getInstance().strQueue.add(str);
                        count++;
                        if (count > 1000) {
                            MyApplication.getInstance().strQueue.remove(0);
                        }
                    }
                } catch (Exception e) {
                    LogPlus.d(TAG, "mSessionThread* " + e.getMessage());
                }
            }
        }
    }

    /* 空清数据 */
    public void clearData() {
        try {
            if (mInputStream.available() > 0) {
                mInputStream.skip(mInputStream.available());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 接收数据 */
    public byte[] receiveData() {
        byte[] ReceArray = null;
        try {
            for (int i = 0; i < 50; i++) {
                if (mInputStream.available() > 0) {
                    Thread.sleep(100);
                    ReceArray = new byte[mInputStream.available()];
                    mInputStream.read(ReceArray);
                    break;
                }
                Thread.sleep(20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReceArray;
    }

    /* byte转换为string */
    public String printString(byte[] ReceArray) {
        StringBuffer sb = new StringBuffer(ReceArray.length);
        String sTemp;
        for (int i = 0; i < ReceArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & ReceArray[i]);
            sb.append(" ");
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase(Locale.getDefault()));
        }
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        /* 停止线程 */
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

        /* 关闭串口 */
        closeSerialPort();

        /* 注销广播 */
        if (Message != null) {
            unregisterReceiver(Message);
            Message = null;
        }

        LogPlus.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

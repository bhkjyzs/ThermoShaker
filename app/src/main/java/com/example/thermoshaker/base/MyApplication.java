package com.example.thermoshaker.base;

import android.app.Application;
import android.content.Intent;
import android.serialport.SerialPort;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.StepDefault;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.adjust.AdjustInterface;
import com.example.thermoshaker.serial.uart.adjust.Default_adjustClass;
import com.example.thermoshaker.serial.uart.debug.DebugInterface;
import com.example.thermoshaker.serial.uart.debug.Default_debugClass;
import com.example.thermoshaker.serial.uart.running.Default_tdfileRunClass;
import com.example.thermoshaker.serial.uart.running.TdfileRunClass;
import com.example.thermoshaker.serial.uart.running.TdfileRunInterface;
import com.example.thermoshaker.serial.uart.system.DefaultSystemClass;
import com.example.thermoshaker.serial.uart.system.SystemInterface;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.service.BioHeartService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static int baudrate = 19200;
    public static SerialPort serialPort;
    private List<ProgramInfo> data;
    private static MyApplication instance;
    //当前设备步骤默认属性
    public StepDefault stepDefault = new StepDefault();
    public MainAppClass appClass;// 用于保存和记录一些参数
    public LinkedList<String> strQueue; // 串口通信队列
    //步骤进入前的步骤列表，对比是否改变了的作用
    public static ProgramInfo programsSteps;
    //串口打开或者关闭
    private boolean mOpened = false;
    public SimpleDateFormat dateFormat; // 用于格式化局部时间
    public SimpleDateFormat AppDateFormat;// 用于常规日期格式化
    public SimpleDateFormat lockDateFormat;
    private Intent serviceIntent;
    //参数
    public SystemInterface systemClass; // 系统类
    public AdjustInterface adjustClass; // 修正类
    public DebugInterface debugClass; // 调试类
    public TdfileRunInterface runningClass; // 运行类
    public Intent comIntent, intentSound;
    //设备是否正在运行程序
    public boolean isRunWork = false;


    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onTerminate() {

        /* 停止串口服务 */
        if (comIntent != null) {
            stopService(comIntent);
            comIntent = null;
        }

        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.hookWebView();
        appClass = new MainAppClass(this);
        strQueue = new LinkedList<String>();
        intentSound = new Intent(UartServer.MSG);
        intentSound.putExtra("serialport", new UartClass(null, UartType.OT_KEY_SOUND_BYTE));
        openSerialPort();
        initData();
        initConfig();
//        initSystemParam();

//        serviceIntent = new Intent(this, BioHeartService.class);
//        startService(serviceIntent);

    }

    public void openSerialPort(){
        /* 开始串口服务 */
        comIntent = new Intent(this, UartServer.class);
        /* 是否重新打开 */
        comIntent.putExtra("REOPEN", true);

        /* 区分主板型号 暂时不需要*/
//        if (Build.VERSION.SDK_INT == 22) {
//            comIntent.putExtra("DEVICE", appClass.getUartSerial().getValue());
//            comIntent.putExtra("DEVICE1",
//                    (appClass.getUartSerial() == MainType.UartSource.uartUP) ? MainType.UartSource.uartDown.getValue()
//                            : MainType.UartSource.uartUP.getValue());
//        } else {
            comIntent.putExtra("DEVICE", "/dev/ttyS3");
//        }

        /* 波特率 */

            comIntent.putExtra("BAUDRATE", baudrate);
//            comIntent.putExtra("BAUDRATE1", 19200);//做适配,暂时不需要
            startService(comIntent);
    }

    /**
     * 按键提示音
     */
    public void KeySound() {
        if (appClass.getSettingViewSound()[0]) {
            sendBroadcast(intentSound);
        }
    }

    public static SerialPort getSerialPort() {
        try {
            File device = new File("/dev/ttyS3");
            serialPort = new SerialPort(device, baudrate); //下WK2 上WK1
            Log.d("getSerialPort", baudrate + "   " + serialPort);
            return serialPort;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }





    }
    private void initConfig() {
        /* 格式化局部时间 */
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        AppDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AppDateFormat.setTimeZone(TimeZone.getDefault());

        lockDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        lockDateFormat.setTimeZone(TimeZone.getDefault());
        //初始化设备型号
        systemClass = new DefaultSystemClass();
        adjustClass = new Default_adjustClass();
        debugClass = new Default_debugClass();
        runningClass= new Default_tdfileRunClass();


    }






    public void initData() {
        try {
            Log.i(TAG, "application:init data");
            File rootFile = new File(DataUtil.data_path + DataUtil.data_name);
            data = new ArrayList<>();
            if (!rootFile.exists()) {
                return;
            }
            for (File file : rootFile.listFiles()) {
                //Log.i("MyApp",file.getAbsolutePath());
                String temp = DataUtil.readData(file.getAbsolutePath());
                ProgramInfo info = JSON.parseObject(temp, ProgramInfo.class);
                info.setFileName(file.getName());
                info.setFilePath(file.getAbsolutePath());
                data.add(info);
            }
            Log.d("===", data.toString() + "");
        } catch (Exception e) {
            Log.i(TAG, e.toString() + "");
        }
    }

    public List<ProgramInfo> getData() {
        return data;
    }

    public List<ProgramInfo> getDataRefre() {
        initData();
        return data;
    }


    public void initSystemParam() {
        byte[] temp = CommandDateUtil.SendQueryCommand(ControlParam.ASK_SYSTEM);
        Log.i("===", "init systemparam：" + temp);
        if (temp != null) {
            Content.control_systemParam = CommandDateUtil.bioSystemRec(temp);
//            int Lamp_Sun_State = Content.control_systemParam.getLamp_Sun_State();
//            if (0 == Lamp_Sun_State) {
//                isLight = false;
//            } else if (ControlParam.WINDOW_OPEN_STATE == Lamp_Sun_State) {
//                isLight = true;
//            } else {
//                isLight = false;
//            }
        }
    }



    /**
     * LiJ 根据主板型号运行系统命令
     */
    public boolean exec(String cmd) {

        Log.d(TAG, cmd);
        try {
            if (cmd != null && !cmd.equals("")) {
                Process su = Runtime.getRuntime().exec("su");
                String string = cmd + ";exit\n";
                su.getOutputStream().write(string.getBytes());
                su.getOutputStream().flush();
                Log.d(TAG, su.waitFor() + "   down");
                if (su.waitFor() != 0)
                    return false;
                else
                    return true;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return false;

    }




}

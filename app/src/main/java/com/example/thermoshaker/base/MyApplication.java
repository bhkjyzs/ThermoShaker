package com.example.thermoshaker.base;

import android.app.Application;
import android.os.Build;
import android.serialport.SerialPortFinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.thermoshaker.model.Device;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.model.StepDefault;
import com.example.thermoshaker.serial.message.SerialPortManager;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";


    private List<ProgramInfo> data;

    private static MyApplication instance;

    //当前设备步骤默认属性
    public StepDefault stepDefault = new StepDefault();

    //步骤进入前的步骤列表，对比是否改变了的作用
    public static ProgramInfo programsSteps;
    //串口打开或者关闭
    private boolean mOpened = false;

    public SimpleDateFormat dateFormat; // 用于格式化局部时间
    public SimpleDateFormat AppDateFormat;// 用于常规日期格式化
    public SimpleDateFormat lockDateFormat;


    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initData();
        initConfig();
        OpenSerialPort(new Device("/dev/ttyS3", "57600"));
    }

    private void initConfig() {
        /* 格式化局部时间 */
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        AppDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AppDateFormat.setTimeZone(TimeZone.getDefault());

        lockDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        lockDateFormat.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 打开串口
     */
    private void OpenSerialPort(Device device) {
        mOpened = SerialPortManager.instance().open(device) != null;
        if (mOpened) {
            ToastUtil.showOne(this, "成功打开串口");
        } else {
            ToastUtil.showOne(this, "打开串口失败");
        }
    }

    /**
     * 关闭串口
     */
    public void CloseSerialPort() {
        SerialPortManager.instance().close();
        mOpened = false;
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

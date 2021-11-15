package com.example.thermoshaker.base;

import android.app.Application;
import android.serialport.SerialPortFinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.thermoshaker.model.Device;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.serial.SerialPortManager;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";


    private List<ProgramInfo> data;

    private static MyApplication instance;

    //步骤进入前的步骤列表，对比是否改变了的作用
    public static List<ProgramStep> programsSteps;
    //串口打开或者关闭
    private boolean mOpened = false;


    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        OpenSerialPort(new Device("","115200"));

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
    public void CloseSerialPort(){
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
            Log.i(TAG, e.toString()+"");
        }
    }

    public List<ProgramInfo> getData() {
        return data;
    }
    public List<ProgramInfo> getDataRefre() {
        initData();
        return data;
    }

}

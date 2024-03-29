package com.example.thermoshaker.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.thermoshaker.serial.uart.system.SystemType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月25日 下午1:56:01
 * @全局交换区分流的一些数据
 */
public class MainAppClass {
    private MyApplication app;

    public MainAppClass(MyApplication app) {
        this.app = app;
        SharedPreferences sharedPreferences = app.getSharedPreferences("CC", Context.MODE_PRIVATE);
        //运行参数设置
        runSetting[0] = sharedPreferences.getInt("TempCtrlMode", 0);
        runSetting[1] = sharedPreferences.getInt("PreHeating", 1);
        runSetting[2] = sharedPreferences.getInt("HeatMode", 0);
        wifiReady = sharedPreferences.getBoolean("wifiReady", false);
        //声音设置
        settingViewSound[0] = sharedPreferences.getBoolean("settingViewSoundKey", false);
        settingViewSound[1] = sharedPreferences.getBoolean("settingViewSoundWarning", true);
        settingViewSound[2] = sharedPreferences.getBoolean("settingViewSoundEnd", true);
        settingViewSound[3] = sharedPreferences.getBoolean("settingViewSoundArrive", false);


    }

    /* 无线开关 */
    private boolean wifiReady = false;

    public boolean isWifiReady() {
        return wifiReady;
    }

    public void setWifiReady(boolean wifiReady) {
        app.getSharedPreferences("CC", Context.MODE_PRIVATE).edit().putBoolean("wifiReady", wifiReady).commit();
        app.exec("sync");
        this.wifiReady = wifiReady;
    }

    //运行参数设置
    private int[] runSetting = new int[]{0,1,0};
    public int[] getRunSetting(){

        return runSetting;
    }
    public void setRunSetting(int[] runSetting){
        try {
            this.runSetting = runSetting;
            Editor edit = app.getSharedPreferences("CC", Context.MODE_PRIVATE).edit();
            edit.putInt("TempCtrlMode", runSetting[0]);
            edit.putInt("PreHeating", runSetting[1]);
            edit.putInt("HeatMode", runSetting[2]);
            edit.commit();
            app.exec("sync");
        } catch (Exception e) {
        }
    }




    /* 保存设置页面的声音参数,按键,报警,程序结束,温度到达 */
    private boolean[] settingViewSound = new boolean[]{false, true, true, false};

    public boolean[] getSettingViewSound() {
        return settingViewSound;
    }

    public void setSettingViewSound(boolean[] settingViewSound) {
        try {
            this.settingViewSound = settingViewSound;
            Editor edit = app.getSharedPreferences("CC", Context.MODE_PRIVATE).edit();
            edit.putBoolean("settingViewSoundKey", settingViewSound[0]);
            edit.putBoolean("settingViewSoundWarning", settingViewSound[1]);
            edit.putBoolean("settingViewSoundEnd", settingViewSound[2]);
            edit.putBoolean("settingViewSoundArrive", settingViewSound[3]);
            edit.commit();
            app.exec("sync");
        } catch (Exception e) {
        }
    }
    public byte getSettingViewSoundByte() {
        byte val = 0;
        if (settingViewSound[0]) {
            val |= SystemType.Sound.SS_KEY.getValue();
        }
        if (settingViewSound[1]) {
            val |= SystemType.Sound.SS_ALARM.getValue();
        }
        if (settingViewSound[2]) {
            val |= SystemType.Sound.SS_PROEND.getValue();
        }
        if (settingViewSound[3]) {
            val |= SystemType.Sound.SS_TEMPLAND.getValue();
        }
        return val;
    }


    /* 机器编号 */
    private String MachineNumber = "";

    public String getMachineNumber() {
        return MachineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        app.getSharedPreferences("CC", Context.MODE_PRIVATE).edit().putString("machineNumber", machineNumber).commit();
        app.exec("sync");
        MachineNumber = machineNumber;
    }
    /* 系统是否初始化 */
    private boolean systemInit = false;

    public boolean isSystemInit() {
        return systemInit;
    }

    public void setSystemInit(boolean systemInit) {
        this.systemInit = systemInit;
    }
    /* 运行文件多久记录一次,毫秒 */
    private int tdRecordTime = 500;

    public int getTdRecordTime() {
        return tdRecordTime;
    }
    /* 串口是否准备就绪 */
    private boolean uartReady = false;

    public boolean isUartReady() {
        return uartReady;
    }

    public void setUartReady(boolean uartReady) {
        this.uartReady = uartReady;
    }

    /* 串口名称 */
    private MainType.UartSource uartSerial = MainType.UartSource.uartUP;

    public MainType.UartSource getUartSerial() {
        return uartSerial;
    }

    public void setUartSerial(MainType.UartSource uart) {
        app.getSharedPreferences("CC", Context.MODE_PRIVATE).edit().putString("uartSerial", uart.getValue()).commit();
        app.exec("sync");
        uartSerial = uart;
    }

    public void setUartSerialStr(String uartStr) {
        if (uartStr.equals(MainType.UartSource.uartDown.getValue()))
            setUartSerial(MainType.UartSource.uartDown);
        else
            setUartSerial(MainType.UartSource.uartUP);
    }

}

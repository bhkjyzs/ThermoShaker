package com.example.thermoshaker.model;

import java.io.Serializable;

public class SystemProgram implements Serializable {


    private int ui_type = 0; //运行界面类型

    private int led_runMode;//LED指示灯运行模式

    private int sound = 0; // 声音设置

    private String VERSUFFIX = "0"; //版本后缀

    private int VERSION = 0; //软件版本(固件版本)

    private String device_num = "0"; //设备编号


    public int getUi_type() {
        return ui_type;
    }

    public void setUi_type(int ui_type) {
        this.ui_type = ui_type;
    }

    public int getLed_runMode() {
        return led_runMode;
    }

    public void setLed_runMode(int led_runMode) {
        this.led_runMode = led_runMode;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public String getVERSUFFIX() {
        return VERSUFFIX;
    }

    public void setVERSUFFIX(String VERSUFFIX) {
        this.VERSUFFIX = VERSUFFIX;
    }

    public int getVERSION() {
        return VERSION;
    }

    public void setVERSION(int VERSION) {
        this.VERSION = VERSION;
    }

    public String getDevice_num() {
        return device_num;
    }

    public void setDevice_num(String device_num) {
        this.device_num = device_num;
    }
}

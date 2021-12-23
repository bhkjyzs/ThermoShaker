package com.example.thermoshaker.model;

import java.util.Date;

public class StepDefault {

    // 温度
    private float minTemp = 0;
    private float maxTemp = 105;
    private float defaultTemp = 95; // 默认温度
    // 时间
    private Date minTime = new Date(0L);
    private Date maxTime = new Date(18L * 60L * 60L * 1000L);
    private Date defaultTime = new Date(30L * 1000L); // 默认时间

    //循环
    private int maxNum=99;
    private int minNum=1;





    private double[] upTemp={3,2,1,0.1};


    public StepDefault() {
    }


    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public Date getMinTime() {
        return minTime;
    }

    public void setMinTime(Date minTime) {
        this.minTime = minTime;
    }

    public Date getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Date maxTime) {
        this.maxTime = maxTime;
    }

    public Date getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(Date defaultTime) {
        this.defaultTime = defaultTime;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getDefaultTemp() {
        return defaultTemp;
    }

    public void setDefaultTemp(float defaultTemp) {
        this.defaultTemp = defaultTemp;
    }
}

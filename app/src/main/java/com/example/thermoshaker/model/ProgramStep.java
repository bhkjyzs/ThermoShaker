package com.example.thermoshaker.model;

import java.io.Serializable;

public class ProgramStep implements Serializable {

    private float temperature;//温度

    private float speed;//速度

    private int time;//时间
    private int ZSpeed;//转速
    private String direction;//方向
    private int shock;//震荡时间
    private int intermission;//间歇时间

    public ProgramStep() {
    }

    public ProgramStep(float temperature, float speed, int time, int ZSpeed, String direction, int shock, int intermission) {
        this.temperature = temperature;
        this.speed = speed;
        this.time = time;
        this.ZSpeed = ZSpeed;
        this.direction = direction;
        this.shock = shock;
        this.intermission = intermission;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getZSpeed() {
        return ZSpeed;
    }

    public void setZSpeed(int ZSpeed) {
        this.ZSpeed = ZSpeed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getShock() {
        return shock;
    }

    public void setShock(int shock) {
        this.shock = shock;
    }

    public int getIntermission() {
        return intermission;
    }

    public void setIntermission(int intermission) {
        this.intermission = intermission;
    }

    @Override
    public String toString() {
        return "ProgramStep{" +
                "temperature=" + temperature +
                ", speed=" + speed +
                ", time='" + time + '\'' +
                ", ZSpeed=" + ZSpeed +
                ", direction='" + direction + '\'' +
                ", shock='" + shock + '\'' +
                ", intermission='" + intermission + '\'' +
                '}';
    }
}

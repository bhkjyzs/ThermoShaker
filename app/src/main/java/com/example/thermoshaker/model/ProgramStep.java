package com.example.thermoshaker.model;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MainType;
import com.example.thermoshaker.base.MyApplication;

import java.io.Serializable;
import java.util.Objects;

public class ProgramStep implements Serializable {

    private float temperature;//温度
    private long time;//时间
    private int ZSpeed;//转速
    private MainType.DirectionType direction;//电机方向

    private double upSpeed;//升温速率
    private double downSpeed;//降温速率
    private int MixingMode;//混匀方式//0为连续 1为间歇
    private int BlendStart;//混合起点//0为同步 1为预热后混匀

    private long continued;//持续时间
    private long intermission;//间歇时间


    public ProgramStep() {
        temperature=25;
        time=0;
        ZSpeed=1000;
        direction= MainType.DirectionType.Forward;
        upSpeed= 3.0;
        downSpeed=1.0;
        MixingMode=0;
        BlendStart=0;
        continued=0;
        intermission=0;

    }


    public long getContinued() {
        return continued;
    }

    public void setContinued(long continued) {
        this.continued = continued;
    }

    public float getTemperature() {
        return temperature;
    }

    public double getUpSpeed() {
        return upSpeed;
    }
    public String getUpSpeedStr(){
        String str="";
        if(upSpeed==3.0&&downSpeed==1.0){
            str = MyApplication.getInstance().getString(R.string.FullPower);

        }else {
            str = upSpeed+"°C/min";

        }

        return str;
    }

    public void setUpSpeed(double upSpeed) {
        this.upSpeed = upSpeed;
    }

    public double getDownSpeed() {
        return downSpeed;
    }
    public String getDownSpeedStr(){
        String str="";
        if(upSpeed==3.0&&downSpeed==1.0){
            str = MyApplication.getInstance().getString(R.string.FullPower);

        }else {
            str = downSpeed+"°C/min";

        }

        return str;
    }

    public void setDownSpeed(double downSpeed) {
        this.downSpeed = downSpeed;
    }

    public int getMixingMode() {
        return MixingMode;
    }

    public String getMixingModeStr(){
        String str="";
        if(MixingMode==0){
            str =  MyApplication.getInstance().getString(R.string.continuity);
        }else {
            str =  MyApplication.getInstance().getString(R.string.intermission);

        }

        return str;

    }

    public void setMixingMode(int mixingMode) {
        MixingMode = mixingMode;
    }

    public int getBlendStart() {
        return BlendStart;
    }

    public String getBlendStartStr(){
        String str="";
        if(BlendStart==0){
           str =  MyApplication.getInstance().getString(R.string.syncnok);
        }else {
            str =  MyApplication.getInstance().getString(R.string.mixprehnok);

        }

        return str;
    }


    public void setBlendStart(int blendStart) {
        BlendStart = blendStart;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getZSpeed() {
        return ZSpeed;
    }

    public void setZSpeed(int ZSpeed) {
        this.ZSpeed = ZSpeed;
    }

    public MainType.DirectionType getDirection() {
        return direction;
    }

    public String getDirectionStr(){
        String str="";
        switch (direction){
            case Forward:
                str= MyApplication.getInstance().getString(R.string.forwardmixing);
                break;
            case Reversal:
                str= MyApplication.getInstance().getString(R.string.rotarymixing);
                break;
            case ForwardAndReverse:
                str= MyApplication.getInstance().getString(R.string.Forwardandreversemixing);
                break;
        }

        return str;

    }

    public void setDirection(MainType.DirectionType direction) {
        this.direction = direction;
    }


    public long getIntermission() {
        return intermission;
    }

    public void setIntermission(long intermission) {
        this.intermission = intermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramStep that = (ProgramStep) o;
        return Float.compare(that.temperature, temperature) == 0 && time == that.time && ZSpeed == that.ZSpeed && Double.compare(that.upSpeed, upSpeed) == 0 && Double.compare(that.downSpeed, downSpeed) == 0 && MixingMode == that.MixingMode && BlendStart == that.BlendStart && continued == that.continued && intermission == that.intermission && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, time, ZSpeed, direction, upSpeed, downSpeed, MixingMode, BlendStart, continued, intermission);
    }
}

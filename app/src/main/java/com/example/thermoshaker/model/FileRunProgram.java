package com.example.thermoshaker.model;

import java.io.Serializable;

public class FileRunProgram implements Serializable {

    private int CURRunStep;//当前步骤
    private int ALLRunStepNum;//总步骤
    private long CURWaitTime;//当前等待时间
    private long CURRemnantTime;//当前总剩余时间
    private int RunCir;//运行循环次数
    private int CirStep;//循环跳转步骤
    private float ModuleTemp;//模块后台显示温度1
    private float DispTemp;//模块显示温度1
    private float LidModuleTemp;//热盖后台显示温度
    private float LidDispTemp;//热盖显示温度
    private int RunCourse;//当前运行状态
    private int RunFileState;//文件当前状态
    private int[] SystemErrCode = new int[4];//系统错误代码

    public int getCURRunStep() {
        return CURRunStep;
    }

    public void setCURRunStep(int CURRunStep) {
        this.CURRunStep = CURRunStep;
    }

    public int getALLRunStepNum() {
        return ALLRunStepNum;
    }

    public void setALLRunStepNum(int ALLRunStepNum) {
        this.ALLRunStepNum = ALLRunStepNum;
    }

    public float getModuleTemp() {
        return ModuleTemp;
    }

    public void setModuleTemp(float moduleTemp) {
        ModuleTemp = moduleTemp;
    }

    public float getDispTemp() {
        return DispTemp;
    }

    public void setDispTemp(float dispTemp) {
        DispTemp = dispTemp;
    }

    public int getRunCourse() {
        return RunCourse;
    }

    public void setRunCourse(int runCourse) {
        RunCourse = runCourse;
    }

    public int getRunFileState() {
        return RunFileState;
    }

    public void setRunFileState(int runFileState) {
        RunFileState = runFileState;
    }

    public int[] getSystemErrCode() {
        return SystemErrCode;
    }

    public void setSystemErrCode(int[] systemErrCode) {
        SystemErrCode = systemErrCode;
    }


    public long getCURWaitTime() {
        return CURWaitTime;
    }

    public void setCURWaitTime(long CURWaitTime) {
        this.CURWaitTime = CURWaitTime;
    }

    public long getCURRemnantTime() {
        return CURRemnantTime;
    }

    public void setCURRemnantTime(long CURRemnantTime) {
        this.CURRemnantTime = CURRemnantTime;
    }

    public int getRunCir() {
        return RunCir;
    }

    public void setRunCir(int runCir) {
        RunCir = runCir;
    }

    public int getCirStep() {
        return CirStep;
    }

    public void setCirStep(int cirStep) {
        CirStep = cirStep;
    }

    public float getLidModuleTemp() {
        return LidModuleTemp;
    }

    public void setLidModuleTemp(float lidModuleTemp) {
        LidModuleTemp = lidModuleTemp;
    }

    public float getLidDispTemp() {
        return LidDispTemp;
    }

    public void setLidDispTemp(float lidDispTemp) {
        LidDispTemp = lidDispTemp;
    }
}

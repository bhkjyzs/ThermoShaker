package com.example.thermoshaker.model;

public class FileRunProgram {

    private int CURRunStep;//当前步骤
    private int ALLRunStepNum;//总步骤
    private float ModuleTemp;//模块后台显示温度1
    private float DispTemp;//模块显示温度1

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
}

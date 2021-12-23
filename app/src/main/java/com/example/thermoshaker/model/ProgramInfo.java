package com.example.thermoshaker.model;

import com.example.thermoshaker.base.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramInfo implements Serializable {

    private List<ProgramStep> stepList;
    private String filePath;
    private String fileName;
    private boolean loopEnable;//循环是否开启
    private int loopStart;//循环开始步骤
    private int loopEnd;//循环结束步骤
    private int loopNum;//循环次数
    public ProgramInfo() {
    }

    public ProgramInfo(String fileName) {
        this.fileName = fileName;
        this.filePath = "";
        stepList = new ArrayList<>();
        loopEnable = false;
        loopStart=0;
        loopEnd=0;
        loopNum=0;
    }

    public ProgramInfo(List<ProgramStep> stepList, String filePath, String fileName) {
        this.stepList = stepList;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public boolean isLoopEnable() {
        return loopEnable;
    }

    public void setLoopEnable(boolean loopEnable) {
        this.loopEnable = loopEnable;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(int loopStart) {
        this.loopStart = loopStart;
    }
    public void setLoopStartStr(String str) {
        if(Integer.parseInt(str)<5&&Integer.parseInt(str)>=1){
            loopStart = Integer.parseInt(str);
        }else {
            loopStart = 2;

        }

    }
    public int getLoopEnd() {
        return loopEnd;
    }
    public void setLoopEndStr(String str) {
        if(Integer.parseInt(str)<=5&&Integer.parseInt(str)>1){
            loopEnd = Integer.parseInt(str);
        }else {
            loopEnd = 1;

        }

    }
    public void setLoopEnd(int loopEnd) {
        this.loopEnd = loopEnd;
    }

    public int getLoopNum() {
        return loopNum;
    }

    public void setLoopNum(int loopNum) {
        this.loopNum = loopNum;
    }
    public void setLoopNumStr(String num){
        if(Integer.parseInt(num)<= MyApplication.getInstance().stepDefault.getMaxNum()&&Integer.parseInt(num)>= MyApplication.getInstance().stepDefault.getMinNum()){
            loopNum = Integer.parseInt(num);
        }else {
            loopNum = 1;

        }

    }

    public List<ProgramStep> getStepList() {
        return stepList;
    }

    public void setStepList(List<ProgramStep> stepList) {
        this.stepList = stepList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramInfo that = (ProgramInfo) o;
        return loopEnable == that.loopEnable && loopStart == that.loopStart && loopEnd == that.loopEnd && loopNum == that.loopNum && Objects.equals(stepList, that.stepList) && Objects.equals(filePath, that.filePath) && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepList, filePath, fileName, loopEnable, loopStart, loopEnd, loopNum);
    }

    @Override
    public String toString() {
        return "ProgramInfo{" +
                "stepList=" + stepList +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", loopEnable=" + loopEnable +
                ", loopStart=" + loopStart +
                ", loopEnd=" + loopEnd +
                ", loopNum=" + loopNum +
                '}';
    }
}

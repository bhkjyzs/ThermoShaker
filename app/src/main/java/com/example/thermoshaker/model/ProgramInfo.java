package com.example.thermoshaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgramInfo implements Serializable {

    private List<ProgramStep> stepList;
    private String filePath;
    private String fileName;

    public ProgramInfo() {
    }

    public ProgramInfo(String fileName) {
        this.fileName = fileName;
        this.filePath = "";
        stepList = new ArrayList<>();
    }

    public ProgramInfo(List<ProgramStep> stepList, String filePath, String fileName) {
        this.stepList = stepList;
        this.filePath = filePath;
        this.fileName = fileName;
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
    public String toString() {
        return "ProgramInfo{" +
                "stepList=" + stepList +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

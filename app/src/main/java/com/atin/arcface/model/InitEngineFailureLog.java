package com.atin.arcface.model;

import java.util.Date;

public class InitEngineFailureLog {
    private Date initTime;
    private int initCount;

    public InitEngineFailureLog() {
    }

    public InitEngineFailureLog(Date initTime, int initCount) {
        this.initTime = initTime;
        this.initCount = initCount;
    }

    public Date getInitTime() {
        return initTime;
    }

    public void setInitTime(Date initTime) {
        this.initTime = initTime;
    }

    public int getInitCount() {
        return initCount;
    }

    public void setInitCount(int initCount) {
        this.initCount = initCount;
    }
}

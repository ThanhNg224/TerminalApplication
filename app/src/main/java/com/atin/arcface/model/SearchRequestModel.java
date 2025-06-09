package com.atin.arcface.model;

import java.util.Date;

public class SearchRequestModel {
    private int requestId;
    private Date starTime;
    private boolean isDone;
    private int accessType;

    public SearchRequestModel() {
    }

    public SearchRequestModel(int requestId, Date starTime, boolean isDone) {
        this.requestId = requestId;
        this.starTime = starTime;
        this.isDone = isDone;
    }

    public SearchRequestModel(int requestId, Date starTime, boolean isDone, int accessType) {
        this.requestId = requestId;
        this.starTime = starTime;
        this.isDone = isDone;
        this.accessType = accessType;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }
}

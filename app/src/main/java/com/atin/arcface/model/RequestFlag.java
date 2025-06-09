package com.atin.arcface.model;

import java.util.Date;

public class RequestFlag {
    private Date requestTime;
    private boolean isAvailable;

    public RequestFlag() {
    }

    public RequestFlag(Date requestTime, boolean isAvailable) {
        this.requestTime = requestTime;
        this.isAvailable = isAvailable;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

package com.atin.arcface.model;

public class PCCovidHistoryInjection {
    private String state;
    private int order;
    private long lastTime;
    private long expiredTime;
    private String injectionName;
    private String injectionAddress;
    private String lotNumber;

    public PCCovidHistoryInjection() {
    }

    public PCCovidHistoryInjection(String state, int order, long lastTime, long expiredTime, String injectionName, String injectionAddress, String lotNumber) {
        this.state = state;
        this.order = order;
        this.lastTime = lastTime;
        this.expiredTime = expiredTime;
        this.injectionName = injectionName;
        this.injectionAddress = injectionAddress;
        this.lotNumber = lotNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getInjectionName() {
        return injectionName;
    }

    public void setInjectionName(String injectionName) {
        this.injectionName = injectionName;
    }

    public String getInjectionAddress() {
        return injectionAddress;
    }

    public void setInjectionAddress(String injectionAddress) {
        this.injectionAddress = injectionAddress;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
}

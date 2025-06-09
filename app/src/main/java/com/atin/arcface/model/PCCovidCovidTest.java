package com.atin.arcface.model;

public class PCCovidCovidTest {
    private String state;
    private int order;
    private int lastTime;
    private int expiredTime;
    private int result;
    private int techTest;
    private String locationTest;

    public PCCovidCovidTest() {
    }

    public PCCovidCovidTest(String state, int order, int lastTime, int expiredTime, int result, int techTest, String locationTest) {
        this.state = state;
        this.order = order;
        this.lastTime = lastTime;
        this.expiredTime = expiredTime;
        this.result = result;
        this.techTest = techTest;
        this.locationTest = locationTest;
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

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public int getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(int expiredTime) {
        this.expiredTime = expiredTime;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTechTest() {
        return techTest;
    }

    public void setTechTest(int techTest) {
        this.techTest = techTest;
    }

    public String getLocationTest() {
        return locationTest;
    }

    public void setLocationTest(String locationTest) {
        this.locationTest = locationTest;
    }
}

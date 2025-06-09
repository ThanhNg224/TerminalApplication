package com.atin.arcface.model;

public class LogResponseModel {
    private String imei;
    private String message;
    private String time;
    private String version;

    public LogResponseModel(String imei, String message, String time, String version) {
        this.imei = imei;
        this.message = message;
        this.time = time;
        this.version = version;
    }

    public LogResponseModel(String imei, String message, String time) {
        this.imei = imei;
        this.message = message;
        this.time = time;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

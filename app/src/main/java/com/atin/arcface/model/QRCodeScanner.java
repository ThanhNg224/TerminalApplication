package com.atin.arcface.model;

import com.atin.arcface.common.Constants;

import java.util.Date;

public class QRCodeScanner {
    private String value;
    private Date scanTime;
    private int status;

    public QRCodeScanner() {
    }

    public void updateTyping(String value){
        this.value = value;
        this.status = Constants.QRCodeScanStatus.TYPING;
        this.scanTime = new Date();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.atin.arcface.model;

public class CacheRequestId {
    private int rgbId;
    private int irId;

    public CacheRequestId() {
    }

    public CacheRequestId(int rgbId, int irId) {
        this.rgbId = rgbId;
        this.irId = irId;
    }

    public int getRgbId() {
        return rgbId;
    }

    public void setRgbId(int rgbId) {
        this.rgbId = rgbId;
    }

    public int getIrId() {
        return irId;
    }

    public void setIrId(int irId) {
        this.irId = irId;
    }
}

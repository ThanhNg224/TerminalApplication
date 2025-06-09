package com.atin.arcface.model;

public class DeviceConfigurationModel {
    private String deviceCode;
    private int cameraRotation;
    private int rectRotation;
    private boolean isMirror;
    private boolean isMirrorVertical;
    private boolean isMirrorHorizontal;

    public DeviceConfigurationModel(String deviceCode, int cameraRotation, int rectRotation, boolean isMirror, boolean isMirrorVertical, boolean isMirrorHorizontal) {
        this.deviceCode = deviceCode;
        this.cameraRotation = cameraRotation;
        this.rectRotation = rectRotation;
        this.isMirror = isMirror;
        this.isMirrorVertical = isMirrorVertical;
        this.isMirrorHorizontal = isMirrorHorizontal;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public int getCameraRotation() {
        return cameraRotation;
    }

    public int getRectRotation() {
        return rectRotation;
    }

    public boolean isMirror() {
        return isMirror;
    }

    public boolean isMirrorVertical() {
        return isMirrorVertical;
    }

    public boolean isMirrorHorizontal() {
        return isMirrorHorizontal;
    }
}

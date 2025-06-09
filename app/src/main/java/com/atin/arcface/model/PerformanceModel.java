package com.atin.arcface.model;

public class PerformanceModel {
    private String imei;
    private String serial;
    private String macAddress;
    private String model;
    private String versionName;
    private int versionCode;
    private float cpu;
    private float ram;
    private float temperature;
    private String storageSize;
    private String storageAvl;
    private String osTime;

    public PerformanceModel() {
    }

    public PerformanceModel(String imei, String serial, String macAddress, String model, String versionName, int versionCode, float cpu, float ram, float temperature, String storageSize, String storageAvl, String osTime) {
        this.imei = imei;
        this.serial = serial;
        this.macAddress = macAddress;
        this.model = model;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.cpu = cpu;
        this.ram = ram;
        this.temperature = temperature;
        this.storageSize = storageSize;
        this.storageAvl = storageAvl;
        this.osTime = osTime;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public float getCpu() {
        return cpu;
    }

    public void setCpu(float cpu) {
        this.cpu = cpu;
    }

    public float getRam() {
        return ram;
    }

    public void setRam(float ram) {
        this.ram = ram;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(String storageSize) {
        this.storageSize = storageSize;
    }

    public String getStorageAvl() {
        return storageAvl;
    }

    public void setStorageAvl(String storageAvl) {
        this.storageAvl = storageAvl;
    }

    public String getOsTime() {
        return osTime;
    }

    public void setOsTime(String osTime) {
        this.osTime = osTime;
    }
}

package com.atin.arcface.model;

public class DeviceInfo {
    private String deviceTime;
    private String ipAddress;
    private String serialNumber;
    private String appVersion;
    private int appVersioNumber;
    private String appPackage;
    private String deviceModel;
    private int initEngineCode;
    private String lastRebootTime;
    private TableSummaryReport tableSummaryReport;
    private MachineDB faceTerminal;

    public DeviceInfo() {
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getAppVersioNumber() {
        return appVersioNumber;
    }

    public void setAppVersioNumber(int appVersioNumber) {
        this.appVersioNumber = appVersioNumber;
    }

    public int getInitEngineCode() {
        return initEngineCode;
    }

    public void setInitEngineCode(int initEngineCode) {
        this.initEngineCode = initEngineCode;
    }

    public String getLastRebootTime() {
        return lastRebootTime;
    }

    public void setLastRebootTime(String lastRebootTime) {
        this.lastRebootTime = lastRebootTime;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public TableSummaryReport getTableSummaryReport() {
        return tableSummaryReport;
    }

    public void setTableSummaryReport(TableSummaryReport tableSummaryReport) {
        this.tableSummaryReport = tableSummaryReport;
    }

    public MachineDB getFaceTerminal() {
        return faceTerminal;
    }

    public void setFaceTerminal(MachineDB faceTerminal) {
        this.faceTerminal = faceTerminal;
    }
}

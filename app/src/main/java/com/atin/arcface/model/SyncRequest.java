package com.atin.arcface.model;

public class SyncRequest {
    private int syncId;
    private int dataType;
    private int actionType;
    private String data;
    private int machineId;
    private String requestTime;
    private String responseTime;
    private String registerBy;
    private String log;
    private int status;

    public SyncRequest() {
    }

    public SyncRequest(int syncId, int dataType, int actionType, String data, int machineId, String requestTime, String responseTime, String registerBy, String log, int status) {
        this.syncId = syncId;
        this.dataType = dataType;
        this.actionType = actionType;
        this.data = data;
        this.machineId = machineId;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.registerBy = registerBy;
        this.log = log;
        this.status = status;
    }

    public int getSyncId() {
        return syncId;
    }

    public void setSyncId(int syncId) {
        this.syncId = syncId;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getRegisterBy() {
        return registerBy;
    }

    public void setRegisterBy(String registerBy) {
        this.registerBy = registerBy;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

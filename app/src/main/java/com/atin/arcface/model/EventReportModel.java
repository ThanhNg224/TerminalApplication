package com.atin.arcface.model;

public class EventReportModel {
    private String accessDate;
    private int totalRecord;
    private int syncRecord;
    private int waitRecord;

    public EventReportModel() {
    }

    public EventReportModel(String accessDate, int totalRecord, int syncRecord, int waitRecord) {
        this.accessDate = accessDate;
        this.totalRecord = totalRecord;
        this.syncRecord = syncRecord;
        this.waitRecord = waitRecord;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getSyncRecord() {
        return syncRecord;
    }

    public void setSyncRecord(int syncRecord) {
        this.syncRecord = syncRecord;
    }

    public int getWaitRecord() {
        return waitRecord;
    }

    public void setWaitRecord(int waitRecord) {
        this.waitRecord = waitRecord;
    }
}

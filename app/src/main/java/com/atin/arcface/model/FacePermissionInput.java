package com.atin.arcface.model;

public class FacePermissionInput {
    private String personId;
    private String deviceCode;
    private String eventId;
    private String terminalStartTime;

    public FacePermissionInput() {
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTerminalStartTime() {
        return terminalStartTime;
    }

    public void setTerminalStartTime(String terminalStartTime) {
        this.terminalStartTime = terminalStartTime;
    }
}

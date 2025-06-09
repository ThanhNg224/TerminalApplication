package com.atin.arcface.model;

public class EventSynchronizeDB {
    private String eventId;
    private String personId;
    private String faceId;
    private String fingerId;
    private String cardNo;
    private String face64;
    private int machineId;
    private String accessDate;
    private String accessTime;
    private String accessType;
    private double temperature;
    private int gender;
    private int age;
    private int wearMask;
    private double scoreMatch;
    private String errorCode;
    private int status;

    public EventSynchronizeDB() {}

    public EventSynchronizeDB(String eventId, String personId, String faceId, String fingerId, String cardNo, String face64, int machineId, String accessDate, String accessTime, String accessType, double temperature, int gender, int age, int wearMask, double scoreMatch, String errorCode, int status) {
        this.eventId = eventId;
        this.personId = personId;
        this.faceId = faceId;
        this.fingerId = fingerId;
        this.cardNo = cardNo;
        this.face64 = face64;
        this.machineId = machineId;
        this.accessDate = accessDate;
        this.accessTime = accessTime;
        this.accessType = accessType;
        this.temperature = temperature;
        this.gender = gender;
        this.age = age;
        this.wearMask = wearMask;
        this.scoreMatch = scoreMatch;
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getFingerId() {
        return fingerId;
    }

    public void setFingerId(String fingerId) {
        this.fingerId = fingerId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getFace64() {
        return face64;
    }

    public void setFace64(String face64) {
        this.face64 = face64;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWearMask() {
        return wearMask;
    }

    public void setWearMask(int wearMask) {
        this.wearMask = wearMask;
    }

    public double getScoreMatch() {
        return scoreMatch;
    }

    public void setScoreMatch(double scoreMatch) {
        this.scoreMatch = scoreMatch;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

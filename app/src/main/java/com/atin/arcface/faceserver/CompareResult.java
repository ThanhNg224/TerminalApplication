package com.atin.arcface.faceserver;

import android.hardware.Camera;

import com.atin.arcface.model.FaceInfoCapture;

public class CompareResult {
    private String personId;
    private String fullName;
    private String personType;
    private String position ;
    private String jobDuties;
    private String facePath;
    private boolean isMask;
    private float similar;
    private int trackId;
    private String summaryCode;
    private String detailCode;
    private float temperature;
    private String personCode;
    private int vaccine;
    private int method;
    private String faceCapture;
    private String note;
    private int remainTurnNumber;
    private boolean resultCheckOnline;
    private int machineId;
    private int compId;
    private String deviceCode;
    private FaceInfoCapture faceInfoCapture;
    private Camera.Size previewSize;

    public CompareResult() {}

    public CompareResult(String personId, float similar, String facePath) {
        this.personId = personId;
        this.similar = similar;
        this.facePath = facePath;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getJobDuties() {
        return jobDuties;
    }

    public void setJobDuties(String jobDuties) {
        this.jobDuties = jobDuties;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getSummaryCode() {
        return summaryCode;
    }

    public void setSummaryCode(String summaryCode) {
        this.summaryCode = summaryCode;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public void setDetailCode(String detailCode) {
        this.detailCode = detailCode;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public boolean isMask() {
        return isMask;
    }

    public void setMask(boolean mask) {
        isMask = mask;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public int getVaccine() {
        return vaccine;
    }

    public void setVaccine(int vaccine) {
        this.vaccine = vaccine;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getFaceCapture() {
        return faceCapture;
    }

    public void setFaceCapture(String faceCapture) {
        this.faceCapture = faceCapture;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRemainTurnNumber() {
        return remainTurnNumber;
    }

    public void setRemainTurnNumber(int remainTurnNumber) {
        this.remainTurnNumber = remainTurnNumber;
    }

    public boolean isResultCheckOnline() {
        return resultCheckOnline;
    }

    public void setResultCheckOnline(boolean resultCheckOnline) {
        this.resultCheckOnline = resultCheckOnline;
    }

    public FaceInfoCapture getFaceInfoCapture() {
        return faceInfoCapture;
    }

    public void setFaceInfoCapture(FaceInfoCapture faceInfoCapture) {
        this.faceInfoCapture = faceInfoCapture;
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
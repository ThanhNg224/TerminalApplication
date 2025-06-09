package com.atin.arcface.model;

public class FaceRegisterInfo {
    private String personId;
    private int compId;
    private int deptId;
    private String personCode;
    private String fullName;
    private String position;
    private String jobDuties;
    private byte[] featureData;
    private String facePath;
    private int status;

    public FaceRegisterInfo(String personId, String facePath) {
        this.personId = personId;
        this.facePath = facePath;
    }

    public FaceRegisterInfo(String personId, int compId, int deptId, String personCode, String fullName, String position, String jobDuties, byte[] featureData, String facePath, int status) {
        this.personId = personId;
        this.compId = compId;
        this.deptId = deptId;
        this.personCode = personCode;
        this.fullName = fullName;
        this.position = position;
        this.jobDuties = jobDuties;
        this.featureData = featureData;
        this.facePath = facePath;
        this.status = status;
    }

    public FaceRegisterInfo(String personId, int compId, int deptId, String personCode, String fullName, String position, String jobDuties, int status) {
        this.personId = personId;
        this.compId = compId;
        this.deptId = deptId;
        this.personCode = personCode;
        this.fullName = fullName;
        this.position = position;
        this.jobDuties = jobDuties;
        this.status = status;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public byte[] getFeatureData() {
        return featureData;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
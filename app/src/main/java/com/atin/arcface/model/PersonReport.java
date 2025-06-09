package com.atin.arcface.model;

public class PersonReport {
    private String personId;
    private int compId;
    private int machineId;
    private int depId;
    private String fullName;
    private String personCode;
    private String position;
    private String jobduties;
    private int personType;
    private int featureSize;

    public PersonReport() {
    }

    public PersonReport(String personId, int compId, int depId, String fullName, String personCode, String position, String jobduties, int personType, int featureSize) {
        this.personId = personId;
        this.compId = compId;
        this.depId = depId;
        this.fullName = fullName;
        this.personCode = personCode;
        this.position = position;
        this.jobduties = jobduties;
        this.personType = personType;
        this.featureSize = featureSize;
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

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getDepId() {
        return depId;
    }

    public void setDepId(int depId) {
        this.depId = depId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getJobduties() {
        return jobduties;
    }

    public void setJobduties(String jobduties) {
        this.jobduties = jobduties;
    }

    public int getPersonType() {
        return personType;
    }

    public void setPersonType(int personType) {
        this.personType = personType;
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public void setFeatureSize(int featureSize) {
        this.featureSize = featureSize;
    }
}

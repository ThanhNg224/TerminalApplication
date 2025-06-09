package com.atin.arcface.model;


public class PersonDB {
    private String personId;
    private int compId;
    private int deptId;
    private String personCode;
    private String fullName;
    private String position;
    private String jobDuties;
    private int personType;
    private int status;
    private int vaccine;

    public PersonDB()
    {
    }

    public PersonDB(String personId, int compId, int deptId, String personCode, String fullName, String position, String jobDuties, int personType, int status, int vaccine) {
        this.personId = personId;
        this.compId = compId;
        this.deptId = deptId;
        this.personCode = personCode;
        this.fullName = fullName;
        this.position = position;
        this.jobDuties = jobDuties;
        this.personType = personType;
        this.status = status;
        this.vaccine = vaccine;
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

    public int getPersonType() {
        return personType;
    }

    public void setPersonType(int personType) {
        this.personType = personType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVaccine() {
        return vaccine;
    }

    public void setVaccine(int vaccine) {
        this.vaccine = vaccine;
    }
}

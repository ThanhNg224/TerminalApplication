package com.atin.arcface.model;

public class CompanyDB {

    private int compId;
    private String companyName;
    private int supId;

    public CompanyDB() {
    }

    public CompanyDB(int compId, String companyName, int supId) {
        this.compId = compId;
        this.companyName = companyName;
        this.supId = supId;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getSupId() {
        return supId;
    }

    public void setSupId(int supId) {
        this.supId = supId;
    }

}

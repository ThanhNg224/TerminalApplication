package com.atin.arcface.model;

public class DepartmentDB {

    private int deptId;
    private String deptName;
    private int supId;
    private int compId;

    public DepartmentDB() {
    }

    public DepartmentDB(int deptId, String deptName, int supId, int compId) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.supId = supId;
        this.compId = compId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getSupId() {
        return supId;
    }

    public void setSupId(int supId) {
        this.supId = supId;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }
}

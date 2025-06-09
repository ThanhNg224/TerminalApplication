package com.atin.arcface.model;


public class GroupDB {

    private int groupId;

    private int deptId;

    private String groupName;

    public GroupDB() {

    }

    public GroupDB(int groupId, int deptId, String groupName) {
        this.groupId = groupId;
        this.deptId = deptId;
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}

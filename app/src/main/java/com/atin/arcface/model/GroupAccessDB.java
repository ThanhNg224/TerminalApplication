package com.atin.arcface.model;

public class GroupAccessDB {

    private int id;

    private int groupId;

    private int machineId;

    private int timeSegId;

    public GroupAccessDB() {

    }

    public GroupAccessDB(int id, int groupId, int machineId, int timeSegId) {
        this.id = id;
        this.groupId = groupId;
        this.machineId = machineId;
        this.timeSegId = timeSegId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getTimeSegId() {
        return timeSegId;
    }

    public void setTimeSegId(int timeSegId) {
        this.timeSegId = timeSegId;
    }
}

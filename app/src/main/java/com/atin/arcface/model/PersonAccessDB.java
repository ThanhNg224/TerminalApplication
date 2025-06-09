package com.atin.arcface.model;

public class PersonAccessDB {
    private int id;
    private String personId;
    private int machineId;
    private String fromdate;
    private String todate;
    private int isDelete;

    public PersonAccessDB() {
    }

    public PersonAccessDB(int id, String personId, int machineId, String fromdate, String todate, int isDelete) {
        this.id = id;
        this.personId = personId;
        this.machineId = machineId;
        this.fromdate = fromdate;
        this.todate = todate;
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}

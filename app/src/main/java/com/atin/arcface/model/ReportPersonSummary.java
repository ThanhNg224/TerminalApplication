package com.atin.arcface.model;

public class ReportPersonSummary {
    private int machineId;
    private int totalPerson;
    private int totalStaff;
    private int totalGuest;
    private int totalStaffValid;
    private int totalStaffInvalid;
    private int totalGuestValid;
    private int totalGuestInvalid;
    private String note;

    public ReportPersonSummary() {
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getTotalPerson() {
        return totalPerson;
    }

    public void setTotalPerson(int totalPerson) {
        this.totalPerson = totalPerson;
    }

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public int getTotalGuest() {
        return totalGuest;
    }

    public void setTotalGuest(int totalGuest) {
        this.totalGuest = totalGuest;
    }

    public int getTotalStaffValid() {
        return totalStaffValid;
    }

    public void setTotalStaffValid(int totalStaffValid) {
        this.totalStaffValid = totalStaffValid;
    }

    public int getTotalStaffInvalid() {
        return totalStaffInvalid;
    }

    public void setTotalStaffInvalid(int totalStaffInvalid) {
        this.totalStaffInvalid = totalStaffInvalid;
    }

    public int getTotalGuestValid() {
        return totalGuestValid;
    }

    public void setTotalGuestValid(int totalGuestValid) {
        this.totalGuestValid = totalGuestValid;
    }

    public int getTotalGuestInvalid() {
        return totalGuestInvalid;
    }

    public void setTotalGuestInvalid(int totalGuestInvalid) {
        this.totalGuestInvalid = totalGuestInvalid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

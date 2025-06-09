package com.atin.arcface.model;

public class TableSummaryReport {
    private int accessTimeSeg;
    private int person;
    private int face;
    private int personGroup;
    private int personAccess;
    private int groupAccess;
    private int faceTerminal;
    private int event;

    public TableSummaryReport() {
    }

    public int getAccessTimeSeg() {
        return accessTimeSeg;
    }

    public void setAccessTimeSeg(int accessTimeSeg) {
        this.accessTimeSeg = accessTimeSeg;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(int personGroup) {
        this.personGroup = personGroup;
    }

    public int getPersonAccess() {
        return personAccess;
    }

    public void setPersonAccess(int personAccess) {
        this.personAccess = personAccess;
    }

    public int getGroupAccess() {
        return groupAccess;
    }

    public void setGroupAccess(int groupAccess) {
        this.groupAccess = groupAccess;
    }

    public int getFaceTerminal() {
        return faceTerminal;
    }

    public void setFaceTerminal(int faceTerminal) {
        this.faceTerminal = faceTerminal;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}

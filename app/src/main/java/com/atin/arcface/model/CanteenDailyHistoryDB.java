package com.atin.arcface.model;

import java.io.Serializable;

public class CanteenDailyHistoryDB implements Serializable {

    private int id;
    private String personId;
    private String logDate;
    private int number;
    private boolean isDelete;

    public CanteenDailyHistoryDB() { }

    public CanteenDailyHistoryDB(int id, String personId,
                                     String logDate, int number, boolean isDelete) {
        this.id = id;
        this.personId = personId;
        this.logDate = logDate;
        this.number = number;
        this.isDelete = isDelete;
    }

    public int getId()                 { return id; }
    public void setId(int id)          { this.id = id; }

    public String getPersonId()        { return personId; }
    public void setPersonId(String p)  { this.personId = p; }

    public String getLogDate()         { return logDate; }
    public void setLogDate(String d)   { this.logDate = d; }

    public int getNumber()             { return number; }
    public void setNumber(int n)       { this.number = n; }

    public boolean isDelete()          { return isDelete; }
    public void setDelete(boolean del) { this.isDelete = del; }

    @Override
    public String toString() {
        return "CanteenDailyHistoryEntity{" +
                "id=" + id +
                ", personId='" + personId + '\'' +
                ", logDate='" + logDate + '\'' +
                ", number=" + number +
                ", isDelete=" + isDelete + '}';
    }
}

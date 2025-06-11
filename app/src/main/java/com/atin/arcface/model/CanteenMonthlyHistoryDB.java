package com.atin.arcface.model;

import java.io.Serializable;

public class CanteenMonthlyHistoryDB implements Serializable {

    private int id;
    private String personId;
    private int reportMonth;
    private int reportYear;
    private int totalNumber;
    private boolean isDelete;

    public CanteenMonthlyHistoryDB() { }

    public CanteenMonthlyHistoryDB(int id, String personId,
                                       int reportMonth, int reportYear,
                                       int totalNumber, boolean isDelete) {
        this.id = id;
        this.personId = personId;
        this.reportMonth = reportMonth;
        this.reportYear = reportYear;
        this.totalNumber = totalNumber;
        this.isDelete = isDelete;
    }

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getPersonId()               { return personId; }
    public void setPersonId(String p)         { this.personId = p; }

    public int getReportMonth()               { return reportMonth; }
    public void setReportMonth(int m)         { this.reportMonth = m; }

    public int getReportYear()                { return reportYear; }
    public void setReportYear(int y)          { this.reportYear = y; }

    public int getTotalNumber()               { return totalNumber; }
    public void setTotalNumber(int t)         { this.totalNumber = t; }

    public boolean isDelete()                 { return isDelete; }
    public void setDelete(boolean del)        { this.isDelete = del; }

    @Override
    public String toString() {
        return "CanteenMonthlyHistoryEntity{" +
                "id=" + id +
                ", personId='" + personId + '\'' +
                ", reportMonth=" + reportMonth +
                ", reportYear=" + reportYear +
                ", totalNumber=" + totalNumber +
                ", isDelete=" + isDelete + '}';
    }
}

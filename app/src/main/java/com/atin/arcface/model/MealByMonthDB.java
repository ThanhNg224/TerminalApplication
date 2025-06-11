package com.atin.arcface.model;

import java.io.Serializable;

public class MealByMonthDB implements Serializable {

    private int id;
    private int month;
    private int year;
    private int eatCount;
    private int compId;

    public MealByMonthDB() { }

    public MealByMonthDB(int id, int month, int year, int eatCount, int compId) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.eatCount = eatCount;
        this.compId = compId;
    }


    public int getId()            { return id; }
    public void setId(int id)     { this.id = id; }

    public int getMonth()         { return month; }
    public void setMonth(int m)   { this.month = m; }

    public int getYear()          { return year; }
    public void setYear(int y)    { this.year = y; }

    public int getEatCount()          { return eatCount; }
    public void setEatCount(int cnt)  { this.eatCount = cnt; }

    public int getCompId()        { return compId; }
    public void setCompId(int c)  { this.compId = c; }

    @Override
    public String toString() {
        return "MealByMonthEntity{" +
                "id=" + id +
                ", month=" + month +
                ", year=" + year +
                ", eatCount=" + eatCount +
                ", compId=" + compId + '}';
    }
}

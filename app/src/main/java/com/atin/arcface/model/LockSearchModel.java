package com.atin.arcface.model;

import java.util.Date;

public class LockSearchModel {
    public Date searchTime;
    public boolean isDone;

    public LockSearchModel() {
    }

    public LockSearchModel(Date searchTime, boolean isDone) {
        this.searchTime = searchTime;
        this.isDone = isDone;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}

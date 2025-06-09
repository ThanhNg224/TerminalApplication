package com.atin.arcface.model;

import com.google.gson.JsonObject;

public class PCCovidResult {
    private int Status;
    private JsonObject Object;
    private boolean isOk;
    private boolean isError;

    public PCCovidResult() {
    }

    public PCCovidResult(int status, JsonObject object, boolean isOk, boolean isError) {
        Status = status;
        Object = object;
        this.isOk = isOk;
        this.isError = isError;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public JsonObject getObject() {
        return Object;
    }

    public void setObject(JsonObject object) {
        Object = object;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}

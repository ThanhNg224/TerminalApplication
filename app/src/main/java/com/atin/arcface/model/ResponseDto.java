package com.atin.arcface.model;

import java.util.Date;

public class ResponseDto<T>{
    private Date timestamp;
    private int status;
    private String error;
    private T data;

    public ResponseDto() {
    }

    public ResponseDto(Date timestamp, int status, String error, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

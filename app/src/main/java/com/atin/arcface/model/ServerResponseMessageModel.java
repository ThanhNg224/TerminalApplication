package com.atin.arcface.model;

public class ServerResponseMessageModel<T> {
    private String timestamp;
    private String status;
    private String error;
    private T data;

    public ServerResponseMessageModel() {
    }

    public ServerResponseMessageModel(String timestamp, String status, String error, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public T getData() {
        return data;
    }
}

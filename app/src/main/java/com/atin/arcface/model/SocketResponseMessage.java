package com.atin.arcface.model;

public class SocketResponseMessage<T>{
    private String actionType;
    private String status;
    private T data;
    private String requestId;

    public SocketResponseMessage() {
    }

    public SocketResponseMessage(String actionType, String status, T data, String requestId) {
        this.actionType = actionType;
        this.status = status;
        this.data = data;
        this.requestId = requestId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

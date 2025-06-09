package com.atin.arcface.model;

public class SocketRequestMessage {
    private String actionType;
    private String data;
    private String requestId;

    public SocketRequestMessage() {
    }

    public SocketRequestMessage(String actionType, String data, String requestId) {
        this.actionType = actionType;
        this.data = data;
        this.requestId = requestId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

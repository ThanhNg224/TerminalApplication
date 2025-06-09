package com.atin.arcface.model;

public class AuthenticateResponseModel {
    private String token;

    public AuthenticateResponseModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

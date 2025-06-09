package com.atin.arcface.model;

public class TokenModel {
    private String unitName;
    private String expireTime;
    private String hashKey;

    public TokenModel() {
    }

    public TokenModel(String unitName, String expireTime, String hashKey) {
        this.unitName = unitName;
        this.expireTime = expireTime;
        this.hashKey = hashKey;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}

package com.atin.arcface.model;

public class AccessResult {
    private String detailCode;
    private String sumaryCode;

    public AccessResult() {
    }

    public AccessResult(String detailCode, String sumaryCode) {
        this.detailCode = detailCode;
        this.sumaryCode = sumaryCode;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public void setDetailCode(String detailCode) {
        this.detailCode = detailCode;
    }

    public String getSumaryCode() {
        return sumaryCode;
    }

    public void setSumaryCode(String sumaryCode) {
        this.sumaryCode = sumaryCode;
    }
}

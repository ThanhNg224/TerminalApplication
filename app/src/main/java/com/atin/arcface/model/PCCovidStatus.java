package com.atin.arcface.model;

public class PCCovidStatus {
    private String pccovidName;
    private int pccovidValue;

    public PCCovidStatus(String pccovidName, int pccovidValue) {
        this.pccovidName = pccovidName;
        this.pccovidValue = pccovidValue;
    }

    public String getPccovidName() {
        return pccovidName;
    }

    public void setPccovidName(String pccovidName) {
        this.pccovidName = pccovidName;
    }

    public int getPccovidValue() {
        return pccovidValue;
    }

    public void setPccovidValue(int pccovidValue) {
        this.pccovidValue = pccovidValue;
    }
}

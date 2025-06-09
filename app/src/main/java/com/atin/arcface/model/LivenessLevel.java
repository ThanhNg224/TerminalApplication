package com.atin.arcface.model;

public class LivenessLevel {
    private String levelName;
    private int levelValue;

    public LivenessLevel() {
    }

    public LivenessLevel(String levelName, int levelValue) {
        this.levelName = levelName;
        this.levelValue = levelValue;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(int levelValue) {
        this.levelValue = levelValue;
    }
}

package com.atin.arcface.model;

public class CircleWraper {
    private int centerX;
    private int centerY;
    private int r;

    public CircleWraper(int centerX) {
    }

    public CircleWraper(int centerX, int centerY, int r) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.r = r;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }
}

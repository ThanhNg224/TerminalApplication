package com.atin.arcface.model;

public class PCCovidSearch {
    private String A;
    private String B;
    private String X;

    public PCCovidSearch() {
    }

    public PCCovidSearch(String a, String b, String x) {
        A = a;
        B = b;
        X = x;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }
}

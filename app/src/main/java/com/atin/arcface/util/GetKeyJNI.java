package com.atin.arcface.util;

public class GetKeyJNI {

    static {
        try {
            System.loadLibrary("GetKeyJNI");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public synchronized static native String key();
}

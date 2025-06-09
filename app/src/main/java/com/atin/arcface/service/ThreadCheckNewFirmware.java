package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;

import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;

import java.util.concurrent.TimeUnit;

public class ThreadCheckNewFirmware extends Thread {
    private final String TAG = "ThreadCheckNewFirmware";
    private Context mContext;

    public ThreadCheckNewFirmware(Context context){
        this.mContext = context;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        UpdateFirmware updateFirmware = new UpdateFirmware(mContext);
        updateFirmware.checkNewFirmware();
    }
}
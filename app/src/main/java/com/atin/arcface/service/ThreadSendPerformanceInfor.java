package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;

import com.atin.arcface.faceserver.SendPerformanceInfor;
import com.atin.arcface.faceserver.SynchronizeServer;

import java.util.concurrent.TimeUnit;

public class ThreadSendPerformanceInfor extends Thread {
    private final String TAG = "ThreadSendPerformanceInfor";

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        SendPerformanceInfor.getInstance().initValue();
        SendPerformanceInfor.getInstance().setInitAlready(true);
        SendPerformanceInfor.getInstance().synchronize();
    }
}

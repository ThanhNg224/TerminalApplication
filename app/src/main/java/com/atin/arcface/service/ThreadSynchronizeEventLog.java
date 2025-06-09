package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;

import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.faceserver.SyncLogServer;
import com.atin.arcface.model.EventDB;

import java.util.concurrent.TimeUnit;

public class ThreadSynchronizeEventLog extends Thread {
    private final String TAG = "ThreadSynchronizeEventLog";
    private Context mContext;
    private SyncLogServer syncLogServer;

    public ThreadSynchronizeEventLog(Context context){
        this.mContext = context;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        syncLogServer = new SyncLogServer(mContext);
        syncLogServer.doSynchronize();
    }

    public void sendLogRealtime(EventDB event){
        if(syncLogServer != null){
            syncLogServer.doSynchronizeLogRealTime(event);
        }
    }
}

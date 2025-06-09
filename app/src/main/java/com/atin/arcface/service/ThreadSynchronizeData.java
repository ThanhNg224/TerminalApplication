package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;
import com.atin.arcface.faceserver.SynchronizeServer;
import java.util.concurrent.TimeUnit;

public class ThreadSynchronizeData extends Thread {
    private final String TAG = "ThreadSynchronizeData";
    private Context mContext;

    public ThreadSynchronizeData(Context context){
        this.mContext = context;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        SynchronizeServer synchronizeDataServer = new SynchronizeServer(mContext);
        synchronizeDataServer.doSynchronize();
    }
}

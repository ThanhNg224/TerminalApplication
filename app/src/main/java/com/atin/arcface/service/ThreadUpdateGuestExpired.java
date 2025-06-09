package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;
import java.util.concurrent.TimeUnit;

public class ThreadUpdateGuestExpired extends Thread {
    private final Context mContext;

    public ThreadUpdateGuestExpired(Context context){
        this.mContext = context;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            String TAG = "ThreadUpdateGuestExpired";
            Log.e(TAG, e.getMessage());
        }
        AutoUpdateGuestExpried autoUpdateGuestExpried = new AutoUpdateGuestExpried(mContext);
        autoUpdateGuestExpried.doUpdate();
    }
}

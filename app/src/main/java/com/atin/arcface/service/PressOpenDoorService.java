package com.atin.arcface.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.atin.arcface.common.ErrorCode;
import com.atin.arcface.common.Hardware;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.util.BaseUtil;
import com.roco.api.weigenRelay.weigenRelay;

public class PressOpenDoorService extends Service {
    private static String TAG = "PressOpenDoorService";
    private boolean isServerRunning = false;

    private Handler handler;
    private Runnable runnable;

    private weigenRelay f6A80MRakindaWG = null;
    private boolean isRelayOpen; //Kiểm tra trạng thái relay đang đóng hay mở
    private boolean isAvailable = true; //Kiểm tra nút nhấn đang nhấn hay nhả

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServerRunning) {
            startServer();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    private void startServer() {
        isServerRunning = true;
        try {
            if(Build.MODEL.equals(MachineName.RAKINDA_A80M)){
                handler = new Handler(Looper.getMainLooper());
                f6A80MRakindaWG = new weigenRelay(getApplicationContext());
                loopGetValue();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void stopServer() {
        isServerRunning = false;
        try{
            handler.removeCallbacks(runnable);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private void loopGetValue() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getIO4Value();
                handler.postDelayed(this, 100); // Delay 1 second
            }
        };
        handler.post(runnable);
    }

    private void getIO4Value(){
        f6A80MRakindaWG.setGpioDirIn(161);
        int result = f6A80MRakindaWG.getGpioInData(161);
        if(result == 1){
            //Log.d("RELAY", "IO4 đóng");
            isAvailable = true;
        }else if(result == 0){
            //Log.d("RELAY", "IO4 mở");
            openDoor();
        }
    }

    private void openDoor(){
        if(!isAvailable || isRelayOpen){
            Log.d("RELAY", "Cancel");
            return;
        }

        Log.d("RELAY", "Open");
        new Thread(() -> {
            isAvailable = false;
            isRelayOpen = true;
            Hardware.openDoor(ErrorCode.CHECKIN_VALID, 1000, Build.MODEL, getApplicationContext());
            isRelayOpen = false;
        }).start();
    }
}

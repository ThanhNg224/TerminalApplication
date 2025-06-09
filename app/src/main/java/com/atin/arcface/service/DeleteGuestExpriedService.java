package com.atin.arcface.service;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.atin.arcface.activity.Application;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.faceserver.FaceServer;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.util.BaseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeleteGuestExpriedService extends Service {
    private static String TAG = "DeleteGuestExpriedService";
    private Database database;
    private HandlerThread handlerThread;
    private Handler serviceHandler;
    private static final long DELAY = 30*60*1000; // Thời gian delay giữa các lần chạy

    @Override
    public void onCreate() {
        super.onCreate();
        database = Application.getInstance().getDatabase();

        // Tạo một HandlerThread và bắt đầu nó
        handlerThread = new HandlerThread("DeleteGuestExpriedService", THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        // Tạo một Handler và liên kết nó với looper của HandlerThread
        serviceHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Gửi một Runnable để thực hiện các hoạt động trong Service trên luồng riêng biệt
        serviceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Thực hiện các hoạt động không làm block main thread ở đây
                doDelete();

                // Lặp lại chạy Runnable sau khoảng thời gian DELAY
                serviceHandler.postDelayed(this, DELAY);
            }
        }, DELAY);

        // Trả về giá trị START_STICKY để khởi động lại Service nếu bị hủy bởi hệ điều hành
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Loại bỏ các callback và ngừng vòng lặp khi Service bị hủy
        serviceHandler.removeCallbacks(handlerThread);

        // Hủy bỏ luồng và Handler
        handlerThread.quit();
        handlerThread = null;
        serviceHandler = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void doDelete(){
        try {
            String imei = BaseUtil.getImeiNumber(getApplicationContext());
            MachineDB machine = database.getMachineByImei(imei);
            List<FaceRegisterInfo> lsGuestExpired = new ArrayList<>();
            if(machine != null){
                lsGuestExpired = database.getGuestExpired(machine.getMachineId(), 0);
            }

            if(!lsGuestExpired.isEmpty()){
                database.deactivePerson(lsGuestExpired);
                FaceServer.getInstance().initFaceList(getApplicationContext());
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            try {
                TimeUnit.MINUTES.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;

import com.atin.arcface.activity.Application;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.faceserver.FaceServer;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoUpdateGuestExpried {
    private static String TAG = "AutoUpdateGuestExpried";
    private Database database;
    private Context mContext;

    public AutoUpdateGuestExpried(Context context) {
        this.mContext = context;
        initDatabase();
    }

    public void doUpdate(){
        while(true){
            try {
                String imei = BaseUtil.getImeiNumber(mContext);
                MachineDB machine = database.getMachineByImei(imei);
                List<FaceRegisterInfo> lsGuestExpired = new ArrayList<>();
                if(machine != null){
                    lsGuestExpired = database.getGuestExpired(machine.getMachineId(), 0);
                }

                if(!lsGuestExpired.isEmpty()){
                    database.deactivePerson(lsGuestExpired);
                    FaceServer.getInstance().initFaceList(mContext);
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

    private void initDatabase() {
        database = Application.getInstance().getDatabase();
    }
}

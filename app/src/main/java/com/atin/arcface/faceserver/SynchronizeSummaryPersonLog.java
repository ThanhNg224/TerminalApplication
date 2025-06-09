package com.atin.arcface.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.BuildConfig;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.EventDB;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.model.ReportPersonSummary;
import com.atin.arcface.service.LogPersonResponseServer;
import com.atin.arcface.service.LogResponseServer;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//Class xử lý post log event giao dịch
public class SynchronizeSummaryPersonLog {
    private String TAG = "SyncPerson";
    private Context mContext;
    private Database database;
    private String domain, imei;
    private Logger log;
    private MachineDB thisDevice = null;
    private ExecutorService excutor;
    private LinkedBlockingQueue<Runnable> blockingQueue;
    private static SynchronizeSummaryPersonLog mInstance;

    /**
     * Singleton construct design pattern.
     *
     * @param context parent context
     * @return single instance of LogResponseServer
     */
    public static synchronized SynchronizeSummaryPersonLog getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SynchronizeSummaryPersonLog(context);
        }
        return mInstance;
    }

    public SynchronizeSummaryPersonLog(Context context) {
        this.mContext = context;
        database = Application.getInstance().getDatabase();
        imei = SingletonObject.getInstance(mContext).getImei();
        initValue();
    }

    private void initValue(){
        log = Log4jHelper.getLogger( TAG );
        blockingQueue = new LinkedBlockingQueue<>();
        excutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, blockingQueue);

        try{
            thisDevice = database.getMachineByImei(imei);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        if(thisDevice == null){
            thisDevice = ConfigUtil.getMachine();
        }
    }

    public void doSynchronize(){
        while(true){
            try {
                if(BaseUtil.isNetworkConnected(mContext)){
                    synchronizeLogRecord();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                try {
                    TimeUnit.MINUTES.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void summaryAndSend(){
        try {
            if(BaseUtil.isNetworkConnected(mContext)){
                synchronizeLogRecord();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void synchronizeLogRecord() {
        if(thisDevice == null){
            try{
                thisDevice = database.getMachineByImei(imei);
            }catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
            if(thisDevice == null){
                thisDevice = ConfigUtil.getMachine();
            }
        }

        int totalPerson = database.countTotalPerson();

        int totalStaff = database.countTotalStaff();
        int totalStaffValid = database.countTotalStaffValid();
        int totalStaffInvalid = database.countTotalStaffInvalid();
        int totalStaffInvalidNoFace = database.countTotalStaffInvalidNoFace();
        int staffInvalid = totalStaffInvalid + totalStaffInvalidNoFace;

        int totalGuest = database.countTotalGuest();
        int totalGuestValid = database.countTotalGuestValid();
        int totalGuestInvalid = database.countTotalGuestInvalid();
        int totalGuestInvalidNoFace = database.countTotalGuestInvalidNoFace();
        int guestInvalid = totalGuestInvalid + totalGuestInvalidNoFace;

        String note = BaseUtil.getSerialNumber() + " - " + BuildConfig.VERSION_NAME + " - " + BuildConfig.VERSION_CODE;

        ReportPersonSummary reportPersonSummary = new ReportPersonSummary();
        reportPersonSummary.setMachineId(thisDevice.getMachineId());
        reportPersonSummary.setTotalPerson(totalPerson);

        reportPersonSummary.setTotalStaff(totalStaff);
        reportPersonSummary.setTotalGuest(totalGuest);

        reportPersonSummary.setTotalStaffValid(totalStaffValid);
        reportPersonSummary.setTotalGuestValid(totalGuestValid);

        reportPersonSummary.setTotalStaffInvalid(staffInvalid);
        reportPersonSummary.setTotalGuestInvalid(guestInvalid);

        reportPersonSummary.setNote(note);

        excutor.execute(() -> {
            try {
                String message = SingletonObject.getInstance(mContext).getGSon().toJson(reportPersonSummary);
                LogResponseServer.getInstance(mContext).responseLog(message);
            } catch (Exception ex) {
                log.error("Error send summary person log: " + ex.getMessage());
            }
        });

        return;
    }
}

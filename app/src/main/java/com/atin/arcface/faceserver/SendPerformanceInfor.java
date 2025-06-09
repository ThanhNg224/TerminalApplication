package com.atin.arcface.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.BuildConfig;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.PerformanceModel;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.StringUtils;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//Class xử lý post log event giao dịch
public class SendPerformanceInfor {
    private String TAG = "SendPerformanceInfor";
    private Context mContext;
    private SharedPreferences pref;
    private String domain;
    private JsonObjectRequest request;
    private ExecutorService excutor;
    private LinkedBlockingQueue<Runnable> executeQueue;
    private LinkedBlockingQueue<String> messageQueue;
    private PerformanceModel performanceModel;
    private static SendPerformanceInfor instance = null;
    private boolean initAlready = false;
    private boolean stopSend = false;

    public static SendPerformanceInfor getInstance() {
        if (instance == null) {
            synchronized (SendPerformanceInfor.class) {
                if (instance == null) {
                    instance = new SendPerformanceInfor();
                }
            }
        }
        return instance;
    }

    public void initValue(){
        mContext = Application.getInstance().getApplicationContext();
        messageQueue = new LinkedBlockingQueue<>(100);
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        executeQueue = new LinkedBlockingQueue<>(100);
        excutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, executeQueue);

        domain = SingletonObject.getInstance(mContext).getDomain();

        //Init default value
        performanceModel = new PerformanceModel();

        String imei = SingletonObject.getInstance(mContext).getImei();
        String serial = BaseUtil.getSerialNumber();
        String mac = BaseUtil.getMacAddress();
        String model = Build.MODEL;
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;

        performanceModel.setImei(imei);
        performanceModel.setSerial(serial);
        performanceModel.setMacAddress(mac);
        performanceModel.setModel(model);
        performanceModel.setVersionName(versionName);
        performanceModel.setVersionCode(versionCode);
    }

    public void addMessageQueue(String message){
        if(!stopSend){
            messageQueue.add(message);
        }
    }

    public void synchronize(){
        while (true){
            synchronized(this)
            {
                while(!messageQueue.isEmpty()){
                    String event = messageQueue.poll();
                    if(event != null || !event.equals("")){
                        sendPerformanceInfor(event);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPerformanceInfor(String note){
        try {
            String[] arrPerformance = note.split("#");
            float cpu = Float.parseFloat(arrPerformance[0]);
            float ram = Float.parseFloat(arrPerformance[1]);
            float temperature = Float.parseFloat(arrPerformance[2]);
            String storeAval = BaseUtil.getAvailableInternalMemorySize();
            String storeSize = BaseUtil.getTotalInternalMemorySize();

            performanceModel.setCpu(cpu);
            performanceModel.setRam(ram);
            performanceModel.setTemperature(temperature);
            performanceModel.setStorageAvl(storeAval);
            performanceModel.setStorageSize(storeSize);
            performanceModel.setOsTime(StringUtils.currentDatetimeSQLiteformat());

            String currentTime = StringUtils.currentDatetimeSQLiteformat();
            String networkErrorTime = ConfigUtil.getNetworkErrorTime(mContext);
            long alongError = 0;
            try {
                alongError = StringUtils.getBetweenSecond(networkErrorTime, currentTime);
            } catch (Exception ex) {
                alongError = 0;
            }
            if(alongError < 30*1000){
                stopSend = true;
                Log.d(TAG,"Network error sleep 1 minute");
                TimeUnit.MINUTES.sleep(1);
                return;
            }else{
                stopSend = false;
            }

            excutor.execute(() -> {
                try {
                    String url = domain + "/api/v1/update-performance/";
                    doSend(url, performanceModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void doSend(String url, PerformanceModel model) throws Exception {
        String jsonObject = SingletonObject.getInstance(mContext).getGSon().toJson(model, PerformanceModel.class);
        JSONObject jObject = new JSONObject(jsonObject);

        request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    Log.d(TAG, "Update performance success");
                },
                error -> {
                    //ConfigUtil.setNetworkErrorTime(mContext, StringUtils.currentDatetimeSQLiteformat());
                    //String cause = StringUtils.getThrowCause(error);
                    //Log.e(TAG, cause);
                    Log.d(TAG, "Update performance failure");
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public boolean isInitAlready() {
        return initAlready;
    }

    public void setInitAlready(boolean initAlready) {
        this.initAlready = initAlready;
    }
}

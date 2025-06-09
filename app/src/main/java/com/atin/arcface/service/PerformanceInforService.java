package com.atin.arcface.service;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.BuildConfig;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.SendPerformanceInfor;
import com.atin.arcface.model.PerformanceModel;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//Class xử lý post log event giao dịch
public class PerformanceInforService extends Service {
    private String TAG = "PerformanceInforService";
    public static final int MSG_SEND_STRING = 1;

    private String performance = "";
    private SharedPreferences pref;
    private JsonObjectRequest request;
    private ExecutorService excutor;
    private LinkedBlockingQueue<Runnable> executeQueue;
    private PerformanceModel performanceModel;
    private Gson mGson;

    private HandlerThread handlerThread;
    private Handler serviceHandler;
    private Messenger messenger;
    private static final long DELAY = 30*1000; // Thời gian delay giữa các lần chạy

    @Override
    public void onCreate() {
        super.onCreate();
        initValue();

        // Tạo một HandlerThread và bắt đầu nó
        handlerThread = new HandlerThread("PerformanceInforThread", THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        // Tạo một Handler và liên kết nó với looper của HandlerThread
        //serviceHandler = new Handler(handlerThread.getLooper());
        // Tạo một Handler và liên kết nó với looper của HandlerThread
        serviceHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SEND_STRING) {
                    String text = (String) msg.obj;
                    updatePerformanceLog(text);
                }
            }
        };

        // Khởi tạo Messenger với Handler của HandlerThread
        messenger = new Messenger(serviceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Gửi một Runnable để thực hiện các hoạt động trong Service trên luồng riêng biệt
        serviceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Thực hiện các hoạt động không làm block main thread ở đây
                doSynchronize();

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
        // Trả về IBinder của Messenger để main thread có thể gửi thông điệp tới Service
        return messenger.getBinder();
    }

    public void initValue(){
        mGson = SingletonObject.getInstance(getApplicationContext()).getGSon();
        pref = SingletonObject.getInstance(getApplicationContext()).getSharedPreferences();
        executeQueue = new LinkedBlockingQueue<>(100);
        excutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, executeQueue);

        //Init default value
        performanceModel = new PerformanceModel();

        String imei = SingletonObject.getInstance(getApplicationContext()).getImei();
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

    public void doSynchronize(){
        if(performance != null & performance.length() > 0 && BaseUtil.isNetworkConnected(getApplicationContext())){
            sendPerformanceInfor();
        }
    }

    public void updatePerformanceLog(String msg){
        synchronized (performance){
            performance = msg;
        }
    }

    private void sendPerformanceInfor(){
        try {
            String[] arrPerformance = performance.split("#");
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

            excutor.execute(() -> {
                try {
                    String domain = SingletonObject.getInstance(getApplicationContext()).getDomain();
                    String url = domain + "/api/v1/update-performance/";
                    doSend(url, performanceModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }finally {
            performance = "";
        }
    }

    private void doSend(String url, PerformanceModel model) throws Exception {
        String jsonObject = mGson.toJson(model, PerformanceModel.class);
        JSONObject jObject = new JSONObject(jsonObject);

        request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    Log.d(TAG, "Post performance info success");
                },
                error -> {
                    Log.d(TAG, "Post performance info failure");
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}

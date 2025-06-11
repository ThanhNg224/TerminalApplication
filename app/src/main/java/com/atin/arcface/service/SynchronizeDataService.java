package com.atin.arcface.service;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static java.lang.Process.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.ProcessSynchronizeData;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.faceserver.CardServer;
import com.atin.arcface.faceserver.FaceServer;
import com.atin.arcface.model.RequestFlag;
import com.atin.arcface.model.SyncRequest;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SynchronizeDataService extends Service {
    private String TAG = "SynchronizeDataService";
    private ExecutorService excutorResult;
    private volatile LinkedBlockingQueue<Runnable> blockingQueueResult;
    private SharedPreferences pref;
    private ProcessSynchronizeData processSynchronizeData;
    private AuthenticateServer authenticateServer;
    private Gson mGson;
    private HandlerThread handlerThread;
    private Handler serviceHandler;
    private static final long DELAY = 15*1000; // Thời gian delay giữa các lần chạy
    private RequestFlag requestFlag = new RequestFlag(Calendar.getInstance().getTime(), true);

    @Override
    public void onCreate() {
        super.onCreate();
        initValue();
        Log.d("SYNC","Service onCreate()");
        Log.d("SYNC","Service onStartCommand()");


        // Tạo một HandlerThread và bắt đầu nó
        handlerThread = new HandlerThread("SynchronizeDataThread", THREAD_PRIORITY_BACKGROUND);
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
        return null;
    }

    public void initValue() {
        pref = SingletonObject.getInstance(getApplicationContext()).getSharedPreferences();
        processSynchronizeData = new ProcessSynchronizeData(getApplicationContext());
        authenticateServer = SingletonObject.getInstance(getApplicationContext()).getAuthenticateServer();
        mGson = SingletonObject.getInstance(getApplicationContext()).getGSon();

        blockingQueueResult = new LinkedBlockingQueue<>(100);
        excutorResult = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, blockingQueueResult);
    }

    public void doSynchronize(){
        Log.d("SYNC", "🌐 doSynchronize() start");
        String domain = SingletonObject.getInstance(getApplicationContext()).getDomain();
        String imei = SingletonObject.getInstance(getApplicationContext()).getImei();
        String url = domain + "/api/v1/synchronize/" + imei;


        if(pref == null){
            pref = getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
        }

        long diffMinute = TimeUnit.MILLISECONDS.toMinutes(Math.abs(requestFlag.getRequestTime().getTime() - Calendar.getInstance().getTime().getTime()));
        if(requestFlag.isAvailable() || diffMinute > 5)
        {Log.d("SYNC", "🌐 calling getRequest with URL=" + url);
            requestFlag.setRequestTime(Calendar.getInstance().getTime());
            requestFlag.setAvailable(false);
            getRequest(url);
        }
    }

    private void getRequest(String url) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(),
                response -> {
                    Log.d("SYNC", "🌐 fetched "
                            + response.length()
                            + " SyncRequests: "
                            + response.toString());
                    if(blockingQueueResult.isEmpty()){
                        excutorResult.execute(() -> {
                            processData(response.toString());
                            blockingQueueResult.clear();
                        });
                    }
                },
                error -> {
                    requestFlag.setRequestTime(Calendar.getInstance().getTime());
                    requestFlag.setAvailable(true);

                    String cause = StringUtils.getThrowCause(error);
                    if(cause.contains("javax.net.ssl.SSLHandshakeException")){
                        BaseUtil.handleSSLHandshake();
                        return;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void processData(String jsonData){
        ConfigUtil.setSyncingData(getApplicationContext(), true);
        boolean needReload = false;
        Log.d("SYNC", "🔄 processData: " + jsonData);
        try {
            Type listType = new TypeToken<ArrayList<SyncRequest>>(){}.getType();
            List<SyncRequest> listData = mGson.fromJson(jsonData, listType);
            if (listData.size() > 0) {
                BaseUtil.broadUpdateSynchronzieStatus(getApplicationContext(), true);
            }

            String domain = SingletonObject.getInstance(getApplicationContext()).getDomain();
            String responseUrl = domain + "/api/v1/synchronize/";
            int progress = 0;
            for (int i = 0; i < listData.size(); i++) {
                requestFlag.setRequestTime(Calendar.getInstance().getTime());
                requestFlag.setAvailable(false);

                ConfigUtil.setSyncingData(getApplicationContext(), true);
                SyncRequest syncData = listData.get(i);
                try {
                    progress = progress + 1;
                    BaseUtil.broadUpdateSynchronzieStatus(getApplicationContext(), true);

                    int result = processSynchronizeData.onProcess(syncData);
                    if(result == Constants.ProcessResult.RELOAD){
                        needReload = true;
                    }else if(result == Constants.ProcessResult.RESTART){
                        try{
                            syncData.setStatus(2);
                            syncData.setLog("Restart success " + BaseUtil.getSerialNumber() + " - " + BaseUtil.getLocalIpv4());
                            reponseRequest(responseUrl + syncData.getSyncId(), syncData);

                            Thread.sleep(5000);
                            BaseUtil.reboot();
                        }catch (Exception ex){
                            showToast(ex.getMessage());
                        }

                        return;
                    }

                    syncData.setStatus(2);
                    syncData.setLog("Synchronize complete from device");
                    reponseRequest(responseUrl + syncData.getSyncId(), syncData);
                } catch (Exception e) {
                    syncData.setStatus(3);
                    syncData.setRegisterBy("Machine");
                    syncData.setLog(e.getMessage());

                    syncData.setLog(e.getMessage());
                    reponseRequest(responseUrl + syncData.getSyncId(), syncData);
                }
            }

            if(needReload){
                FaceServer.getInstance().initFaceList(getApplicationContext());
                CardServer.getInstance().init(getApplicationContext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }finally {
            ConfigUtil.setSyncingData(getApplicationContext(), false);
            BaseUtil.broadUpdateSynchronzieStatus(getApplicationContext(), false);
            requestFlag.setRequestTime(Calendar.getInstance().getTime());
            requestFlag.setAvailable(true);
        }
    }

    public void reponseRequest(String url, SyncRequest syncRequest) throws Exception {
        String jsonObject = mGson.toJson(syncRequest, SyncRequest.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jObject,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    if (error.getMessage() == null) {
                        String cause = StringUtils.getThrowCause(error);
                    }
                }) {
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

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.atin.arcface.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.ProcessSynchronizeData;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.SyncRequest;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SynchronizeServer {
    private String TAG = "SynchronizeServer";
    private Context mContext;
    private ExecutorService excutorRequest, excutorResult;
    private volatile LinkedBlockingQueue<Runnable> blockingQueueRequest, blockingQueueResult;
    private SharedPreferences pref;
    private String domain, imei;
    private RegisterAndRecognizeDualActivity mainActivity;
    private ProcessSynchronizeData processSynchronizeData;
    private AuthenticateServer authenticateServer;
    private Gson gson;

    public SynchronizeServer(Context context) {
        this.mContext = context;
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        mainActivity = SingletonObject.getInstance(mContext).getMainActivity();
        processSynchronizeData = new ProcessSynchronizeData(mContext);
        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
        domain = SingletonObject.getInstance(mContext).getDomain();
        imei = SingletonObject.getInstance(mContext).getImei();
        gson = SingletonObject.getInstance(mContext).getGSon();
        initValue();
    }

    private void initValue(){
        blockingQueueRequest = new LinkedBlockingQueue<>(100);
        blockingQueueResult = new LinkedBlockingQueue<>(100);
        excutorRequest = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, blockingQueueRequest);
        excutorResult = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, blockingQueueResult);
    }

    public void doSynchronize(){
        String url = domain + "/api/v1/synchronize/" + imei;

        while(true){
            try {
                if(!ConfigUtil.getSyncingData(mContext) && BaseUtil.isNetworkConnected(mContext)){
                    if(blockingQueueRequest.isEmpty()){
                        excutorRequest.execute(() -> {
                            getRequest(url);
                            blockingQueueRequest.clear();
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getRequest(String url) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(),
                response -> {
                    if(blockingQueueResult.isEmpty()){
                        excutorResult.execute(() -> {
                            processData(response.toString());
                            blockingQueueResult.clear();
                        });
                    }
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void processData(String jsonData){
        ConfigUtil.setSyncingData(mContext, true);
        boolean needReload = false;

        try {
            Type listType = new TypeToken<ArrayList<SyncRequest>>(){}.getType();
            List<SyncRequest> listData = gson.fromJson(jsonData, listType);
            if (listData.size() > 0) {
                updateSynchStatus(true);
                //mainActivity.showLoadingDialog("Đồng bộ dữ liệu từ server", "Tải dữ liệu", listData.size(), Constants.PROGRESS_SHOW);
            }

            String responseUrl = domain + "/api/v1/synchronize/";
            int progress = 0;
            for (int i = 0; i < listData.size(); i++) {
                ConfigUtil.setSyncingData(mContext, true);
                SyncRequest syncData = listData.get(i);
                try {
                    progress = progress + 1;
                    updateSynchStatus(true);
                    //mainActivity.showLoadingDialog("Đồng bộ dữ liệu từ server", "Xử lý dữ liệu", progress, Constants.PROGRESS_REFRESH);

                    int result = processSynchronizeData.onProcess(syncData);
                    if(result == Constants.ProcessResult.RELOAD){
                        needReload = true;
                    }else if(result == Constants.ProcessResult.RESTART){
                        try{
                            syncData.setStatus(2);
                            syncData.setLog("Restart success");
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
                FaceServer.getInstance().initFaceList(mContext);
                CardServer.getInstance().init(mContext);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }finally {
            ConfigUtil.setSyncingData(mContext, false);
            updateSynchStatus(false);
            //mainActivity.showLoadingDialog("Đồng bộ dữ liệu từ server", "Hoàn tất", 0, Constants.PROGRESS_CLOSE);
        }
    }

    private void updateSynchStatus(boolean isSynch){
        //mainActivity.runOnUiThread(() -> viewModel.setStatus(isSynch));
        mainActivity.runOnUiThread(() -> mainActivity.updateSynchronizeStatus(isSynch));
    }

    public void reponseRequest(String url, SyncRequest syncRequest) throws Exception {
        String jsonObject = SingletonObject.getInstance(mContext).getGSon().toJson(syncRequest, SyncRequest.class);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void showToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}

package com.atin.arcface.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.model.LogResponseModel;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LogResponseServer {
    private static final String TAG = "LogResponseServer";
    private Context mContext;
    private SharedPreferences pref;
    private String domain;
    private RegisterAndRecognizeDualActivity mainActivity;
    private AuthenticateServer authenticateServer;
    private Gson mGson;
    private static LogResponseServer mInstance;

    public LogResponseServer(Context context) {
        this.mContext = context;
        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        mainActivity = SingletonObject.getInstance(mContext).getMainActivity();
        domain = SingletonObject.getInstance(mContext).getDomain();
        mGson = SingletonObject.getInstance(mContext).getGSon();
    }

    /**
     * Singleton construct design pattern.
     *
     * @param context parent context
     * @return single instance of LogResponseServer
     */
    public static synchronized LogResponseServer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LogResponseServer(context);
        }
        return mInstance;
    }

    public void responseLog(String message){
        String imei = SingletonObject.getInstance(mContext).getImei();
        LogResponseModel logResponseModel = new LogResponseModel(imei, message, StringUtils.currentDatetimeSQLiteformat());
        doResponse(logResponseModel);
    }

    public void responseVersion(LogResponseModel logModel) {
        String jsonObject = mGson.toJson(logModel, LogResponseModel.class);
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = domain + "/api/v1/version";

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, url, jObject,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    Log.e(TAG, cause);
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

    private void doResponse(LogResponseModel logModel) {
        String jsonObject = mGson.toJson(logModel, LogResponseModel.class);
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = domain + "/api/v1/log";

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, url, jObject,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("TimeoutError")) {
                        showToast(mContext.getString(R.string.msg_timeout_error));
                        return;
                    }

                    if (cause.contains("com.android.volley.NoConnectionError")) {
                        showToast(mContext.getString(R.string.msg_server_error));
                        return;
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
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

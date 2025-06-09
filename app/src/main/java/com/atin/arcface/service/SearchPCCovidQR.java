package com.atin.arcface.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.model.PCCovidSearch;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchPCCovidQR {
    private static final String TAG = "SearchVaccineQRcodeService";
    private Context mContext;
    private String domain;
    private Gson mGson;
    private AuthenticateServer authenticateServer;
    private RegisterAndRecognizeDualActivity mainActivity;
    private static SearchPCCovidQR mInstance;

    public SearchPCCovidQR(Context context) {
        this.mContext = context;
        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
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
    public static synchronized SearchPCCovidQR getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SearchPCCovidQR(context);
        }
        return mInstance;
    }

    public void getPCCovidInformation(PCCovidSearch pcCovidSearch, String token) {
        String jsonObject = mGson.toJson(pcCovidSearch, PCCovidSearch.class);
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = domain + "/api/v1/version";
        url = "https://qrapi.pccovid.gov.vn/api/QRScanHistory/ScanQR";

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, url, jObject,
                response -> {
                    Intent intent = new Intent(Constants.PROCESS_PCCOVID_QRCODE);
                    intent.putExtra("DATA", response.toString());
                    mContext.sendBroadcast(intent);
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("com.android.volley.AuthFailureError")) {
                        authenticateServer.getToken();
                        return;
                    }

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
                headers.put("Accept", "*/*");
                headers.put("Cookie", token);
                //headers.put("Authorization", token);
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

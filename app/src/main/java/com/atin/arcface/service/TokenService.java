package com.atin.arcface.service;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

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
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
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

public class TokenService extends Thread {
    private AuthenticateServer authenticateServer;
    private Context mContext;
    private static final long DELAY = 5*60*1000; // Thời gian delay giữa các lần chạy
    private static Logger logger = Log4jHelper.getLogger(TokenService.class.getSimpleName());

    public TokenService(Context context){
        this.mContext = context;
        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Log.e(TokenService.class.getSimpleName(), e.getMessage());
        }

        while (true){
            try{
                authenticateServer.getToken();
                Thread.sleep(DELAY);
            }catch (Exception ex){
                logger.error("Error getToken", ex);
            }
        }
    }
}

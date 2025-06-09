package com.atin.arcface.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.android.volley.toolbox.Volley;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.EventDB;
import com.atin.arcface.model.LogResponseModel;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.model.ServerResponseMessageModel;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

//Class xử lý post log event giao dịch
public class SyncLogServer {
    private String TAG = "SyncLogServer";
    private Context mContext;
    private Database database;
    private SharedPreferences pref;
    private String domain, imei;
    private Logger logger;
    private MachineDB thisDevice = null;
    private AuthenticateServer authenticateServer;
    private ExecutorService excutor;
    private LinkedBlockingQueue<Runnable> blockingQueue;

    public SyncLogServer(Context context) {
        this.mContext = context;
        database = Application.getInstance().getDatabase();

        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        domain = SingletonObject.getInstance(mContext).getDomain();
        imei = SingletonObject.getInstance(mContext).getImei();
        initValue();
    }

    private void initValue(){
        logger = Log4jHelper.getLogger( TAG );
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
                logger.error(e.getMessage());
            } finally {
                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doSynchronizeLogRealTime(EventDB event) {
        String url = domain + "/api/v1/event/";

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

        excutor.execute(() -> {
            try {
                event.setCompId(thisDevice.getCompId());
                sendLogRealtime(url, event);
            } catch (Exception ex) {
                logger.error("Error excutor send log realtime: " + ex.getMessage());
            }
        });
    }

    private void synchronizeLogRecord() {
        String url = domain + "/api/v1/event/";

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

        List<EventDB> lsLogNotSync = database.getAllEventNeedSync();
        for (EventDB eventDBModel :lsLogNotSync ) {
            String currentTime = StringUtils.currentDatetimeSQLiteformat();
            String networkErrorTime = ConfigUtil.getNetworkErrorTime(mContext);
            long alongError = 0;
            try {
                alongError = StringUtils.getBetweenSecond(networkErrorTime, currentTime);
            } catch (Exception ex) {
                alongError = 0;
            }

            if(alongError < 30*1000){
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            excutor.execute(() -> {
                try {
                    eventDBModel.setCompId(thisDevice.getCompId());
                    sendLog(url, eventDBModel, true);
                } catch (Exception ex) {
                    logger.error("Error excutor send event: " + ex.getMessage());
                }
            });
        }
    }

    private void sendLog(String url, EventDB eventModel, boolean deleteEventAfterSend) throws Exception {
        try{
            String facePath = eventModel.getFacePath();
            File fileImage = new File(facePath);
            if(fileImage.exists()){
                Bitmap bm = BitmapFactory.decodeFile(facePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                String fileBase64 = Base64.encodeToString(b, Base64.NO_WRAP);
                eventModel.setFace64(fileBase64);
            }
        }catch (Exception ex){
            logger.error("Error convertImage: " + ex.getMessage());
        }

        doSend(url, eventModel, deleteEventAfterSend);
    }

    private void doSend(String url, EventDB eventDBModel, boolean deleteEventAfterSend) throws Exception {
        String jsonObject = SingletonObject.getInstance(mContext).getGSon().toJson(eventDBModel, EventDB.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    try{
                        Type eventType = new TypeToken<ServerResponseMessageModel<EventDB>>(){}.getType();
                        ServerResponseMessageModel<EventDB> eventResponse = SingletonObject.getInstance(mContext).getGSon().fromJson(response.toString(), eventType);
                        String errorCode = "";
                        if(eventResponse != null){
                            errorCode = eventResponse.getError();
                        }
                        logger.info("-> Send daily success: " + eventDBModel.getEventId() + (errorCode != null ? " - MSG: " + errorCode : ""));
                        if(deleteEventAfterSend){
                            database.updateEventStatus (eventDBModel, Constants.EVENT_STATUS_SYNCED);
                            File fileImageLog = new File(eventDBModel.getFacePath());
                            if(fileImageLog.exists()){
                                fileImageLog.delete();
                            }
                        }
                    }catch (Exception ex){
                        logger.error("Error doSend event " + ex.getMessage());
                    }
                },
                error -> {
                    ConfigUtil.setNetworkErrorTime(mContext, StringUtils.currentDatetimeSQLiteformat());

                    String cause = StringUtils.getThrowCause(error);
                    if(cause.contains("javax.net.ssl.SSLHandshakeException")){
                        BaseUtil.handleSSLHandshake();
                        return;
                    }
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
        logger.info("Send event daily: " + eventDBModel.getEventId());
    }

    private void sendLogRealtime(String url, EventDB eventModel) throws Exception {
        try{
            String facePath = eventModel.getFacePath();
            File fileImage = new File(facePath);
            if(fileImage.exists()){
                Bitmap bm = BitmapFactory.decodeFile(facePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                String fileBase64 = Base64.encodeToString(b, Base64.NO_WRAP);
                eventModel.setFace64(fileBase64);
            }
        }catch (Exception ex){
            logger.error("Error convertImage: " + ex.getMessage());
        }

        doSendRealtime(url, eventModel);
    }

    private void doSendRealtime(String url, EventDB eventDBModel) throws Exception {
        String jsonObject = SingletonObject.getInstance(mContext).getGSon().toJson(eventDBModel, EventDB.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    Type eventType = new TypeToken<ServerResponseMessageModel<EventDB>>(){}.getType();
                    ServerResponseMessageModel<EventDB> eventResponse = SingletonObject.getInstance(mContext).getGSon().fromJson(response.toString(), eventType);
                    String errorCode = "";
                    if(eventResponse != null){
                        errorCode = eventResponse.getError();
                    }
                    logger.info("-> Send realtime success: " + eventDBModel.getEventId() + (errorCode != null ? " - MSG: " + errorCode : ""));
                },
                error -> {
                    ConfigUtil.setNetworkErrorTime(mContext, StringUtils.currentDatetimeSQLiteformat());

                    String cause = StringUtils.getThrowCause(error);
                    logger.error("-> Error: " + cause);
                    if(cause.contains("javax.net.ssl.SSLHandshakeException")){
                        BaseUtil.handleSSLHandshake();
                        return;
                    }
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
        logger.info("Send event realtime: " + eventDBModel.getEventId());
    }
}

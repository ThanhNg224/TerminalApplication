package com.atin.arcface.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.util.BaseUtil;
import com.google.gson.Gson;
import com.innohi.YNHAPI;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class SingletonObject {
    public Context mContext;
    private static SingletonObject mInstance;
    private RegisterAndRecognizeDualActivity mainActivity;
    private AuthenticateServer authenticateServer;
    private SharedPreferences sharedPreferences;
    private String domain;
    private String imei;
    private Gson mGSon;
    public String ROOT_PATH;
    public String DOWNLOAD_PATH;
    private YNHAPI mYNHAPI;

    public SingletonObject(Context context) {
        this.mContext = context;
    }

    public void init(){
        authenticateServer = new AuthenticateServer(mContext);
        sharedPreferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);

        mGSon = new Gson();

        switch (Build.MODEL){
            case MachineName.RAKINDA_F6:
                mYNHAPI = YNHAPI.getInstance();
                break;

            default:
                break;
        }

        String prefServerDomain = sharedPreferences.getString( Constants.PREF_BUSINESS_SERVER_HOST, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getServerUrlApi());
        String httpPrefix = "";
        if(prefServerDomain.contains(Constants.HTTP) || prefServerDomain.contains(Constants.HTTPS)){
            httpPrefix = "";
        }else{
            httpPrefix = Constants.HTTPS;
            if(BaseUtil.regexIpv4(prefServerDomain)){
                httpPrefix = Constants.HTTP;
            }
        }
        domain = httpPrefix + prefServerDomain;
        imei = BaseUtil.getImeiNumber(mContext);

        if (ROOT_PATH == null) {
            ROOT_PATH = mContext.getFilesDir().getAbsolutePath();
        }

        if (DOWNLOAD_PATH == null){
            DOWNLOAD_PATH = ROOT_PATH + File.separator + "download";
        }

        File downloadFolder = new File(DOWNLOAD_PATH);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }
    }

    /**
     * Singleton construct design pattern.
     *
     * @param context parent context
     * @return single instance of LogResponseServer
     */
    public static synchronized SingletonObject getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonObject(context);
        }
        return mInstance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public RegisterAndRecognizeDualActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(RegisterAndRecognizeDualActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public AuthenticateServer getAuthenticateServer() {
        return authenticateServer;
    }

    public void setAuthenticateServer(AuthenticateServer authenticateServer) {
        this.authenticateServer = authenticateServer;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Gson getGSon() {
        return mGSon;
    }

    public YNHAPI getmYNHAPI() {
        return mYNHAPI;
    }

    public void setmYNHAPI(YNHAPI mYNHAPI) {
        this.mYNHAPI = mYNHAPI;
    }
}

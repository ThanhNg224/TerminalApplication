package com.atin.arcface.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.BuildConfig;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.KhachHangUtils;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.FirmwareModel;
import com.atin.arcface.model.LogResponseModel;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UpdateFirmware {
    private static final String TAG = "UpdateFirmware";
    private Context mContext;
    private ExecutorService excutor;
    private SharedPreferences pref;
    private LinkedBlockingQueue<Runnable> blockingQueue;
    private String domain;
    public static String ROOT_PATH;
    public static String DOWNLOAD_PATH;
    private AlertDialog newUpdateDialog;
    private FirmwareModel mFirmwareModel = null;
    private int firmwareNumber = 0;
    private File fileDownload = null;
    private AuthenticateServer authenticateServer;
    private RegisterAndRecognizeDualActivity mainActivity;
    private Gson gson;

    public UpdateFirmware(Context context) {
        this.mContext = context;
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        initValue();
        initDialog();
    }

    private void initValue(){
        blockingQueue = new LinkedBlockingQueue<>(2);
        excutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, blockingQueue);
        firmwareNumber = ConfigUtil.getFirmwareNumber(mContext);

        authenticateServer = SingletonObject.getInstance(mContext).getAuthenticateServer();
        mainActivity = SingletonObject.getInstance(mContext).getMainActivity();
        domain = SingletonObject.getInstance(mContext).getDomain();
        ROOT_PATH = SingletonObject.getInstance(mContext).ROOT_PATH;
        DOWNLOAD_PATH = SingletonObject.getInstance(mContext).DOWNLOAD_PATH;
        gson = SingletonObject.getInstance(mContext).getGSon();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton(mContext.getString(R.string.label_update), (dialog, id) -> notificateInstall());
        builder.setNegativeButton(mContext.getString(R.string.label_cancel), (dialog, which) -> newUpdateDialog.dismiss());

        mainActivity.runOnUiThread(() -> {
            newUpdateDialog = builder.create();
            newUpdateDialog.setCanceledOnTouchOutside(false);
        });
    }

    public void checkNewFirmware() {
        String url = domain + "/api/v1/firmware/";

        while(true){
            try {
                if(BaseUtil.isNetworkConnected(mContext)){
                    updateFirmware(url);
                }
            } catch (Exception e) {
                Log.e("UpdateFirmware", e.getMessage());
            } finally {
                try {
                    TimeUnit.MINUTES.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateFirmware(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
            response -> {
                excutor.execute(() -> {
                    try {
                        mFirmwareModel = gson.fromJson(response.toString(), FirmwareModel.class);
                        int currentFirmwareNumber= ConfigUtil.getFirmwareNumber(mContext);
                        int newFirmwareNumber = mFirmwareModel.getFirmwareNumber();

                        if(newFirmwareNumber > currentFirmwareNumber){
                            fileDownload = new File(DOWNLOAD_PATH + File.separator +  mFirmwareModel.getFileName());
                            if(!fileDownload.exists()){
                                File downloadFolder = new File(DOWNLOAD_PATH);
                                if (downloadFolder.exists()){
                                    for(File file: downloadFolder.listFiles())
                                        if (!file.isDirectory()) {
                                            BaseUtil.deleteFile(file);
                                        }
                                }
                                downloadFirmware();
                            }else{
                                checkFileAndUpdate();
                            }
                        }else{
                            LogResponseModel logResponseModel = new LogResponseModel(BaseUtil.getImeiNumber(mContext), BuildConfig.VERSION_NAME, StringUtils.currentDatetimeSQLiteformat(), "" + firmwareNumber);
                            LogResponseServer.getInstance(mContext).responseVersion(logResponseModel);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
            },
            error -> {
                String cause = StringUtils.getThrowCause(error);
                Log.e(TAG, cause);
            }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void downloadFirmware(){
        DownloadFileAsync downloadFileAsync = new DownloadFileAsync();
        downloadFileAsync.execute(mFirmwareModel.getFirmwareUrl());
    }

    private void checkFileAndUpdate(){
        boolean md5IsValid = BaseUtil.checkMD5(TAG, mFirmwareModel.getMd5(), fileDownload);
        if(!md5IsValid){
            File downloadFolder = new File(DOWNLOAD_PATH);
            if (downloadFolder.exists()){
                for(File file: downloadFolder.listFiles())
                    if (!file.isDirectory()) {
                        BaseUtil.deleteFile(file);
                    }
            }
            downloadFirmware();
        }else{
            try {
                LogResponseServer.getInstance(mContext).responseLog(mContext.getResources().getString(R.string.message_notification_update_firmware));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            notificateUpdate();
        }
    }

    private void notificateUpdate(){
        ((RegisterAndRecognizeDualActivity)mContext).runOnUiThread(new Runnable() {
            public void run() {
                newUpdateDialog.setTitle(String.format(mContext.getString(R.string.message_update_firmware), mFirmwareModel.getFirmwareVersion()));
                newUpdateDialog.setMessage(mFirmwareModel.getFix());
                newUpdateDialog.show();
            }
        });
    }

    private void notificateInstall(){
        File fileDownload = new File(DOWNLOAD_PATH + File.separator +  mFirmwareModel.getFileName());
        Uri fileUri = null;
        try{
            fileUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID  + ".provider", fileDownload);
        }
        catch (Exception ex){
            Log.e("getUriForFile", ex.getMessage());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            try {
                LogResponseServer.getInstance(mContext).responseLog(mContext.getResources().getString(R.string.message_start_download_firmware));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String...aurl) {
            HttpURLConnection connection = null;
            InputStream input = null;
            FileOutputStream out = null;
            int count = 0;

            try{
                URL url = new URL(aurl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                input = connection.getInputStream();
                out = new FileOutputStream(fileDownload);

                long total = 0;
                byte[] buffer = new byte[1024];
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    publishProgress("" + (int)((total*100)/lenghtOfFile));
                    out.write(buffer, 0, count);
                }
                out.flush();
            }catch(IOException e){
                Log.e(TAG, e.getMessage());
                return "ERROR";
            } finally {
                if(out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(input != null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(connection != null){
                    connection.disconnect();
                }
            }
            return null;
        }

        protected void onProgressUpdate(String...progress) {
            Log.d(TAG,progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
            try {
                LogResponseServer.getInstance(mContext).responseLog(mContext.getResources().getString(R.string.message_donwload_firmware_success));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if(unused != null && unused.equals("ERROR")){
                return;
            }
            checkFileAndUpdate();
        }
    }
}

package com.atin.arcface.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arcsoft.face.BuildConfig;
import com.atin.arcface.R;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.AuthenticateServer;
import com.atin.arcface.model.FirmwareModel;
import com.atin.arcface.model.LogResponseModel;
import com.atin.arcface.model.ServerResponseMessageModel;
import com.atin.arcface.service.LogResponseServer;
import com.atin.arcface.service.SingletonObject;
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

public class UpdateSystemActivity extends BaseActivity {
    private static final String TAG = "UpdateSystemActivity";
    private ProgressDialog mProgressDialog;
    private AlertDialog newUpdateDialog;
    private TextView txtNotification;

    private FirmwareModel mFirmwareModel = null;
    private AuthenticateServer authenticateServer;
    private SharedPreferences pref;

    public static String ROOT_PATH;
    public static String DOWNLOAD_PATH;
    private String firmwareUrl;
    private File fileDownload = null;
    private String domain, imei;
    private Gson gson;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_system);
        txtNotification = findViewById(R.id.txtNotification);
        authenticateServer = new AuthenticateServer(this);

        pref = SingletonObject.getInstance(this).getSharedPreferences();
        domain = SingletonObject.getInstance(this).getDomain();
        imei = SingletonObject.getInstance(this).getImei();
        gson = SingletonObject.getInstance(this).getGSon();

        initDialog();
        initValue();
    }

    private void initValue(){
        if (ROOT_PATH == null) {
            ROOT_PATH = getFilesDir().getAbsolutePath();
        }

        if (DOWNLOAD_PATH == null){
            DOWNLOAD_PATH = ROOT_PATH + File.separator + "download";
        }

        File downloadFolder = new File(DOWNLOAD_PATH);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }

        firmwareUrl = domain + "/api/v1/firmware-signed/";
        if(BaseUtil.isNetworkConnected(this)){
            checkUpdate(firmwareUrl);
        }
    }


    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getString(R.string.label_update), (dialog, id) -> notificateInstall());
        builder.setNegativeButton(getString(R.string.label_cancel), (dialog, which) -> newUpdateDialog.dismiss());
        newUpdateDialog = builder.create();
        newUpdateDialog.setCanceledOnTouchOutside(false);
    }

    private void checkUpdate(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                response -> {
                    runOnUiThread(() -> {
                        try {
                            mFirmwareModel = gson.fromJson(response.toString(), FirmwareModel.class);
                            int currentFirmwareNumber= ConfigUtil.getFirmwareNumber(UpdateSystemActivity.this);
                            int newFirmwareNumber = mFirmwareModel.getFirmwareNumber();

                            if(newFirmwareNumber >= currentFirmwareNumber){
                                txtNotification.setText(String.format(getResources().getString(R.string.message_firmware_status), mFirmwareModel.getFileName()));
                            }else{
                                txtNotification.setText(getResources().getString(R.string.message_latest_version));
                            }
                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    });
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("com.android.volley.NoConnectionError") || cause.contains("com.android.volley.ServerError")) {
                        showToast(getString(R.string.msg_server_error));
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
        VolleySingleton.getInstance(UpdateSystemActivity.this).addToRequestQueue(request);
    }

    public void downloadNewVersion(View view){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, firmwareUrl, new JSONObject(),
                response -> {
                    try {
                        mFirmwareModel = gson.fromJson(response.toString(), FirmwareModel.class);
                        int currentFirmwareNumber= ConfigUtil.getFirmwareNumber(this);
                        int newFirmwareNumber = mFirmwareModel.getFirmwareNumber();

                        if(newFirmwareNumber >= currentFirmwareNumber){ //Cho phép cài đè lại cả bản đang chạy
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
                        }
                    } catch (Exception e) {
                        showToast(e.getMessage());
                    }
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("com.android.volley.NoConnectionError") || cause.contains("com.android.volley.ServerError")) {
                        showToast(getString(R.string.msg_server_error));
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
        VolleySingleton.getInstance(UpdateSystemActivity.this).addToRequestQueue(request);
    }

    private void downloadFirmware(){
        if(BaseUtil.isNetworkConnected(this)){
            LogResponseServer.getInstance(UpdateSystemActivity.this).responseLog(getResources().getString(R.string.message_start_download_firmware));
            DownloadFileAsync downloadFileAsync = new DownloadFileAsync();
            downloadFileAsync.execute(mFirmwareModel.getFirmwareUrl());
            LogResponseServer.getInstance(UpdateSystemActivity.this).responseLog(getResources().getString(R.string.message_donwload_firmware_success));
        }
    }

    private void checkFileAndUpdate(){
        boolean md5IsValid = BaseUtil.checkMD5(TAG, mFirmwareModel.getMd5(), fileDownload);
        if(!md5IsValid){
            showToast(getResources().getString(R.string.message_md5_not_correct_try_download));
            LogResponseServer.getInstance(UpdateSystemActivity.this).responseLog(getResources().getString(R.string.message_md5_not_correct_try_download));

            File downloadFolder = new File(DOWNLOAD_PATH);
            if (downloadFolder.exists()){
                for(File file: downloadFolder.listFiles()){
                    if (!file.isDirectory()) {
                        BaseUtil.deleteFile(file);
                    }
                }
            }
        }else{
            LogResponseServer.getInstance(UpdateSystemActivity.this).responseLog(getResources().getString(R.string.message_notification_update_firmware));
            notificateUpdate();
        }
    }

    private void notificateUpdate(){
        runOnUiThread(() -> {
            newUpdateDialog.setTitle(String.format(getString(R.string.message_update_firmware), mFirmwareModel.getFirmwareVersion()));
            newUpdateDialog.setMessage(mFirmwareModel.getFix());
            newUpdateDialog.show();
        });
    }

    private void notificateInstall(){
        File fileDownload = new File(DOWNLOAD_PATH + File.separator +  mFirmwareModel.getFileName());
        Uri fileUri = null;
        try{
            fileUri = FileProvider.getUriForFile(this, com.atin.arcface.BuildConfig.APPLICATION_ID  + ".provider", fileDownload);
        }
        catch (Exception ex){
            showToast("Error notificateInstall " + ex.getMessage());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    public void onBack(View view){
        onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> showDialog(DIALOG_DOWNLOAD_PROGRESS));
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
                runOnUiThread(() -> showToast(e.getMessage()));
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
            return "";
        }

        protected void onProgressUpdate(String...progress) {
            Log.d(TAG,progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            runOnUiThread(() -> {
                dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
                checkFileAndUpdate();
            });
        }
    }

    //our progress bar settings
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    public void requestSyncData(View view){
        String url = domain + "/api/v1/request-synchronize/" + imei;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                response -> {
                    try {
                        ServerResponseMessageModel<String> reponseMsg = gson.fromJson(response.toString(), ServerResponseMessageModel.class);
                        showToast(reponseMsg.getData());
                    } catch (Exception e) {
                        showToast(e.getMessage());
                    }
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if(cause.contains("javax.net.ssl.SSLHandshakeException")){
                        BaseUtil.handleSSLHandshake();
                        return;
                    }

                    showToast(getString(R.string.message_request_synchronize_failure));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}

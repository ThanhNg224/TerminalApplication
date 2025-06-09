package com.atin.arcface.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.model.ActiveDeviceInfo;

import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.model.ActiveViewModel;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.Log4jHelper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ActiveByInputKeyActivity extends BaseActivity {
    private SharedPreferences pref;
    private ActiveViewModel activeViewModel;
    private EditText edtActiveKey;
    private TextView edtMAC;
    private TextView edtIMEI;
    private TextView edtVersion;
    private View viewLoadingAnimation;
    private FaceEngine faceEngine = new FaceEngine();
    private static final int ACTION_REQUEST_PERMISSIONS = 2;
    private boolean blActiveOffile = false;

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_by_input_key);
        edtActiveKey = findViewById(R.id.edtActiveKey);
        edtMAC = findViewById(R.id.edtMAC);
        edtIMEI = findViewById(R.id.edtIMEI);
        edtVersion = findViewById(R.id.edtVersion);
        viewLoadingAnimation = findViewById(R.id.loadingAnimation);
        pref = getSharedPreferences("PREF", MODE_PRIVATE);
        logger = Log4jHelper.getLogger("ActiveByInputKeyActivity");

        activeViewModel = new ViewModelProvider(
                getViewModelStore(),
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(ActiveViewModel.class);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        initValue();
        logger.info("SerialNumber: " + BaseUtil.getSerialNumber());

        ActiveDeviceInfo activeDeviceInfo = new ActiveDeviceInfo();
        int resDeviceInfo = faceEngine.getActiveDeviceInfo(ActiveByInputKeyActivity.this, activeDeviceInfo);
        if (resDeviceInfo == ErrorInfo.MOK) {
            logger.info("DeviceInfo:");
            logger.info(activeDeviceInfo.getDeviceInfo());
        }

        activeOffline(); //Try active offline if exits file active

        activeViewModel.getActiveResult().observe(this, result -> {
            ConfigUtil.commitAppId(getApplicationContext(), BaseUtil.getAppId(Build.MODEL));
            ConfigUtil.commitSdkKey(getApplicationContext(), BaseUtil.getSdkKey(Build.MODEL));
            ConfigUtil.commitActiveKey(getApplicationContext(), edtActiveKey.getText().toString());

            ActiveFileInfo activeFileInfo = new ActiveFileInfo();
            int res = faceEngine.getActiveFileInfo(ActiveByInputKeyActivity.this, activeFileInfo);
            if (res == ErrorInfo.MOK) {
                pref.edit().putBoolean(Constants.PREF_ACTIVE_ALREADY, true).apply();
                if (!blActiveOffile) {
                    storeActiveFile();
                }

                logger.info("ActiveInfo:");
                logger.info(activeFileInfo.toString());

                showLoadingAnimation(false);
                startActivities();
            } else {
                showLoadingAnimation(false);
                showToast("Active error: " + res);
            }
        });
    }

    private void showLoadingAnimation(boolean visible) {
        viewLoadingAnimation.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                startActivities();
            }
        }
    }

    private void initValue() {
        //Mac Address
        edtMAC.setText(BaseUtil.getMacAddress());

        //Imei Address
        edtIMEI.setText(BaseUtil.getImeiNumber(this));

        //Version
        String versionName = com.atin.arcface.BuildConfig.VERSION_NAME;
        edtVersion.setText(versionName);

        //Fix KEY
        String key = getKey(BaseUtil.getImeiNumber(this));
        edtActiveKey.setText(key);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onActiveKey(View view) {
        activeOnline(view);
    }

    public void activeOnline(View view) {
        if (checkPermissions(NEEDED_PERMISSIONS)) {
            showLoadingAnimation(true);
            blActiveOffile = false;
            String activeKey = activeViewModel.formatActiveKey(edtActiveKey.getText().toString());
            runOnSubThread(() -> activeViewModel.activeOnline(getApplicationContext(), activeKey, BaseUtil.getAppId(Build.MODEL), BaseUtil.getSdkKey(Build.MODEL)));
        } else {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
    }

    public void activeOffline() {
        String activeFilePath = Environment.getExternalStorageDirectory() + File.separator + "Systems" + File.separator + "ArcFacePro32.dat";
        File activeFile = new File(activeFilePath);
        if (activeFile.exists()) {
            if (checkPermissions(NEEDED_PERMISSIONS)) {
                showLoadingAnimation(true);
                blActiveOffile = true;
                runOnSubThread(() -> {
                    activeViewModel.activeOffline(getApplicationContext(), activeFilePath);
                    ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                    int res = faceEngine.getActiveFileInfo(ActiveByInputKeyActivity.this, activeFileInfo);
                    if (res == ErrorInfo.MOK) {
                        logger.info("ActiveInfo:");
                        logger.info(activeFileInfo.toString());
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            }
        }
    }

    private void storeActiveFile() {
        try {
            String srcFilePath = getFilesDir().getAbsolutePath() + File.separator + "ArcFacePro32.dat";
            String desActivePath = Environment.getExternalStorageDirectory() + File.separator + "Systems";

            File desActivePathFile = new File(desActivePath);
            if (!desActivePathFile.exists()) {
                desActivePathFile.mkdirs();
            }

            File srcFile = new File(srcFilePath);
            if (srcFile.exists()) {
                FileUtils.copyFileToDirectory(srcFile, desActivePathFile);
            }
        } catch (IOException e) {
            Log.e("ActiveByInputKeyActivity", "Error storeActiveFile " + e.getMessage());
        }
    }

    public void startActivities() {
        startActivity(new Intent(this, RegisterAndRecognizeDualActivity.class));
    }

    //Fix tạm key theo imei để phụ vụ việc giúp khách hàng cài lại máy do mất signkey
    private String getKey(String imei) {
        String key = "";
        switch (imei) {
            case "28270d9663f7f454":
                key = "0858-1164-Z9LR-8AKE";
                break;

            case "867584036284714":
                key = "0858-116Q-4397-WDBK";
                break;

            case "26656936db6cf1dd":
                key = "0858-116Q-43AF-4DVL";
                break;

            case "2924df4d419bb57e":
                key = "0858-116Q-43KQ-E9M5";
                break;

            case "bb92b9d1a791770a": //AT
                key = "0858-116Q-43RC-RC2T";
                break;

            case "6f9e732faa6d3c68": //AT
                key = "0858-116Q-43CY-L6XH";
                break;

            case "45c0f53ab55a8791":
                key = "0858-116Q-43L3-YZQV";
                break;

            case "4721d618e8826d48":
                key = "0858-116Q-43PM-UYZL";
                break;

            case "cc63b92ccdb87d47":
                key = "0858-116Q-43MG-FU96";
                break;

            case "37e668e18b1cac85":
                key = "0858-116Q-43NQ-12UT";
                break;

            case "eff13ee3b71d55c1":
                key = "0858-116Q-43QQ-TVJ6";
                break;

            case "2c26e9f7a9dd7b77":
                key = "0858-116Q-43TP-EXGU";
                break;

            case "482c25915fcabf5c":
                key = "0858-116Q-455B-FF64";
                break;

            case "b74e9bfd5a23e976":
                key = "0858-116Q-457A-L3VL";
                break;

            case "9815100f4bc9d885": //AT
                key = "0858-116Q-45BM-QVAE";
                break;

            case "2e98f537e3436826":
                key = "0858-116Q-45C6-GQP1";
                break;

            case "2222715219ada694":
                key = "0858-116Q-45DX-3H4K";
                break;

            case "55978069f84c5a6":
                key = "0858-116Q-45EM-RA5U";
                break;

            default:
                break;
        }
        return key;
    }

    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        initValue();
    }
}

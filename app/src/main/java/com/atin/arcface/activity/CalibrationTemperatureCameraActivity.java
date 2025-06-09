package com.atin.arcface.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.common.thermalimage.CalibrationCallBack;
import com.common.thermalimage.HotImageCallback;
import com.common.thermalimage.TemperatureBitmapData;
import com.common.thermalimage.TemperatureData;
import com.common.thermalimage.ThermalImageUtil;

import java.util.List;

public class CalibrationTemperatureCameraActivity extends AppCompatActivity {
    ThermalImageUtil temperatureUtil;
    TemperatureData temperatureData;
    TextView tiptv;
    ImageView image;
    TextView txtNhietDo;
    EditText et_distance;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_temperature_camera);
        temperatureUtil = new ThermalImageUtil(this);
        tiptv = findViewById(R.id.tip);
        image = findViewById(R.id.image);
        txtNhietDo = findViewById(R.id.txtNhietDo);

        et_distance = findViewById(R.id.et_distance);
        et_distance.setText("50");

        pref = getSharedPreferences("PREF", MODE_PRIVATE);

        if(!isInstalled(CalibrationTemperatureCameraActivity.this,"com.telpo.temperatureservice")){
            showTip("not install TempatureServices.apk");
        }else{
            AppInstallReceiver apkInstallListener = new AppInstallReceiver();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            registerReceiver(apkInstallListener, intentFilter);

            boolean blCalibrationCameraStatus = pref.getBoolean(Constants.PREF_CALIBRATION_CAMERA_STATUS, false);
            if(blCalibrationCameraStatus){
                //displayTemperateCamera();
            }
        }
    }

    private void showTip(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tiptv.setText(msg);
            }
        });
    }

    public void onCalibration(View view){
        temperatureUtil.calibrationTem_DAT(new CalibrationCallBack.Stub() {
            @Override
            public void onCalibrating() {
                showTip("Hiệu chỉnh...");
            }

            @Override
            public void onSuccess() {
                showTip("Hiệu chỉnh thành công");
                pref.edit().putBoolean(Constants.PREF_CALIBRATION_CAMERA_STATUS, true).apply();
            }

            @Override
            public void onFail(final String errmsg) {
                showTip("Hiệu chỉnh thất bại! " + errmsg);
                pref.edit().putBoolean(Constants.PREF_CALIBRATION_CAMERA_STATUS, false).apply();
            }
        });
    }

    private boolean isInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public class AppInstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if(packageName.equals("com.telpo.temperatureservice")){
                    temperatureUtil = new ThermalImageUtil(CalibrationTemperatureCameraActivity.this);
                }
            }
        }
    }

    public void displayTemperateCamera(){
        float distance=50;
        final float distances = distance;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    temperatureData = temperatureUtil.getDataAndBitmap(distances, true, new HotImageCallback.Stub() {
                        @Override
                        public void onTemperatureFail(String e) {}

                        @Override
                        public void getTemperatureBimapData(final TemperatureBitmapData data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    image.setImageBitmap(data.getBitmap());
                                    txtNhietDo.setText(temperatureData.getTemperature() + " ℃");
                                }
                            });
                        }

                    });
                }
            }
        }).start();
    }
}
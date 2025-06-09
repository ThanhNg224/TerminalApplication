package com.atin.arcface.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.atin.arcface.BuildConfig;
import com.atin.arcface.R;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.databinding.ActivitySystemSettingBinding;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.DoorType;
import com.atin.arcface.model.Language;
import com.atin.arcface.model.LivenessLevel;
import com.atin.arcface.model.MachineFunction;
import com.atin.arcface.service.AdminReceiver;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.AlarmManagerUtil;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.DoorTypeUtils;
import com.atin.arcface.util.LanguageUtils;
import com.atin.arcface.util.LivenessLevelUtils;
import com.atin.arcface.util.MachineFunctionUtils;
import com.atin.arcface.widget.DoorTypeAdapter;
import com.atin.arcface.widget.LanguageAdapter;
import com.atin.arcface.widget.LivenessLevelAdapter;
import com.atin.arcface.widget.MachineFunctionAdapter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import androidx.databinding.DataBindingUtil;

public class SystemSettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int ACTIVATION_REQUEST = 47; // identifies our request id
    private static final int IMAGE_PICK = 1;
    private SharedPreferences pref;
    private EditText edtCompanyName, edtDelayRecognize, edtThreshold, edtTempThreshold, edtServerHost,
            edtUsernameAuth, edtPasswordAuth, edtDistanceDetect, edtNomaskQualityThreshold,
            edtMaskQualityThreshold, edtRegisterQualityThreshold, edtDelayDoorTime;
    private TextView edtIPAddress, edtMAC, edtIMEI, edtVersion;
    private Switch switchLivenessDetect, switchTurnLed, switchTurnSound;
    private Switch switchAutoStart, switchUseBusinessCheck, switchCheckMask, switchCheckTemperature,
            switchSavePersonUnreg, switchShowPersonCode, switchAutoReboot, switchNoDelay,
            switchAutoSleep, switchShowTemperature, switchHideNavigator, switchHideStatus;
    private TimePicker timePicker;
    private ImageView imgLogo;
    private Toast toast = null;
    private Dialog powerDialog;
    private Intent startHomeScreen;
    private Spinner spnDeviceFunction, spnLanguage, spnLivenessLevel, spnDoorType;
    private MachineFunctionAdapter machineFunctionAdapter;
    private LivenessLevelAdapter livenessLevelAdapter;
    private LanguageAdapter languageAdapter;
    private DoorTypeAdapter doorTypeAdapter;
    private String EVENT_PATH, FACE_PATH, UPLOAD_PATH;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdmin;

    private ActivitySystemSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_system_setting);

        //setContentView(R.layout.activity_system_setting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pref = getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);

        initView();
        initListener();
        initValue();
        LanguageUtils.loadLocale();
        updateViewByLanguage();
        initSleepDialog();
    }

    private void initView(){
        edtCompanyName = findViewById(R.id.edtCompanyName);
        edtIPAddress = findViewById(R.id.edtIPAddress);
        edtMAC = findViewById(R.id.edtMAC);
        edtIMEI = findViewById(R.id.edtIMEI);
        edtVersion = findViewById(R.id.edtVersion);
        switchLivenessDetect = findViewById(R.id.switchLivenessDetect);
        switchTurnLed = findViewById(R.id.switchTurnLed);
        switchTurnSound = findViewById(R.id.switchTurnSound);
        edtDelayRecognize = findViewById(R.id.edtDelayRecognize);
        edtDelayDoorTime = findViewById(R.id.edtDelayDoorTime);
        switchAutoStart = findViewById(R.id.switchAutoStart);
        switchNoDelay = findViewById(R.id.switchNoDelay);
        edtThreshold = findViewById(R.id.edtThreshold);
        edtTempThreshold = findViewById(R.id.edtTemperatureThreshold);
        edtServerHost = findViewById(R.id.edtServerHost);
        edtUsernameAuth = findViewById(R.id.edtAuthUsername);
        edtPasswordAuth = findViewById(R.id.edtAuthPassword);
        imgLogo = findViewById(R.id.imgLogo);
        switchUseBusinessCheck = findViewById(R.id.switchUseBusinessCheck);
        switchCheckMask = findViewById(R.id.switchCheckMask);
        switchCheckTemperature = findViewById(R.id.switchCheckTemperature);
        switchSavePersonUnreg = findViewById(R.id.switchSavePersonUnreg);
        switchShowPersonCode = findViewById(R.id.switchShowPersonCode);
        spnDeviceFunction = (Spinner) findViewById(R.id.spnDeviceFunction);
        spnLanguage = findViewById(R.id.spnLanguage);
        spnLivenessLevel = findViewById(R.id.spnLivenessLevel);
        spnDoorType = findViewById(R.id.spnDoorType);
        edtDistanceDetect = findViewById(R.id.edtDistanceDetect);
        switchAutoReboot = findViewById(R.id.switchAutoReboot);
        timePicker = (TimePicker) this.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // 24H Mode
        edtNomaskQualityThreshold = findViewById(R.id.edtNomaskQualityThreshold);
        edtMaskQualityThreshold = findViewById(R.id.edtMaskQualityThreshold);
        edtRegisterQualityThreshold = findViewById(R.id.edtRegisterQualityThreshold);
        switchAutoSleep = findViewById(R.id.switchAutoSleep);
        switchShowTemperature = findViewById(R.id.switchShowTemperature);
        switchHideNavigator = findViewById(R.id.switchHideNavigator);
        switchHideStatus = findViewById(R.id.switchHideStatus);

        //Machine function
        machineFunctionAdapter = new MachineFunctionAdapter(this, android.R.layout.simple_spinner_item, MachineFunctionUtils.getListFunction());
        machineFunctionAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnDeviceFunction.setAdapter(machineFunctionAdapter);
        spnDeviceFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                MachineFunction machineFunction = machineFunctionAdapter.getItem(position);
                if (machineFunction.getFunctionValue() != MachineFunctionUtils.getMachineFunction().getFunctionValue()) {
                    MachineFunctionUtils.setMachineFunction(machineFunction);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        //Language
        languageAdapter = new LanguageAdapter(this, android.R.layout.simple_spinner_item, LanguageUtils.getLanguageData());
        languageAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnLanguage.setAdapter(languageAdapter);
        spnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Language language = languageAdapter.getItem(position);
                if (!language.getCode().equals(LanguageUtils.getCurrentLanguage().getCode())) {
                    LanguageUtils.changeLanguage(language);
                    updateViewByLanguage();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        //Liveness level
        livenessLevelAdapter = new LivenessLevelAdapter(this, android.R.layout.simple_spinner_item, LivenessLevelUtils.getListLevel());
        livenessLevelAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnLivenessLevel.setAdapter(livenessLevelAdapter);
        spnLivenessLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LivenessLevel livenessLevel = livenessLevelAdapter.getItem(position);
                if (livenessLevel.getLevelValue() != LivenessLevelUtils.getLivenessLevel().getLevelValue()) {
                    LivenessLevelUtils.setLivenessLevel(livenessLevel);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        //Loại cửa tích hợp
        doorTypeAdapter = new DoorTypeAdapter(this, android.R.layout.simple_spinner_item, DoorTypeUtils.getListDoorType());
        doorTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnDoorType.setAdapter(doorTypeAdapter);
        spnDoorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                DoorType doorType = doorTypeAdapter.getItem(position);
                if (doorType.getValue() != DoorTypeUtils.getDoorType().getValue()) {
                    DoorTypeUtils.setDoorType(doorType);
                }

                if(doorType.getValue() == Application.getInstance().getResources().getInteger(R.integer.door_type_with_controller_value)){
                    edtDelayDoorTime.setVisibility(View.INVISIBLE);
                }else{
                    edtDelayDoorTime.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }

    private void initListener(){
        switchAutoReboot.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
    }

    private void initSleepDialog() {
        View view = getLayoutInflater().inflate(R.layout.power_layout, null);
        powerDialog = new Dialog(this, R.style.Theme_D1NoTitleDim);
        powerDialog.setContentView(view);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(powerDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = screenHeight + 420;
        powerDialog.getWindow().setAttributes(lp);
    }

    public void showOption(View view){
        powerDialog.show();
    }

    public void onDatabase(View view){
        startActivity(new Intent(SystemSettingActivity.this, ListPersonActivity.class));
    }

    public void onEvent(View view){
        startActivity(new Intent(SystemSettingActivity.this, EventActivity.class));
    }

    public void homeScreen (View view){
        if(powerDialog != null && powerDialog.isShowing()){
            powerDialog.hide();
        }

        startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomeScreen);
    }

    public void reboot (View view){
        try {
            Runtime.getRuntime().exec("su");
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            showToast(LanguageUtils.getString(R.string.message_device_not_support));
        }
    }

    public void clearLogEvent (View view){
        if(powerDialog != null && powerDialog.isShowing()){
            powerDialog.hide();
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(SystemSettingActivity.this);
        alert.setTitle(LanguageUtils.getString(R.string.label_clear_event));
        alert.setMessage(LanguageUtils.getString(R.string.message_clear_event));
        alert.setPositiveButton(getString(R.string.button_yes), (dialog, which) -> {
            Database db = Application.getInstance().getDatabase();
            db.clearEventLog();

            if (EVENT_PATH == null){
                EVENT_PATH = getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "event";
            }

            File eventFolder = new File(EVENT_PATH);
            if (eventFolder.exists()){
                for(File file: eventFolder.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }

            dialog.dismiss();
        });

        alert.setNegativeButton(getString(R.string.button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void clearAllData (View view){
        if(powerDialog != null && powerDialog.isShowing()){
            powerDialog.hide();
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(SystemSettingActivity.this);
        alert.setTitle(LanguageUtils.getString(R.string.label_clear_all));
        alert.setMessage(LanguageUtils.getString(R.string.message_clear_all));
        alert.setPositiveButton(getString(R.string.button_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Database db = Application.getInstance().getDatabase();
                db.clearDatabase();

                if (EVENT_PATH == null){
                    EVENT_PATH = getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "event";
                }

                File eventFolder = new File(EVENT_PATH);
                if (eventFolder.exists()){
                    for(File file: eventFolder.listFiles())
                        if (!file.isDirectory())
                            file.delete();
                }

                if (FACE_PATH == null){
                    FACE_PATH = getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "face";
                }

                File faceFolder = new File(FACE_PATH);
                if (faceFolder.exists()){
                    for(File file: faceFolder.listFiles())
                        if (!file.isDirectory())
                            file.delete();
                }

                if(UPLOAD_PATH == null ){
                    UPLOAD_PATH = getFilesDir().getAbsolutePath() + File.separator + "upload";
                }

                File uploadFolder = new File(UPLOAD_PATH);
                if (uploadFolder.exists()){
                    for(File file: uploadFolder.listFiles())
                        if (!file.isDirectory())
                            file.delete();
                }

                pref.edit().putString(Constants.PREF_TOKEN_AUTH, "").apply();
            }
        });

        alert.setNegativeButton(getString(R.string.button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void cancel(View view){
        if(powerDialog != null && powerDialog.isShowing()){
            powerDialog.hide();
        }
    }

    private void initValue(){
        //Company name
        edtCompanyName.setText(pref.getString(Constants.PREF_COMPANY_NAME, "ATInnovation"));

        //IpAddress
        edtIPAddress.setText(BaseUtil.getLocalIpv4());

        //Mac Address
        edtMAC.setText(BaseUtil.getMacAddress());

        //Imei Address
        edtIMEI.setText(BaseUtil.getImeiNumber(this));

        //Version
        String versionName = BuildConfig.VERSION_NAME;
        edtVersion.setText(versionName);

        //Ẩn thanh điều hướng
        switchHideNavigator.setChecked(pref.getBoolean(Constants.PREF_HIDE_NAVIGATION_BAR, false));

        //Ẩn thanh trạng thái
        switchHideStatus.setChecked(pref.getBoolean(Constants.PREF_HIDE_STATUS_BAR, false));

        //Liveness Detect
        switchLivenessDetect.setChecked(pref.getBoolean(Constants.PREF_LIVENESS_DETECT, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isCheckProfing() ));

        //Show Dialog Time (mặc định là 500ms)
        edtDelayRecognize.setText(String.valueOf(pref.getInt(Constants.PREF_DELAY_RECOGNIZE_TIME, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime())));

        //Khoảng cách nhận diện
        edtDistanceDetect.setText(String.valueOf(pref.getInt(Constants.PREF_DISTANCE_DETECT, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDistance())));

        //Auto Start Program
        switchAutoStart.setChecked(pref.getBoolean(Constants.PREF_AUTOMATIC_START, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isAutoStart()));

        //Auto sleep screen
        switchAutoSleep.setChecked(pref.getBoolean(Constants.PREF_AUTOMATIC_SLEEP, false));

        //Hiển thị thông tin nhiệt độ
        switchShowTemperature.setChecked(pref.getBoolean(Constants.PREF_SHOW_BODY_TEMPERATURE, false));

        //Recognize no delay
        switchNoDelay.setChecked(pref.getBoolean(Constants.PREF_NO_DELAY, true));

        //Use led
        switchTurnLed.setChecked(pref.getBoolean(Constants.PREF_USE_LED, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isUseLed()));

        //Use sound
        switchTurnSound.setChecked(pref.getBoolean(Constants.PREF_USE_SOUND, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isUseSound()));

        //Check điều kiện người truy nhập
        switchUseBusinessCheck.setChecked(pref.getBoolean(Constants.PREF_USE_BUSINESS_CHECK, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isCheckAccessControl()));

        //Check khẩu trang
        switchCheckMask.setChecked(pref.getBoolean(Constants.PREF_CHECK_MASK, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isCheckMask()));

        //Check nhiệt độ
        switchCheckTemperature.setChecked(pref.getBoolean(Constants.PREF_CHECK_TEMPERATURE, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isCheckTemperature()));

        //Lưu thông tin người chưa đăng ký
        switchSavePersonUnreg.setChecked(pref.getBoolean(Constants.PREF_SAVE_PERSON_UNREG, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isSaveGuest()));

        //Hiển thị thông tin mã nhân sự/chức danh (tận dụng trên thông tin vaccine tiêm chủng)
        switchShowPersonCode.setChecked(pref.getBoolean(Constants.PREF_SHOW_VACCINE_INFO, true));

        //Threshold compare
        edtThreshold.setText(pref.getString(Constants.PREF_THRESHOLD, "" + CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getFaceThreshold()));

        //Threshold quality no mask
        String strNoMaskQT = pref.getString(Constants.PREF_NOMASK_QUALITY_THRESHOLD, "" + ConfigUtil.getImageQualityNoMaskRecognizeThreshold(SystemSettingActivity.this));
        edtNomaskQualityThreshold.setText(String.valueOf(Float.parseFloat(strNoMaskQT)));

        //Threshold quality mask
        String strMaskQT = pref.getString(Constants.PREF_MASK_QUALITY_THRESHOLD, "" + ConfigUtil.getImageQualityMaskRecognizeThreshold(SystemSettingActivity.this));
        edtMaskQualityThreshold.setText(String.valueOf(Float.parseFloat(strMaskQT)));

        //Threshold quality register
        String strRegisteQT =pref.getString(Constants.PREF_REGISTER_QUALITY_THRESHOLD, "" + ConfigUtil.getImageQualityNoMaskRegisterThreshold(SystemSettingActivity.this));
        edtRegisterQualityThreshold.setText(String.valueOf(Float.parseFloat(strRegisteQT)));

        //Temperature threshold
        edtTempThreshold.setText(pref.getString(Constants.PREF_TEMPERATURE_THRESHOLD, "" + CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getTemperatureThreshold()));

        //Server host
        edtServerHost.setText(pref.getString(Constants.PREF_BUSINESS_SERVER_HOST, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getServerUrlApi()));

        //Username Auth Server
        //edtUsernameAuth.setText(pref.getString(Constants.PREF_USERNAME_AUTH, BaseUtil.getImeiNumber(SystemSettingActivity.this)));
        edtUsernameAuth.setText(BaseUtil.getImeiNumber(SystemSettingActivity.this));

        //Password Auth Server
        edtPasswordAuth.setText(pref.getString(Constants.PREF_PASSWORD_AUTH, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getPasswordApi()));

        //Logo path
        String uriLogo = pref.getString(Constants.PREF_LOGO, "");
        if("".equals(uriLogo)){
            int intDefaultLogoResource = pref.getInt(Constants.PREF_DEFAULT_LOGO_RESOURCE, 0);
            imgLogo.setImageResource(intDefaultLogoResource);
        }else{
            Uri uriLogoImage = Uri.parse(uriLogo);
            imgLogo.setImageURI(uriLogoImage);
            imgLogo.refreshDrawableState();
        }

        //Chức năng thiết bị
        MachineFunction machineFunction = MachineFunctionUtils.getMachineFunction();
        int functionPosition = machineFunctionAdapter.getPosition(machineFunction);
        spnDeviceFunction.setSelection(functionPosition);

        //Mức độ kiểm tra sự sống
        LivenessLevel livenessLevel = LivenessLevelUtils.getLivenessLevel();
        int livenessLevelPosition = livenessLevelAdapter.getPosition(livenessLevel);
        spnLivenessLevel.setSelection(livenessLevelPosition);

        //Loại cửa tích hợp
        DoorType doorType = DoorTypeUtils.getDoorType();
        int doorTypePosition = doorTypeAdapter.getPosition(doorType);
        spnDoorType.setSelection(doorTypePosition);

        if(doorType.getValue() == Application.getInstance().getResources().getInteger(R.integer.door_type_with_controller_value)){
            edtDelayDoorTime.setVisibility(View.INVISIBLE);
        }else{
            edtDelayDoorTime.setVisibility(View.VISIBLE);
        }

        //Thời gian chờ cửa
        edtDelayDoorTime.setText(String.valueOf(pref.getInt(Constants.PREF_DELAY_DOOR_TIME, 3000)));

        //Ngôn ngữ
        Language language = LanguageUtils.getCurrentLanguage();
        int languagePosition = languageAdapter.getPosition(language);
        spnLanguage.setSelection(languagePosition);

        //Reboot time
        switchAutoReboot.setChecked(pref.getBoolean(Constants.PREF_USE_AUTO_REBOOT, true));
        boolean state = switchAutoReboot.isChecked();
        timePicker.setEnabled(state);

        timePicker.setHour(pref.getInt(Constants.PREF_FREQ_REBOOT_HOUR, 0));
        timePicker.setMinute(pref.getInt(Constants.PREF_FREQ_REBOOT_MINUTE, 0));
    }

    public void Save(View view){
        double nguongXacNhan = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getFaceThreshold();
        int distanceDetectPixel = Constants.MIN_FACE_SIZE_RGB;
        String strNguongXacNhan = String.valueOf(edtThreshold.getText());
        if(StringUtils.isNotEmpty(strNguongXacNhan)){
            try{
                nguongXacNhan = Double.parseDouble(strNguongXacNhan);
            }catch (Exception ex){
                nguongXacNhan = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getFaceThreshold();
            }

            if(nguongXacNhan < 0.7 || nguongXacNhan >1){
                showToast(LanguageUtils.getString(R.string.message_score_matching_invalid));
                return;
            }
        }

        int distanceDetect = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDistance();
        String strDistanceDetect = String.valueOf(edtDistanceDetect.getText());
        if(StringUtils.isNotEmpty(strDistanceDetect)){
            try{
                distanceDetect = Integer.parseInt(strDistanceDetect);
            }catch (Exception ex){
                distanceDetect = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDistance();
            }

            if(distanceDetect < 20 || distanceDetect > 100){
                showToast(LanguageUtils.getString(R.string.message_distance_invalid));
                return;
            }

            distanceDetectPixel = BaseUtil.distanceToPixel(distanceDetect, Build.MODEL);
        }

        int delayRecognizeTime = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime();
        String strBetweenTime = String.valueOf(edtDelayRecognize.getText());
        if(StringUtils.isNotEmpty(strBetweenTime)){
            try{
                delayRecognizeTime = Integer.parseInt(strBetweenTime);
            }catch (Exception ex){
                delayRecognizeTime = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime();
                Log.e("ERROR", ex.getMessage());
            }

            if(delayRecognizeTime <= 1000 && delayRecognizeTime > 10*1000){
                showToast(LanguageUtils.getString(R.string.message_delay_time_invalid));
                return;
            }
        }

        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        pref.edit().putString(Constants.PREF_COMPANY_NAME, String.valueOf(edtCompanyName.getText())).apply();
        pref.edit().putBoolean(Constants.PREF_HIDE_NAVIGATION_BAR, switchHideNavigator.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_HIDE_STATUS_BAR, switchHideStatus.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_LIVENESS_DETECT, switchLivenessDetect.isChecked()).apply();
        pref.edit().putInt(Constants.PREF_DELAY_RECOGNIZE_TIME, delayRecognizeTime).apply();
        pref.edit().putInt(Constants.PREF_DELAY_DOOR_TIME, Integer.parseInt(String.valueOf(edtDelayDoorTime.getText()))).apply();
        pref.edit().putBoolean(Constants.PREF_AUTOMATIC_START, switchAutoStart.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_AUTOMATIC_SLEEP, switchAutoSleep.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_NO_DELAY, switchNoDelay.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_USE_LED, switchTurnLed.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_USE_SOUND, switchTurnSound.isChecked()).apply();
        pref.edit().putString(Constants.PREF_THRESHOLD, String.valueOf(nguongXacNhan)).apply();
        pref.edit().putString(Constants.PREF_TEMPERATURE_THRESHOLD, String.valueOf(edtTempThreshold.getText())).apply();
        pref.edit().putString(Constants.PREF_NOMASK_QUALITY_THRESHOLD, String.valueOf(edtNomaskQualityThreshold.getText())).apply();
        pref.edit().putString(Constants.PREF_MASK_QUALITY_THRESHOLD, String.valueOf(edtMaskQualityThreshold.getText())).apply();
        pref.edit().putString(Constants.PREF_REGISTER_QUALITY_THRESHOLD, String.valueOf(edtRegisterQualityThreshold.getText())).apply();
        pref.edit().putString(Constants.PREF_BUSINESS_SERVER_HOST, String.valueOf(edtServerHost.getText())).apply();
        pref.edit().putString(Constants.PREF_USERNAME_AUTH, String.valueOf(edtUsernameAuth.getText())).apply();
        pref.edit().putString(Constants.PREF_PASSWORD_AUTH, String.valueOf(edtPasswordAuth.getText())).apply();
        pref.edit().putInt(Constants.PREF_DEFAULT_LOGO_RESOURCE, R.drawable.logo).apply();
        pref.edit().putBoolean(Constants.PREF_USE_BUSINESS_CHECK, switchUseBusinessCheck.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_CHECK_MASK, switchCheckMask.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_CHECK_TEMPERATURE, switchCheckTemperature.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_SAVE_PERSON_UNREG, switchSavePersonUnreg.isChecked()).apply();
        pref.edit().putInt(Constants.PREF_DISTANCE_DETECT, distanceDetect).apply();
        pref.edit().putInt(Constants.PREF_DISTANCE_DETECT_PIXEL, distanceDetectPixel).apply();
        pref.edit().putBoolean(Constants.PREF_SHOW_VACCINE_INFO, switchShowPersonCode.isChecked()).apply();
        pref.edit().putBoolean(Constants.PREF_USE_AUTO_REBOOT, switchAutoReboot.isChecked()).apply();
        pref.edit().putInt(Constants.PREF_FREQ_REBOOT_HOUR, timePicker.getHour()).apply();
        pref.edit().putInt(Constants.PREF_FREQ_REBOOT_MINUTE, timePicker.getMinute()).apply();
        pref.edit().putBoolean(Constants.PREF_SHOW_BODY_TEMPERATURE, switchShowTemperature.isChecked()).apply();

        if(switchAutoReboot.isChecked()){
            setDailyReboot();
        }else{
            cancelDailyReboot();
        }

        SingletonObject.getInstance(this).init();

        Toast.makeText(this, getString(R.string.message_update_success), Toast.LENGTH_SHORT).show();
    }

    public void Close(View view){
        Intent i = new Intent(SystemSettingActivity.this, RegisterAndRecognizeDualActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void checkUpdateSystem(View view){
        startActivity(new Intent(SystemSettingActivity.this, UpdateSystemActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imgLogo.setImageURI(selectedImage);
                    pref.edit().putString(Constants.PREF_LOGO, selectedImage.toString()).apply();
                }
                break;

            case IMAGE_PICK:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                    try (InputStream ins = getContentResolver().openInputStream(selectedImage)) {
                        String fileExtension = BaseUtil.getMimeType(getApplicationContext(), selectedImage);
                        String logoSave = Environment.getExternalStorageDirectory() + File.separator + "Systems" + File.separator + "logo." + fileExtension;
                        File dest = new File(logoSave);
                        createFileFromStream(ins, dest);
                        File file = new File(logoSave);
                        if(file.isFile() && file.exists()){
                            selectedImage = Uri.fromFile(file);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    imgLogo.setImageURI(selectedImage);
                    pref.edit().putString(Constants.PREF_LOGO, selectedImage.toString()).apply();
                }
                break;

            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    showToast(LanguageUtils.getString(R.string.message_administrator_on));
                } else {
                    showToast(LanguageUtils.getString(R.string.message_administrator_off));
                }
                return;

            default:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imgLogo.setImageURI(selectedImage);
                    pref.edit().putString(Constants.PREF_LOGO, selectedImage.toString()).apply();
                }
                break;
        }
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setDefaultUser(View view){
        edtUsernameAuth.setText(BaseUtil.getImeiNumber(SystemSettingActivity.this));
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }

    public void calibrationCamera(View view){
        if(!isInstalled(SystemSettingActivity.this,"com.telpo.temperatureservice")){
            Toast.makeText(this, getString(R.string.message_device_not_support), Toast.LENGTH_SHORT);
        }else{
            startActivity(new Intent(SystemSettingActivity.this, CalibrationTemperatureCameraActivity.class));
        }
    }

    public void requestAdminPermission(View view){
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdmin = new ComponentName(this, AdminReceiver.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"");
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    //Kiểm tra đã cài gói service cho phần nhiệt độ chưa
    private boolean isInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    private void setDailyReboot() {
        int hour = pref.getInt(Constants.PREF_FREQ_REBOOT_HOUR, 0);
        int minute = pref.getInt(Constants.PREF_FREQ_REBOOT_MINUTE, 0);
        AlarmManagerUtil.setAlarm(this, 1, hour, minute, 0, 0, "Auto reboot", 0);
    }

    private void cancelDailyReboot(){
        AlarmManagerUtil.cancelAlarm(this, Constants.ALARM_REBOOT_OS, 0);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.switchAutoReboot){
            boolean state = switchAutoReboot.isChecked();
            timePicker.setEnabled(state);
        } else if(view.getId() == R.id.imgLogo){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, IMAGE_PICK);
        }
    }

    private void updateViewByLanguage() {
        //Device info
        binding.lbDeviceInfo.setText(LanguageUtils.getString(R.string.label_device_info));
        binding.lbIPAddress.setText(LanguageUtils.getString(R.string.label_ip_address));
        binding.lbIMEI.setText(LanguageUtils.getString(R.string.label_imei));
        binding.lbMAC.setText(LanguageUtils.getString(R.string.label_mac_address));
        binding.lbVersion.setText(LanguageUtils.getString(R.string.label_version));

        //Display info
        binding.lbDisplay.setText(LanguageUtils.getString(R.string.label_display));
        binding.lbCompanyName.setText(LanguageUtils.getString(R.string.label_company_name));
        binding.lbLanguage.setText(LanguageUtils.getString(R.string.label_language));
        binding.lbHideNavigator.setText(LanguageUtils.getString(R.string.label_hide_navigator_bar));
        binding.lbHideStatus.setText(LanguageUtils.getString(R.string.label_hide_status_bar));

        //Application settings
        binding.lbSetting.setText(LanguageUtils.getString(R.string.label_config_application));
        binding.lbFunction.setText(LanguageUtils.getString(R.string.label_function));
        binding.lbLiveness.setText(LanguageUtils.getString(R.string.label_liveness_detect));
        binding.lbLivenessLevel.setText(LanguageUtils.getString(R.string.label_liveness_level));
        binding.lbUseLed.setText(LanguageUtils.getString(R.string.label_use_led));
        binding.lbUseSound.setText(LanguageUtils.getString(R.string.label_use_sound));
        binding.lbAutoStart.setText(LanguageUtils.getString(R.string.label_automatic_start));
        binding.lbAutoSleep.setText(LanguageUtils.getString(R.string.label_automatic_sleep));
        binding.lbNoDelay.setText(LanguageUtils.getString(R.string.label_no_delay));
        binding.lbCheckAccessPermission.setText(LanguageUtils.getString(R.string.label_check_access_business));
        binding.lbCheckFaceMask.setText(LanguageUtils.getString(R.string.label_check_mask));
        binding.lbCheckBodyTemperature.setText(LanguageUtils.getString(R.string.label_check_temperature));
        binding.lbSaveGuestLog.setText(LanguageUtils.getString(R.string.label_save_person_unreg));
        binding.lbShowPersonCode.setText(LanguageUtils.getString(R.string.label_show_personCode));
        binding.lbScanQRCode.setText(LanguageUtils.getString(R.string.label_pccovid));
        binding.lbCalibrateSensor.setText(LanguageUtils.getString(R.string.label_calibration_temperature_camera));
        binding.lbDoorType.setText(LanguageUtils.getString(R.string.label_door_type));
        binding.lbDelayDoorTime.setText(LanguageUtils.getString(R.string.label_wait_door_time));

        //Daily auto reboot configuration
        binding.lbScheduleReboot.setText(LanguageUtils.getString(R.string.label_restart_header));
        binding.lbUseRebootSchedule.setText(LanguageUtils.getString(R.string.label_use_auto_reboot));
        binding.lbRebootTime.setText(LanguageUtils.getString(R.string.label_time_reboot));

        //Face recognition parameter
        binding.lbFaceParameter.setText(LanguageUtils.getString(R.string.label_face_recognize_parameter));
        binding.lbFaceThreshold.setText(LanguageUtils.getString(R.string.label_face_threshold));
        binding.lbNomaskQualityThreshold.setText(LanguageUtils.getString(R.string.label_nomask_quality_threshold));
        binding.lbMaskQualityThreshold.setText(LanguageUtils.getString(R.string.label_mask_quality_threshold));
        binding.lbRegisterQualityThreshold.setText(LanguageUtils.getString(R.string.label_register_quality_threshold));
        binding.lbTemperatureThreshold.setText(LanguageUtils.getString(R.string.label_temperature_threshold));
        binding.lbDelayTime.setText(LanguageUtils.getString(R.string.label_time_keep_open_door));
        binding.lbDistanceDetect.setText(LanguageUtils.getString(R.string.label_distance_calibration));

        //Server info
        binding.lbServerInfo.setText(LanguageUtils.getString(R.string.label_server_parameter));
        binding.lbServerName.setText(LanguageUtils.getString(R.string.label_bussiness_server_host));
        binding.lbUsername.setText(LanguageUtils.getString(R.string.label_authentication_username));

        //Upgrade
        binding.lbUpgrade.setText(LanguageUtils.getString(R.string.label_update));
        binding.lbCheckNewVersion.setText(LanguageUtils.getString(R.string.label_check_update_version));

        //Button
        binding.btnSave.setText(LanguageUtils.getString(R.string.button_save));
        binding.btnClose.setText(LanguageUtils.getString(R.string.button_close));
        binding.btnOptions.setText(LanguageUtils.getString(R.string.button_options));
        binding.btnGallery.setText(LanguageUtils.getString(R.string.button_database));
        binding.btnEvent.setText(LanguageUtils.getString(R.string.button_event));
    }
}

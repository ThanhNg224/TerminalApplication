package com.atin.arcface.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.LivenessParam;
import com.arcsoft.face.MaskInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.atin.arcface.BuildConfig;
import com.atin.arcface.R;
import com.atin.arcface.common.AccessBussiness;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.DeviceConfigurationParam;
import com.atin.arcface.common.EmitSound;
import com.atin.arcface.common.ErrorCode;
import com.atin.arcface.common.Hardware;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.common.NavigationUtils;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.faceserver.CompareResult;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.faceserver.FaceServer;
import com.atin.arcface.model.AccessResult;
import com.atin.arcface.model.DeviceConfigurationModel;
import com.atin.arcface.model.DoorType;
import com.atin.arcface.model.DrawInfo;
import com.atin.arcface.model.EventDB;
import com.atin.arcface.model.FaceBiometric;
import com.atin.arcface.model.FaceDB;
import com.atin.arcface.model.FaceInfoCapture;
import com.atin.arcface.model.FacePreviewInfo;
import com.atin.arcface.model.InitEngineFailureLog;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.model.MealByMonthDB;
import com.atin.arcface.model.PersonAccessDB;
import com.atin.arcface.model.PersonDB;
import com.atin.arcface.model.TemperatureError;
import com.atin.arcface.service.CrashExceptionHandler;
import com.atin.arcface.service.DeleteGuestExpriedService;
import com.atin.arcface.service.LogResponseServer;
import com.atin.arcface.service.PerformanceInforService;
import com.atin.arcface.service.PressOpenDoorService;
import com.atin.arcface.service.PrinterHelper;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.service.ProcFolderParser;
import com.atin.arcface.service.SynchronizeDataService;
import com.atin.arcface.service.ThreadCheckNewFirmware;
import com.atin.arcface.service.ThreadRemoveLogFile;
import com.atin.arcface.service.ThreadSynchronizeEventLog;
import com.atin.arcface.service.ThreadSynchronizeSummaryPersonLog;
import com.atin.arcface.service.TokenService;
import com.atin.arcface.service.WebSocketConnection;
import com.atin.arcface.util.AlarmManagerUtil;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.DialogListener;
import com.atin.arcface.util.DoorTypeUtils;
import com.atin.arcface.util.DrawHelper;
import com.atin.arcface.util.LanguageUtils;
import com.atin.arcface.util.LivenessLevelUtils;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.MachineFunctionUtils;
import com.atin.arcface.util.StringUtils;
import com.atin.arcface.util.camera.CameraListener;
import com.atin.arcface.util.camera.DualCameraHelper;
import com.atin.arcface.util.face.FaceHelper;
import com.atin.arcface.util.face.FaceHelperIr;
import com.atin.arcface.util.face.FaceListener;
import com.atin.arcface.util.face.RequestFeatureStatus;
import com.atin.arcface.widget.BlurBehind;
import com.atin.arcface.widget.FaceRectView;
import com.atin.arcface.widget.ShowFaceInfoAdapter;
import com.common.thermalimage.HotImageCallback;
import com.common.thermalimage.TemperatureBitmapData;
import com.common.thermalimage.TemperatureData;
import com.common.thermalimage.ThermalImageUtil;
import com.google.gson.Gson;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterAndRecognizeDualActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "RegisterAndRecognize";
    public static Context instance;
    private static final int MAX_DETECT_NUM = 3;
    public static String EVENT_PATH;

    private DualCameraHelper cameraHelper, cameraHelperIr;
    private DrawHelper drawHelperRgb, drawHelperIr;
    private Camera.Size previewSize, previewSizeIr;
    private volatile byte[] rgbData, irData;
    private final HashMap<Integer, FaceInfoCapture> hmFaceCapture = new HashMap<>();

    private FaceEngine ftEngine; //Detect
    private FaceEngine frEngine; //Recognize
    private FaceEngine flEngine; //Live
    private int ftInitCode = -1, frInitCode = -1, flInitCode = -1;

    private FaceEngine ftIrEngine; //Detect
    private FaceEngine frIrEngine; //Recognize
    private FaceEngine flIrEngine; //Live
    private int ftIrInitCode = -1, frIrInitCode = -1, flIrInitCode = -1;

    //Biến dùng để đánh dấu engine đã được khởi tạo để tránh phải khởi tạo nhiều lần gây lỗi bộ nhớ
    private static boolean isEngineInited = false;

    private static final int INIT_MASK = FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_MASK_DETECT | FaceEngine.ASF_IMAGEQUALITY | FaceEngine.ASF_MASK_DETECT;

    /**
     * Camera ưu tiên
     */
    private FaceHelper faceHelperRgb;
    private FaceHelperIr faceHelperIr;
    private final ConcurrentHashMap<Integer, Integer> requestFeatureRgbStatusMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> requestFeatureIrStatusMap = new ConcurrentHashMap<>();

    /**
     * Các điều khiển hiển thị trong bản xem trước của máy ảnh có thể là SurfaceView và TextureView
     */
    private View previewView, previewViewIr;
    private ImageView imgLogo;
    private TextView txtTemperature;
    private TextView txtNotification;
    private BlurBehind vBlurBehind;

    //Kết quả hiển thị
    private List<CompareResult> compareResultList;
    private ShowFaceInfoAdapter adapter;

    //component camera nhiệt
    private ThermalImageUtil temperatureUtil;
    private TemperatureData temperatureData;
    private ImageView imgTemperaturePhoto;
    private String temperature = "0", tempDescription = "";
    private boolean supportTemperatureCamera = false;

    /**
     * Vẽ ô vuông quanh mặt
     */
    private FaceRectView faceRectView;
    private final ConcurrentHashMap<Integer, FaceBiometric> mListFaceBiometricRgb = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, FaceBiometric> mListFaceBiometricIr = new ConcurrentHashMap<>();

    private long mLastClickTime;
    private int countTabLogo = 5;
    private Database database;

    /**
     * Preference variable
     */
    private SharedPreferences pref;
    private boolean prefSavePersonUnreg;
    private boolean prefActiveAlready;
    private boolean prefUseSound;
    private boolean prefUseLed;
    private boolean prefAutoSleep;
    private boolean prefDisplayRealTime;
    private boolean prefShowBodyTemperature;
    private float prefSimilarThreshold, prefTemperatureThreshold;
    private int prefMachineFunction;
    private int prefDelayRecognizeTime;
    private int totalFaceInIrFrame = 0;
    private AccessBussiness accessBussiness = null;

    //Biến tạm
    private boolean isLiveness = false;

    //Camera config param
    private DeviceConfigurationModel cameraConfig = null;
    private InitPreferenceReceiver initAllReceiver = null;

    /**
     * Permission Is Need
     */
    private static final int ACTION_REQUEST_PERMISSIONS = 2;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private Logger logger;
    private String performanceNote;
    private MachineDB thisDevice = null;
    private static final Gson gson = new Gson();

    //Dùng để đánh dấu trạng thái khi khuôn mặt được detect và thoát khỏi vùng detect
    private boolean mFaceApperanceTick = false;

    private ExecutorService excutorLed, excutorLog, excutorSound, excutorRelay;

    //Component result dialog
    private TextView txtVersion;
    private ImageView imgIntenet, imgDefaultStatus, printStatus;
    private View vSyncStatus;
    private String mTempRequestTime; //Khi nhận diện nhanh xuất hiện case time từ configutil giữa 2 luồng không giống nhau, phải lưu ra biến tạm để so sánh tránh lỗi dialog bị nhấp nháy
    private int mRequestId;
    private TwinDialog twinDialog;

    public static Context getContext() {
        return instance;
    }
    private FaceFeature rgbFeature = null, irFeature = null;

    private ThreadSynchronizeEventLog threadSynchronizeEventLog = null;

    //Component for clock
    private TextView txtDate, txtHour;
    private Handler handler;
    private Runnable runnable;

    //message send performance to service
    private Messenger serviceMessenger;

    public MachineDB getThisDevice() {
        return thisDevice;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //setContentView(R.layout.activity_register_and_recognize_dual);
        com.atin.arcface.databinding.ActivityRegisterAndRecognizeDualBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register_and_recognize_dual);

        // Activity Sau khi bắt đầu, nó khóa theo hướng khi bắt đầu
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(attributes);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        pref = getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(RegisterAndRecognizeDualActivity.this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        //Khởi tạo database sql lite
        initDatabase();

        //Init view
        initView();

        //Pref
        initPreferenceValue();

        //Multiple language
        LanguageUtils.loadLocale();

        //Kiểm tra active engine
        if (!prefActiveAlready) {
            startActivity(new Intent(RegisterAndRecognizeDualActivity.this, ActiveByInputKeyActivity.class));
            return;
        }

        //Khởi tạo server xử lý nhận diện Face
        FaceServer.getInstance().init(getApplicationContext());
        //CardServer.getInstance().init(getApplicationContext());

        //Thread post log về server, đồng bộ dữ liệu với máy chủ, update new firmware, xóa guest hết hạn vào cuối ngày
        initAllThread();

        //Thread delay frame rate RGB
        new Thread(this::manageFrameRateRGB).start();

        //Hander kiểm tra person dialog show
        new Thread(this::hideResultDialog).start();

        String domain = SingletonObject.getInstance(this).getDomain();
        new Thread(this::internetStatus).start();
        new Thread(() -> cloudApiStatus(domain + "/api/v1/hello")).start();
        new Thread(this::printerStatus).start();

        //Trạng thái phần cứng khi khởi tạo
        initHardwareState(Build.MODEL);


        //Auto start application
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler(this));
        BaseUtil.handleSSLHandshake();

        //Service lấy thông tin CPU RAM TEMPERATURE
        Intent intent = new Intent(this, ProcFolderParser.class);
        startService(intent);

        initClock();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    private void initDatabase() {
        database = Application.getInstance().getDatabase();
    }

    private void initReceiver() {
        try {
            IntentFilter[] filters = new IntentFilter[]{
                    new IntentFilter(Constants.INIT_PREFERENCE),
                    new IntentFilter(Constants.INIT_BUSSINES_ACCESS_DATA),
                    new IntentFilter(Constants.ALARM_REBOOT_OS),
                    new IntentFilter(Constants.SHOW_NOTIFICATION),
                    new IntentFilter(Constants.SYNCHRONIZE_STATUS),
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                for (IntentFilter filter : filters) {
                    registerReceiver(initAllReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
                for (IntentFilter filter : filters) {
                    registerReceiver(initAllReceiver, filter, null, null, Context.RECEIVER_NOT_EXPORTED);
                }
            } else {
                for (IntentFilter filter : filters) {
                    registerReceiver(initAllReceiver, filter);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error initReceiver " + ex.getMessage());
        }
    }

    private void initView() {
        previewView = findViewById(R.id.texture_preview);
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        previewViewIr = findViewById(R.id.texture_preview_ir);
        previewViewIr.getViewTreeObserver().addOnGlobalLayoutListener(this);
        txtNotification = findViewById(R.id.notification);
        txtTemperature = findViewById(R.id.txtTemperature);
        imgTemperaturePhoto = findViewById(R.id.imgTemperature);
        imgLogo = findViewById(R.id.imgLogo);
        faceRectView = findViewById(R.id.face_rect_view);
        vBlurBehind = findViewById(R.id.vBlurBehinde);
        txtDate = findViewById(R.id.txtDate);
        txtHour = findViewById(R.id.txtHour);

        txtVersion = findViewById(R.id.txtVersion);

        imgIntenet = findViewById(R.id.intenetStatus);
        imgDefaultStatus = findViewById(R.id.cloudDefaultStatus);
        vSyncStatus = findViewById(R.id.cloudSyncStatus);
        printStatus = findViewById(R.id.printerStatus);

        accessBussiness = new AccessBussiness(RegisterAndRecognizeDualActivity.this);
        logger = Log4jHelper.getLogger("RegisterAndRecognizeDualActivity");
        cameraConfig = DeviceConfigurationParam.getInstance().getModel(Build.MODEL);

        RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person);
        compareResultList = new ArrayList<>();
        adapter = new ShowFaceInfoAdapter(this, compareResultList);
        recyclerShowFaceInfo.setAdapter(adapter);
        recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());

        LinkedBlockingQueue<Runnable> blockingQueueLed = new LinkedBlockingQueue<>();
        excutorLed = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, blockingQueueLed);

        LinkedBlockingQueue<Runnable> blockingQueueLog = new LinkedBlockingQueue<>();
        excutorLog = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, blockingQueueLog);

        LinkedBlockingQueue<Runnable> blockingQueueSound = new LinkedBlockingQueue<>();
        excutorSound = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, blockingQueueSound);

        LinkedBlockingQueue<Runnable> blockingQueueRelay = new LinkedBlockingQueue<>();
        excutorRelay = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, blockingQueueRelay);

        initAllReceiver = new InitPreferenceReceiver();
    }

    private void initClock(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000); // Cập nhật thời gian mỗi giây
            }
        };
    }

    private void updateTime() {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            String[] dateArray = currentTime.split("-", -1);
            if(dateArray.length > 1){
                txtDate.setText(dateArray[0]);
                txtHour.setText(dateArray[1]);
            }else{
                txtDate.setText("");
                txtHour.setText("");
            }
        }catch (Exception ex){
            Log.e(TAG, "Error update time");
        }
    }

    private void initPreferenceValue() {
        //Init singleton common object
        SingletonObject.getInstance(RegisterAndRecognizeDualActivity.this).init();
        SingletonObject.getInstance(RegisterAndRecognizeDualActivity.this).setMainActivity(RegisterAndRecognizeDualActivity.this);

        txtVersion.setText(BuildConfig.VERSION_NAME + " - " + BaseUtil.getImeiNumber(this));

        //Init Local value
        float faceThreshold = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getFaceThreshold();
        prefSimilarThreshold = Float.parseFloat(pref.getString(Constants.PREF_THRESHOLD, String.valueOf(faceThreshold)));
        if (prefSimilarThreshold < faceThreshold) {
            prefSimilarThreshold = faceThreshold;
        }
        prefTemperatureThreshold = Float.parseFloat(pref.getString(Constants.PREF_TEMPERATURE_THRESHOLD, "" + CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getTemperatureThreshold()));
        boolean prefShowPersonCode = pref.getBoolean(Constants.PREF_SHOW_VACCINE_INFO, true);
        prefSavePersonUnreg = pref.getBoolean(Constants.PREF_SAVE_PERSON_UNREG, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isSaveGuest());
        prefMachineFunction = MachineFunctionUtils.getMachineFunction().getFunctionValue();
        prefActiveAlready = pref.getBoolean(Constants.PREF_ACTIVE_ALREADY, false);
        prefUseSound = pref.getBoolean(Constants.PREF_USE_SOUND, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isUseSound());
        prefUseLed = pref.getBoolean(Constants.PREF_USE_LED, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).isUseLed());
        prefDelayRecognizeTime = pref.getInt(Constants.PREF_DELAY_RECOGNIZE_TIME, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime());
        int prefDelayDoorTime = pref.getInt(Constants.PREF_DELAY_DOOR_TIME, 3000);
        prefAutoSleep = pref.getBoolean(Constants.PREF_AUTOMATIC_SLEEP, false);
        prefShowBodyTemperature = pref.getBoolean(Constants.PREF_SHOW_BODY_TEMPERATURE, false);
        prefDisplayRealTime = true;

        runOnUiThread(() -> initLogo(imgLogo));

        String imei = BaseUtil.getImeiNumber(this);
        try{
            thisDevice = database.getMachineByImei(imei);
        }catch (Exception ex){
            Log.e(TAG, Objects.requireNonNull(ex.getMessage()));
        }
        if(thisDevice != null && (ConfigUtil.getMachine() == null || ConfigUtil.getMachine().getMachineId() == 0)){
            ConfigUtil.setMachine(thisDevice);
        }

        if(thisDevice == null){
            thisDevice = ConfigUtil.getMachine();
        }

        ConfigUtil.setSyncingData(this, false);
        ConfigUtil.setNetworkErrorTime(this, Constants.Value.DEFAULT_ERROR_NETWORK_TIME);

        //Lịch tự động khởi động lại
        boolean prefDailyReboot = pref.getBoolean(Constants.PREF_USE_AUTO_REBOOT, false);
        if(prefDailyReboot){
            int hour = pref.getInt(Constants.PREF_FREQ_REBOOT_HOUR, 0);
            int minute = pref.getInt(Constants.PREF_FREQ_REBOOT_MINUTE, 0);
            AlarmManagerUtil.setAlarm(this, 1, hour, minute, 0, 0, "Auto reboot", 0);
        }else{
            AlarmManagerUtil.cancelAlarm(this, Constants.ALARM_REBOOT_OS, 0);
        }

        boolean blHideNavigationBar = pref.getBoolean(Constants.PREF_HIDE_NAVIGATION_BAR, false);
        NavigationUtils.hideNavigationBar(this, blHideNavigationBar);

        boolean blHideStatusBar = pref.getBoolean(Constants.PREF_HIDE_STATUS_BAR, false);
        NavigationUtils.hideStatusBar(this, blHideStatusBar);

        initScreen();
    }

    private void initAllThread(){
        //Đồng bộ dữ liệu với máy chủ
        Intent synchronizeDataServiceIntent = new Intent(this, SynchronizeDataService.class);
        startService(synchronizeDataServiceIntent);

        //Post log về server
        threadSynchronizeEventLog = new ThreadSynchronizeEventLog(RegisterAndRecognizeDualActivity.this);
        threadSynchronizeEventLog.start();

        //Đồng bộ thông tin performance về server
        Intent performanceInforServiceIntent = new Intent(this, PerformanceInforService.class);
        startService(performanceInforServiceIntent);
        bindService(performanceInforServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        //Interval check new firmware
        ThreadCheckNewFirmware checkNewFirmware = new ThreadCheckNewFirmware(RegisterAndRecognizeDualActivity.this);
        checkNewFirmware.start();

        //Tự động update guest hết hạn
        Intent deleteGuestExpriedServiceIntent = new Intent(this, DeleteGuestExpriedService.class);
        startService(deleteGuestExpriedServiceIntent);

        ThreadSynchronizeSummaryPersonLog synchronizeSummaryPersonLog = new ThreadSynchronizeSummaryPersonLog(RegisterAndRecognizeDualActivity.this);
        synchronizeSummaryPersonLog.start();

        //Nhấn nút mở cửa
        Intent pressOpenDoorIntent = new Intent(this, PressOpenDoorService.class);
        startService(pressOpenDoorIntent);

        //Refresh token
        TokenService threadToken = new TokenService(this);
        threadToken.start();

        ThreadRemoveLogFile threadRemoveLogFile = new ThreadRemoveLogFile();
        threadRemoveLogFile.start();

        //Connect to web socket server
        WebSocketConnection webSocketConnection = new WebSocketConnection(RegisterAndRecognizeDualActivity.this);
        webSocketConnection.run();
    }

    private void initTemperatureReceiver() {
        supportTemperatureCamera = BaseUtil.isInstalledPackage(RegisterAndRecognizeDualActivity.this, Constants.Value.TEMPERATURE_PACKAGE_NAME);
        if (supportTemperatureCamera && prefShowBodyTemperature) {
            temperatureUtil = new ThermalImageUtil(this);
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            registerReceiver(initAllReceiver, intentFilter);

            //Start thread lấy thông tin nhiệt độ
            new Thread(() -> displayTemperateCamera()).start();

            //Hiển thị khung hình ảnh nhiệt
            imgTemperaturePhoto.setVisibility(View.VISIBLE);
            txtTemperature.setVisibility(View.INVISIBLE);
            vBlurBehind.setVisibility(View.INVISIBLE);
        } else {
            imgTemperaturePhoto.setVisibility(View.INVISIBLE);
            txtTemperature.setVisibility(View.INVISIBLE);
            vBlurBehind.setVisibility(View.INVISIBLE);
        }
    }

    private void initHardwareState(String deviceCode) {
        switch (deviceCode) {
            case MachineName.RAKINDA_F3:
                Hardware.turnDoor(0, Build.MODEL, getApplicationContext()); //Đóng cửa
                Hardware.turnLed(0, Constants.LED_ALL, Build.MODEL, getApplicationContext());//Tắt đèn led màu
                break;

            case MachineName.TELPO_F8:
                Hardware.turnIrLight(1, Build.MODEL, getApplicationContext()); //Mở đèn hồng ngoại khi bật thiết bị
                Hardware.turnLed(0, Constants.LED_ALL, Build.MODEL, getApplicationContext());//Tắt đèn led màu
                Hardware.turnLight(0, Constants.LED_ALL, Build.MODEL);//Tắt đèn sáng
                break;

            case MachineName.TELPO_TPS980P:
                Hardware.turnIrLight(1, Build.MODEL, getApplicationContext()); //Mở đèn hồng ngoại khi bật thiết bị
                Hardware.turnLed(0, Constants.LED_ALL, Build.MODEL, getApplicationContext());//Tắt đèn led màu
                Hardware.turnLight(0, Constants.LED_ALL, Build.MODEL);//Tắt đèn sáng
                break;

            case MachineName.TELPO_TPS950:
                Hardware.turnIrLight(1, Build.MODEL, getApplicationContext()); //Mở đèn hồng ngoại khi bật thiết bị
                Hardware.turnLed(0, Constants.LED_ALL, Build.MODEL, getApplicationContext());//Tắt đèn led màu
                Hardware.turnLight(0, Constants.LED_ALL, Build.MODEL);//Tắt đèn sáng
                break;

            case MachineName.RAKINDA_A80M:
                Hardware.turnIrLight(1, Build.MODEL, getApplicationContext()); //Mở đèn hồng ngoại khi bật thiết bị
                Hardware.turnLight(0, Constants.LED_ALL, Build.MODEL);//Tắt đèn sáng
                break;

            default:
                break;
        }
    }

    private void initScreen() {
        switch (Build.MODEL) {
            case MachineName.RAKINDA_F3:
                break;

            case MachineName.TELPO_F8:
                txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                txtHour.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                break;

            case MachineName.TELPO_TPS980P:
                break;

            case MachineName.TELPO_TPS950:
                break;

            default:
                break;
        }
    }

    /**
     * Khởi tạo SDK
     */
    private void initEngine() {
        ftEngine = new FaceEngine();

        ftInitCode = ftEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, INIT_MASK);

        frEngine = new FaceEngine();
        frInitCode = frEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);

        flEngine = new FaceEngine();
        flInitCode = flEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);

        //LivenessParam livenessParam = new LivenessParam(ConfigUtil.getRgbLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getIrLivenessThreshold(RegisterAndRecognizeDualActivity.this));
        LivenessParam livenessParam = new LivenessParam(ConfigUtil.getRgbLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getIrLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getLivenessFqThreshold(RegisterAndRecognizeDualActivity.this));
        flEngine.setLivenessParam(livenessParam);

        logger.info("initEngine:  init: " + ftInitCode);

        if (ftInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "ftEngine", ftInitCode);
            logger.info("initEngine: " + error);
        }
        if (frInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "frEngine", frInitCode);
            logger.info("initEngine: " + error);
        }
        if (flInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "flEngine", flInitCode);
            logger.info("initEngine: " + error);
        }

        if (ftInitCode == ErrorInfo.MERR_EXPIRED || ftInitCode == ErrorInfo.MERR_ASF_ACTIVEKEY_EXPIRED || ftInitCode == ErrorInfo.MERR_ASF_LICENSE_FILE_EXPIRED) {
            txtNotification.setVisibility(View.VISIBLE);
        } else {
            txtNotification.setVisibility(View.INVISIBLE);
        }

        if(ftInitCode == ErrorInfo.MERR_ASF_DEVICE_MISMATCH){
            if(ConfigUtil.getInitEngineFailure() != null && ConfigUtil.getInitEngineFailure().getInitTime() != null){
                long durationInMilliseconds = new Date().getTime() - ConfigUtil.getInitEngineFailure().getInitTime().getTime();
                long minutes = durationInMilliseconds / (60 * 1000);
                if(minutes > 5){
                    ConfigUtil.setInitEngineFailure(null);
                }
            }

            if(ConfigUtil.getInitEngineFailure() == null){
                InitEngineFailureLog initEngineFailureLog = new InitEngineFailureLog();
                initEngineFailureLog.setInitTime(new Date());
                initEngineFailureLog.setInitCount(0);
                ConfigUtil.setInitEngineFailure(initEngineFailureLog);
            }

            InitEngineFailureLog initEngineFailureLog = ConfigUtil.getInitEngineFailure();
            if(initEngineFailureLog != null){
                initEngineFailureLog.setInitCount(initEngineFailureLog.getInitCount() + 1);
                ConfigUtil.setInitEngineFailure(initEngineFailureLog);
                if(initEngineFailureLog.getInitCount() == 1){
                    try {
                        logger.error("ENGINE: Khởi tạo ứng dụng không thành công reboot " + ftInitCode);
                        BaseUtil.reboot();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    txtNotification.setText("ENGINE: Lỗi khởi tạo phần mềm vui lòng khởi động lại" + " - " + initEngineFailureLog.getInitCount() + " - " + ftInitCode);
                }
            }
        }else{
            ConfigUtil.setInitEngineFailure(null);
        }

        isEngineInited = true;
    }

    /**
     * Khởi tạo SDK
     */
    private void initIrEngine() {
        ftIrEngine = new FaceEngine();
        ftIrInitCode = ftIrEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, INIT_MASK);

        frIrEngine = new FaceEngine();
        frIrInitCode = frIrEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);

        flIrEngine = new FaceEngine();
        flIrInitCode = flIrEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                MAX_DETECT_NUM, FaceEngine.ASF_IR_LIVENESS);

        LivenessParam livenessParam = new LivenessParam(ConfigUtil.getRgbLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getIrLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getLivenessFqThreshold(RegisterAndRecognizeDualActivity.this));
        //LivenessParam livenessParam = new LivenessParam(ConfigUtil.getRgbLivenessThreshold(RegisterAndRecognizeDualActivity.this), ConfigUtil.getIrLivenessThreshold(RegisterAndRecognizeDualActivity.this));
        flIrEngine.setLivenessParam(livenessParam);

        Log.i(TAG, "initIrEngine:  init: " + ftInitCode);

        if (ftIrInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "ftIrEngine", ftIrInitCode);
            Log.i(TAG, "initIrEngine: " + error);
        }
        if (frIrInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "frIrEngine", frIrInitCode);
            Log.i(TAG, "initIrEngine: " + error);
        }
        if (flIrInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.message_specific_engine_init_failed, "flIrEngine", flIrInitCode);
            Log.i(TAG, "initIrEngine: " + error);
        }
    }

    /**
     * Huỷ SDK
     */
    private void unInitEngine() {
        if (ftInitCode == ErrorInfo.MOK && ftEngine != null) {
            synchronized (ftEngine) {
                int ftUnInitCode = ftEngine.unInit();
                logger.info("unInitEngine: " + ftUnInitCode);
            }
        }
        if (frInitCode == ErrorInfo.MOK && frEngine != null) {
            synchronized (frEngine) {
                int frUnInitCode = frEngine.unInit();
                logger.info("unInitEngine: " + frUnInitCode);
            }
        }
        if (flInitCode == ErrorInfo.MOK && flEngine != null) {
            synchronized (flEngine) {
                int flUnInitCode = flEngine.unInit();
                logger.info("unInitEngine: " + flUnInitCode);
            }
        }

        isEngineInited = false;
    }

    private void unInitIrEngine() {
        if (ftIrInitCode == ErrorInfo.MOK && ftIrEngine != null) {
            synchronized (ftIrEngine) {
                int ftUnInitCode = ftIrEngine.unInit();
                Log.i(TAG, "unInitIrEngine: " + ftUnInitCode);
            }
        }
        if (frIrInitCode == ErrorInfo.MOK && frIrEngine != null) {
            synchronized (frIrEngine) {
                int frUnInitCode = frIrEngine.unInit();
                Log.i(TAG, "unInitIrEngine: " + frUnInitCode);
            }
        }
        if (flIrInitCode == ErrorInfo.MOK && flIrEngine != null) {
            synchronized (flIrEngine) {
                int flUnInitCode = flIrEngine.unInit();
                Log.i(TAG, "unInitIrEngine: " + flUnInitCode);
            }
        }
    }

    private void initLogo(ImageView imgView) {
        //Set logo
        String uriLogo = pref.getString(Constants.PREF_LOGO, "");
        int intDefaultLogoResource = pref.getInt(Constants.PREF_DEFAULT_LOGO_RESOURCE, 0);
        if (!"".equals(uriLogo)) {
            Uri uriLogoImage = Uri.parse(uriLogo);
            imgView.setImageURI(uriLogoImage);

            imgView.invalidate();
            imgView.refreshDrawableState();
            return;
        }
        if (intDefaultLogoResource != 0) {
            imgView.setImageResource(intDefaultLogoResource);

            imgView.invalidate();
            imgView.refreshDrawableState();
        }
    }

    public void tapLogo(View view) {
        try {
            if (mLastClickTime == 5) {
                mLastClickTime = SystemClock.elapsedRealtime();
            }

            //Click lien tục trong 5second
            long during = SystemClock.elapsedRealtime() - mLastClickTime;
            if (during > 5000) {
                mLastClickTime = 5;
                return;
            }

            countTabLogo--;
            if (countTabLogo == 0) {
                mLastClickTime = 5;
                countTabLogo = 5;
                onPause();

                final DialogListener dialogListener = new DialogListener() {
                    @Override
                    public void onShow() {
                    }

                    @Override
                    public void onClose() {
                        onResume();
                    }

                    @Override
                    public void onResponse(Object object) {

                    }
                };

                adjustScreenBrightness(Constants.ScreenBrightnessValue.MAX);
                LoginDialog loginDialogs = new LoginDialog(RegisterAndRecognizeDualActivity.this, dialogListener);
                loginDialogs.showDialogLogin();
            } else {
                showToast(String.format(getString(R.string.message_tooltip_login), "" + countTabLogo));
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    public void onActiveByKeyInput(View view) {
        startActivity(new Intent(this, ActiveByInputKeyActivity.class));
    }

    /**
     * Trong {@link #previewView} sau khi bố trí đầu tiên hoàn tất, hãy loại bỏ trình nghe và khởi tạo động cơ và camera
     */
    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        previewViewIr.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        if (prefActiveAlready) {
            if (cameraHelper != null) {
                cameraHelper.release();
                cameraHelper = null;
            }

            if (cameraHelperIr != null) {
                cameraHelperIr.release();
                cameraHelperIr = null;
            }

            if(!isEngineInited){
                initEngine();
                initIrEngine();
            }
            initRgbCamera();
            initIrCamera();
        }
    }

    @Override
    protected void onPause() {
        synchronized(this){
            if (cameraHelper != null) {
                cameraHelper.release();
                cameraHelper = null;
            }

            if (cameraHelperIr != null) {
                cameraHelperIr.release();
                cameraHelperIr = null;
            }

            if(handler != null){
                handler.removeCallbacks(runnable); // Dừng cập nhật thời gian khi ứng dụng tạm dừng
            }
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        if (checkPermissions(NEEDED_PERMISSIONS)) {
            if(!isEngineInited){
                initEngine();
                initIrEngine();
            }
            initPreferenceValue();

            if (cameraHelper == null && ftInitCode == ErrorInfo.MOK) {
                initRgbCamera();
            }

            if (cameraHelperIr == null && ftInitCode == ErrorInfo.MOK) {
                initIrCamera();
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.CPU_INFO));
            initReceiver();
            initTemperatureReceiver();

            if(handler != null){
                handler.postDelayed(runnable, 0); // Bắt đầu cập nhật thời gian khi ứng dụng được khởi động
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        if (cameraHelperIr != null) {
            cameraHelperIr.release();
            cameraHelperIr = null;
        }

        //faceHelper Có thể có các hoạt động tốn thời gian FR vẫn đang được thực hiện, ngăn chặn crash
        if (faceHelperRgb != null) {
            synchronized (faceHelperRgb) {
                unInitEngine();
            }
            ConfigUtil.setTrackedFaceCount(this, faceHelperRgb.getTrackedFaceCount());
            faceHelperRgb.release();
            faceHelperRgb = null;
        } else {
            unInitEngine();
        }

        //faceHelper Có thể có các hoạt động tốn thời gian FR vẫn đang được thực hiện, ngăn chặn crash
        if (faceHelperIr != null) {
            synchronized (faceHelperIr) {
                unInitIrEngine();
            }
            ConfigUtil.setTrackedFaceIrCount(this, faceHelperIr.getTrackedFaceCount());
            faceHelperIr.release();
            faceHelperIr = null;
        } else {
            unInitIrEngine();
        }

        FaceServer.getInstance().unInit();

        if (initAllReceiver != null) {
            unregisterReceiver(initAllReceiver);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initRgbCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFailRgb: " + e.getMessage());
            }

            //Yêu cầu gọi lại FR
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId, final Integer errorCode) {
                //FR thất bại
                if (faceFeature == null) {
                    requestFeatureRgbStatusMap.clear();
                    return;
                }

                if(ftInitCode != ErrorInfo.MOK){
                    runOnUiThread(() -> txtNotification.setText("ENGINE: Lỗi khởi tạo phần mềm vui lòng khởi động lại ứng dụng" + " - " + ftInitCode));
                    return;
                }

                //Đang upload ảnh thì không xử lý
                ConfigUtil.getSyncingData(RegisterAndRecognizeDualActivity.this);//requestFeatureRgbStatusMap.clear();
//return;

                //Lưu feature vào biến toàn cục
                rgbFeature = faceFeature;
                //End lưu feature

                //FR thành công
                if (compareResultList.isEmpty()){
                    if(thisDevice == null){
                        BaseUtil.broadcastShowMessage(getApplicationContext(), "THIẾT BỊ CHƯA ĐƯỢC ĐỊNH DANH");
                        return;
                    }else{
                        runOnUiThread(() -> txtNotification.setText(""));
                    }
                    searchFaceOffline(faceFeature, requestId);
                }

                requestFeatureRgbStatusMap.clear();
            }

            @Override
            public void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, final Integer requestId, Integer errorCode) {
            }
        };

        CameraListener rgbCameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Camera.Size lastPreviewSize = previewSize;
                previewSize = camera.getParameters().getPreviewSize();

                drawHelperRgb = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(),
                        cameraConfig.getRectRotation(), cameraId, cameraConfig.isMirror(), cameraConfig.isMirrorHorizontal(), cameraConfig.isMirrorVertical());

                if (faceHelperRgb == null ||
                        lastPreviewSize == null ||
                        lastPreviewSize.width != previewSize.width || lastPreviewSize.height != previewSize.height) {
                    Integer trackedFaceCount = null;
                    if (faceHelperRgb != null) {
                        trackedFaceCount = faceHelperRgb.getTrackedFaceCount();
                        faceHelperRgb.release();
                    }
                    faceHelperRgb = new FaceHelper.Builder()
                            .ftEngine(ftEngine)
                            .frEngine(frEngine)
                            .flEngine(flEngine)
                            .frQueueSize(MAX_DETECT_NUM)
                            .flQueueSize(MAX_DETECT_NUM)
                            .previewSize(previewSize)
                            .faceListener(faceListener)
                            .context(RegisterAndRecognizeDualActivity.this)
                            .trackedFaceCount(trackedFaceCount == null ? ConfigUtil.getTrackedFaceCount(RegisterAndRecognizeDualActivity.this.getApplicationContext()) : trackedFaceCount)
                            .build();
                }
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                rgbData = nv21;
                isEngineInited = false; //Khi chương trình đã chạy thì reset trạng thái khởi tạo engine
                processRgbPreviewData();
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelperRgb != null) {
                    drawHelperRgb.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper = new DualCameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(cameraConfig.getCameraRotation())
                .specificCameraId(Constants.CAMERA_RGB)
                .isMirror(cameraConfig.isMirror())
                .previewOn(previewView)
                .cameraListener(rgbCameraListener)
                .build();

        cameraHelper.init();
        try {
            cameraHelper.start();
        } catch (RuntimeException e) {
            showToast(e.getMessage() + LanguageUtils.getString(R.string.message_camera_error_notice));
        }
    }

    private void initIrCamera() {
        final FaceListener faceIrListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFailIr: " + e.getMessage());
            }

            //Yêu cầu gọi lại FR
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId, final Integer errorCode) {
                //FR thất bại
                if (faceFeature == null ) {
                    requestFeatureIrStatusMap.clear();
                    return;
                }

                //Lưu feature vào biến toàn cục
                irFeature = faceFeature;
                //End lưu feature

                requestFeatureIrStatusMap.clear();
            }

            @Override
            public void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, final Integer requestId, Integer errorCode) {
            }
        };

        CameraListener irCameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Camera.Size lastPreviewSize = previewSizeIr;
                previewSizeIr = camera.getParameters().getPreviewSize();
                drawHelperIr = new DrawHelper(previewSizeIr.width, previewSizeIr.height, previewViewIr.getWidth(), previewViewIr.getHeight(), displayOrientation
                        , cameraId, cameraConfig.isMirror(), cameraConfig.isMirrorVertical(), cameraConfig.isMirrorHorizontal());

                if (faceHelperIr == null ||
                        lastPreviewSize == null ||
                        lastPreviewSize.width != previewSizeIr.width || lastPreviewSize.height != previewSizeIr.height) {
                    Integer trackedFaceCount = null;
                    if (faceHelperIr != null) {
                        trackedFaceCount = faceHelperIr.getTrackedFaceCount();
                        faceHelperIr.release();
                    }
                    faceHelperIr = new FaceHelperIr.Builder()
                            .ftEngine(ftIrEngine)
                            .frEngine(frIrEngine)
                            .flEngine(flIrEngine)
                            .frQueueSize(MAX_DETECT_NUM)
                            .flQueueSize(MAX_DETECT_NUM)
                            .previewSize(previewSizeIr)
                            .faceListener(faceIrListener)
                            .context(RegisterAndRecognizeDualActivity.this)
                            .trackedFaceCount(trackedFaceCount == null ? ConfigUtil.getTrackedFaceIrCount(RegisterAndRecognizeDualActivity.this.getApplicationContext()) : trackedFaceCount)
                            .build();
                }
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                irData = nv21;

                if (LivenessLevelUtils.getLivenessLevel().getLevelValue() == getResources().getInteger(R.integer.liveness_level_high_value))
                {
                    processIrPreviewData();
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelperIr != null) {
                    drawHelperIr.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelperIr = new DualCameraHelper.Builder()
                .previewViewSize(new Point(previewViewIr.getMeasuredWidth(), previewViewIr.getMeasuredHeight()))
                .rotation(cameraConfig.getCameraRotation())
                .specificCameraId(Constants.CAMERA_IR)
                .isMirror(cameraConfig.isMirror())
                .previewOn(previewViewIr)
                .cameraListener(irCameraListener)
                .build();
        cameraHelperIr.init();

        try {
            cameraHelperIr.start();
        } catch (RuntimeException e) {
            showToast(e.getMessage() + getString(R.string.message_camera_error_notice));
        }
    }

    /**
     * Xử lý camera RGB
     */
    private synchronized void processRgbPreviewData() {
        if (rgbData != null) {
            final byte[] cloneNv21Rgb = rgbData.clone();

            if (faceRectView != null) {
                faceRectView.clearFaceInfo();
            }

            List<FacePreviewInfo> facePreviewInfoList = faceHelperRgb.onPreviewFrame(cloneNv21Rgb);
            if (facePreviewInfoList != null && faceRectView != null && drawHelperRgb != null) {
                if (facePreviewInfoList.size() > 0) {
                    drawPreviewInfo(facePreviewInfoList);

                    //Nhận diện liên tục nếu có sự xuất hiện của khuôn mặt mới
                    if (mFaceApperanceTick) {
                        mFaceApperanceTick = false;
                    }
                    //End
                } else {
                    isLiveness = false;
                    mFaceApperanceTick = true;
                }
            }
            clearLeftFace(facePreviewInfoList);

            if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                for (int i = 0; i < facePreviewInfoList.size(); i++) {
                    if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                        //Bật đèn led nếu phát hiện khuôn mặt
                        if ( prefUseLed) {
                            turnOnLight();
                            adjustScreenBrightness(Constants.ScreenBrightnessValue.MAX);
                        }
                        //End led
                    }

                    Integer status = requestFeatureRgbStatusMap.get(facePreviewInfoList.get(i).getTrackId());

                    /**
                     * Đối với mỗi khuôn mặt, nếu trạng thái trống hoặc thất bại, hãy yêu cầu FR (có thể thêm các phán đoán khác khi cần để giới hạn số lượng FR),，
                     * Kết quả của các đặc điểm khuôn mặt được FR trả về là {@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}
                     */
                    if (status == null || status == RequestFeatureStatus.TO_RETRY || status == RequestFeatureStatus.FAILED) {
                        requestFeatureRgbStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                        faceHelperRgb.requestFaceFeature(cloneNv21Rgb, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());

                        //thaidd save image info value to global variable
                        FaceBiometric faceBiometric = new FaceBiometric(cloneNv21Rgb, facePreviewInfoList.get(i));
                        int requestId = facePreviewInfoList.get(i).getTrackId();
                        mListFaceBiometricRgb.put(requestId, faceBiometric);
                    }
                }
            } else {
                Set<Integer> keySet = requestFeatureRgbStatusMap.keySet();
                for (int key : keySet) {
                    requestFeatureRgbStatusMap.put(key, RequestFeatureStatus.SUCCEED);
                }
            }

            rgbData = null;
        }
    }

    /**
     * Xử lý camera IR
     */
    private synchronized void processIrPreviewData() {
        try {
            if (irData != null) {
                final byte[] cloneNv21Ir = irData.clone();
                List<FacePreviewInfo> facePreviewInfoList = faceHelperIr.onPreviewIrFrame(cloneNv21Ir);
                if (facePreviewInfoList != null && !facePreviewInfoList.isEmpty() && previewSizeIr != null) {
                    //Bật đèn led nếu phát hiện khuôn mặt sử dụng đoạn code này khi có kiểm tra giả mạo
                    if (prefUseLed) {
                        turnOnLight();
                        adjustScreenBrightness(Constants.ScreenBrightnessValue.MAX);
                    }
                    //End led

                    totalFaceInIrFrame = facePreviewInfoList.size();

                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        requestFeatureIrStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                        faceHelperIr.requestFaceFeature(cloneNv21Ir, facePreviewInfoList.get(i).getFaceInfo(), previewSizeIr.width, previewSizeIr.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());

                        //thaidd save image info value to global variable
                        if(!isLiveness){
                            FaceBiometric faceBiometric = new FaceBiometric(cloneNv21Ir, facePreviewInfoList.get(i));
                            int requestId = facePreviewInfoList.get(i).getTrackId();
                            mListFaceBiometricIr.put(requestId, faceBiometric);
                        }
                    }
                } else {
                    totalFaceInIrFrame = 0;
                }

                irData = null;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error processIrPreviewData " + ex.getMessage());
        }
    }

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();

        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            try {
                if (supportTemperatureCamera && temperatureUtil != null) {
                    if(temperature != null && !temperature.isEmpty()){
                        double tem = Double.parseDouble((temperature == null || temperature.isEmpty()) ? "0" : temperature);
                        if (tem > prefTemperatureThreshold) {
                            txtTemperature.setBackgroundResource(R.drawable.rounded_edge_red);
                        } else {
                            txtTemperature.setBackgroundResource(R.drawable.rounded_edge_green);
                        }

                        if(compareResultList.isEmpty()){
                            txtTemperature.setText(LanguageUtils.getString(R.string.label_body_temperature) + ": " + temperature + " ℃");
                        }

                        //Temperature out off range
                        if(tem >= 40){
                            txtTemperature.setText(tempDescription);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (LivenessLevelUtils.getLivenessLevel().getLevelValue() != getResources().getInteger(R.integer.liveness_level_no_value)) {
                drawInfoList.add(new DrawInfo(
                        drawHelperRgb.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()),
                        GenderInfo.UNKNOWN,
                        AgeInfo.UNKNOWN_AGE,
                        isLiveness ? LivenessInfo.ALIVE : LivenessInfo.NOT_ALIVE,
                        ""));
            } else {
                drawInfoList.add(new DrawInfo(
                        drawHelperRgb.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()),
                        GenderInfo.UNKNOWN,
                        AgeInfo.UNKNOWN_AGE,
                        LivenessInfo.ALIVE,
                        ""));
            }
        }

        //Vẽ ô vuông
        drawHelperRgb.draw(faceRectView, drawInfoList);
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
                if (!prefActiveAlready) {
                    startActivity(new Intent(this, LicenseActivity.class));
                    return;
                }

                if(!isEngineInited){
                    initEngine();
                    initIrEngine();
                }

                initRgbCamera();
                initIrCamera();

                if (cameraHelper != null) {
                    cameraHelper.start();
                }

                if (cameraHelperIr != null) {
                    cameraHelperIr.start();
                }
            } else {
                Toast.makeText(this, LanguageUtils.getString(R.string.message_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Xóa các khuôn mặt còn lại
     *
     * @param facePreviewInfoList Danh sách khuôn mặt và trackId
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureRgbStatusMap.keySet();
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureRgbStatusMap.clear();
            return;
        }

        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureRgbStatusMap.remove(integer);
            }
        }
    }

    private void searchFaceOffline(final FaceFeature frFace, final Integer requestId) {
        Observable
                .create((ObservableOnSubscribe<CompareResult>) emitter -> {
                    CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
                    if (compareResult == null) {
                        emitter.onError(null);
                    } else {
                        emitter.onNext(compareResult);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        //Kiểm tra giả mạo
                        if(LivenessLevelUtils.getLivenessLevel().getLevelValue() == getResources().getInteger(R.integer.liveness_level_medium_value) && !isLiveness){
                            checkLivenessRGB();
                            if(!isLiveness){
                                requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                                requestFeatureIrStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                                mListFaceBiometricRgb.clear();
                                mListFaceBiometricIr.clear();
                                return;
                            }
                        }

                        if(LivenessLevelUtils.getLivenessLevel().getLevelValue() == getResources().getInteger(R.integer.liveness_level_high_value)){
                            checkLivenessIr();
                            //boolean isOnePerson = compareFaceInTwoCamera();
                            //if(!isLiveness || !isOnePerson){
                            if(!isLiveness){
                                requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                                requestFeatureIrStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                                mListFaceBiometricRgb.clear();
                                mListFaceBiometricIr.clear();
                                return;
                            }
                        }
                        //End

                        //Capture khuôn mặt tại thời điểm check
                        FaceBiometric faceBiometric = mListFaceBiometricRgb.get(requestId);
                        if (faceBiometric == null) {
                            return;
                        }
                        byte[] mNv21 = faceBiometric.getFaceData();
                        FacePreviewInfo facePreviewInfo = faceBiometric.getFacePreviewInfo();
                        FaceInfo mFaceInfo = facePreviewInfo.getFaceInfo();

                        int mask = facePreviewInfo.getMask();
                        if (mask == MaskInfo.UNKNOWN) {
                            mListFaceBiometricRgb.clear();
                            return;
                        }
                        //end

                        //Khi hiển thị mọi người, hãy lưu trackId của họ
                        mRequestId = requestId;
                        compareResult.setTrackId(requestId);
                        compareResult.setMask(mask == MaskInfo.WORN ? true : false);

                        FaceInfoCapture faceInfoCapture = new FaceInfoCapture(mNv21, mFaceInfo);
                        hmFaceCapture.put(requestId, faceInfoCapture);

                        mListFaceBiometricRgb.clear();
                        mListFaceBiometricIr.clear();
                        //End capture khuôn mặt tại thời điểm check

                        //Xử lý kết quả (hiển thị + audio + mở cửa)
                        float flTemperature = Float.parseFloat((temperature == null || temperature.equals("")) ? "0" : temperature);
                        final String personId = compareResult.getPersonId();
                        compareResult.setTemperature(flTemperature);

                        if (compareResult.getSimilar() < prefSimilarThreshold) {
                            processFaceNotFound(compareResult, requestId);
                            requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                            return;
                        }

                        if(LivenessLevelUtils.getLivenessLevel().getLevelValue() == getResources().getInteger(R.integer.liveness_level_high_value)){
                            if(compareFaceInTwoCamera()){
                                processFaceFound(compareResult);
                                requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            }else{
                                requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                            }
                        }else{
                            processFaceFound(compareResult);
                            requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {
                        requestFeatureRgbStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                    }
                });
    }

    private void processFaceNotFound(CompareResult compareResult, int requestId){
        int coundTime = ConfigUtil.getTotalTimeCheckFaceNotFound(RegisterAndRecognizeDualActivity.this);
        if (++coundTime > Constants.MAXTIME_RETRY_SEARCH_FACE) {
            coundTime = 0;
        }

        if (coundTime == Constants.MAXTIME_RETRY_SEARCH_FACE) {
            AccessResult accessResult = accessBussiness.checkPersonNotFound(compareResult.isMask(), 0, prefMachineFunction);
            compareResult.setSummaryCode(accessResult.getSumaryCode());
            compareResult.setDetailCode(accessResult.getDetailCode());

            logger.info("Face not found - Score: " + compareResult.getSimilar()
                    + " - PersonId: " + compareResult.getPersonId()
                    + " - Errorcode: " + compareResult.getSummaryCode() + "-" + compareResult.getPersonCode()
            );
            processResult(compareResult, true);
        }

        ConfigUtil.setTotalTimeCheckFaceNotFound(RegisterAndRecognizeDualActivity.this, coundTime);
    }

    private void processFaceFound(CompareResult compareResult){
        String personId = compareResult.getPersonId();

        List<CompareResult> lsTwins = accessBussiness.getTwins(personId);
        if(lsTwins.isEmpty()){
            logger.info("Face matched "
                    + " - Score: " + compareResult.getSimilar()
                    + " - PersonId: " + compareResult.getPersonId()
            );

            checkPersonPermission(personId, compareResult, 0);
        }else {
            logger.info("Show twins "
                    + " - Score: " + compareResult.getSimilar()
                    + " - PersonId: " + compareResult.getPersonId()
            );

            showTwins (compareResult, 0,  lsTwins);
        }
    }

    private void checkLivenessRGB() {
        Set<Integer> setRequest = mListFaceBiometricRgb.keySet();
        if (setRequest == null) {
            isLiveness = false;
            return;
        }
        List<Integer> lstRequest = new ArrayList<>(setRequest);
        if (lstRequest.size() == 0) {
            isLiveness = false;
            return;
        }

        int requestId = lstRequest.get(lstRequest.size() - 1);
        try {
            requestId = lstRequest
                    .stream()
                    .mapToInt(v -> v)
                    .max().orElseThrow(NoSuchElementException::new);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }

        FaceBiometric faceBiometric = mListFaceBiometricRgb.get(requestId);
        if (faceBiometric == null) {
            isLiveness = false;
            return;
        }
        byte[] mNv21 = faceBiometric.getFaceData();
        FacePreviewInfo facePreviewInfo = faceBiometric.getFacePreviewInfo();
        FaceInfo mFaceInfoRgb = facePreviewInfo.getFaceInfo();
        LivenessInfo livenessInfo = faceHelperRgb.getLiveness(mFaceInfoRgb, mNv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfo.getTrackId());
        if (livenessInfo == null || livenessInfo.getLiveness() != LivenessInfo.ALIVE) {
            isLiveness = false;
            return;
        }
        //end
        isLiveness = true;
    }

    private void checkLivenessIr() {
        Set<Integer> setRequest = mListFaceBiometricIr.keySet();
        if (setRequest == null) {
            isLiveness = false;
            return;
        }
        List<Integer> lstRequest = new ArrayList<>(setRequest);
        if (lstRequest.size() == 0) {
            isLiveness = false;
            return;
        }

        int requestId = lstRequest.get(lstRequest.size() - 1);
        try {
            requestId = lstRequest
                    .stream()
                    .mapToInt(v -> v)
                    .max().orElseThrow(NoSuchElementException::new);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }

        FaceBiometric faceBiometric = mListFaceBiometricIr.get(requestId);
        if (faceBiometric == null) {
            isLiveness = false;
            return;
        }
        byte[] mNv21 = faceBiometric.getFaceData();
        FacePreviewInfo facePreviewInfo = faceBiometric.getFacePreviewInfo();
        FaceInfo mFaceInfoRgb = facePreviewInfo.getFaceInfo();
        LivenessInfo livenessInfo = faceHelperIr.getLiveness(mFaceInfoRgb, mNv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfo.getTrackId());
        if (livenessInfo == null || livenessInfo.getLiveness() != LivenessInfo.ALIVE) {
            isLiveness = false;
            return;
        }
        //end
        isLiveness = true;
    }

    private boolean compareFaceInTwoCamera() {
        if (totalFaceInIrFrame == 0) {
            return false;
        }
        float compareScore = FaceServer.getInstance().compareFeature(rgbFeature, irFeature);
        if (compareScore <= 0.8) {
            return false;
        }
        return true;
    }

    private void checkPersonPermission(String personId, CompareResult cmp, float temp) {
        accessBussiness.checkPermission(cmp, personId);
    }

    public void processResult(CompareResult compareResult, boolean faceNotFound) {
        try {
            boolean isError = false;
            String sumaryCode = compareResult.getSummaryCode();
            String detailCode = compareResult.getDetailCode();

            if (sumaryCode.equalsIgnoreCase(ErrorCode.COMMON_CONFIG_ERROR)) {
                isError = true;
                sumaryCode = ErrorCode.COMMON_CONFIG_ERROR;
                detailCode = ErrorCode.COMMON_CONFIG_ERROR;
            }

            final boolean isErrorFinal = isError;
            final String sCodeFinal = sumaryCode;
            final String dCodeFinal = detailCode;

            //Lưu log
            if (((faceNotFound && prefSavePersonUnreg) || !faceNotFound) && !isError) {
                excutorLog.execute(() -> insertLog(compareResult));
            }

            //Hiển thị dialog
            runOnUiThread(() -> showResult(compareResult, isErrorFinal));

            //Sound
            if (prefUseSound) {
                excutorSound.execute(() -> EmitSound.openSound(getApplicationContext(), sCodeFinal, dCodeFinal));
            }

            //Relay
            if(faceNotFound || (!faceNotFound && (!dCodeFinal.equals(ErrorCode.CHECKIN_VALID)
                    && !dCodeFinal.equals(ErrorCode.CHECKOUT_VALID)
                    && !dCodeFinal.equals(ErrorCode.TIMEKEEPING_VALID)))){
                //Không làm gì
            }else{
                DoorType doorType = DoorTypeUtils.getDoorType();
                if(doorType.getValue() == Application.getInstance().getResources().getInteger(R.integer.door_type_with_controller_value)){
                    excutorRelay.execute(() -> Hardware.openDoor(sCodeFinal, 1000, Build.MODEL, getApplicationContext()));
                }else{
                    excutorRelay.execute(() -> {
                        Calendar cal = Calendar.getInstance();
                        Hardware.openDoorOnly(sCodeFinal, Build.MODEL, getApplicationContext());
                    });
                }
            }

            //Signal light
            if (prefUseLed) {
                excutorLed.execute(() -> Hardware.openLed(sCodeFinal, 2000, Build.MODEL, getApplicationContext()));
            }
        } catch (Exception ex) {
            logger.error("Error processResult " + ex.getMessage());
        }
    }

    public void showDialogConfirmCanteen(CompareResult compareResult) {
        try {
            String sumaryCode = compareResult.getSummaryCode();
            String detailCode = compareResult.getDetailCode();

            //Hiển thị dialog
            runOnUiThread(() -> showResult(compareResult, false));

            //Sound
            if (prefUseSound) {
                excutorSound.execute(() -> EmitSound.openSound(getApplicationContext(), sumaryCode, detailCode));
            }
        } catch (Exception ex) {
            logger.error("Error processResult " + ex.getMessage());
        }
    }

    private synchronized void showResult(CompareResult compareResult, boolean isError) {
        try {
            //Cập nhật thông tin cho list kết quả để hiển thị ra màn hình
            PersonDB personInfoDb = database.getPerson(compareResult.getPersonId());
            if (personInfoDb != null) {
                compareResult.setFullName(personInfoDb.getFullName());
                compareResult.setPosition(personInfoDb.getPosition());
                compareResult.setJobDuties(personInfoDb.getJobDuties());
                compareResult.setPersonCode(personInfoDb.getPersonCode());
                compareResult.setVaccine(personInfoDb.getVaccine());
            }

            logger.info("Show result"
                    + " - PersonId: " + compareResult.getPersonId()
                    + " - PersonCode: " + compareResult.getPersonCode()
                    + " - PersonName: " + compareResult.getFullName()
            );

            compareResultList.clear();
            compareResultList.add(compareResult);

            adapter.notifyItemInserted(1);
            adapter.notifyDataSetChanged();

            displayTemperature(true);

            ConfigUtil.setDialogResultShowTime(this, StringUtils.currentDatetimeMilisecondSQLiteformat());
            mTempRequestTime = ConfigUtil.getDialogResultShowTime(getContext());
            prefDelayRecognizeTime = pref.getInt(Constants.PREF_DELAY_RECOGNIZE_TIME, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void displayTemperature(boolean doShow) {
        if (!doShow) {
            txtTemperature.setText("");
            txtTemperature.setVisibility(View.INVISIBLE);
            vBlurBehind.setVisibility(View.INVISIBLE);
            return;
        }

        vBlurBehind.setVisibility(View.VISIBLE);

        if (supportTemperatureCamera && temperatureUtil != null) {
            if (temperature != null && !temperature.isEmpty() && txtTemperature.getVisibility() != View.VISIBLE) {
                double tem = Double.parseDouble((temperature == null || temperature.isEmpty()) ? "0" : temperature);
                txtTemperature.setVisibility(View.VISIBLE);
                if (tem > prefTemperatureThreshold) {
                    txtTemperature.setBackgroundResource(R.drawable.rounded_edge_red);
                } else {
                    txtTemperature.setBackgroundResource(R.drawable.rounded_edge_green);
                }
                txtTemperature.setText(LanguageUtils.getString(R.string.label_body_temperature) + ": " + temperature + " ℃");

                //Temperature out off range
                if (tem >= 40) {
                    txtTemperature.setBackgroundResource(R.drawable.rounded_edge_red);
                    txtTemperature.setText("TOO HOT");
                }
            }
        }
    }

    //Insert event Log
    private void insertLog(CompareResult compareResult) {
        EventDB model = new EventDB();

        try {
            String currentDate = StringUtils.currentDateSQLiteformat();
            String currentTime = StringUtils.currentDatetimeSQLiteformat();
            String eventId = UUID.randomUUID().toString();

            logger.info("Start insert log - EventId: " + eventId + " - PersonId: " + compareResult.getPersonId());

            byte[] bFace = new byte[0];
            try{
                FaceInfoCapture faceInfoCapture = hmFaceCapture.get(compareResult.getTrackId());
                bFace = FaceServer.getInstance().captureFacePhoto(faceInfoCapture.getFaceCapture(), faceInfoCapture.getFaceInfo(), previewSize);
            }catch (Exception ex){
                bFace = new byte[0];
            }

            if (EVENT_PATH == null) {
                EVENT_PATH = getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "event";
            }

            File eventFolder = new File(EVENT_PATH);
            if (!eventFolder.exists()) {
                eventFolder.mkdirs();
            }

            File imageFile = new File(EVENT_PATH + File.separator + eventId + ".jpg");
            try {
                BaseUtil.saveImageFile(imageFile, bFace);
            } catch (Exception ex) {
                logger.error("Error save image " + ex.getMessage());
            }
            String personId = compareResult.getPersonId();
            String faceId = "";
            if (personId == null || personId.equals("") || personId.equals(Constants.DEFAULT_PERSON_ID_NOT_FOUND)) {
                faceId = Constants.DEFAULT_PERSON_ID_NOT_FOUND;
            } else {
                FaceDB faceDB = database.getFaceByPerson(compareResult.getPersonId());
                faceId = faceDB.getFaceId();
            }

            model.setEventId(eventId);
            model.setPersonId(compareResult.getPersonId());
            //model.setFaceId(faceId);
            model.setFacePath(imageFile.getAbsolutePath());
            model.setMachineId(thisDevice.getMachineId());
            model.setAccessDate(currentDate);
            model.setAccessTime(currentTime);
            model.setAccessType(String.valueOf(Constants.FACE_RECOGNIZE));
            model.setScoreMatch(compareResult.getSimilar());
            model.setTemperature(compareResult.getTemperature());
            model.setWearMask(compareResult.isMask() ? 1 : 0);
            model.setErrorCode(compareResult.getDetailCode());
            model.setStatus(Constants.EVENT_STATUS_WAIT_SYNC);
            model.setCompId(thisDevice.getCompId());
            model.setNote(performanceNote);
            database.addEvent(model);
            logger.info("Insert database success - EventId: " + eventId);

            if(prefDisplayRealTime){
                logger.info("Post log realtime - EventId: " + eventId);
                threadSynchronizeEventLog.sendLogRealtime(model);
            }
        } catch (Exception ex) {
            logger.error("Error insertLog " + ex.getMessage());
        }
    }

    //Broadcast nhận intent reinitReferenceValue
    public class InitPreferenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INIT_PREFERENCE)) {
                initPreferenceValue();
            }

            if (intent.getAction().equals(Constants.ALARM_REBOOT_OS)) {
                String msg = intent.getStringExtra("msg");
                long intervalMillis = intent.getLongExtra("intervalMillis", 0);
                if (intervalMillis != 0) {
                    AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis, intent);
                }
                int flag = intent.getIntExtra("soundOrVibrator", 0);
                Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
                clockIntent.putExtra("msg", msg);
                clockIntent.putExtra("flag", flag);
                clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(clockIntent);
            }

            if (intent.getAction().equals(Constants.SHOW_NOTIFICATION)) {
                runOnUiThread(() -> {
                    String msg = intent.getStringExtra("msg");
                    txtNotification.setVisibility(View.VISIBLE);
                    txtNotification.setText(msg);
                });
            }

            if (intent.getAction().equals(Constants.SYNCHRONIZE_STATUS)) {
                runOnUiThread(() -> {
                    boolean msg = intent.getBooleanExtra("msg", false);
                    updateSynchronizeStatus(msg);
                });
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            performanceNote = StringUtils.nvl(intent.getStringExtra("cpuMem"));

            try {
                String[] arrCPU = performanceNote.split("#");
                float cpu = Float.parseFloat(arrCPU[0]);
                if (cpu >= 99) {
                    LogResponseServer.getInstance(context).responseLog("Warning CPU reload resource " + cpu);
                    onStop();
                    onResume();
                }

                // Gửi chuỗi từ main thread tới Service
                if(serviceMessenger != null){
                    Message message = Message.obtain(null, PerformanceInforService.MSG_SEND_STRING, performanceNote);
                    serviceMessenger.send(message);
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            } finally {
                Log.d("CPU", performanceNote);
            }
        }
    };

    public void displayTemperateCamera() {
        while (true) {
            temperatureData = temperatureUtil.getDataAndBitmap(70, true, new HotImageCallback.Stub() {
                @Override
                public void onTemperatureFail(String e) {
                    try{
                        TemperatureError temperatureError = gson.fromJson(e, TemperatureError.class);
                        temperature = temperatureError.getTemperature();

                        if (temperatureError.getErr().contains("too hot")) {
                            tempDescription = "TOO HOT";
                        }
                    }catch (Exception ex){
                    }
                }

                @Override
                public void getTemperatureBimapData(final TemperatureBitmapData data) {
                    runOnUiThread(() -> imgTemperaturePhoto.setImageBitmap(data.getBitmap()));
                    if (temperatureData != null) {
                        temperature = String.valueOf(temperatureData.getTemperature());
                    }
                }
            });
        }
    }

    public void hideResultDialog() {
        while (true) {
            try {
                long along = 0;
                String currentTime = StringUtils.currentDatetimeMilisecondSQLiteformat();

                //Tự tắt dialog sau MAX_TIME_SHOW_DIALOG time
                try {
                    String previewPopupTime = ConfigUtil.getDialogResultShowTime(getContext());
                    along = StringUtils.getBetweenSecond(previewPopupTime, currentTime);
                    if (along >= prefDelayRecognizeTime + 500) {
                        if (!compareResultList.isEmpty()) {
                            runOnUiThread(() -> {
                                if (previewPopupTime.equals(mTempRequestTime)) {
                                    resetVariable();
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //Tự tắt đèn led sau MAX_TIME_SHOW_DIALOG time
                String lastTimeFaceAppeared = ConfigUtil.getLastTimeFaceAppeared(RegisterAndRecognizeDualActivity.this);
                try {
                    along = StringUtils.getBetweenSecond(lastTimeFaceAppeared, currentTime);
                    if (along >= prefDelayRecognizeTime + 500) {
                        Hardware.turnLight(0, Constants.LED_GREEN, Build.MODEL);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //Tự tắt sleep sau 30s nếu có bật auto sleep
                along = StringUtils.getBetweenSecond(lastTimeFaceAppeared, currentTime);
                if (along >= 30*1000) {
                    adjustScreenBrightness(Constants.ScreenBrightnessValue.MIN);
                }

                //Clear map cache face frame
                List<Integer> listRequestId = new ArrayList<Integer>();
                hmFaceCapture.keySet().iterator().forEachRemaining(listRequestId::add);
                for (int i = 0; i < listRequestId.size(); i++) {
                    int requestId = listRequestId.get(i);
                    if (requestId < mRequestId - 10 && hmFaceCapture.containsKey(requestId)) {
                        hmFaceCapture.remove(requestId);
                    }
                }
                //End

                Thread.sleep(250);
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e);
            }
        }
    }

    public void manageFrameRateRGB() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception ex) {
                Log.e(TAG, "Error manageFrameRateRgb: " + ex.getMessage());
            }
        }
    }

    public void hideDialog(){
        resetVariable();
    }

    private void resetVariable() {
        compareResultList.clear();
        adapter.notifyDataSetChanged();

        displayTemperature(false);

        requestFeatureRgbStatusMap.clear();
        requestFeatureIrStatusMap.clear();

        temperature = "";
        txtTemperature.setText("");
        txtNotification.setText("");
    }

    private void turnOnLight() {
        excutorLed.execute(() -> {
            try {
                Hardware.turnLight(1, Constants.LED_GREEN, Build.MODEL);
                ConfigUtil.setLastTimeFaceAppeared(RegisterAndRecognizeDualActivity.this, StringUtils.currentDatetimeSQLiteformat());
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        });
    }

    private void adjustScreenBrightness(float value){
        //Không sử dụng bảo vệ màn hình
        if(!prefAutoSleep){
            return;
        }

        runOnUiThread(() -> {
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = value;
            getWindow().setAttributes(layout);
        });
    }

    private void internetStatus(){
        while(true){
            try{
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm == null){
                    continue;
                }

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnected()){
                    runOnUiThread(() -> imgIntenet.setImageResource(R.drawable.internet_on));
                }else{
                    runOnUiThread(() -> imgIntenet.setImageResource(R.drawable.internet_off));
                }

                TimeUnit.SECONDS.sleep(20);
            }catch (InterruptedException ex){
                break;
            }
        }
    }

    private void cloudApiStatus(String url){
        while(true){
            try{
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                        response -> {
                            runOnUiThread(() -> imgDefaultStatus.setImageResource(R.drawable.cloud_done));
                        },
                        error -> {
                            runOnUiThread(() -> imgDefaultStatus.setImageResource(R.drawable.cloud_off));
                            String cause = StringUtils.getThrowCause(error);
                            Log.e(TAG, cause);
                        }) {
                };
                VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
                TimeUnit.SECONDS.sleep(20);
            }catch (InterruptedException ex){
                break;
            }
        }
    }

    private void printerStatus(){
        while(true){
            try{
                if(prefMachineFunction != Constants.CANTEEN){
                    printStatus.setVisibility(View.GONE);
                }else{
                    printStatus.setVisibility(View.VISIBLE);
                    boolean printerIsConnected = PrinterHelper.getInstance(getApplicationContext()).isConnected();
                    runOnUiThread(() -> printStatus.setImageResource(printerIsConnected ? R.drawable.ic_printer_connected : R.drawable.ic_printer_disconnected));
                }
                TimeUnit.SECONDS.sleep(3);
            }catch (InterruptedException ex){
                break;
            }
        }
    }

    public void updateSynchronizeStatus(boolean isSynch){
        if(isSynch){
            vSyncStatus.setVisibility(View.VISIBLE);
        }else{
            vSyncStatus.setVisibility(View.GONE);
        }
    }

    private void showTwins (CompareResult compareResult, float flTemperature, List<CompareResult> lsTwins){
        final DialogListener dialogListener = new DialogListener() {
            @Override
            public void onShow() {
                twinDialog.setVisible(true);
            }

            @Override
            public void onClose() {
                logger.info("Twins dialog close - PersonId: " + compareResult.getPersonId());
                twinDialog.setVisible(false);
                onResume();
            }

            @Override
            public void onResponse(Object itemSelected) {
                twinDialog.setVisible(false);
                CompareResult personSelected = (CompareResult) itemSelected;
                logger.info("Twins dialog selected - PersonId: " + personSelected.getPersonId());

                compareResult.setPersonId(personSelected.getPersonId());
                compareResult.setFullName(personSelected.getFullName());
                compareResult.setPosition(personSelected.getPosition());
                compareResult.setJobDuties(personSelected.getJobDuties());
                compareResult.setPersonCode(personSelected.getPersonCode());
                compareResult.setPersonType(personSelected.getPersonType());
                compareResult.setFacePath(personSelected.getFacePath());

                checkPersonPermission(personSelected.getPersonId(), compareResult, flTemperature);
            }
        };

        if(twinDialog != null && twinDialog.isVisible()){
            return;
        }

        twinDialog = new TwinDialog(RegisterAndRecognizeDualActivity.this, dialogListener);
        twinDialog.showDialog(lsTwins);
        EmitSound.openSoundSpecial(RegisterAndRecognizeDualActivity.this, ErrorCode.SPECIAL_CONFIRM_TWINS);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceMessenger = null;
        }
    };

    public void confirmCanteen(CompareResult compareResult) {
        try {
            compareResult.setPreviewSize(previewSize);
            compareResult.setMachineId(thisDevice.getMachineId());

            accessBussiness.confirmCanteenUsageLocal(compareResult);


            processResult(compareResult, true);


            compareResultList.clear();
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Lỗi xác nhận suất ăn: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    public void updateWaitDialogTime(int delayTime){
        prefDelayRecognizeTime = delayTime;
    }
}
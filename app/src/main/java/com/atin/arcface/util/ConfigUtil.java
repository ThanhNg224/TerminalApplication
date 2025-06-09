package com.atin.arcface.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.StringRes;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.Hardware;
import com.atin.arcface.model.CircleWraper;
import com.atin.arcface.model.DoorType;
import com.atin.arcface.model.InitEngineFailureLog;
import com.atin.arcface.model.Language;
import com.atin.arcface.model.LivenessLevel;
import com.atin.arcface.model.LockSearchModel;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.model.MachineFunction;
import com.atin.arcface.service.SingletonObject;

public class ConfigUtil {
    private static final String APP_NAME = "ArcFaceDemo";
    private static final String TRACKED_FACE_COUNT = "trackedFaceCount";
    private static final String TRACKED_FACE_IR_COUNT = "trackedFaceIrCount";
    private static final String TRACK_ID = "trackID";
    private static final String SYNCING_DATA = "syncingData";
    private static final String DIALOG_RESULT_SHOW_TIME = "dialogResultShowTime";
    private static final String TOTAL_TIME_CHECK_FACE_NOT_FOUND = "totalTimeCheckFaceNotFound";
    private static final String LAST_TIME_FACE_APPEARED = "lastTimeFaceAppeared";
    private static final String NETWORK_ERROR_TIME = "networdErrorTime";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String PCCOVID = "PCCOVID";
    private static final String MACHINE_FUNCTION = "MACHINE_FUNCTION";
    private static final String LIVENESS_LEVEL = "LIVENESS_LEVEL";
    private static final String DOOR_TYPE = "DOOR_TYPE";
    private static final String MACHINE = "MACHINE";
    private static final String PASSWORD_USER_LOGIN = "PASSWORD_USER_LOGIN";
    private static final String QRCODE_LAST_TIME = "qrcodeLastTime";
    private static final String NORMAL_OPEN = "NORMAL_OPEN";
    private static final String CIRCLE_WRAPER = "CIRCLE_WRAPER";
    private static final String INIT_ENGINE_FAIURE = "INIT_ENGINE_FAIURE";
    private static final String NETWORK_AVAILABLE = "NETWORK_AVAILABLE";
    private static final String LAST_REBOOT_TIME = "LAST_REBOOT_TIME";
    private static final String LOCK_TIME_SEARCH = "LOCK_TIME_SEARCH";

    /**
     * Ngưỡng giả mạo RGB
     */
    private static final float RECOMMEND_RGB_LIVENESS_THRESHOLD = 0.50f;
    /**
     * Ngưỡng giả mạo IR
     */
    private static final float RECOMMEND_IR_LIVENESS_THRESHOLD = 0.70f;
    /**
     * 活体 FQ 检测阈值
     */
    private static final float RECOMMEND_LIVENESS_FQ_THRESHOLD = 0.65f;
    /**
     * Ngưỡng điểm chất lượng ảnh tối thiểu không có khẩu trang đủ để nhận diện
     */
    public static final float IMAGE_QUALITY_NO_MASK_RECOGNIZE_THRESHOLD = 0.55f;//0.49f;
    /**
     * Ngưỡng điểm chất lượng ảnh tối thiểu không có khẩu trang đủ để đăng ký
     */
    public static final float IMAGE_QUALITY_NO_MASK_REGISTER_THRESHOLD = 0.63f;
    /**
     * Ngưỡng điểm chất lượng ảnh tối thiểu khi có khẩu trang đủ để nhận diện
     */
    public static final float IMAGE_QUALITY_MASK_RECOGNIZE_THRESHOLD = 0.3f;//0.29f;
    /**
     * Ngưỡng điểm chất lượng ảnh tối thiểu khi có khẩu trang đủ để nhận diện
     */
    public static final float COMPARE_IR_AND_RGB_THRESHOLD = 0.7f;

    /**
     * Kích thước khuôn mặt tối thiểu khi sử dụng nhận diện
     */
    private static final int RECOMMEND_FACE_SIZE_LIMIT = 160;
    /**
     * Kích thươc khuôn mặt tối thiểu khi nhận diện từ video
     */
    private static final int RECOMMEND_FACE_MOVE_LIMIT = 20;
    /**
     * Số lượng khuôn mặt tối đa được phát hiện cùng lúc
     */
    private static final int DEFAULT_MAX_DETECT_FACE_NUM = 1;
    /**
     * Ti lệ kích thước khuôn mặt
     */
    private static final int DEFAULT_SCALE = 16;
    /**
     * Độ phân giải camera mặc định
     */

    public static boolean setLockTimeSearch(LockSearchModel model) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(LOCK_TIME_SEARCH, SingletonObject.getInstance(context).getGSon().toJson(model))
                .commit();
    }

    public static LockSearchModel getLockTimeSearch() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(LOCK_TIME_SEARCH, ""), LockSearchModel.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setLastRebootTime() {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(LAST_REBOOT_TIME, StringUtils.currentDatetimeMilisecondSQLiteformat())
                .commit();
    }

    public static String getLastRebootTime() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            return sharedPreferences.getString(LAST_REBOOT_TIME, "1900-01-01 00:00:00");
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setInitEngineFailure(InitEngineFailureLog input) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(INIT_ENGINE_FAIURE, SingletonObject.getInstance(context).getGSon().toJson(input))
                .commit();
    }

    public static InitEngineFailureLog getInitEngineFailure() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(INIT_ENGINE_FAIURE, ""), InitEngineFailureLog.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setNetworkAvailable(boolean status) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putBoolean(NETWORK_AVAILABLE, status)
                .commit();
    }

    public static boolean getNetworkAvailable() {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NETWORK_AVAILABLE, false);
    }

    public static boolean setMachine(MachineDB machineDB) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(MACHINE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(MACHINE, SingletonObject.getInstance(context).getGSon().toJson(machineDB))
                .commit();
    }

    public static MachineDB getMachine() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(MACHINE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(MACHINE, ""), MachineDB.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setCircleWraper(CircleWraper circleWraper) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(CIRCLE_WRAPER, SingletonObject.getInstance(context).getGSon().toJson(circleWraper))
                .commit();
    }

    public static CircleWraper getCircleWraper() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(CIRCLE_WRAPER, ""), CircleWraper.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setNormalOpenDoor(Context context, boolean status) {
        if (context == null) {
            return false;
        }

        if(status){
            Hardware.turnDoor(1, Build.MODEL, context); //Mở cửa
        }else{
            Hardware.turnDoor(0, Build.MODEL, context); //Đóng cửa
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(NORMAL_OPEN, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putBoolean(NORMAL_OPEN, status)
                .commit();
    }

    public static boolean getNormalOpenDoor (Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(NORMAL_OPEN, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NORMAL_OPEN, false);
    }

    public static boolean setQRCodeLastTime(Context context, String time) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(QRCODE_LAST_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(QRCODE_LAST_TIME, time)
                .commit();
    }

    public static String getQRCodeLastTime (Context context) {
        if (context == null) {
            return StringUtils.currentDatetimeSQLiteformat();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(QRCODE_LAST_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(QRCODE_LAST_TIME, StringUtils.currentDatetimeSQLiteformat());
    }

    public static boolean setPasswordLogin(Context context, String password) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PASSWORD_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(PASSWORD_USER_LOGIN, password)
                .commit();
    }

    public static String getPasswordLogin(Context context) {
        if (context == null) {
            return StringUtils.currentDatetimeSQLiteformat();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PASSWORD_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_USER_LOGIN, "");
    }

    public static int getFirmwareNumber (Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean setMachineFunction(MachineFunction machineFunction) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(MACHINE_FUNCTION, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(MACHINE_FUNCTION, SingletonObject.getInstance(context).getGSon().toJson(machineFunction))
                .commit();
    }

    public static MachineFunction getMachineFunction() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(MACHINE_FUNCTION, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(MACHINE_FUNCTION, ""), MachineFunction.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setLivenessLevel(LivenessLevel model) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(LIVENESS_LEVEL, SingletonObject.getInstance(context).getGSon().toJson(model))
                .commit();
    }

    public static LivenessLevel getLivenessLevel() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(LIVENESS_LEVEL, ""), LivenessLevel.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setDoorType(DoorType model) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(DOOR_TYPE, SingletonObject.getInstance(context).getGSon().toJson(model))
                .commit();
    }

    public static DoorType getDoorType() {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(Constants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(DOOR_TYPE, ""), DoorType.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setLanguage(Language language) {
        SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE);
        Context context = Application.getInstance().getApplicationContext();
        return sharedPreferences.edit()
                .putString(LANGUAGE, SingletonObject.getInstance(context).getGSon().toJson(language))
                .commit();
    }

    public static Language getLanguage () {
        try{
            SharedPreferences sharedPreferences = Application.getInstance().getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE);
            Context context = Application.getInstance().getApplicationContext();
            return SingletonObject.getInstance(context).getGSon().fromJson(sharedPreferences.getString(LANGUAGE, ""), Language.class);
        }catch (Exception ex){
            return null;
        }
    }

    public static boolean setTotalTimeCheckFaceNotFound(Context context, int time) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOTAL_TIME_CHECK_FACE_NOT_FOUND, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putInt(TOTAL_TIME_CHECK_FACE_NOT_FOUND, time)
                .commit();
    }

    public static int getTotalTimeCheckFaceNotFound (Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOTAL_TIME_CHECK_FACE_NOT_FOUND, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TOTAL_TIME_CHECK_FACE_NOT_FOUND, 0);
    }

    public static boolean setDialogResultShowTime(Context context, String showTime) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(DIALOG_RESULT_SHOW_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(DIALOG_RESULT_SHOW_TIME, showTime)
                .commit();
    }

    public static String getDialogResultShowTime (Context context) {
        if (context == null) {
            return StringUtils.currentDatetimeSQLiteformat();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(DIALOG_RESULT_SHOW_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DIALOG_RESULT_SHOW_TIME, StringUtils.currentDatetimeSQLiteformat());
    }

    public static boolean setLastTimeFaceAppeared(Context context, String showTime) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(LAST_TIME_FACE_APPEARED, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(LAST_TIME_FACE_APPEARED, showTime)
                .commit();
    }

    public static String getLastTimeFaceAppeared (Context context) {
        if (context == null) {
            return StringUtils.currentDatetimeSQLiteformat();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(LAST_TIME_FACE_APPEARED, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_TIME_FACE_APPEARED, StringUtils.currentDatetimeSQLiteformat());
    }

    public static boolean setNetworkErrorTime(Context context, String showTime) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(NETWORK_ERROR_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putString(NETWORK_ERROR_TIME, showTime)
                .commit();
    }

    public static String getNetworkErrorTime (Context context) {
        if (context == null) {
            return StringUtils.currentDatetimeSQLiteformat();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(NETWORK_ERROR_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NETWORK_ERROR_TIME, "2000-01-01 00:00:00");
    }

    public static boolean setSyncingData(Context context, boolean isSyncing) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(SYNCING_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putBoolean(SYNCING_DATA, isSyncing)
                .commit();
    }

    public static boolean getSyncingData(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(SYNCING_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SYNCING_DATA, false);
    }

    public static boolean setTrackedFaceCount(Context context, int trackedFaceCount) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putInt(TRACKED_FACE_COUNT, trackedFaceCount)
                .commit();
    }

    public static int getTrackedFaceCount(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TRACKED_FACE_COUNT, 0);
    }

    public static boolean setTrackedFaceIrCount(Context context, int trackedFaceCount) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putInt(TRACKED_FACE_IR_COUNT, trackedFaceCount)
                .commit();
    }

    public static int getTrackedFaceIrCount(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TRACKED_FACE_IR_COUNT, 0);
    }

    public static int getTrackId(Context context){
        if (context == null){
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TRACK_ID,0);
    }

    public static String getAppId(Context context) {
        return getString(context, R.string.preference_app_id, BaseUtil.getAppId(Build.MODEL));
    }

    public static String getSdkKey(Context context) {
        return getString(context, R.string.preference_sdk_key, BaseUtil.getSdkKey(Build.MODEL));
    }

    public static String getActiveKey(Context context) {
        return getString(context, R.string.preference_active_key, "Constants.ACTIVE_KEY");
    }

    public static String getAppVersion(Context context) {
        return getString(context, R.string.preference_app_version, "1.0.1");
    }

    public static boolean commitAppId(Context context, String appId) {
        return commitString(context, R.string.preference_app_id, appId);
    }

    public static boolean commitSdkKey(Context context, String sdkKey) {
        return commitString(context, R.string.preference_sdk_key, sdkKey);
    }

    public static boolean commitActiveKey(Context context, String activeKey) {
        return commitString(context, R.string.preference_active_key, activeKey);
    }

    public static boolean commitAppVersion(Context context, String appVersion) {
        return commitString(context, R.string.preference_app_version, appVersion);
    }

    /**
     * Set preference value by string key
     *
     * @param context
     * @param keyRes
     * @param newValue
     * @return result
     */
    private static boolean commitString(Context context, @StringRes int keyRes, String newValue) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.edit()
                .putString(context.getString(keyRes), newValue)
                .commit();
    }

    /**
     * Set preference value by string key
     *
     * @param context
     * @param keyRes
     * @param defaultValue
     * @return preference
     */
    private static String getString(Context context, @StringRes int keyRes, String defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(keyRes);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Set preference value by int key
     *
     * @param context
     * @param keyRes   Id
     * @param newValue value
     * @return result
     */
    private static boolean commitInt(Context context, @StringRes int keyRes, int newValue) {
        if (context == null) {
            return false;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.edit()
                .putInt(context.getString(keyRes), newValue)
                .commit();
    }

    /**
     * Số lượng khuôn mặt tối đã được nhận diện trong một khung hình
     *
     * @param context
     * @return Số lượng khuôn mặt
     */
    public static int getRecognizeMaxDetectFaceNum(Context context) {
        try {
            return Integer.parseInt(getString(context, R.string.preference_recognize_max_detect_num, String.valueOf(DEFAULT_MAX_DETECT_FACE_NUM)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return DEFAULT_MAX_DETECT_FACE_NUM;
    }

    /**
     * Các ngưỡng điểm
     *
     * @param context
     * @return Ngưỡng điểm
     */
    public static float getRgbLivenessThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_rgb_liveness_threshold, String.valueOf(RECOMMEND_RGB_LIVENESS_THRESHOLD)));
    }

    public static float getIrLivenessThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_ir_liveness_threshold, String.valueOf(RECOMMEND_IR_LIVENESS_THRESHOLD)));
    }

    public static float getLivenessFqThreshold(Context context){
        return Float.parseFloat(getString(context, R.string.preference_liveness_fq_threshold, String.valueOf(RECOMMEND_LIVENESS_FQ_THRESHOLD)));
    }

    public static float getImageQualityNoMaskRecognizeThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_image_quality_no_mask_recognize_threshold,
                String.valueOf(IMAGE_QUALITY_NO_MASK_RECOGNIZE_THRESHOLD)));
    }

    public static float getImageQualityNoMaskRegisterThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_image_quality_no_mask_register_threshold,
                String.valueOf(IMAGE_QUALITY_NO_MASK_REGISTER_THRESHOLD)));
    }

    public static float getImageQualityMaskRecognizeThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_image_quality_mask_recognize_threshold,
                String.valueOf(IMAGE_QUALITY_MASK_RECOGNIZE_THRESHOLD)));
    }

    public static int getFaceSizeLimit(Context context) {
        return Integer.parseInt(getString(context, R.string.preference_recognize_face_size_limit, String.valueOf(RECOMMEND_FACE_SIZE_LIMIT)));
    }

    public static float getCompareIrAndRgbThreshold(Context context) {
        return Float.parseFloat(getString(context, R.string.preference_compare_ir_and_rgb_threshold_threshold,
                String.valueOf(COMPARE_IR_AND_RGB_THRESHOLD)));
    }
}

package com.atin.arcface.common;

import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.faceserver.*;
import com.atin.arcface.model.*;
import com.atin.arcface.model.SyncRequest;
import com.atin.arcface.service.LogPersonResponseServer;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.FilterZipFile;
import com.atin.arcface.util.LanguageUtils;
import com.atin.arcface.util.LivenessLevelUtils;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.MachineFunctionUtils;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.apache.commons.io.*;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class ProcessSynchronizeData {
    private static final String TAG = "ProcessSynchronizeData";
    private Context mContext;
    public static String ROOT_PATH;
    private String EVENT_PATH;
    public static String FACE_PATH;
    public static String FEATURE_PATH;
    public static String UPLOAD_PATH;
    private SharedPreferences pref;
    private AuthenticateServer authenticateServer;
    private Database database;
    private String prefServerDomain;
    private String httpPrefix;

    public ProcessSynchronizeData(Context context) {
        this.mContext = context;
        initValue();
        initDatabase();
    }

    private void initDatabase(){
        database = Application.getInstance().getDatabase();
    }

    private void initValue(){
        if (ROOT_PATH == null) {
            ROOT_PATH = mContext.getFilesDir().getAbsolutePath();
        }

        if (FEATURE_PATH == null){
            FEATURE_PATH = ROOT_PATH + File.separator + "data" + File.separator + "feature";
        }

        if (FACE_PATH == null){
            FACE_PATH = ROOT_PATH + File.separator + "data" + File.separator + "face";
        }

        if(UPLOAD_PATH == null ){
            UPLOAD_PATH = ROOT_PATH + File.separator + "upload";
        }

        File featurePath = new File(FEATURE_PATH);
        if (!featurePath.exists()) {
            featurePath.mkdirs();
        }

        File facePath = new File(FACE_PATH);
        if (!facePath.exists()) {
            facePath.mkdirs();
        }

        File uploadPath = new File(UPLOAD_PATH);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        pref = mContext.getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
        authenticateServer = new AuthenticateServer(mContext);

        prefServerDomain = pref.getString( Constants.PREF_BUSINESS_SERVER_HOST, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getServerUrlApi());
        if(prefServerDomain.contains(Constants.HTTP) || prefServerDomain.contains(Constants.HTTPS)){
            httpPrefix = "";
        }else{
            httpPrefix = Constants.HTTPS;
            if(BaseUtil.regexIpv4(prefServerDomain)){
                httpPrefix = Constants.HTTP;
            }
        }
    }

    public int onProcess(SyncRequest syncData) throws Exception {
        int result = Constants.ProcessResult.SUCCESS;

        int actionType = syncData.getActionType();
        int dataType = syncData.getDataType();
        switch (dataType){
            case Constants.SyncDataType.PERSON:
                try{
                    PersonDB person = parsePerson(syncData.getData());
                    synchronizePerson(person, actionType);
                    result = Constants.ProcessResult.RELOAD;
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                break;

            case Constants.SyncDataType.FACE:
                FaceDB face = parseFace(syncData.getData()); //OK
                synchronizeFace(face, actionType);
                result = Constants.ProcessResult.RELOAD;
                break;

            case Constants.SyncDataType.CARD:
                CardDB card = parseCard(syncData.getData()); //OK
                synchronizeCard(card, actionType);
                result = Constants.ProcessResult.RELOAD;
                break;

            case Constants.SyncDataType.MACHINE:
                MachineDB machine = parseMachine(syncData.getData()); //OK
                synchronizeMachine(machine, actionType);
                savePreferenceValue();

                Intent intent = new Intent(Constants.INIT_PREFERENCE);
                mContext.sendBroadcast(intent);
                break;

            case Constants.SyncDataType.PERSON_GROUP:
                for (String lineData: toArrayData(syncData.getData())) {
                    PersonGroupDB personGroup = parsePersonGroup(lineData); //OK
                    synchronizePersonGroup(personGroup, actionType);
                }
                break;

            case Constants.SyncDataType.GROUP_ACCESS:
                String[] arrayData  = toArrayData(syncData.getData());
                for (String lineData: arrayData) {
                    GroupAccessDB groupAccess = parseGroupAccess(lineData); //OK
                    synchronizeGroupAccess(groupAccess, actionType);
                }
                break;

            case Constants.SyncDataType.ACCESS_TIME_SEG:
                AccessTimeSegDB accessTimeSeg = parseAccessTimeSeg(syncData.getData()); //OK
                synchronizeAccessTimeSeg(accessTimeSeg, actionType);
                break;

            case Constants.SyncDataType.PERSON_ACCESS:
                PersonAccessDB personAccess = parsePersonAccess(syncData.getData()); //OK
                synchronizePersonAccess(personAccess, actionType);
                break;

            case Constants.SyncDataType.CLEAR_EVENT:
                clearEventData();
                break;

            case Constants.SyncDataType.CLEAR_DATA:
                clearAllData();
                break;

            case Constants.SyncDataType.UPLOAD_DATABASE:
                uploadFileRequest(syncData);
                break;

            case Constants.SyncDataType.NORMAL_OPEN_DOOR:
                ConfigUtil.setNormalOpenDoor(mContext, true);
                break;

            case Constants.SyncDataType.NORMAL_CLOSE_DOOR:
                ConfigUtil.setNormalOpenDoor(mContext, false);
                break;

            case Constants.SyncDataType.REBOOT_DEVICE:
                result = Constants.ProcessResult.RESTART;
                break;

            case Constants.SyncDataType.GET_ALL_PERSON_INFO:
                reportAllPerson();
                break;

            case Constants.SyncDataType.GET_PERSON_INFO:
                String personId = parsePersonReport(syncData.getData());
                reportPersonInfo(personId);
                break;

            case Constants.SyncDataType.SUMMARY_PERSON_LOG:
                logSummaryPersonReport();
                break;

            case Constants.SyncDataType.UPLOAD_LOG_FILE:
                uploadLogFile(syncData);
                break;

            case Constants.SyncDataType.TWIN:
                TwinDB twinDB = parseTwin(syncData.getData()); //OK
                synchronizeTwins(twinDB, actionType);
                break;

            case Constants.SyncDataType.CLEAR_LOG_FILE:
                clearLogFile();
                break;

            case Constants.SyncDataType.RESEND_EVENT:
                resendEvent(syncData.getData());
                break;
        }

        return result;
    }

    public void uploadDatabase(FaceTerminalRemote syncData) {
        String url = httpPrefix + prefServerDomain + "/api/v1/synchronize/upload-database";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    if (error.getMessage() == null) {
                        String cause = StringUtils.getThrowCause(error);
                        if (cause.contains("com.android.volley.NoConnectionError")) {
                            return;
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("token", TokenEngineUtils.getToken());
                return headers;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                byte[] dataFile = new byte[0];
                String srcFilePath = mContext.getDatabasePath("ATIN_Manager").getAbsolutePath();
                File fileSrc = new File(srcFilePath);
                if(fileSrc.exists()){
                    try {
                        dataFile = FileUtils.readFileToByteArray(fileSrc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, DataPart> params = new HashMap<>();
                params.put("DataFile", new DataPart("ATIN_Manager.db", dataFile));
                params.put("SyncId", new DataPart("" + syncData.getId(), new byte[0]));
                return params;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }

    public void uploadLogFile(SyncRequest syncData) {
        String url = httpPrefix + prefServerDomain + "/api/v1/synchronize/upload-log-file";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    if (error.getMessage() == null) {
                        String cause = StringUtils.getThrowCause(error);
                        if (cause.contains("com.android.volley.NoConnectionError")) {
                            showToast(mContext.getString(R.string.msg_server_error));
                            return;
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                //headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                byte[] dataFile = new byte[0];

                String logFilePath = Environment.getExternalStorageDirectory().toString();
                String sourceFilePath = logFilePath + File.separator + "ATINAccess";

                String logtime = StringUtils.getDateTime("yyyyMMdd_HHmmss");
                String fileZipName = "ATINAccess" + "_" + logtime + ".zip";
                String fileZipPath = logFilePath + File.separator + fileZipName;

                zipLogFile(sourceFilePath, fileZipPath);
                deleteAllZipLogFileExclude(fileZipName);
                File fileSrc = new File(fileZipPath);
                if(fileSrc.exists()){
                    try {
                        dataFile = FileUtils.readFileToByteArray(fileSrc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, DataPart> params = new HashMap<>();
                params.put("DataFile", new DataPart(fileZipName, dataFile, "application/octet-stream"));
                params.put("SyncId", new DataPart("" + syncData.getSyncId(), new byte[0]));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(30*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }

    public void uploadLogFile(FaceTerminalRemote syncData) {
        String url = httpPrefix + prefServerDomain + "/api/v1/synchronize/upload-log-file";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    if (error.getMessage() == null) {
                        String cause = StringUtils.getThrowCause(error);
                        if (cause.contains("com.android.volley.NoConnectionError")) {
                            return;
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("token", TokenEngineUtils.getToken());
                return headers;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                byte[] dataFile = new byte[0];

                String logFilePath = Environment.getExternalStorageDirectory().toString();
                String sourceFilePath = logFilePath + File.separator + "ATINAccess";

                String logtime = StringUtils.getDateTime("yyyyMMdd_HHmmss");
                String fileZipName = "ATINAccess" + "_" + logtime + ".zip";
                String fileZipPath = logFilePath + File.separator + fileZipName;

                zipLogFile(sourceFilePath, fileZipPath);
                deleteAllZipLogFileExclude(fileZipName);
                File fileSrc = new File(fileZipPath);
                if(fileSrc.exists()){
                    try {
                        dataFile = FileUtils.readFileToByteArray(fileSrc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, DataPart> params = new HashMap<>();
                params.put("DataFile", new DataPart(fileZipName, dataFile, "application/octet-stream"));
                params.put("SyncId", new DataPart("" + syncData.getId(), new byte[0]));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(30*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }

    public void clearLogFile(){
        String sourceFilePath = Environment.getExternalStorageDirectory().toString() + File.separator + "ATINAccess";
        File logFolder = new File (sourceFilePath);

        if (!logFolder.isDirectory()) {
            return;
        }
        String currentFileLogName = StringUtils.convertDateToString(Calendar.getInstance().getTime(), "yyyyMMdd") + ".log";
        File[] allFile = logFolder.listFiles();
        for(int i=0; i<allFile.length; i++){
            File logTextFile = allFile[i];
            if(logTextFile.isFile() && !logTextFile.getName().contains(currentFileLogName)){
                try {
                    FileUtils.forceDelete(logTextFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void zipLogFile(String sourceFilePath, String zipFilePath){
        try{
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            File fileToZip = new File(sourceFilePath);
            BaseUtil.zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private void deleteAllZipLogFileExclude(String fileNameExclude){
        File rootPath = new File (Environment.getExternalStorageDirectory().toString());

        if (!rootPath.isDirectory()) {
            return;
        }

        File[] allFile = rootPath.listFiles(new FilterZipFile());
        for(int i=0; i<allFile.length; i++){
            File zipFile = allFile[i];
            if(!zipFile.getName().equalsIgnoreCase(fileNameExclude)){
                try {
                    FileUtils.forceDelete(zipFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearEventData(){
        database.clearEventLog();

        if (EVENT_PATH == null){
            EVENT_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "event";
        }

        File eventFolder = new File(EVENT_PATH);
        if (eventFolder.exists()){
            for(File file: eventFolder.listFiles())
                if (!file.isDirectory())
                    file.delete();
        }
    }

    private void clearAllData(){
        database.clearDatabase();

        if (EVENT_PATH == null){
            EVENT_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "event";
        }

        File eventFolder = new File(EVENT_PATH);
        if (eventFolder.exists()){
            for(File file: eventFolder.listFiles())
                if (!file.isDirectory())
                    file.delete();
        }

        if (FACE_PATH == null){
            FACE_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator + "face";
        }

        File faceFolder = new File(FACE_PATH);
        if (faceFolder.exists()){
            for(File file: faceFolder.listFiles())
                if (!file.isDirectory())
                    file.delete();
        }

        if(UPLOAD_PATH == null ){
            UPLOAD_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator + "upload";
        }

        File uploadFolder = new File(UPLOAD_PATH);
        if (uploadFolder.exists()){
            for(File file: uploadFolder.listFiles())
                if (!file.isDirectory())
                    file.delete();
        }
    }

    private String[] toArrayData(String data){
        String groupAccessData = StringUtils.nvl(data);
        return groupAccessData.split("\\|");
    }

    public void savePreferenceValue(){
        MachineDB thisDevice = null;
        String imei = BaseUtil.getImeiNumber(mContext);
        try{
            thisDevice = database.getMachineByImei(imei);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        if(thisDevice == null){
            thisDevice = ConfigUtil.getMachine();
        }

        if(thisDevice != null && thisDevice.getMachineId() != 0){
            MachineDB oldMachine = ConfigUtil.getMachine();
            if(oldMachine == null){
                ConfigUtil.setMachine(thisDevice);
            }else{
                if(oldMachine.getMachineId() != thisDevice.getMachineId()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getAutoSleep() != thisDevice.getAutoSleep()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getAutoStart() != thisDevice.getAutoStart()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getDistanceDetect() != thisDevice.getDistanceDetect()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getFaceThreshold() != thisDevice.getFaceThreshold()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getFraudProof() != thisDevice.getFraudProof()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getImei().equals(thisDevice.getImei())){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getLanguage().equals(thisDevice.getLanguage())){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getLogo().equals(thisDevice.getLogo())){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getRestartTime().equals(thisDevice.getRestartTime())){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getServerIp().equals(thisDevice.getServerIp())){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(oldMachine.getUseMask() != thisDevice.getUseMask()){
                    ConfigUtil.setMachine(thisDevice);
                }

                if(!oldMachine.getUsernameServer().equals(thisDevice.getUsernameServer())){
                    ConfigUtil.setMachine(thisDevice);
                }

                switch (thisDevice.getFraudProof()){
                    case 1: //medium
                        LivenessLevel mediumLevel = new LivenessLevel(Application.getInstance().getString(R.string.liveness_level_medium_name), Application.getInstance().getResources().getInteger(R.integer.liveness_level_medium_value));
                        LivenessLevelUtils.setLivenessLevel(mediumLevel);
                        break;

                    case 2: //high
                        LivenessLevel highLevel = new LivenessLevel(Application.getInstance().getString(R.string.liveness_level_high_name), Application.getInstance().getResources().getInteger(R.integer.liveness_level_high_value));
                        LivenessLevelUtils.setLivenessLevel(highLevel);
                        break;

                    default: //no level
                        LivenessLevel noLivenessLevel = new LivenessLevel(Application.getInstance().getString(R.string.liveness_level_no_name), Application.getInstance().getResources().getInteger(R.integer.liveness_level_no_value));
                        LivenessLevelUtils.setLivenessLevel(noLivenessLevel);
                }
            }
        }

        int livernessDetect = thisDevice.getFraudProof();
        int delayTime = thisDevice.getDelay();
        if(delayTime <= 1000 || delayTime > 10*1000){
            delayTime = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDelayTime();
        }
        int autoStart = thisDevice.getAutoStart();
        int autoSleep = thisDevice.getAutoSleep();
        int useLed = thisDevice.getLed();
        int saveVisistor = thisDevice.getAutoSaveVisitor();
        double faceThreshold = thisDevice.getFaceThreshold();
        double nomaskQuality = thisDevice.getNoMaskQualityThreshold();
        double maskQuality = thisDevice.getMaskQualityThreshold();
        double registerQuality = thisDevice.getRegQualityThreshold();
        double tempThreshold = thisDevice.getTemperatureThreshold();
        String userServer = thisDevice.getUsernameServer();
        String passServer = thisDevice.getPasswordServer();
        String username = thisDevice.getUsername();
        String password = thisDevice.getPassword();
        int distanceDetect = thisDevice.getDistanceDetect();
        if(distanceDetect < 20 || distanceDetect > 100){
            distanceDetect = CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getDistance();
        }
        int useMask = thisDevice.getUseMask();
        int useSound = thisDevice.getVolume();
        int useTemperature = thisDevice.getUseTemperature();
        int useVaccine = thisDevice.getUseVaccine();
        Uri logo = Uri.fromFile(new File(thisDevice.getLogo()));

        int useDailyReboot = thisDevice.getDailyReboot();
        String restartTime = thisDevice.getRestartTime();
        int restartHour = 0;
        int restartMinute = 0;
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(restartTime)){
            try{
                String[] arrTime = restartTime.split(":", -1);
                restartHour = Integer.parseInt(arrTime[0]);
                restartMinute = Integer.parseInt(arrTime[1]);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        int noDelay = thisDevice.getNoDelay();

        SharedPreferences pref = mContext.getSharedPreferences("PREF", MODE_PRIVATE);
        pref.edit().putBoolean(Constants.PREF_LIVENESS_DETECT, livernessDetect == 1 ? true : false).apply();
        pref.edit().putInt(Constants.PREF_DELAY_RECOGNIZE_TIME, delayTime).apply();
        pref.edit().putBoolean(Constants.PREF_AUTOMATIC_START, autoStart == 1 ? true: false).apply();
        pref.edit().putBoolean(Constants.PREF_AUTOMATIC_SLEEP, autoSleep == 1 ? true: false).apply();
        pref.edit().putBoolean(Constants.PREF_USE_LED, useLed == 1 ? true: false).apply();
        pref.edit().putBoolean(Constants.PREF_USE_SOUND, useSound == 0 ? false : true).apply();
        pref.edit().putString(Constants.PREF_THRESHOLD, String.valueOf(faceThreshold)).apply();
        pref.edit().putString(Constants.PREF_NOMASK_QUALITY_THRESHOLD, String.valueOf(nomaskQuality)).apply();
        pref.edit().putString(Constants.PREF_MASK_QUALITY_THRESHOLD, String.valueOf(maskQuality)).apply();
        pref.edit().putString(Constants.PREF_REGISTER_QUALITY_THRESHOLD, String.valueOf(registerQuality)).apply();
        pref.edit().putString(Constants.PREF_TEMPERATURE_THRESHOLD, String.valueOf(tempThreshold)).apply();
        pref.edit().putString(Constants.PREF_USERNAME, username).apply();
        pref.edit().putString(Constants.PREF_PASSWORD, password).apply();
        pref.edit().putString(Constants.PREF_USERNAME_AUTH, userServer).apply();
        pref.edit().putString(Constants.PREF_PASSWORD_AUTH, passServer).apply();
        pref.edit().putInt(Constants.PREF_DEFAULT_LOGO_RESOURCE, R.drawable.logo).apply();
        pref.edit().putBoolean(Constants.PREF_CHECK_MASK, useMask == 1 ? true: false).apply();
        pref.edit().putBoolean(Constants.PREF_CHECK_TEMPERATURE, useTemperature == 1 ? true: false).apply();
        pref.edit().putBoolean(Constants.PREF_SAVE_PERSON_UNREG, saveVisistor == 1 ? true: false).apply();
        pref.edit().putInt(Constants.PREF_DISTANCE_DETECT, distanceDetect).apply();
        pref.edit().putBoolean(Constants.PREF_SHOW_VACCINE_INFO, useVaccine == 1 ? true: false).apply();
        pref.edit().putString(Constants.PREF_LOGO, logo.toString()).apply();
        pref.edit().putBoolean(Constants.PREF_USE_AUTO_REBOOT, useDailyReboot == 1 ? true: false).apply();
        pref.edit().putInt(Constants.PREF_FREQ_REBOOT_HOUR, restartHour).apply();
        pref.edit().putInt(Constants.PREF_FREQ_REBOOT_MINUTE, restartMinute).apply();
        pref.edit().putBoolean(Constants.PREF_NO_DELAY, noDelay == 1 ? true: false).apply();

        int functionDevice = thisDevice.getDeviceFunction();
        MachineFunction machineFunction = MachineFunctionUtils.searchMachineFunction(functionDevice);
        MachineFunctionUtils.setMachineFunction(machineFunction);

        String languageCode = thisDevice.getLanguage();
        Language language = LanguageUtils.searchLanguageData(languageCode);
        LanguageUtils.changeLanguage(language);
    }

    public void synchronizePerson(PersonDB model, int action){
        PersonDB personDB = database.getPerson(model.getPersonId());

        switch (action){
            case Constants.SyncAction.SAVE:
                if(personDB != null){
                    database.updatePerson(model);
                }else{
                    database.addPerson(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deletePerson(model.getPersonId());
                break;
        }
    }

    public void synchronizeFace(FaceDB modelInput, int action) throws Exception {
        byte[] faceFeature = null;
        FaceDB model = modelInput;

        try{
            FaceDB faceDB = database.getFace(model.getFaceId());
            String facePath = FACE_PATH + File.separator +  model.getFaceId() + ".jpg";
            //FaceRegisterInfo faceInfo = new FaceRegisterInfo(model.getPersonId(), facePath);

            switch (action){
                case Constants.SyncAction.SAVE:
                    if(model.getFaceFeature() == null || model.getFaceFeature().length == 0){
                        faceFeature = processFaceImage(model.getFaceUrl(), facePath);
                        model.setFaceFeature(faceFeature);
                    }else{
                        downloadFaceImage(model.getFaceUrl(), facePath, model.getImageBase64());
                    }

                    model.setFacePath(facePath);
                    //faceInfo.setFeatureData(faceFeature);

                    if(faceDB == null){
                        database.deleteFaceByPerson(model.getPersonId());
                        database.addFace(model);
                        //FaceServer.getInstance().updatePersonRealtime(faceInfo, Constants.SyncAction.ADD);
                    }else{
                        database.updateFace(model);
                        //FaceServer.getInstance().updatePersonRealtime(faceInfo, Constants.SyncAction.UPDATE);
                    }
                    break;

                case Constants.SyncAction.REMOVE:
                    File fileFace = new File(facePath);
                    if(fileFace.exists()){
                        BaseUtil.deleteFile(fileFace);
                    }
                    database.deleteFace(model.getPersonId());
                    //FaceServer.getInstance().updatePersonRealtime(faceInfo, Constants.SyncAction.REMOVE);
                    break;
            }
        }catch ( Exception ex){
            throw ex;
        }
    }

    public void synchronizeCard(CardDB model, int action){
        CardDB cardDB = database.getCard(model.getCardId());

        switch (action){
            case Constants.SyncAction.SAVE:
                if(cardDB == null){
                    database.addCard(model);
                }else{
                    database.updateCard(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deleteCard(model.getCardId());
                break;
        }
    }

    public void synchronizeMachine(MachineDB model, int action){
        MachineDB machineDb = database.getMachine(model.getMachineId());

        switch (action){
            case Constants.SyncAction.SAVE:
                if(machineDb == null){
                    database.addMachine(model);
                }else{
                    database.updateMachine(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deleteMachine(model.getMachineId());
                break;
        }
    }

    public void synchronizeAccessTimeSeg(AccessTimeSegDB model, int action){
        AccessTimeSegDB accessTimeSegDB = database.getAccessTimeSeg(model.getId());

        switch (action){
            case Constants.SyncAction.SAVE:
                if(accessTimeSegDB == null){
                    database.addAccessTimeSeg(model);
                }else{
                    database.deleteAccessTimeSeg(model.getId());
                    database.addAccessTimeSeg(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deleteAccessTimeSeg(model.getId());
                break;
        }
    }

    public void synchronizeGroupAccess(GroupAccessDB model, int action){
        GroupAccessDB groupAccessDB = database.getGroupAccess(model.getId());

        switch (action){
            case Constants.SyncAction.SAVE:
                if(groupAccessDB == null){
                    database.addGroupAccess(model);
                }else{
                    database.deleteGroupAccess(model.getId());
                    database.addGroupAccess(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deleteGroupAccess(model.getId());
                break;
        }
    }

    public void synchronizePersonGroup(PersonGroupDB model, int action){
        PersonGroupDB personGroupDB = database.getPersonGroup(model.getId());
        switch (action){
            case Constants.SyncAction.SAVE:
                if(personGroupDB == null){
                    database.addPersonGroup(model);
                }else{
                    database.deletePersonGroup(model.getId());
                    database.addPersonGroup(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deletePersonGroup(model.getId());
                break;
        }
    }

    public void synchronizePersonAccess(PersonAccessDB model, int action){
        PersonAccessDB personAccessDB = database.getPersonAccess(model.getId());
        switch (action){
            case Constants.SyncAction.SAVE:
                if(personAccessDB == null){
                    database.addPersonAccess(model);
                }else{
                    database.deletePersonAccess(model.getPersonId());
                    database.addPersonAccess(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deletePersonAccess(model.getPersonId());
                break;
        }
    }

    public void synchronizeTwins(TwinDB model, int action){
        TwinDB twinDB = database.getTwin(model.getId());
        switch (action){
            case Constants.SyncAction.SAVE:
                if(twinDB == null){
                    database.addTwin(model);
                }else{
                    database.deleteTwin(model.getId());
                    database.addTwin(model);
                }
                break;

            case Constants.SyncAction.REMOVE:
                database.deleteTwin(model.getId());
                break;
        }
    }

    public void clearDatabase () throws Exception {
        database.truncateAllDatabase();
        clearEventData();
        ConfigUtil.setMachine(null);

        Intent intent = new Intent(Constants.INIT_PREFERENCE);
        mContext.sendBroadcast(intent);

        try{
            FaceServer.getInstance().initFaceList(mContext);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    public void doClear(int action){
        switch (action){
            case Constants.CLEAR_EVENT_LOG:
                database.deleteAllEvent();
                break;

            case Constants.CLEAR_PERSON:
                database.deleteAllPerson();
                break;

            case Constants.CLEAR_PARAM_CONFIG:
                break;

            case Constants.CLEAR_DAILY:
                break;

            case Constants.CLEAR_ALL:
                database.deleteAllEvent();
                database.deleteAllPerson();
                break;
        }
    }

    private void resendEvent(String eventDate) throws Exception {
        try{
            Date dtEvent = StringUtils.convertStringToDate(eventDate, "yyyy-MM-dd");
            if(dtEvent == null){
                throw new Exception("Access date is invalid " + eventDate);
            }

            database.updateEventStatus(eventDate, Constants.EVENT_STATUS_WAIT_SYNC);
        }catch (Exception ex){
            throw ex;
        }
    }

    public AccessTimeSegDB parseAccessTimeSeg(String lineValue) {
        AccessTimeSegDB model = null;
        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            int timeSegId = Integer.parseInt(arrLine[0]);
            int compId = Integer.parseInt(arrLine[1]);
            String timeSegName = arrLine[2];

            String mondayStart1 = "null".equals(arrLine[3]) ? "00:00:00" : arrLine[3];
            String mondayEnd1 =   "null".equals(arrLine[4])  ? "00:00:00" : arrLine[4];
            String mondayStart2 = "null".equals(arrLine[5])  ? "00:00:00" : arrLine[5];
            String mondayEnd2 =   "null".equals(arrLine[6])  ? "00:00:00" : arrLine[6];
            String mondayStart3 = "null".equals(arrLine[7])  ? "00:00:00" : arrLine[7];
            String mondayEnd3 =   "null".equals(arrLine[8])  ? "00:00:00" : arrLine[8];
            String mondayStart4 = "null".equals(arrLine[9])  ? "00:00:00" : arrLine[9];
            String mondayEnd4 =   "null".equals(arrLine[10])  ? "00:00:00" : arrLine[10];

            String tuesdayStart1 = "null".equals(arrLine[11]) ? "00:00:00" : arrLine[11];
            String tuesdayEnd1 =   "null".equals(arrLine[12]) ? "00:00:00" : arrLine[12];
            String tuesdayStart2 = "null".equals(arrLine[13]) ? "00:00:00" : arrLine[13];
            String tuesdayEnd2 =   "null".equals(arrLine[14]) ? "00:00:00" : arrLine[14];
            String tuesdayStart3 = "null".equals(arrLine[15]) ? "00:00:00" : arrLine[15];
            String tuesdayEnd3 =   "null".equals(arrLine[16]) ? "00:00:00" : arrLine[16];
            String tuesdayStart4 = "null".equals(arrLine[17]) ? "00:00:00" : arrLine[17];
            String tuesdayEnd4 =   "null".equals(arrLine[18]) ? "00:00:00" : arrLine[18];

            String wednesdayStart1 = "null".equals(arrLine[19]) ? "00:00:00" : arrLine[19];
            String wednesdayEnd1 =   "null".equals(arrLine[20]) ? "00:00:00" : arrLine[20];
            String wednesdayStart2 = "null".equals(arrLine[21]) ? "00:00:00" : arrLine[21];
            String wednesdayEnd2 =   "null".equals(arrLine[22]) ? "00:00:00" : arrLine[22];
            String wednesdayStart3 = "null".equals(arrLine[23]) ? "00:00:00" : arrLine[23];
            String wednesdayEnd3 =   "null".equals(arrLine[24]) ? "00:00:00" : arrLine[24];
            String wednesdayStart4 = "null".equals(arrLine[25]) ? "00:00:00" : arrLine[25];
            String wednesdayEnd4 =   "null".equals(arrLine[26]) ? "00:00:00" : arrLine[26];

            String thusdayStart1 = "null".equals(arrLine[27]) ? "00:00:00" : arrLine[27];
            String thusdayEnd1 =   "null".equals(arrLine[28]) ? "00:00:00" : arrLine[28];
            String thusdayStart2 = "null".equals(arrLine[29]) ? "00:00:00" : arrLine[29];
            String thusdayEnd2 =   "null".equals(arrLine[30]) ? "00:00:00" : arrLine[30];
            String thusdayStart3 = "null".equals(arrLine[31]) ? "00:00:00" : arrLine[31];
            String thusdayEnd3 =   "null".equals(arrLine[32]) ? "00:00:00" : arrLine[32];
            String thusdayStart4 = "null".equals(arrLine[33]) ? "00:00:00" : arrLine[33];
            String thusdayEnd4 =   "null".equals(arrLine[34]) ? "00:00:00" : arrLine[34];

            String fridayStart1 = "null".equals(arrLine[35]) ? "00:00:00" : arrLine[35];
            String fridayEnd1 =   "null".equals(arrLine[36]) ? "00:00:00" : arrLine[36];
            String fridayStart2 = "null".equals(arrLine[37]) ? "00:00:00" : arrLine[37];
            String fridayEnd2 =   "null".equals(arrLine[38]) ? "00:00:00" : arrLine[38];
            String fridayStart3 = "null".equals(arrLine[39]) ? "00:00:00" : arrLine[39];
            String fridayEnd3 =   "null".equals(arrLine[40]) ? "00:00:00" : arrLine[40];
            String fridayStart4 = "null".equals(arrLine[41]) ? "00:00:00" : arrLine[41];
            String fridayEnd4 =   "null".equals(arrLine[42]) ? "00:00:00" : arrLine[42];

            String SaturdayStart1 = "null".equals(arrLine[43]) ? "00:00:00" : arrLine[43];
            String SaturdayEnd1 =   "null".equals(arrLine[44]) ? "00:00:00" : arrLine[44];
            String SaturdayStart2 = "null".equals(arrLine[45]) ? "00:00:00" : arrLine[45];
            String SaturdayEnd2 =   "null".equals(arrLine[46]) ? "00:00:00" : arrLine[46];
            String SaturdayStart3 = "null".equals(arrLine[47]) ? "00:00:00" : arrLine[47];
            String SaturdayEnd3 =   "null".equals(arrLine[48]) ? "00:00:00" : arrLine[48];
            String SaturdayStart4 = "null".equals(arrLine[49]) ? "00:00:00" : arrLine[49];
            String SaturdayEnd4 =   "null".equals(arrLine[50]) ? "00:00:00" : arrLine[50];

            String SundayStart1 = "null".equals(arrLine[51]) ? "00:00:00" : arrLine[51];
            String SundayEnd1 =   "null".equals(arrLine[52]) ? "00:00:00" : arrLine[52];
            String SundayStart2 = "null".equals(arrLine[53]) ? "00:00:00" : arrLine[53];
            String SundayEnd2 =   "null".equals(arrLine[54]) ? "00:00:00" : arrLine[54];
            String SundayStart3 = "null".equals(arrLine[55]) ? "00:00:00" : arrLine[55];
            String SundayEnd3 =   "null".equals(arrLine[56]) ? "00:00:00" : arrLine[56];
            String SundayStart4 = "null".equals(arrLine[57]) ? "00:00:00" : arrLine[57];
            String SundayEnd4 =   "null".equals(arrLine[58]) ? "00:00:00" : arrLine[58];

            model = new AccessTimeSegDB(timeSegId, compId, timeSegName,
                    mondayStart1, mondayEnd1, mondayStart2, mondayEnd2, mondayStart3, mondayEnd3, mondayStart4, mondayEnd4,
                    tuesdayStart1, tuesdayEnd1, tuesdayStart2, tuesdayEnd2, tuesdayStart3, tuesdayEnd3, tuesdayStart4, tuesdayEnd4,
                    wednesdayStart1, wednesdayEnd1, wednesdayStart2, wednesdayEnd2, wednesdayStart3, wednesdayEnd3, wednesdayStart4, wednesdayEnd4,
                    thusdayStart1, thusdayEnd1, thusdayStart2, thusdayEnd2, thusdayStart3, thusdayEnd3, thusdayStart4, thusdayEnd4,
                    fridayStart1, fridayEnd1, fridayStart2, fridayEnd2, fridayStart3, fridayEnd3, fridayStart4, fridayEnd4,
                    SaturdayStart1, SaturdayEnd1, SaturdayStart2, SaturdayEnd2, SaturdayStart3, SaturdayEnd3, SaturdayStart4, SaturdayEnd4,
                    SundayStart1, SundayEnd1, SundayStart2, SundayEnd2, SundayStart3, SundayEnd3, SundayStart4, SundayEnd4
            );
        }catch (Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public PersonGroupDB parsePersonGroup (String lineValue) {
        PersonGroupDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            int id = Integer.parseInt(arrLine[0]);
            String personId = arrLine[1];
            int groupId = Integer.parseInt(arrLine[2]);
            model = new PersonGroupDB(id, personId, groupId);
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public PersonAccessDB parsePersonAccess (String lineValue) {
        PersonAccessDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            int id = Integer.parseInt(arrLine[0]);
            String personId = arrLine[1];
            int machineId = Integer.parseInt(arrLine[2]);
            String fromdate = arrLine[3];
            String todate = arrLine[4];
            int isdelete = Integer.parseInt(arrLine[5]);
            model = new PersonAccessDB(id, personId, machineId, fromdate, todate, isdelete);
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public GroupAccessDB parseGroupAccess(String lineValue) {
        GroupAccessDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            int gaId = Integer.parseInt(arrLine[0]);
            int groupId = Integer.parseInt(arrLine[1]);
            int machineId = Integer.parseInt(arrLine[2]);
            int timeSegId = Integer.parseInt(arrLine[3]);
            model = new GroupAccessDB(gaId, groupId, machineId, timeSegId );
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public TwinDB parseTwin(String lineValue) {
        TwinDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            int twinId = Integer.parseInt(arrLine[0]);
            String personId = arrLine[1];
            String similarPersonId = arrLine[2];
            model = new TwinDB(twinId, personId, similarPersonId );
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public PersonDB parsePerson (String lineValue){
        PersonDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            String personId = arrLine[0];
            int compId = Integer.parseInt(arrLine[1]);
            int deptId = Integer.parseInt(arrLine[2]);
            String personCode = arrLine[3];
            String fullName = arrLine[4];
            String position = arrLine[5];
            String jobDuties = arrLine[6];
            int status = Integer.parseInt(arrLine[7]);
            int personType = Integer.parseInt(arrLine[8]);
            int vaccine = Integer.parseInt(arrLine[9]);

            model = new PersonDB(
                    personId,
                    compId,
                    deptId,
                    personCode,
                    fullName,
                    position,
                    jobDuties,
                    personType,
                    status,
                    vaccine
            );
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public FaceDB parseFace (String lineValue){
        FaceDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE, -1);
            String faceId = arrLine[0];
            String personId = arrLine[1];
            String faceUrl = arrLine[2];
            byte[] feature = new byte[0];
            String imageBase64 = "";

            if(arrLine.length >= 4){
                String featureBase64 = arrLine[3];
                if(featureBase64 != null && !featureBase64.equals("") && !featureBase64.equals("null")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        feature = Base64.getDecoder().decode(arrLine[3].getBytes());
                    } else {
                        feature = android.util.Base64.decode(arrLine[3].getBytes(), android.util.Base64.DEFAULT);
                    }
                }
            }

            if(arrLine.length >= 5){
                imageBase64 = arrLine[4];
            }

            model = new FaceDB(
                    faceId,
                    personId,
                    faceUrl,
                    "",
                    feature,
                    "",
                    1,
                    imageBase64
            );
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public CardDB parseCard (String lineValue){
        CardDB model = null;

        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE);
            String cardId = arrLine[0];
            String personId = arrLine[1];
            String cardNo = arrLine[2];

            model = new CardDB(
                    cardId,
                    personId,
                    cardNo
            );
        }catch ( Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public MachineDB parseMachine(String lineValue) {
        MachineDB model = null;
        try{
            String [] arrLine = lineValue.split(Constants.FIELD_SAPERATE, -1);
            int machineId = Integer.parseInt(arrLine[0]);
            int compId = Integer.parseInt(arrLine[1]);
            String deviceName = arrLine[2];
            int deviceType = Integer.parseInt(arrLine[3]);
            int deviceFunction = Integer.parseInt(arrLine[4]);
            String ipAddress = arrLine[5];
            String imei = arrLine[6];
            String mac = arrLine[7];
            String serverIp = arrLine[8];
            int serverPort = Integer.parseInt(arrLine[9]);
            int fraudProof = Integer.parseInt(arrLine[10]);
            int angleDetect = Integer.parseInt(arrLine[11]);
            int autoStart = Integer.parseInt(arrLine[12]);
            int autoSaveVisitor = Integer.parseInt(arrLine[13]);
            int distanceDetect = Integer.parseInt(arrLine[14]);
            String username = arrLine[15];
            String password = arrLine[16];
            String logo = arrLine[17];
            int volume = Integer.parseInt(arrLine[18]);
            int brightness = Integer.parseInt(arrLine[19]);
            int delay = Integer.parseInt(arrLine[20]);
            int led = Integer.parseInt(arrLine[21]);
            double fingerThreshold = Double.parseDouble(arrLine[22]);
            double faceThreshold = Double.parseDouble(arrLine[23]);
            double temperatureThreshold = Double.parseDouble(arrLine[24]);
            String firmwareVersion = StringUtils.nvl(arrLine[25]);
            String usernameServer = StringUtils.nvl(arrLine[26]);
            String passwordServer = StringUtils.nvl(arrLine[27]);
            int useMask = Integer.parseInt(arrLine[28]);
            int useTemperatuer = Integer.parseInt(arrLine[29]);
            int useVaccine = Integer.parseInt(arrLine[30]);
            int usePCCovid = Integer.parseInt(arrLine[31]);
            String pccovidPhone = StringUtils.nvl(arrLine[32]);
            String pccovidLocation = StringUtils.nvl(arrLine[33]);
            String pccovidToken = StringUtils.nvl(arrLine[34]);

            int dailyReboot = 0;
            String restartTime = "";
            String language = LanguageUtils.getCurrentLanguage().getCode();
            String logoPath = downloadLogo(logo);
            int noDelay = 1;
            double nomaskQualityThreshold = 0.49f;
            double maskQualityThreshold = 0.29f;
            double regQualityThreshold = 0.63f;
            int autoSleep = 0;

            if(arrLine.length >= 36){
                dailyReboot = Integer.parseInt(arrLine[35]);
            }

            if(arrLine.length >= 37){
                restartTime = StringUtils.nvl(arrLine[36]);
            }

            if(arrLine.length >= 38){
                language = StringUtils.nvl(arrLine[37]);
            }

            if(arrLine.length >= 39){
                noDelay = Integer.parseInt(arrLine[38]);
            }


            if(arrLine.length >= 40){
                nomaskQualityThreshold = Double.parseDouble(arrLine[39]);
            }

            if(arrLine.length >= 41){
                maskQualityThreshold = Double.parseDouble(arrLine[40]);
            }

            if(arrLine.length >= 42){
                regQualityThreshold = Double.parseDouble(arrLine[41]);
            }

            if(arrLine.length >= 43){
                autoSleep = Integer.parseInt(arrLine[42]);
            }

            model = new MachineDB(
                    machineId, compId, deviceName, deviceType, deviceFunction, ipAddress,
                    imei, mac, serverIp, serverPort, fraudProof, angleDetect, autoStart,
                    autoSaveVisitor, distanceDetect, username, password, logoPath, volume, brightness,
                    delay, led, fingerThreshold, faceThreshold, temperatureThreshold, firmwareVersion,
                    usernameServer, passwordServer, useMask, useTemperatuer, useVaccine, usePCCovid,
                    pccovidPhone, pccovidLocation, pccovidToken, dailyReboot, restartTime, language,
                    noDelay, nomaskQualityThreshold, maskQualityThreshold, regQualityThreshold, autoSleep
            );
        }catch (Exception ex){
            Log.e("SYNC", ex.getMessage());
        }
        return model;
    }

    public String parsePersonReport(String lineValue) {
        String personId = lineValue;
        return personId;
    }

    private String downloadLogo(String logoUrl){
        String logoPath = "";

        try {
            URL url = new URL(logoUrl);
            String fileName = FilenameUtils.getName(url.getPath());
            logoPath = UPLOAD_PATH + File.separator + fileName;
            File logo = new File(logoPath);
            boolean blResult = BaseUtil.downloadLogo(logoUrl, logo, FilenameUtils.getExtension(logoUrl));
            if(!blResult){
                logoPath = ""; ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logoPath;
    }

    private byte[] processFaceImage(String url, String filePath) throws Exception {
        byte[] faceFeatures = new byte[]{};

        try{
            File destFace = new File(filePath);
            BaseUtil.deleteFile(destFace);

            boolean blDownload = BaseUtil.downloadImage(url, destFace);
            if(blDownload){
                BaseUtil.resizeImage(destFace);
                faceFeatures = FaceServer.getInstance().extractFaceFeature(mContext, destFace);
            }
        }catch (Exception ex){
            throw ex;
        }

        if(faceFeatures == null || faceFeatures.length == 0){
            throw new Exception("Can not extract feature in image " + url + " - Machine " + BaseUtil.getSerialNumber());
        }

        return faceFeatures;
    }

    private boolean downloadFaceImage(String url, String filePath, String imageBase64) throws Exception {
        if(imageBase64 != null && !imageBase64.equals("") && !imageBase64.equals("null")){
            File destFace = new File(filePath);
            BaseUtil.saveImage(imageBase64, destFace);
            return true;
        }

        boolean downloadFinish = false;
        try{
            File destFace = new File(filePath);
            BaseUtil.deleteFile(destFace);
            downloadFinish = BaseUtil.downloadImage(url, destFace);
            if(downloadFinish){
                BaseUtil.resizeImage(destFace);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            throw new Exception("Can not download image " + url);
        }

        return downloadFinish;
    }

    public void uploadFileRequest(SyncRequest syncData) {
        String url = httpPrefix + prefServerDomain + "/api/v1/synchronize/upload-file";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    if (error.getMessage() == null) {
                        String cause = StringUtils.getThrowCause(error);
                        if (cause.contains("com.android.volley.NoConnectionError")) {
                            showToast(mContext.getString(R.string.msg_server_error));
                            return;
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return headers;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                byte[] dataFile = new byte[0];
                String srcFilePath = mContext.getDatabasePath("ATIN_Manager").getAbsolutePath();
                File fileSrc = new File(srcFilePath);
                if(fileSrc.exists()){
                    try {
                        dataFile = FileUtils.readFileToByteArray(fileSrc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, DataPart> params = new HashMap<>();
                params.put("DataFile", new DataPart("ATIN_Manager.db", dataFile));
                params.put("SyncId", new DataPart("" + syncData.getSyncId(), new byte[0]));
                return params;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }

    public void logSummaryPersonReport(){
        SynchronizeSummaryPersonLog.getInstance(mContext).summaryAndSend();
    }

    public void reportAllPerson() throws JSONException {
        List<PersonReport> lsAllPersonInvalid = database.getAllPersonReport();
        LogPersonResponseServer.getInstance(mContext).responseLog(lsAllPersonInvalid);
    }

    public void reportPersonInfo(String personId) throws JSONException {
        List<PersonReport> personInvalid = database.getPersonReport(personId);
        if(personInvalid.isEmpty()){
            personInvalid = database.getPersonReportNoFace(personId);
        }

        LogPersonResponseServer.getInstance(mContext).responseLog(personInvalid);
    }

    private void showToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}

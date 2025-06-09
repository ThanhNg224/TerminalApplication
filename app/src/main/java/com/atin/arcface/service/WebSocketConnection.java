package com.atin.arcface.service;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.atin.arcface.BuildConfig;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.ProcessSynchronizeData;
import com.atin.arcface.common.WebSocketActionRequest;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.faceserver.FaceServer;
import com.atin.arcface.model.DeviceInfo;
import com.atin.arcface.model.EventDB;
import com.atin.arcface.model.EventOfPersonRequest;
import com.atin.arcface.model.EventReportModel;
import com.atin.arcface.model.FaceTerminalRemote;
import com.atin.arcface.model.PersonReport;
import com.atin.arcface.model.SocketRequestMessage;
import com.atin.arcface.model.SocketResponseMessage;
import com.atin.arcface.model.TableSummaryReport;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketConnection implements Runnable{
    private static final String TAG = "WebSocketConnection";
    private static Logger logger = Log4jHelper.getLogger(TAG);
    private Context mContext;
    private RegisterAndRecognizeDualActivity mainActivity;
    private static final String LOGIN = "login";
    private static final String PASSCODE = "passcode";
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private static final Gson gson = new Gson();
    private Database database;
    private ProcessSynchronizeData processSynchronizeData;

    public WebSocketConnection(Context context) {
        this.mContext = context;
        mainActivity = SingletonObject.getInstance(mContext).getMainActivity();
        database = Application.getInstance().getDatabase();

        processSynchronizeData = new ProcessSynchronizeData(mContext);

        String webDomain = SingletonObject.getInstance(mContext).getDomain();
        String webSocketUrl = "";

        if(webDomain.contains("https")){
            webSocketUrl = webDomain.replaceAll("https://", "");
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://" + webSocketUrl + "/register");
        }else{
            webSocketUrl = webDomain.replaceAll("http://", "");
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + webSocketUrl + "/register");
        }
    }


    @Override
    public void run() {
        doConnect();
    }

    private void doConnect() {
        resetSubscriptions();
        connectStomp();
    }

    private void connectStomp() {
        String webDomain = SingletonObject.getInstance(mContext).getDomain();
        if(webDomain.equals("") || webDomain.equals("http://") || webDomain.equals("https://")){
            return;
        }

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(LOGIN, "guest"));
        headers.add(new StompHeader(PASSCODE, "guest"));
        headers.add(new StompHeader("username", BaseUtil.getSerialNumber()));
        headers.add(new StompHeader("ipaddress", BaseUtil.getLocalIpv4()));
        headers.add(new StompHeader("regtime", StringUtils.currentDatetimeMilisecondSQLiteformat()));

        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            ConfigUtil.setNetworkAvailable(true);
                            //mainActivity.updateNetworkStatus(true);
                            break;

                        case ERROR:
                            Log.e(TAG, "Stomp connection error " + lifecycleEvent.getException());
                            ConfigUtil.setNetworkAvailable(false);
                            //mainActivity.updateNetworkStatus(false);
                            reConnectStomp();
                            break;

                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            ConfigUtil.setNetworkAvailable(false);
                            //mainActivity.updateNetworkStatus(false);
                            disconnectStomp();
                            resetSubscriptions();
                            reConnectStomp();
                            break;

                        case FAILED_SERVER_HEARTBEAT:
                            ConfigUtil.setNetworkAvailable(false);
                            //mainActivity.updateNetworkStatus(false);
                            Log.e(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = mStompClient.topic("/users/queue/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    String msg = processServerResponse(topicMessage.getPayload());
                    sendToServer("/app/clientResponse", msg);
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);
        mStompClient.connect(headers);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void disconnectStomp(){
        if(mStompClient != null){
            mStompClient.disconnect();
        }
    }

    private void reConnectStomp(){
        Log.d(TAG, "Try to reconnect stomp server");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connectStomp();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void sendToServer(String des, String msg){
        compositeDisposable.add(mStompClient.send(des, msg)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                }));
    }

    private String processServerResponse(String request){
        SocketResponseMessage responseMessage = null;
        SocketRequestMessage socketRequest = gson.fromJson(request, SocketRequestMessage.class);
        switch (socketRequest.getActionType()){
            case WebSocketActionRequest.REBOOT_DEVICE:
                try {
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            BaseUtil.reboot();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                } catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.DEVICE_INFO:
                try{
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setDeviceTime(StringUtils.currentDatetimeMilisecondSQLiteformat());
                    deviceInfo.setSerialNumber(BaseUtil.getSerialNumber());
                    deviceInfo.setIpAddress(BaseUtil.getLocalIpv4());
                    deviceInfo.setAppVersion(BuildConfig.VERSION_NAME);
                    deviceInfo.setAppVersioNumber(BuildConfig.VERSION_CODE);
                    deviceInfo.setAppPackage(BuildConfig.APPLICATION_ID);
                    deviceInfo.setDeviceModel(Build.MODEL);
                    deviceInfo.setInitEngineCode(FaceServer.getInstance().getInitEnigineCode());
                    deviceInfo.setLastRebootTime(ConfigUtil.getLastRebootTime());

                    TableSummaryReport tableSummaryReport = new TableSummaryReport();
                    tableSummaryReport.setPerson(database.countRecord(Database.PERSON));
                    tableSummaryReport.setFace(database.countRecord(Database.FACE));
                    tableSummaryReport.setPersonGroup(database.countRecord(Database.PERSON_GROUP));
                    tableSummaryReport.setAccessTimeSeg(database.countRecord(Database.ACCESS_TIME_SEG));
                    tableSummaryReport.setFaceTerminal(database.countRecord(Database.MACHINE));
                    tableSummaryReport.setGroupAccess(database.countRecord(Database.GROUP_ACCESS));
                    tableSummaryReport.setEvent(database.countRecord(Database.EVENT));
                    tableSummaryReport.setPersonAccess(database.countRecord(Database.PERSON_ACCESS));
                    deviceInfo.setTableSummaryReport(tableSummaryReport);
                    deviceInfo.setFaceTerminal(database.getMachineByImei(BaseUtil.getSerialNumber()));

                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", deviceInfo, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.GET_PERSON_INFO:
                try{
                    String personId = socketRequest.getData();
                    List<PersonReport> lsPersonEntity = database.getPersonReport(personId);
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", lsPersonEntity, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.GET_ALL_PERSON_INFO:
                try{
                    List<PersonReport> lsAllPersonEntity = database.getAllPersonReport();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", lsAllPersonEntity, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.GET_LIST_LOG_FILE_NAME:
                try{
                    List<String> lsFileName = new ArrayList<>();
                    String fileName = Environment.getExternalStorageDirectory() + "/" + Constants.DEFAULT_LOG_PATH;
                    File directory = new File(fileName);
                    if (directory.isDirectory()) {
                        List<File> files = (List<File>) FileUtils.listFiles(directory, null, false);
                        for (File file : files) {
                            lsFileName.add(file.getName());
                        }
                    }
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", lsFileName, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.GET_LOG_FILE_CONTENT:
                try{
                    String fileContent = "";
                    String fileName = socketRequest.getData();
                    String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.DEFAULT_LOG_PATH + "/" + fileName;
                    File logFile = new File(filePath);
                    if (logFile.exists()) {
                        fileContent = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8.name());
                        if(fileContent.length() > 10000){
                            fileContent = fileContent.substring(fileContent.length() - 10000, fileContent.length());
                        }
                    }
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", fileContent, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.GET_EVENT_OF_PERSON:
                try{
                    String eventofPersonMsg = socketRequest.getData();
                    EventOfPersonRequest eventOfPersonRequest = gson.fromJson(eventofPersonMsg, EventOfPersonRequest.class);
                    List<EventDB> lsEvent = database.getEventOfPerson(eventOfPersonRequest.getPersonId(), eventOfPersonRequest.getAccessDate());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", gson.toJson(lsEvent), socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.UPDATE_EVENT:
                try{
                    String eventId = socketRequest.getData();
                    database.updateEventStatusById(eventId, Constants.EVENT_STATUS_WAIT_SYNC);
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.DELETE_EVENT:
                try{
                    String accessDate = socketRequest.getData();
                    database.deleteEventByAccessDate(accessDate);
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.UPDATE_ALL_EVENT:
                try{
                    String accessDate = socketRequest.getData();
                    database.updateEventStatus(accessDate, Constants.EVENT_STATUS_WAIT_SYNC);
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", accessDate, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            case WebSocketActionRequest.UPLOAD_LOG_FILE:
                try{
                    logger.info("Start upload log file");
                    String syncRequest = socketRequest.getData();
                    FaceTerminalRemote requestUploadLogFile = gson.fromJson(syncRequest, FaceTerminalRemote.class);
                    new Thread(() -> {
                        processSynchronizeData.uploadLogFile(requestUploadLogFile);
                    }).start();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }finally {
                    logger.info("End gupload database");
                }
                break;

            case WebSocketActionRequest.UPLOAD_DATABASE:
                try{
                    logger.info("Start upload database");
                    String syncRequest = socketRequest.getData();
                    FaceTerminalRemote requestUploadLogFile = gson.fromJson(syncRequest, FaceTerminalRemote.class);
                    new Thread(() -> {
                        processSynchronizeData.uploadDatabase(requestUploadLogFile);
                    }).start();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }finally {
                    logger.info("End gupload database");
                }
                break;

            case WebSocketActionRequest.GET_DATABASE_STRUCTURE:
                try{
                    logger.info("Start get database structure");
                    Map<String, String> structure = database.getDatabaseStructure();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", structure, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }finally {
                    logger.info("End get database structure");
                }
                break;

            case WebSocketActionRequest.CLEAR_LOG:
                try{
                    logger.info("Start clear log");
                    processSynchronizeData.clearLogFile();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }finally {
                    logger.info("End clear log");
                }
                break;

            case WebSocketActionRequest.CLEAR_ALL_DATABASE:
                try{
                    logger.info("Start clear database");
                    processSynchronizeData.clearDatabase();
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", null, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }finally {
                    logger.info("End clear database");
                }
                break;

            case WebSocketActionRequest.REPORT_EVENT:
                try{
                    String reportDate = socketRequest.getData();
                    EventReportModel eventReport = database.getEventReport(reportDate);
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "SUCCESS", eventReport, socketRequest.getRequestId());
                }catch (Exception e) {
                    logger.error("Error get " + socketRequest.getActionType() + ": " + e.getMessage());
                    responseMessage = new SocketResponseMessage(socketRequest.getActionType(), "ERROR", e.getMessage(), socketRequest.getRequestId());
                }
                break;

            default:
                break;
        }

        return gson.toJson(responseMessage);
    }
}

package com.atin.arcface.common;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.os.*;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.activity.UpdateSystemActivity;
import com.atin.arcface.faceserver.*;
import com.atin.arcface.model.*;
import com.atin.arcface.service.LogResponseServer;
import com.atin.arcface.service.PrinterHelper;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.service.TicketFormatter;
import com.atin.arcface.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.*;

public class AccessBussiness {
    private Context context;
    private Database database;
    private SharedPreferences pref;
    private boolean prefCheckMask;
    private boolean prefCheckTemperature;
    private float prefTemperatureThreshold;
    private boolean supportTemperatureCamera;
    private String pendingPersonId;
    private EventDB pendingEvent;

    private static Gson mGson = new Gson();
    private static Logger logger = Log4jHelper.getLogger(AccessBussiness.class.getSimpleName());

    public AccessBussiness(Context context) {
        this.context = context;
        initDatabase();
        pref = context.getSharedPreferences(Constants.SHARE_PREFERENCE, context.MODE_PRIVATE);
    }

    private void initDatabase() {
        database = Application.getInstance().getDatabase();
    }

    public void checkPermission(CompareResult compareResult, String personId) {
        MachineDB thisDevice = SingletonObject.getInstance(context).getMainActivity().getThisDevice();
        String deviceCode = thisDevice != null ? thisDevice.getImei() : "";

        int machineFunction = MachineFunctionUtils.getMachineFunction().getFunctionValue();

        if (machineFunction == Constants.CANTEEN) {
            //
            if (!offlineCanteenCheck(personId, deviceCode, compareResult)) {
                return; //
            }

            //
            if (ErrorCode.COMMON_ACCESS_VALID.equals(compareResult.getSummaryCode())) {
                pendingPersonId = personId;
                pendingEvent = getEventDb(compareResult);
                pendingEvent.setUploadStatus(Constants.UPLOAD_PENDING);

                SingletonObject.getInstance(context)
                        .getMainActivity()
                        .showDialogConfirmCanteen(compareResult);
            }
        } else {

            checkNormalPermission(personId, compareResult, 37f);
        }
    }

    private void checkNormalPermission(String personId, CompareResult compareResult, float flTemperature) {
        try {
            int machineFunction = MachineFunctionUtils.getMachineFunction().getFunctionValue();
            AccessResult accessResult = checkAccessPermission(personId, compareResult.isMask(), flTemperature, machineFunction, Constants.AccessType.FACE_RECOGNIZE);
            compareResult.setSummaryCode(accessResult.getSumaryCode());
            compareResult.setDetailCode(accessResult.getDetailCode());

            logger.info("Check permission"
                    + " - PersonId: " + compareResult.getPersonId()
                    + " - Errorcode: " + compareResult.getSummaryCode() + "/" + compareResult.getDetailCode()
            );

            SingletonObject.getInstance(context).getMainActivity().processResult(compareResult, false);
        } catch (Exception ex) {
            logger.error("Error checkPersonPermission " + ex.getMessage());
        }
    }

    public AccessResult checkAccessPermission(String personId, boolean isMask, float temperature, int machineFunction, int accessType) {
        String errorCode = checkAccess(personId, accessType);
        AccessResult result = processResult(machineFunction, errorCode);

        prefCheckMask = pref.getBoolean(Constants.PREF_CHECK_MASK, false);
        prefCheckTemperature = pref.getBoolean(Constants.PREF_CHECK_TEMPERATURE, false);
        prefTemperatureThreshold = Float.parseFloat(pref.getString(Constants.PREF_TEMPERATURE_THRESHOLD, "37.5f"));
        supportTemperatureCamera = BaseUtil.isInstalledPackage(context, "com.telpo.temperatureservice");

        if (supportTemperatureCamera && prefCheckTemperature) {
            if (temperature >= prefTemperatureThreshold) {
                return new AccessResult(errorCode, ErrorCode.COMMON_HIGHT_TEMPERATURE);
            }
        }

        if (prefCheckMask && !isMask) {
            return new AccessResult(result.getDetailCode(), ErrorCode.COMMON_NO_MASK);
        }

        return result;
    }

//    public void checkCanteenPermission(String personId, String deviceCode, CompareResult compareResult) throws Exception {
//        FacePermissionInput facePermissionInput = new FacePermissionInput();
//        facePermissionInput.setPersonId(personId);
//        facePermissionInput.setDeviceCode(deviceCode);
//
//        if (LockSearchUtil.isAvailable()) {
//            LockSearchUtil.doLock();
//            String url = SingletonObject.getInstance(context).getDomain() + "/api/v1/facePermission";
//            doSearchCanteenPermission(url, facePermissionInput, compareResult);
//        }
//    }

    private boolean offlineCanteenCheck(String personId, String deviceCode, CompareResult compareResult) {
        Log.d("CANTEEN-OFFLINE", "offlineCanteenCheck START: personId=" + personId + ", deviceCode=" + deviceCode);

        checkCanteenPermission(personId, deviceCode, compareResult);

        Log.d("CANTEEN-OFFLINE", "SummaryCode=" + compareResult.getSummaryCode() +
                ", DetailCode=" + compareResult.getDetailCode() +
                ", Note=" + compareResult.getNote());

        // Gán personType là dạng mô tả lỗi (human readable), không phải code lỗi
        if (!ErrorCode.COMMON_ACCESS_VALID.equals(compareResult.getSummaryCode())) {
            // Mapping mã lỗi sang mô tả (nếu muốn show UI đẹp)
            String typeDesc = getTypeDescFromSummaryCode(compareResult.getSummaryCode());
            compareResult.setPersonType(typeDesc);

            Log.d("CANTEEN-OFFLINE", "Set personType = " + typeDesc);

            setPersonMeta(compareResult, personId);

            Log.d("CANTEEN-OFFLINE", "Access NOT valid, processResult & return false");
            SingletonObject.getInstance(context).getMainActivity().processResult(compareResult, false);
            return false;
        }

        // Hợp lệ
        compareResult.setPersonType("Hợp lệ");
        setPersonMeta(compareResult, personId);
        Log.d("CANTEEN-OFFLINE", "Access VALID, continue");
        return true;
    }

    // Mapping mã lỗi sang human readable (ví dụ)
    private String getTypeDescFromSummaryCode(String code) {
        switch (code) {
            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY:
                return "Hết lượt trong ngày";
            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                return "Ngoài giờ phục vụ";
            case ErrorCode.COMMON_EXPIRED:
                return "Quyền truy cập hết hạn";
            case ErrorCode.COMMON_NOT_ACCESS:
                return "Không có quyền truy cập";
            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH:
                return "Hết lượt trong tháng";
            default:
                return "Lỗi: " + code;
        }
    }

    public AccessResult checkPersonNotFound(boolean isMask, float temperature, int machineType) {
        prefCheckMask = pref.getBoolean(Constants.PREF_CHECK_MASK, false);
        prefCheckTemperature = pref.getBoolean(Constants.PREF_CHECK_TEMPERATURE, false);
        prefTemperatureThreshold = Float.parseFloat(pref.getString(Constants.PREF_TEMPERATURE_THRESHOLD, "37.5f"));
        supportTemperatureCamera = BaseUtil.isInstalledPackage(context, "com.telpo.temperatureservice");

        if (supportTemperatureCamera && prefCheckTemperature) {
            if (temperature >= prefTemperatureThreshold) {
                return new AccessResult(ErrorCode.COMMON_FACE_NOT_FOUND, ErrorCode.COMMON_HIGHT_TEMPERATURE);
            }
        }

        if (prefCheckMask) {
            if (!isMask) {
                return new AccessResult(ErrorCode.COMMON_FACE_NOT_FOUND, ErrorCode.COMMON_NO_MASK);
            }
        }

        AccessResult result = processResult(machineType, ErrorCode.COMMON_FACE_NOT_FOUND);
        return result;
    }

    private String checkAccess(String personId, int accessType) {
        try {
            PersonDB personDB = database.getPerson(personId);
            switch (accessType) {
                case Constants.AccessType.FACE_RECOGNIZE:
                    if (personDB.getPersonType() == Constants.PersonType.STAFF) {
                        return checkStaff(personId);
                    } else {
                        return checkVisitor(personId);
                    }

                case Constants.AccessType.CARD_RECOGNIZE:
                    return checkCard(personId);
            }
        } catch (Exception ex) {
            LogResponseServer.getInstance(context).responseLog("Error checkAccess: " + personId + " - " + ex.getMessage());
        }
        return ErrorCode.COMMON_NOT_ACCESS;
    }

    private String checkStaff(String personId) {
        boolean blOutOffServiceTime = false;
        boolean blNotAccess = false;

        //On/Off kiểm tra quyền ra vào
        boolean checkAccessBusiness = pref.getBoolean(Constants.PREF_USE_BUSINESS_CHECK, true);
        if (!checkAccessBusiness) {
            return ErrorCode.COMMON_ACCESS_VALID;
        }

        //Kiểm tra nhóm quyền truy nhập
        MachineDB machine = database.getMachineByImei(BaseUtil.getImeiNumber(context));
        if (machine == null) {
            machine = ConfigUtil.getMachine();
        }
        List<PersonGroupDB> lsPersonGroup = database.getListPersonGroup(personId);
        List<GroupAccessDB> lsGroupAccess = database.getListGroupAccess(machine.getMachineId());

        if (lsGroupAccess.isEmpty()) {
            return ErrorCode.COMMON_NOT_ACCESS;
        }

        //Kiểm tra thêm giờ ra vào cụ thể gán cho từng người
        boolean blAccessTimeExpired = true;
        List<PersonAccessDB> lsPersonAccess = database.getListPersonAccess(personId, machine.getMachineId());
        if (lsPersonAccess.size() > 0) {
            for (PersonAccessDB personAccessDB : lsPersonAccess) {
                String fromDate = personAccessDB.getFromdate();
                String toDate = personAccessDB.getTodate();

                Date dtFromDate = StringUtils.convertStringToDate(fromDate, "yyyy-MM-dd HH:mm:ss");
                Date dtToDate = StringUtils.convertStringToDate(toDate, "yyyy-MM-dd HH:mm:ss");
                Date dtCurrent = Calendar.getInstance().getTime();

                if (dtFromDate != null && dtToDate == null) { //Từ ngày khác null & đến ngày null
                    if (dtCurrent.compareTo(dtFromDate) > 0) {
                        blAccessTimeExpired = false;
                    }
                } else if (dtFromDate == null && dtToDate != null) { //Từ ngày null & đến ngày khác null
                    if (dtCurrent.compareTo(dtToDate) < 0) {
                        blAccessTimeExpired = false;
                    }
                } else if (dtFromDate != null && dtToDate != null) { //Từ ngày và đến ngày đều khác null
                    if (dtCurrent.compareTo(dtFromDate) > 0 && dtCurrent.compareTo(dtToDate) < 0) {
                        blAccessTimeExpired = false;
                    }
                } else { //Từ ngày và đến ngày đều null
                    blAccessTimeExpired = false;
                }
            }
        } else {
            blAccessTimeExpired = false;
        }

        if (blAccessTimeExpired) {
            return ErrorCode.COMMON_EXPIRED;
        }
        //End

        for (PersonGroupDB personGroupDB : lsPersonGroup) {
            int groupId = personGroupDB.getGroupId();

            List<GroupAccessDB> lsSubGroupAccess = //database.getListGroupAccessByGroup(groupId);
                    lsGroupAccess.stream()
                            .filter(item -> item.getGroupId() == groupId)
                            .collect(Collectors.toList());

            if (lsSubGroupAccess == null || lsSubGroupAccess.size() == 0) {
                blNotAccess = true;
                continue;
            }

            for (GroupAccessDB groupAccessDB : lsSubGroupAccess) {
                int timeSegId = groupAccessDB.getTimeSegId();
                AccessTimeSegDB timeSeg = database.getAccessTimeSeg(timeSegId);

                AccessTimeSegDBConvert time = ParseModel.parseToTimeSegModel(timeSeg);
                boolean allowTime = checkExistTime(time);
                if (allowTime) {
                    return ErrorCode.COMMON_ACCESS_VALID;
                }
                blOutOffServiceTime = true;
            }
        }

        if (blOutOffServiceTime) {
            return ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME;
        }

        if (blNotAccess) {
            return ErrorCode.COMMON_NOT_ACCESS;
        }

        return ErrorCode.COMMON_NOT_ACCESS;
    }

    private String checkVisitor(String personId) {
        MachineDB machine = database.getMachineByImei(BaseUtil.getImeiNumber(context));
        List<PersonAccessDB> lsPersonAccess = database.getListPersonAccess(personId, machine.getMachineId());

        for (PersonAccessDB personAccessDB : lsPersonAccess) {
            String fromDate = personAccessDB.getFromdate();
            String toDate = personAccessDB.getTodate();

            Date dtFromDate = StringUtils.convertStringToDate(fromDate, "yyyy-MM-dd HH:mm:ss");
            Date dtToDate = StringUtils.convertStringToDate(toDate, "yyyy-MM-dd HH:mm:ss");
            Date dtCurrent = Calendar.getInstance().getTime();

            if (dtFromDate != null && dtToDate != null && dtCurrent.compareTo(dtFromDate) > 0 && dtCurrent.compareTo(dtToDate) < 0) {
                return ErrorCode.COMMON_ACCESS_VALID;
            }
            continue;
        }

        return ErrorCode.COMMON_NOT_ACCESS;
    }

    private String checkCard(String personId) {
        boolean blOutOffServiceTime = false;
        boolean blNotAccess = false;

        MachineDB machine = database.getMachineByImei(BaseUtil.getImeiNumber(context));
        List<PersonGroupDB> lsPersonGroup = database.getListPersonGroup(personId);
        List<GroupAccessDB> lsGroupAccess = database.getListGroupAccess(machine.getMachineId());

        if (lsPersonGroup.isEmpty() || lsGroupAccess.isEmpty()) {
            return ErrorCode.COMMON_CARD_NOT_ACCESS;
        }

        //Kiểm tra thêm giờ ra vào cụ thể gán cho từng người
        boolean blAccessTimeExpired = true;
        List<PersonAccessDB> lsPersonAccess = database.getListPersonAccess(personId, machine.getMachineId());
        if (lsPersonAccess.size() > 0) {
            for (PersonAccessDB personAccessDB : lsPersonAccess) {
                String fromDate = personAccessDB.getFromdate();
                String toDate = personAccessDB.getTodate();

                Date dtFromDate = StringUtils.convertStringToDate(fromDate, "yyyy-MM-dd HH:mm:ss");
                Date dtToDate = StringUtils.convertStringToDate(toDate, "yyyy-MM-dd HH:mm:ss");
                Date dtCurrent = Calendar.getInstance().getTime();

                if (dtFromDate != null && dtToDate != null && dtCurrent.compareTo(dtFromDate) > 0 && dtCurrent.compareTo(dtToDate) < 0) {
                    blAccessTimeExpired = false;
                }
            }
        } else {
            blAccessTimeExpired = false;
        }

        if (blAccessTimeExpired) {
            return ErrorCode.COMMON_EXPIRED;
        }
        //End

        for (PersonGroupDB personGroupDB : lsPersonGroup) {
            int groupId = personGroupDB.getGroupId();

            List<GroupAccessDB> lsSubGroupAccess = //database.getListGroupAccessByGroup(groupId);
                    lsGroupAccess.stream()
                            .filter(item -> item.getGroupId() == groupId)
                            .collect(Collectors.toList());

            if (lsSubGroupAccess == null || lsSubGroupAccess.size() == 0) {
                blNotAccess = true;
                continue;
            }

            for (GroupAccessDB groupAccessDB : lsSubGroupAccess) {
                int timeSegId = groupAccessDB.getTimeSegId();
                AccessTimeSegDB timeSeg = database.getAccessTimeSeg(timeSegId);

                AccessTimeSegDBConvert time = ParseModel.parseToTimeSegModel(timeSeg);
                boolean allowTime = checkExistTime(time);
                if (allowTime) {
                    return ErrorCode.COMMON_CARD_ACCESS_VALID;
                }
                blOutOffServiceTime = true;
            }
        }

        if (blOutOffServiceTime) {
            return ErrorCode.COMMON_CARD_ACCESS_OUT_OFF_SERVICE_TIME;
        }

        if (blNotAccess) {
            return ErrorCode.COMMON_CARD_NOT_ACCESS;
        }

        return ErrorCode.COMMON_CARD_NOT_ACCESS;
    }

    private AccessResult processResult(int machineFunction, String errorCode) {
        AccessResult accessResult;

        switch (machineFunction) {
            case Constants.CHECK_IN:
                accessResult = checkinBussiness(errorCode);
                break;

            case Constants.CHECK_OUT:
                accessResult = checkoutBussiness(errorCode);
                break;

            case Constants.TIME_KEEPING:
                accessResult = timeKeepingBusiness(errorCode);
                break;

            case Constants.CANTEEN:
                accessResult = timeKeepingBusiness(errorCode);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + machineFunction);
        }
        return accessResult;
    }

    public AccessResult timeKeepingBusiness(String errorCode) {
        switch (errorCode) {
            case ErrorCode.COMMON_FACE_NOT_FOUND:
                return new AccessResult(ErrorCode.TIMEKEEPING_FACE_NOT_FOUND, ErrorCode.COMMON_FACE_NOT_FOUND);

            case ErrorCode.COMMON_NOT_ACCESS:
                return new AccessResult(ErrorCode.TIMEKEEPING_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                return new AccessResult(ErrorCode.TIMEKEEPING_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_ACCESS_VALID:
                return new AccessResult(ErrorCode.TIMEKEEPING_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_CARD_NOT_ACCESS:
                return new AccessResult(ErrorCode.TIMEKEEPING_CARD_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_CARD_ACCESS_OUT_OFF_SERVICE_TIME:
                return new AccessResult(ErrorCode.TIMEKEEPING_CARD_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_CARD_ACCESS_VALID:
                return new AccessResult(ErrorCode.TIMEKEEPING_CARD_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_EXPIRED:
                return new AccessResult(ErrorCode.COMMON_EXPIRED, ErrorCode.COMMON_EXPIRED);

            default:
                return new AccessResult(ErrorCode.TIMEKEEPING_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);
        }
    }

    public AccessResult checkinBussiness(String errorCode) {
        switch (errorCode) {
            case ErrorCode.COMMON_FACE_NOT_FOUND:
                return new AccessResult(ErrorCode.CHECKIN_FACE_NOT_FOUND, ErrorCode.COMMON_FACE_NOT_FOUND);

            case ErrorCode.COMMON_NOT_ACCESS:
                return new AccessResult(ErrorCode.CHECKIN_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                return new AccessResult(ErrorCode.CHECKIN_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_ACCESS_VALID:
                return new AccessResult(ErrorCode.CHECKIN_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_CARD_NOT_ACCESS:
                return new AccessResult(ErrorCode.CHECKIN_CARD_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_CARD_ACCESS_OUT_OFF_SERVICE_TIME:
                return new AccessResult(ErrorCode.CHECKIN_CARD_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_CARD_ACCESS_VALID:
                return new AccessResult(ErrorCode.CHECKIN_CARD_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_EXPIRED:
                return new AccessResult(ErrorCode.COMMON_EXPIRED, ErrorCode.COMMON_EXPIRED);

            default:
                return new AccessResult(ErrorCode.CHECKIN_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);
        }
    }

    public AccessResult checkoutBussiness(String errorCode) {
        switch (errorCode) {
            case ErrorCode.COMMON_FACE_NOT_FOUND:
                return new AccessResult(ErrorCode.CHECKOUT_FACE_NOT_FOUND, ErrorCode.COMMON_FACE_NOT_FOUND);

            case ErrorCode.COMMON_NOT_ACCESS:
                return new AccessResult(ErrorCode.CHECKOUT_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                return new AccessResult(ErrorCode.CHECKOUT_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_ACCESS_VALID:
                return new AccessResult(ErrorCode.CHECKOUT_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_CARD_NOT_ACCESS:
                return new AccessResult(ErrorCode.CHECKOUT_CARD_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);

            case ErrorCode.COMMON_CARD_ACCESS_OUT_OFF_SERVICE_TIME:
                return new AccessResult(ErrorCode.CHECKOUT_CARD_OUT_OF_SERVICE_TIME, ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);

            case ErrorCode.COMMON_CARD_ACCESS_VALID:
                return new AccessResult(ErrorCode.CHECKOUT_CARD_VALID, ErrorCode.COMMON_ACCESS_VALID);

            case ErrorCode.COMMON_EXPIRED:
                return new AccessResult(ErrorCode.COMMON_EXPIRED, ErrorCode.COMMON_EXPIRED);

            default:
                return new AccessResult(ErrorCode.CHECKOUT_NOT_ACCESS, ErrorCode.COMMON_NOT_ACCESS);
        }
    }

    public boolean checkExistTime(AccessTimeSegDBConvert model) {
        boolean blExists = false;

        try {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1)
                dayOfWeek = 8; //Nếu là chủ nhật thì gán bằng 8 để so sánh với db cho dễ

            switch (dayOfWeek) {
                case 2:
                    blExists = compareTimeSeg(model.getMondayStart1(), model.getMondayEnd1(),
                            model.getMondayStart2(), model.getMondayEnd2(),
                            model.getMondayStart3(), model.getMondayEnd3(),
                            model.getMondayStart4(), model.getMondayEnd4());
                    break;
                case 3:
                    blExists = compareTimeSeg(model.getTuesdayStart1(), model.getTuesdayEnd1(),
                            model.getTuesdayStart2(), model.getTuesdayEnd2(),
                            model.getTuesdayStart3(), model.getTuesdayEnd3(),
                            model.getTuesdayStart4(), model.getTuesdayEnd4());
                    break;
                case 4:
                    blExists = compareTimeSeg(model.getWednesdayStart1(), model.getWednesdayEnd1(),
                            model.getWednesdayStart2(), model.getWednesdayEnd2(),
                            model.getWednesdayStart3(), model.getWednesdayEnd3(),
                            model.getWednesdayStart4(), model.getWednesdayEnd4());
                    break;
                case 5:
                    blExists = compareTimeSeg(model.getThusdayStart1(), model.getThusdayEnd1(),
                            model.getThusdayStart2(), model.getThusdayEnd2(),
                            model.getThusdayStart3(), model.getThusdayEnd3(),
                            model.getThusdayStart4(), model.getThusdayEnd4());
                    break;
                case 6:
                    blExists = compareTimeSeg(model.getFridayStart1(), model.getFridayEnd1(),
                            model.getFridayStart2(), model.getFridayEnd2(),
                            model.getFridayStart3(), model.getFridayEnd3(),
                            model.getFridayStart4(), model.getFridayEnd4());
                    break;
                case 7:
                    blExists = compareTimeSeg(model.getSaturdayStart1(), model.getSaturdayEnd1(),
                            model.getSaturdayStart2(), model.getSaturdayEnd2(),
                            model.getSaturdayStart3(), model.getSaturdayEnd3(),
                            model.getSaturdayStart4(), model.getSaturdayEnd4());
                    break;
                case 8:
                    blExists = compareTimeSeg(model.getSundayStart1(), model.getSundayEnd1(),
                            model.getSundayStart2(), model.getSundayEnd2(),
                            model.getSundayStart3(), model.getSundayEnd3(),
                            model.getSundayStart4(), model.getSundayEnd4());
                    break;
            }
        } catch (Exception ex) {
            LogResponseServer.getInstance(context).responseLog("Error checkExistTime: " + ex.getMessage());
        }

        return blExists;
    }

    private static boolean compareTimeSeg(Date dtStart1, Date dtEnd1, Date dtStart2, Date dtEnd2, Date dtStart3, Date dtEnd3, Date dtStart4, Date dtEnd4) throws Exception {
        Date dtCurrentTime = Calendar.getInstance().getTime();
        Log.d("TRACE-CANTEEN", "compareTimeSeg input: " +
                dtStart1 + " - " + dtEnd1 + " | " +
                dtStart2 + " - " + dtEnd2 + " | " +
                dtStart3 + " - " + dtEnd3 + " | " +
                dtStart4 + " - " + dtEnd4);

        if (dtCurrentTime.after(dtStart1) && dtCurrentTime.before(dtEnd1)) {
            return true;
        }

        if (dtCurrentTime.after(dtStart2) && dtCurrentTime.before(dtEnd2)) {
            return true;
        }

        if (dtCurrentTime.after(dtStart3) && dtCurrentTime.before(dtEnd3)) {
            return true;
        }

        if (dtCurrentTime.after(dtStart4) && dtCurrentTime.before(dtEnd4)) {
            return true;
        }

        return false;
    }

    public List<CompareResult> getTwins(String personId) {
        List<CompareResult> lsTwinOfPerson = new ArrayList<>();
        List<TwinDB> lsTwins = database.getListTwinOfPerson(personId);

        //Phải add thêm chính đối tượng hiện tại vào danh sách sinh đôi
        if (lsTwins.size() > 0) {
            TwinDB twinDB = new TwinDB();
            twinDB.setPersonId(personId);
            twinDB.setSimilarPersonId(personId);
            lsTwins.add(twinDB);
        }

        for (TwinDB twin : lsTwins) {
            try {
                PersonDB personDB = database.getPerson(twin.getSimilarPersonId());
                FaceDB faceDB = database.getFaceByPerson(personDB.getPersonId());

                CompareResult compareResult = new CompareResult();
                compareResult.setPersonId(personDB.getPersonId());
                compareResult.setPersonCode(personDB.getPersonCode());
                compareResult.setFullName(personDB.getFullName());
                compareResult.setFacePath(faceDB.getFacePath());
                compareResult.setPersonType("" + personDB.getPersonType());
                compareResult.setPosition(personDB.getPosition());
                compareResult.setJobDuties(personDB.getJobDuties());

                lsTwinOfPerson.add(compareResult);
            } catch (Exception ex) {
                Log.e("Error getTwins", ex.getMessage());
            }
        }
        return lsTwinOfPerson;
    }

    private void doSearchCanteenPermission(String url, FacePermissionInput facePermission, CompareResult compareResult) throws Exception {
        String jsonObject = mGson.toJson(facePermission, FacePermissionInput.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    try {
                        String jsonResponse = response.toString();
                        Type responseType = new TypeToken<ResponseDto<CompareResult>>() {
                        }.getType();
                        ResponseDto<CompareResult> responseDto = mGson.fromJson(jsonResponse, responseType);

                        if (responseDto.getStatus() != 200) {
                            if (responseDto.getError().length() > 200) {
                                BaseUtil.broadcastShowMessage(context, "Lỗi kiểm tra quyền canteen");
                            } else {
                                BaseUtil.broadcastShowMessage(context, "Lỗi kiểm tra quyền canteen " + responseDto.getError());
                            }
                            return;
                        }

                        if (responseDto.getData() == null) {
                            BaseUtil.broadcastShowMessage(context, "Nội dung api canteen không hợp lệ");
                            return;
                        }
                        CompareResult compareResultApi = responseDto.getData();

                        compareResult.setSummaryCode(compareResultApi.getSummaryCode());
                        compareResult.setDetailCode(compareResultApi.getDetailCode());
                        compareResult.setNote(compareResultApi.getNote());

                        if (compareResultApi.getSummaryCode().equals(ErrorCode.COMMON_ACCESS_VALID)) {
                            SingletonObject.getInstance(context).getMainActivity().processResult(compareResult, false);
                        } else {
                            SingletonObject.getInstance(context).getMainActivity().processResult(compareResult, false);
                        }

                        BaseUtil.broadcastShowMessage(context, "");
                    } catch (Exception ex) {
                        Toast.makeText(context, "LỖI API " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("com.android.volley.TimeoutError")) {
                        Toast.makeText(context, "HẾT THỜI GIAN XỬ LÝ TÌM KIẾM", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "LỖI API TÌM KIẾM", Toast.LENGTH_SHORT).show();
                    }
                    //mainActivity.showWatingUi(false);
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    int statusCode = response.statusCode;
                    if (statusCode != 200) {
                        Toast.makeText(context, "LỖI API TÌM KIẾM " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("token", TokenEngineUtils.getToken());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void confirmCanteenUsage(String url, CompareResult compareResult) throws Exception {
        EventDB eventDB = getEventDb(compareResult);
        String jsonObject = mGson.toJson(eventDB, EventDB.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    try {
                        String jsonResponse = response.toString();
                        Type responseType = new TypeToken<ResponseDto<Boolean>>() {
                        }.getType();
                        ResponseDto<Boolean> responseDto = mGson.fromJson(jsonResponse, responseType);

                        SingletonObject.getInstance(context).getMainActivity().hideDialog();

                        if (responseDto.getStatus() != 200) {
                            if (responseDto.getError().length() > 200) {
                                Toast.makeText(context, "Lỗi api xác nhận sử dụng bữa ăn canteen", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Lỗi api xác nhận sử dụng bữa ăn canteen " + responseDto.getError(), Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (responseDto.getData() == null) {
                            Toast.makeText(context, "Nội dung api xác nhận bữa ăn không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TicketFormatter.printFormattedCanteenTicket(context, compareResult);
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(context, "LỖI API XÁC NHẬN CANTEEN " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    if (cause.contains("com.android.volley.TimeoutError")) {
                        Toast.makeText(context, "HẾT THỜI GIAN XỬ LÝ TÌM KIẾM", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "LỖI API TÌM KIẾM", Toast.LENGTH_SHORT).show();
                    }
                    //mainActivity.showWatingUi(false);
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    int statusCode = response.statusCode;
                    if (statusCode != 200) {
                        Toast.makeText(context, "LỖI API TÌM KIẾM " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("token", TokenEngineUtils.getToken());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void confirmCanteenUsageLocal(CompareResult compareResult) {
        try {
            //Tạo bản ghi event giống như online
            EventDB eventDB = getEventDb(compareResult);
            String jsonObject = mGson.toJson(eventDB, EventDB.class);
            JSONObject jObject = new JSONObject(jsonObject); //

            // Ghi event vào local DB để sync sau
            eventDB.setUploadStatus(Constants.UPLOAD_PENDING);  // đánh dấu chưa sync
            database.insertEventLog(eventDB);

            //In vé ăn
            TicketFormatter.printFormattedCanteenTicket(context, compareResult);

            //Update quota (ăn trong ngày/tháng)
            String todayIso = StringUtils.currentDateSQLiteformat();
            Calendar now = Calendar.getInstance();
            int month = now.get(Calendar.MONTH) + 1;
            int year = now.get(Calendar.YEAR);
            database.increaseDailyAndMonthly(compareResult.getPersonId(), todayIso);

            //Toast thành công giống như API success
            Toast.makeText(context, "Đã xác nhận suất ăn offline thành công", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Toast.makeText(context, "Lỗi xử lý offline: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("CANTEEN_LOCAL", "❌ confirmCanteenUsageLocal FAILED", ex);
        }
    }


    //Insert event Log
    private EventDB getEventDb(CompareResult compareResult) {
        EventDB model = new EventDB();

        try {
            String currentDate = StringUtils.currentDateSQLiteformat();
            String currentTime = StringUtils.currentDatetimeSQLiteformat();
            String eventId = UUID.randomUUID().toString();

            logger.info("Start get event canteen - EventId: " + eventId + " - PersonId: " + compareResult.getPersonId());

            byte[] bFace = new byte[0];
            try {
                FaceInfoCapture faceInfoCapture = compareResult.getFaceInfoCapture();
                bFace = FaceServer.getInstance().captureFacePhoto(faceInfoCapture.getFaceCapture(), faceInfoCapture.getFaceInfo(), compareResult.getPreviewSize());
            } catch (Exception ex) {

            }

            model.setEventId(eventId);
            model.setPersonId(compareResult.getPersonId());
            String fileBase64 = BaseUtil.convertByteToBase64(bFace);
            model.setFace64(fileBase64);
            model.setMachineId(compareResult.getMachineId());
            model.setAccessDate(currentDate);
            model.setAccessTime(currentTime);
            model.setAccessType(String.valueOf(Constants.FACE_RECOGNIZE));
            model.setScoreMatch(compareResult.getSimilar());
            model.setTemperature(compareResult.getTemperature());
            model.setWearMask(compareResult.isMask() ? 1 : 0);
            model.setErrorCode(compareResult.getDetailCode());
            model.setStatus(Constants.EVENT_STATUS_WAIT_SYNC);
            model.setCompId(compareResult.getCompId());
            model.setDeviceCode(BaseUtil.getImeiNumber(context));
        } catch (Exception ex) {
            logger.error("Error get event entity " + ex.getMessage());
        }

        return model;
    }

    private void setPersonMeta(CompareResult cmp, String personId) {
        cmp.setPersonId(personId);
    }

    public void checkCanteenPermission(String personId, String deviceCode, CompareResult compareResult) {
        Log.d("CANTEEN-FLOW", "START checkCanteenPermission: personId=" + personId + ", deviceCode=" + deviceCode);

        // 1. Kiểm tra nhân viên tồn tại
        PersonDB personEntity = database.getPerson(personId);
        if (personEntity == null) {
            compareResult.setNote("ID nhân viên không tồn tại");
            compareResult.setSummaryCode(ErrorCode.COMMON_FACE_NOT_FOUND);
            compareResult.setDetailCode(ErrorCode.COMMON_FACE_NOT_FOUND);
            return;
        }
        // 2. Kiểm tra trạng thái nhân viên
        if (personEntity.getStatus() == 0) {
            compareResult.setNote("TÀI KHOẢN NGỪNG HOẠT ĐỘNG");
            compareResult.setSummaryCode(ErrorCode.COMMON_ACCOUNT_SUPPEND);
            compareResult.setDetailCode(ErrorCode.COMMON_ACCOUNT_SUPPEND);
            return;
        }

        // 3. Kiểm tra quyền truy cập (PERSON_ACCESS)
        List<PersonAccessDB> lsPersonAccess = database.getListPersonAccess(personId, 0);
        Date now = Calendar.getInstance().getTime();
        boolean hasValidAccess = false;
        if (lsPersonAccess != null && !lsPersonAccess.isEmpty()) {
            for (PersonAccessDB pa : lsPersonAccess) {
                Date dtFrom = StringUtils.convertStringToDate(pa.getFromdate(), "yyyy-MM-dd HH:mm:ss");
                Date dtTo = StringUtils.convertStringToDate(pa.getTodate(), "yyyy-MM-dd HH:mm:ss");
                // fromdate > now: chưa đến ngày hiệu lực -> bỏ qua
                if (dtFrom != null && dtFrom.after(now)) continue;
                // todate < now: đã hết hạn -> bỏ qua
                if (dtTo != null && dtTo.before(now)) continue;
                // Chỉ cần có 1 record hợp lệ là pass
                hasValidAccess = true;
                break;
            }
        }
        if (!hasValidAccess) {
            compareResult.setNote("QUYỀN TRUY CẬP HẾT HẠN");
            compareResult.setSummaryCode(ErrorCode.COMMON_EXPIRED);
            compareResult.setDetailCode(ErrorCode.COMMON_EXPIRED);
            return;
        }

        // 4. Kiểm tra nhóm truy cập (group)
        List<PersonGroupDB> lsPersonGroup = database.getListPersonGroup(personId);
        if (lsPersonGroup == null || lsPersonGroup.isEmpty()) {
            compareResult.setNote("Nhóm truy cập nhân viên không hợp lệ");
            compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
            compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
            return;
        }
        List<Integer> lsAllGroup = new ArrayList<>();
        for (PersonGroupDB group : lsPersonGroup) {
            lsAllGroup.add(group.getGroupId());
        }

        // 5. Kiểm tra thiết bị
        MachineDB machine = database.getMachineByImei(deviceCode);
        if (machine == null) {
            compareResult.setNote("Thông tin thiết bị không tồn tại " + deviceCode);
            compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
            compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
            return;
        }

// 6. Kiểm tra group access hợp lệ (phải có ít nhất 1 group access đúng máy + đúng giờ)
        boolean blOutOffServiceTime = false;
        boolean blNotAccessPermission = false;
        List<GroupAccessDB> lsGroupAccessValid = new ArrayList<>();

        Log.d("CANTEEN-DEBUG", "Máy hiện tại: machineId = " + machine.getMachineId());
        Log.d("CANTEEN-DEBUG", "DANH SÁCH NHÓM của user: " + lsAllGroup);

        for (Integer groupId : lsAllGroup) {
            List<GroupAccessDB> lsGroupAccess = database.getListGroupAccessByGroup(groupId);
            Log.d("CANTEEN-DEBUG", "---- groupId=" + groupId + " có " +
                    (lsGroupAccess != null ? lsGroupAccess.size() : 0) + " groupAccess");

            if (lsGroupAccess == null || lsGroupAccess.isEmpty()) {
                Log.d("CANTEEN-DEBUG", "    => Không có groupAccess nào cho groupId=" + groupId);
                blNotAccessPermission = true;
                continue;
            }
            for (GroupAccessDB groupAccess : lsGroupAccess) {
                Log.d("CANTEEN-DEBUG", "    groupAccess.id=" + groupAccess.getId()
                        + " | groupId=" + groupAccess.getGroupId()
                        + " | machineId=" + groupAccess.getMachineId()
                        + " | timeSegId=" + groupAccess.getTimeSegId()
                        + " | accessTurnType=" + groupAccess.getAccessTurnType()
                        + " | accessTurnNumber=" + groupAccess.getAccessTurnNumber());

                if (machine.getMachineId() != groupAccess.getMachineId()) {
                    Log.d("CANTEEN-DEBUG", "    => [SKIP] Không khớp máy! (current=" + machine.getMachineId()
                            + ", groupAccess=" + groupAccess.getMachineId() + ")");
                    blNotAccessPermission = true;
                    continue;
                }

                int timeSegId = groupAccess.getTimeSegId();
                AccessTimeSegDB timeSeg = database.getAccessTimeSeg(timeSegId);

                if (timeSeg == null) {
                    Log.d("CANTEEN-DEBUG", "    => [SKIP] Không tìm thấy time segment id: " + timeSegId);
                    blOutOffServiceTime = true;
                    continue;
                }
                AccessTimeSegDBConvert timeSegObj = ParseModel.parseToTimeSegModel(timeSeg);
                boolean timeValid = checkExistTime(timeSegObj);
                Log.d("CANTEEN-DEBUG", "        checkExistTime timeSegId=" + timeSegId + " -> " + timeValid);

                if (!timeValid) {
                    Log.d("CANTEEN-DEBUG", "    => [SKIP] Ngoài giờ phục vụ!");
                    blOutOffServiceTime = true;
                    continue;
                }

                // Đủ quyền + đúng máy + đúng giờ
                Log.d("CANTEEN-DEBUG", "    => [PASS] groupAccess HỢP LỆ! Thêm vào lsGroupAccessValid!");
                lsGroupAccessValid.add(groupAccess);

                // Khi đã có hợp lệ, reset các flag lỗi này lại:
                blOutOffServiceTime = false;
                blNotAccessPermission = false;
            }
        }
        Log.d("CANTEEN-DEBUG", "TỔNG SỐ groupAccess hợp lệ tìm được: " + lsGroupAccessValid.size());

        if (lsGroupAccessValid.isEmpty()) {
            if (blOutOffServiceTime) {
                Log.d("CANTEEN-DEBUG", "==> Lỗi: Ngoài giờ phục vụ!");
                compareResult.setNote("Ngoài giờ phục vụ");
                compareResult.setSummaryCode(ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);
                compareResult.setDetailCode(ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME);
            } else if (blNotAccessPermission) {
                Log.d("CANTEEN-DEBUG", "==> Lỗi: Không có quyền thực hiện (Không có groupAccess hợp lệ cho máy này)!");
                compareResult.setNote("Không có quyền thực hiện");
                compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
                compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
            } else {
                Log.d("CANTEEN-DEBUG", "==> Lỗi: Phân quyền canteen không hợp lệ!");
                compareResult.setNote("Phân quyền canteen không hợp lệ");
                compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
                compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
            }
            return;
        } else {
            Log.d("CANTEEN-DEBUG", "==> Đã tìm thấy ÍT NHẤT 1 groupAccess hợp lệ! Cho pass bước này.");
        }


        // 7. Kiểm tra cấu hình số lượt ăn trong tháng
        MealByMonthDB mealByMonthEntity = database.getMealQuota(Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
        if (mealByMonthEntity == null) {
            compareResult.setNote("Thông tin cấu hình số lượt ăn trong tháng không tồn tại");
            compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
            compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
            return;
        }
        int monthUsed = database.countMonthly(personId, Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
        if (monthUsed >= mealByMonthEntity.getEatCount()) {
            compareResult.setNote("Đã sử dụng hết số lượt ăn trong tháng");
            compareResult.setSummaryCode(ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH);
            compareResult.setDetailCode(ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH);
            return;
        }

        // 8. Kiểm tra lượt ăn/ngày cho từng group access hợp lệ
        boolean isUsedUp = false;
        int dailyUsed = database.countDaily(personId, StringUtils.currentDateSQLiteformat());
        for (GroupAccessDB groupAccess : lsGroupAccessValid) {
            if (groupAccess.getAccessTurnType() == null || groupAccess.getAccessTurnType() == 0) {
                compareResult.setNote("Thông tin hợp lệ");
                compareResult.setRemainTurnNumber(0);
                compareResult.setSummaryCode(ErrorCode.COMMON_ACCESS_VALID);
                compareResult.setDetailCode(ErrorCode.COMMON_ACCESS_VALID);
                return;
            }
            Integer totalAccessTimeNumber = groupAccess.getAccessTurnNumber();
            if (totalAccessTimeNumber == null || totalAccessTimeNumber == 0) {
                isUsedUp = true;
                continue;
            }
            if (dailyUsed < totalAccessTimeNumber) {
                int remainNumber = totalAccessTimeNumber - dailyUsed;
                compareResult.setNote("Thông tin hợp lệ");
                compareResult.setRemainTurnNumber(remainNumber);
                compareResult.setSummaryCode(ErrorCode.COMMON_ACCESS_VALID);
                compareResult.setDetailCode(ErrorCode.COMMON_ACCESS_VALID);
                return;
            }
            isUsedUp = true;
        }
        if (isUsedUp) {
            compareResult.setNote("Đã sử dụng hết số lượt ăn trong ngày");
            compareResult.setSummaryCode(ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY);
            compareResult.setDetailCode(ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY);
            return;
        }

        // Fallback cuối cùng
        compareResult.setNote("Không có quyền thực hiện");
        compareResult.setSummaryCode(ErrorCode.COMMON_NOT_ACCESS);
        compareResult.setDetailCode(ErrorCode.COMMON_NOT_ACCESS);
    }

}

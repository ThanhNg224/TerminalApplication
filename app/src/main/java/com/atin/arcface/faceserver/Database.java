package com.atin.arcface.faceserver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.atin.arcface.common.Constants;
import com.atin.arcface.model.AccessTimeSegDB;
import com.atin.arcface.model.CardDB;
import com.atin.arcface.model.EventDB;
import com.atin.arcface.model.EventReportModel;
import com.atin.arcface.model.FaceDB;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.model.GroupAccessDB;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.model.PersonAccessDB;
import com.atin.arcface.model.PersonDB;
import com.atin.arcface.model.PersonGroupDB;
import com.atin.arcface.model.PersonReport;
import com.atin.arcface.model.TwinDB;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends SQLiteOpenHelper {

    // Phiên bản
    private static final int DATABASE_VERSION = 6;

    // Tên cơ sở dữ liệu.
    private static final String TAG = "Database";
    private static final String DATABASE_NAME = "ATIN_Manager";

    public static final String PERSON = "PERSON";

    public static final String FACE = "FACE";

    public static final String CARD = "CARD";

    public static final String PERSON_GROUP = "PERSON_GROUP";

    public static final String ACCESS_TIME_SEG = "ACCESS_TIME_SEG";

    public static final String MACHINE = "MACHINE";

    public static final String GROUP_ACCESS = "GROUP_ACCESS";

    public static final String EVENT = "EVENT";

    public static final String PERSON_ACCESS = "PERSON_ACCESS";

    public static final String TWIN = "TWIN";

    public static final String LOG = "LOG";

    private Context mContext;
    private Logger logger;

    public Database(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        checkTempDatabase();
        logger = Log4jHelper.getLogger("Database");
    }

    //Sẽ xóa và sử dụng updateDatabase sau khi chắc chắn tất cả thiết bị đã được dùng bản sign
    private void checkTempDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            if(!isFieldExist(EVENT, Constants.NOTE)){
                db.execSQL("ALTER TABLE " + EVENT + " ADD COLUMN NOTE TEXT"); //Thêm mới trường NOTE
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean isFieldExist(String tableName, String fieldName)
    {
        boolean isExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("PRAGMA table_info("+tableName+")",null);
        res.moveToFirst();
        do {
            String currentColumn = res.getString(1);
            if (currentColumn.equals(fieldName)) {
                isExist = true;
            }
        } while (res.moveToNext());
        return isExist;
    }
    //Endtemp

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Database.onCreate ... ");

        // Script tạo bảng lưu thông tin nhân viên.
        String scriptPerson = "CREATE TABLE IF NOT EXISTS " + PERSON +"("
                + "PERSON_ID TEXT NOT NULL PRIMARY KEY,"
                + "COMP_ID INTEGER,"
                + "DEPT_ID INTEGER,"
                + "PERSON_CODE TEXT,"
                + "FULL_NAME TEXT,"
                + "POSITION TEXT,"
                + "JOBDUTIES TEXT,"
                + "PERSON_TYPE INTEGER DEFAULT 1,"
                + "STATUS INTEGER, "
                + "VACCINE INTEGER "
                + " )";
        db.execSQL(scriptPerson);

        // Script tạo bảng lưu thông tin nhân viên.
        String scriptFace = "CREATE TABLE IF NOT EXISTS " + FACE +"("
                + "FACE_ID TEXT NOT NULL PRIMARY KEY,"
                + "PERSON_ID TEXT NOT NULL,"
                + "FACE_URL TEXT, "
                + "FACE_PATH TEXT, "
                + "FACE_FEATURE BLOB, "
                + "FEATURE_PATH TEXT, "
                + "FACE_STATUS INTEGER "
                + " )";
        db.execSQL(scriptFace);

        // Script tạo bảng lưu thông tin nhân viên.
        String scriptCard = "CREATE TABLE IF NOT EXISTS " + CARD +"("
                + "CARD_ID TEXT NOT NULL PRIMARY KEY,"
                + "PERSON_ID TEXT NOT NULL,"
                + "CARD_NO TEXT "
                + " )";
        db.execSQL(scriptCard);

        // Script tạo bảng lưu thông tin nhóm người
        String scriptPersonGroup = "CREATE TABLE " + PERSON_GROUP + " ( "
                + "ID INTEGER NOT NULL PRIMARY KEY, "
                + "PERSON_ID TEXT, "
                + "GROUP_ID INTEGER "
                + ") ";
        db.execSQL(scriptPersonGroup);

        // Script tạo bảng lưu thông tin thiết bị
        String scriptMachine = "CREATE TABLE " + MACHINE + " ( "
                + "MACHINE_ID INTEGER NOT NULL PRIMARY KEY, "
                + "COMP_ID INTEGER, "
                + "DEVICE_NAME TEXT, "
                + "DEVICE_TYPE INTEGER, "
                + "DEVICE_FUNCTION INTEGER, "
                + "IPADDRESS TEXT, "
                + "IMEI TEXT, "
                + "MAC TEXT, "
                + "SERVER_IP TEXT, "
                + "SERVER_PORT INTEGER, "
                + "FRAUD_PROOF INTEGER, "
                + "ANGLE_DETECT INTEGER, "
                + "AUTO_START INTEGER, "
                + "AUTO_SAVE_VISITOR INTEGER, "
                + "SHOW_OPTION INTEGER, "
                + "DISTANT_DETECT INTEGER, "
                + "USERNAME TEXT, "
                + "PASSWORD TEXT, "
                + "USERNAME_TOKEN TEXT, "
                + "PASSWORD_TOKEN TEXT, "
                + "LOGO TEXT, "
                + "VOLUME INTEGER, "
                + "BRIGHTNESS INTEGER, "
                + "DELAY INTEGER, "
                + "LED INTEGER, "
                + "FP_THRESHOLD REAL, "
                + "FACE_THRESHOLD REAL, "
                + "TEMPERATURE_THRESHOLD REAL,"
                + "USE_MASK INTEGER,"
                + "USE_TEMPERATURE INTEGER,"
                + "FIRMWARE_VERSION TEXT,"
                + "USE_VACCINE INTEGER,"
                + "USE_PCCOVID INTEGER DEFAULT 0,"
                + "PCCOVID_PHONE TEXT,"
                + "PCCOVID_LOCATION TEXT,"
                + "PCCOVID_TOKEN TEXT,"
                + "DAILY_REBOOT INTEGER DEFAULT 0,"
                + "RESTART_TIME TEXT,"
                + "LANGUAGE TEXT,"
                + "NO_DELAY INTEGER DEFAULT 1,"
                + "NOMASK_QUALITY_THRESHOLD REAL,"
                + "MASK_QUALITY_THRESHOLD REAL,"
                + "REGISTER_QUALITY_THRESHOLD REAL,"
                + "AUTO_SLEEP DEFAULT 0"
                + ") ";
        db.execSQL(scriptMachine);

        //Bảng nhóm giờ truy nhập theo nhóm
        String scriptAccessTime = "CREATE TABLE " + ACCESS_TIME_SEG + " ( "
                + "TIME_SEG_ID INTERGER NOT NULL PRIMARY KEY, "
                + "COMP_ID INTEGER, "
                + "TIME_SEG_NAME TEXT, "
                + "SUNDAY_START1 TEXT, "
                + "SUNDAY_END1 TEXT, "
                + "SUNDAY_START2 TEXT, "
                + "SUNDAY_END2 TEXT, "
                + "SUNDAY_START3 TEXT, "
                + "SUNDAY_END3 TEXT, "
                + "SUNDAY_START4 TEXT, "
                + "SUNDAY_END4 TEXT, "
                + "MONDAY_START1 TEXT, "
                + "MONDAY_END1 TEXT, "
                + "MONDAY_START2 TEXT, "
                + "MONDAY_END2 TEXT, "
                + "MONDAY_START3 TEXT, "
                + "MONDAY_END3 TEXT, "
                + "MONDAY_START4 TEXT, "
                + "MONDAY_END4 TEXT, "
                + "TUESDAY_START1 TEXT, "
                + "TUESDAY_END1 TEXT, "
                + "TUESDAY_START2 TEXT, "
                + "TUESDAY_END2 TEXT, "
                + "TUESDAY_START3 TEXT, "
                + "TUESDAY_END3 TEXT, "
                + "TUESDAY_START4 TEXT, "
                + "TUESDAY_END4 TEXT, "
                + "WEDNESDAY_START1 TEXT, "
                + "WEDNESDAY_END1 TEXT, "
                + "WEDNESDAY_START2 TEXT, "
                + "WEDNESDAY_END2 TEXT, "
                + "WEDNESDAY_START3 TEXT, "
                + "WEDNESDAY_END3 TEXT, "
                + "WEDNESDAY_START4 TEXT, "
                + "WEDNESDAY_END4 TEXT, "
                + "THURSDAY_START1 TEXT, "
                + "THURSDAY_END1 TEXT, "
                + "THURSDAY_START2 TEXT, "
                + "THURSDAY_END2 TEXT, "
                + "THURSDAY_START3 TEXT, "
                + "THURSDAY_END3 TEXT, "
                + "THURSDAY_START4 TEXT, "
                + "THURSDAY_END4 TEXT, "
                + "FRIDAY_START1 TEXT, "
                + "FRIDAY_END1 TEXT, "
                + "FRIDAY_START2 TEXT, "
                + "FRIDAY_END2 TEXT, "
                + "FRIDAY_START3 TEXT, "
                + "FRIDAY_END3 TEXT, "
                + "FRIDAY_START4 TEXT, "
                + "FRIDAY_END4 TEXT, "
                + "SATURDAY_START1 TEXT, "
                + "SATURDAY_END1 TEXT, "
                + "SATURDAY_START2 TEXT, "
                + "SATURDAY_END2 TEXT, "
                + "SATURDAY_START3 TEXT, "
                + "SATURDAY_END3 TEXT, "
                + "SATURDAY_START4 TEXT, "
                + "SATURDAY_END4 TEXT "
                + " ) ";
        db.execSQL(scriptAccessTime);

        //Bảng vùng truy nhập chi tiết
        String scriptGroupAccess = "CREATE TABLE " + GROUP_ACCESS + " ( "
                + "GA_ID INTEGER NOT NULL PRIMARY KEY, "
                + "GROUP_ID INTEGER, "
                + "MACHINE_ID INTEGER, "
                + "TIME_SEG_ID INTEGER "
                + " ) ";
        db.execSQL(scriptGroupAccess);

        // Script tạo bảng lưu thông tin check in, check out
        String scriptEvent = "CREATE TABLE " + EVENT + " ( "
                + "EVENT_ID	TEXT NOT NULL PRIMARY KEY, "
                + "METHOD	INTEGER, "
                + "PERSON_ID TEXT, "
                + "FACE_ID TEXT, "
                + "FINGER_ID TEXT, "
                + "CARD_NO TEXT, "
                + "FACE_IMAGE TEXT, "
                + "MACHINE_ID INTEGER, "
                + "ACCESS_DATE TEXT, "
                + "ACCESS_TIME TEXT, "
                + "ACCESS_TYPE TEXT, "
                + "TEMPERATURE REAL, "
                + "GENDER INTEGER, "
                + "AGE INTEGER, "
                + "WEAR_MASK INTEGER, "
                + "SCORE_MATCH REAL, "
                + "ERROR_CODE TEXT, "
                + "STATUS INTEGER, "
                + "NOTE INTEGER "
                + ") ";
        db.execSQL(scriptEvent);

        // Script tạo bảng lưu thông tin nhóm người
        String scriptPersonsAccess = "CREATE TABLE " + PERSON_ACCESS + " ( "
                + "ID INTEGER NOT NULL PRIMARY KEY, "
                + "PERSON_ID TEXT, "
                + "MACHINE_ID INTEGER, "
                + "FROM_DATE TEXT, "
                + "TO_DATE TEXT, "
                + "IS_DELETE INTEGER "
                + ") ";
        db.execSQL(scriptPersonsAccess);

        // Script tạo bảng lưu log
        String scriptLog = "CREATE TABLE " + LOG + " ( "
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "IMEI TEXT, "
                + "LOG_CONTENT TEXT, "
                + "LOG_DATE TEXT, "
                + "STATUS INTEGER "
                + ") ";
        db.execSQL(scriptLog);

        // Script tạo bảng lưu thông tin anh chị em sinh đôi
        String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                + "ID INTEGER NOT NULL PRIMARY KEY, "
                + "PERSON_ID TEXT, "
                + "SIMILAR_PERSON_ID TEXT "
                + ") ";
        db.execSQL(scriptTwins);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Database.onUpgrade ... ");
        //Lưu ý khi upgrade version data base phải check version cũ và từng version mới để thêm scipt của từng version cũ
        /**
         * Note version
         * 1. Khơi tạo
         * 2. Thêm trường phân biệt khách và nhân viên
         * 3. Thêm trường vaccine
         */

        //VERSION2
        if (oldVersion == 1) {
            if(newVersion == 2){
                //scrip of version2
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN PERSON_TYPE INTEGER DEFAULT 1"); //Thêm mới trường PERSON_TYPE (1: nhân viên, 2: khách)
                db.execSQL("UPDATE " + PERSON + " SET PERSON_TYPE = 1 WHERE PERSON_TYPE IS NULL"); //Cập nhật lại giá trị trường PERSON_TYPE = 1 nếu null
                db.execSQL("ALTER TABLE " + EVENT + " ADD COLUMN NOTE TEXT"); //Thêm mới trường NOTE (lưu thông tin performance thiết bị)

                String scriptPersonsAccess = "CREATE TABLE " + PERSON_ACCESS + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "MACHINE_ID INTEGER, "
                        + "FROM_DATE TEXT, "
                        + "TO_DATE TEXT, "
                        + "IS_DELETE INTEGER "
                        + ") ";
                db.execSQL(scriptPersonsAccess); //Tạo phân quyền truy nhập theo từng nhân sự
            }

            if(newVersion == 3){
                //scrip of version2
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN PERSON_TYPE INTEGER DEFAULT 1"); //Thêm mới trường PERSON_TYPE (1: nhân viên, 2: khách)
                db.execSQL("UPDATE " + PERSON + " SET PERSON_TYPE = 1 WHERE PERSON_TYPE IS NULL"); //Cập nhật lại giá trị trường PERSON_TYPE = 1 nếu null
                db.execSQL("ALTER TABLE " + EVENT + " ADD COLUMN NOTE TEXT"); //Thêm mới trường NOTE (lưu thông tin performance thiết bị)

                String scriptPersonsAccess = "CREATE TABLE " + PERSON_ACCESS + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "MACHINE_ID INTEGER, "
                        + "FROM_DATE TEXT, "
                        + "TO_DATE TEXT, "
                        + "IS_DELETE INTEGER "
                        + ") ";
                db.execSQL(scriptPersonsAccess); //Tạo phân quyền truy nhập theo từng nhân sự

                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine
            }

            if(newVersion == 4){
                //scrip of version2
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN PERSON_TYPE INTEGER DEFAULT 1"); //Thêm mới trường PERSON_TYPE (1: nhân viên, 2: khách)
                db.execSQL("UPDATE " + PERSON + " SET PERSON_TYPE = 1 WHERE PERSON_TYPE IS NULL"); //Cập nhật lại giá trị trường PERSON_TYPE = 1 nếu null
                db.execSQL("ALTER TABLE " + EVENT + " ADD COLUMN NOTE TEXT"); //Thêm mới trường NOTE (lưu thông tin performance thiết bị)

                String scriptPersonsAccess = "CREATE TABLE " + PERSON_ACCESS + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "MACHINE_ID INTEGER, "
                        + "FROM_DATE TEXT, "
                        + "TO_DATE TEXT, "
                        + "IS_DELETE INTEGER "
                        + ") ";
                db.execSQL(scriptPersonsAccess); //Tạo phân quyền truy nhập theo từng nhân sự

                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine

                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null
            }

            if(newVersion == 5){
                //scrip of version2
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN PERSON_TYPE INTEGER DEFAULT 1"); //Thêm mới trường PERSON_TYPE (1: nhân viên, 2: khách)
                db.execSQL("UPDATE " + PERSON + " SET PERSON_TYPE = 1 WHERE PERSON_TYPE IS NULL"); //Cập nhật lại giá trị trường PERSON_TYPE = 1 nếu null
                db.execSQL("ALTER TABLE " + EVENT + " ADD COLUMN NOTE TEXT"); //Thêm mới trường NOTE (lưu thông tin performance thiết bị)

                String scriptPersonsAccess = "CREATE TABLE " + PERSON_ACCESS + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "MACHINE_ID INTEGER, "
                        + "FROM_DATE TEXT, "
                        + "TO_DATE TEXT, "
                        + "IS_DELETE INTEGER "
                        + ") ";
                db.execSQL(scriptPersonsAccess); //Tạo phân quyền truy nhập theo từng nhân sự

                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine

                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null

                //scrip of version5
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN DAILY_REBOOT INTEGER DEFAULT 0"); //Có/không sử dụng đặt lịch khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN RESTART_TIME TEXT"); //Thời gian khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN LANGUAGE TEXT"); //Ngôn ngữ
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NO_DELAY INTEGER DEFAULT 1"); //Xử lý ngay khi thấy khuôn mặt mới không chờ hết delaytime
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NOMASK_QUALITY_THRESHOLD REAL DEFAULT 0.49"); //Ngưỡng điểm chất lượng ảnh không khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN MASK_QUALITY_THRESHOLD REAL DEFAULT 0.29"); //Ngưỡng điểm chất lượng ảnh có khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN REGISTER_QUALITY_THRESHOLD REAL DEFAULT 0.63"); //Ngưỡng điểm chất lượng ảnh tối thiểu đủ để làm ảnh đăng ký
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN AUTO_SLEEP INTEGER DEFAULT 0"); //Tự động sleep màn hinh
            }

            if(newVersion == 6){
                //scrip of version6
                String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "SIMILAR_PERSON_ID TEXT "
                        + ") ";
                db.execSQL(scriptTwins);
            }
        }

        //VERSION3 - 2021-09-18
        if (oldVersion == 2) {
            if(newVersion == 3){
                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine
            }

            if(newVersion == 4){
                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine

                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null
            }

            if(newVersion == 5){
                //scrip of version3
                db.execSQL("ALTER TABLE " + PERSON + " ADD COLUMN VACCINE INTEGER"); //Thêm mới trường VACCINE (0 không có dữ liệu, -1 chưa tiêm, 1 tiêm 1 mũi, 2 tiêm 2 mũi)
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_VACCINE INTEGER"); //Thêm mới trường có/không sử dụng vaccine

                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null

                //scrip of version5
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN DAILY_REBOOT INTEGER DEFAULT 0"); //Có/không sử dụng đặt lịch khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN RESTART_TIME TEXT"); //Thời gian khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN LANGUAGE TEXT"); //Ngôn ngữ
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NO_DELAY INTEGER DEFAULT 1"); //Xử lý ngay khi thấy khuôn mặt mới không chờ hết delaytime
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NOMASK_QUALITY_THRESHOLD REAL DEFAULT 0.49"); //Ngưỡng điểm chất lượng ảnh không khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN MASK_QUALITY_THRESHOLD REAL DEFAULT 0.29"); //Ngưỡng điểm chất lượng ảnh có khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN REGISTER_QUALITY_THRESHOLD REAL DEFAULT 0.63"); //Ngưỡng điểm chất lượng ảnh tối thiểu đủ để làm ảnh đăng ký
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN AUTO_SLEEP INTEGER DEFAULT 0"); //Tự động sleep màn hinh
            }

            if(newVersion == 6){
                //scrip of version6
                String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "SIMILAR_PERSON_ID TEXT "
                        + ") ";
                db.execSQL(scriptTwins);
            }
        }

        //VERSION4 - 2021-12-09
        if (oldVersion == 3) {
            if(newVersion == 4){
                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null
            }

            if(newVersion == 5){
                //scrip of version4
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN USE_PCCOVID INTEGER DEFAULT 0"); //Thêm mới trường có/không sử dụng chức năng check mã pccoid với người lạ không
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_PHONE TEXT"); //PCCOVID Phone
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_LOCATION TEXT"); //PCCOVID Location
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN PCCOVID_TOKEN TEXT"); //PCCOVID Token
                db.execSQL("UPDATE " + MACHINE + " SET USE_PCCOVID = 0 WHERE USE_PCCOVID IS NULL"); //Cập nhật lại giá trị trường USE_PCCOVID = 0 nếu null

                //scrip of version5
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN DAILY_REBOOT INTEGER DEFAULT 0"); //Có/không sử dụng đặt lịch khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN RESTART_TIME TEXT"); //Thời gian khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN LANGUAGE TEXT"); //Ngôn ngữ
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NO_DELAY INTEGER DEFAULT 1"); //Xử lý ngay khi thấy khuôn mặt mới không chờ hết delaytime
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NOMASK_QUALITY_THRESHOLD REAL DEFAULT 0.49"); //Ngưỡng điểm chất lượng ảnh không khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN MASK_QUALITY_THRESHOLD REAL DEFAULT 0.29"); //Ngưỡng điểm chất lượng ảnh có khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN REGISTER_QUALITY_THRESHOLD REAL DEFAULT 0.63"); //Ngưỡng điểm chất lượng ảnh tối thiểu đủ để làm ảnh đăng ký
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN AUTO_SLEEP INTEGER DEFAULT 0"); //Tự động sleep màn hinh
            }

            if(newVersion == 6){
                //scrip of version6
                String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "SIMILAR_PERSON_ID TEXT "
                        + ") ";
                db.execSQL(scriptTwins);
            }
        }

        //VERSION5 - 2022-01-18
        if (oldVersion == 4) {
            if(newVersion == 5){
                //scrip of version5
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN DAILY_REBOOT INTEGER DEFAULT 0"); //Có/không sử dụng đặt lịch khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN RESTART_TIME TEXT"); //Thời gian khởi động lại hàng ngày
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN LANGUAGE TEXT"); //Ngôn ngữ
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NO_DELAY INTEGER DEFAULT 1"); //Xử lý ngay khi thấy khuôn mặt mới không chờ hết delaytime
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN NOMASK_QUALITY_THRESHOLD REAL DEFAULT 0.49"); //Ngưỡng điểm chất lượng ảnh không khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN MASK_QUALITY_THRESHOLD REAL DEFAULT 0.29"); //Ngưỡng điểm chất lượng ảnh có khẩu trang đủ để xử lý
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN REGISTER_QUALITY_THRESHOLD REAL DEFAULT 0.63"); //Ngưỡng điểm chất lượng ảnh tối thiểu đủ để làm ảnh đăng ký
                db.execSQL("ALTER TABLE " + MACHINE + " ADD COLUMN AUTO_SLEEP INTEGER DEFAULT 0"); //Tự động sleep màn hinh
            }

            if(newVersion == 6){
                //scrip of version6
                String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "SIMILAR_PERSON_ID TEXT "
                        + ") ";
                db.execSQL(scriptTwins);
            }
        }

        //VERSION6 - 2023-07-10
        if (oldVersion == 5) {
            if(newVersion == 6){
                //scrip of version6
                String scriptTwins = "CREATE TABLE " + TWIN + " ( "
                        + "ID INTEGER NOT NULL PRIMARY KEY, "
                        + "PERSON_ID TEXT, "
                        + "SIMILAR_PERSON_ID TEXT "
                        + ") ";
                db.execSQL(scriptTwins);
            }
        }
    }

    public synchronized void clearDatabase () {
        Log.i(TAG, "Database deleteAllData ");
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();

            db.delete(GROUP_ACCESS, null, null);

            db.delete(PERSON_GROUP, null, null);

            db.delete(ACCESS_TIME_SEG, null, null);

            db.delete(EVENT, null, null);

            db.delete(FACE, null, null);

            db.delete(CARD, null, null);

            db.delete(PERSON, null, null);

            db.delete(MACHINE, null, null);

            db.delete(TWIN, null, null);

            db.setTransactionSuccessful();
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void clearEventLog() {
        Log.i(TAG, "Database clearEventLog");
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            db.delete(EVENT, null, null);
            db.setTransactionSuccessful();
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void clearFaceData() {
        Log.i(TAG, "Database clearFaceData");
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            db.delete(EVENT, null, null);
            db.delete(PERSON, null, null);
            db.setTransactionSuccessful();
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized List<FaceRegisterInfo> getAllActivePerson() {
        String selectQuery =
              " SELECT p.PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
            + " FULL_NAME, POSITION, JOBDUTIES, FACE_FEATURE, "
            + " FACE_PATH, p.STATUS "
            + " FROM PERSON p, FACE f "
            + " WHERE p.PERSON_ID = f.PERSON_ID "
            + " AND p.STATUS = 1 "
            + " AND f.FACE_STATUS = 1 ";
            //+ " AND (p.PERSON_TYPE IS NULL OR p.PERSON_TYPE = 1) ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<FaceRegisterInfo> lsData = new ArrayList<FaceRegisterInfo>();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    FaceRegisterInfo model = new FaceRegisterInfo(
                            cursor.getString(0), //PERSON_ID
                            cursor.getInt(1), //COMP_ID
                            cursor.getInt(2), //DEPT_ID
                            cursor.getString(3), //PERSON_CODE
                            cursor.getString(4), //FULL_NAME
                            cursor.getString(5), //POSITION
                            cursor.getString(6), //JOBDUTIES
                            cursor.getBlob(7), //FACE_FEATURE
                            cursor.getString(8), //FACE_PATH
                            cursor.getInt(9) //STATUS
                    );
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized FaceRegisterInfo getFaceRegisterInfo(String personId) {
        String selectQuery =
                " SELECT p.PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
                        + " FULL_NAME, POSITION, JOBDUTIES, FACE_FEATURE, "
                        + " FACE_PATH, p.STATUS "
                        + " FROM PERSON p, FACE f "
                        + " WHERE p.PERSON_ID = f.PERSON_ID "
                        + " AND (p.PERSON_TYPE IS NULL OR p.PERSON_TYPE = 1) "
                        + " AND p.PERSON_ID = ? ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        FaceRegisterInfo model = null;

        try{
            cursor = db.rawQuery(selectQuery, new String[]{personId});

            if (cursor != null && cursor.moveToFirst()) {
                model = new FaceRegisterInfo(
                        cursor.getString(0), //PERSON_ID
                        cursor.getInt(1), //COMP_ID
                        cursor.getInt(2), //DEPT_ID
                        cursor.getString(3), //PERSON_CODE
                        cursor.getString(4), //FULL_NAME
                        cursor.getString(5), //POSITION
                        cursor.getString(6), //JOBDUTIES
                        cursor.getBlob(7), //FACE_FEATURE
                        cursor.getString(8), //FACE_PATH
                        cursor.getInt(9) //STATUS
                );
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return model;
    }

    public synchronized List<FaceRegisterInfo> getGuestExpired (int machineId, int totalExpiredDay) {
        String selectQuery =
                " SELECT PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
            + " FULL_NAME, POSITION, JOBDUTIES, STATUS "
            + " FROM PERSON p "
            + " WHERE p.STATUS = 1 "
            + " AND p.PERSON_TYPE = 2 "
            + " AND p.PERSON_ID IN (SELECT PERSON_ID FROM PERSON_ACCESS WHERE PERSON_ID = p.PERSON_ID AND (FROM_DATE > ? OR TO_DATE < ?) AND IS_DELETE = 0) "
            + " UNION ALL "
            + " SELECT PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
            + " FULL_NAME, POSITION, JOBDUTIES, STATUS "
            + " FROM PERSON p "
            + " WHERE p.STATUS = 1 "
            + " AND p.PERSON_TYPE = 2 "
            + " AND NOT EXISTS (SELECT PERSON_ID FROM PERSON_ACCESS WHERE PERSON_ID = p.PERSON_ID) ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<FaceRegisterInfo> lsData = new ArrayList<FaceRegisterInfo>();

        try{
            Date currentDatetime = Calendar.getInstance().getTime();
            Date expiredDatetime = StringUtils.plusDayToDate(currentDatetime, totalExpiredDay);

            String strExpiredTime = StringUtils.convertDateToString(expiredDatetime, "yyyy-MM-dd HH:mm:ss");
            cursor = db.rawQuery(selectQuery, new String[]{ strExpiredTime, strExpiredTime});

            if (cursor.moveToFirst()) {
                do {
                    FaceRegisterInfo model = new FaceRegisterInfo(
                        cursor.getString(0), //PERSON_ID
                        cursor.getInt(1), //COMP_ID
                        cursor.getInt(2), //DEPT_ID
                        cursor.getString(3), //PERSON_CODE
                        cursor.getString(4), //FULL_NAME
                        cursor.getString(5), //POSITION
                        cursor.getString(6), //JOBDUTIES
                        cursor.getInt(7) //STATUS
                    );
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    //Chỉ lấy danh sách nhân sự và khách còn hiệu lực
    public synchronized List<FaceRegisterInfo> searchPerson(String searchContent) {
        String selectQuery =
                  " SELECT p.PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
                + " FULL_NAME, POSITION, JOBDUTIES, FACE_FEATURE, "
                + " FACE_PATH, p.STATUS "
                + " FROM PERSON p, FACE f "
                + " WHERE p.PERSON_ID = f.PERSON_ID "
                + " AND p.STATUS = 1 "
                + " AND f.FACE_STATUS = 1 "
                + " AND (p.PERSON_TYPE IS NULL OR p.PERSON_TYPE = 1) "
                + " AND ('' = '" + searchContent + "' OR UPPER(p.FULL_NAME) LIKE UPPER('%" + searchContent + "%') OR UPPER(p.POSITION) LIKE UPPER('%" + searchContent + "%') OR UPPER(p.PERSON_CODE) = UPPER('" + searchContent + "'))"
                + " UNION ALL "
                + " SELECT p.PERSON_ID, COMP_ID, DEPT_ID, PERSON_CODE, "
                + " FULL_NAME, POSITION, JOBDUTIES, '' as FACE_FEATURE, "
                + " '' as FACE_PATH, p.STATUS "
                + " FROM PERSON p"
                + " WHERE p.STATUS = 1 "
                + " AND p.PERSON_ID NOT IN (SELECT PERSON_ID FROM FACE f) "
                + " AND (p.PERSON_TYPE IS NULL OR p.PERSON_TYPE = 1) "
                + " AND ('' = '" + searchContent + "' OR UPPER(p.FULL_NAME) LIKE UPPER('%" + searchContent + "%') OR UPPER(p.POSITION) LIKE UPPER('%" + searchContent + "%') OR UPPER(p.PERSON_CODE) = UPPER('" + searchContent + "'))";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<FaceRegisterInfo> lsData = new ArrayList<FaceRegisterInfo>();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    FaceRegisterInfo model = new FaceRegisterInfo(
                            cursor.getString(0), //PERSON_ID
                            cursor.getInt(1), //COMP_ID
                            cursor.getInt(2), //DEPT_ID
                            cursor.getString(3), //PERSON_CODE
                            cursor.getString(4), //FULL_NAME
                            cursor.getString(5), //POSITION
                            cursor.getString(6), //JOBDUTIES
                            cursor.getBlob(7), //FACE_FEATURE
                            cursor.getString(8), //FACE_PATH
                            cursor.getInt(9) //STATUS
                    );
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    //Method lưu thông tin nhân sự
    public synchronized void addPerson(PersonDB model) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.COMP_ID, model.getCompId());
            values.put(Constants.DEPT_ID, model.getDeptId());
            values.put(Constants.PERSON_CODE, model.getPersonCode());
            values.put(Constants.FULL_NAME, model.getFullName());
            values.put(Constants.POSITION, model.getPosition());
            values.put(Constants.JOBDUTIES, model.getJobDuties());
            values.put(Constants.PERSON_TYPE, model.getPersonType());
            values.put(Constants.STATUS, model.getStatus());
            values.put(Constants.VACCINE, model.getVaccine());
            db.insert(PERSON, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListPerson(List<PersonDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (PersonDB model: lsData) {
                ContentValues values = new ContentValues();
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.COMP_ID, model.getCompId());
                values.put(Constants.DEPT_ID, model.getDeptId());
                values.put(Constants.PERSON_CODE, model.getPersonCode());
                values.put(Constants.FULL_NAME, model.getFullName());
                values.put(Constants.POSITION, model.getPosition());
                values.put(Constants.JOBDUTIES, model.getJobDuties());
                values.put(Constants.PERSON_TYPE, model.getStatus());
                values.put(Constants.STATUS, model.getStatus());
                values.put(Constants.VACCINE, model.getVaccine());
                db.insert(PERSON, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized int updatePerson (PersonDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(Constants.COMP_ID, model.getCompId());
            values.put(Constants.DEPT_ID, model.getDeptId());
            values.put(Constants.PERSON_CODE, model.getPersonCode());
            values.put(Constants.FULL_NAME, model.getFullName());
            values.put(Constants.POSITION, model.getPosition());
            values.put(Constants.JOBDUTIES, model.getJobDuties());
            values.put(Constants.PERSON_TYPE, model.getPersonType());
            values.put(Constants.STATUS, model.getStatus());
            values.put(Constants.VACCINE, model.getVaccine());

            result = db.update(PERSON, values,
                    Constants.PERSON_ID + " = ?",
                    new String[]{model.getPersonId()});

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        }finally {
            db.endTransaction();
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                updatePerson (model);
                Log.d(TAG, "Retry updatePerson " + retryTime);
            }
        }
        return result;
    }

    public synchronized void deletePerson(String personId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.delete(PERSON, Constants.PERSON_ID + " = ?", new String[] { personId });
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deactivePerson(List<FaceRegisterInfo>  lsPerson) {
        SQLiteDatabase db = this.getWritableDatabase();
        int retryTime = 0;
        boolean needRetry = false;

        try{
            db.beginTransaction();
            for (FaceRegisterInfo model : lsPerson) {
                ContentValues values = new ContentValues();
                values.put(Constants.STATUS, "0");

                db.update(PERSON, values,
                        Constants.PERSON_ID + " = ?",
                        new String[]{model.getPersonId()});
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        }finally {
            db.endTransaction();
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                deactivePerson (lsPerson);
                Log.d(TAG, "Retry deactivePerson " + retryTime);
            }
        }
    }

    public synchronized void deleteListPerson(List<PersonDB> lsPerson) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (PersonDB model : lsPerson) {
                db.delete(PERSON, Constants.PERSON_ID + " = ?", new String[] { model.getPersonId() });
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteAllPerson() {
        SQLiteDatabase db = this.getWritableDatabase();
        db = this.getWritableDatabase();
        try{
            db.delete(PERSON, null, null);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized PersonDB getPerson(String personId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        PersonDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try {
            cursor = db.query(PERSON, new String[]{
                            Constants.PERSON_ID,
                            Constants.COMP_ID,
                            Constants.DEPT_ID,
                            Constants.PERSON_CODE,
                            Constants.FULL_NAME,
                            Constants.POSITION,
                            Constants.JOBDUTIES,
                            Constants.PERSON_TYPE,
                            Constants.STATUS,
                            Constants.VACCINE
                    },
                    Constants.PERSON_ID + "=?", new String[]{personId},
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                model = new PersonDB(
                        cursor.getString(0), //PERSON_ID
                        cursor.getInt(1), //COMP_ID
                        cursor.getInt(2), //DEPT_ID
                        cursor.getString(3), //PERSON_CODE
                        cursor.getString(4), //FULL_NAME
                        cursor.getString(5), //POSITION
                        cursor.getString(6), //JOBDUTIES
                        cursor.getInt(7), //PERSON_TYPE
                        cursor.getInt(8), //STATUS
                        cursor.getInt(9) //VACCINE
                );

                return model;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getPerson(personId);
                Log.d(TAG, "Retry getPerson " + retryTime);
            }
        }

        return model;
    }

    public synchronized PersonDB getPersonByCode(String personCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        PersonDB model = null;

        try{
            cursor = db.query(PERSON, new String[] {
                            Constants.PERSON_ID,
                            Constants.COMP_ID,
                            Constants.DEPT_ID,
                            Constants.PERSON_CODE,
                            Constants.FULL_NAME,
                            Constants.POSITION,
                            Constants.JOBDUTIES,
                            Constants.PERSON_TYPE,
                            Constants.STATUS,
                            Constants.VACCINE
                    },
                    Constants.PERSON_CODE + "=?", new String[] { personCode },
                    null, null, null, null);

            if (cursor.moveToFirst()){
                model = new PersonDB(
                        cursor.getString(0), //PERSON_ID
                        cursor.getInt(1), //COMP_ID
                        cursor.getInt(2), //DEPT_ID
                        cursor.getString(3), //PERSON_CODE
                        cursor.getString(4), //FULL_NAME
                        cursor.getString(5), //POSITION
                        cursor.getString(6), //JOBDUTIES
                        cursor.getInt(7), //PERSON_TYPE
                        cursor.getInt(8), //STATUS
                        cursor.getInt(9) //VACCINE
                );
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }

        return model;
    }

    public synchronized List<PersonDB> getAllPerson() {
        List<PersonDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(PERSON, new String[] {
                            Constants.PERSON_ID,
                            Constants.COMP_ID,
                            Constants.DEPT_ID,
                            Constants.PERSON_CODE,
                            Constants.FULL_NAME,
                            Constants.POSITION,
                            Constants.JOBDUTIES,
                            Constants.PERSON_TYPE,
                            Constants.STATUS,
                            Constants.VACCINE
                    },
                    Constants.STATUS + " = 1 ",null, null, null, Constants.FULL_NAME + " ASC");

            // Duyệt trên con trỏ, và thêm vào danh sách.
            PersonDB model;
            if (cursor.moveToFirst()) {
                do {
                    try{
                        model = new PersonDB(
                                cursor.getString(0), //PERSON_ID
                                cursor.getInt(1), //COMP_ID
                                cursor.getInt(2), //DEPT_ID
                                cursor.getString(3), //PERSON_CODE
                                cursor.getString(4), //FULL_NAME
                                cursor.getString(5), //POSITION
                                cursor.getString(6), //JOBDUTIES
                                cursor.getInt(7), //PERSON_TYPE
                                cursor.getInt(8), //STATUS
                                cursor.getInt(9) //VACCINE
                        );
                        // Add list.
                        lsData.add(model);
                    }catch (Exception ex){
                        Log.e(TAG, ex.getMessage());
                    }
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, "getAllPerson: " + ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }

        return lsData;
    }

    public synchronized List<PersonDB> getListPerson(int groupId) {
        List<PersonDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(PERSON, new String[] {
                            Constants.PERSON_ID,
                            Constants.COMP_ID,
                            Constants.DEPT_ID,
                            Constants.PERSON_CODE,
                            Constants.FULL_NAME,
                            Constants.POSITION,
                            Constants.JOBDUTIES,
                            Constants.PERSON_TYPE,
                            Constants.STATUS,
                            Constants.VACCINE
                    },
                    Constants.GROUP_ID + " = ? AND " + Constants.STATUS + " = 1 ",new String[]{"" + groupId}, null, null, Constants.FULL_NAME + " ASC");

            // Duyệt trên con trỏ, và thêm vào danh sách.
            PersonDB model = null;
            if (cursor.moveToFirst()) {
                do {
                    try{
                        model = new PersonDB(
                                cursor.getString(0), //PERSON_ID
                                cursor.getInt(1), //COMP_ID
                                cursor.getInt(2), //DEPT_ID
                                cursor.getString(3), //PERSON_CODE
                                cursor.getString(4), //FULL_NAME
                                cursor.getString(5), //POSITION
                                cursor.getString(6), //JOBDUTIES
                                cursor.getInt(7), //PERSON_TYPE
                                cursor.getInt(8), //STATUS
                                cursor.getInt(9) //VACCINE
                        );
                        // Add list.
                        lsData.add(model);
                    }catch (Exception ex){
                        Log.e(TAG, ex.getMessage());
                    }
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }
    //End person

    //Add Face
    public synchronized void addFace(FaceDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int retryTime = 0;
        boolean needRetry = false;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FACE_ID, model.getFaceId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.FACE_URL, model.getFaceUrl());
            values.put(Constants.FACE_PATH, model.getFacePath());
            values.put(Constants.FACE_FEATURE, model.getFaceFeature());
            values.put(Constants.FEATURE_PATH, model.getFeaturePath());
            values.put(Constants.FACE_STATUS, model.getFaceStatus());
            db.insert(FACE, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        }finally {
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                addFace(model);
                Log.d(TAG, "Retry addFace " + retryTime);
            }
        }
    }

    public synchronized void addListFace(List<FaceDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (FaceDB model: lsData) {
                ContentValues values = new ContentValues();
                values.put(Constants.FACE_ID, model.getFaceId());
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.FACE_URL, model.getFaceUrl());
                values.put(Constants.FACE_PATH, model.getFacePath());
                values.put(Constants.FACE_FEATURE, model.getFaceFeature());
                values.put(Constants.FEATURE_PATH, model.getFeaturePath());
                values.put(Constants.FACE_STATUS, model.getFaceStatus());
                db.insert(FACE, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized int updateFace (FaceDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            ContentValues values = new ContentValues();
            values.put(Constants.FACE_ID, model.getFaceId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.FACE_URL, model.getFaceUrl());
            values.put(Constants.FACE_PATH, model.getFacePath());
            values.put(Constants.FACE_FEATURE, model.getFaceFeature());
            values.put(Constants.FEATURE_PATH, model.getFeaturePath());
            values.put(Constants.FACE_STATUS, model.getFaceStatus());

            result = db.update(FACE, values,
                    Constants.FACE_ID + " = ?",
                    new String[]{model.getFaceId()});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        }finally {
            closeObject(db);
            if(needRetry && retryTime < 3){
                retryTime++;
                updateFace (model);
                Log.d(TAG, "Retry getGuestExpired " + retryTime);
            }
        }
        return result;
    }

    public synchronized void deleteFace(String faceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(FACE, Constants.FACE_ID + " = ?", new String[] { faceId });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteFaceByPerson(String personId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(FACE, Constants.PERSON_ID + " = ?", new String[] { personId });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListFace(List<FaceDB> lsFace) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (FaceDB model : lsFace) {
                db.delete(FACE, Constants.FACE_ID + " = ?", new String[] { model.getFaceId() });
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteAllFace() {
        SQLiteDatabase db = this.getWritableDatabase();
        db = this.getWritableDatabase();
        try{
            db.delete(FACE, null, null);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized FaceDB getFace(String faceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        FaceDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(FACE, new String[] {
                            Constants.FACE_ID,
                            Constants.PERSON_ID,
                            Constants.FACE_URL,
                            Constants.FACE_PATH,
                            Constants.FACE_FEATURE,
                            Constants.FEATURE_PATH,
                            Constants.FACE_STATUS,
                    },
                    Constants.FACE_ID + " = ? AND " + Constants.FACE_STATUS + " = ? ", new String[] { faceId, "1" },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()){
                model = new FaceDB(
                        cursor.getString(0), //FACE_ID
                        cursor.getString(1), //PERSON_ID
                        cursor.getString(2), //FACE_URL
                        cursor.getString(3), //FACE_PATH
                        cursor.getBlob(4), //FACE_FEATURE
                        cursor.getString(5), //FEATURE_PATH
                        cursor.getInt(6) //FACE_STATUS
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "getPerson: " + ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getFace (faceId);
                Log.d(TAG, "Retry getFace " + retryTime);
            }
        }

        return model;
    }

    public synchronized FaceDB getFaceByPerson (String personId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        FaceDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(FACE, new String[] {
                            Constants.FACE_ID,
                            Constants.PERSON_ID,
                            Constants.FACE_URL,
                            Constants.FACE_PATH,
                            Constants.FACE_FEATURE,
                            Constants.FEATURE_PATH,
                            Constants.FACE_STATUS,
                    },
                    Constants.PERSON_ID + " = ? AND " + Constants.FACE_STATUS + " = ? ", new String[] { personId, "1" },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()){
                model = new FaceDB(
                        cursor.getString(0), //FACE_ID
                        cursor.getString(1), //PERSON_ID
                        cursor.getString(2), //FACE_URL
                        cursor.getString(3), //FACE_PATH
                        cursor.getBlob(4), //FACE_FEATURE
                        cursor.getString(5), //FEATURE_PATH
                        cursor.getInt(6) //FACE_STATUS
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "getFaceByPerson: " + ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getFaceByPerson (personId);
                Log.d(TAG, "Retry getFaceByPerson " + retryTime);
            }
        }

        return model;
    }
    //End Face

    //Add Card
    public synchronized void addCard(CardDB model) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.CARD_ID, model.getCardId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.CARD_NO, model.getCardNo());
            db.insert(CARD, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListCard(List<CardDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (CardDB model: lsData) {
                ContentValues values = new ContentValues();
                values.put(Constants.CARD_ID, model.getCardId());
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.CARD_NO, model.getCardNo());
                db.insert(CARD, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized int updateCard (CardDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        try{
            ContentValues values = new ContentValues();
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.CARD_NO, model.getCardNo());

            result = db.update(CARD, values,
                    Constants.CARD_ID + " = ?",
                    new String[]{model.getPersonId()});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
        return result;
    }

    public synchronized void deleteCard(String cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(CARD, Constants.CARD_ID + " = ?", new String[] { cardId });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListCard(List<CardDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (CardDB model : lsData) {
                db.delete(CARD, Constants.CARD_ID + " = ?", new String[] { model.getCardId() });
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteAllCard() {
        SQLiteDatabase db = this.getWritableDatabase();
        db = this.getWritableDatabase();
        try{
            db.delete(CARD, null, null);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized CardDB getCard(String cardId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        CardDB model = null;

        try{
            cursor = db.query(CARD, new String[] {
                            Constants.CARD_ID,
                            Constants.PERSON_ID,
                            Constants.CARD_NO
                    },
                    Constants.CARD_ID + " = ? ", new String[] { cardId },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()){
                model = new CardDB(
                        cursor.getString(0), //CardId
                        cursor.getString(1), //PersonId
                        cursor.getString(2) //CardNo
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "getPerson: " + ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }

        return model;
    }

    public synchronized List<CardDB> getAllActiveCards() {
        String selectQuery =
                " SELECT c.CARD_ID, c.PERSON_ID, c.CARD_NO "
                        + " FROM PERSON p, CARD c "
                        + " WHERE p.PERSON_ID = c.PERSON_ID "
                        + " AND p.STATUS = 1 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<CardDB> lsData = new ArrayList<CardDB>();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CardDB model = new CardDB(
                            cursor.getString(0), //CARD_ID
                            cursor.getString(1), //PERSON_ID
                            cursor.getString(2)  //CARD_NO
                    );
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }
    //End card

    //Start PersonGroup Mapping
    public synchronized void addPersonGroup (PersonGroupDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int retryTime = 0;
        boolean needRetry = false;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.PERSON_GROUP_ID, model.getId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.GROUP_ID, model.getGroupId());
            db.insert(PERSON_GROUP, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        }finally {
            closeObject(db);
            if(needRetry && retryTime < 3){
                retryTime++;
                addPersonGroup (model);
                Log.d(TAG, "Retry addPersonGroup " + retryTime);
            }
        }
    }

    public synchronized void addListPersonGroup (List<PersonGroupDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (PersonGroupDB model: lsData
            ) {
                ContentValues values = new ContentValues();
                values.put(Constants.PERSON_GROUP_ID, model.getId());
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.GROUP_ID, model.getGroupId());
                db.insert(PERSON_GROUP, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deletePersonGroup (int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(PERSON_GROUP, Constants.PERSON_GROUP_ID + " = ?", new String[] { String.valueOf(id) });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListPersonGroup (List<PersonGroupDB> lsdata) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (PersonGroupDB model : lsdata ) {
                db.delete(PERSON_GROUP, Constants.PERSON_GROUP_ID + " = ?", new String[] { String.valueOf(model.getId()) });
            }

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized List<PersonGroupDB> getAllPersonGroup() {
        List<PersonGroupDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(PERSON_GROUP, new String[] {
                            Constants.PERSON_GROUP_ID,
                            Constants.PERSON_ID,
                            Constants.GROUP_ID
                    },
                    null,null, null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    PersonGroupDB model = new PersonGroupDB(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2)
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized List<PersonGroupDB> getListPersonGroup(String personId) {
        List<PersonGroupDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(PERSON_GROUP, new String[] {
                            Constants.PERSON_GROUP_ID,
                            Constants.PERSON_ID,
                            Constants.GROUP_ID
                    },
                    Constants.PERSON_ID + "=?", new String[] { String.valueOf(personId) },
                    null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    PersonGroupDB model = new PersonGroupDB(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2)
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                lsData = getListPersonGroup(personId);
                Log.d(TAG, "Retry getListPersonGroup " + retryTime);
            }
        }
        return lsData;
    }

    public synchronized PersonGroupDB getPersonGroup (int id) {
        PersonGroupDB model = new PersonGroupDB();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(PERSON_GROUP, new String[] {
                            Constants.PERSON_GROUP_ID,
                            Constants.PERSON_ID,
                            Constants.GROUP_ID
                    },
                    Constants.PERSON_GROUP_ID + "=?", new String[] { String.valueOf(id) },
                    null, null, null);

            if (cursor.moveToFirst()){
                model = new PersonGroupDB(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "getPersonGroup: " + ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);
            if(needRetry && retryTime < 3){
                retryTime++;
                getPersonGroup(id);
                Log.d(TAG, "Retry getGuestExpired " + retryTime);
            }
        }
        return model;
    }
    //End group


    /**
     * Insert Machine
     * @param model
     */
    public synchronized void addMachine(MachineDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.MACHINE_ID, model.getMachineId());
            values.put(Constants.COMP_ID, model.getCompId());
            values.put(Constants.DEVICE_NAME, model.getDeviceName());
            values.put(Constants.DEVICE_TYPE, model.getDeviceType());
            values.put(Constants.DEVICE_FUNCTION, model.getDeviceFunction());
            values.put(Constants.IPADDRESS, model.getIpaddress());
            values.put(Constants.IMEI, model.getImei());
            values.put(Constants.MAC, model.getMac());
            values.put(Constants.SERVER_IP, model.getServerIp());
            values.put(Constants.SERVER_PORT, model.getServerPort());
            values.put(Constants.FRAUD_PROOF, model.getFraudProof());
            values.put(Constants.ANGLE_DETECT, model.getAngleDetect());
            values.put(Constants.AUTO_START, model.getAutoStart());
            values.put(Constants.AUTO_SAVE_VISITOR, model.getAutoSaveVisitor());
            values.put(Constants.DISTANT_DETECT, model.getDistanceDetect());
            values.put(Constants.USERNAME, model.getUsername());
            values.put(Constants.PASSWORD, model.getPassword());
            values.put(Constants.LOGO, model.getLogo());
            values.put(Constants.VOLUME, model.getVolume());
            values.put(Constants.BRIGHTNESS, model.getBrightness());
            values.put(Constants.DELAY, model.getDelay());
            values.put(Constants.LED, model.getLed());
            values.put(Constants.FP_THRESHOLD, model.getFingerThreshold());
            values.put(Constants.FACE_THRESHOLD, model.getFaceThreshold());
            values.put(Constants.TEMPERATURE_THRESHOLD, model.getTemperatureThreshold());
            values.put(Constants.FIRMWARE_VERSION, model.getFirmwareVersion());
            values.put(Constants.USERNAME_SERVER, model.getUsernameServer());
            values.put(Constants.PASSWORD_SERVER, model.getPasswordServer());
            values.put(Constants.USE_MASK, model.getUseMask());
            values.put(Constants.USE_TEMPERATURE, model.getUseTemperature());
            values.put(Constants.USE_VACCINE, model.getUseVaccine());
            values.put(Constants.USE_PCCOVID, model.getUsePCCovid());
            values.put(Constants.PCCOVID_PHONE, model.getPccovidPhone());
            values.put(Constants.PCCOVID_LOCATION, model.getPccovidLocation());
            values.put(Constants.PCCOVID_TOKEN, model.getPccovidToken());
            values.put(Constants.DAILY_REBOOT, model.getDailyReboot());
            values.put(Constants.RESTART_TIME, model.getRestartTime());
            values.put(Constants.LANGUAGE, model.getLanguage());
            values.put(Constants.NO_DELAY, model.getNoDelay());
            values.put(Constants.NOMASK_QUALITY_THRESHOLD, model.getNoMaskQualityThreshold());
            values.put(Constants.MASK_QUALITY_THRESHOLD, model.getMaskQualityThreshold());
            values.put(Constants.REGISTER_QUALITY_THRESHOLD, model.getRegQualityThreshold());
            values.put(Constants.AUTO_SLEEP, model.getAutoSleep());

            db.insert(MACHINE, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListMachine (List<MachineDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (MachineDB model: lsData
            ) {
                ContentValues values = new ContentValues();
                values.put(Constants.MACHINE_ID, model.getMachineId());
                values.put(Constants.COMP_ID, model.getCompId());
                values.put(Constants.DEVICE_NAME, model.getDeviceName());
                values.put(Constants.DEVICE_TYPE, model.getDeviceType());
                values.put(Constants.DEVICE_FUNCTION, model.getDeviceFunction());
                values.put(Constants.IPADDRESS, model.getIpaddress());
                values.put(Constants.IMEI, model.getImei());
                values.put(Constants.MAC, model.getMac());
                values.put(Constants.SERVER_IP, model.getServerIp());
                values.put(Constants.SERVER_PORT, model.getServerPort());
                values.put(Constants.FRAUD_PROOF, model.getFraudProof());
                values.put(Constants.ANGLE_DETECT, model.getAngleDetect());
                values.put(Constants.AUTO_START, model.getAutoStart());
                values.put(Constants.AUTO_SAVE_VISITOR, model.getAutoSaveVisitor());
                values.put(Constants.DISTANT_DETECT, model.getDistanceDetect());
                values.put(Constants.USERNAME, model.getUsername());
                values.put(Constants.PASSWORD, model.getPassword());
                values.put(Constants.LOGO, model.getLogo());
                values.put(Constants.VOLUME, model.getVolume());
                values.put(Constants.BRIGHTNESS, model.getBrightness());
                values.put(Constants.DELAY, model.getDelay());
                values.put(Constants.LED, model.getLed());
                values.put(Constants.FP_THRESHOLD, model.getFingerThreshold());
                values.put(Constants.FACE_THRESHOLD, model.getFaceThreshold());
                values.put(Constants.TEMPERATURE_THRESHOLD, model.getTemperatureThreshold());
                values.put(Constants.FIRMWARE_VERSION, model.getFirmwareVersion());
                values.put(Constants.USERNAME_SERVER, model.getUsernameServer());
                values.put(Constants.PASSWORD_SERVER, model.getPasswordServer());
                values.put(Constants.USE_MASK, model.getUseMask());
                values.put(Constants.USE_TEMPERATURE, model.getUseTemperature());
                values.put(Constants.USE_VACCINE, model.getUseVaccine());
                values.put(Constants.USE_PCCOVID, model.getUsePCCovid());
                values.put(Constants.PCCOVID_PHONE, model.getPccovidPhone());
                values.put(Constants.PCCOVID_LOCATION, model.getPccovidLocation());
                values.put(Constants.PCCOVID_TOKEN, model.getPccovidToken());
                values.put(Constants.DAILY_REBOOT, model.getDailyReboot());
                values.put(Constants.RESTART_TIME, model.getRestartTime());
                values.put(Constants.LANGUAGE, model.getLanguage());
                values.put(Constants.NO_DELAY, model.getNoDelay());
                values.put(Constants.NOMASK_QUALITY_THRESHOLD, model.getNoMaskQualityThreshold());
                values.put(Constants.MASK_QUALITY_THRESHOLD, model.getMaskQualityThreshold());
                values.put(Constants.REGISTER_QUALITY_THRESHOLD, model.getRegQualityThreshold());
                values.put(Constants.AUTO_SLEEP, model.getAutoSleep());

                db.insert(MACHINE, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    /**
     * Insert Machine
     * @param model
     */
    public synchronized void updateMachine(MachineDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COMP_ID, model.getCompId());
            values.put(Constants.DEVICE_NAME, model.getDeviceName());
            values.put(Constants.DEVICE_TYPE, model.getDeviceType());
            values.put(Constants.DEVICE_FUNCTION, model.getDeviceFunction());
            values.put(Constants.IPADDRESS, model.getIpaddress());
            values.put(Constants.IMEI, model.getImei());
            values.put(Constants.MAC, model.getMac());
            values.put(Constants.SERVER_IP, model.getServerIp());
            values.put(Constants.SERVER_PORT, model.getServerPort());
            values.put(Constants.FRAUD_PROOF, model.getFraudProof());
            values.put(Constants.ANGLE_DETECT, model.getAngleDetect());
            values.put(Constants.AUTO_START, model.getAutoStart());
            values.put(Constants.AUTO_SAVE_VISITOR, model.getAutoSaveVisitor());
            values.put(Constants.DISTANT_DETECT, model.getDistanceDetect());
            values.put(Constants.USERNAME, model.getUsername());
            values.put(Constants.PASSWORD, model.getPassword());
            values.put(Constants.LOGO, model.getLogo());
            values.put(Constants.VOLUME, model.getVolume());
            values.put(Constants.BRIGHTNESS, model.getBrightness());
            values.put(Constants.DELAY, model.getDelay());
            values.put(Constants.LED, model.getLed());
            values.put(Constants.FP_THRESHOLD, model.getFingerThreshold());
            values.put(Constants.FACE_THRESHOLD, model.getFaceThreshold());
            values.put(Constants.TEMPERATURE_THRESHOLD, model.getTemperatureThreshold());
            values.put(Constants.FIRMWARE_VERSION, model.getFirmwareVersion());
            values.put(Constants.USERNAME_SERVER, model.getUsernameServer());
            values.put(Constants.PASSWORD_SERVER, model.getPasswordServer());
            values.put(Constants.USE_MASK, model.getUseMask());
            values.put(Constants.USE_TEMPERATURE, model.getUseTemperature());
            values.put(Constants.USE_VACCINE, model.getUseVaccine());
            values.put(Constants.USE_PCCOVID, model.getUsePCCovid());
            values.put(Constants.PCCOVID_PHONE, model.getPccovidPhone());
            values.put(Constants.PCCOVID_LOCATION, model.getPccovidLocation());
            values.put(Constants.PCCOVID_TOKEN, model.getPccovidToken());
            values.put(Constants.DAILY_REBOOT, model.getDailyReboot());
            values.put(Constants.RESTART_TIME, model.getRestartTime());
            values.put(Constants.LANGUAGE, model.getLanguage());
            values.put(Constants.NO_DELAY, model.getNoDelay());
            values.put(Constants.NOMASK_QUALITY_THRESHOLD, model.getNoMaskQualityThreshold());
            values.put(Constants.MASK_QUALITY_THRESHOLD, model.getMaskQualityThreshold());
            values.put(Constants.REGISTER_QUALITY_THRESHOLD, model.getRegQualityThreshold());
            values.put(Constants.AUTO_SLEEP, model.getAutoSleep());

            db.update(MACHINE, values,
                    Constants.MACHINE_ID + " = ?",
                    new String[]{String.valueOf(model.getMachineId())});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized MachineDB getMachineByImei(String imei) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        MachineDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(MACHINE, new String[] {
                            Constants.MACHINE_ID,
                            Constants.COMP_ID,
                            Constants.DEVICE_NAME,
                            Constants.DEVICE_TYPE,
                            Constants.DEVICE_FUNCTION,
                            Constants.IPADDRESS,
                            Constants.IMEI,
                            Constants.MAC,
                            Constants.SERVER_IP,
                            Constants.SERVER_PORT,
                            Constants.FRAUD_PROOF,
                            Constants.ANGLE_DETECT,
                            Constants.AUTO_START,
                            Constants.AUTO_SAVE_VISITOR,
                            Constants.DISTANT_DETECT,
                            Constants.USERNAME,
                            Constants.PASSWORD,
                            Constants.LOGO,
                            Constants.VOLUME,
                            Constants.BRIGHTNESS,
                            Constants.DELAY,
                            Constants.LED,
                            Constants.FP_THRESHOLD,
                            Constants.FACE_THRESHOLD,
                            Constants.TEMPERATURE_THRESHOLD,
                            Constants.FIRMWARE_VERSION,
                            Constants.USERNAME_SERVER,
                            Constants.PASSWORD_SERVER,
                            Constants.USE_MASK,
                            Constants.USE_TEMPERATURE,
                            Constants.USE_VACCINE,
                            Constants.USE_PCCOVID,
                            Constants.PCCOVID_PHONE,
                            Constants.PCCOVID_LOCATION,
                            Constants.PCCOVID_TOKEN,
                            Constants.DAILY_REBOOT,
                            Constants.RESTART_TIME,
                            Constants.LANGUAGE,
                            Constants.NO_DELAY,
                            Constants.NOMASK_QUALITY_THRESHOLD,
                            Constants.MASK_QUALITY_THRESHOLD,
                            Constants.REGISTER_QUALITY_THRESHOLD,
                            Constants.AUTO_SLEEP
                    },
                    Constants.IMEI + "=?", new String[] { imei },
                    null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    model = new MachineDB(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getInt(9),
                            cursor.getInt(10),
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getInt(13),
                            cursor.getInt(14),
                            cursor.getString(15),
                            cursor.getString(16),
                            cursor.getString(17),
                            cursor.getInt(18),
                            cursor.getInt(19),
                            cursor.getInt(20),
                            cursor.getInt(21),
                            cursor.getDouble(22),
                            cursor.getDouble(23),
                            cursor.getDouble(24),
                            cursor.getString(25),
                            cursor.getString(26),
                            cursor.getString(27),
                            cursor.getInt(28),
                            cursor.getInt(29),
                            cursor.getInt(30),
                            cursor.getInt(31),
                            cursor.getString(32),
                            cursor.getString(33),
                            cursor.getString(34),
                            cursor.getInt(35),
                            cursor.getString(36),
                            cursor.getString(37),
                            cursor.getInt(38),
                            cursor.getDouble(39),
                            cursor.getDouble(40),
                            cursor.getDouble(41),
                            cursor.getInt(42)
                    );
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getMachineByImei(imei);
                Log.d(TAG, "Retry getMachineByImei " + retryTime);
            }
        }
        return model;
    }

    public synchronized MachineDB getMachine(int machineId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        MachineDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(MACHINE, new String[] {
                            Constants.MACHINE_ID,
                            Constants.COMP_ID,
                            Constants.DEVICE_NAME,
                            Constants.DEVICE_TYPE,
                            Constants.DEVICE_FUNCTION,
                            Constants.IPADDRESS,
                            Constants.IMEI,
                            Constants.MAC,
                            Constants.SERVER_IP,
                            Constants.SERVER_PORT,
                            Constants.FRAUD_PROOF,
                            Constants.ANGLE_DETECT,
                            Constants.AUTO_START,
                            Constants.AUTO_SAVE_VISITOR,
                            Constants.DISTANT_DETECT,
                            Constants.USERNAME,
                            Constants.PASSWORD,
                            Constants.LOGO,
                            Constants.VOLUME,
                            Constants.BRIGHTNESS,
                            Constants.DELAY,
                            Constants.LED,
                            Constants.FP_THRESHOLD,
                            Constants.FACE_THRESHOLD,
                            Constants.TEMPERATURE_THRESHOLD,
                            Constants.FIRMWARE_VERSION,
                            Constants.USERNAME_SERVER,
                            Constants.PASSWORD_SERVER,
                            Constants.USE_MASK,
                            Constants.USE_TEMPERATURE,
                            Constants.USE_VACCINE,
                            Constants.USE_PCCOVID,
                            Constants.PCCOVID_PHONE,
                            Constants.PCCOVID_LOCATION,
                            Constants.PCCOVID_TOKEN,
                            Constants.DAILY_REBOOT,
                            Constants.RESTART_TIME,
                            Constants.LANGUAGE,
                            Constants.NO_DELAY,
                            Constants.NOMASK_QUALITY_THRESHOLD,
                            Constants.MASK_QUALITY_THRESHOLD,
                            Constants.REGISTER_QUALITY_THRESHOLD,
                            Constants.AUTO_SLEEP
                    },
                    Constants.MACHINE_ID + "=?", new String[] { String.valueOf(machineId) },
                    null, null, null);

            if (cursor != null)
                cursor.moveToFirst();

            model = new MachineDB(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getInt(9),
                    cursor.getInt(10),
                    cursor.getInt(11),
                    cursor.getInt(12),
                    cursor.getInt(13),
                    cursor.getInt(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17),
                    cursor.getInt(18),
                    cursor.getInt(19),
                    cursor.getInt(20),
                    cursor.getInt(21),
                    cursor.getDouble(22),
                    cursor.getDouble(23),
                    cursor.getDouble(24),
                    cursor.getString(25),
                    cursor.getString(26),
                    cursor.getString(27),
                    cursor.getInt(28),
                    cursor.getInt(29),
                    cursor.getInt(30),
                    cursor.getInt(31),
                    cursor.getString(32),
                    cursor.getString(33),
                    cursor.getString(34),
                    cursor.getInt(35),
                    cursor.getString(36),
                    cursor.getString(37),
                    cursor.getInt(38),
                    cursor.getDouble(39),
                    cursor.getDouble(40),
                    cursor.getDouble(41),
                    cursor.getInt(42)
            );

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getMachine(machineId);
                Log.d(TAG, "Retry getMachine " + retryTime);
            }
        }
        return model;
    }

    public synchronized List<MachineDB> getAllMachine() {
        List<MachineDB> listMachine = new ArrayList<>();
        MachineDB model = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(MACHINE, new String[] {
                            Constants.MACHINE_ID,
                            Constants.COMP_ID,
                            Constants.DEVICE_NAME,
                            Constants.DEVICE_TYPE,
                            Constants.DEVICE_FUNCTION,
                            Constants.IPADDRESS,
                            Constants.IMEI,
                            Constants.MAC,
                            Constants.SERVER_IP,
                            Constants.SERVER_PORT,
                            Constants.FRAUD_PROOF,
                            Constants.ANGLE_DETECT,
                            Constants.AUTO_START,
                            Constants.AUTO_SAVE_VISITOR,
                            Constants.DISTANT_DETECT,
                            Constants.USERNAME,
                            Constants.PASSWORD,
                            Constants.LOGO,
                            Constants.VOLUME,
                            Constants.BRIGHTNESS,
                            Constants.DELAY,
                            Constants.LED,
                            Constants.FP_THRESHOLD,
                            Constants.FACE_THRESHOLD,
                            Constants.TEMPERATURE_THRESHOLD,
                            Constants.FIRMWARE_VERSION,
                            Constants.USERNAME_SERVER,
                            Constants.PASSWORD_SERVER,
                            Constants.USE_MASK,
                            Constants.USE_TEMPERATURE,
                            Constants.USE_VACCINE,
                            Constants.USE_PCCOVID,
                            Constants.PCCOVID_PHONE,
                            Constants.PCCOVID_LOCATION,
                            Constants.PCCOVID_TOKEN,
                            Constants.DAILY_REBOOT,
                            Constants.RESTART_TIME,
                            Constants.LANGUAGE,
                            Constants.NO_DELAY,
                            Constants.NOMASK_QUALITY_THRESHOLD,
                            Constants.MASK_QUALITY_THRESHOLD,
                            Constants.REGISTER_QUALITY_THRESHOLD,
                            Constants.AUTO_SLEEP
                    },
                    null, null,
                    null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    model = new MachineDB(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getInt(9),
                            cursor.getInt(10),
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getInt(13),
                            cursor.getInt(14),
                            cursor.getString(15),
                            cursor.getString(16),
                            cursor.getString(17),
                            cursor.getInt(18),
                            cursor.getInt(19),
                            cursor.getInt(20),
                            cursor.getInt(21),
                            cursor.getDouble(22),
                            cursor.getDouble(23),
                            cursor.getDouble(24),
                            cursor.getString(25),
                            cursor.getString(26),
                            cursor.getString(27),
                            cursor.getInt(28),
                            cursor.getInt(29),
                            cursor.getInt(30),
                            cursor.getInt(31),
                            cursor.getString(32),
                            cursor.getString(33),
                            cursor.getString(34),
                            cursor.getInt(35),
                            cursor.getString(36),
                            cursor.getString(37),
                            cursor.getInt(38),
                            cursor.getDouble(39),
                            cursor.getDouble(40),
                            cursor.getDouble(41),
                            cursor.getInt(42)
                    );

                    listMachine.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return listMachine;
    }

    public synchronized void deleteMachine(int machineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(MACHINE, Constants.MACHINE_ID + " = ?", new String[] { String.valueOf(machineId) });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListMachine(List<MachineDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            for (MachineDB model: lsData
            ) {
                db.delete(MACHINE, Constants.MACHINE_ID + " = ?", new String[] { String.valueOf(model.getMachineId()) });
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }
    //End machine

    //Start GroupAccess
    public synchronized void addGroupAccess (GroupAccessDB model) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.GA_ID, model.getId());
            values.put(Constants.GROUP_ID, model.getGroupId());
            values.put(Constants.MACHINE_ID, model.getMachineId());
            values.put(Constants.TIME_SEG_ID, model.getTimeSegId());
            db.insert(GROUP_ACCESS, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListGroupAccess (List<GroupAccessDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (GroupAccessDB model: lsData
            ) {
                ContentValues values = new ContentValues();
                values.put(Constants.GA_ID, model.getId());
                values.put(Constants.GROUP_ID, model.getGroupId());
                values.put(Constants.MACHINE_ID, model.getMachineId());
                values.put(Constants.TIME_SEG_ID, model.getTimeSegId());
                db.insert(GROUP_ACCESS, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteGroupAccess (int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(GROUP_ACCESS, Constants.GA_ID + " = ?", new String[] { String.valueOf(id) });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListGroupAccess (List<GroupAccessDB> lsdata) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (GroupAccessDB model : lsdata ) {
                db.delete(GROUP_ACCESS, Constants.GA_ID + " = ?", new String[] { String.valueOf(model.getId()) });
            }

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteAllGroupAccess () {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            db.delete(GROUP_ACCESS, null, null );
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized GroupAccessDB getGroupAccess (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        GroupAccessDB model = null;


        try{
            cursor = db.query(GROUP_ACCESS, new String[] {
                            Constants.GA_ID,
                            Constants.GROUP_ID,
                            Constants.MACHINE_ID,
                            Constants.TIME_SEG_ID
                    },
                    Constants.GA_ID + "=?", new String[] { String.valueOf(id) },
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()){
                model = new GroupAccessDB(
                        cursor.getInt(0), //GA_ID
                        cursor.getInt(1), //GROUP_ID
                        cursor.getInt(2), //MACHINE_ID
                        cursor.getInt(3) //TIME_SEG_ID
                );
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return model;
    }

    public synchronized List<GroupAccessDB> getListGroupAccess (int machineId) {
        List<GroupAccessDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(GROUP_ACCESS, new String[] {
                            Constants.GA_ID,
                            Constants.GROUP_ID,
                            Constants.MACHINE_ID,
                            Constants.TIME_SEG_ID
                    },
                    Constants.MACHINE_ID + "=?", new String[] { String.valueOf(machineId) },
                    null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    GroupAccessDB model = new GroupAccessDB(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3)
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                lsData = getListGroupAccess(machineId);
                Log.d(TAG, "Retry getListGroupAccess " + retryTime);
            }
        }
        return lsData;
    }

    public synchronized List<GroupAccessDB> getListGroupAccessByGroup (int groupId) {
        List<GroupAccessDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(GROUP_ACCESS, new String[] {
                            Constants.GA_ID,
                            Constants.GROUP_ID,
                            Constants.MACHINE_ID,
                            Constants.TIME_SEG_ID
                    },
                    Constants.GROUP_ID + "= ? ", new String[] { String.valueOf(groupId) },
                    null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    GroupAccessDB model = new GroupAccessDB(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3)
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }
    //End group access

    //Start person access
    public synchronized void addPersonAccess(PersonAccessDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(Constants.PERSON_ACCESS_ID, model.getId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.MACHINE_ID, model.getMachineId());
            values.put(Constants.FROM_DATE, model.getFromdate());
            values.put(Constants.TO_DATE, model.getTodate());
            values.put(Constants.IS_DELETE, model.getIsDelete());
            db.insert(PERSON_ACCESS, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.close();
        }
    }

    public synchronized void deleteAllPersonAccess () {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(PERSON_ACCESS, null, null);
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deletePersonAccess (String personId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(PERSON_ACCESS, Constants.PERSON_ID + " = ?", new String[]{"" + personId});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized PersonAccessDB getPersonAccess (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        PersonAccessDB model = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(PERSON_ACCESS, new String[] {
                            Constants.PERSON_ACCESS_ID,
                            Constants.PERSON_ID,
                            Constants.MACHINE_ID,
                            Constants.FROM_DATE,
                            Constants.TO_DATE,
                            Constants.IS_DELETE
                    },
                    Constants.PERSON_ACCESS_ID + "=?", new String[] { String.valueOf(id) },
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()){
                model = new PersonAccessDB(
                        cursor.getInt(0), //PERSON_ACCESS_ID
                        cursor.getString(1), //PERSON_ID
                        cursor.getInt(2), //MACHINE_ID
                        cursor.getString(3), //FROM_DATE
                        cursor.getString(4), //TO_DATE
                        cursor.getInt(5) //IS_DELETE
                );
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getPersonAccess (id);
                Log.d(TAG, "Retry getPersonAccess " + retryTime);
            }
        }
        return model;
    }

    public synchronized List<PersonAccessDB> getListPersonAccess (String personId, int machineId) {
        List<PersonAccessDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(PERSON_ACCESS, new String[] {
                            Constants.PERSON_ACCESS_ID,
                            Constants.PERSON_ID,
                            Constants.MACHINE_ID,
                            Constants.FROM_DATE,
                            Constants.TO_DATE,
                            Constants.IS_DELETE
                    },
                    Constants.MACHINE_ID + " =? AND " + Constants.PERSON_ID + " =? AND " + Constants.IS_DELETE + " = 0",
                    new String[] { String.valueOf(machineId), personId },null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    PersonAccessDB model = new PersonAccessDB(
                            cursor.getInt(0), //PERSON_ACCESS_ID
                            cursor.getString(1), //PERSON_ID
                            cursor.getInt(2), //MACHINE_ID
                            cursor.getString(3), //FROM_DATE
                            cursor.getString(4), //TO_DATE
                            cursor.getInt(5) //IS_DELETE
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());

            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                lsData = getListPersonAccess ( personId, machineId);
                Log.d(TAG, "Retry getListPersonAccess " + retryTime);
            }
        }
        return lsData;
    }

    public synchronized List<PersonAccessDB> getListPersonAccess (int machineId) {
        List<PersonAccessDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(PERSON_ACCESS, new String[] {
                            Constants.PERSON_ACCESS_ID,
                            Constants.PERSON_ID,
                            Constants.MACHINE_ID,
                            Constants.FROM_DATE,
                            Constants.TO_DATE,
                            Constants.IS_DELETE
                    },
                    Constants.MACHINE_ID + "=?", new String[] { String.valueOf(machineId) },
                    null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    PersonAccessDB model = new PersonAccessDB(
                            cursor.getInt(0), //PERSON_ACCESS_ID
                            cursor.getString(1), //PERSON_ID
                            cursor.getInt(2), //MACHINE_ID
                            cursor.getString(3), //FROM_DATE
                            cursor.getString(4), //TO_DATE
                            cursor.getInt(5) //IS_DELETE
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }
    //End person access

    public synchronized String addEvent(EventDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            try{
                values.put(Constants.EVENT_ID, model.getEventId());
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.FACE_ID, model.getFaceId());
                values.put(Constants.FINGER_ID, model.getFingerId());
                values.put(Constants.CARD_NO, model.getCardNo());
                values.put(Constants.FACE_IMAGE, model.getFacePath());
                values.put(Constants.MACHINE_ID, model.getMachineId());
                values.put(Constants.ACCESS_DATE, model.getAccessDate());
                values.put(Constants.ACCESS_TIME, model.getAccessTime());
                values.put(Constants.ACCESS_TYPE, model.getAccessType());
                values.put(Constants.TEMPERATURE, model.getTemperature());
                values.put(Constants.GENDER, model.getGender());
                values.put(Constants.AGE, model.getAge());
                values.put(Constants.WEAR_MASK, model.getWearMask());
                values.put(Constants.SCORE_MATCH, model.getScoreMatch());
                values.put(Constants.ERROR_CODE, model.getErrorCode());
                values.put(Constants.STATUS, model.getStatus());
                values.put(Constants.NOTE, model.getNote());
            }catch (Exception ex){
                logger.error("Event value error: " + ex.getMessage());
            }
            db.insert(EVENT, null, values);
        }catch (Exception ex){
            logger.error("Save event error: " + ex.getMessage());
        }finally {
            closeObject(db);
        }
        return model.getEventId();
    }

    public synchronized void deleteAllEvent () {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(EVENT, null, null);
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.close();
        }
    }

    public synchronized void deleteEventByAccessDate (String accessDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(EVENT, Constants.MACHINE_ID + "=?", new String[] { String.valueOf(accessDate) });
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.close();
        }
    }

    public synchronized void updateEventStatus (EventDB model, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(Constants.STATUS, status);
            db.update(EVENT, values, Constants.EVENT_ID + " = ?", new String[]{"" + model.getEventId()});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void updateEventStatusById (String eventId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(Constants.STATUS, status);
            db.update(EVENT, values, Constants.EVENT_ID + " = ?", new String[]{"" + eventId});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void updateEventStatus (String eventDate, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(Constants.STATUS, status);
            db.update(EVENT, values, Constants.ACCESS_DATE + " = ?", new String[]{eventDate});
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized List<EventDB> getEventOfPerson(String personId, String accessDate) {
        List<EventDB> listLog = new ArrayList<EventDB>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(EVENT, new String[] {
                            Constants.EVENT_ID,
                            Constants.PERSON_ID,
                            Constants.FACE_ID,
                            Constants.FINGER_ID,
                            Constants.CARD_NO,
                            Constants.FACE_IMAGE,
                            Constants.MACHINE_ID,
                            Constants.ACCESS_DATE,
                            Constants.ACCESS_TIME,
                            Constants.ACCESS_TYPE,
                            Constants.TEMPERATURE,
                            Constants.GENDER,
                            Constants.AGE,
                            Constants.WEAR_MASK,
                            Constants.SCORE_MATCH,
                            Constants.ERROR_CODE,
                            Constants.STATUS,
                            Constants.NOTE
                    },
                    Constants.PERSON_ID + " = ? AND " + "strftime('%Y-%m-%d', substr(" + Constants.ACCESS_TIME + ", 1, 19))" + " = ? ", new String[]{ personId, accessDate},
                    null, null, Constants.ACCESS_TIME + " ASC ", null);

            if (cursor.moveToFirst()) {
                do {
                    EventDB model = new EventDB(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getDouble(10),
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getInt(13),
                            cursor.getDouble(14),
                            cursor.getString(15),
                            cursor.getInt(16),
                            cursor.getString(17)
                    );

                    listLog.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }

        return listLog;
    }

    public synchronized List<EventReportModel> getAllEventReport() {
        String selectQuery =
            " SELECT   ACCESS_DATE," +
            "    COUNT(*) AS TOTAL_RECORD," +
            "    SUM(CASE WHEN STATUS = 2 THEN 1 ELSE 0 END) AS SYNC_RECORD," +
            "    SUM(CASE WHEN STATUS = 1 THEN 1 ELSE 0 END) AS WAIT_RECORD" +
            " FROM EVENT " +
            " GROUP BY ACCESS_DATE " +
            " ORDER BY ACCESS_DATE DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<EventReportModel> lsData = new ArrayList<>();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    EventReportModel model = new EventReportModel(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3)
                    );
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized EventReportModel getEventReport(String reportDate) {
        String selectQuery =
                " SELECT   ACCESS_DATE," +
                        "    COUNT(*) AS TOTAL_RECORD," +
                        "    SUM(CASE WHEN STATUS = 2 THEN 1 ELSE 0 END) AS SYNC_RECORD," +
                        "    SUM(CASE WHEN STATUS = 1 THEN 1 ELSE 0 END) AS WAIT_RECORD" +
                        " FROM EVENT " +
                        " WHERE ACCESS_DATE = '" + reportDate + "'" +
                        " GROUP BY ACCESS_DATE " +
                        " ORDER BY ACCESS_DATE DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        EventReportModel result = null;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = new EventReportModel(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized List<EventDB> getAllEventNeedSync() {
        List<EventDB> listLog = new ArrayList<EventDB>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(EVENT, new String[] {
                            Constants.EVENT_ID,
                            Constants.PERSON_ID,
                            Constants.FACE_ID,
                            Constants.FINGER_ID,
                            Constants.CARD_NO,
                            Constants.FACE_IMAGE,
                            Constants.MACHINE_ID,
                            Constants.ACCESS_DATE,
                            Constants.ACCESS_TIME,
                            Constants.ACCESS_TYPE,
                            Constants.TEMPERATURE,
                            Constants.GENDER,
                            Constants.AGE,
                            Constants.WEAR_MASK,
                            Constants.SCORE_MATCH,
                            Constants.ERROR_CODE,
                            Constants.STATUS,
                            Constants.NOTE
                    },
                    Constants.STATUS + " = ? " ,new String[]{ "" + Constants.EVENT_STATUS_WAIT_SYNC},
                    null, null, Constants.ACCESS_TIME + " ASC ", "10");

            if (cursor.moveToFirst()) {
                do {
                    EventDB model = new EventDB(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getDouble(10),
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getInt(13),
                            cursor.getDouble(14),
                            cursor.getString(15),
                            cursor.getInt(16),
                            cursor.getString(17)
                    );

                    listLog.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }

        return listLog;
    }

    public synchronized List<EventDB> getAllEventSynched() {
        List<EventDB> listLog = new ArrayList<EventDB>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(EVENT, new String[] {
                            Constants.EVENT_ID,
                            Constants.PERSON_ID,
                            Constants.FACE_ID,
                            Constants.FINGER_ID,
                            Constants.CARD_NO,
                            Constants.FACE_IMAGE,
                            Constants.MACHINE_ID,
                            Constants.ACCESS_DATE,
                            Constants.ACCESS_TIME,
                            Constants.ACCESS_TYPE,
                            Constants.TEMPERATURE,
                            Constants.GENDER,
                            Constants.AGE,
                            Constants.WEAR_MASK,
                            Constants.SCORE_MATCH,
                            Constants.ERROR_CODE,
                            Constants.STATUS,
                            Constants.NOTE
                    },
                    Constants.STATUS + " = ? AND " + Constants.ACCESS_DATE + " < ? ", new String[]{ String.valueOf(Constants.EVENT_STATUS_SYNCED), StringUtils.currentDateSQLiteformat()},
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    EventDB model = new EventDB(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getDouble(10),
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getInt(13),
                            cursor.getDouble(14),
                            cursor.getString(15),
                            cursor.getInt(16),
                            cursor.getString(17)
                    );

                    listLog.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return listLog;
    }

    public synchronized void addAccessTimeSeg (AccessTimeSegDB model) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.TIME_SEG_ID, model.getId());
            values.put(Constants.COMP_ID, model.getCompId());
            values.put(Constants.TIME_SEG_NAME, model.getTimeSegName());

            values.put(Constants.MONDAY_START1, model.getMondayStart1());
            values.put(Constants.MONDAY_START2, model.getMondayStart2());
            values.put(Constants.MONDAY_START3, model.getMondayStart3());
            values.put(Constants.MONDAY_START4, model.getMondayStart4());
            values.put(Constants.MONDAY_END1, model.getMondayEnd1());
            values.put(Constants.MONDAY_END2, model.getMondayEnd2());
            values.put(Constants.MONDAY_END3, model.getMondayEnd3());
            values.put(Constants.MONDAY_END4, model.getMondayEnd4());

            values.put(Constants.TUESDAY_START1, model.getTuesdayStart1());
            values.put(Constants.TUESDAY_START2, model.getTuesdayStart2());
            values.put(Constants.TUESDAY_START3, model.getTuesdayStart3());
            values.put(Constants.TUESDAY_START4, model.getTuesdayStart4());
            values.put(Constants.TUESDAY_END1, model.getTuesdayEnd1());
            values.put(Constants.TUESDAY_END2, model.getTuesdayEnd2());
            values.put(Constants.TUESDAY_END3, model.getTuesdayEnd3());
            values.put(Constants.TUESDAY_END4, model.getTuesdayEnd4());

            values.put(Constants.WEDNESDAY_START1, model.getWednesdayStart1());
            values.put(Constants.WEDNESDAY_START2, model.getWednesdayStart2());
            values.put(Constants.WEDNESDAY_START3, model.getWednesdayStart3());
            values.put(Constants.WEDNESDAY_START4, model.getWednesdayStart4());
            values.put(Constants.WEDNESDAY_END1, model.getWednesdayEnd1());
            values.put(Constants.WEDNESDAY_END2, model.getWednesdayEnd2());
            values.put(Constants.WEDNESDAY_END3, model.getWednesdayEnd3());
            values.put(Constants.WEDNESDAY_END4, model.getWednesdayEnd4());

            values.put(Constants.THURSDAY_START1, model.getThusdayStart1());
            values.put(Constants.THURSDAY_START2, model.getThusdayStart2());
            values.put(Constants.THURSDAY_START3, model.getThusdayStart3());
            values.put(Constants.THURSDAY_START4, model.getThusdayStart4());
            values.put(Constants.THURSDAY_END1, model.getThusdayEnd1());
            values.put(Constants.THURSDAY_END2, model.getThusdayEnd2());
            values.put(Constants.THURSDAY_END3, model.getThusdayEnd3());
            values.put(Constants.THURSDAY_END4, model.getThusdayEnd4());

            values.put(Constants.FRIDAY_START1, model.getFridayStart1());
            values.put(Constants.FRIDAY_START2, model.getFridayStart2());
            values.put(Constants.FRIDAY_START3, model.getFridayStart3());
            values.put(Constants.FRIDAY_START4, model.getFridayStart4());
            values.put(Constants.FRIDAY_END1, model.getFridayEnd1());
            values.put(Constants.FRIDAY_END2, model.getFridayEnd2());
            values.put(Constants.FRIDAY_END3, model.getFridayEnd3());
            values.put(Constants.FRIDAY_END4, model.getFridayEnd4());

            values.put(Constants.SATURDAY_START1, model.getSaturdayStart1());
            values.put(Constants.SATURDAY_START2, model.getSaturdayStart2());
            values.put(Constants.SATURDAY_START3, model.getSaturdayStart3());
            values.put(Constants.SATURDAY_START4, model.getSaturdayStart4());
            values.put(Constants.SATURDAY_END1, model.getSaturdayEnd1());
            values.put(Constants.SATURDAY_END2, model.getSaturdayEnd2());
            values.put(Constants.SATURDAY_END3, model.getSaturdayEnd3());
            values.put(Constants.SATURDAY_END4, model.getSaturdayEnd4());

            values.put(Constants.SUNDAY_START1, model.getSundayStart1());
            values.put(Constants.SUNDAY_START2, model.getSundayStart2());
            values.put(Constants.SUNDAY_START3, model.getSundayStart3());
            values.put(Constants.SUNDAY_START4, model.getSundayStart4());
            values.put(Constants.SUNDAY_END1, model.getSundayEnd1());
            values.put(Constants.SUNDAY_END2, model.getSundayEnd2());
            values.put(Constants.SUNDAY_END3, model.getSundayEnd3());
            values.put(Constants.SUNDAY_END4, model.getSundayEnd4());

            db.insert(ACCESS_TIME_SEG, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListAccessTimeSeg (List<AccessTimeSegDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            for (AccessTimeSegDB model: lsData
            ) {
                ContentValues values = new ContentValues();
                values.put(Constants.TIME_SEG_ID, model.getId());
                values.put(Constants.COMP_ID, model.getCompId());
                values.put(Constants.TIME_SEG_NAME, model.getTimeSegName());

                values.put(Constants.MONDAY_START1, model.getMondayStart1());
                values.put(Constants.MONDAY_START2, model.getMondayStart2());
                values.put(Constants.MONDAY_START3, model.getMondayStart3());
                values.put(Constants.MONDAY_START4, model.getMondayStart4());
                values.put(Constants.MONDAY_END1, model.getMondayEnd1());
                values.put(Constants.MONDAY_END2, model.getMondayEnd2());
                values.put(Constants.MONDAY_END3, model.getMondayEnd3());
                values.put(Constants.MONDAY_END4, model.getMondayEnd4());

                values.put(Constants.TUESDAY_START1, model.getTuesdayStart1());
                values.put(Constants.TUESDAY_START2, model.getTuesdayStart2());
                values.put(Constants.TUESDAY_START3, model.getTuesdayStart3());
                values.put(Constants.TUESDAY_START4, model.getTuesdayStart4());
                values.put(Constants.TUESDAY_END1, model.getTuesdayEnd1());
                values.put(Constants.TUESDAY_END2, model.getTuesdayEnd2());
                values.put(Constants.TUESDAY_END3, model.getTuesdayEnd3());
                values.put(Constants.TUESDAY_END4, model.getTuesdayEnd4());

                values.put(Constants.WEDNESDAY_START1, model.getWednesdayStart1());
                values.put(Constants.WEDNESDAY_START2, model.getWednesdayStart2());
                values.put(Constants.WEDNESDAY_START3, model.getWednesdayStart3());
                values.put(Constants.WEDNESDAY_START4, model.getWednesdayStart4());
                values.put(Constants.WEDNESDAY_END1, model.getWednesdayEnd1());
                values.put(Constants.WEDNESDAY_END2, model.getWednesdayEnd2());
                values.put(Constants.WEDNESDAY_END3, model.getWednesdayEnd3());
                values.put(Constants.WEDNESDAY_END4, model.getWednesdayEnd4());

                values.put(Constants.THURSDAY_START1, model.getThusdayStart1());
                values.put(Constants.THURSDAY_START2, model.getThusdayStart2());
                values.put(Constants.THURSDAY_START3, model.getThusdayStart3());
                values.put(Constants.THURSDAY_START4, model.getThusdayStart4());
                values.put(Constants.THURSDAY_END1, model.getThusdayEnd1());
                values.put(Constants.THURSDAY_END2, model.getThusdayEnd2());
                values.put(Constants.THURSDAY_END3, model.getThusdayEnd3());
                values.put(Constants.THURSDAY_END4, model.getThusdayEnd4());

                values.put(Constants.FRIDAY_START1, model.getFridayStart1());
                values.put(Constants.FRIDAY_START2, model.getFridayStart2());
                values.put(Constants.FRIDAY_START3, model.getFridayStart3());
                values.put(Constants.FRIDAY_START4, model.getFridayStart4());
                values.put(Constants.FRIDAY_END1, model.getFridayEnd1());
                values.put(Constants.FRIDAY_END2, model.getFridayEnd2());
                values.put(Constants.FRIDAY_END3, model.getFridayEnd3());
                values.put(Constants.FRIDAY_END4, model.getFridayEnd4());

                values.put(Constants.SATURDAY_START1, model.getSaturdayStart1());
                values.put(Constants.SATURDAY_START2, model.getSaturdayStart2());
                values.put(Constants.SATURDAY_START3, model.getSaturdayStart3());
                values.put(Constants.SATURDAY_START4, model.getSaturdayStart4());
                values.put(Constants.SATURDAY_END1, model.getSaturdayEnd1());
                values.put(Constants.SATURDAY_END2, model.getSaturdayEnd2());
                values.put(Constants.SATURDAY_END3, model.getSaturdayEnd3());
                values.put(Constants.SATURDAY_END4, model.getSaturdayEnd4());

                values.put(Constants.SUNDAY_START1, model.getSundayStart1());
                values.put(Constants.SUNDAY_START2, model.getSundayStart2());
                values.put(Constants.SUNDAY_START3, model.getSundayStart3());
                values.put(Constants.SUNDAY_START4, model.getSundayStart4());
                values.put(Constants.SUNDAY_END1, model.getSundayEnd1());
                values.put(Constants.SUNDAY_END2, model.getSundayEnd2());
                values.put(Constants.SUNDAY_END3, model.getSundayEnd3());
                values.put(Constants.SUNDAY_END4, model.getSundayEnd4());

                db.insert(ACCESS_TIME_SEG, null, values);
            }

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized List<AccessTimeSegDB> getAllAccessTimeSeg () {
        List<AccessTimeSegDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(ACCESS_TIME_SEG, new String[] {
                            Constants.TIME_SEG_ID,
                            Constants.COMP_ID,
                            Constants.TIME_SEG_NAME,
                            Constants.MONDAY_START1,
                            Constants.MONDAY_END1,
                            Constants.MONDAY_START2,
                            Constants.MONDAY_END2,
                            Constants.MONDAY_START3,
                            Constants.MONDAY_END3,
                            Constants.MONDAY_START4,
                            Constants.MONDAY_END4,
                            Constants.TUESDAY_START1,
                            Constants.TUESDAY_END1,
                            Constants.TUESDAY_START2,
                            Constants.TUESDAY_END2,
                            Constants.TUESDAY_START3,
                            Constants.TUESDAY_END3,
                            Constants.TUESDAY_START4,
                            Constants.TUESDAY_END4,
                            Constants.WEDNESDAY_START1,
                            Constants.WEDNESDAY_END1,
                            Constants.WEDNESDAY_START2,
                            Constants.WEDNESDAY_END2,
                            Constants.WEDNESDAY_START3,
                            Constants.WEDNESDAY_END3,
                            Constants.WEDNESDAY_START4,
                            Constants.WEDNESDAY_END4,
                            Constants.THURSDAY_START1,
                            Constants.THURSDAY_END1,
                            Constants.THURSDAY_START2,
                            Constants.THURSDAY_END2,
                            Constants.THURSDAY_START3,
                            Constants.THURSDAY_END3,
                            Constants.THURSDAY_START4,
                            Constants.THURSDAY_END4,
                            Constants.FRIDAY_START1,
                            Constants.FRIDAY_END1,
                            Constants.FRIDAY_START2,
                            Constants.FRIDAY_END2,
                            Constants.FRIDAY_START3,
                            Constants.FRIDAY_END3,
                            Constants.FRIDAY_START4,
                            Constants.FRIDAY_END4,
                            Constants.SATURDAY_START1,
                            Constants.SATURDAY_END1,
                            Constants.SATURDAY_START2,
                            Constants.SATURDAY_END2,
                            Constants.SATURDAY_START3,
                            Constants.SATURDAY_END3,
                            Constants.SATURDAY_START4,
                            Constants.SATURDAY_END4,
                            Constants.SUNDAY_START1,
                            Constants.SUNDAY_END1,
                            Constants.SUNDAY_START2,
                            Constants.SUNDAY_END2,
                            Constants.SUNDAY_START3,
                            Constants.SUNDAY_END3,
                            Constants.SUNDAY_START4,
                            Constants.SUNDAY_END4
                    },
                    null, null,
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    AccessTimeSegDB model = new AccessTimeSegDB(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10),
                            cursor.getString(11),
                            cursor.getString(12),
                            cursor.getString(13),
                            cursor.getString(14),
                            cursor.getString(15),
                            cursor.getString(16),
                            cursor.getString(17),
                            cursor.getString(18),
                            cursor.getString(19),
                            cursor.getString(20),
                            cursor.getString(21),
                            cursor.getString(22),
                            cursor.getString(23),
                            cursor.getString(24),
                            cursor.getString(25),
                            cursor.getString(26),
                            cursor.getString(27),
                            cursor.getString(28),
                            cursor.getString(29),
                            cursor.getString(30),
                            cursor.getString(31),
                            cursor.getString(32),
                            cursor.getString(33),
                            cursor.getString(34),
                            cursor.getString(35),
                            cursor.getString(36),
                            cursor.getString(37),
                            cursor.getString(38),
                            cursor.getString(39),
                            cursor.getString(40),
                            cursor.getString(41),
                            cursor.getString(42),
                            cursor.getString(43),
                            cursor.getString(44),
                            cursor.getString(45),
                            cursor.getString(46),
                            cursor.getString(47),
                            cursor.getString(48),
                            cursor.getString(49),
                            cursor.getString(50),
                            cursor.getString(51),
                            cursor.getString(52),
                            cursor.getString(53),
                            cursor.getString(54),
                            cursor.getString(55),
                            cursor.getString(56),
                            cursor.getString(57),
                            cursor.getString(58)
                    );

                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, "getAllAccessTimeSeg: " + ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized AccessTimeSegDB getAccessTimeSeg (int accessTimeSegId) {
        AccessTimeSegDB model = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int retryTime = 0;
        boolean needRetry = false;

        try{
            cursor = db.query(ACCESS_TIME_SEG, new String[] {
                            Constants.TIME_SEG_ID,
                            Constants.COMP_ID,
                            Constants.TIME_SEG_NAME,
                            Constants.MONDAY_START1,
                            Constants.MONDAY_END1,
                            Constants.MONDAY_START2,
                            Constants.MONDAY_END2,
                            Constants.MONDAY_START3,
                            Constants.MONDAY_END3,
                            Constants.MONDAY_START4,
                            Constants.MONDAY_END4,
                            Constants.TUESDAY_START1,
                            Constants.TUESDAY_END1,
                            Constants.TUESDAY_START2,
                            Constants.TUESDAY_END2,
                            Constants.TUESDAY_START3,
                            Constants.TUESDAY_END3,
                            Constants.TUESDAY_START4,
                            Constants.TUESDAY_END4,
                            Constants.WEDNESDAY_START1,
                            Constants.WEDNESDAY_END1,
                            Constants.WEDNESDAY_START2,
                            Constants.WEDNESDAY_END2,
                            Constants.WEDNESDAY_START3,
                            Constants.WEDNESDAY_END3,
                            Constants.WEDNESDAY_START4,
                            Constants.WEDNESDAY_END4,
                            Constants.THURSDAY_START1,
                            Constants.THURSDAY_END1,
                            Constants.THURSDAY_START2,
                            Constants.THURSDAY_END2,
                            Constants.THURSDAY_START3,
                            Constants.THURSDAY_END3,
                            Constants.THURSDAY_START4,
                            Constants.THURSDAY_END4,
                            Constants.FRIDAY_START1,
                            Constants.FRIDAY_END1,
                            Constants.FRIDAY_START2,
                            Constants.FRIDAY_END2,
                            Constants.FRIDAY_START3,
                            Constants.FRIDAY_END3,
                            Constants.FRIDAY_START4,
                            Constants.FRIDAY_END4,
                            Constants.SATURDAY_START1,
                            Constants.SATURDAY_END1,
                            Constants.SATURDAY_START2,
                            Constants.SATURDAY_END2,
                            Constants.SATURDAY_START3,
                            Constants.SATURDAY_END3,
                            Constants.SATURDAY_START4,
                            Constants.SATURDAY_END4,
                            Constants.SUNDAY_START1,
                            Constants.SUNDAY_END1,
                            Constants.SUNDAY_START2,
                            Constants.SUNDAY_END2,
                            Constants.SUNDAY_START3,
                            Constants.SUNDAY_END3,
                            Constants.SUNDAY_START4,
                            Constants.SUNDAY_END4
                    },
                    Constants.TIME_SEG_ID + " = ? ", new String[] { String.valueOf(accessTimeSegId)},
                    null, null, null, null);

            if (cursor.moveToFirst()){
                model = new AccessTimeSegDB(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getString(18),
                        cursor.getString(19),
                        cursor.getString(20),
                        cursor.getString(21),
                        cursor.getString(22),
                        cursor.getString(23),
                        cursor.getString(24),
                        cursor.getString(25),
                        cursor.getString(26),
                        cursor.getString(27),
                        cursor.getString(28),
                        cursor.getString(29),
                        cursor.getString(30),
                        cursor.getString(31),
                        cursor.getString(32),
                        cursor.getString(33),
                        cursor.getString(34),
                        cursor.getString(35),
                        cursor.getString(36),
                        cursor.getString(37),
                        cursor.getString(38),
                        cursor.getString(39),
                        cursor.getString(40),
                        cursor.getString(41),
                        cursor.getString(42),
                        cursor.getString(43),
                        cursor.getString(44),
                        cursor.getString(45),
                        cursor.getString(46),
                        cursor.getString(47),
                        cursor.getString(48),
                        cursor.getString(49),
                        cursor.getString(50),
                        cursor.getString(51),
                        cursor.getString(52),
                        cursor.getString(53),
                        cursor.getString(54),
                        cursor.getString(55),
                        cursor.getString(56),
                        cursor.getString(57),
                        cursor.getString(58)
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "getAccessTimeSeg: " + ex.getMessage());
            if(ex.getMessage().contains("Cannot perform this operation because the connection pool has been closed")){
                needRetry = true;
            }
        } finally {
            closeObject(cursor);
            closeObject(db);

            if(needRetry && retryTime < 3){
                retryTime++;
                model = getAccessTimeSeg (accessTimeSegId);
                Log.d(TAG, "Retry getAccessTimeSeg " + retryTime);
            }
        }
        return model;
    }

    public synchronized void deleteAccessTimeSeg(int timeSegId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(ACCESS_TIME_SEG, Constants.TIME_SEG_ID + " = ?", new String[] { String.valueOf(timeSegId) });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }
    //end

    //Start twin
    public synchronized void addTwin (TwinDB model) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.TWIN_ID, model.getId());
            values.put(Constants.PERSON_ID, model.getPersonId());
            values.put(Constants.SIMILAR_PERSON_ID, model.getSimilarPersonId());
            db.insert(TWIN, null, values);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void addListTwin (List<TwinDB> lsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (TwinDB model: lsData
            ) {
                ContentValues values = new ContentValues();
                values.put(Constants.TWIN_ID, model.getId());
                values.put(Constants.PERSON_ID, model.getPersonId());
                values.put(Constants.SIMILAR_PERSON_ID, model.getSimilarPersonId());
                db.insert(TWIN, null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized void deleteTwin (int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(TWIN, Constants.TWIN_ID + " = ?", new String[] { String.valueOf(id) });
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(db);
        }
    }

    public synchronized void deleteListTwin (List<TwinDB> lsdata) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            for (TwinDB model : lsdata ) {
                db.delete(TWIN, Constants.TWIN_ID + " = ?", new String[] { String.valueOf(model.getId()) });
            }

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.endTransaction();
            closeObject(db);
        }
    }

    public synchronized TwinDB getTwin (int id) {
        TwinDB model = new TwinDB();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(TWIN, new String[] {
                            Constants.TWIN_ID,
                            Constants.PERSON_ID,
                            Constants.SIMILAR_PERSON_ID
                    },
                    Constants.TWIN_ID + "=?", new String[] { String.valueOf(id) },
                    null, null, null);

            if (cursor.moveToFirst()){
                model = new TwinDB(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
            }
        }catch (Exception ex){
            Log.e(TAG, "Error getTwin: " + ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return model;
    }

    public synchronized List<TwinDB> getListTwinOfPerson(String personId) {
        List<TwinDB> lsData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.query(TWIN, new String[] {
                            Constants.TWIN_ID,
                            Constants.PERSON_ID,
                            Constants.SIMILAR_PERSON_ID
                    },
                    Constants.PERSON_ID + "=?", new String[] { String.valueOf(personId) },
                    null, null, null);

            // Duyệt trên con trỏ, và thêm vào danh sách.
            if (cursor.moveToFirst()) {
                do {
                    TwinDB model = new TwinDB(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    );
                    // Add list.
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, "Error get getListTwinOfPerson: " + ex.getMessage());
        } finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }
    //End twin

    public synchronized int countTotalPerson() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p "
                        + " WHERE 1=1"
                        + " AND p.STATUS = 1 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalStaff() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p "
                        + " WHERE 1=1"
                        + " AND p.STATUS = 1 AND p.PERSON_TYPE = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalGuest() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p "
                        + " WHERE 1=1"
                        + " AND p.STATUS = 1 AND p.PERSON_TYPE = 2";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalStaffValid() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p, FACE f "
                        + " WHERE p.PERSON_ID = f.PERSON_ID "
                        + " AND p.STATUS = 1 "
                        + " AND p.PERSON_TYPE = 1 "
                        + " AND f.FACE_STATUS = 1 "
                        + " AND length(f.FACE_FEATURE) > 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalGuestValid() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p, FACE f "
                        + " WHERE p.PERSON_ID = f.PERSON_ID "
                        + " AND p.STATUS = 1 "
                        + " AND p.PERSON_TYPE = 2 "
                        + " AND f.FACE_STATUS = 1 "
                        + " AND length(f.FACE_FEATURE) > 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalStaffInvalid() {
        String selectQuery =
              " SELECT COUNT(1) "
            + " FROM PERSON p, FACE f "
            + " WHERE p.PERSON_ID = f.PERSON_ID "
            + " AND p.STATUS = 1 "
            + " AND p.PERSON_TYPE = 1 "
            + " AND f.FACE_STATUS = 1 "
            + " AND length(f.FACE_FEATURE) = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalStaffInvalidNoFace() {
        String selectQuery =
                  " SELECT COUNT(1) "
                + " FROM PERSON p "
                + " WHERE 1=1 "
                + " AND p.STATUS = 1 "
                + " AND p.PERSON_TYPE = 1 "
                + " AND p.PERSON_ID NOT IN (SELECT PERSON_ID FROM FACE)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalGuestInvalid() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p, FACE f "
                        + " WHERE p.PERSON_ID = f.PERSON_ID "
                        + " AND p.STATUS = 1 "
                        + " AND p.PERSON_TYPE = 2 "
                        + " AND f.FACE_STATUS = 1 "
                        + " AND length(f.FACE_FEATURE) = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized int countTotalGuestInvalidNoFace() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p "
                        + " WHERE 1=1 "
                        + " AND p.STATUS = 1 "
                        + " AND p.PERSON_TYPE = 2 "
                        + " AND p.PERSON_ID NOT IN (SELECT PERSON_ID FROM FACE)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized List<PersonReport> getAllPersonReport() {
        String selectQuery =
                " SELECT p.PERSON_ID, p.COMP_ID, p.DEPT_ID, p.FULL_NAME, p.PERSON_CODE, p.POSITION, p.JOBDUTIES, p.PERSON_TYPE, length(f.FACE_FEATURE) FACE_FEATURE "
                + " FROM PERSON p, FACE f "
                + " WHERE p.PERSON_ID = f.PERSON_ID "
                + " AND p.STATUS = 1 "
                + " AND f.FACE_STATUS = 1"
                + " UNION ALL"
                + " SELECT p.PERSON_ID, p.COMP_ID, p.DEPT_ID, p.FULL_NAME, p.PERSON_CODE, p.POSITION, p.JOBDUTIES, p.PERSON_TYPE, 0 FACE_FEATURE "
                + " FROM PERSON p"
                + " WHERE p.STATUS = 1 "
                + " AND p.PERSON_ID NOT IN (SELECT PERSON_ID FROM FACE)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<PersonReport> lsData = new ArrayList<PersonReport>();
        MachineDB machine = ConfigUtil.getMachine();
        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    PersonReport model = new PersonReport(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getInt(7),
                            cursor.getInt(8)
                    );
                    model.setMachineId(machine.getMachineId());
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized int getAllPersonReportInvalidNoFace() {
        String selectQuery =
                " SELECT COUNT(1) "
                        + " FROM PERSON p "
                        + " WHERE 1=1 "
                        + " AND p.STATUS = 1 "
                        + " AND p.PERSON_ID NOT IN (SELECT PERSON_ID FROM FACE)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return result;
    }

    public synchronized List<PersonReport> getPersonReport(String personId) {
        String selectQuery =
                " SELECT p.PERSON_ID, p.COMP_ID, p.DEPT_ID, p.FULL_NAME, p.PERSON_CODE, p.POSITION, p.JOBDUTIES, p.PERSON_TYPE, length(f.FACE_FEATURE) "
                        + " FROM PERSON p, FACE f "
                        + " WHERE p.PERSON_ID = f.PERSON_ID "
                        + " AND p.STATUS = 1 "
                        + " AND f.FACE_STATUS = 1"
                        + " AND upper(p.PERSON_ID) = upper('" + personId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<PersonReport> lsData = new ArrayList<PersonReport>();
        MachineDB machine = ConfigUtil.getMachine();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    PersonReport model = new PersonReport(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getInt(7),
                            cursor.getInt(8)
                    );
                    model.setMachineId(machine.getMachineId());
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized List<PersonReport> getPersonReportNoFace(String personId) {
        String selectQuery =
                " SELECT p.PERSON_ID, p.COMP_ID, p.DEPT_ID, p.FULL_NAME, p.PERSON_CODE, p.POSITION, p.JOBDUTIES, p.PERSON_TYPE, -1 "
                        + " FROM PERSON p "
                        + " WHERE 1=1 "
                        + " AND p.STATUS = 1 "
                        + " AND upper(p.PERSON_ID) = upper('" + personId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        List<PersonReport> lsData = new ArrayList<PersonReport>();
        MachineDB machine = ConfigUtil.getMachine();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    PersonReport model = new PersonReport(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getInt(7),
                            cursor.getInt(8)
                    );
                    model.setMachineId(machine.getMachineId());
                    lsData.add(model);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return lsData;
    }

    public synchronized int countRecord(String tableName){
        String selectQuery = " SELECT count(1) TOTAL FROM " + tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int totalRecord = 0;

        try{
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null){
                cursor.moveToFirst();
            }
            totalRecord = cursor.getInt(0);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            closeObject(cursor);
            closeObject(db);
        }
        return totalRecord;
    }

    private void closeObject(Cursor cursor){
        if(cursor != null){
            cursor.close();
        }
    }

    private void closeObject(SQLiteDatabase db){
        if(db != null){
            db.close();
        }
    }

    // Phương thức để lấy cấu trúc của một bảng
    public String getTableStructure(String tableName) {
        StringBuffer strStructure = new StringBuffer();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        String columnName = cursor.getString(cursor.getColumnIndex("name"));
                        String columnType = cursor.getString(cursor.getColumnIndex("type"));
                        strStructure.append(columnName + ":" + columnType + "\n");
                    }
                } finally {
                    cursor.close();
                }
            }
        }catch (Exception ex){
            logger.error("Error getTableStructure " + tableName + " " + ex.getMessage());
        }
        return strStructure.toString();
    }


    public Map<String, String> getDatabaseStructure(){
        Map<String, String> hm = new HashMap<>();
        hm.put("DatabaseName", DATABASE_NAME);
        hm.put("DatabaseVersion", "" + DATABASE_VERSION);
        hm.put(PERSON, getTableStructure(PERSON));
        hm.put(FACE, getTableStructure(FACE));
        hm.put(CARD, getTableStructure(CARD));
        hm.put(PERSON_GROUP, getTableStructure(PERSON_GROUP));
        hm.put(ACCESS_TIME_SEG, getTableStructure(ACCESS_TIME_SEG));
        hm.put(MACHINE, getTableStructure(MACHINE));
        hm.put(GROUP_ACCESS, getTableStructure(GROUP_ACCESS));
        hm.put(EVENT, getTableStructure(EVENT));
        hm.put(PERSON_ACCESS, getTableStructure(PERSON_ACCESS));
        return hm;
    }

    public void truncateAllDatabase(){
        try{
            truncateTable(PERSON_ACCESS);
            truncateTable(GROUP_ACCESS);
            truncateTable(PERSON_GROUP);
            truncateTable(ACCESS_TIME_SEG);
            truncateTable(EVENT);
            truncateTable(FACE);
            truncateTable(CARD);
            truncateTable(MACHINE);
            truncateTable(PERSON);
        }catch (Exception ex){
            logger.error("");
        }
    }

    private void truncateTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(tableName, null, null);
            db.delete(tableName, "name = ?", new String[] { tableName });
        }catch ( Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            db.close();
        }
    }
}

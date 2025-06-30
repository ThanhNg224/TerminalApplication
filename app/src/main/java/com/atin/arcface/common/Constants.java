package com.atin.arcface.common;

public class Constants {
    public static final String MOI_TRUONG = PartnerName.GELEXIMCO;
    public static final int MAX_EAT_PER_DAY = 2;

    public static final int UPLOAD_PENDING = 0;
    public static final int UPLOAD_DONE = 1;


    public class PartnerName {
        public static final String DEVELOP = "DEVELOP";
        public static final String TEST = "TEST";
        public static final String VANHUONG = "VANHUONG";
        public static final String GELEXIMCO = "GELEXIMCO";
    }


    public static final String APP_ID = "FH8DuoKtwLTctLKAdmKL5y6NfTGL9UvXukE2wQHeoPyp";
    public static final String SDK_KEY = "GvsVMvpQ6pQXzrrA5FaK1s5j6K6ZEvtSFA62vuWS22Sr";

    public static final String ACTIVE_CONFIG_FILE_NAME = "activeConfig.txt";
    public static final String PREFIX_TOKEN = "DDT ";
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    public static int BUFFER_SIZE = 1024;
    public static final int MAX_COUNT_DOWN_SCANN_QRCODE = 100;

    /**
     * Path of registration map
     */
    public static final String DEFAULT_REGISTER_FACES_DIR = "sdcard/arcfacedemo/register";
    public static final String DEFAULT_LOG_PATH = "ATINAccess";

    /**
     * User info
     */
    public static final String USER_DEFAULT = "admin";
    public static final String PASSWORD_DEFAULT = "atin@123";
    public static final String SUPER_PASSWORD = "atin@123";

    /**
     * Start of ATSoftware
     */
    public static final int MIN_FACE_SIZE = 70;
    public static final int MIN_FACE_SIZE_RGB = 75;
    public static final int MIN_FACE_SIZE_IR = 40;

    public static final int CAMERA_RGB = 0;
    public static final int CAMERA_IR = 1;
    public static final int MAXTIME_RETRY_SEARCH_FACE = 1;
    public static final String DEFAULT_PERSON_ID_NOT_FOUND = "00000000-0000-0000-0000-000000000000";

    /**
     * Độ lệch ngang của dữ liệu xem trước IR so với dữ liệu xem trước RGB Lưu ý: đó là dữ liệu xem trước. Dữ liệu xem trước của máy ảnh thông thường là chiều rộng> chiều cao
     */
    public static final int HORIZONTAL_OFFSET = 0;
    /**
     * Độ lệch dọc của dữ liệu xem trước IR so với dữ liệu xem trước RGB Lưu ý: đó là dữ liệu xem trước. Dữ liệu xem trước của camera chung là chiều rộng> chiều cao
     */
    public static final int ASF_OP_0_ONLY = 1;
    public static final int ASF_OP_90_ONLY = 2;
    public static final int ASF_OP_180_ONLY = 3;
    public static final int ASF_OP_270_ONLY = 4;

    /**
     * Clear object
     */
    public static final int CLEAR_EVENT_LOG = 1;
    public static final int CLEAR_PERSON = 2;
    public static final int CLEAR_PARAM_CONFIG = 3;
    public static final int CLEAR_DAILY = 4;
    public static final int CLEAR_ALL = 5;

    /**
     * Tên sharepreference
     */
    public static final String SHARE_PREFERENCE = "PREF";
    public static final String AUTHENTICATION_PREFERENCE = "Authentication";

    /**
     * Event status
     */
    public static final int EVENT_STATUS_WAIT_SYNC = 1;
    public static final int EVENT_STATUS_SYNCED = 2;
    public static final int EVENT_STATUS_SYNC_ERROR = 3;
    public static final int EVENT_STATUS_DELETE = 4;

    /**
     * Progress Action
     */
    public static final int PROGRESS_SHOW = 1;
    public static final int PROGRESS_REFRESH = 2;
    public static final int PROGRESS_CLOSE = 3;

    /**
     * Tên file audio
     * */
    public static final String SOUND_CHECKIN_SUCCESS = "checkin_success.mp3";
    public static final String SOUND_CHECKOUT_SUCCESS = "checkout_success.mp3";
    public static final String SOUND_CHECKIN_PERSON_NOT_FOUND = "checkin_person_not_found.mp3";
    public static final String SOUND_CHECKOUT_PERSON_NOT_FOUND = "checkout_person_not_found.mp3";
    public static final String SOUND_INSUFFICIENT_AUTHORITY = "insufficient_authority.mp3";
    public static final String SOUND_NETWORK_ERROR = "network_error.mp3";
    public static final String SOUND_TIME_OUT_ERROR = "time_out_error.mp3";
    public static final String SOUND_CHECK_OFFLINE_SUCCESS = "check_offline_success.mp3";
    public static final String SOUND_PERSON_NOT_REGISTER = "person_not_register.mp3";

    public static final String ERROR_CONNECT_EXCEPTION = "class java.net.ConnectException";
    public static final String ERROR_TIME_OUT_EXCEPTION = "class java.net.SocketTimeoutException";

    public static final String DATE_FORMAT_ddMMyyyyHHmmssSSS = "ddMMyyyyHHmmssSSS";
    public static final String DATE_FORMAT_ddMMyyyy = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATETIME_SQLITE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String LABEL_DEVICE_CODE = "DeviceCode";
    public static final String LABEL_QR_CODE = "Qrcode";
    public static final String LABEL_FINGER_ID = "FingerId";
    public static final String LABEL_CARD_ID = "CardId";
    public static final String LABEL_PERSON_NAME = "PersonName";
    public static final String LABEL_SIGN = "Sign";
    public static final String LABEL_ORDER_TYPE = "Oper_type";
    public static final String LABEL_NONCE = "Nonce";
    public static final String LABEL_PHOTO = "Photo";
    public static final String LABEL_FACE_ID = "faceId";
    public static final String LABEL_HOST_DOMAIN = "hostOrDomain";
    public static final String LABEL_USER_ID = "userId";
    public static final String LABEL_USER_NAME = "name";
    public static final String LABEL_DEPARTMENT_NAME = "departmentName";
    public static final String LABEL_POSITION_NAME = "positionName";
    public static final String LABEL_CARD_NUMBER = "cardNum";
    public static final String LABEL_FINGER_PRINT = "fingerPrint";
    public static final String LABEL_IMAGE_BASE64 = "base64";
    public static final String LABEL_DEVICE_ID = "deviceId";
    public static final String LABEL_FLAG = "flag";
    public static final String LABEL_DATA= "data";
    public static final String LABEL_QUEUE_SERVER_TO_CAMERA_PREFIX = "image.atin.queue.stc.";
    public static final String LABEL_QUEUE_CAMERA_TO_SERVER_PREFIX = "image.atin.queue.cts.";
    public static final String LABEL_FORMAT_CODE = "UTF-8";

    public static final String PREF_COMPANY_NAME = "COMPANY_NAME";
    public static final String PREF_MAC_ADDRESS = "MAC_ADDRESS";
    public static final String PREF_DEVICE_CODE = "DEVICE_CODE";
    public static final String PREF_IMEI = "IMEI";
    public static final String PREF_FACE_DETECTION = "FACE_DETECTION";
    public static final String PREF_LIVENESS_DETECT = "LIVENESS_DETECT";
    public static final String PREF_RECOGNIZE_MASK = "RECOGNIZE_MASK";
    public static final String PREF_USE_TEMPERATURE_CAMERA = "USE_TEMPERATURE_CAMERA";
    public static final String PREF_USE_LED = "USE_LED";
    public static final String PREF_USE_SOUND = "USE_SOUND";
    public static final String PREF_HIDE_NAVIGATION_BAR = "HIDE_NAVIGATION_BAR";
    public static final String PREF_HIDE_STATUS_BAR = "HIDE_STATUS_BAR";
    public static final String PREF_CALIBRATION_CAMERA_STATUS = "CALIBRATION_CAMERA_STATUS";
    public static final String PREF_DISTANCE_TEMPERATURE_CAMERA = "DISTANCE_TEMPERATURE_CAMERA";
    public static final String PREF_WARNING_HIGHT_TEMPERATURE_CAMERA = "WARNING_HIGHT_TEMPERATURE_CAMERA";
    public static final String PREF_ALARM_HIGHT_TEMPERATURE_CAMERA = "ALARM_HIGHT_TEMPERATURE_CAMERA";
    public static final String PREF_NOT_ALLOW_HIGHT_TEMPERATURE_CAMERA = "NOT_ALLOW_HIGHT_TEMPERATURE_CAMERA";
    public static final String PREF_MAX_THRESHOLD_TEMPERATURE_CAMERA = "MAX_THRESHOLD_TEMPERATURE_CAMERA";
    public static final String PREF_CALIBRATION_TEMPERATURE_CAMERA = "CALIBRATION_TEMPERATURE_CAMERA";
    public static final String PREF_TIME_BEFORE_SLEEP = "TIME_BEFORE_SLEEP";
    public static final String PREF_TIME_BETWEEN_RECOGNIZE = "TIME_BETWEEN_RECOGNIZE";
    public static final String PREF_TIME_WAITING_OPEN_DOOR = "TIME_WAITING_OPEN_DOOR";
    public static final String PREF_TIME_WAITING_OPEN_DOOR_MONITOR = "TIME_WAITING_OPEN_DOOR_MONITOR";
    public static final String PREF_TIME_PARENT_AUTH = "TIME_PARENT_AUTH";
    public static final String PREF_DELAY_DOOR_TIME = "DELAY_DOOR_TIME";
    public static final String PREF_DELAY_RECOGNIZE_TIME = "DELAY_RECOGNIZE_TIME";
    public static final String PREF_DATABASE_VERSION = "DATABASE_VERSION";
    public static final String PREF_DAY_DELETE_EVENT = "DAY_DELETE_EVENT";
    public static final String PREF_AUTOMATIC_START = "AUTOMATIC_START";
    public static final String PREF_AUTOMATIC_SLEEP = "PREF_AUTOMATIC_SLEEP";
    public static final String PREF_USE_CARD_READER = "USE_CARD_READER";
    public static final String PREF_SAVE_PERSON_UNREG = "SAVE_PERSON_UNREG";
    public static final String PREF_USE_BUSINESS_CHECK = "USE_BUSINESS_CHECK";
    public static final String PREF_CHECK_MASK = "CHECK_MASK";
    public static final String PREF_SHOW_VACCINE_INFO = "SHOW_VACCINE_INFO";
    public static final String PREF_SHOW_BODY_TEMPERATURE = "SHOW_BODY_TEMPERATURE";
    public static final String PREF_CHECK_TEMPERATURE = "CHECK_TEMPERATURE";
    public static final String PREF_DISTANCE_DETECT = "DISTANCE_DETECT";
    public static final String PREF_DISTANCE_DETECT_PIXEL = "DISTANCE_DETECT_PIXEL";
    public static final String PREF_OFFLINE = "USE_OFFLINE";
    public static final String PREF_THRESHOLD = "THRESHOLD";
    public static final String PREF_NOMASK_QUALITY_THRESHOLD = "NOMASK_QUALITY_THRESHOLD";
    public static final String PREF_MASK_QUALITY_THRESHOLD = "PREF_MASK_QUALITY_THRESHOLD";
    public static final String PREF_REGISTER_QUALITY_THRESHOLD = "REGISTER_QUALITY_THRESHOLD";
    public static final String PREF_TEMPERATURE_THRESHOLD = "TEMPERATURE_THRESHOLD";
    public static final String PREF_BUSINESS_SERVER_HOST = "BUSINESS_SERVER_HOST";
    public static final String PREF_BUSINESS_SERVER_PORT = "BUSINESS_SERVER_PORT";
    public static final String PREF_MQ_LIMIT_MESSAGE = "MQ_LIMIT_MESSAGE";
    public static final String PREF_APP_ID = "APP_ID";
    public static final String PREF_SDK_KEY = "SDK_KEY";
    public static final String PREF_ACTIVE_ALREADY = "ACTIVE_ALREADY";
    public static final String PREF_LOGO = "LOGO";
    public static final String PREF_DEFAULT_LOGO_RESOURCE = "DEFAULT_LOGO_RESOURCE";
    public static final String PREF_MIRROR_VERTICAL = "MIRROR_VERTICAL";
    public static final String PREF_MIRROR_HORIZONTAL = "MIRROR_HORIZONTAL";
    public static final String PREF_USERNAME = "USERNAME";
    public static final String PREF_PASSWORD = "PASSWORD";
    public static final String PREF_USERNAME_AUTH = "USERNAME_AUTH";
    public static final String PREF_PASSWORD_AUTH = "PASSWORD_AUTH";
    public static final String PREF_TOKEN_AUTH = "TOKEN_AUTH ";
    public static final String PREF_USE_AUTO_REBOOT = "USE_AUTO_REBOOT";
    public static final String PREF_FREQ_REBOOT_HOUR = "FREQ_REBOOT_HOUR";
    public static final String PREF_FREQ_REBOOT_MINUTE = "FREQ_REBOOT_MINUTE";
    public static final String PREF_NO_DELAY = "PREF_NO_DELAY";

    public static final String SERVER_STATUS_CONNECTED = "SERVER_STATUS_CONNECTED";
    public static final String SERVER_STATUS_NOT_CONNECTED = "SERVER_STATUS_NOT_CONNECTED";
    public static final String NOT_FOUND = "NOT_FOUND";

    public static final String ID = "Id";
    public static final String PERSON_ACCESS_ID = "Id";
    public static final String IS_DELETE = "IS_DELETE";
    public static final String PERSON_GROUP_ID = "ID";
    public static final String ABSENCE_ID = "ID";
    public static final String TWIN_ID = "ID";
    public static final String POSITION = "POSITION";
    public static final String JOBDUTIES = "JOBDUTIES";
    public static final String STATUS = "STATUS";
    public static final String VACCINE = "VACCINE";
    public static final String SYNC_TIME = "SYNC_TIME";
    public static final String SYNC_ID = "SYNC_ID";
    public static final String PERSON_ID = "PERSON_ID";
    public static final String SIMILAR_PERSON_ID = "SIMILAR_PERSON_ID";
    public static final String PERSON_CODE = "PERSON_CODE";
    public static final String PERSON_TYPE = "PERSON_TYPE";
    public static final String FULL_NAME = "FULL_NAME";
    public static final String COMP_ID = "COMP_ID";
    public static final String COMP_NAME = "COMP_NAME";
    public static final String DEPT_ID = "DEPT_ID";
    public static final String DEPT_NAME = "DEPT_NAME";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String GROUP_CODE = "GROUP_CODE";
    public static final String GUARANTOR_ID = "GUARANTOR_ID";
    public static final String GUARANTEE_ID = "GUARANTEE_ID";
    public static final String GROUP_CODE_MAP = "GROUP_CODE_MAP";
    public static final String DEPARTMENT = "DEPARTMENT";
    public static final String AVATAR_BASE64  = "AVATAR_BASE64";
    public static final String AVATAR = "AVATAR";
    public static final String AVATAR_PATH = "AVATAR_PATH";
    public static final String FACE_BASE64 = "FACE_BASE64";
    public static final String FACE = "FACE";
    public static final String SPECIAL_CODE = "SPECIAL_CODE";
    public static final String FACE_ID = "FACE_ID";
    public static final String FINGER_ID = "FACE_ID";
    public static final String FACE_CODE = "FACE_CODE";
    public static final String FACE_FEATURE = "FACE_FEATURE";
    public static final String FEATURE_PATH = "FEATURE_PATH";
    public static final String FACE_URL= "FACE_URL";
    public static final String FACE_PATH = "FACE_PATH";
    public static final String FACE_STATUS = "FACE_STATUS";
    public static final String CARD_NO = "CARD_NO";
    public static final String CARD_ID = "CARD_ID";
    public static final String EVENT_ID = "EVENT_ID";
    public static final String METHOD = "METHOD";
    public static final String FACE_IMAGE = "FACE_IMAGE";
    public static final String MACHINE_ID = "MACHINE_ID";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String DEVICE_FUNCTION = "DEVICE_FUNCTION";
    public static final String ACCESS_DATE = "ACCESS_DATE";
    public static final String ACCESS_TIME = "ACCESS_TIME";
    public static final String ACCESS_TYPE = "ACCESS_TYPE";
    public static final String TEMPERATURE = "TEMPERATURE";
    public static final String GENDER = "GENDER";
    public static final String AGE = "AGE";
    public static final String WEAR_MASK = "WEAR_MASK";
    public static final String SCORE_MATCH = "SCORE_MATCH";
    public static final String SYNC_DATETIME = "SYNC_DATETIME";
    public static final String ERROR_CODE = "ERROR_CODE";
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String ERROR = "ERROR";
    public static final String DATE_REG = "DATE_REG";
    public static final String REG_DATE = "REG_DATE";
    public static final String FROM_DATE = "FROM_DATE";
    public static final String TO_DATE = "TO_DATE";
    public static final String PERSON_NEXT_TIME = "PERSON_NEXT_TIME";
    public static final String SESSION = "SESSION";
    public static final String TRAN_TYPE = "TRAN_TYPE";
    public static final String AREA_DETAIL_ID = "AREA_DETAIL_ID";
    public static final String TIME_SEG_ID = "TIME_SEG_ID";
    public static final String TIME_SEG_NAME = "TIME_SEG_NAME";
    public static final String SUNDAY_START1 = "SUNDAY_START1";
    public static final String SUNDAY_END1   = "SUNDAY_END1";
    public static final String SUNDAY_START2 = "SUNDAY_START2";
    public static final String SUNDAY_END2   = "SUNDAY_END2";
    public static final String SUNDAY_START3 = "SUNDAY_START3";
    public static final String SUNDAY_END3   = "SUNDAY_END3";
    public static final String SUNDAY_START4 = "SUNDAY_START4";
    public static final String SUNDAY_END4   = "SUNDAY_END4";
    public static final String MONDAY_START1 = "MONDAY_START1";
    public static final String MONDAY_END1   = "MONDAY_END1";
    public static final String MONDAY_START2 = "MONDAY_START2";
    public static final String MONDAY_END2   = "MONDAY_END2";
    public static final String MONDAY_START3 = "MONDAY_START3";
    public static final String MONDAY_END3   = "MONDAY_END3";
    public static final String MONDAY_START4 = "MONDAY_START4";
    public static final String MONDAY_END4   = "MONDAY_END4";
    public static final String TUESDAY_START1 = "TUESDAY_START1";
    public static final String TUESDAY_END1   = "TUESDAY_END1";
    public static final String TUESDAY_START2 = "TUESDAY_START2";
    public static final String TUESDAY_END2   = "TUESDAY_END2";
    public static final String TUESDAY_START3 = "TUESDAY_START3";
    public static final String TUESDAY_END3   = "TUESDAY_END3";
    public static final String TUESDAY_START4 = "TUESDAY_START4";
    public static final String TUESDAY_END4   = "TUESDAY_END4";
    public static final String WEDNESDAY_START1 = "WEDNESDAY_START1";
    public static final String WEDNESDAY_END1   = "WEDNESDAY_END1";
    public static final String WEDNESDAY_START2 = "WEDNESDAY_START2";
    public static final String WEDNESDAY_END2   = "WEDNESDAY_END2";
    public static final String WEDNESDAY_START3 = "WEDNESDAY_START3";
    public static final String WEDNESDAY_END3   = "WEDNESDAY_END3";
    public static final String WEDNESDAY_START4 = "WEDNESDAY_START4";
    public static final String WEDNESDAY_END4   = "WEDNESDAY_END4";
    public static final String THURSDAY_START1 = "THURSDAY_START1";
    public static final String THURSDAY_END1   = "THURSDAY_END1";
    public static final String THURSDAY_START2 = "THURSDAY_START2";
    public static final String THURSDAY_END2   = "THURSDAY_END2";
    public static final String THURSDAY_START3 = "THURSDAY_START3";
    public static final String THURSDAY_END3   = "THURSDAY_END3";
    public static final String THURSDAY_START4 = "THURSDAY_START4";
    public static final String THURSDAY_END4   = "THURSDAY_END4";
    public static final String FRIDAY_START1 = "FRIDAY_START1";
    public static final String FRIDAY_END1   = "FRIDAY_END1";
    public static final String FRIDAY_START2 = "FRIDAY_START2";
    public static final String FRIDAY_END2   = "FRIDAY_END2";
    public static final String FRIDAY_START3 = "FRIDAY_START3";
    public static final String FRIDAY_END3   = "FRIDAY_END3";
    public static final String FRIDAY_START4 = "FRIDAY_START4";
    public static final String FRIDAY_END4   = "FRIDAY_END4";
    public static final String SATURDAY_START1 = "SATURDAY_START1";
    public static final String SATURDAY_END1   = "SATURDAY_END1";
    public static final String SATURDAY_START2 = "SATURDAY_START2";
    public static final String SATURDAY_END2   = "SATURDAY_END2";
    public static final String SATURDAY_START3 = "SATURDAY_START3";
    public static final String SATURDAY_END3   = "SATURDAY_END3";
    public static final String SATURDAY_START4 = "SATURDAY_START4";
    public static final String SATURDAY_END4   = "SATURDAY_END4";
    public static final String GA_ID   = "ID";
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String FUNCTION = "FUNCTION";
    public static final String IPADDRESS = "IPADDRESS";
    public static final String IMEI = "IMEI";
    public static final String MAC = "MAC";
    public static final String SERVER_IP = "SERVER_IP";
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String FRAUD_PROOF = "FRAUD_PROOF";
    public static final String AUTO_START = "AUTO_START";
    public static final String ANGLE_DETECT = "ANGLE_DETECT";
    public static final String AUTO_SAVE_VISITOR = "AUTO_SAVE_VISITOR";
    public static final String DISTANT_DETECT = "DISTANT_DETECT";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String USERNAME_SERVER = "USERNAME_TOKEN";
    public static final String PASSWORD_SERVER = "PASSWORD_TOKEN";
    public static final String STORAGE = "STORAGE";
    public static final String USAGE_STORAGE = "USAGE_STORAGE";
    public static final String FIRMWARE_VERSION = "FIRMWARE_VERSION";
    public static final String VOLUME = "VOLUME";
    public static final String BRIGHTNESS = "BRIGHTNESS";
    public static final String SUBNETMASK = "SUBNETMASK";
    public static final String DELAY = "DELAY";
    public static final String LED = "LED";
    public static final String FP_THRESHOLD = "FP_THRESHOLD";
    public static final String FACE_THRESHOLD = "FACE_THRESHOLD";
    public static final String TEMPERATURE_THRESHOLD = "TEMPERATURE_THRESHOLD";
    public static final String USE_MASK = "USE_MASK";
    public static final String USE_TEMPERATURE = "USE_TEMPERATURE";
    public static final String USE_VACCINE = "USE_VACCINE";
    public static final String FACE_COUNT = "FACE_COUNT";
    public static final String CARD_COUNT = "CARD_COUNT";
    public static final String DATE_APPLY = "DATE_APPLY";
    public static final String APPLY_DATE = "APPLY_DATE";
    public static final String NOTE = "NOTE";
    public static final String PROCESS_PCCOVID_QRCODE = "PROCESS_PCCOVID_QRCODE";
    public static final String USE_PCCOVID = "USE_PCCOVID";
    public static final String PCCOVID_PHONE = "PCCOVID_PHONE";
    public static final String PCCOVID_LOCATION = "PCCOVID_LOCATION";
    public static final String PCCOVID_TOKEN = "PCCOVID_TOKEN";
    public static final String DAILY_REBOOT = "DAILY_REBOOT";
    public static final String RESTART_TIME = "RESTART_TIME";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String NO_DELAY = "NO_DELAY";
    public static final String CPU_INFO = "CPU_INFO";
    public static final String NOMASK_QUALITY_THRESHOLD = "NOMASK_QUALITY_THRESHOLD";
    public static final String MASK_QUALITY_THRESHOLD = "MASK_QUALITY_THRESHOLD";
    public static final String REGISTER_QUALITY_THRESHOLD = "REGISTER_QUALITY_THRESHOLD";
    public static final String AUTO_SLEEP = "AUTO_SLEEP";
    public static final String SHOW_NOTIFICATION = "SHOW_NOTIFICATION";

    public static final String ACCESS_TURN_TYPE = "ACCESS_TURN_TYPE";

    public static final String ACCESS_TURN_NUMBER = "ACCESS_TURN_NUMBER";

    public static final int FACE_RECOGNIZE = 1;
    public static final int CARD_RECOGNIZE = 2;
    public static final int FINGER_RECOGNIZE = 3;

    public static final String RESPONSE_PERSON_EXISTS = "PERSON_EXISTS";
    public static final String RESPONSE_PERSON_NOT_EXISTS = "PERSON_NOT_EXISTS";
    public static final String RESPONSE_SUCCESS = "RESPONSE_SUCCESS";
    public static final String RESPONSE_ERROR = "RESPONSE_ERROR";
    public static final String WAITING_RESPONSE = "WAITING_RESPONSE";
    public static final String LOGO = "LOGO";

    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;
    public static final int TIME_KEEPING = 3;
    public static final int CANTEEN = 4;

    // table moi


    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String INIT_PREFERENCE = "INIT_PREFERENCE";
    public static final String INIT_BUSSINES_ACCESS_DATA = "INIT_BUSSINES_ACCESS_DATA";
    public static final String ALARM_REBOOT_OS = "ALARM_REBOOT_OS";
    public static final String SYNCHRONIZE_STATUS = "SYNCHRONIZE_STATUS";

    public static final boolean DOOR_OPEN = true;
    public static final boolean DOOR_CLOSE = false;

    public static final int LED_RED = 1;
    public static final int LED_GREEN = 2;
    public static final int LED_WHITE = 3;
    public static final int LED_ALL = 4;
    public static final int LED_YELLOW = 5;
    public static final int LED_BLUE = 6;
    public static final String FIELD_SAPERATE = ",";

    public class AccessType{
        public static final int FACE_RECOGNIZE = 1;
        public static final int CARD_RECOGNIZE = 2;
        public static final int QRCODE_RECOGNIZE = 4;
        public static final int CANTEEN       = 3;
    }

    public static class SyncDataType
    {
        public static final int PERSON = 1;
        public static final int FACE = 2;
        public static final int CARD = 3;
        public static final int MACHINE = 4;
        public static final int PERSON_GROUP = 5;
        public static final int GROUP_ACCESS = 6;
        public static final int ACCESS_TIME_SEG = 7;
        public static final int CLEAR_EVENT = 8;
        public static final int CLEAR_DATA = 9;
        public static final int PERSON_ACCESS = 10;
        public static final int UPLOAD_DATABASE = 11;
        public static final int NORMAL_OPEN_DOOR = 12;
        public static final int NORMAL_CLOSE_DOOR = 13;
        public static final int REBOOT_DEVICE = 14;
        public static final int GET_ALL_PERSON_INFO = 15;
        public static final int GET_PERSON_INFO = 16;
        public static final int SUMMARY_PERSON_LOG = 17;
        public static final int UPLOAD_LOG_FILE = 18;
        public static final int GROUP = 19;
        public static final int TICKET = 20;
        public static final int AREA = 21;
        public static final int AREA_GROUP = 22;
        public static final int TWIN = 23;
        public static final int CLEAR_LOG_FILE = 24;
        public static final int RESEND_EVENT = 25;

        public static final int MEAL_BY_MONTH = 26;
        public static final int CANTEEN_DAILY_HISTORY_SNAP   = 27;
        public static final int CANTEEN_MONTHLY_HISTORY_SNAP = 28;
    }

    public static class SyncAction
    {
        public static final int SAVE = 1;
        public static final int REMOVE = 2;
        public static final int ADD = 3;
        public static final int UPDATE = 4;
    }

    public class FileExtension{
        public static final String JPEG = ".jpg";
        public static final String APK = ".apk";
    }

    public class PersonType{
        public static final int STAFF = 1;
        public static final int VISITOR = 2;
    }

    public class VaccineValue{
        public static final int Unknown = 0;
        public static final int Zero = -1;
        public static final int FirstTime = 1;
        public static final int SecondTime = 2;
        public static final int ThirdTime = 3;
        public static final int FourthTime = 4;
    }

    public static final int FACE_IMAGE_MAXIMUM_WIDTH = 980;
    public static final int FACE_IMAGE_MAXIMUM_HEIGHT = 980;

    public class Value {
        public static final int DEFAULT_LANGUAGE_ID = 0;
        public static final String DEFAULT_ERROR_NETWORK_TIME = "2000-01-01 00:00:00";
        public static final String TEMPERATURE_PACKAGE_NAME = "com.telpo.temperatureservice";
    }

    public class RequestCode {
        public static final int CHANGE_LANGUAGE = 10000;
    }

    public class QRCodeScanStatus{
        public static final int CANCEL = 0;
        public static final int TYPING = 1;
        public static final int PROCESSING = 2;
        public static final int FINISH = 3;
    }

    public class ScreenBrightnessValue{
        public static final float MIN = 0.01f;
        public static final float MAX = 1f;
    }

    public class ProcessResult{
        public static final int FAILURE = 0;
        public static final int SUCCESS = 1;
        public static final int RELOAD = 2;
        public static final int RESTART = 3;
    }
    public static class EventStatus {
        public static final int WAIT_SYNC = 0;   // chưa đẩy lên server
        public static final int SYNCED    = 1;   // đã đồng bộ
    }

}
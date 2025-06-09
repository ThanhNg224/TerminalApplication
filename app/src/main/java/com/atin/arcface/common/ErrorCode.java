package com.atin.arcface.common;

public class ErrorCode {
    public static final String COMMON_FACE_NOT_FOUND = "0001";
    public static final String COMMON_CONFIG_ERROR = "0002";
    public static final String COMMON_ACCESS_VALID = "0003";
    public static final String COMMON_ACCESS_OUT_OF_SERVICE_TIME = "0004";
    public static final String COMMON_NOT_ACCESS = "0005";
    public static final String COMMON_NO_MASK = "0006";
    public static final String COMMON_HIGHT_TEMPERATURE = "0007";
    public static final String COMMON_MACHINE_NOT_DEFINE = "0008";
    public static final String COMMON_BEEP_SOUND = "0009";
    public static final String COMMON_REQUEST_CHECK_FACE = "0010";
    public static final String COMMON_REQUEST_PCCOVID_QRCODE = "0011";
    public static final String COMMON_PCCOVID_VALID = "0012";
    public static final String COMMON_CONFIG_PCCOVID_ERROR = "0013";
    public static final String COMMON_CARD_NOT_ACCESS = "0014";
    public static final String COMMON_CARD_ACCESS_VALID = "0015";
    public static final String COMMON_CARD_ACCESS_OUT_OFF_SERVICE_TIME = "0016";
    public static final String COMMON_DETECT_QR_CODE = "0017";
    public static final String COMMON_EXPIRED = "0018";
    public static final String COMMON_USED_UP_TURN_ACCESS = "0019";
    public static final String COMMON_TICKET_NOT_FOUND = "0020";
    public static final String COMMON_ACCESS_TOO_SHORT = "0021";
    public static final String COMMON_CANTEEN_ACCESS_VALID = "0022";
    public static final String COMMON_ACCOUNT_SUPPEND = "0023";
    public static final String COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY = "0024";
    public static final String COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH = "0025";

    public static final String CHECKIN_VALID = "1001";
    public static final String CHECKIN_OUT_OF_SERVICE_TIME = "1002";
    public static final String CHECKIN_NOT_ACCESS = "1003";
    public static final String CHECKIN_FACE_NOT_FOUND = "1004";
    public static final String CHECKIN_CARD_VALID = "1005";
    public static final String CHECKIN_CARD_OUT_OF_SERVICE_TIME = "1006";
    public static final String CHECKIN_CARD_NOT_ACCESS = "1007";
    public static final String CHECKIN_CARD_FACE_NOT_FOUND = "1008";

    public static final String CHECKOUT_VALID = "2001";
    public static final String CHECKOUT_OUT_OF_SERVICE_TIME = "2002";
    public static final String CHECKOUT_NOT_ACCESS = "2003";
    public static final String CHECKOUT_FACE_NOT_FOUND = "2004";
    public static final String CHECKOUT_CARD_VALID = "2005";
    public static final String CHECKOUT_CARD_OUT_OF_SERVICE_TIME = "2006";
    public static final String CHECKOUT_CARD_NOT_ACCESS = "2007";
    public static final String CHECKOUT_CARD_FACE_NOT_FOUND = "2008";

    public static final String TIMEKEEPING_VALID = "3001";
    public static final String TIMEKEEPING_OUT_OF_SERVICE_TIME = "3002";
    public static final String TIMEKEEPING_NOT_ACCESS = "3003";
    public static final String TIMEKEEPING_FACE_NOT_FOUND = "3004";
    public static final String TIMEKEEPING_CARD_VALID = "3005";
    public static final String TIMEKEEPING_CARD_OUT_OF_SERVICE_TIME = "3006";
    public static final String TIMEKEEPING_CARD_NOT_ACCESS = "3007";
    public static final String TIMEKEEPING_CARD_FACE_NOT_FOUND = "3008";

    public static final String SPECIAL_READ_QRCODE_SUCCESS = "x0001";
    public static final String SPECIAL_CONFIRM_TWINS = "x0002";
    public static final String BUTTON_CLICK = "x0010";
    public static final String TING_NOTIFICATION = "x0011";
}

package com.atin.arcface.common;

import android.content.Context;

import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.HardwareUtils;
import com.common.pos.api.util.PosUtil;
import com.innohi.YNHAPI;

public class Hardware {
    private static void controlLed(int ledColor, int delayTime, String deviceCode, Context context) {
        turnLed(1, ledColor, deviceCode, context);
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        turnLed(0, ledColor, deviceCode, context);
    }

    private static void controlDoor(int delayTime, String deviceCode, Context context) {
        turnDoor(1, deviceCode, context);
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        turnDoor(0, deviceCode, context);
    }

    private static void closeAndOpen(int delayTime, String deviceCode, Context context) {
        turnDoor(0, deviceCode, context);
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        turnDoor(1, deviceCode, context);
    }

    public static void openLed(String resultCode, int delayTime, String deviceCode, Context context) {
        switch (resultCode) {
            case ErrorCode.COMMON_FACE_NOT_FOUND:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_NOT_ACCESS:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_EXPIRED:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_NO_MASK:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_HIGHT_TEMPERATURE:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_ACCESS_VALID:
                controlLed(Constants.LED_GREEN, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_USED_UP_TURN_ACCESS:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_TICKET_NOT_FOUND:
                controlLed(Constants.LED_RED, delayTime, deviceCode, context);
                break;

            default:
                break;
        }
    }

    public static void turnLed(int status, int ledColor, String deviceCode, Context context) {
        switch (deviceCode){
            case MachineName.RAKINDA_F3:
                HardwareUtils.turnLed(ledColor, status, deviceCode);
                break;

            case MachineName.RAKINDA_A80M:
                switch (ledColor){
                    case Constants.LED_WHITE:
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.white.light.open" : "com.custom.white.light.close");
                        break;

                    case Constants.LED_RED:
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.red.light.open" : "com.custom.red.light.close");
                        break;

                    case Constants.LED_GREEN:
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.green.light.open" : "com.custom.green.light.close");
                        break;

                    case Constants.LED_ALL:
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.white.light.open" : "com.custom.white.light.close");
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.red.light.open" : "com.custom.red.light.close");
                        BaseUtil.broadcastAction(context, status == 1 ? "com.custom.green.light.open" : "com.custom.green.light.close");
                        break;
                }
                break;

            case MachineName.RAKINDA_F6:
                switch (ledColor){
                    case Constants.LED_WHITE:
                        SingletonObject.getInstance(context).getmYNHAPI().setLightBrightness(YNHAPI.LIGHT_WHITE, status == 1 ? 204 : 0);
                        break;

                    case Constants.LED_RED:
                        SingletonObject.getInstance(context).getmYNHAPI().setLightBrightness(YNHAPI.LIGHT_RED, status == 1 ? 204 : 0);
                        break;

                    case Constants.LED_GREEN:
                        SingletonObject.getInstance(context).getmYNHAPI().setLightBrightness(YNHAPI.LIGHT_GREEN, status == 1 ? 204 : 0);
                        break;
                }
                break;

            case MachineName.TELPO_F8:
                HardwareUtils.turnLed(ledColor, status, deviceCode);
                break;

            case MachineName.TELPO_TPS980P:
                HardwareUtils.turnLed(ledColor, status, deviceCode);
                break;

            case MachineName.TELPO_TPS950:
                HardwareUtils.turnLed(ledColor, status, deviceCode);
                break;

            default:
                break;
        }
    }

    public static void turnLight(int status, int ledColor, String deviceCode) {
        switch (deviceCode){
            case MachineName.RAKINDA_F3:
                HardwareUtils.turnLed(ledColor, status, deviceCode);
                break;

            case MachineName.TELPO_F8:
                PosUtil.setLedLight(status == 0 ? 0 : 80);
                break;

            case MachineName.TELPO_TPS980P:
                PosUtil.setLedLight(status == 0 ? 0 : 80);
                break;

            case MachineName.TELPO_TPS950:
                PosUtil.setLedLight(status == 0 ? 0 : 80);
                break;

            default:
                break;
        }
    }

    public static void turnIrLight(int status, String deviceCode, Context context) {
        switch (deviceCode){
            case MachineName.TELPO_F8:
                PosUtil.setIRLed(1);
                break;

            case MachineName.RAKINDA_A80M:
                BaseUtil.broadcastAction(context, "com.custom.red_infre.light.open");
                break;

            case MachineName.TELPO_TPS980P:
                PosUtil.setIRLed(1);
                break;

            case MachineName.TELPO_TPS950:
                PosUtil.setIRLed(1);
                break;

            default:
                break;
        }
    }

    public static void openDoor(String resultCode, int delayTime, String deviceCode, Context context) {
        switch (resultCode) {
            case ErrorCode.CHECKIN_VALID:
                controlDoor(delayTime, deviceCode, context);
                break;

            case ErrorCode.CHECKOUT_VALID:
                controlDoor(delayTime, deviceCode, context);
                break;

            case ErrorCode.TIMEKEEPING_VALID:
                controlDoor(delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_NO_MASK:
                controlDoor(delayTime, deviceCode, context);
                break;

            case ErrorCode.COMMON_ACCESS_VALID:
                controlDoor(delayTime, deviceCode, context);
                break;

            default:
                break;
        }
    }

    public static void openDoorOnly(String resultCode, String deviceCode, Context context) {
        switch (resultCode) {
            case ErrorCode.CHECKIN_VALID:
                closeAndOpen(50, deviceCode, context);
                break;

            case ErrorCode.CHECKOUT_VALID:
                closeAndOpen(50, deviceCode, context);
                break;

            case ErrorCode.TIMEKEEPING_VALID:
                closeAndOpen(50, deviceCode, context);
                break;

            case ErrorCode.COMMON_NO_MASK:
                closeAndOpen(50, deviceCode, context);
                break;

            case ErrorCode.COMMON_ACCESS_VALID:
                closeAndOpen(50, deviceCode, context);
                break;

            default:
                break;
        }
    }

    public static void turnDoor(int status, String deviceCode, Context context) {
        switch (deviceCode){
            case MachineName.RAKINDA_F3:
                HardwareUtils.openOrCloseDoor(status == 1 ? Constants.DOOR_OPEN : Constants.DOOR_CLOSE);
                break;

            case MachineName.RAKINDA_A80M:
                BaseUtil.broadcastAction(context, status == 1 ? "com.custom.relay.open" : "com.custom.relay.close");
                break;

            case MachineName.RAKINDA_F6:
                SingletonObject.getInstance(context).getmYNHAPI().setGpioState(YNHAPI.RELAY, (status == 1 ? YNHAPI.GpioState.HIGH : YNHAPI.GpioState.LOW));
                break;

            case MachineName.TELPO_F8:
                PosUtil.setRelayPower(status);
                PosUtil.getBellStatus();
                break;

            case MachineName.TELPO_TPS980P:
                PosUtil.setRelayPower(status);
                break;

            case MachineName.TELPO_TPS950:
                PosUtil.setRelayPower(status);
                break;

            default:
                break;
        }
    }
}

package com.atin.arcface.util;

import android.util.Log;

import com.atin.arcface.common.Constants;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.util.hardware.LedUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HardwareUtils {

    private static Boolean DIOFlags[] = {false, false, false, false, false};
    private static final String DIOPATH[] = {"/sys/class/gpio/gpio400/value",
            "/sys/class/gpio/gpio372/value",
            "/sys/class/gpio/gpio414/value",
            "/sys/class/gpio/gpio409/value",
            "/sys/class/gpio/gpio397/value",};

    private static String HUMAN_DET = "/sys/class/gpio/gpio410/value";
    private static String HUMAN_DET_EN = "/sys/class/gpio/gpio411/value";

    private static final int MSG = 0;
    private static final int DOOR = 1;
    private static final String LED_W_EN = "/sys/class/gpio/gpio398/value";
    private static final String LED_G_EN =  "/sys/class/gpio/gpio486/value";
    private static final String  LED_R_EN ="/sys/class/gpio/gpio375/value";

    // true open door false close door
    public static void openOrCloseDoor(boolean flag) {
        if (!flag) {
            writeValue(DIOPATH[DOOR], "1");
            DIOFlags[DOOR] = true;
        } else {
            writeValue(DIOPATH[DOOR], "0");
            DIOFlags[DOOR] = false;
        }
    }

    public static void turnOnGreenLed() {
        writeValue(LED_G_EN, "1");
    }

    public static void turnOffGreenLed() {
        writeValue(LED_G_EN, "0");
    }

    public static void turnOnWhiteLed() {
        writeValue(LED_W_EN, "1");
    }

    public static void turnOffWhiteLed() {
        writeValue(LED_W_EN, "0");
    }

    public static void turnOnRedLed() {
        writeValue(LED_R_EN, "1");
    }

    public static void turnLed(int colorCode, int status, String deviceCode) {
        String color = "";
        switch (deviceCode){
            case MachineName.RAKINDA_F3:
                if(colorCode == Constants.LED_WHITE){
                    color = LED_W_EN;
                    writeValue(color, "" + status);
                }else if (colorCode == Constants.LED_RED){
                    color = LED_R_EN;
                    writeValue(color, "" + status);
                }else if (colorCode == Constants.LED_GREEN) {
                    color = LED_G_EN;
                    writeValue(color, "" + status);
                }else{
                    writeValue( LED_W_EN, "" + status);
                    writeValue( LED_R_EN, "" + status);
                    writeValue( LED_G_EN, "" + status);
                }
                break;

            default:
                if(status == 1){
                    if (colorCode == Constants.LED_RED){
                        LedUtil.getInstance().setLedLight(LedUtil.RED, LedUtil.OPEN);
                    }else if (colorCode == Constants.LED_GREEN) {
                        LedUtil.getInstance().setLedLight(LedUtil.GREEN, LedUtil.OPEN);
                    }else if (colorCode == Constants.LED_YELLOW) {
                        LedUtil.getInstance().setLedLight(LedUtil.YELLOW, LedUtil.OPEN);
                    }else if (colorCode == Constants.LED_BLUE) {
                        LedUtil.getInstance().setLedLight(LedUtil.BLUE, LedUtil.OPEN);
                    }
                }else{
                    LedUtil.getInstance().setLedLight(LedUtil.RED, LedUtil.CLOSE);
                    LedUtil.getInstance().setLedLight(LedUtil.GREEN, LedUtil.CLOSE);
                    LedUtil.getInstance().setLedLight(LedUtil.YELLOW, LedUtil.CLOSE);
                    LedUtil.getInstance().setLedLight(LedUtil.BLUE, LedUtil.CLOSE);
                }
                break;
        }
    }

    public static void turnOffRedLed() {
        writeValue(LED_R_EN, "0");
    }

    public static void turnOffAll() {
        writeValue(LED_R_EN, "0");
    }

    private static void writeValue(String path, String value) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(value);
            bw.close();
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
    }

    String readValue(String path){
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            line = br.readLine();
        } catch (IOException e) {
            // ex.printStackTrace();
        }
        return  line;
    }


}

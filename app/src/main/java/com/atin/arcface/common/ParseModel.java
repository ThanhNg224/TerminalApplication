package com.atin.arcface.common;

import com.atin.arcface.model.AccessTimeSegDB;
import com.atin.arcface.model.AccessTimeSegDBConvert;
import com.atin.arcface.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

public class ParseModel {

    private static String fixTimeStr(String timeStr) {
        if (timeStr == null || timeStr.isEmpty() || timeStr.equals("null")) return "00:00:00";
        if (timeStr.length() == 5) return timeStr + ":00";
        return timeStr;
    }

    public static AccessTimeSegDBConvert parseToTimeSegModel(AccessTimeSegDB model){
        Date dtToDay = Calendar.getInstance().getTime();
        String strDay = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dtToDay);

        String startMon1 = strDay + " " + fixTimeStr(model.getMondayStart1());
        String startMon2 = strDay + " " + fixTimeStr(model.getMondayStart2());
        String startMon3 = strDay + " " + fixTimeStr(model.getMondayStart3());
        String startMon4 = strDay + " " + fixTimeStr(model.getMondayStart4());
        String endMon1 = strDay + " " + fixTimeStr(model.getMondayEnd1());
        String endMon2 = strDay + " " + fixTimeStr(model.getMondayEnd2());
        String endMon3 = strDay + " " + fixTimeStr(model.getMondayEnd3());
        String endMon4 = strDay + " " + fixTimeStr(model.getMondayEnd4());

        String startTue1 = strDay + " " + fixTimeStr(model.getTuesdayStart1());
        String startTue2 = strDay + " " + fixTimeStr(model.getTuesdayStart2());
        String startTue3 = strDay + " " + fixTimeStr(model.getTuesdayStart3());
        String startTue4 = strDay + " " + fixTimeStr(model.getTuesdayStart4());
        String endTue1 = strDay + " " + fixTimeStr(model.getTuesdayEnd1());
        String endTue2 = strDay + " " + fixTimeStr(model.getTuesdayEnd2());
        String endTue3 = strDay + " " + fixTimeStr(model.getTuesdayEnd3());
        String endTue4 = strDay + " " + fixTimeStr(model.getTuesdayEnd4());

        String startWed1 = strDay + " " + fixTimeStr(model.getWednesdayStart1());
        String startWed2 = strDay + " " + fixTimeStr(model.getWednesdayStart2());
        String startWed3 = strDay + " " + fixTimeStr(model.getWednesdayStart3());
        String startWed4 = strDay + " " + fixTimeStr(model.getWednesdayStart4());
        String endWed1 = strDay + " " + fixTimeStr(model.getWednesdayEnd1());
        String endWed2 = strDay + " " + fixTimeStr(model.getWednesdayEnd2());
        String endWed3 = strDay + " " + fixTimeStr(model.getWednesdayEnd3());
        String endWed4 = strDay + " " + fixTimeStr(model.getWednesdayEnd4());

        String startThu1 = strDay + " " + fixTimeStr(model.getThusdayStart1());
        String startThu2 = strDay + " " + fixTimeStr(model.getThusdayStart2());
        String startThu3 = strDay + " " + fixTimeStr(model.getThusdayStart3());
        String startThu4 = strDay + " " + fixTimeStr(model.getThusdayStart4());
        String endThu1 = strDay + " " + fixTimeStr(model.getThusdayEnd1());
        String endThu2 = strDay + " " + fixTimeStr(model.getThusdayEnd2());
        String endThu3 = strDay + " " + fixTimeStr(model.getThusdayEnd3());
        String endThu4 = strDay + " " + fixTimeStr(model.getThusdayEnd4());

        String startFri1 = strDay + " " + fixTimeStr(model.getFridayStart1());
        String startFri2 = strDay + " " + fixTimeStr(model.getFridayStart2());
        String startFri3 = strDay + " " + fixTimeStr(model.getFridayStart3());
        String startFri4 = strDay + " " + fixTimeStr(model.getFridayStart4());
        String endFri1 = strDay + " " + fixTimeStr(model.getFridayEnd1());
        String endFri2 = strDay + " " + fixTimeStr(model.getFridayEnd2());
        String endFri3 = strDay + " " + fixTimeStr(model.getFridayEnd3());
        String endFri4 = strDay + " " + fixTimeStr(model.getFridayEnd4());

        String startSat1 = strDay + " " + fixTimeStr(model.getSaturdayStart1());
        String startSat2 = strDay + " " + fixTimeStr(model.getSaturdayStart2());
        String startSat3 = strDay + " " + fixTimeStr(model.getSaturdayStart3());
        String startSat4 = strDay + " " + fixTimeStr(model.getSaturdayStart4());
        String endSat1 = strDay + " " + fixTimeStr(model.getSaturdayEnd1());
        String endSat2 = strDay + " " + fixTimeStr(model.getSaturdayEnd2());
        String endSat3 = strDay + " " + fixTimeStr(model.getSaturdayEnd3());
        String endSat4 = strDay + " " + fixTimeStr(model.getSaturdayEnd4());

        String startSun1 = strDay + " " + fixTimeStr(model.getSundayStart1());
        String startSun2 = strDay + " " + fixTimeStr(model.getSundayStart2());
        String startSun3 = strDay + " " + fixTimeStr(model.getSundayStart3());
        String startSun4 = strDay + " " + fixTimeStr(model.getSundayStart4());
        String endSun1 = strDay + " " + fixTimeStr(model.getSundayEnd1());
        String endSun2 = strDay + " " + fixTimeStr(model.getSundayEnd2());
        String endSun3 = strDay + " " + fixTimeStr(model.getSundayEnd3());
        String endSun4 = strDay + " " + fixTimeStr(model.getSundayEnd4());

        AccessTimeSegDBConvert parseModel = new AccessTimeSegDBConvert(
                StringUtils.convertStringToDate(startMon1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endMon1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startMon2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endMon2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startMon3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endMon3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startMon4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endMon4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startTue1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endTue1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startTue2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endTue2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startTue3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endTue3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startTue4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endTue4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startWed1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endWed1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startWed2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endWed2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startWed3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endWed3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startWed4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endWed4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startThu1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endThu1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startThu2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endThu2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startThu3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endThu3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startThu4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endThu4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startFri1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endFri1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startFri2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endFri2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startFri3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endFri3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startFri4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endFri4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startSat1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSat1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSat2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSat2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSat3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSat3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSat4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSat4, "dd/MM/yyyy HH:mm:ss"),

                StringUtils.convertStringToDate(startSun1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSun1, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSun2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSun2, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSun3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSun3, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(startSun4, "dd/MM/yyyy HH:mm:ss"),
                StringUtils.convertStringToDate(endSun4, "dd/MM/yyyy HH:mm:ss")
        );

        return parseModel;
    }
}
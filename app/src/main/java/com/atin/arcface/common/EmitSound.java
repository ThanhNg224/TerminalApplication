package com.atin.arcface.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.HardwareUtils;
import com.atin.arcface.util.LanguageUtils;

public class EmitSound {
    private static MediaPlayer mp;

    public static void openSound (Context context, String summaryCode, String detailCode) {
        switch(summaryCode){
            case ErrorCode.COMMON_HIGHT_TEMPERATURE:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.hight_temperature);
                }else{
                    playSoundMessage(context, R.raw.hight_temperature_en);
                }
                break;

            case ErrorCode.COMMON_NO_MASK:
                switch (detailCode){
                    case ErrorCode.CHECKIN_OUT_OF_SERVICE_TIME:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.CHECKIN_NOT_ACCESS:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.CHECKOUT_OUT_OF_SERVICE_TIME:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.CHECKOUT_NOT_ACCESS:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.TIMEKEEPING_OUT_OF_SERVICE_TIME:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.TIMEKEEPING_NOT_ACCESS:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.group_not_access);
                        }else{
                            playSoundMessage(context, R.raw.group_not_access_en);
                        }
                        break;

                    case ErrorCode.COMMON_CANTEEN_ACCESS_VALID:
                        playSoundMessage(context, R.raw.canteen_valid);
                        break;

                    default:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.nomask);
                        }else{
                            playSoundMessage(context, R.raw.nomask_en);
                        }
                        break;
                }
                break;

            case ErrorCode.COMMON_FACE_NOT_FOUND:
                switch (detailCode){
                    case ErrorCode.CHECKIN_FACE_NOT_FOUND:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.face_not_found);
                        }else{
                            playSoundMessage(context, R.raw.face_not_found_en);
                        }
                        break;

                    case ErrorCode.CHECKOUT_FACE_NOT_FOUND:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.face_not_found);
                        }else{
                            playSoundMessage(context, R.raw.face_not_found_en);
                        }
                        break;

                    case ErrorCode.TIMEKEEPING_FACE_NOT_FOUND:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.face_not_found);
                        }else{
                            playSoundMessage(context, R.raw.face_not_found_en);
                        }
                        break;

                    case ErrorCode.COMMON_REQUEST_PCCOVID_QRCODE:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.khaibaoyte);
                        }else{
                            playSoundMessage(context, R.raw.khaibaoyte);
                        }
                        break;

                    case ErrorCode.COMMON_REQUEST_CHECK_FACE:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.confirm_face);
                        }else{
                            playSoundMessage(context, R.raw.confirm_face_en);
                        }
                        break;
                }
                break;

            case ErrorCode.COMMON_NOT_ACCESS:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.group_not_access);
                }else{
                    playSoundMessage(context, R.raw.group_not_access_en);
                }
                break;

            case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.out_of_access_time);
                }else{
                    playSoundMessage(context, R.raw.out_of_access_time_en);
                }
                break;

            case ErrorCode.COMMON_ACCESS_VALID:
                switch (detailCode){
                    case ErrorCode.CHECKIN_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.checkin_success);
                        }else{
                            playSoundMessage(context, R.raw.checkin_success_en);
                        }
                        break;

                    case ErrorCode.CHECKOUT_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.checkout_success);
                        }else{
                            playSoundMessage(context, R.raw.checkout_success_en);
                        }
                        break;

                    case ErrorCode.TIMEKEEPING_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.timekeeping_success);
                        }else{
                            playSoundMessage(context, R.raw.timekeeping_success_en);
                        }
                        break;

                    case ErrorCode.CHECKIN_CARD_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.checkin_success);
                        }else{
                            playSoundMessage(context, R.raw.checkin_success_en);
                        }
                        break;

                    case ErrorCode.CHECKOUT_CARD_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.checkout_success);
                        }else{
                            playSoundMessage(context, R.raw.checkout_success_en);
                        }
                        break;

                    case ErrorCode.TIMEKEEPING_CARD_VALID:
                        if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                            playSoundMessage(context, R.raw.checkin_success);
                        }else{
                            playSoundMessage(context, R.raw.checkin_success_en);
                        }
                        break;
                }
                break;

            case ErrorCode.COMMON_BEEP_SOUND:
                playSoundMessage(context, R.raw.beep_01);
                break;

            case ErrorCode.COMMON_MACHINE_NOT_DEFINE:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.machine_not_registed);
                }else{
                    playSoundMessage(context, R.raw.machine_not_registed_en);
                }
                break;

            case ErrorCode.COMMON_CONFIG_PCCOVID_ERROR:
                playSoundMessage(context, R.raw.config_pccovid_error);
                break;

            case ErrorCode.COMMON_DETECT_QR_CODE:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.confirm_face);
                }else{
                    playSoundMessage(context, R.raw.confirm_face_en);
                }
                break;

            case ErrorCode.COMMON_EXPIRED:
                if(LanguageUtils.getCurrentLanguage().getCode().equals(Application.getInstance().getString(R.string.language_vietnamese_code))){
                    playSoundMessage(context, R.raw.access_time_expired);
                }else{
                    playSoundMessage(context, R.raw.access_time_expired_en);
                }
                break;

            case ErrorCode.COMMON_USED_UP_TURN_ACCESS:
                playSoundMessage(context, R.raw.beep_effect);
                break;

            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY:
                playSoundMessage(context, R.raw.beep_effect);
                break;

            case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH:
                playSoundMessage(context, R.raw.beep_effect);
                break;

            default:
                break;
        }
    }

    public static void openSoundSpecial (Context context, String soundCode) {
        switch (soundCode) {
            case ErrorCode.SPECIAL_CONFIRM_TWINS:
                playSoundMessageNoWait(context, R.raw.confirm_twins);
                break;

            case ErrorCode.TING_NOTIFICATION:
                playSoundMessageNoWait(context, R.raw.x0010_ting01);
                break;

            case ErrorCode.BUTTON_CLICK:
                playSoundMessageNoWait(context, R.raw.mouse_click);
                break;

            default:
                break;
        }
    }

    private static void playSoundMessage (Context context, int resouceId) {
        try {
            mp = MediaPlayer.create(context, resouceId);
            mp.start();

            mp.setOnCompletionListener(mp -> {
                mp.stop();
                mp.release();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void playSoundMessageNoWait (Context context, int resouceId) {
        try {
            mp = MediaPlayer.create(context, resouceId);
            mp.start();

            mp.setOnCompletionListener(mp -> {
                mp.stop();
                mp.release();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

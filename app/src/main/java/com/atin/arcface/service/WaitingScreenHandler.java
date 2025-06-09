package com.atin.arcface.service;

import android.app.Activity;
import android.app.Dialog;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingScreenHandler {

    private Activity activity;
    private Dialog dialog;
    private Timer mTimer;
    private TimerTask mTask;
    private Date mDtCurrentDate;

    public WaitingScreenHandler(Activity activity, Dialog dialog){
        this.activity = activity;
        this.dialog = dialog;
        init_Led();
    }

    public WaitingScreenHandler(Activity activity){
        this.activity = activity;
        init_Led();
    }

    public void execute() {
        if (mTask == null) {
            mTask = new TimerTask() {
                @Override
                public void run() {
//                    int ret = PosUtil.getPriximitySensorStatus();
//                    Message message = mHandler.obtainMessage(ret);
//                    mHandler.sendMessage(message);
                }
            };
        }
        if (mTimer == null) { mTimer = new Timer(); }

        mTimer.schedule(mTask, 0, 5*1000);
    }

    private void init_Led(){
        //Set độ sáng của led 50% (max 250)
//        String cmd = "echo " + 125 + " > /sys/class/backlight/led-brightness/brightness";
//        ShellUtils.execCommand(cmd, false);
    }

//    Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            final SharedPreferences pref = activity.getSharedPreferences("PREF", activity.getApplicationContext().MODE_PRIVATE);
//            final boolean mUseLed = pref.getBoolean(Constants.PREF_USE_LED, false);
//
//            mDtCurrentDate = Calendar.getInstance().getTime();
//            final String strCurrentDay = StringUtils.convertDateToString(mDtCurrentDate);
//            final Date dtFromDate = StringUtils.convertStringToDate(strCurrentDay + " 18:00:00", "dd/MM/yyyy hh:mm:ss");
//
//            final String strNextDay = StringUtils.plusDayToDate(strCurrentDay, 1);
//            final Date dtToDate = StringUtils.convertStringToDate(strNextDay + " 08:00:00","dd/MM/yyyy hh:mm:ss");
//
//            if(mUseLed){
//                if (msg.what == 1) {
//                    //Có người trong khu vực thiết bị
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            if(dtFromDate.before(mDtCurrentDate) && dtToDate.after(mDtCurrentDate)){
//                                PosUtil.setLedPower(Constants.LED_ON);
//                            }else{
//                                PosUtil.setLedPower(Constants.LED_OFF);
//                            }
//                            //dialog.hide();
//                        }
//                    });
//                }else if (msg.what == 0){
//                    //Không có người trong khu vực thiết bị
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            PosUtil.setLedPower(Constants.LED_OFF);
//                            //dialog.show();
//                        }
//                    });
//                }
//            }else{
//                PosUtil.setLedPower(Constants.LED_OFF);
//
////                if (msg.what == 1) {
////                    //Có người trong khu vực thiết bị
////                    activity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            dialog.hide();
////                        }
////                    });
////                }else if (msg.what == 0){
////                    //Không có người trong khu vực thiết bị
////                    activity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            dialog.show();
////                        }
////                    });
////                }
//            }
//        }
//    };

}

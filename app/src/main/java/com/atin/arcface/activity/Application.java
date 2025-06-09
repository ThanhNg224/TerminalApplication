package com.atin.arcface.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.util.BaseUtil;
import com.common.pos.api.util.ShellUtils;
import com.google.gson.Gson;

import java.util.List;

public class Application extends android.app.Application {

    private static Application instance;
    private Database database;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static Application getInstance() {
        return instance;
    }

    public boolean isAppOnForeground(Context context) {
        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses != null){
            String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    ret = true;
                }
            }
        }

        return ret;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = new Database(instance);
        database.getWritableDatabase();

        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);

        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityPaused(Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {

                    }

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        if(!isAppOnForeground(getApplicationContext())){
                            boolean autoStart = pref.getBoolean(Constants.PREF_AUTOMATIC_START, false);
                            if(autoStart){
                                Intent mStartActivity = new Intent(activity, RegisterAndRecognizeDualActivity.class);
                                int mPendingIntentId = 1;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, mPendingIntent);
                                System.exit(0);
                            }
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                    }
                });
    }

    public Database getDatabase(){
        return database;
    }
}
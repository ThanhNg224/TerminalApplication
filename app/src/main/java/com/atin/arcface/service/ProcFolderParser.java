package com.atin.arcface.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.atin.arcface.common.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.Locale;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * 1. All PIDs are obtained by parsing /proc folder
 * 2. Mem info of each process is get through ActivityManager.getProcessMemoryInfo( int[] pids).
 * 3. CPU info of each process is get through parsing /proc/[pid]/stat file, which is implemented in
 * AppProcess.java.
 */
public class ProcFolderParser extends IntentService {
    // Use activity manager to get mem info for each process
    private ActivityManager activityManager;

    // how many clock ticks in one second, this is used to calculated cpu usage of each process
    public static long samplePeriod = 1;
    public float generalCpuUsage = 0.0f;
    private ActivityManager.MemoryInfo generalMem = new ActivityManager.MemoryInfo();

    // total memory of the device, this value will also be used to calculate mem percentage of each proc by AppProcess.class
    public static long totalMem;
    public static NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
    private long totalTime = 1, busyTime = 1, totalTimeLast = 0, busyTimeLast = 0;
    private float temperature = 0;
    public static final String DELIMITER = "#";

    public ProcFolderParser() {
        super("ProcFolderParser");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(generalMem);

        // right shift by 10 bits, convert to KB, this is used by AppProcess.java
        this.totalMem = generalMem.totalMem >> 10;
        while (true) {
            try {
                calculateCpuUsageOfEachProc();
                cpuTemperature();

                StringBuilder generalInfo = new StringBuilder(String.format("%.2f", generalCpuUsage * 100) + DELIMITER);
                activityManager.getMemoryInfo(generalMem);
                generalInfo.append(String.format("%.2f", generalMem.availMem * 100 / (double) generalMem.totalMem) + DELIMITER + String.format("%.2f", temperature)); //%CPU#%RAM#°C
                String data = generalInfo.toString().replaceAll(",", ".");
                sendIntent(Constants.CPU_INFO, "cpuMem", data);

                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendIntent(String action, String extraKey, String extraValue) {
        Intent localIntent = new Intent(action);
        localIntent.putExtra(extraKey, extraValue);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * Sample the CPU-related file, and then calculate the difference between this time and last time.
     * How to calculate total CPU usage: https://github.com/Leo-G/DevopsWiki/wiki/How-Linux-CPU-Usage-Time-and-Percentage-is-calculated
     */
    private void calculateCpuUsageOfEachProc() {
        String[] fields;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("/proc/stat"));
            fields = reader.readLine().split("[ ]+");
            this.busyTime = Long.parseLong(fields[1]) + Long.parseLong(fields[2]) + Long.parseLong(fields[3]) + Long.parseLong(fields[6]) + Long.parseLong(fields[7]) + Long.parseLong(fields[8]);
            this.totalTime = this.busyTime + Long.parseLong(fields[4]) + Long.parseLong(fields[5]);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        this.samplePeriod = totalTime - totalTimeLast;
        this.generalCpuUsage = (busyTime - busyTimeLast) / (float) this.samplePeriod;
        this.totalTimeLast = this.totalTime;
        this.busyTimeLast = this.busyTime;
    }

    public void cpuTemperature()
    {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                temperature = temp / 1000.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

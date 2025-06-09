package com.atin.arcface.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.atin.arcface.R;

import java.io.IOException;

public class ClockAlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        String message = this.getIntent().getStringExtra("msg");
        int flag = this.getIntent().getIntExtra("flag", 0);
        showDialogInBroadcastReceiver(message, flag);
    }

    private void showDialogInBroadcastReceiver(String message, final int flag) {
        if (flag == 0) {
            try {
                Runtime.getRuntime().exec("su");
                Runtime.getRuntime().exec("reboot");
            } catch (IOException e) {
                Toast.makeText(this, "Thiết bị không cho phép", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


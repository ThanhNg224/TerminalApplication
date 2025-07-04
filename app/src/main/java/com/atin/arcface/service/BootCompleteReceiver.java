package com.atin.arcface.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;

public class BootCompleteReceiver extends BroadcastReceiver
{
    @Override public void onReceive(Context context, Intent intent)
    {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, RegisterAndRecognizeDualActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
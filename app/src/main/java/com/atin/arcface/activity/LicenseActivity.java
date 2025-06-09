package com.atin.arcface.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.atin.arcface.R;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
    }

    public void onActiveByKeyInput(View view){
        startActivity(new Intent(this, ActiveByInputKeyActivity.class));
    }
}

package com.atin.arcface.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.ProcessSynchronizeData;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.EventReportModel;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.util.DialogListener;
import com.atin.arcface.widget.ListEventAdapter;
import com.atin.arcface.widget.ListPersonAdapter;

import java.util.List;

public class EventActivity extends AppCompatActivity implements DialogListener {
    private Database database;
    private ListView list;
    private ListEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initDatabase();
        loadData();
    }


    private void initDatabase() {
        database = Application.getInstance().getDatabase();
    }

    private void loadData(){
        try{
            List<EventReportModel> lsEvent = database.getAllEventReport();
            adapter= new ListEventAdapter(this, lsEvent, database, this);
            list= findViewById(R.id.lvEvent);
            list.setAdapter(adapter);
        }catch (Exception ex){
            Log.e("EventActivity", ex.getMessage());
        }

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onResponse(Object object) {
        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        loadData();
    }
}
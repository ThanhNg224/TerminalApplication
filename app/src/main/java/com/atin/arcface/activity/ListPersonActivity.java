package com.atin.arcface.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.ProcessSynchronizeData;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.widget.ListPersonAdapter;
import java.util.List;

public class ListPersonActivity extends AppCompatActivity {
    private Database database;
    private ListView list;
    private TextView txtTotalImage;
    private EditText txtSearchInput;
    private ListPersonAdapter adapter;
    private ProcessSynchronizeData processSynchronizeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_person);
        initView();
        initDatabase();
        loadData();
    }

    private void initView(){
        txtTotalImage = findViewById(R.id.txtTotalImage);
        txtSearchInput = findViewById(R.id.txtSearchInput);
    }

    private void initDatabase() {
        processSynchronizeData = new ProcessSynchronizeData(this);
        database = Application.getInstance().getDatabase();
    }

    private void loadData(){
        try{
            String searchInput = txtSearchInput.getText().toString();
            List<FaceRegisterInfo> lsPerson = database.searchPerson(searchInput);
            adapter= new ListPersonAdapter(this,lsPerson);
            list= findViewById(R.id.lvPerson);
            list.setAdapter(adapter);
            txtTotalImage.setText("" + lsPerson.size());
        }catch (Exception ex){
            Log.e("ListPersonActivity", ex.getMessage());
        }

    }

    public void clearAllPerson(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(ListPersonActivity.this);
        alert.setTitle("Xóa dữ liệu nhân sự");
        alert.setMessage("Xác nhận xóa toàn bộ dữ liệu nhân sự trên thiết bị?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                processSynchronizeData.doClear(Constants.CLEAR_ALL);
                loadData();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void onSearch(View view){
        loadData();
    }
}
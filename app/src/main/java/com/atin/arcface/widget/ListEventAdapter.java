package com.atin.arcface.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.EventReportModel;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.DialogListener;
import com.atin.arcface.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ListEventAdapter extends ArrayAdapter<EventReportModel> {
    private final Activity context;
    private List<EventReportModel> lsData;
    private Database database;
    private DialogListener callback;

    public ListEventAdapter(Activity context, List<EventReportModel> lsData, Database database, DialogListener callback) {
        super (context, R.layout.item_event, lsData);
        this.context=context;
        this.lsData=lsData;
        this.database = database;
        this.callback = callback;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_event, null,true);

        TextView accessDate = (TextView) rowView.findViewById(R.id.accessDate);
        TextView totalRecord = (TextView) rowView.findViewById(R.id.totalRecord);
        TextView syncRecord = (TextView) rowView.findViewById(R.id.synchRecord);
        TextView waitRecord = (TextView) rowView.findViewById(R.id.waitRecord);
        Button btnResynch = (Button) rowView.findViewById(R.id.btnResynch);

        accessDate.setText("Ngày: " + lsData.get(position).getAccessDate());
        totalRecord.setText("Tổng số bản ghi: " + lsData.get(position).getTotalRecord());
        syncRecord.setText("Đã đồng bộ: " + lsData.get(position).getSyncRecord());
        waitRecord.setText("Chưa đồng bộ: " + lsData.get(position).getWaitRecord());

        // Đặt sự kiện click cho Button
        btnResynch.setOnClickListener(v -> {
            database.updateEventStatus(lsData.get(position).getAccessDate(), Constants.EVENT_STATUS_WAIT_SYNC);
            callback.onResponse(null);
        });

        return rowView;
    }
}
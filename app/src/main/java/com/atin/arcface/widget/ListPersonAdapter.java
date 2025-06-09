package com.atin.arcface.widget;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atin.arcface.R;
import com.atin.arcface.model.FaceRegisterInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ListPersonAdapter extends ArrayAdapter<FaceRegisterInfo> {
    private final Activity context;
    private List<FaceRegisterInfo> lsPerson;

    public ListPersonAdapter(Activity context, List<FaceRegisterInfo> lsPerson) {
        super (context, R.layout.item_person, lsPerson);

        this.context=context;
        this.lsPerson=lsPerson;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_person, null,true);

        TextView fullName = (TextView) rowView.findViewById(R.id.fullname);
        TextView personCode = (TextView) rowView.findViewById(R.id.personCode);
        TextView positions = (TextView) rowView.findViewById(R.id.position);
        TextView jobduties = (TextView) rowView.findViewById(R.id.jobduties);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        fullName.setText(lsPerson.get(position).getFullName());
        personCode.setText(lsPerson.get(position).getPersonCode());
        positions.setText(lsPerson.get(position).getPosition());
        jobduties.setText("" + lsPerson.get(position).getJobDuties());
        Glide.with(context)
                .asBitmap()
                .load(lsPerson.get(position).getFacePath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        return rowView;

    };
}
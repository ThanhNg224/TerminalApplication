package com.atin.arcface.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atin.arcface.R;
import com.atin.arcface.faceserver.CompareResult;
import com.atin.arcface.model.PersonDB;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ListTwinsAdapter extends ArrayAdapter<CompareResult> {
    private final Activity context;
    private List<CompareResult> lsData;

    public ListTwinsAdapter(Activity context, List<CompareResult> lsData) {
        super (context, R.layout.item_twin, lsData);
        this.context=context;
        this.lsData=lsData;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_twin, null,true);

        TextView fullName = (TextView) rowView.findViewById(R.id.fullName);
        TextView personCode = (TextView) rowView.findViewById(R.id.personCode);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        fullName.setText(lsData.get(position).getFullName());
        personCode.setText(lsData.get(position).getPersonCode());

        Glide.with(getContext())
                .load(lsData.get(position).getFacePath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        return rowView;

    };
}
package com.atin.arcface.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.atin.arcface.model.LivenessLevel;

import java.util.List;

public class LivenessLevelAdapter extends ArrayAdapter<LivenessLevel> {
    private List<LivenessLevel> lsData;

    // Your sent context
    public LivenessLevelAdapter(Context context, int textViewResourceId, List<LivenessLevel> lsData) {
        super(context, textViewResourceId, lsData);
        this.lsData = lsData;
    }

    @Override
    public int getCount() {
        return lsData.size();
    }

    @Override
    public LivenessLevel getItem(int position) {
        return lsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(@Nullable LivenessLevel item) {
        for (int i = 0; i < lsData.size(); i++) {
            if (lsData.get(i).getLevelValue() == item.getLevelValue()) {
                return i;
            }
        }
        return 0;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LivenessLevel item = getItem(position);

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(item.getLevelName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LivenessLevel item = getItem(position);
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(item.getLevelName());
        return label;
    }
}
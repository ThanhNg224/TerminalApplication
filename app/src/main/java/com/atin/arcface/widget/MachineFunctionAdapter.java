package com.atin.arcface.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.atin.arcface.model.MachineFunction;

import java.util.List;

public class MachineFunctionAdapter extends ArrayAdapter<MachineFunction> {
    private List<MachineFunction> lsData;

    // Your sent context
    public MachineFunctionAdapter(Context context, int textViewResourceId, List<MachineFunction> lsData) {
        super(context, textViewResourceId, lsData);
        this.lsData = lsData;
    }

    @Override
    public int getCount() {
        return lsData.size();
    }

    @Override
    public MachineFunction getItem(int position) {
        return lsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(@Nullable MachineFunction item) {
        for (int i = 0; i < lsData.size(); i++) {
            if (lsData.get(i).getFunctionValue() == item.getFunctionValue()) {
                return i;
            }
        }
        return 0;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MachineFunction item = getItem(position);

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(item.getFunctionName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        MachineFunction item = getItem(position);
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(item.getFunctionName());
        return label;
    }
}
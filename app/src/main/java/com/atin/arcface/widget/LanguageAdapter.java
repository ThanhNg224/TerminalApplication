package com.atin.arcface.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.atin.arcface.model.Language;
import java.util.List;

public class LanguageAdapter extends ArrayAdapter<Language> {
    private List<Language> lsLanguage;

    // Your sent context
    public LanguageAdapter(Context context, int textViewResourceId, List<Language> lsLanguage) {
        super(context, textViewResourceId, lsLanguage);
        this.lsLanguage = lsLanguage;
    }

    @Override
    public int getCount() {
        return lsLanguage.size();
    }

    @Override
    public Language getItem(int position) {
        return lsLanguage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(@Nullable Language item) {
        for (int i = 0; i < lsLanguage.size(); i++) {
            if (lsLanguage.get(i).getCode().equals(item.getCode())) {
                return i;
            }
        }
        return 0;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Language language = getItem(position);

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(language.getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Language language = getItem(position);
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(language.getName());
        return label;
    }
}
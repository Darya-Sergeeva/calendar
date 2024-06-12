package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ColorPickerAdapter extends BaseAdapter {

    private final Context context;
    private final int[] colors;

    public ColorPickerAdapter(Context context, int[] colors) {
        this.context = context;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.color_picker_item, parent, false);
        }

        View colorView = view.findViewById(R.id.colorView);
        colorView.setBackgroundColor(colors[position]);
        view.setTag(colors[position]);

        return view;
    }
}


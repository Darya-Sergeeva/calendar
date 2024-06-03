package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class SubtaskAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    private int layout;
    private List<String> subtasks;

    public SubtaskAdapter(Context context, int resource, List<String> subtasks) {
        super(context, resource, subtasks);
        this.subtasks = subtasks;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String subtask = subtasks.get(position);

        viewHolder.subtaskName.setText(subtask);

        return convertView;
    }

    private static class ViewHolder {
        final TextView subtaskName;
        final CheckBox subtaskDone;

        ViewHolder(View view) {
            subtaskName = view.findViewById(R.id.subtaskName);
            subtaskDone = view.findViewById(R.id.subtaskDone);
        }
    }
}



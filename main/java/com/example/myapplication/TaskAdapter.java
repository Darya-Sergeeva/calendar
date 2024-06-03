package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    private LayoutInflater inflater;
    private int layout;
    private List<Task> tasks;
    private DatabaseHelper dbHelper;

    public TaskAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new DatabaseHelper(context);
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

        Task task = tasks.get(position);

        viewHolder.taskButton.setText(task.getName());
        viewHolder.dateView.setText(task.getDate());
        viewHolder.checkBox.setChecked(task.isDone());

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            // Update task status in the database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_DONE, isChecked ? 1 : 0);
            db.update(DatabaseHelper.TABLE_TASKS, values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        });

        viewHolder.taskButton.setOnClickListener(v -> {
            // Handle task button click
            Intent intent = new Intent(getContext(), SubtaskActivity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskName", task.getName());
            getContext().startActivity(intent);
        });

        viewHolder.subtasksLayout.removeAllViews();
        for (String subtask : task.getSubtasks()) {
            View subtaskView = inflater.inflate(R.layout.subtask_item, null);
            TextView subtaskName = subtaskView.findViewById(R.id.subtaskName);
            CheckBox subtaskDone = subtaskView.findViewById(R.id.subtaskDone);

            subtaskName.setText(subtask);

            subtaskDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update subtask status in the database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_SUBTASK_NAME, subtask);
                db.update(DatabaseHelper.TABLE_SUBTASKS, values, DatabaseHelper.COLUMN_TASK_ID + " = ? AND " + DatabaseHelper.COLUMN_SUBTASK_NAME + " = ?", new String[]{String.valueOf(task.getId()), subtask});
            });

            viewHolder.subtasksLayout.addView(subtaskView);
        }

        return convertView;
    }

    private static class ViewHolder {
        final Button taskButton;
        final TextView dateView;
        final CheckBox checkBox;
        final LinearLayout subtasksLayout;

        ViewHolder(View view) {
            taskButton = view.findViewById(R.id.task_button);
            dateView = view.findViewById(R.id.task_date);
            checkBox = view.findViewById(R.id.task_done);
            subtasksLayout = view.findViewById(R.id.subtasks_layout);
        }
    }
}


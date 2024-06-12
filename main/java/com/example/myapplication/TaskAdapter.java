package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        Task task = taskList.get(position);

        CheckBox taskCheckbox = view.findViewById(R.id.taskCheckbox);
        TextView taskName = view.findViewById(R.id.taskName);
        ImageButton deleteTaskButton = view.findViewById(R.id.deleteTaskButton);

        taskName.setText(task.getName());
        taskCheckbox.setChecked(task.isDone());

        taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            updateTaskStatus(task);
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubtaskActivity.class);
            intent.putExtra("taskId", task.getId());
            context.startActivity(intent);
        });

        deleteTaskButton.setOnClickListener(v -> {
            deleteTask(task);
            taskList.remove(position);
            notifyDataSetChanged();
        });

        return view;
    }

    private void updateTaskStatus(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_DONE, task.isDone() ? 1 : 0);

        int rowsAffected = db.update(DatabaseHelper.TABLE_TASKS, values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        if (rowsAffected > 0) {
            Toast.makeText(context, "Статус задачи обновлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при обновлении статуса задачи", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_TASKS, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        if (rowsDeleted > 0) {
            Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при удалении задачи", Toast.LENGTH_SHORT).show();
        }
    }
}

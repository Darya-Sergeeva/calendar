package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
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

public class SubtaskAdapter extends BaseAdapter {
    private Context context;
    private List<Subtask> subtaskList;
    private DatabaseHelper dbHelper;

    public SubtaskAdapter(Context context, List<Subtask> subtaskList) {
        this.context = context;
        this.subtaskList = subtaskList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return subtaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return subtaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return subtaskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.subtask_item, parent, false);
        }

        Subtask subtask = subtaskList.get(position);

        CheckBox subtaskCheckbox = view.findViewById(R.id.subtaskCheckbox);
        TextView subtaskName = view.findViewById(R.id.subtaskName);
        ImageButton deleteSubtaskButton = view.findViewById(R.id.deleteSubtaskButton);

        subtaskName.setText(subtask.getName());
        subtaskCheckbox.setChecked(subtask.isDone());

        subtaskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subtask.setDone(isChecked);
            updateSubtaskStatus(subtask);
        });

        deleteSubtaskButton.setOnClickListener(v -> {
            deleteSubtask(subtask);
            subtaskList.remove(position);
            notifyDataSetChanged();
        });

        return view;
    }

    private void updateSubtaskStatus(Subtask subtask) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_DONE, subtask.isDone() ? 1 : 0);

        int rowsAffected = db.update(DatabaseHelper.TABLE_SUBTASKS, values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(subtask.getId())});
        if (rowsAffected > 0) {
            Toast.makeText(context, "Статус подзадачи обновлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при обновлении статуса подзадачи", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSubtask(Subtask subtask) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_SUBTASKS, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(subtask.getId())});
        if (rowsDeleted > 0) {
            Toast.makeText(context, "Подзадача удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при удалении подзадачи", Toast.LENGTH_SHORT).show();
        }
    }
}

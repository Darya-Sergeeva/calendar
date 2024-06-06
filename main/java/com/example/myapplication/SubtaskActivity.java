package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SubtaskActivity extends AppCompatActivity {

    private TextView taskNameView;
    private ListView subtaskListView;
    private Button addSubtaskButton;
    private DatabaseHelper dbHelper;
    private SubtaskAdapter adapter;
    private List<String> subtasks;
    private int taskId;
    private String taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask);

        taskNameView = findViewById(R.id.taskNameView);
        subtaskListView = findViewById(R.id.subtaskListView);
        addSubtaskButton = findViewById(R.id.addSubtaskButton);
        dbHelper = new DatabaseHelper(this);

        // Получение ID задачи и имени из Intent
        taskId = getIntent().getIntExtra("taskId", -1);
        taskName = getIntent().getStringExtra("taskName");

        taskNameView.setText(taskName);
        subtasks = new ArrayList<>();
        adapter = new SubtaskAdapter(this, R.layout.subtask_item, subtasks);
        subtaskListView.setAdapter(adapter);

        loadSubtasks();

        addSubtaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubtaskDialog();
            }
        });

        subtaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteSubtask(subtasks.get(position));
                return true;
            }
        });
    }

    private void loadSubtasks() {
        subtasks.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SUBTASKS,
                new String[]{DatabaseHelper.COLUMN_SUBTASK_NAME},
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null, null, null);

        while (cursor.moveToNext()) {
            String subtaskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBTASK_NAME));
            subtasks.add(subtaskName);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddSubtaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить подзадачу");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String subtaskName = input.getText().toString();
            if (!subtaskName.isEmpty()) {
                addSubtask(subtaskName);
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addSubtask(String subtaskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBTASK_NAME, subtaskName);
        values.put(DatabaseHelper.COLUMN_TASK_ID, taskId);

        long newRowId = db.insert(DatabaseHelper.TABLE_SUBTASKS, null, values);
        if (newRowId != -1) {
            subtasks.add(subtaskName);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Подзадача добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении подзадачи", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSubtask(String subtaskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_SUBTASKS,
                DatabaseHelper.COLUMN_TASK_ID + " = ? AND " + DatabaseHelper.COLUMN_SUBTASK_NAME + " = ?",
                new String[]{String.valueOf(taskId), subtaskName});

        subtasks.remove(subtaskName);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Подзадача удалена", Toast.LENGTH_SHORT).show();
    }
}

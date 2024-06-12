package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SubtaskActivity extends AppCompatActivity {
    private ListView subtaskListView;
    private DatabaseHelper dbHelper;
    private SubtaskAdapter subtaskAdapter;
    private List<Subtask> subtaskList;
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask);

        subtaskListView = findViewById(R.id.subtaskListView);
        dbHelper = new DatabaseHelper(this);
        taskId = getIntent().getLongExtra("taskId", -1);

        subtaskList = new ArrayList<>();
        subtaskAdapter = new SubtaskAdapter(this, subtaskList);
        subtaskListView.setAdapter(subtaskAdapter);

        loadSubtasksForTask(taskId);

        FloatingActionButton addSubtaskButton = findViewById(R.id.addSubtaskButton);
        addSubtaskButton.setOnClickListener(v -> showAddSubtaskDialog());
    }

    private void loadSubtasksForTask(long taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SUBTASKS, null, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, null, null, null);

        subtaskList.clear();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBTASK_NAME));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_DONE)) > 0;
            subtaskList.add(new Subtask(id, (int) taskId, name, isDone));
        }
        cursor.close();
        subtaskAdapter.notifyDataSetChanged();
    }

    private void showAddSubtaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить подзадачу");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_subtask, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            EditText subtaskNameInput = customLayout.findViewById(R.id.subtaskNameInput);
            String subtaskName = subtaskNameInput.getText().toString();
            if (!subtaskName.isEmpty()) {
                saveSubtask(subtaskName);
            } else {
                Toast.makeText(this, "Введите название подзадачи", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveSubtask(String subtaskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBTASK_NAME, subtaskName);
        values.put(DatabaseHelper.COLUMN_TASK_ID, taskId);
        values.put(DatabaseHelper.COLUMN_IS_DONE, 0);

        long newRowId = db.insert(DatabaseHelper.TABLE_SUBTASKS, null, values);
        if (newRowId != -1) {
            loadSubtasksForTask(taskId);
            Toast.makeText(this, "Подзадача добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении подзадачи", Toast.LENGTH_SHORT).show();
        }
    }
}

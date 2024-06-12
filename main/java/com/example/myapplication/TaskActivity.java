package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private ListView taskListView;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskListView = findViewById(R.id.taskListView);
        dbHelper = new DatabaseHelper(this);
        selectedDate = getIntent().getStringExtra("selectedDate");

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

        loadTasksForDate(selectedDate);

        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadTasksForDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TASKS, null, DatabaseHelper.COLUMN_TASK_DATE + " = ?", new String[]{date}, null, null, null);

        taskList.clear();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_NAME));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_DONE)) > 0;
            taskList.add(new Task(id, name, date, isDone));
        }
        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить задачу");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            EditText taskNameInput = customLayout.findViewById(R.id.taskNameInput);
            String taskName = taskNameInput.getText().toString();
            if (!taskName.isEmpty()) {
                saveTask(taskName);
            } else {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveTask(String taskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName);
        values.put(DatabaseHelper.COLUMN_TASK_DATE, selectedDate);
        values.put(DatabaseHelper.COLUMN_IS_DONE, 0);

        long newRowId = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        if (newRowId != -1) {
            loadTasksForDate(selectedDate);
            MainActivity.updateCalendar(); // Обновление календаря
            Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении задачи", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для перехода к SubtaskActivity
    private void navigateToSubtaskActivity(long taskId) {
        Intent intent = new Intent(TaskActivity.this, SubtaskActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }

    // Адаптер для TaskActivity
    private class TaskAdapter extends BaseAdapter {
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

            view.setOnClickListener(v -> navigateToSubtaskActivity(task.getId()));

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
                MainActivity.updateCalendar(); // Обновление календаря
            } else {
                Toast.makeText(context, "Ошибка при обновлении статуса задачи", Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteTask(Task task) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_TASKS, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
            if (rowsDeleted > 0) {
                Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show();
                MainActivity.updateCalendar(); // Обновление календаря
            } else {
                Toast.makeText(context, "Ошибка при удалении задачи", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView taskListView;
    private FloatingActionButton addTaskButton;
    private DatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private String selectedDate;
    private Set<String> datesWithTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        taskListView = findViewById(R.id.taskListView);
        addTaskButton = findViewById(R.id.addTaskButton);
        dbHelper = new DatabaseHelper(this);

        tasks = new ArrayList<>();
        datesWithTasks = new HashSet<>();
        adapter = new TaskAdapter(this, R.layout.task_item, tasks);
        taskListView.setAdapter(adapter);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadTasksForSelectedDate();
        loadDatesWithTasks();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                loadTasksForSelectedDate();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = tasks.get(position);
                Intent intent = new Intent(MainActivity.this, SubtaskActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("taskName", task.getName());
                startActivity(intent);
            }
        });

        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = tasks.get(position);
                deleteTask(task);
                return true;
            }
        });
    }

    private void loadTasksForSelectedDate() {
        tasks.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TASKS,
                null,
                DatabaseHelper.COLUMN_TASK_DATE + " = ?",
                new String[]{selectedDate},
                null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_NAME));
            String taskDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DATE));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_DONE)) == 1;

            Task task = new Task(id, taskName, taskDate, isDone);
            loadSubtasksForTask(task);
            tasks.add(task);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void loadSubtasksForTask(Task task) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SUBTASKS,
                new String[]{DatabaseHelper.COLUMN_SUBTASK_NAME},
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())},
                null, null, null);

        while (cursor.moveToNext()) {
            String subtaskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBTASK_NAME));
            task.addSubtask(subtaskName);
        }
        cursor.close();
    }

    private void loadDatesWithTasks() {
        datesWithTasks.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(true, DatabaseHelper.TABLE_TASKS,
                new String[]{DatabaseHelper.COLUMN_TASK_DATE},
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DATE));
            datesWithTasks.add(date);
        }
        cursor.close();
        highlightDatesWithTasks();
    }

    private void highlightDatesWithTasks() {
        // TODO: Implement highlighting logic for dates with tasks in the CalendarView
        // This may involve custom rendering or using a third-party library.
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить задачу");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String taskName = input.getText().toString();
            if (!taskName.isEmpty()) {
                addTask(taskName);
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addTask(String taskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName);
        values.put(DatabaseHelper.COLUMN_TASK_DATE, selectedDate);
        values.put(DatabaseHelper.COLUMN_IS_DONE, 0);

        long newRowId = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        if (newRowId != -1) {
            Task task = new Task((int) newRowId, taskName, selectedDate, false);
            tasks.add(task);
            adapter.notifyDataSetChanged();
            datesWithTasks.add(selectedDate);
            highlightDatesWithTasks();
            Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении задачи", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_TASKS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.delete(DatabaseHelper.TABLE_SUBTASKS,
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});

        tasks.remove(task);
        adapter.notifyDataSetChanged();
        loadDatesWithTasks();
        Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
    }
}

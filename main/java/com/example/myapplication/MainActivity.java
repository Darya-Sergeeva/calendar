package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView taskRecyclerView;
    private Button addTaskButton;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskRepository taskRepository;

    private SimpleDateFormat dateFormat;
    private String selectedDate;
    private Set<String> taskDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        addTaskButton = findViewById(R.id.addTaskButton);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(new Date(calendarView.getDate()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                openTaskDetail(task.getId());
            }

            @Override
            public void onTaskDeleteClick(Task task) {
                deleteTask(task);
            }
        });

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(taskAdapter);

        taskRepository = new TaskRepository(this);

        loadTasks();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dateFormat.format(new Date(year - 1900, month, dayOfMonth));
                loadTasksForSelectedDate();
            }
        });

        addTaskButton.setOnClickListener(v -> addTask());
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(taskRepository.getAllTasks());
        taskAdapter.notifyDataSetChanged();

        taskDates = new HashSet<>();
        for (Task task : taskList) {
            taskDates.add(task.getDate());
        }

        calendarView.invalidate();
    }

    private void loadTasksForSelectedDate() {
        taskList.clear();
        List<Task> allTasks = taskRepository.getAllTasks();
        for (Task task : allTasks) {
            if (task.getDate().equals(selectedDate)) {
                taskList.add(task);
            }
        }
        taskAdapter.notifyDataSetChanged();
    }

    private void addTask() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Add Task");
        builder.setPositiveButton("Add", (dialog, which) -> {
            EditText taskTitleInput = dialogView.findViewById(R.id.taskTitleInput);
            String taskTitle = taskTitleInput.getText().toString();
            if (!taskTitle.isEmpty()) {
                long newTaskId = taskRepository.addTask(taskTitle, selectedDate);
                if (newTaskId != -1) {
                    taskList.add(new Task(newTaskId, taskTitle, selectedDate));
                    taskAdapter.notifyDataSetChanged();
                    taskDates.add(selectedDate);
                    calendarView.invalidate();
                } else {
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteTask(Task task) {
        int deletedRows = taskRepository.deleteTask(task.getId());
        if (deletedRows > 0) {
            taskList.remove(task);
            taskAdapter.notifyDataSetChanged();
            taskDates.remove(task.getDate());
            for (Task t : taskList) {
                if (t.getDate().equals(task.getDate())) {
                    taskDates.add(t.getDate());
                    break;
                }
            }
            calendarView.invalidate();
        } else {
            Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show();
        }
    }

    private void openTaskDetail(long taskId) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }
}

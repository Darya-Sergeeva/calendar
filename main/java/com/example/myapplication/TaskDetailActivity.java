package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText taskTitleInput;
    private RecyclerView subtaskRecyclerView;
    private SubtaskAdapter subtaskAdapter;
    private Task task;
    private TaskRepository taskRepository;

    private Button addSubtaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskTitleInput = findViewById(R.id.taskTitleInput);
        subtaskRecyclerView = findViewById(R.id.subtaskRecyclerView);
        addSubtaskButton = findViewById(R.id.addSubtaskButton);

        taskRepository = new TaskRepository(this);
        long taskId = getIntent().getLongExtra("taskId", -1);

        addSubtaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ваш код для обработки нажатия на кнопку добавления подзадачи
            }
        });

        if (taskId != -1) {
            task = taskRepository.getTask(taskId);
            if (task != null) {
                taskTitleInput.setText(task.getTitle());
                loadSubtasks(taskId);
            }
        }
    }

    private void loadSubtasks(long taskId) {
        List<Subtask> subtasks = taskRepository.getSubtasksForTask(taskId);
        subtaskAdapter = new SubtaskAdapter(subtasks, new SubtaskAdapter.OnSubtaskClickListener() {
            @Override
            public void onSubtaskDeleteClick(Subtask subtask) {
                deleteSubtask(subtask);
            }
        });

        subtaskRecyclerView.setLayoutManager(new LinearLayoutManager(TaskDetailActivity.this));
        subtaskRecyclerView.setAdapter(subtaskAdapter);
    }

    private void deleteSubtask(Subtask subtask) {
        int deletedRows = taskRepository.deleteSubtask(subtask.getId());
        if (deletedRows > 0) {
            task.getSubtasks().remove(subtask);
            subtaskAdapter.notifyDataSetChanged();
            Toast.makeText(TaskDetailActivity.this, "Subtask deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TaskDetailActivity.this, "Error deleting subtask", Toast.LENGTH_SHORT).show();
        }
    }
}



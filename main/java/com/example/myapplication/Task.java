package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String name;
    private String date;
    private boolean isDone;
    private List<String> subtasks;

    public Task(int id, String name, String date, boolean isDone) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isDone = isDone;
        this.subtasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public List<String> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(String subtask) {
        subtasks.add(subtask);
    }
}

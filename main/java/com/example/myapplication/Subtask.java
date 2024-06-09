package com.example.myapplication;

public class Subtask {
    private long id;
    private String title;
    private long taskId;

    public Subtask(String title, long taskId) {
        this.title = title;
        this.taskId = taskId;
    }

    public Subtask(long id, String title, long taskId) {
        this.id = id;
        this.title = title;
        this.taskId = taskId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public long getTaskId() {
        return taskId;
    }
}




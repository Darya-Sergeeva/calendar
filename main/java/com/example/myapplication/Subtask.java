package com.example.myapplication;

public class Subtask {
    private long id;
    private int taskId;
    private String name;
    private boolean isDone;

    public Subtask(long id, int taskId, String name, boolean isDone) {
        this.id = id;
        this.taskId = taskId;
        this.name = name;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}

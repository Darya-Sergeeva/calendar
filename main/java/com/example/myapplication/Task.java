package com.example.myapplication;

public class Task {
    private long id;
    private String name;
    private String date;
    private boolean isDone;

    public Task(long id, String name, String date, boolean isDone) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isDone = isDone;
    }

    public long getId() {
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
}


package com.example.myapplication;

public class Task {
    private long id;
    private String title;
    private String date;

    public Task(long id, String title, String date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}



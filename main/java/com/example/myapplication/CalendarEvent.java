package com.example.myapplication;

public class CalendarEvent {
    private String name;
    private int color;
    private String date;

    public CalendarEvent(String name, int color, String date) {
        this.name = name;
        this.color = color;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }
}

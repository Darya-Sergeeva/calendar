package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CalendarView;

import java.util.Set;

public class CustomCalendarView extends CalendarView {

    private Set<String> datesWithTasks;

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDatesWithTasks(Set<String> datesWithTasks) {
        this.datesWithTasks = datesWithTasks;
        invalidate(); // Перерисовать календарь
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        // Логика для выделения дат с задачами
        for (String date : datesWithTasks) {
            // Пример выделения даты с задачами
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7)) - 1;
            int day = Integer.parseInt(date.substring(8, 10));
            // Нарисовать цветной кружок или квадрат вокруг даты
        }
    }
}


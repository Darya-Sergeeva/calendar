package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CalendarView;

public class CustomCalendarView extends CalendarView {
    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWeekSeparatorLineColor(getResources().getColor(R.color.orange));
        setFocusedMonthDateColor(getResources().getColor(R.color.orangeDark));
        setUnfocusedMonthDateColor(getResources().getColor(R.color.orangeLight));
        setWeekDayTextAppearance(R.style.CalendarWeekDayTextAppearance);
        setDateTextAppearance(R.style.CalendarDateTextAppearance);
    }
}



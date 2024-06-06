package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<String> days;
    private Set<String> datesWithTasks;
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, List<String> days, Set<String> datesWithTasks) {
        this.days = days;
        this.datesWithTasks = datesWithTasks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayText.setText(day);

        if (datesWithTasks.contains(day)) {
            holder.dayText.setBackgroundResource(R.drawable.custom_calendar_date_background);
        } else {
            holder.dayText.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.calendarDayText);
        }
    }
}


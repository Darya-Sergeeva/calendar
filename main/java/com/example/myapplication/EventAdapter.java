package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private List<CalendarEvent> events;
    private OnEventDeleteListener deleteListener;

    public EventAdapter(Context context, List<CalendarEvent> events, OnEventDeleteListener deleteListener) {
        this.context = context;
        this.events = events;
        this.deleteListener = deleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CalendarEvent event = events.get(position);
        holder.eventName.setText(event.getName());
        holder.eventColor.setBackgroundColor(event.getColor());
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(event.getName()));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnEventDeleteListener {
        void onDelete(String eventName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        View eventColor;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventColor = itemView.findViewById(R.id.eventColor);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

package com.example.myapplication;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder> {

    private List<Subtask> subtaskList;
    private OnSubtaskClickListener listener;

    public interface OnSubtaskClickListener {
        void onSubtaskDeleteClick(Subtask subtask);
    }

    public SubtaskAdapter(List<Subtask> subtaskList, OnSubtaskClickListener listener) {
        this.subtaskList = subtaskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask, parent, false);
        return new SubtaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskViewHolder holder, int position) {
        Subtask subtask = subtaskList.get(position);
        holder.subtaskTitle.setText(subtask.getTitle());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSubtaskDeleteClick(subtask);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subtaskList.size();
    }

    public static class SubtaskViewHolder extends RecyclerView.ViewHolder {
        TextView subtaskTitle;
        Button deleteButton;

        public SubtaskViewHolder(@NonNull View itemView) {
            super(itemView);
            subtaskTitle = itemView.findViewById(R.id.subtaskTitle);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

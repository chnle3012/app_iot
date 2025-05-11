package com.example.btl_iot.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.HistoryResponse;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryResponse.History> historyList;
    private final HistoryItemListener listener;

    public interface HistoryItemListener {
        void onHistoryItemClick(HistoryResponse.History history);
        void onViewDetailsClick(HistoryResponse.History history);
        void onDeleteClick(HistoryResponse.History history);
        void onOptionsClick(HistoryResponse.History history, View view);
    }

    public HistoryAdapter(List<HistoryResponse.History> historyList, HistoryItemListener listener) {
        this.historyList = historyList != null ? historyList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<HistoryResponse.History> newHistoryList) {
        this.historyList = newHistoryList != null ? newHistoryList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryResponse.History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView name;
        TextView historyId;
        TextView mode;
        ImageButton optionsButton;
        Button viewDetailsButton;
        Button deleteButton;
        HistoryItemListener itemListener;

        public HistoryViewHolder(@NonNull View itemView, HistoryItemListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.history_item_title);
            name = itemView.findViewById(R.id.history_item_name);
            historyId = itemView.findViewById(R.id.history_item_id);
            mode = itemView.findViewById(R.id.history_item_mode);
            optionsButton = itemView.findViewById(R.id.history_item_options);
            viewDetailsButton = itemView.findViewById(R.id.btn_view_details);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            this.itemListener = listener;
        }

        public void bind(HistoryResponse.History history) {
            // Format timestamp to be more readable if needed
            title.setText(history.getTimestamp());
            
            // Set person name if available
            name.setText(history.getPeople() != null ? history.getPeople().getName() : "Unknown");
            
            // Set history ID
            historyId.setText("ID: " + history.getHistoryId());
            
            // Set mode
            mode.setText("Mode: " + history.getMode());

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.onHistoryItemClick(history);
                }
            });

            optionsButton.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.onOptionsClick(history, v);
                }
            });

            viewDetailsButton.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.onViewDetailsClick(history);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.onDeleteClick(history);
                }
            });
        }
    }
}
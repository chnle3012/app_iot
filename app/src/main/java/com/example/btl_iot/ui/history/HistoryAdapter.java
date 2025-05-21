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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        android.util.Log.d("HistoryAdapter", "Adapter created with initial list size: " + this.historyList.size());
    }

    public void updateData(List<HistoryResponse.History> newHistoryList) {
        android.util.Log.d("HistoryAdapter", "updateData called with list size: " + 
            (newHistoryList != null ? newHistoryList.size() : "null"));
        this.historyList = newHistoryList != null ? newHistoryList : new ArrayList<>();
        notifyDataSetChanged();
        android.util.Log.d("HistoryAdapter", "notifyDataSetChanged called");
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.util.Log.d("HistoryAdapter", "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        android.util.Log.d("HistoryAdapter", "onBindViewHolder called for position: " + position);
        HistoryResponse.History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        int count = historyList.size();
        android.util.Log.d("HistoryAdapter", "getItemCount: " + count);
        return count;
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
            android.util.Log.d("HistoryAdapter", "Binding history item: " + history.getHistoryId());
            
            // Format timestamp to be more readable
            String formattedTimestamp = formatTimestamp(history.getTimestamp());
            title.setText(formattedTimestamp);
            
            // Set person name if available
            String personName = history.getPeople() != null ? history.getPeople().getName() : "Unknown";
            name.setText(personName);
            
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
            
            android.util.Log.d("HistoryAdapter", "Binding complete for history item: " + history.getHistoryId());
        }
        
        private String formatTimestamp(String timestamp) {
            if (timestamp == null || timestamp.isEmpty()) {
                return "Unknown time";
            }
            
            try {
                // Parse the original timestamp format (assuming it's in yyyy-MM-dd HH-mm-ss format)
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
                // Create the correct output format (HH:mm:ss)
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                
                Date date = originalFormat.parse(timestamp);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                android.util.Log.e("HistoryAdapter", "Error parsing timestamp: " + e.getMessage());
                // If parsing fails, return the original timestamp
            }
            
            return timestamp;
        }
    }
}
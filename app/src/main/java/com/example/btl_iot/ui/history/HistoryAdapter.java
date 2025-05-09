package com.example.btl_iot.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.HistoryResponse;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryResponse.History> historyList;

    public HistoryAdapter(List<HistoryResponse.History> historyList) {
        this.historyList = historyList;
    }

    public void updateData(List<HistoryResponse.History> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryResponse.History history = historyList.get(position);
        holder.title.setText(history.getTimestamp());
        holder.name.setText(history.getPeople() != null ? history.getPeople().getName() : "Unknown");
        holder.historyId.setText("ID: " + history.getHistoryId());
        holder.mode.setText("Mode: " + history.getMode());
    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView name;
        TextView historyId;
        TextView mode;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.history_item_title);
            name = itemView.findViewById(R.id.history_item_name);
            historyId = itemView.findViewById(R.id.history_item_id);
            mode = itemView.findViewById(R.id.history_item_mode);
        }
    }
}
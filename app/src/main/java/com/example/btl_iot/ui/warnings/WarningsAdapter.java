package com.example.btl_iot.ui.warnings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.WarningResponse;

import java.util.List;

public class WarningsAdapter extends RecyclerView.Adapter<WarningsAdapter.WarningViewHolder> {

    private List<WarningResponse.Warning> warningList;
    private final WarningItemListener listener;

    public WarningsAdapter(List<WarningResponse.Warning> warningList, WarningItemListener listener) {
        this.warningList = warningList;
        this.listener = listener;
    }

    public void updateData(List<WarningResponse.Warning> newWarningList) {
        this.warningList = newWarningList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        WarningResponse.Warning warning = warningList.get(position);
        holder.message.setText("Info: " + warning.getInfo());
        holder.timestamp.setText(warning.getTimestamp());
        holder.id.setText("ID: " + warning.getId());

        holder.itemView.setOnClickListener(v -> listener.onWarningItemClicked(warning));
    }

    @Override
    public int getItemCount() {
        return warningList == null ? 0 : warningList.size();
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView timestamp;
        TextView id;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.warning_item_message);
            timestamp = itemView.findViewById(R.id.warning_item_timestamp);
            id = itemView.findViewById(R.id.warning_item_id);
        }
    }

    public interface WarningItemListener {
        void onWarningItemClicked(WarningResponse.Warning warning);
    }
}
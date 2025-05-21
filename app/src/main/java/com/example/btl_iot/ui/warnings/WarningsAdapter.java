package com.example.btl_iot.ui.warnings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.WarningResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        
        // Set nội dung cảnh báo
        holder.message.setText("Info: " + warning.getInfo());
        
        // Set ID cảnh báo
        holder.id.setText("ID: " + warning.getWarningId());
        
        // Set thời gian với định dạng đúng
        holder.timestamp.setText(formatTimestamp(warning.getTimestamp()));

        // Set sự kiện click cho item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWarningItemClick(warning);
            }
        });
        
        // Set sự kiện click cho nút options
        holder.options.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOptionsClick(warning, v);
            }
        });
        
        // Set sự kiện click cho nút xem chi tiết
        holder.btnViewDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(warning);
            }
        });
        
        // Set sự kiện click cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(warning);
            }
        });
    }

    @Override
    public int getItemCount() {
        return warningList == null ? 0 : warningList.size();
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
            android.util.Log.e("WarningsAdapter", "Error parsing timestamp: " + e.getMessage());
            // If parsing fails, return the original timestamp
        }
        
        return timestamp;
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView message;
        TextView id;
        TextView timestamp;
        Button btnViewDetail;
        Button btnDelete;
        ImageButton options;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.warning_item_icon);
            message = itemView.findViewById(R.id.warning_item_message);
            id = itemView.findViewById(R.id.warning_item_id);
            timestamp = itemView.findViewById(R.id.warning_item_timestamp);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            options = itemView.findViewById(R.id.warning_item_options);
        }
    }

    public interface WarningItemListener {
        void onWarningItemClick(WarningResponse.Warning warning);
        void onViewDetailsClick(WarningResponse.Warning warning);
        void onDeleteClick(WarningResponse.Warning warning);
        void onOptionsClick(WarningResponse.Warning warning, View view);
    }
}
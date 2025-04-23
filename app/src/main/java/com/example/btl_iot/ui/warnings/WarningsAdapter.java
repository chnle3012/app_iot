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

    // Constructor để nhận vào danh sách cảnh báo
    public WarningsAdapter(List<WarningResponse.Warning> warningList) {
        this.warningList = warningList;
    }

    // Phương thức để cập nhật dữ liệu mới
    public void updateData(List<WarningResponse.Warning> newWarningList) {
        this.warningList = newWarningList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_warning để tạo ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        // Lấy cảnh báo tại vị trí hiện tại trong danh sách
        WarningResponse.Warning warning = warningList.get(position);

        // Gán dữ liệu vào các TextView trong item
        holder.message.setText("Infor: " + warning.getMessage());
        holder.timestamp.setText(warning.getTimestamp());
        holder.id.setText("ID: " + warning.getId());
    }

    @Override
    public int getItemCount() {
        return warningList == null ? 0 : warningList.size();
    }

    // ViewHolder để quản lý các thành phần trong item_warning
    static class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView timestamp;
        TextView id;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các TextView từ layout item_warning
            message = itemView.findViewById(R.id.warning_item_message);
            timestamp = itemView.findViewById(R.id.warning_item_timestamp);
            id = itemView.findViewById(R.id.warning_item_id);
        }
    }
}

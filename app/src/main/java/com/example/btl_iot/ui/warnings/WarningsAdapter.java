package com.example.btl_iot.ui.warnings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.Warning;
import java.util.List;

public class WarningsAdapter extends RecyclerView.Adapter<WarningsAdapter.WarningViewHolder> {

    private final List<Warning> warningList;

    public WarningsAdapter(List<Warning> warningList) {
        this.warningList = warningList;
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        Warning warning = warningList.get(position);
        holder.textViewWarning.setText(warning.getMessage());
    }

    @Override
    public int getItemCount() {
        return warningList.size();
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWarning;

        WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWarning = itemView.findViewById(R.id.text_warning);
        }
    }
}

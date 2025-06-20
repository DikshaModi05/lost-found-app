package com.example.lostandfoundapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<HistoryItem> historyList;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        holder.textType.setText(item.getItemType());
        holder.textName.setText(item.getItemName());
        holder.textDate.setText(item.getItemDate() != null ? item.getItemDate() : "N/A");
        holder.textStatus.setText(item.getStatus() != null ? item.getStatus() : "Unknown");

        // Optional: Color status badge based on value
        String status = item.getStatus();
        if (status != null) {
            switch (status.toLowerCase()) {
                case "missing":
                case "unclaimed":
                    holder.textStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_red_dark));
                    break;
                case "found":
                case "returned":
                    holder.textStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_green_dark));
                    break;
                default:
                    holder.textStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));
                    break;
            }
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_upload)
                .into(holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("report_item", item);
            intent.putExtra("position", holder.getAdapterPosition());

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textType, textName, textDate, textStatus;
        ImageView itemImage;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textType);
            textName = itemView.findViewById(R.id.textName);
            textDate = itemView.findViewById(R.id.textDate);
            itemImage = itemView.findViewById(R.id.itemImage);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}

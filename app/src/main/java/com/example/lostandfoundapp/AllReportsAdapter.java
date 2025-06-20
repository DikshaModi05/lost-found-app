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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AllReportsAdapter extends RecyclerView.Adapter<AllReportsAdapter.ViewHolder> {

    private Context context;
    private List<HistoryItem> reportList;

    public AllReportsAdapter(Context context, List<HistoryItem> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public AllReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllReportsAdapter.ViewHolder holder, int position) {
        HistoryItem item = reportList.get(position);

        holder.textItemName.setText(item.getItemName());
        holder.textItemType.setText(item.getItemType());
        holder.textItemDate.setText("Date: " + item.getItemDate());

        // Click handler
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("report_item", item); // ✅ Corrected key
            intent.putExtra("position", holder.getAdapterPosition());
            ((Activity) context).startActivityForResult(intent, 1001);
        });

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.imageItem);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    // Optional: Update status when coming back from detail activity
    public void updateItemStatus(HistoryItem updatedItem, int position) {
        if (position >= 0 && position < reportList.size()) {
            reportList.set(position, updatedItem);
            notifyItemChanged(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;
        TextView textItemName, textItemType, textItemDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.image_item);
            textItemName = itemView.findViewById(R.id.text_item_name);
            textItemType = itemView.findViewById(R.id.text_item_type);
            textItemDate = itemView.findViewById(R.id.text_item_date);
        }
    }
}

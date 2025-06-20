package com.example.lostandfoundapp;

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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<ItemModel> itemList;

    public SearchAdapter(Context context, List<ItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = itemList.get(position);

        holder.textType.setText(item.getType());
        holder.textName.setText(item.getTitle());
        holder.textDate.setText(item.getDate() != null ? item.getDate() : "N/A");

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_upload)
                .into(holder.itemImage);

        // 🔄 Convert ItemModel to HistoryItem
        holder.itemView.setOnClickListener(v -> {
            HistoryItem historyItem = new HistoryItem();
            historyItem.setId(item.getId()); // if available
            historyItem.setItemType(item.getType());
            historyItem.setItemName(item.getTitle());
            historyItem.setItemDate(item.getDate());
            historyItem.setImageUrl(item.getImageUrl());
            historyItem.setLocation(item.getLocation());
            historyItem.setReportedBy(item.getUserEmail()); // reporter's email
            historyItem.setDescription(item.getDescription());
            historyItem.setUserId(item.getUserId()); // optional: Firebase UID
            historyItem.setStatus(item.getStatus()); // optional

            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("report_item", historyItem);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textType, textName, textDate;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.itemType);
            textName = itemView.findViewById(R.id.itemTitle);
            textDate = itemView.findViewById(R.id.itemDate);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}

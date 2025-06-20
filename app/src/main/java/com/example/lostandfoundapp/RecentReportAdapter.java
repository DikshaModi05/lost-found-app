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

public class RecentReportAdapter extends RecyclerView.Adapter<RecentReportAdapter.RecentViewHolder> {

    private Context context;
    private List<HistoryItem> recentList;

    public RecentReportAdapter(Context context, List<HistoryItem> recentList) {
        this.context = context;
        this.recentList = recentList;
    }

    public void updateList(List<HistoryItem> newList) {
        recentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_report, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        HistoryItem item = recentList.get(position);
        holder.textType.setText(item.getItemType());
        holder.textName.setText(item.getItemName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("report_item", recentList.get(position));
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.rounded_background_purple) // Make sure you have a default image in `res/drawable`
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textType, textName;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            textType = itemView.findViewById(R.id.textViewType);
            textName = itemView.findViewById(R.id.textViewName);
        }
    }
}

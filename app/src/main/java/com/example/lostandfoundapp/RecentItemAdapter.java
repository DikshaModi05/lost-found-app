package com.example.lostandfoundapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecentItemAdapter extends RecyclerView.Adapter<RecentItemAdapter.ViewHolder> {

    private Context context;
    private List<HistoryItem> itemList;

    public RecentItemAdapter(Context context, List<HistoryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentItemAdapter.ViewHolder holder, int position) {
        HistoryItem item = itemList.get(position);

        holder.textViewName.setText(item.getItemName());
        holder.textViewDate.setText(item.getItemDate());
        holder.textViewType.setText(item.getItemType()); // ✅ Add this line

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_upload)
                .into(holder.imageViewItem);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewItem;
        TextView textViewName, textViewDate, textViewType; // ✅ Add this line

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.itemDate);
            textViewType = itemView.findViewById(R.id.textViewType); // ✅ Add this
        }
    }

}

package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.*;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewRecent;
    private RecentReportAdapter recentReportAdapter;
    private List<HistoryItem> recentList;
    private ImageView iconViewAll;
    private LinearLayout commonItemsLayout;
    private static final String TAG = "HomeFragment";

    private final String[] commonItems = {
            "Personal Belongings", "Stationary", "Utility", "Accessories", "Others"
    };

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Important for toolbar menu
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView: HomeFragment view inflated");

        // Setup RecyclerView
        recyclerViewRecent = view.findViewById(R.id.recyclerView_recent);
        iconViewAll = view.findViewById(R.id.icon_view_all);
        iconViewAll.setVisibility(View.GONE);
        commonItemsLayout = view.findViewById(R.id.commonItemsLayout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecent.setLayoutManager(layoutManager);

        recentList = new ArrayList<>();
        recentReportAdapter = new RecentReportAdapter(getContext(), recentList);
        recyclerViewRecent.setAdapter(recentReportAdapter);

        // Load data
        loadRecentReportsFromFirebase();
        addCommonItemButtons();

        // View all icon toggle
        recyclerViewRecent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                iconViewAll.setVisibility(dx > 0 ? View.VISIBLE : View.GONE);
            }
        });

        iconViewAll.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AllReportsActivity.class));
        });

        return view;
    }

    // Inflate top toolbar menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear(); // 👈 This clears previous items and prevents duplicates
        inflater.inflate(R.menu.opt_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    // Handle toolbar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(getContext(), SearchActivity.class));
            return true;
        } else if (id == R.id.action_notifications) {
            startActivity(new Intent(getContext(), NotificationsActivity.class)); // replace with your activity
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(getContext(), ProfileActivity.class)); // replace with your activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadRecentReportsFromFirebase() {
        recentList.clear();

        DatabaseReference lostRef = FirebaseDatabase.getInstance().getReference("LostItems");
        DatabaseReference foundRef = FirebaseDatabase.getInstance().getReference("FoundItems");

        List<HistoryItem> tempList = new ArrayList<>();

        lostRef.orderByChild("timestamp").limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                            HistoryItem item = itemSnap.getValue(HistoryItem.class);
                            if (item != null) {
                                tempList.add(item);
                            }
                        }

                        foundRef.orderByChild("timestamp").limitToLast(10)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                                            HistoryItem item = itemSnap.getValue(HistoryItem.class);
                                            if (item != null) {
                                                tempList.add(item);
                                            }
                                        }

                                        // Sort & show latest 4
                                        Collections.sort(tempList, (i1, i2) ->
                                                Long.compare(i2.getTimestamp(), i1.getTimestamp()));

                                        recentList.clear();
                                        recentList.addAll(tempList.subList(0, Math.min(4, tempList.size())));
                                        recentReportAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "FoundItems load error: " + error.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "LostItems load error: " + error.getMessage());
                    }
                });
    }

    private void addCommonItemButtons() {
        int[] icons = {
                R.drawable.belongings,
                R.drawable.stationary,
                R.drawable.lunch,
                R.drawable.accessory,
                R.drawable.others
        };

        for (int i = 0; i < commonItems.length; i++) {
            View buttonView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_common_button, commonItemsLayout, false);

            TextView textView = buttonView.findViewById(R.id.buttonText);
            ImageView imageView = buttonView.findViewById(R.id.iconImage);

            textView.setText(commonItems[i]);
            imageView.setImageResource(icons[i]);

            int finalI = i;
            buttonView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AllReportsActivity.class);
                intent.putExtra("filter_by", commonItems[finalI]);
                startActivity(intent);
            });

            commonItemsLayout.addView(buttonView);
        }
    }
}

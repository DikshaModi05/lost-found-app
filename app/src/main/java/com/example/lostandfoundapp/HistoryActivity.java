package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner filterSpinner;
    private HistoryAdapter adapter;
    private List<HistoryItem> itemList;
    private List<HistoryItem> fullItemList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView_history);
        filterSpinner = findViewById(R.id.filterSpinner);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        fullItemList = new ArrayList<>();
        adapter = new HistoryAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                applyFilter(selected);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadItems();
    }

    private void loadItems() {
        itemList.clear();
        fullItemList.clear();

        DatabaseReference lostRef = FirebaseDatabase.getInstance().getReference("LostItems");
        DatabaseReference foundRef = FirebaseDatabase.getInstance().getReference("FoundItems");

        // Fetch LostItems
        lostRef.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot lostSnapshot) {
                        for (DataSnapshot dataSnapshot : lostSnapshot.getChildren()) {
                            HistoryItem item = dataSnapshot.getValue(HistoryItem.class);
                            if (item != null) fullItemList.add(item);
                        }

                        // Fetch FoundItems
                        foundRef.orderByChild("userId").equalTo(currentUserId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot foundSnapshot) {
                                        for (DataSnapshot dataSnapshot : foundSnapshot.getChildren()) {
                                            HistoryItem item = dataSnapshot.getValue(HistoryItem.class);
                                            if (item != null) fullItemList.add(item);
                                        }

                                        // Sort by timestamp (latest first)
                                        Collections.sort(fullItemList, new Comparator<HistoryItem>() {
                                            @Override
                                            public int compare(HistoryItem o1, HistoryItem o2) {
                                                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                                            }
                                        });

                                        applyFilter(filterSpinner.getSelectedItem().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(HistoryActivity.this, "Failed to load Found items", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(HistoryActivity.this, "Failed to load Lost items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            HistoryItem updatedItem = (HistoryItem) data.getSerializableExtra("updated_item");
            int position = data.getIntExtra("position", -1);

            if (position != -1 && updatedItem != null && position < itemList.size()) {
                itemList.set(position, updatedItem);         // Update displayed list
                adapter.notifyItemChanged(position);         // Notify change visually

                // Also update full list for future filters
                for (int i = 0; i < fullItemList.size(); i++) {
                    if (fullItemList.get(i).getId().equals(updatedItem.getId())) {
                        fullItemList.set(i, updatedItem);
                        break;
                    }
                }
            }
        }
    }

    private void applyFilter(String type) {
        itemList.clear();
        for (HistoryItem item : fullItemList) {
            if (type.equals("All") || item.getItemType().equalsIgnoreCase(type)) {
                itemList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

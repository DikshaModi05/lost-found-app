package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllReportsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AllReportsAdapter adapter;
    private List<HistoryItem> allReportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        recyclerView = findViewById(R.id.recyclerView_all_reports);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        allReportsList = new ArrayList<>();
        adapter = new AllReportsAdapter(this, allReportsList);
        recyclerView.setAdapter(adapter);

        loadAllReports();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            HistoryItem updatedItem = (HistoryItem) data.getSerializableExtra("updated_item");
            int position = data.getIntExtra("position", -1);

            if (updatedItem != null && position != -1) {
                adapter.updateItemStatus(updatedItem, position);
            }
        }
    }


    private void loadAllReports() {
        DatabaseReference lostRef = FirebaseDatabase.getInstance().getReference("LostItems");
        DatabaseReference foundRef = FirebaseDatabase.getInstance().getReference("FoundItems");

        List<HistoryItem> tempList = new ArrayList<>();

        lostRef.orderByChild("timestamp").limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    HistoryItem item = snap.getValue(HistoryItem.class);
                    if (item != null) tempList.add(item);
                }

                foundRef.orderByChild("timestamp").limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            HistoryItem item = snap.getValue(HistoryItem.class);
                            if (item != null) tempList.add(item);
                        }

                        // Sort by timestamp descending
                        Collections.sort(tempList, (i1, i2) -> Long.compare(i2.getTimestamp(), i1.getTimestamp()));

                        allReportsList.clear();
                        allReportsList.addAll(tempList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}

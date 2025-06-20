package com.example.lostandfoundapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    RecyclerView resultRecyclerView;
    List<ItemModel> resultList = new ArrayList<>();
    SearchAdapter searchAdapter;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        resultRecyclerView.setLayoutManager(gridLayoutManager);

        searchAdapter = new SearchAdapter(this, resultList);
        resultRecyclerView.setAdapter(searchAdapter);

        String query = getIntent().getStringExtra("query");
        dbRef = FirebaseDatabase.getInstance().getReference();

        loadResults(query);
    }

    void loadResults(String query) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultList.clear();

                // LostItems loop
                for (DataSnapshot snap : snapshot.child("LostItems").getChildren()) {
                    ItemModel item = snap.getValue(ItemModel.class);
                    if (item != null) {
                        if (snap.hasChild("itemDate")) {
                            item.setDate(snap.child("itemDate").getValue(String.class));
                        }
                        if (item.getTitle() != null &&
                                item.getTitle().equalsIgnoreCase(query)) {
                            resultList.add(item);
                        }
                    }
                }

                // FoundItems loop
                for (DataSnapshot snap : snapshot.child("FoundItems").getChildren()) {
                    ItemModel item = snap.getValue(ItemModel.class);
                    if (item != null) {
                        if (snap.hasChild("itemDate")) {
                            item.setDate(snap.child("itemDate").getValue(String.class));
                        }
                        if (item.getTitle() != null &&
                                item.getTitle().equalsIgnoreCase(query)) {
                            resultList.add(item);
                        }
                    }
                }

                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchResultsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.*;

import java.util.*;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ListView suggestionList;

    Set<String> suggestionSet = new HashSet<>();
    List<String> suggestionListData = new ArrayList<>();
    ArrayAdapter<String> suggestionAdapter;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        suggestionList = findViewById(R.id.suggestionList);

        // Automatically open keyboard
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestionListData);
        suggestionList.setAdapter(suggestionAdapter);

        dbRef = FirebaseDatabase.getInstance().getReference();
        loadItemsFromFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                suggestionList.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    suggestionList.setVisibility(View.GONE);
                } else {
                    List<String> matched = new ArrayList<>();
                    for (String s : suggestionSet) {
                        if (s.toLowerCase().startsWith(newText.toLowerCase())) {
                            matched.add(s);
                        }
                    }

                    if (!matched.isEmpty()) {
                        suggestionAdapter = new ArrayAdapter<>(SearchActivity.this,
                                android.R.layout.simple_list_item_1, matched);
                        suggestionList.setAdapter(suggestionAdapter);
                        suggestionList.setVisibility(View.VISIBLE);
                    } else {
                        suggestionList.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });

        suggestionList.setOnItemClickListener((parent, view, position, id) -> {
            String selected = suggestionAdapter.getItem(position);
            if (selected != null) {
                searchView.setQuery(selected, true); // optional: update text in search bar
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                intent.putExtra("query", selected);
                startActivity(intent);
            }
        });
    }

    private void loadItemsFromFirebase() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suggestionSet.clear();

                for (DataSnapshot snap : snapshot.child("LostItems").getChildren()) {
                    ItemModel item = snap.getValue(ItemModel.class);
                    if (item != null && item.getTitle() != null) {
                        suggestionSet.add(item.getTitle());
                    }
                }

                for (DataSnapshot snap : snapshot.child("FoundItems").getChildren()) {
                    ItemModel item = snap.getValue(ItemModel.class);
                    if (item != null && item.getTitle() != null) {
                        suggestionSet.add(item.getTitle());
                    }
                }

                suggestionListData.clear();
                suggestionListData.addAll(suggestionSet);
                suggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

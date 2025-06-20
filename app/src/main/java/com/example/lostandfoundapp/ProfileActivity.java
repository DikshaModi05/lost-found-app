package com.example.lostandfoundapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    TextView textName, textEmail, textLostCount, textFoundCount, textReturnedCount, textImpact;
    LinearLayout recentActivityContainer;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textLostCount = findViewById(R.id.textLostCount);
        textFoundCount = findViewById(R.id.textFoundCount);
        textReturnedCount = findViewById(R.id.textReturnedCount);
        textImpact = findViewById(R.id.textImpact);
        recentActivityContainer = findViewById(R.id.recentActivityContainer);

        // Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            textName.setText(user.getDisplayName());
            textEmail.setText(user.getEmail());
            fetchAllData(user.getUid());
        }
    }

    private void fetchAllData(String userId) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int lostCount = 0, foundCount = 0, returnedCount = 0;
                List<DataSnapshot> allItems = new ArrayList<>();

                // Lost Items
                DataSnapshot lostItemsSnapshot = snapshot.child("LostItems");
                for (DataSnapshot snap : lostItemsSnapshot.getChildren()) {
                    if (userId.equals(snap.child("userId").getValue(String.class))) {
                        lostCount++;
                        // ❌ Do not count returned here
                        allItems.add(snap);
                    }
                }

                // Found Items
                DataSnapshot foundItemsSnapshot = snapshot.child("FoundItems");
                for (DataSnapshot snap : foundItemsSnapshot.getChildren()) {
                    if (userId.equals(snap.child("userId").getValue(String.class))) {
                        foundCount++;

                        // ✅ Count returned only from FoundItems marked as Returned
                        String status = snap.child("status").getValue(String.class);
                        if ("Returned".equalsIgnoreCase(status)) {
                            returnedCount++;
                        }

                        allItems.add(snap);
                    }
                }

                // Update UI
                textLostCount.setText("Lost: " + lostCount);
                textFoundCount.setText("Found: " + foundCount);
                textReturnedCount.setText("Returned: " + returnedCount);
                textImpact.setText("🎉 You’ve helped " + returnedCount + " people get their items back!");

                // Show recent
                showRecentActivity(allItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecentActivity(List<DataSnapshot> allItems) {
        // Sort by timestamp desc
        Collections.sort(allItems, (a, b) -> {
            Long t1 = a.child("timestamp").getValue(Long.class);
            Long t2 = b.child("timestamp").getValue(Long.class);
            return Long.compare(t2 != null ? t2 : 0, t1 != null ? t1 : 0);
        });

        recentActivityContainer.removeAllViews();
        for (int i = 0; i < Math.min(3, allItems.size()); i++) {
            DataSnapshot snap = allItems.get(i);
            String title = snap.child("itemName").getValue(String.class);
            String time = snap.child("itemDate").getValue(String.class);

            // Identify source (Lost or Found)
            String parentPath = snap.getRef().getParent().getKey();
            String type = "LostItems".equals(parentPath) ? "Lost" : "Found";

            TextView text = new TextView(this);
            text.setText("📌 " + type + ": " + title + " (" + time + ")");
            text.setTextSize(14);
            try {
                text.setTextColor(Color.parseColor("#555555"));
            } catch (IllegalArgumentException e) {
                text.setTextColor(Color.DKGRAY); // fallback
            }
            text.setPadding(0, 4, 0, 4);
            recentActivityContainer.addView(text);
        }
    }
}

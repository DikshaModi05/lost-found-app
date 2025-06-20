package com.example.lostandfoundapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ReportDetailActivity extends AppCompatActivity {

    ImageView imageViewDetail;
    TextView textType, textName, textDate, textLocation, textReporter, textDescription;
    Button buttonContactReporter, buttonStatus;

    private String itemKey;
    private String currentUserUid;
    private String itemReporterUid;
    private String itemStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        imageViewDetail = findViewById(R.id.imageViewDetail);
        textType = findViewById(R.id.textViewDetailType);
        textName = findViewById(R.id.textViewDetailName);
        textDate = findViewById(R.id.textViewDetailDate);
        textLocation = findViewById(R.id.textViewDetailLocation);
        textReporter = findViewById(R.id.textViewDetailReporter);
        textDescription = findViewById(R.id.textViewDetailDescription);
        buttonContactReporter = findViewById(R.id.buttonContactReporter);
        buttonStatus = findViewById(R.id.buttonStatus);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HistoryItem item = (HistoryItem) getIntent().getSerializableExtra("report_item");

        if (item != null) {
            itemKey = item.getId();
            itemReporterUid = item.getUserId();
            itemStatus = item.getStatus();

            // Set default status if needed
            if (itemStatus == null || itemStatus.trim().isEmpty()) {
                itemStatus = "Lost".equalsIgnoreCase(item.getItemType()) ? "Missing" : "Unclaimed";
                item.setStatus(itemStatus);
                // Save to correct node
                FirebaseDatabase.getInstance().getReference(item.getItemType().equals("Lost") ? "LostItems" : "FoundItems")
                        .child(itemKey)
                        .child("status")
                        .setValue(itemStatus);
            }

            // Load views
            Glide.with(this).load(item.getImageUrl()).into(imageViewDetail);
            textType.setText("Type: " + item.getItemType());
            textName.setText("Name: " + item.getItemName());
            textDate.setText("Date: " + item.getItemDate());
            textLocation.setText("Location: " + item.getLocation());
            textReporter.setText("Reported By: " + item.getReportedBy());
            textDescription.setText("Description: " + item.getDescription());

            // Reporter button logic
            if (currentUserUid.equals(itemReporterUid)) {
                buttonContactReporter.setEnabled(false);
                buttonContactReporter.setAlpha(0.6f);
                buttonContactReporter.setText("This is your item");
            } else {
                buttonContactReporter.setOnClickListener(v -> {
                    String reporterEmail = item.getReportedBy();
                    if (reporterEmail != null && reporterEmail.contains("@")) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + reporterEmail));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Your Lost/Found Item");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nI saw your report on the Lost and Found app. Let's connect regarding this item.");
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                        } catch (Exception e) {
                            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Show button color/status
            updateStatusButton();

            // Status change handler
            buttonStatus.setOnClickListener(v -> {
                if (itemStatus == null) {
                    Toast.makeText(this, "Item status is unknown", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!currentUserUid.equals(itemReporterUid)) {
                    Toast.makeText(this, "Only the reporter can change status", Toast.LENGTH_SHORT).show();
                    return;
                }

                String itemType = item.getItemType();
                String firebaseNode = itemType.equals("Lost") ? "LostItems" : "FoundItems";
                String newStatus = "";

                if ("Lost".equalsIgnoreCase(itemType)) {
                    newStatus = "Missing".equals(itemStatus) ? "Found" : "Missing";
                } else if ("Found".equalsIgnoreCase(itemType)) {
                    newStatus = "Unclaimed".equals(itemStatus) ? "Returned" : "Unclaimed";
                }

                final String finalNewStatus = newStatus;

                new AlertDialog.Builder(this)
                        .setTitle("Update Status")
                        .setMessage("Do you want to mark this item as \"" + finalNewStatus + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // 🔥 Update Firebase at correct node
                            FirebaseDatabase.getInstance()
                                    .getReference(firebaseNode)
                                    .child(itemKey)
                                    .child("status")
                                    .setValue(finalNewStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        itemStatus = finalNewStatus;
                                        item.setStatus(finalNewStatus);
                                        updateStatusButton();
                                        Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();

                                        // ✅ Send result back
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("updated_item", item);
                                        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update Firebase", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        } else {
            Toast.makeText(this, "No item data found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStatusButton() {
        if (itemStatus == null) {
            buttonStatus.setText("Status: Unknown");
            buttonStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
            buttonStatus.setEnabled(false);
            return;
        }

        buttonStatus.setText("Status: " + itemStatus);

        if ("Missing".equals(itemStatus) || "Unclaimed".equals(itemStatus)) {
            buttonStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        } else {
            buttonStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        }

        if (!currentUserUid.equals(itemReporterUid)) {
            buttonStatus.setText("Status: " + itemStatus + " 🔒");
            buttonStatus.setEnabled(false);
            buttonStatus.setAlpha(0.6f);
        } else {
            buttonStatus.setEnabled(true);
            buttonStatus.setAlpha(1.0f);
        }
    }
}

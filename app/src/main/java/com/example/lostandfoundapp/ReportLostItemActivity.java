package com.example.lostandfoundapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReportLostItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageViewPreview;
    private Button uploadBtn, btnSubmit;

    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;

    private TextInputEditText editItemName, editItemDescription, editItemLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_items);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("LostItems");
        databaseRef = FirebaseDatabase.getInstance().getReference("LostItems");

        uploadBtn = findViewById(R.id.btnUploadImage);
        btnSubmit = findViewById(R.id.btnSubmitLostItem);
        imageViewPreview = findViewById(R.id.imagePreview);

        editItemName = findViewById(R.id.editItemName);
        editItemDescription = findViewById(R.id.editItemDescription);
        editItemLocation = findViewById(R.id.editItemLocation);

        uploadBtn.setOnClickListener(v -> openFileChooser());

        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                if (imageUri != null) {
                    uploadDataToFirebase(imageUri);
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewPreview.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        return !editItemName.getText().toString().trim().isEmpty() &&
                !editItemDescription.getText().toString().trim().isEmpty() &&
                !editItemLocation.getText().toString().trim().isEmpty();
    }

    private void uploadDataToFirebase(Uri imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user == null) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "User not logged in. Please login again.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return;
                    }

                    String reporterEmail = user.getEmail();
                    String userId = user.getUid();

                    String imageUrl = uri.toString();
                    String itemId = databaseRef.push().getKey();
                    String itemName = editItemName.getText().toString().trim();
                    String itemDesc = editItemDescription.getText().toString().trim();
                    String location = editItemLocation.getText().toString().trim();
                    String dateString = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
                    long timestamp = System.currentTimeMillis();

                    HistoryItem item = new HistoryItem(
                            "Lost",            // itemType
                            itemName,
                            dateString,        // itemDate
                            imageUrl,
                            timestamp,
                            itemDesc,
                            reporterEmail,
                            location,
                            "Missing",         // Default status
                            userId,
                            itemId             // ✅ Required!
                    );


                    databaseRef.child(itemId).setValue(item)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Lost item reported successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}

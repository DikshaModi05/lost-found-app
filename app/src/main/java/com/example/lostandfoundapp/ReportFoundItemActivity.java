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

public class ReportFoundItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imageUri;
    private ImageView imageFoundPreview;
    private Button btnUploadFoundImage, btnSubmitFoundItem;

    private TextInputEditText editFoundItemName, editFoundItemDescription, editFoundItemLocation;

    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_items);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("FoundItems");
        databaseRef = FirebaseDatabase.getInstance().getReference("FoundItems");

        imageFoundPreview = findViewById(R.id.imageFoundPreview);
        btnUploadFoundImage = findViewById(R.id.btnUploadFoundImage);
        btnSubmitFoundItem = findViewById(R.id.btnSubmitFoundItem);

        editFoundItemName = findViewById(R.id.editFoundItemName);
        editFoundItemDescription = findViewById(R.id.editFoundItemDescription);
        editFoundItemLocation = findViewById(R.id.editFoundItemLocation);

        btnUploadFoundImage.setOnClickListener(v -> openFileChooser());
        btnSubmitFoundItem.setOnClickListener(v -> {
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
            imageFoundPreview.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        return !editFoundItemName.getText().toString().trim().isEmpty() &&
                !editFoundItemDescription.getText().toString().trim().isEmpty() &&
                !editFoundItemLocation.getText().toString().trim().isEmpty();
    }

    private void uploadDataToFirebase(Uri imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // ✅ Always get the current user right before using
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user == null) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "User not logged in. Please login again.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return;
                    }

                    // ✅ Now safe to access
                    String reporterEmail = user.getEmail(); // Will NOT be null
                    String userId = user.getUid();

                    String imageUrl = uri.toString();
                    String itemId = databaseRef.push().getKey();
                    String itemName = editFoundItemName.getText().toString().trim();
                    String itemDesc = editFoundItemDescription.getText().toString().trim();
                    String location = editFoundItemLocation.getText().toString().trim();
                    String dateString = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

                    HashMap<String, Object> foundItem = new HashMap<>();
                    foundItem.put("id", itemId);
                    foundItem.put("itemName", itemName);
                    foundItem.put("description", itemDesc);
                    foundItem.put("location", location);
                    foundItem.put("imageUrl", imageUrl);
                    foundItem.put("timestamp", System.currentTimeMillis());
                    foundItem.put("itemDate", dateString);
                    foundItem.put("userId", userId);
                    foundItem.put("reportedBy", reporterEmail); // ✅ This will now be visible
                    foundItem.put("itemType", "Found");

                    databaseRef.child(itemId).setValue(foundItem)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Found item reported successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Failed to save item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

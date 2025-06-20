package com.example.lostandfoundapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddLostItemActivity extends AppCompatActivity {

    private TextInputEditText editUserName, editItemName, editItemDescription, editItemLocation;
    private Button btnUploadImage;
    private MaterialButton btnSubmitLostItem;
    private ImageView imagePreview;
    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_items);

        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference("LostItems");
        databaseReference = FirebaseDatabase.getInstance().getReference("LostItems");

        // UI Elements
//        editUserName = findViewById(R.id.editUserName);
        editItemName = findViewById(R.id.editItemName);
        editItemDescription = findViewById(R.id.editItemDescription);
        editItemLocation = findViewById(R.id.editItemLocation);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmitLostItem = findViewById(R.id.btnSubmitLostItem);
        imagePreview = findViewById(R.id.imagePreview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Upload Image Button
        btnUploadImage.setOnClickListener(v -> openFileChooser());

        // Submit Lost Item Button
        btnSubmitLostItem.setOnClickListener(v -> uploadLostItem());
    }

    // Open File Picker
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imagePreview);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    // Get File Extension
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Upload Lost Item Data
    private void uploadLostItem() {
        String userName = editUserName.getText().toString().trim();
        String itemName = editItemName.getText().toString().trim();
        String itemDescription = editItemDescription.getText().toString().trim();
        String itemLocation = editItemLocation.getText().toString().trim();

        if (userName.isEmpty() || itemName.isEmpty() || itemDescription.isEmpty() || itemLocation.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveItemToDatabase(userName, itemName, itemDescription, itemLocation, uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddLostItemActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            // No image selected, save without image
            saveItemToDatabase(userName, itemName, itemDescription, itemLocation, null);
        }
    }

    // Save data to Firebase Realtime Database
    private void saveItemToDatabase(String userName, String itemName, String itemDescription, String itemLocation, String imageUrl) {
        String uploadId = databaseReference.push().getKey();
        HashMap<String, Object> lostItem = new HashMap<>();
        lostItem.put("userName", userName);
        lostItem.put("itemName", itemName);
        lostItem.put("itemDescription", itemDescription);
        lostItem.put("itemLocation", itemLocation);
        if (imageUrl != null) {
            lostItem.put("imageUrl", imageUrl);
        }

        if (uploadId != null) {
            databaseReference.child(uploadId).setValue(lostItem).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AddLostItemActivity.this, "Item reported successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddLostItemActivity.this, "Failed to save item. Try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

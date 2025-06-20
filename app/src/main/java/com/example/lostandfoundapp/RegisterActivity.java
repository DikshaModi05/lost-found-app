package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    EditText edFullName, edEmailId, edPassword, edConfirmPW;
    Button buttonReg;
    ProgressBar progressBar;
    TextView textNewUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        edFullName = findViewById(R.id.editTextFullName);
        edEmailId = findViewById(R.id.editTextEmailId);
        edPassword = findViewById(R.id.editTextPassword);
        edConfirmPW = findViewById(R.id.editTextConfirmPW);
        buttonReg = findViewById(R.id.buttonReg);
        progressBar = findViewById(R.id.progressBar);
        textNewUser = findViewById(R.id.textNewUser);
        mAuth = FirebaseAuth.getInstance();

        // Hide progress bar initially
        progressBar.setVisibility(View.GONE);

        // Adjust insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register button click event
        buttonReg.setOnClickListener(view -> registerUser());

        // "Already a user?" button navigates to LoginActivity
        textNewUser.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String fullName = edFullName.getText().toString().trim();
        String email = edEmailId.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        String confirmPassword = edConfirmPW.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            edFullName.setError("Full Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edEmailId.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmailId.setError("Enter a valid email");
            return;
        }

        if (!email.endsWith("@student.mes.ac.in")) {
            edEmailId.setError("Only MES Email ID allowed!");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            edPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            edConfirmPW.setError("Passwords do not match");
            return;
        }

        // Show progress bar when registration starts
        progressBar.setVisibility(View.VISIBLE);

        // Firebase Registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // ✅ Update user's name in Firebase Authentication
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    // ✅ Send email verification
                                    user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Registration successful! Please verify your email.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace(); // ✅ Print error details in Logcat
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed! Unknown error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

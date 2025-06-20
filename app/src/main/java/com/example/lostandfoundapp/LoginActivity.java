package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView edLoginEmail;
    EditText edLoginPW;
    TextView edLoginForgotPW, edLoginNotRegistered;
    Button btnLogin;
    CheckBox checkboxShowPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edLoginEmail = findViewById(R.id.editTextLoginEmailAddress);
        edLoginPW = findViewById(R.id.editTextLoginPassword);
        edLoginNotRegistered = findViewById(R.id.textViewNotRegistered);
        edLoginForgotPW = findViewById(R.id.textViewForgotPW);
        btnLogin = findViewById(R.id.buttonLogin);
        checkboxShowPassword = findViewById(R.id.checkboxShowPassword);


        checkboxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Password visible
                edLoginPW.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Password hidden
                edLoginPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            // Cursor ko last mein set karne ke liye
            edLoginPW.setSelection(edLoginPW.length());
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin.setOnClickListener(v -> loginUser());

        edLoginForgotPW.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        edLoginNotRegistered.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = edLoginEmail.getText().toString().trim();
        String password = edLoginPW.getText().toString().trim();

        // Validations
        if (TextUtils.isEmpty(email)) {
            edLoginEmail.setError("Enter your email");
            return;
        }
        if (!email.endsWith("@student.mes.ac.in")) {  // ✅ MES Email Validation
            edLoginEmail.setError("Use MES email ID");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edLoginPW.setError("Enter your password");
            return;
        }

        // Firebase Login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {  // ✅ Email verification check
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email! Check your inbox.", Toast.LENGTH_LONG).show();
                                sendVerificationEmail(user);
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Function to resend the verification email
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    EditText etphone, etpass;
    Button loginButton;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etphone = findViewById(R.id.editTextPhoneNumber);
        etpass = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String phone = etphone.getText().toString().trim();
        String password = etpass.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("^05\\d{8}$")) {
            Toast.makeText(this, "Phone must be 10 digits and start with 05", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(phone + "@smartkart.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();  // Get current user UID
                        mDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String role = snapshot.getValue(String.class);
                                if (role != null) {
                                    // Pass the role to HomePageActivity
                                    Intent intent = new Intent(LoginPage.this, HomePageActivity.class);
                                    intent.putExtra("USER_ROLE", role);
                                    intent.putExtra("USER_ID", phone);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginPage.this, "Role not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(LoginPage.this, "Error loading role", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(LoginPage.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
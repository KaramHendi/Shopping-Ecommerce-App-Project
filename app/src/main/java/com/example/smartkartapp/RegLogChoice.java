package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class RegLogChoice extends AppCompatActivity {
    Button reg, log, logout;
    TextView admlog;
    TextView stflog;
    TextView about;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onResume() {
        super.onResume();
        checkUserRole();  // Refresh visibility when coming back
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_log_choice);

        reg = findViewById(R.id.btnreg);
        log = findViewById(R.id.btnlog);
        admlog = findViewById(R.id.admlog);
        stflog = findViewById(R.id.stflog);
        about = findViewById(R.id.tvAbout);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);

        // Retrieve the role from the intent (if available)
        String userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole != null) {
            updateRoleVisibility(userRole); // Update visibility based on the role
        }

        // Button click listeners
        log.setOnClickListener(v -> startActivity(new Intent(this, LoginPage.class)));
        reg.setOnClickListener(v -> startActivity(new Intent(this, RegisterPage.class)));
        admlog.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
        stflog.setOnClickListener(v -> startActivity(new Intent(this, StaffLogin.class)));
        about.setOnClickListener(v -> startActivity(new Intent(this, About.class)));
    }

    private void checkUserRole() {
        // Hide both TextViews initially
        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);

        // Check if the user is logged in
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();  // Get current user UID
            mDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    if (role != null) {
                        updateRoleVisibility(role); // Update visibility based on the role
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegLogChoice.this, "Error loading role", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Helper function to update visibility based on user role
    private void updateRoleVisibility(String role) {
        if (role.equals("admin")) {
            admlog.setVisibility(View.VISIBLE);
            stflog.setVisibility(View.VISIBLE);  // Admin can see both admin and staff login
        } else if (role.equals("staff")) {
            stflog.setVisibility(View.VISIBLE);  // Staff can only see staff login
        }
    }
}

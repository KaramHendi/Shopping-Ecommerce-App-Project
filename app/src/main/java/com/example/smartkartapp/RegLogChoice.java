package com.example.smartkartapp;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class RegLogChoice extends AppCompatActivity {

    Button reg, log, logout;
    TextView admlog, stflog, about;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onResume() {
        super.onResume();
        // Trigger animation every time the activity comes into the foreground
        animateLogo();
        checkUserRole();  // Refresh visibility when coming back
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_log_choice);

        // Initialize views
        reg = findViewById(R.id.btnreg);
        log = findViewById(R.id.btnlog);
        admlog = findViewById(R.id.admlog);
        stflog = findViewById(R.id.stflog);
        about = findViewById(R.id.tvAbout);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Hide both admin and staff login options initially
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

    private void animateLogo() {
        // Find the logo ImageView
        ImageView logo = findViewById(R.id.imageView2);

        // Create ObjectAnimator for scaling, rotating, and translating
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 1.5f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(logo, "translationX", 0f, 500f, 0f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(logo, "translationY", 0f, 500f, 0f);

        // Set up the AnimatorSet to play all animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotate, translateX, translateY);

        // Optionally, add an interpolator for a bouncing effect
        animatorSet.setInterpolator(new BounceInterpolator());

        // Set duration for the animation (e.g., 2000 ms = 2 seconds)
        animatorSet.setDuration(2000);

        // Start the animation
        animatorSet.start();
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

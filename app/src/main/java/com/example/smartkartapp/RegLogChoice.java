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

    Button reg, log, logout, continueBtn;
    TextView admlog, stflog, about;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private String userRole = "";
    private String userPhone = "";

    @Override
    protected void onResume() {
        super.onResume();
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
        continueBtn = findViewById(R.id.btnContinue);  // New Continue Button

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Hide both admin and staff login options initially
        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);
        continueBtn.setVisibility(View.GONE);  // Hide by default

        // Button click listeners
        log.setOnClickListener(v -> startActivity(new Intent(this, LoginPage.class)));
        reg.setOnClickListener(v -> startActivity(new Intent(this, RegisterPage.class)));
        admlog.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
        stflog.setOnClickListener(v -> startActivity(new Intent(this, StaffLogin.class)));
        about.setOnClickListener(v -> startActivity(new Intent(this, About.class)));

        // Continue to home page with role and phone
        continueBtn.setOnClickListener(v -> {
            if (!userRole.isEmpty() && !userPhone.isEmpty()) {
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("USER_ROLE", userRole);
                intent.putExtra("USER_ID", userPhone);
                startActivity(intent);
            } else {
                Toast.makeText(RegLogChoice.this, "Unable to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animateLogo() {
        ImageView logo = findViewById(R.id.imageView2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 1.5f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(logo, "translationX", 0f, 500f, 0f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(logo, "translationY", 0f, 500f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotate, translateX, translateY);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(2000);
        animatorSet.start();
    }

    private void checkUserRole() {
        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);
        continueBtn.setVisibility(View.GONE);

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userRole = snapshot.child("role").getValue(String.class);
                        userPhone = snapshot.child("phone").getValue(String.class);

                        if (userRole != null) {
                            updateRoleVisibility(userRole);
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(RegLogChoice.this, "Role not found, signed out", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegLogChoice.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegLogChoice.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateRoleVisibility(String role) {
        if (role.equals("admin")) {
            admlog.setVisibility(View.VISIBLE);
            stflog.setVisibility(View.VISIBLE);
            continueBtn.setVisibility(View.VISIBLE);
        } else if (role.equals("staff")) {
            stflog.setVisibility(View.VISIBLE);
            continueBtn.setVisibility(View.VISIBLE);
        } else if (role.equals("user")) {
            continueBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Allow back button functionality
    }
}

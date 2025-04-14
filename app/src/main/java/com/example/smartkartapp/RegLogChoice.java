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

    @Override
    protected void onResume() {
        super.onResume();
        animateLogo();
        checkUserRole();
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
        continueBtn = findViewById(R.id.btnContinue); // New Button

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);
        continueBtn.setVisibility(View.GONE); // Hide by default

        String userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole != null) {
            updateRoleVisibility(userRole);
        }

        log.setOnClickListener(v -> startActivity(new Intent(this, LoginPage.class)));
        reg.setOnClickListener(v -> startActivity(new Intent(this, RegisterPage.class)));
        admlog.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
        stflog.setOnClickListener(v -> startActivity(new Intent(this, StaffLogin.class)));
        about.setOnClickListener(v -> startActivity(new Intent(this, About.class)));
        continueBtn.setOnClickListener(v -> startActivity(new Intent(this, HomePageActivity.class)));
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
                        continueBtn.setVisibility(View.VISIBLE);
                        String role = snapshot.child("role").getValue(String.class);
                        if (role != null) {
                            updateRoleVisibility(role);
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegLogChoice.this, "Error loading user info", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateRoleVisibility(String role) {
        if (role.equals("admin")) {
            admlog.setVisibility(View.VISIBLE);
            stflog.setVisibility(View.VISIBLE);
        } else if (role.equals("staff")) {
            stflog.setVisibility(View.VISIBLE);
        }
    }
}

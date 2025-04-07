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
    Button reg, log;
    TextView admlog, stflog, about;
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
        mDatabase = FirebaseDatabase.getInstance().getReference("memberReg");

        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);

        checkUserRole();

        log.setOnClickListener(v -> startActivity(new Intent(this, LoginPage.class)));
        reg.setOnClickListener(v -> startActivity(new Intent(this, RegisterPage.class)));
        admlog.setOnClickListener(v -> startActivity(new Intent(this, AdminLogin.class)));
        stflog.setOnClickListener(v -> startActivity(new Intent(this, StaffLogin.class)));
        about.setOnClickListener(v -> startActivity(new Intent(this, About.class)));
    }

    private void checkUserRole() {
        admlog.setVisibility(View.GONE);
        stflog.setVisibility(View.GONE);

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            mDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    if (role != null) {
                        if (role.equals("admin")) {
                            admlog.setVisibility(View.VISIBLE);
                            stflog.setVisibility(View.VISIBLE);
                        } else if (role.equals("staff")) {
                            stflog.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegLogChoice.this, "Error loading role", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

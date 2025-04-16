package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddStaff extends AppCompatActivity {

    EditText stfName, stfPhone, stfPass, stfRePass;
    Button regStaff;
    static DatabaseReference databaseUsers;
    FirebaseAuth mAuth;

    public static void getStaff() {
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        // UI elements
        stfName = findViewById(R.id.reg_staff_name);
        stfPhone = findViewById(R.id.reg_staff_phone);
        stfPass = findViewById(R.id.reg_staff_pass);
        stfRePass = findViewById(R.id.reg_staff_repass);
        regStaff = findViewById(R.id.btn_reg_staff);

        regStaff.setOnClickListener(v -> registerStaff());
    }

    public void registerStaff() {
        final String name = stfName.getText().toString().trim();
        final String phone = stfPhone.getText().toString().trim();
        final String pass = stfPass.getText().toString().trim();
        String repass = stfRePass.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (!phone.matches("\\d{10,15}")) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(repass)) {
            Toast.makeText(this, "Confirmed password does not match the given password", Toast.LENGTH_SHORT).show();
        } else {
            databaseUsers.orderByChild("staffname").equalTo(name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(AddStaff.this, "Staff name already exists. Please choose another name.", Toast.LENGTH_SHORT).show();
                            } else {
                                String email = phone + "@smartkart.com";

                                // Register staff
                                mAuth.createUserWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                String uid = mAuth.getCurrentUser().getUid();
                                                String id = databaseUsers.push().getKey();

                                                StaffReg staffReg = new StaffReg(name, pass, id, phone, "staff");

                                                if (id != null) {
                                                    // Save user data in 'users' node under UID
                                                    databaseUsers.child(uid).setValue(staffReg);
                                                    // Save the role under 'role' in the 'users' node
                                                    databaseUsers.child(uid).child("role").setValue("staff");

                                                    Toast.makeText(AddStaff.this, "Staff Registered", Toast.LENGTH_SHORT).show();

                                                    // Sign out staff and re-login as admin
                                                    mAuth.signOut();
                                                    FirebaseUser currentAdmin = mAuth.getCurrentUser();
                                                    String adminEmail = "0587654321@smartkart.com";
                                                    String adminPassword = "appadmin123"; // update if needed
                                                    mAuth.signInWithEmailAndPassword(adminEmail, adminPassword)
                                                            .addOnCompleteListener(loginTask -> {
                                                                if (loginTask.isSuccessful()) {
                                                                    Intent intent = new Intent(AddStaff.this, AdminHomePage.class);
                                                                    intent.putExtra("CALLINGACTIVITY", "AddStaff");
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(AddStaff.this, "Staff registered but failed to login as admin.", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(AddStaff.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddStaff.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddStaff.this, AdminHomePage.class);
        intent.putExtra("CALLINGACTIVITY", "AddStaff");
        startActivity(intent);
        finish();
    }
}

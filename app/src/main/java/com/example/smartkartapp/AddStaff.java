package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddStaff extends AppCompatActivity {
    EditText stfName, stfPhone, stfPass, stfRePass;
    Button regStaff;
    static DatabaseReference databaseStaff;

    public static void getStaff() {
        databaseStaff = FirebaseDatabase.getInstance().getReference("staffreg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        // Initialize Firebase reference
        databaseStaff = FirebaseDatabase.getInstance().getReference("memberReg");

        // Initialize UI elements
        stfName = findViewById(R.id.reg_staff_name);
        stfPhone = findViewById(R.id.reg_staff_phone);
        stfPass = findViewById(R.id.reg_staff_pass);
        stfRePass = findViewById(R.id.reg_staff_repass);
        regStaff = findViewById(R.id.btn_reg_staff);

        // Register staff on button click
        regStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStaff();
            }
        });
    }

    // Method to register staff
    public void registerStaff() {
        final String name = stfName.getText().toString().trim();
        final String phone = stfPhone.getText().toString().trim();
        final String pass = stfPass.getText().toString().trim();
        String repass = stfRePass.getText().toString().trim();

        // Validate fields
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
            // Check if username already exists in Firebase
            databaseStaff.orderByChild("staffname").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(AddStaff.this, "Staff name already exists. Please choose another name.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create new staff entry
                        String id = databaseStaff.push().getKey();
                        StaffReg staffReg = new StaffReg("staffName", "password", "id", "phone", "staff");


                        if (id != null) {
                            databaseStaff.child(id).setValue(staffReg);
                            Toast.makeText(AddStaff.this, "Staff Registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddStaff.this, AdminHomePage.class);
                            intent.putExtra("CALLINGACTIVITY", "AddStaff");
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AddStaff.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Optional: Back navigation to AdminHomePage
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddStaff.this, AdminHomePage.class);
        intent.putExtra("CALLINGACTIVITY", "AddStaff");
        startActivity(intent);
        finish();
    }
}

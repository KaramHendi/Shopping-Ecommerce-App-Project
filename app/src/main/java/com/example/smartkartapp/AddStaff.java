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
    EditText stfName, stfPass, stfRePass;
    Button regStaff;
    static DatabaseReference databaseStaff;

    public static void getStaff() {
        databaseStaff=FirebaseDatabase.getInstance().getReference("staffReg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        // Initialize Firebase reference
        databaseStaff = FirebaseDatabase.getInstance().getReference("staffReg");

        // Initialize UI elements
        stfName = findViewById(R.id.reg_staff_name);
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
        final String name = stfName.getText().toString();
        final String pass = stfPass.getText().toString();
        String repass = stfRePass.getText().toString();

        // Validate fields
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(repass)) {
            // Removed the condition to prevent registration if passwords don't match
            Toast.makeText(this, "Confirmed password does not match the given password", Toast.LENGTH_SHORT).show();
        } else {
            // Check if username already exists in Firebase
            databaseStaff.orderByChild("staffname").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Username already exists
                        Toast.makeText(AddStaff.this, "Staff name already exists. Please choose another name.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create new staff entry
                        String id = databaseStaff.push().getKey();
                        StaffReg staffReg = new StaffReg(name, pass, id);

                        // Save staff data to Firebase
                        if (id != null) {
                            databaseStaff.child(id).setValue(staffReg);

                            // Show success message
                            Toast.makeText(AddStaff.this, "Staff Registered", Toast.LENGTH_SHORT).show();

                            // Redirect to AdminHomePage
                            Intent intent = new Intent(AddStaff.this, AdminHomePage.class);
                            intent.putExtra("CALLINGACTIVITY", "AddStaff");
                            startActivity(intent);
                            finish(); // Close AddStaff activity
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
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
        finish(); // Close AddStaff activity
    }
}

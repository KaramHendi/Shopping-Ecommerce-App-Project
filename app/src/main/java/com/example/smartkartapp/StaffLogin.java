package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class StaffLogin extends AppCompatActivity {
    EditText stfname, stfpass;
    TextView stfstatus;
    Button stflogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        stfname = findViewById(R.id.stfname);
        stfpass = findViewById(R.id.stfpass);
        stfstatus = findViewById(R.id.stfstatus);
        stflogin = findViewById(R.id.stflogin);

        // Clear any previous status
        stfstatus.setText("");

        // Initialize staff database reference
        AddStaff.getStaff();

        // Login button click listener
        stflogin.setOnClickListener(v -> {
            String enteredName = stfname.getText().toString().trim();
            String enteredPass = stfpass.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(enteredName)) {
                stfstatus.setText("Please enter your name");
                return;
            }
            if (TextUtils.isEmpty(enteredPass)) {
                stfstatus.setText("Please enter your password");
                return;
            }

            // Check credentials against the database
            AddStaff.databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot staffSnapshot) {
                    boolean found = false;

                    // Iterate through all staff members to check if credentials match
                    for (DataSnapshot snap : staffSnapshot.getChildren()) {
                        StaffReg staff = snap.getValue(StaffReg.class);
                        if (staff != null && staff.getStaffname().equals(enteredName) && staff.getPassword().equals(enteredPass)) {
                            found = true;
                            checkOngoingDelivery(staff.getStaffname(), staff.getPassword());
                            break;
                        }
                    }

                    // If not found, display error
                    if (!found) {
                        stfstatus.setText("Invalid Credentials or Phone number mismatch");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    stfstatus.setText("Database error: " + error.getMessage());
                }
            });
        });
    }

    private void checkOngoingDelivery(String name, String password) {
        // Initialize delivery database reference
        AcceptOrders.getDelivery();

        AcceptOrders.databaseOngoingDelivery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot deliverySnapshot) {
                boolean isBusy = false;

                // Check if the staff is already assigned to an ongoing delivery
                for (DataSnapshot snap : deliverySnapshot.getChildren()) {
                    DeliverOrder order = snap.getValue(DeliverOrder.class);
                    if (order != null && order.getDeliveryStaffName().equals(name)) {
                        isBusy = true;
                        // Staff is busy, redirect to current order status page
                        Intent i = new Intent(StaffLogin.this, CurrentOrderStatus.class);
                        i.putExtra("STAFFNAME", name);
                        i.putExtra("STAFFPASSWORD", password);
                        startActivity(i);
                        finish();
                        break;
                    }
                }

                // If staff is not busy, show available orders
                if (!isBusy) {
                    Intent i = new Intent(StaffLogin.this, AcceptOrders.class);
                    i.putExtra("STAFFNAME", name);
                    i.putExtra("STAFFPASSWORD", password);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stfstatus.setText("Delivery DB error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Handle back press to navigate to RegLogChoice
        startActivity(new Intent(StaffLogin.this, RegLogChoice.class));
        finish();
    }
}

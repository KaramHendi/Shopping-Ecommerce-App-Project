package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    String sna, spa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        stfname = findViewById(R.id.stfname);
        stfpass = findViewById(R.id.stfpass);
        stfstatus = findViewById(R.id.stfstatus);
        stflogin = findViewById(R.id.stflogin);

        stfstatus.setText("");

        stflogin.setOnClickListener(v -> {
            sna = stfname.getText().toString().trim();
            spa = stfpass.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(sna)) {
                stfstatus.setText("Please enter your name.");
                return;
            } else if (TextUtils.isEmpty(spa)) {
                stfstatus.setText("Please enter your password.");
                return;
            }

            stfstatus.setText("Checking credentials...");

            // Fetch staff details from the database
            AddStaff.getStaff();
            AddStaff.databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean success = false;

                    for (DataSnapshot staffSnapshot : dataSnapshot.getChildren()) {
                        StaffReg staffReg = staffSnapshot.getValue(StaffReg.class);
                        if (staffReg != null) {
                            String dpn = staffReg.getStaffname();
                            String dpa = staffReg.getPassword();

                            if (dpn != null && dpa != null &&
                                    dpn.equals(sna) && dpa.equals(spa)) {
                                success = true;
                                stfstatus.setText("Login successful. Checking delivery status...");
                                handleStaffLogin(dpn, dpa);
                                break;
                            }
                        }
                    }

                    if (!success) {
                        stfstatus.setText("Staff not found or password incorrect.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    stfstatus.setText("Database error: " + databaseError.getMessage());
                }
            });
        });
    }

    private void handleStaffLogin(String staffName, String password) {
        AcceptOrders.getDelivery();
        AcceptOrders.databaseOngoingDelivery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isBusy = false;

                for (DataSnapshot deliverySnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = deliverySnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null && deliverOrder.getDeliveryStaffName().equals(staffName)) {
                        isBusy = true;
                        stfstatus.setText("You have an ongoing delivery. Redirecting...");
                        Intent intent = new Intent(StaffLogin.this, CurrentOrderStatus.class);
                        intent.putExtra("STAFFNAME", staffName);
                        intent.putExtra("STAFFPASSWORD", password);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }

                if (!isBusy) {
                    stfstatus.setText("No current delivery. Redirecting to Accept Orders...");
                    Intent intent = new Intent(StaffLogin.this, AcceptOrders.class);
                    intent.putExtra("STAFFNAME", staffName);
                    intent.putExtra("STAFFPASSWORD", password);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                stfstatus.setText("Delivery database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(StaffLogin.this, RegLogChoice.class));
        finish();
    }
}

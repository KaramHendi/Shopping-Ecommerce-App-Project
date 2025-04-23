package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StaffHomePage extends AppCompatActivity {
    Button acOrd, currentOrderStatus, viewMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home_page);

        final String currentStaffName = getIntent().getStringExtra("STAFFNAME");
        final String currentStaffPassword = getIntent().getStringExtra("STAFFPASSWORD");
        Toast.makeText(this, "Welcome, " + currentStaffName + "!", Toast.LENGTH_SHORT).show();

        acOrd = findViewById(R.id.accpet_orders);
        viewMessages = findViewById(R.id.view_messages); // New button for viewing messages

        acOrd.setOnClickListener(v -> {
            Intent i = new Intent(StaffHomePage.this, AcceptOrders.class);
            i.putExtra("STAFFNAME", currentStaffName);
            i.putExtra("STAFFPASSWORD", currentStaffPassword);
            startActivity(i);
        });


        // On click for viewing messages from users
        viewMessages.setOnClickListener(v -> {
            Intent i = new Intent(StaffHomePage.this, ViewMessagesActivity.class); // This activity will show messages
            startActivity(i);
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(StaffHomePage.this, RegLogChoice.class));
    }

    public void show() {
        Toast.makeText(this, "Accept a delivery in order to check its status", Toast.LENGTH_SHORT).show();
    }
}

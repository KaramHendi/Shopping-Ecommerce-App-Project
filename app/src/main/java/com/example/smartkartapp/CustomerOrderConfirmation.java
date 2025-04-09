package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerOrderConfirmation extends AppCompatActivity {

    private Button orderPickedUpButton, orderNotPickedUpButton;
    private String orderId; // Pass this from previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_confirmation); // Ensure this is your correct XML layout

        orderPickedUpButton = findViewById(R.id.orderPickedUpButton);
        orderNotPickedUpButton = findViewById(R.id.orderNotPickedUpButton);

        // Get the order ID passed from previous activity
        orderId = getIntent().getStringExtra("ORDERID");

        // Set listeners for the buttons
        orderPickedUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(true); // Order picked up
            }
        });

        orderNotPickedUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(false); // Order not picked up
            }
        });
    }

    private void updateOrderStatus(boolean isPickedUp) {
        DatabaseReference databaseOrderRef = FirebaseDatabase.getInstance().getReference("deliverOrder").child(orderId);

        if (isPickedUp) {
            // Update status to "Order picked up"
            databaseOrderRef.child("status").setValue("Order picked up successfully");

            // Send message to customer confirming the pickup (you can add your custom logic here)
            Toast.makeText(CustomerOrderConfirmation.this, "Order status updated: Picked up successfully.", Toast.LENGTH_SHORT).show();
        } else {
            // Set order status back to "Pending"
            databaseOrderRef.child("status").setValue("Pending");

            // Send message to customer confirming that the order is still pending
            Toast.makeText(CustomerOrderConfirmation.this, "Order status updated: Pending again.", Toast.LENGTH_SHORT).show();
        }

        // You may navigate back to a different screen after updating the order status
        finish(); // Close the current activity
    }
}

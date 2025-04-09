package com.example.smartkartapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerOrderConfirmation extends AppCompatActivity {

    TextView orderDetails, orderPrice;
    Button orderPickedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_confirmation);

        // Initialize UI components
        orderDetails = findViewById(R.id.tvOrderDetails);
        orderPrice = findViewById(R.id.tvOrderPrice);
        orderPickedButton = findViewById(R.id.orderPickedButton);

        // Get order ID from intent
        final String orderId = getIntent().getStringExtra("ORDERID");

        // Check if the orderId is valid
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish(); // Exit the activity if no valid order ID is passed
            return;
        }

        // Fetch order details for the customer
        FirebaseDatabase.getInstance().getReference("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DeliverOrder deliverOrder = dataSnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null) {
                        orderDetails.setText(deliverOrder.getOrderDetails());
                        orderPrice.setText(deliverOrder.getPrice());
                    }
                } else {
                    Toast.makeText(CustomerOrderConfirmation.this, "Order not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Toast.makeText(CustomerOrderConfirmation.this, "Error fetching order details.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle order pickup confirmation button click
        orderPickedButton.setOnClickListener(v -> confirmOrderPicked(orderId));
    }

    public void confirmOrderPicked(String orderId) {
        // Update the order status to "Picked Up"
        FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
                .child("status").setValue("Picked Up")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify the customer about the status update
                        Toast.makeText(CustomerOrderConfirmation.this, "Order picked up! Thank you.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle errors if updating the status fails
                        Toast.makeText(CustomerOrderConfirmation.this, "Failed to update the order status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

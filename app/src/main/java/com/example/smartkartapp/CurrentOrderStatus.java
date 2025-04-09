package com.example.smartkartapp;

import android.content.Intent;
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

public class CurrentOrderStatus extends AppCompatActivity {

    TextView custName, custAddr, custPhone, orderDet, orderPrice;
    Button confirmDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_status);

        // Initialize UI components
        custName = findViewById(R.id.tvCustName);
        custPhone = findViewById(R.id.tvCustPhone);
        custAddr = findViewById(R.id.tvCustAddr);
        orderDet = findViewById(R.id.tvdet);
        orderPrice = findViewById(R.id.tvItemPrice);  // Make sure this matches the ID in your XML
        confirmDelivery = findViewById(R.id.confirmDelivery);

        // Get staff name from intent
        final String staffname = getIntent().getStringExtra("STAFFNAME");

        // Fetch ongoing orders for the staff
        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot deliverySnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = deliverySnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null && deliverOrder.getDeliveryStaffName().equals(staffname)) {
                        // Fill in the order details for the staff
                        custName.setText(deliverOrder.getName());
                        custPhone.setText(deliverOrder.getPhone());
                        custAddr.setText(deliverOrder.getAddress());
                        orderDet.setText(deliverOrder.getOrderDetails());
                        orderPrice.setText(deliverOrder.getPrice());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

        // Handle confirm delivery button click (sets status to "Delivered")
        confirmDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatusToDelivered(staffname);
            }
        });
    }

    public void updateOrderStatusToDelivered(String staffname) {
        // Update the order status to "Delivered"
        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("deliveryStaffName").equalTo(staffname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String orderId = orderSnapshot.getKey();
                            FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
                                    .child("status").setValue("Delivered");

                            // Send message to customer asking if they picked up the order
                            sendCustomerConfirmationMessage(orderId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if any
                    }
                });
    }

    private void sendCustomerConfirmationMessage(String orderId) {
        // Simulate sending a message to the customer
        Toast.makeText(this, "Notification sent to customer: Did you pick up the order?", Toast.LENGTH_SHORT).show();

        // Go to CustomerOrderConfirmation activity for customer response
        Intent intent = new Intent(this, CustomerOrderConfirmation.class);
        intent.putExtra("ORDER_ID", orderId);  // Pass the order ID to the next activity
        startActivity(intent);
    }
}

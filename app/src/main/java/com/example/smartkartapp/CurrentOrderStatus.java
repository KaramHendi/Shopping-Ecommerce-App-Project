package com.example.smartkartapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText custPass;
    Button confirmDelivery, orderDeliveredButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_status);

        // Initialize UI components
        custName = findViewById(R.id.tvCustName);
        custPhone = findViewById(R.id.tvCustPhone);
        custAddr = findViewById(R.id.tvCustAddr);
        orderDet = findViewById(R.id.tvdet);
        orderPrice = findViewById(R.id.tvItemPrice);
        confirmDelivery = findViewById(R.id.confirmDelivery);
        custPass = findViewById(R.id.tvCustPass);
        orderDeliveredButton = findViewById(R.id.orderDeliveredButton);  // New button for "Order Delivered"

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

        // Handle confirm delivery button click (sets status to "Awaiting Customer Confirmation")
        confirmDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatusToAwaitingCustomerConfirmation(staffname);
            }
        });

        // Handle "Order Delivered" button click (sets status to "Delivered")
        orderDeliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatusToDelivered(staffname);
            }
        });
    }

    public void updateOrderStatusToAwaitingCustomerConfirmation(String staffname) {
        // Update the order status to "Awaiting Customer Confirmation"
        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("deliveryStaffName").equalTo(staffname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String orderId = orderSnapshot.getKey();
                            FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
                                    .child("status").setValue("Awaiting Customer Confirmation");

                            // Send notification to customer
                            sendCustomerNotification(orderId, "Awaiting Customer Confirmation");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if any
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

                            // Send notification to customer
                            sendCustomerNotification(orderId, "Delivered");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if any
                    }
                });
    }

    private void sendCustomerNotification(String orderId, String status) {
        // Send a notification to the customer (simulated via a toast for simplicity)
        Toast.makeText(this, "Notification sent to the customer: Order " + status, Toast.LENGTH_SHORT).show();
    }
}

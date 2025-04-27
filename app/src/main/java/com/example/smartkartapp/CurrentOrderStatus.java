package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentOrderStatus extends AppCompatActivity {

    TextView tv1, tvtopmsg;
    Button btnDelivered;
    String staffname;
    String[] deliveryKeys = new String[4];

    DatabaseReference databaseDeliveries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_status);

        tv1 = findViewById(R.id.tv1);
        tvtopmsg = findViewById(R.id.tvItemPrice);
        btnDelivered = findViewById(R.id.confirmDelivery);

        staffname = getIntent().getStringExtra("STAFFNAME");
        if (staffname == null) {
            Toast.makeText(this, "Staff information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseDeliveries = FirebaseDatabase.getInstance().getReference("deliverOrder");

        loadDeliveries();

        btnDelivered.setOnClickListener(v -> showConfirmationDialog());
    }

    private void loadDeliveries() {
        databaseDeliveries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                tv1.setText("");

                for (DataSnapshot deliverySnap : snapshot.getChildren()) {
                    if (i >= 4) break;

                    DeliverOrder order = deliverySnap.getValue(DeliverOrder.class);
                    if (order == null) continue;

                    if (staffname.equals(order.getDeliveryStaffName())) {
                        String name = order.getName() != null ? order.getName() : "N/A";
                        String phone = order.getPhone() != null ? order.getPhone() : "N/A";
                        String address = order.getAddress() != null ? order.getAddress() : "N/A";
                        String orderDetails = order.getOrderDetails() != null ? order.getOrderDetails() : "N/A";
                        String price = order.getPrice() != null ? order.getPrice() : "N/A";

                        String details = "NAME: " + name +
                                "\nPHONE: " + phone +
                                "\nADDRESS: " + address +
                                "\nORDER DETAILS: " + orderDetails +
                                "\nPRICE: " + price;

                        deliveryKeys[i] = deliverySnap.getKey();

                        if (i == 0) {
                            tv1.setText(details);
                        }

                        i++;
                    }
                }

                if (i > 0) {
                    tvtopmsg.setText("Deliver the orders and tap Delivered");
                } else {
                    tvtopmsg.setText("You have no current deliveries.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CurrentOrderStatus.this, "Failed to load deliveries", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delivery")
                .setMessage("Are you sure the order has been delivered?")
                .setPositiveButton("Yes", (dialog, which) -> markAsDelivered())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void markAsDelivered() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        DatabaseReference userOrdersRef = FirebaseDatabase.getInstance().getReference("userOrders");

        for (String key : deliveryKeys) {
            if (key != null) {
                // Step 1: Delete the order from "orders"
                ordersRef.child(key).removeValue()
                        .addOnSuccessListener(unused -> {
                            // Step 2: Update status to "delivered" in "userOrders"
                            userOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        if (userSnapshot.hasChild(key)) {
                                            userSnapshot.getRef().child(key).child("status").setValue("delivered");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CurrentOrderStatus.this, "Failed to update user order status", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Step 3: Remove from "deliverOrder"
                            databaseDeliveries.child(key).removeValue();

                            Toast.makeText(CurrentOrderStatus.this, "Order Delivered Successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CurrentOrderStatus.this, "Failed to delete from orders", Toast.LENGTH_SHORT).show();
                        });
            }
        }

        startActivity(new Intent(CurrentOrderStatus.this, AcceptOrders.class));
        finish();
    }


    @Override
    public void onBackPressed() {
        // Prevent going back
    }
}

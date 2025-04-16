package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentOrderStatus extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tvtopmsg;
    Button btnDelivered;
    String staffname;
    String[] deliveryKeys = new String[4];

    DatabaseReference databaseDeliveries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_status);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tvtopmsg = findViewById(R.id.tvItemPrice);
        btnDelivered = findViewById(R.id.confirmDelivery);

        staffname = getIntent().getStringExtra("STAFFNAME");
        databaseDeliveries = FirebaseDatabase.getInstance().getReference("deliverOrder");

        loadDeliveries();

        btnDelivered.setOnClickListener(v -> showConfirmationDialog());
    }

    private void loadDeliveries() {
        databaseDeliveries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot deliverySnap : snapshot.getChildren()) {
                    DeliverOrder order = deliverySnap.getValue(DeliverOrder.class);
                    if (order != null && staffname.equals(order.getDeliveryStaffName())) {
                        String details = "NAME:" + order.getName() +
                                "\nPHONE:" + order.getPhone() +
                                "\nADDRESS:" + order.getAddress() +
                                "\nORDER DETAILS:" + order.getPrice() +
                                "\nPRICE:" + order.getPrice();

                        deliveryKeys[i] = deliverySnap.getKey();

                        switch (i) {
                            case 0: tv1.setText(details); break;
                            case 1: tv2.setText(details); break;
                            case 2: tv3.setText(details); break;
                            case 3: tv4.setText(details); break;
                        }

                        i++;
                        if (i >= 4) break;
                    }
                }

                if (i > 0) {
                    tvtopmsg.setText("Deliver the orders and tap Delivered");
                } else {
                    tv1.setText("");
                    tv2.setText("");
                    tv3.setText("");
                    tv4.setText("");
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
        for (String key : deliveryKeys) {
            if (key != null) {
                databaseDeliveries.child(key).removeValue();
            }
        }

        Toast.makeText(this, "Order status updated to Pending Confirmation", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CurrentOrderStatus.this, AcceptOrders.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CurrentOrderStatus.this, CurrentOrderStatus.class));
    }
}

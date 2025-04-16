package com.example.smartkartapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private ListView lvOrderHistory;
    private ArrayList<Orders> orderList;
    private OrdersAdapter ordersAdapter;
    private DatabaseReference databaseOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lvOrderHistory = findViewById(R.id.lvOrderHistory);
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        lvOrderHistory.setAdapter(ordersAdapter);

        String userId = getIntent().getStringExtra("USER_ID");

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase reference to orders
        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");

        // Query orders for the specific user
        databaseOrders.orderByChild("custphone").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    orderList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Orders order = snapshot.getValue(Orders.class);
                        if (order != null) {
                            orderList.add(order);
                        }
                    }
                    ordersAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderHistoryActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

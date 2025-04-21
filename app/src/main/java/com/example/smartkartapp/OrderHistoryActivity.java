package com.example.smartkartapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lvOrderHistory = findViewById(R.id.lvOrderHistory);
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        lvOrderHistory.setAdapter(ordersAdapter);

        String userPhone = getIntent().getStringExtra("USER_PHONE");

        if (userPhone == null || userPhone.isEmpty()) {
            Toast.makeText(this, "User phone not found", Toast.LENGTH_SHORT).show();
            return;
        }

        getUserUidAndFetchOrders(userPhone);
    }

    private void getUserUidAndFetchOrders(String userPhone) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("phone").equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String uid = userSnap.getKey();
                        Log.d("OrderHistory", "User found with UID: " + uid);
                        fetchUserOrders(uid);
                        return;
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserOrders(String uid) {
        DatabaseReference userOrdersRef = FirebaseDatabase.getInstance().getReference("userOrders").child(uid);

        userOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Orders order = orderSnap.getValue(Orders.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

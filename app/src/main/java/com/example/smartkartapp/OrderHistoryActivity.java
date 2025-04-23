package com.example.smartkartapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private ArrayList<Orders> orderList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.ordersRecyclerView);
        progressBar = findViewById(R.id.progressBar); // Ensure it's defined in XML

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        recyclerView.setAdapter(ordersAdapter);

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

        progressBar.setVisibility(View.VISIBLE);

        userOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot orderSnap : snapshot.getChildren()) {
                        Orders order = orderSnap.getValue(Orders.class);
                        if (order != null) {
                            // Optional: Set default or dummy status if missing
                            if (order.getStatus() == null || order.getStatus().isEmpty()) {
                                order.setStatus("Ongoing");  // fallback
                            }
                            orderList.add(order);
                        }
                    }

                    if (orderList.isEmpty()) {
                        Toast.makeText(OrderHistoryActivity.this, "No orders found.", Toast.LENGTH_SHORT).show();
                    }

                    ordersAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "No orders found for this user.", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

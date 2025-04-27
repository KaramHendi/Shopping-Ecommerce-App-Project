package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AcceptOrders extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tvtopmsg;
    static DatabaseReference databaseOngoingDelivery;

    String[] orderKeys = new String[4];

    public static void getDelivery() {
        databaseOngoingDelivery = FirebaseDatabase.getInstance().getReference("deliverOrder");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_orders);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tvtopmsg = findViewById(R.id.tvtopmsg);

        final String staffname = getIntent().getStringExtra("STAFFNAME");
        final String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");

        if (staffname == null || staffpassword == null) {
            Toast.makeText(this, "Staff information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseOngoingDelivery = FirebaseDatabase.getInstance().getReference("deliverOrder");
        PlaceOrder.getOrder();

        PlaceOrder.databaseOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                clearAllTextViews();

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    if (i >= 4) break;

                    Orders orders = orderSnapshot.getValue(Orders.class);
                    if (orders == null) continue;

                    String name = orders.getCustname() != null ? orders.getCustname() : "N/A";
                    String phone = orders.getCustphone() != null ? orders.getCustphone() : "N/A";
                    String address = orders.getCustaddr() != null ? orders.getCustaddr() : "N/A";
                    String specs = orders.getSpec() != null ? orders.getSpec() : "N/A";
                    String priceStr = orders.getPrice() != 0 ? String.valueOf(orders.getPrice()) : "N/A";

                    String details = "NAME: " + name +
                            "\nPHONE: " + phone +
                            "\nADDRESS: " + address +
                            "\nORDER DETAILS: " + specs +
                            "\nPRICE: " + priceStr;

                    orderKeys[i] = orderSnapshot.getKey();

                    switch (i) {
                        case 0: tv1.setText(details); break;
                        case 1: tv2.setText(details); break;
                        case 2: tv3.setText(details); break;
                        case 3: tv4.setText(details); break;
                    }
                    i++;
                }

                if (i > 0) {
                    tvtopmsg.setText("Tap on an order to start its delivery");
                } else {
                    tvtopmsg.setText("There are no ongoing orders");
                }

                setupOrderClickListeners(staffname, staffpassword);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AcceptOrders.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllTextViews() {
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");
    }

    private void setupOrderClickListeners(final String staffname, final String staffpassword) {
        tv1.setOnClickListener(v -> handleOrderClick(tv1.getText().toString(), staffname, staffpassword, 0));
        tv2.setOnClickListener(v -> handleOrderClick(tv2.getText().toString(), staffname, staffpassword, 1));
        tv3.setOnClickListener(v -> handleOrderClick(tv3.getText().toString(), staffname, staffpassword, 2));
        tv4.setOnClickListener(v -> handleOrderClick(tv4.getText().toString(), staffname, staffpassword, 3));
    }

    private void handleOrderClick(String details, String staffname, String staffpassword, int index) {
        if (!details.isEmpty() && orderKeys[index] != null) {
            addOrderToDeliver(details, staffname, staffpassword, orderKeys[index]);
        }
    }

    public void addOrderToDeliver(String details, final String staffname, final String staffpassword, String orderKey) {
        String[] lines = details.split("\n");
        if (lines.length < 5) {
            Toast.makeText(this, "Invalid order format", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = lines[0].replace("NAME:", "").trim();
        String phone = lines[1].replace("PHONE:", "").trim();
        String address = lines[2].replace("ADDRESS:", "").trim();
        String specs = lines[3].replace("ORDER DETAILS:", "").trim();
        String price = lines[4].replace("PRICE:", "").trim();

        String id = databaseOngoingDelivery.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Failed to generate delivery ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DeliverOrder deliverOrder = new DeliverOrder(name, phone, id, address, specs, staffname, price);

        databaseOngoingDelivery.child(id).setValue(deliverOrder)
                .addOnSuccessListener(unused -> {
                    PlaceOrder.databaseOrders.child(orderKey).removeValue();
                    Toast.makeText(this, "Order moved to your delivery list", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AcceptOrders.this, CurrentOrderStatus.class);
                    i.putExtra("STAFFNAME", staffname);
                    i.putExtra("STAFFPASSWORD", staffpassword);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to move order to delivery", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AcceptOrders.this, StaffHomePage.class));
        finish();
    }
}

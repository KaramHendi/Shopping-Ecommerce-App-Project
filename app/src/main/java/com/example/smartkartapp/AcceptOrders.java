package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    public static void getDelivery() {
        databaseOngoingDelivery = FirebaseDatabase.getInstance().getReference("deliverOrder");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_orders);

        // Initialize the TextViews
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tvtopmsg = findViewById(R.id.tvtopmsg);

        final String staffname = getIntent().getStringExtra("STAFFNAME");
        final String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");

        databaseOngoingDelivery = FirebaseDatabase.getInstance().getReference("deliverOrder");

        PlaceOrder.getOrder();

        PlaceOrder.databaseOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                // Loop through each order and display up to 4 ongoing orders
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    i++;
                    Orders orders = orderSnapshot.getValue(Orders.class);
                    String name = orders.getCustname();
                    String phone = orders.getCustphone();
                    String address = orders.getCustaddr();
                    String specs = orders.getSpec();
                    int price = orders.getPrice();
                    String details = "NAME:" + name + "\nPHONE:" + phone + "\nADDRESS:" + address + "\nORDER DETAILS:" + specs + "\nPRICE:" + price;

                    // Set the TextViews with the order details
                    switch (i) {
                        case 1:
                            tv1.setText(details);
                            tv2.setText("");
                            tv3.setText("");
                            tv4.setText("");
                            break;
                        case 2:
                            tv2.setText(details);
                            tv3.setText("");
                            tv4.setText("");
                            break;
                        case 3:
                            tv3.setText(details);
                            tv4.setText("");
                            break;
                        case 4:
                            tv4.setText(details);
                            break;
                    }
                    if (i > 0) {
                        tvtopmsg.setText("Tap on an order to start its delivery");
                    }
                }
                // No ongoing orders message
                if (i == 0) {
                    tvtopmsg.setText("There are no ongoing orders");
                    tv1.setText("");
                }

                // Set onClickListener for each TextView dynamically
                setOnClickListenerForOrder(tv1, staffname, staffpassword);
                setOnClickListenerForOrder(tv2, staffname, staffpassword);
                setOnClickListenerForOrder(tv3, staffname, staffpassword);
                setOnClickListenerForOrder(tv4, staffname, staffpassword);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database error
            }
        });
    }

    // Helper method to set onClickListener for each TextView
    private void setOnClickListenerForOrder(TextView textView, final String staffname, final String staffpassword) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textView.getText().toString().equals("")) {
                    String details = textView.getText().toString();
                    addOrderToDeliver(details, staffname, staffpassword);
                }
            }
        });
    }

    public void addOrderToDeliver(String details, final String staffname, final String staffpassword) {
        final String name = details.substring(5, details.indexOf("\nPHONE"));
        final String phone = details.substring(details.indexOf("PHONE") + 6, details.indexOf("\nADDRESS"));
        final String address = details.substring(details.indexOf("ADDRESS") + 8, details.indexOf("\nORDER DETAILS"));
        final String specs = details.substring(details.indexOf("ORDER DETAILS") + 14, details.indexOf("\nPRICE"));
        final String price = details.substring(details.indexOf("PRICE") + 6);
        final String id = databaseOngoingDelivery.push().getKey();

        // Create the DeliverOrder object and add it to the database with status
        DeliverOrder deliverOrder = new DeliverOrder(name, phone, id, address, specs, staffname, price, "Pending");
        databaseOngoingDelivery.child(id).setValue(deliverOrder);
        Toast.makeText(this, "Order added to your to deliver list", Toast.LENGTH_SHORT).show();

        // Remove the order from the orders database and redirect to CurrentOrderStatus
        PlaceOrder.databaseOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Orders orders = orderSnapshot.getValue(Orders.class);
                    if (orders.getCustname().equals(name) &&
                            orders.getCustphone().equals(phone) &&
                            orders.getCustaddr().equals(address) &&
                            orders.getSpec().equals(specs)) {
                        // Remove the order from the orders database
                        orderSnapshot.getRef().removeValue();

                        // Pass the necessary data to the next screen
                        Intent i = new Intent(AcceptOrders.this, CurrentOrderStatus.class);
                        i.putExtra("STAFFNAME", staffname);
                        i.putExtra("STAFFPASSWORD", staffpassword);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(AcceptOrders.this, StaffLogin.class));
    }
}

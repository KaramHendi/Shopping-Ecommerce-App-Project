package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class PlaceOrder extends AppCompatActivity {

    TextView cname, cphone, ordspec, ordprice, ordError;
    EditText addr;
    Button plord;

    DatabaseReference databaseReference;
    static DatabaseReference databaseOrders;
    static DatabaseReference databaseStocks;
    FirebaseAuth auth;

    String custName, custPhone, custPass, itemDetails;
    int itemPrice;
    String uid;

    public static void getOrder() {
        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        // UI elements
        cname = findViewById(R.id.custname);
        cphone = findViewById(R.id.custphone);
        ordspec = findViewById(R.id.itemdet);
        ordprice = findViewById(R.id.itemprice);
        addr = findViewById(R.id.etadd);
        ordError = findViewById(R.id.ordError);
        plord = findViewById(R.id.btnplord);

        // Firebase initialization
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            showToast("User not found.");
            finish();
            return;
        }

        // Get intent data
        custPass = getIntent().getStringExtra("CUSTPASS");
        itemDetails = getIntent().getStringExtra("ITEMDET");
        itemPrice = getIntent().getIntExtra("item_price", 0);

        ordspec.setText(itemDetails);
        ordprice.setText(String.valueOf(itemPrice));

        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
        databaseStocks = FirebaseDatabase.getInstance().getReference("stocks");

        // Initially disable place order button
        plord.setEnabled(false);

        // Fetch current user details
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MemberReg user = snapshot.getValue(MemberReg.class);
                if (user != null) {
                    // Log fetched user data for debugging
                    Log.d("PlaceOrder", "User name: " + user.getName() + ", phone: " + user.getPhone());

                    custName = user.getName() != null ? user.getName() : "N/A";  // Ensure a fallback value for custName
                    custPhone = user.getPhone();

                    // Log the customer details
                    Log.d("PlaceOrder", "Customer Name: " + custName + ", Customer Phone: " + custPhone);

                    cname.setText(custName);  // Set name in TextView
                    cphone.setText(custPhone); // Set phone in TextView

                    plord.setEnabled(true);  // Enable the "Place Order" button
                } else {
                    showToast("Failed to load user data.");
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to load user data.");
                finish();
            }
        });

        // Place order
        plord.setOnClickListener(v -> {
            String address = addr.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                ordError.setText("Please enter your address");
                return;
            }

            // Fetch user details fresh before placing order
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    MemberReg user = snapshot.getValue(MemberReg.class);
                    if (user != null) {
                        custName = user.getName() != null ? user.getName() : "N/A";  // Ensure a fallback value for custName
                        custPhone = user.getPhone();

                        // Fetch item stock details
                        databaseStocks.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean itemFound = false;

                                for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                                    StockReg stockReg = stockSnapshot.getValue(StockReg.class);

                                    if (stockReg != null && stockReg.getItemName().equalsIgnoreCase(itemDetails)) {
                                        itemFound = true;
                                        int currentStock = stockReg.getCurrentStockAvailaible();

                                        if (currentStock > 0) {
                                            currentStock--;  // Update stock count

                                            databaseStocks.child(stockReg.getId())
                                                    .child("currentStockAvailaible")
                                                    .setValue(currentStock);

                                            // Place the order in database
                                            String orderId = databaseOrders.push().getKey();
                                            if (orderId != null) {
                                                Orders order = new Orders(orderId, itemDetails, custName, custPhone, address, custPass, itemPrice, "pending");

                                                databaseOrders.child(orderId).setValue(order)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                FirebaseDatabase.getInstance().getReference("userOrders")
                                                                        .child(uid).child(orderId).setValue(order);

                                                                showToast("Order placed successfully!");
                                                                goToHome();
                                                            } else {
                                                                showToast("Failed to place order.");
                                                            }
                                                        });
                                            } else {
                                                showToast("Failed to generate order ID.");
                                            }
                                        } else {
                                            showToast("Item is out of stock!");
                                        }
                                        break;
                                    }
                                }

                                if (!itemFound) {
                                    showToast("Item not found in stock!");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showToast("Database error: " + databaseError.getMessage());
                            }
                        });

                    } else {
                        showToast("Failed to load user data.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to load user data.");
                }
            });
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToHome() {
        Intent intent = new Intent(PlaceOrder.this, HomePageActivity.class);
        intent.putExtra("NAME", custName);
        intent.putExtra("PHONE", custPhone);
        intent.putExtra("PASSWORD", custPass);
        intent.putExtra("CALLINGACTIVITY", "PlaceOrder");
        startActivity(intent);
        finish();
    }
}

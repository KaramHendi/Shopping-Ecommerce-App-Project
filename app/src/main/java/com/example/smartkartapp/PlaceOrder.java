package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

    static DatabaseReference databaseOrders;
    static DatabaseReference databaseStocks;

    String custName, custPhone, custPass, itemDetails;
    int itemPrice;

    public static void getOrder() {
        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        // UI elements initialization
        cname = findViewById(R.id.custname);
        cphone = findViewById(R.id.custphone);
        ordspec = findViewById(R.id.itemdet);
        ordprice = findViewById(R.id.itemprice);
        addr = findViewById(R.id.etadd);
        ordError = findViewById(R.id.ordError);
        plord = findViewById(R.id.btnplord);

        // Clear previous error messages
        ordError.setText("");

        // Retrieve data passed through the Intent
        custName = getIntent().getStringExtra("CUSTNAME");
        custPhone = getIntent().getStringExtra("CUSTPH");
        custPass = getIntent().getStringExtra("CUSTPASS");
        itemDetails = getIntent().getStringExtra("ITEMDET");
        itemPrice = getIntent().getIntExtra("item_price", 0);

        // Set UI with received data
        cname.setText(custName);
        cphone.setText(custPhone);
        ordspec.setText(itemDetails);
        ordprice.setText(String.valueOf(itemPrice));

        // Firebase database references
        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
        databaseStocks = FirebaseDatabase.getInstance().getReference("stocks");

        // Button click to place the order
        plord.setOnClickListener(v -> {
            String address = addr.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                ordError.setText("Please enter your address");
                return;
            }

            // Check stock and place order
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
                                currentStock--;
                                // Update stock availability in the database
                                databaseStocks.child(stockReg.getId()).child("currentStockAvailaible").setValue(currentStock);

                                // Create a new order
                                String orderId = databaseOrders.push().getKey();
                                Orders order = new Orders(orderId, itemDetails, custName, custPhone, address, custPass, itemPrice, "pending");

                                // Save the order in the "orders" node
                                databaseOrders.child(orderId).setValue(order).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Save order under userOrders as well
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference("userOrders")
                                                .child(uid).child(orderId).setValue(order);

                                        showToast("Order placed successfully!");
                                        goToHome();
                                    } else {
                                        showToast("Failed to place order. Please try again later.");
                                    }
                                });
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
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToHome() {
        // Redirect to the Home Page Activity after successful order placement
        Intent intent = new Intent(PlaceOrder.this, HomePageActivity.class);
        intent.putExtra("NAME", custName);
        intent.putExtra("PHONE", custPhone);
        intent.putExtra("PASSWORD", custPass);
        intent.putExtra("CALLINGACTIVITY", "PlaceOrder");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Handle back press, ensuring the user can navigate back to the previous screen with the right data
        Intent intent = new Intent(PlaceOrder.this, DisplayItem.class);
        intent.putExtra("NAME", custName);
        intent.putExtra("PHONE", custPhone);
        intent.putExtra("PASSWORD", custPass);
        intent.putExtra("CALLING_ACTIVITY", getIntent().getStringExtra("CALLING_ACTIVITY"));
        intent.putExtra("image_id", getIntent().getIntExtra("image_id", 0));
        intent.putExtra("item_details", itemDetails);
        intent.putExtra("item_price", itemPrice);
        startActivity(intent);
        finish();
    }
}

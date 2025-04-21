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

        // UI elements
        cname = findViewById(R.id.custname);
        cphone = findViewById(R.id.custphone);
        ordspec = findViewById(R.id.itemdet);
        ordprice = findViewById(R.id.itemprice);
        addr = findViewById(R.id.etadd);
        ordError = findViewById(R.id.ordError);
        plord = findViewById(R.id.btnplord);

        ordError.setText("");

        // Get intent extras
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

        // Firebase references
        databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
        databaseStocks = FirebaseDatabase.getInstance().getReference("stocks");

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
                            int curr = stockReg.getCurrentStockAvailaible();

                            if (curr > 0) {
                                curr--;
                                databaseStocks.child(stockReg.getId()).child("currentStockAvailaible").setValue(curr);

                                String orderId = databaseOrders.push().getKey();
                                Orders order = new Orders(orderId, itemDetails, custName, custPhone, address, custPass, itemPrice);

                                // Save order in orders node
                                databaseOrders.child(orderId).setValue(order).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Save under userOrders as well
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference("userOrders")
                                                .child(uid).child(orderId).setValue(order);

                                        showToast("Order placed successfully");
                                        goToHome();
                                    } else {
                                        showToast("Failed to place order");
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
                    showToast("Database Error: " + databaseError.getMessage());
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

    @Override
    public void onBackPressed() {
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

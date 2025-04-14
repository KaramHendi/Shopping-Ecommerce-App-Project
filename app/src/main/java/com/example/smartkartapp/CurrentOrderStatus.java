package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CurrentOrderStatus extends AppCompatActivity {

    TextView custName, custAddr, custPhone, orderDet, orderPrice;
    EditText custPass;
    Button confirmDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_status);

        custName = findViewById(R.id.tvCustName);
        custPhone = findViewById(R.id.tvCustPhone);
        custAddr = findViewById(R.id.tvCustAddr);
        orderDet = findViewById(R.id.tvdet);
        orderPrice = findViewById(R.id.tvItemPrice);
        confirmDelivery = findViewById(R.id.confirmDelivery);
        custPass = findViewById(R.id.tvCustPass);

        // Clear the fields initially
        custName.setText("");
        custAddr.setText("");
        orderDet.setText("");
        custPhone.setText("");
        custPass.setText("");

        final String staffname = getIntent().getStringExtra("STAFFNAME");
        final String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");

        // Fetch ongoing deliveries
        AcceptOrders.databaseOngoingDelivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot deliverySnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = deliverySnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder.getDeliveryStaffName().equals(staffname)) {
                        // Populate fields with order details
                        custName.setText(deliverOrder.getName());
                        custPhone.setText(deliverOrder.getPhone());
                        custAddr.setText(deliverOrder.getAddress());
                        orderDet.setText(deliverOrder.getOrderDetails());
                        orderPrice.setText(deliverOrder.getPrice());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation
            }
        });

        // Confirm delivery button click
        confirmDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDeliveryConfirmation();
            }
        });
    }

    public void checkDeliveryConfirmation() {
        // Check if password field is empty
        if (TextUtils.isEmpty(custPass.getText().toString())) {
            Toast.makeText(this, "Please enter the customer's password to confirm delivery", Toast.LENGTH_SHORT).show();
        } else {
            // Get user data from Firebase
            RegisterPage.getuser();
            RegisterPage.databaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        MemberReg memberReg = userSnapshot.getValue(MemberReg.class);
                        final String name = memberReg.getUsername();
                        final String phone = memberReg.getPhone();
                        String password = memberReg.getPassword();

                        // Check if customer exists and password matches
                        if (name.equals(custName.getText().toString()) && phone.equals(custPhone.getText().toString())) {
                            if (password.equals(custPass.getText().toString())) {
                                AcceptOrders.getDelivery();
                                AcceptOrders.databaseOngoingDelivery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                            DeliverOrder deliverOrder = orderSnapshot.getValue(DeliverOrder.class);
                                            if (name.equals(deliverOrder.getName()) && phone.equals(deliverOrder.getPhone()) &&
                                                    (orderDet.getText().toString()).equals(deliverOrder.getOrderDetails())) {
                                                // Remove order from ongoing deliveries
                                                orderSnapshot.getRef().removeValue();

                                                // Show success message
                                                showSuccess();

                                                // Clear fields after delivery
                                                clearFields();

                                                // Redirect to AcceptOrders screen
                                                String staffname = getIntent().getStringExtra("STAFFNAME");
                                                String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");
                                                Intent i = new Intent(CurrentOrderStatus.this, AcceptOrders.class);
                                                i.putExtra("STAFFNAME", staffname);
                                                i.putExtra("STAFFPASSWORD", staffpassword);
                                                startActivity(i);
                                                finish();
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });
                            } else {
                                // Show error message for incorrect password
                                showError("Incorrect password. Please make sure the password is correct.");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database cancellation
                }
            });
        }
    }

    // Show success message
    public void showSuccess() {
        Toast.makeText(this, "Order delivered successfully! You can now proceed with the next delivery.", Toast.LENGTH_LONG).show();
    }

    // Show error message
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Clear input fields after successful delivery
    public void clearFields() {
        custName.setText("");
        custAddr.setText("");
        orderDet.setText("");
        custPhone.setText("");
        custPass.setText("");
    }

    // Handle back press event
    public void onBackPressed() {
        Toast.makeText(this, "Please confirm the delivery before logging out.", Toast.LENGTH_LONG).show();
    }

    // Handle activity destruction
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

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

        // Initializing UI components
        custName = findViewById(R.id.tvCustName);
        custPhone = findViewById(R.id.tvCustPhone);
        custAddr = findViewById(R.id.tvCustAddr);
        orderDet = findViewById(R.id.tvdet);
        orderPrice = findViewById(R.id.tvItemPrice);
        confirmDelivery = findViewById(R.id.confirmDelivery);
        custPass = findViewById(R.id.tvCustPass);

        // Resetting fields
        custName.setText("");
        custAddr.setText("");
        orderDet.setText("");
        custPhone.setText("");
        custPass.setText("");

        final String staffname = getIntent().getStringExtra("STAFFNAME");
        final String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");

        // Fetch ongoing delivery orders
        AcceptOrders.databaseOngoingDelivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot deliverySnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = deliverySnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null && deliverOrder.getDeliveryStaffName().equals(staffname)) {
                        // Fill in customer details from the deliver order
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
                // Handle any errors while fetching data
            }
        });

        // Handle confirm delivery button click
        confirmDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDeliveryConfirmation();
            }
        });
    }

    public void checkDeliveryConfirmation() {
        if (TextUtils.isEmpty(custPass.getText().toString())) {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch ongoing delivery orders to get OTP
        AcceptOrders.databaseOngoingDelivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = orderSnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null && deliverOrder.getName().equals(custName.getText().toString())
                            && deliverOrder.getPhone().equals(custPhone.getText().toString())) {

                        // Check if the entered OTP matches
                        if (custPass.getText().toString().equals(deliverOrder.getOtp())) {
                            completeDelivery(deliverOrder);
                        } else {
                            showError();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors while fetching data
            }
        });
    }

    private void completeDelivery(DeliverOrder deliverOrder) {
        // Remove the order from the database after delivery confirmation
        AcceptOrders.databaseOngoingDelivery.child(deliverOrder.getId()).removeValue();

        // Show success message
        showSuccess();

        // Reset fields and navigate to AcceptOrders screen
        resetFields();
        navigateToAcceptOrders();
    }

    private void resetFields() {
        // Reset the fields to be empty
        custName.setText("");
        custAddr.setText("");
        orderDet.setText("");
        custPhone.setText("");
        custPass.setText("");
    }

    private void navigateToAcceptOrders() {
        String staffname = getIntent().getStringExtra("STAFFNAME");
        String staffpassword = getIntent().getStringExtra("STAFFPASSWORD");
        Intent i = new Intent(CurrentOrderStatus.this, AcceptOrders.class);
        i.putExtra("STAFFNAME", staffname);
        i.putExtra("STAFFPASSWORD", staffpassword);
        startActivity(i);
        finish();
    }

    public void showSuccess() {
        Toast.makeText(this, "Order delivered successfully", Toast.LENGTH_SHORT).show();
    }

    public void showError() {
        Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Prevent user from going back without confirming delivery
        Toast.makeText(this, "Please deliver this order before you log out", Toast.LENGTH_SHORT).show();
    }
}

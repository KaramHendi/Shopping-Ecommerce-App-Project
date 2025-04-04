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
            Toast.makeText(this, "Please enter the customer's password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user data for password validation
        RegisterPage.getuser();
        RegisterPage.databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    MemberReg memberReg = userSnapshot.getValue(MemberReg.class);
                    if (memberReg != null) {
                        final String name = memberReg.getUsername();
                        final String phone = memberReg.getPhone();
                        final String password = memberReg.getPassword();

                        // Check if the entered customer name and phone match
                        if (name.equals(custName.getText().toString()) && phone.equals(custPhone.getText().toString())) {
                            if (password.equals(custPass.getText().toString())) {
                                // Password matches, now confirm delivery
                                completeDelivery(name, phone);
                            } else {
                                showError();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors while fetching data
            }
        });
    }

    private void completeDelivery(final String name, final String phone) {
        // Get the ongoing delivery orders
        AcceptOrders.databaseOngoingDelivery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    DeliverOrder deliverOrder = orderSnapshot.getValue(DeliverOrder.class);
                    if (deliverOrder != null && name.equals(deliverOrder.getName()) && phone.equals(deliverOrder.getPhone())
                            && orderDet.getText().toString().equals(deliverOrder.getOrderDetails())) {

                        // Remove the order from the database after delivery confirmation
                        orderSnapshot.getRef().removeValue();

                        // Show success message
                        showSuccess();

                        // Reset fields and navigate to AcceptOrders screen
                        resetFields();
                        navigateToAcceptOrders();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors while processing data
            }
        });
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
        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Prevent user from going back without confirming delivery
        Toast.makeText(this, "Please deliver this order before you log out", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Kill the process to clean up resources
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddStock extends AppCompatActivity {
    public static DatabaseReference databaseStocks;
    EditText itemId, addQuantity;
    Button btnUpdate;

    public static void getStocks() {
        databaseStocks= FirebaseDatabase.getInstance().getReference("stocksReg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        itemId = findViewById(R.id.etId);
        addQuantity = findViewById(R.id.etAdd);
        btnUpdate = findViewById(R.id.btnUpdateStock);
        databaseStocks = FirebaseDatabase.getInstance().getReference("stocks");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs
                String itemIdText = itemId.getText().toString();
                String addQuantityText = addQuantity.getText().toString();

                if (TextUtils.isEmpty(itemIdText)) {
                    itemIdNotEntered();
                } else if (TextUtils.isEmpty(addQuantityText)) {
                    quantityNotEntered();
                } else {
                    try {
                        int inputItemId = Integer.parseInt(itemIdText);
                        int quantityToAdd = Integer.parseInt(addQuantityText);

                        if (quantityToAdd <= 0) {
                            quantityNotEntered();
                        } else {
                            // Query the stock by item_id
                            databaseStocks.orderByChild("item_id").equalTo(inputItemId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                                            StockReg stockReg = stockSnapshot.getValue(StockReg.class);
                                            if (stockReg != null) {
                                                int currentStock = stockReg.getCurrentStockAvailaible();
                                                int updatedStock = currentStock + quantityToAdd;

                                                // Update the stock in Firebase
                                                databaseStocks.child(stockReg.getId()).child("currentStockAvailaible").setValue(updatedStock);
                                                showSuccess();

                                                // Navigate back to AdminHomePage
                                                Intent intent = new Intent(AddStock.this, AdminHomePage.class);
                                                intent.putExtra("CALLINGACTIVITY", "AddStock");
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    } else {
                                        invalidItemId();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(AddStock.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddStock.this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void itemIdNotEntered() {
        Toast.makeText(this, "Please Enter Item Id", Toast.LENGTH_SHORT).show();
    }

    public void quantityNotEntered() {
        Toast.makeText(this, "Please Enter Quantity to add", Toast.LENGTH_SHORT).show();
    }

    public void invalidItemId() {
        Toast.makeText(this, "Invalid item id", Toast.LENGTH_SHORT).show();
    }

    public void showSuccess() {
        Toast.makeText(this, "Stock Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddStock.this, AdminHomePage.class);
        intent.putExtra("CALLINGACTIVITY", "AddStock1");
        startActivity(intent);
    }
}

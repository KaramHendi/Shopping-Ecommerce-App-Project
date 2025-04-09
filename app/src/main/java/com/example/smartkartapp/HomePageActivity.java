package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    Button clothing, electronics, books, otherItems;
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        clothing = findViewById(R.id.clothing);
        electronics = findViewById(R.id.electronics);
        books = findViewById(R.id.books);
        otherItems = findViewById(R.id.otherItems);

        // Get the role passed from the login activity
        userRole = getIntent().getStringExtra("USER_ROLE");

        // Handle different roles
        if (userRole != null) {
            if (userRole.equals("admin")) {
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
            } else if (userRole.equals("staff")) {
                Toast.makeText(this, "Welcome Staff", Toast.LENGTH_SHORT).show();
            } else if (userRole.equals("user")) {
                Toast.makeText(this, "Welcome User", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Role not found", Toast.LENGTH_SHORT).show();
        }

        clothing.setOnClickListener(v -> navigateToCategoryActivity(Clothing.class));
        electronics.setOnClickListener(v -> navigateToCategoryActivity(Electronics.class));
        books.setOnClickListener(v -> navigateToCategoryActivity(Books.class));
        otherItems.setOnClickListener(v -> navigateToCategoryActivity(OtherItems.class));
    }

    private void navigateToCategoryActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        intent.putExtra("USER_ROLE", userRole);  // Pass role again if needed
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HomePageActivity.this, RegLogChoice.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.customerOrders:
                // Navigate to the customer's order page
                startActivity(new Intent(this, CustomerOrderConfirmation.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home_page_drawer, menu);
        return true;
    }
}

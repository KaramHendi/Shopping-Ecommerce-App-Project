package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
                // Admin has full access, show admin-related features
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                // Additional setup for admin features if needed
            } else if (userRole.equals("staff")) {
                // Staff has limited access
                Toast.makeText(this, "Welcome Staff", Toast.LENGTH_SHORT).show();
                // You can limit staff access here by hiding/showing specific views
            } else if (userRole.equals("user")) {
                // Regular users have access to general features
                Toast.makeText(this, "Welcome ", Toast.LENGTH_SHORT).show();
                // Setup for user interface
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
}
package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    Button clothing, electronics, books, otherItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        clothing = findViewById(R.id.clothing);
        electronics = findViewById(R.id.electronics);
        books = findViewById(R.id.books);
        otherItems = findViewById(R.id.otherItems);

        final String sna = getIntent().getStringExtra("NAME");
        final String sph = getIntent().getStringExtra("PHONE");
        final String spa = getIntent().getStringExtra("PASSWORD");
        final String callingActivity = getIntent().getStringExtra("CALLINGACTIVITY");

        if ("RegisterPage".equals(callingActivity)) {
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        } else if ("LoginPage".equals(callingActivity)) {
            Toast.makeText(this, "Hello, " + sna + "!", Toast.LENGTH_SHORT).show();
        }

        clothing.setOnClickListener(v -> navigateToCategoryActivity(Clothing.class));
        electronics.setOnClickListener(v -> navigateToCategoryActivity(Electronics.class));
        books.setOnClickListener(v -> navigateToCategoryActivity(Books.class));
        otherItems.setOnClickListener(v -> navigateToCategoryActivity(OtherItems.class));
    }

    private void navigateToCategoryActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        intent.putExtra("NAME", getIntent().getStringExtra("NAME"));
        intent.putExtra("PHONE", getIntent().getStringExtra("PHONE"));
        intent.putExtra("PASSWORD", getIntent().getStringExtra("PASSWORD"));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HomePageActivity.this, LoginPage.class));
    }
}

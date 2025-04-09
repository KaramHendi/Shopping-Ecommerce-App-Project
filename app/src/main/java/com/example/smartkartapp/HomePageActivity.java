package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HomePageActivity extends AppCompatActivity {
    Button clothing, electronics, books, otherItems;
    String userRole;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize views
        clothing = findViewById(R.id.clothing);
        electronics = findViewById(R.id.electronics);
        books = findViewById(R.id.books);
        otherItems = findViewById(R.id.otherItems);
        fab = findViewById(R.id.fab);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Handle Navigation Drawer icon click
        toolbar.setNavigationIcon(R.drawable.ic_menu);  // Ensure you have ic_menu in your drawable
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.customerOrders) {
                if (userRole != null && userRole.equals("staff")) {
                    startActivity(new Intent(HomePageActivity.this, CustomerOrderConfirmation.class));
                } else {
                    Toast.makeText(HomePageActivity.this, "Access restricted", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

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

        // Navigate to different categories
        clothing.setOnClickListener(v -> navigateToCategoryActivity(Clothing.class));
        electronics.setOnClickListener(v -> navigateToCategoryActivity(Electronics.class));
        books.setOnClickListener(v -> navigateToCategoryActivity(Books.class));
        otherItems.setOnClickListener(v -> navigateToCategoryActivity(OtherItems.class));

        // Floating Action Button action
        fab.setOnClickListener(v -> Toast.makeText(this, "FAB Clicked", Toast.LENGTH_SHORT).show());
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
                if (userRole != null && userRole.equals("staff")) {
                    startActivity(new Intent(this, CustomerOrderConfirmation.class));
                } else {
                    Toast.makeText(this, "Access restricted", Toast.LENGTH_SHORT).show();
                }
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

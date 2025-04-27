package com.example.smartkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button clothing, electronics, books, otherItems;
    String userRole, userId, userName, userPhone;

    private DrawerLayout drawerLayout;
    private FloatingActionButton contactStaffFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(HomePageActivity.this, RegLogChoice.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home_page);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Category buttons
        clothing = findViewById(R.id.clothing);
        electronics = findViewById(R.id.electronics);
        books = findViewById(R.id.books);
        otherItems = findViewById(R.id.otherItems);

        // FloatingActionButton to contact staff
        contactStaffFab = findViewById(R.id.fab);

        // Get intent data
        userRole = getIntent().getStringExtra("USER_ROLE");
        userId = getIntent().getStringExtra("USER_ID");
        userName = getIntent().getStringExtra("USER_NAME");
        userPhone = getIntent().getStringExtra("USER_PHONE");

        // Default values for null cases
        if (userRole == null) userRole = "user";
        if (userId == null) userId = "";
        if (userName == null) userName = "Guest";
        if (userPhone == null) userPhone = "N/A";

        // Display welcome message based on role
        switch (userRole) {
            case "admin":
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                break;
            case "staff":
                Toast.makeText(this, "Welcome Staff", Toast.LENGTH_SHORT).show();
                break;
            case "user":
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Role not recognized", Toast.LENGTH_SHORT).show();
                break;
        }

        // Set category listeners
        clothing.setOnClickListener(v -> navigateToCategoryActivity(Clothing.class));
        electronics.setOnClickListener(v -> navigateToCategoryActivity(Electronics.class));
        books.setOnClickListener(v -> navigateToCategoryActivity(Books.class));
        otherItems.setOnClickListener(v -> navigateToCategoryActivity(OtherItems.class));

        // FAB listener
        contactStaffFab.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, SendMessageActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("USER_PHONE", userPhone);
            intent.putExtra("USER_ROLE", userRole);
            startActivity(intent);
        });
    }

    private void navigateToCategoryActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        intent.putExtra("USER_ROLE", userRole);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("USER_PHONE", userPhone);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra("USER_ROLE", userRole);
            profileIntent.putExtra("USER_ID", userId);
            profileIntent.putExtra("USER_NAME", userName);
            profileIntent.putExtra("USER_PHONE", userPhone);
            startActivity(profileIntent);
        } else if (id == R.id.nav_order_history) {
            Intent historyIntent = new Intent(this, OrderHistoryActivity.class);
            historyIntent.putExtra("USER_ROLE", userRole);
            historyIntent.putExtra("USER_PHONE", userPhone);
            historyIntent.putExtra("USER_ID", userId);
            startActivity(historyIntent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, RegLogChoice.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(this, RegLogChoice.class);
            i.putExtra("USER_ROLE", userRole);
            i.putExtra("USER_PHONE", userPhone);
            i.putExtra("USER_NAME", userName);
            i.putExtra("USER_ID", userId);
            startActivity(i);
            finish();
        }
    }
}

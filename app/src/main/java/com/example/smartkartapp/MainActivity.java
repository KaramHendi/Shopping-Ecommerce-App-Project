package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout to the main activity layout

        // Find views by their IDs
        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView textViewWelcome = findViewById(R.id.textViewOnboardWelcomeHeader);
        Button btnGetStarted = findViewById(R.id.btnMainGetStarted);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in); // Fade-in for the logo and text
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up); // Slide-up for the button

        // Apply animations
        imgLogo.startAnimation(fadeIn); // Logo fades in
        textViewWelcome.startAnimation(fadeIn); // Welcome text fades in
        btnGetStarted.startAnimation(slideUp); // Button slides up into view

        // Set onClickListener for the "Get Started" button
        btnGetStarted.setOnClickListener(v -> {
            // Create intent to navigate to the next activity
            Intent intent = new Intent(MainActivity.this, RegLogChoice.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Apply transition animation
            finish(); // Close the current activity to prevent back navigation
        });

    }
}

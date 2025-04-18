package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.tvPhone);

        // Load the animations
        Animation bounceFadeIn = AnimationUtils.loadAnimation(this, R.anim.bounce_fade_in); // For logo
        Animation zoomRotateFadeIn = AnimationUtils.loadAnimation(this, R.anim.zoom_rotate_fade_in); // For text
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation); // For scaling the image

        // Apply animations
        imageView.startAnimation(bounceFadeIn);
        imageView.startAnimation(scaleAnimation); // Apply the scaling animation to the image
        textView.startAnimation(zoomRotateFadeIn);

        // Timer to switch activity after the animation
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Create intent to start the next activity
                Intent intent = new Intent(MainActivity.this, RegLogChoice.class);

                // Start the activity with transition animations
                startActivity(intent);

                // Apply transition animation (slide in from right and slide out to left)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                finish(); // Prevent returning to this activity
            }
        }, 2500); // Wait for 2.5 seconds before starting the next activity
    }
}

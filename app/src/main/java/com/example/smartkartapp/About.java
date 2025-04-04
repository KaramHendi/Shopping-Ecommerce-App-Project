package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.URI;

public class About extends AppCompatActivity {
    Button fb,tw,gt,ig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        fb=findViewById(R.id.fb);
        gt=findViewById(R.id.gt);
        tw=findViewById(R.id.tw);
        ig=findViewById(R.id.ig);
        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/karam_hendi_/"));
                startActivity(intent);
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/profile.php?id=61568597606386&locale=ar_AR"));
                startActivity(intent);
            }
        });

        gt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/KaramHendi"));
                startActivity(intent);
            }
        });


        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://x.com/karamhendizd"));
                startActivity(intent);
            }
        });

    }
}
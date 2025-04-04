package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdminLogin extends AppCompatActivity {
    EditText admuser, admpass;
    Button admlog;
    TextView admstatus;

    private static final String ADMIN_USERNAME = "smartkart";
    private static final String ADMIN_PASSWORD = "appadmin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        admuser = findViewById(R.id.admuser);
        admpass = findViewById(R.id.admpass);
        admlog = findViewById(R.id.admlogin);
        admstatus = findViewById(R.id.admstatus);

        admstatus.setText("");

        admlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = admuser.getText().toString().trim();
                String password = admpass.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    admstatus.setText("Please enter all credentials");
                    return;
                }

                if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                    Intent intent = new Intent(AdminLogin.this, AdminHomePage.class);
                    intent.putExtra("CALLINGACTIVITY", "AdminLogin");
                    startActivity(intent);
                    finish(); // prevent going back to login
                } else {
                    admstatus.setText("Invalid credentials");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminLogin.this, RegLogChoice.class));
        finish();
    }
}

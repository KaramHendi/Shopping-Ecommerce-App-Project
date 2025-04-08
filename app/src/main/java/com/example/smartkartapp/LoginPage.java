package com.example.smartkartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    EditText phoneno, pass;
    Button login;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        phoneno = findViewById(R.id.logphone);
        pass = findViewById(R.id.logpass);
        login = findViewById(R.id.btnlogin);
        status = findViewById(R.id.tvstatus);
        status.setText("");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ph = phoneno.getText().toString().trim();
                String pa = pass.getText().toString().trim();

                if (TextUtils.isEmpty(ph) || TextUtils.isEmpty(pa)) {
                    status.setText("Please enter both phone and password");
                    return;
                }

                if (!ph.matches("^05\\d{8}$")) {
                    status.setText("Phone must be 10 digits and start with 05");
                    return;
                }

                FirebaseDatabase.getInstance().getReference("users")
                        .orderByChild("phone")
                        .equalTo(ph)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                                        MemberReg user = userSnap.getValue(MemberReg.class);

                                        if (user != null && user.getPassword().equals(pa)) {
                                            Intent i = new Intent(LoginPage.this, HomePageActivity.class);
                                            i.putExtra("USER_ROLE", user.getRole());
                                            i.putExtra("NAME", user.getUsername());
                                            i.putExtra("PHONE", user.getPhone());
                                            i.putExtra("PASSWORD", user.getPassword());
                                            startActivity(i);
                                            finish();
                                        } else {
                                            status.setText("Incorrect password");
                                        }
                                    }
                                } else {
                                    status.setText("Phone number not registered");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginPage.this, RegLogChoice.class));
        finish();
    }
}

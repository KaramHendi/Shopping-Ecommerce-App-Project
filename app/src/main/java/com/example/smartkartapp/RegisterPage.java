package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterPage extends AppCompatActivity {
    EditText etname, etphone, etpass;
    Button register;
    static DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        databaseUsers = FirebaseDatabase.getInstance().getReference("memberReg");

        register = findViewById(R.id.btnregister);
        etname = findViewById(R.id.etName);
        etphone = findViewById(R.id.etPhone);
        etpass = findViewById(R.id.etPassword);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });
    }

    public void reg() {
        String name = etname.getText().toString().trim();
        String phone = etphone.getText().toString().trim();
        String password = etpass.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number", Toast.LENGTH_SHORT).show();
        } else if (!phone.matches("^05\\d{8}$")) {
            Toast.makeText(this, "Phone must be 10 digits and start with 05", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else {
            // Check if phone already exists
            databaseUsers.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(RegisterPage.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        MemberReg memberReg = new MemberReg(phone, name, password, phone); // Use phone as ID
                        databaseUsers.child(phone).setValue(memberReg);
                        Toast.makeText(RegisterPage.this, "User registered", Toast.LENGTH_SHORT).show();

                        // Clear fields
                        etname.setText("");
                        etphone.setText("");
                        etpass.setText("");

                        // Go to HomePageActivity
                        Intent intent = new Intent(RegisterPage.this, HomePageActivity.class);
                        intent.putExtra("userPhone", phone);
                        startActivity(intent);
                        finish(); // optional: finish RegisterPage so user can't go back
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RegisterPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void getuser() {
        databaseUsers = FirebaseDatabase.getInstance().getReference("memberReg");
    }
}

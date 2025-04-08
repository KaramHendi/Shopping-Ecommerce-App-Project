package com.example.smartkartapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterPage extends AppCompatActivity {
    EditText etname, etphone, etpass;
    Button register;
    FirebaseAuth mAuth;
    static DatabaseReference databaseUsers;

    public static void getuser() {
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        etname = findViewById(R.id.etName);
        etphone = findViewById(R.id.etPhone);
        etpass = findViewById(R.id.etPassword);
        register = findViewById(R.id.btnregister);

        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        register.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etname.getText().toString().trim();
        String phone = etphone.getText().toString().trim();
        String password = etpass.getText().toString().trim();
        String role;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("^05\\d{8}$")) {
            Toast.makeText(this, "Phone must be 10 digits and start with 05", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is admin
        if (name.equals("smartkart") && phone.equals("0587654321") && password.equals("appadmin123")) {
            role = "admin";
        } else {
            // Check if phone is in the list of staff numbers from the database
            if (isStaffPhoneNumber(phone)) {
                role = "staff";
            } else {
                // Default role for all others is "user"
                role = "user";
            }
        }

        register.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(phone + "@smartkart.com", password)
                .addOnCompleteListener(task -> {
                    register.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            MemberReg member = new MemberReg(phone, name, password, phone, role);
                            databaseUsers.child(uid).setValue(member)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Registered as " + role, Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(RegisterPage.this, HomePageActivity.class);
                                            intent.putExtra("userPhone", phone);
                                            intent.putExtra("userRole", role);
                                            intent.putExtra("CALLINGACTIVITY", "RegisterPage");
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to store user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method to check if the phone number belongs to staff
    private boolean isStaffPhoneNumber(final String phone) {
        final boolean[] isStaff = {false}; // Using an array to hold value from the async query

        // Query the Firebase database for users with the same phone number
        databaseUsers.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isStaff[0] = true; // If the phone number exists, it's a staff number
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterPage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Return the result after the Firebase query completes
        return isStaff[0];
    }
}

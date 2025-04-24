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

        // Validate the input fields
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

        // Determine the user's role asynchronously
        assignUserRole(phone, name, password);
    }

    private void assignUserRole(final String phone, final String name, final String password) {
        // Initialize default role
        String role;

        // Check if the user is an admin
        if (name.equals("smartkart") && phone.equals("0587654321") && password.equals("appadmin123")) {
            role = "admin";
            registerUserInFirebase(phone, name, password, role);
        } else {
            // Check if the phone number belongs to a staff member
            checkIfStaff(phone, name, password);
        }
    }

    private void checkIfStaff(final String phone, final String name, final String password) {
        databaseUsers.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.exists() ? "staff" : "user";
                registerUserInFirebase(phone, name, password, role);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterPage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUserInFirebase(String phone, String name, String password, String role) {
        register.setEnabled(false);

        // Create the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(phone + "@smartkart.com", password)
                .addOnCompleteListener(task -> {
                    register.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // Create the MemberReg object and save it to Firebase Database
                            MemberReg member = new MemberReg(phone, name, password, role);
                            databaseUsers.child(uid).setValue(member)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Registered as " + role, Toast.LENGTH_SHORT).show();

                                            // Pass user data to HomePageActivity
                                            Intent intent = new Intent(RegisterPage.this, HomePageActivity.class);
                                            intent.putExtra("USER_ROLE", role);  // Pass the user role
                                            intent.putExtra("USER_ID", phone);   // Pass the user ID
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
}

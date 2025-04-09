package com.example.smartkartapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private EditText phoneNumber, otpInput;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth mAuth;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI elements
        phoneNumber = findViewById(R.id.phoneNumber);
        otpInput = findViewById(R.id.otpInput);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(PhoneAuthActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                } else {
                    sendOtp(phone);
                }
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpInput.getText().toString();
                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(PhoneAuthActivity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOtp(otp);
                }
            }
        });
    }

    // Method to send OTP
    private void sendOtp(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)  // Timeout duration
                .setActivity(this)           // Current activity
                .setCallbacks(mCallbacks)   // On verification state changed
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callback for phone number verification
    private OnVerificationStateChangedCallbacks mCallbacks = new OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // If verification is successful, sign in with the credential
            mAuth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(PhoneAuthActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneAuthActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                            // Navigate to the next screen (e.g., main activity)
                        } else {
                            Toast.makeText(PhoneAuthActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthActivity.this, "OTP Verification Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            PhoneAuthActivity.this.verificationId = verificationId;
            Toast.makeText(PhoneAuthActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
        }
    };

    // Method to verify OTP entered by user
    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    // Sign in with PhoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Navigate to the next screen
                        Toast.makeText(PhoneAuthActivity.this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                        // Example: You can navigate to the main activity here
                        // startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(PhoneAuthActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

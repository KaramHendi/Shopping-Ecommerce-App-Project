package com.example.smartkartapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendMessageActivity extends AppCompatActivity {
    EditText messageInput;
    Button sendButton;
    DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Firebase reference for messages
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(SendMessageActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
        } else {
            // Push the message to Firebase
            String messageId = messagesRef.push().getKey(); // Create a unique key for the message
            if (messageId != null) {
                messagesRef.child(messageId).setValue(message);
                messageInput.setText(""); // Clear the input after sending

                // Optional: Show a confirmation message
                Toast.makeText(SendMessageActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

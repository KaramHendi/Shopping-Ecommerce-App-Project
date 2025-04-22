package com.example.smartkartapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewMessagesActivity extends AppCompatActivity {
    TextView messagesTextView;
    DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messagesTextView = findViewById(R.id.messagesTextView);

        // Initialize Firebase reference to the "messages" node
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Fetch messages from Firebase Realtime Database
        fetchMessages();
    }

    private void fetchMessages() {
        // Attach a listener to read the messages
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder messages = new StringBuilder();

                // Loop through all the messages in the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String message = snapshot.getValue(String.class);  // Assuming each message is a simple String
                    messages.append(message).append("\n");
                }

                // Set the messages to the TextView
                if (messages.length() > 0) {
                    messagesTextView.setText(messages.toString());
                } else {
                    messagesTextView.setText("No messages available.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Show an error message if the database read fails
                Toast.makeText(ViewMessagesActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

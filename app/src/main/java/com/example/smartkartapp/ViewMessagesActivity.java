package com.example.smartkartapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewMessagesActivity extends AppCompatActivity {
    ListView messagesListView;
    DatabaseReference messagesRef;
    ArrayList<String> messagesList;
    ArrayList<String> messageKeys;
    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messagesListView = findViewById(R.id.messagesListView);
        messagesList = new ArrayList<>();
        messageKeys = new ArrayList<>();

        adapter = new MessageAdapter(this, messagesList, messageKeys);
        messagesListView.setAdapter(adapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        fetchMessages();
    }

    private void fetchMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messagesList.clear();
                messageKeys.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String message = child.getValue(String.class);
                    messagesList.add(message);
                    messageKeys.add(child.getKey());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewMessagesActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

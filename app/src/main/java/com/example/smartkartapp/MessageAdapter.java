package com.example.smartkartapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<String> {
    private final List<String> keys;

    public MessageAdapter(Context context, List<String> messages, List<String> keys) {
        super(context, 0, messages);
        this.keys = keys;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        TextView messageText = convertView.findViewById(R.id.messageTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        String message = getItem(position);
        messageText.setText(message);

        deleteButton.setOnClickListener(v -> {
            String keyToDelete = keys.get(position);
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
            messagesRef.child(keyToDelete).removeValue()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), "Message deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show());
        });

        return convertView;
    }
}

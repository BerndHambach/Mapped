package com.example.mapped.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.mapped.MessagesAdapter;
import com.example.mapped.MessageModel;
import com.example.mapped.R;
import com.example.mapped.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Chat extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseDatabase fbdatabase;
    DatabaseReference dbreferenceSender, dbreferenceReciever;

    //private int generatedChatKey;
    //private String chatKey;
   // String getUserMobile = "";
    String recieverId;
    String senderRoom, recieverRoom;

    MessagesAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recieverId = getIntent().getStringExtra("id");

        senderRoom = FirebaseAuth.getInstance().getUid()+recieverId;
        recieverRoom = recieverId+FirebaseAuth.getInstance().getUid();

       // messageAdapter = new MessagesAdapter(this);
        binding.recyclerChat.setAdapter(messageAdapter);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        fbdatabase = FirebaseDatabase.getInstance();
        dbreferenceSender = fbdatabase.getReference("chats").child(senderRoom);
        dbreferenceReciever = fbdatabase.getReference("chats").child(recieverRoom);

       /* dbreferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        /*binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEditText.getText().toString();
                if (message.trim().length()>0){
                    sendMessage(message);
                }
            }
        });*/

    }

    private void sendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), message);

       // messageAdapter.add(messageModel);

        dbreferenceSender
                .child(messageId)
                .setValue(messageModel);
        dbreferenceReciever
                .child(messageId)
                .setValue(messageModel);
    }
}
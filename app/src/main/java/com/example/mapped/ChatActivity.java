package com.example.mapped;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private UserModel current_ChatPartner;
    private TextView chat_Name;
    private ImageView iv_back;
    private CircleImageView civ_sendbtn;
    private EditText chat_inputText;

    private FirebaseAuth mAuth;
    private String messageSenderID, messageReceiverID;

    private DatabaseReference rootRef;

    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private RecyclerView userMessagesRecylerList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_Name = (TextView) findViewById(R.id.name);
        iv_back =(ImageView) findViewById(R.id.chat_backBtn);
        civ_sendbtn = (CircleImageView) findViewById(R.id.chats_sendBtn);
        chat_inputText = (EditText) findViewById(R.id.chats_EditText);

        rootRef = FirebaseDatabase.getInstance().getReference();
        current_ChatPartner = (UserModel) getIntent().getSerializableExtra("CURRENT_CHAT_PARTNER");

        userMessagesRecylerList = (RecyclerView) findViewById(R.id.recycler_chat);


        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesRecylerList.setLayoutManager(linearLayoutManager);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesRecylerList.setAdapter(messagesAdapter);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

       chat_Name.setText(current_ChatPartner.getUserName().toString());
        messageReceiverID = current_ChatPartner.getUserId().toString();
        civ_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendChatMessage();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Messages messages = snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messagesAdapter.notifyDataSetChanged();

                        userMessagesRecylerList.smoothScrollToPosition(userMessagesRecylerList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendChatMessage (){

        String messageText = chat_inputText.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "First write your message", Toast.LENGTH_SHORT).show();

        }
        else {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                    chat_inputText.setText("");
                }
            });
        }

    }

}
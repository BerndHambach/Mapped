package com.example.mapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfilePage extends AppCompatActivity {

    private UserModel userModel;

    private TextView userName, userInfo;
    private CircleImageView userProfilePic;

    private ImageButton btn_back;

    private Button btn_SendMessage, btn_declineMessageRequeset;

    private String current_State;
    private String sender_UserID, receiver_UserID;
    private FirebaseAuth mAuth;
    private DatabaseReference chatRequestRef, contactsRef, notificationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        userModel = (UserModel) getIntent().getSerializableExtra("USER_MODEL");

        userName = findViewById(R.id.tv_userProfileName);
        userProfilePic = findViewById(R.id.iv_userProfilePic);

        userInfo = findViewById(R.id.tv_userInfo);
        btn_back = findViewById(R.id.btn_back2);
        btn_SendMessage = findViewById(R.id.send_message_request_button);
        btn_declineMessageRequeset = findViewById(R.id.decline_message_request_button);


        current_State = "new";
        mAuth = FirebaseAuth.getInstance();
        sender_UserID = mAuth.getCurrentUser().getUid();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        receiver_UserID = userModel.getUserId();

        userName.setText(userModel.getUserName());
        Uri uri = Uri.parse(userModel.getUserProfilPhotoUrl());
        Picasso.get().load(uri).into(userProfilePic);
        userName.setText("Über " + userModel.getUserName());
        userInfo.setText(userModel.getUserInfo());

        ManageChatRequest();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void ManageChatRequest() {

        chatRequestRef.child(sender_UserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild(receiver_UserID)){
                            String request_type = snapshot.child(receiver_UserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                current_State = "request_sent";
                                btn_SendMessage.setText("Chancel Chat Request");
                            }
                            else if(request_type.equals("received")){

                                current_State = "request_received";
                                btn_SendMessage.setText("Accept Chat Request");

                                btn_declineMessageRequeset.setVisibility(View.VISIBLE);
                                btn_declineMessageRequeset.setEnabled(true);
                                btn_declineMessageRequeset.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        ChancelChatRequest();
                                    }
                                });
                            }
                        }
                        else {
                            contactsRef.child(sender_UserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.hasChild(receiver_UserID)){
                                                current_State = "friends";
                                                btn_SendMessage.setText("Remove this Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (!sender_UserID.equals(receiver_UserID)) {

            btn_SendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btn_SendMessage.setEnabled(false);

                    if(current_State.equals("new")){
                        SendChatRequest();
                    }
                    if(current_State.equals("request_sent")){
                        ChancelChatRequest();
                    }
                    if(current_State.equals("request_received")){
                        AcceptChatRequest();
                    }
                    if(current_State.equals("friends")){
                        RemoveSpezificContact();
                    }
                    
                }
            });
        }
        else {
            btn_SendMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpezificContact() {
        contactsRef.child(sender_UserID).child(receiver_UserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            contactsRef.child(receiver_UserID).child(sender_UserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                btn_SendMessage.setEnabled(true);
                                                current_State = "new";
                                                btn_SendMessage.setText("Send Message");

                                                btn_declineMessageRequeset.setVisibility(View.INVISIBLE);
                                                btn_declineMessageRequeset.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void AcceptChatRequest() {
        contactsRef.child(sender_UserID).child(receiver_UserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            contactsRef.child(receiver_UserID).child(sender_UserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                chatRequestRef.child(sender_UserID).child(receiver_UserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){
                                                                    chatRequestRef.child(receiver_UserID).child(sender_UserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                btn_SendMessage.setEnabled(true);
                                                                                current_State = "friends";
                                                                                btn_SendMessage.setText("Remove this Contact");

                                                                                btn_declineMessageRequeset.setVisibility(View.INVISIBLE);
                                                                                btn_declineMessageRequeset.setEnabled(false);

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void ChancelChatRequest() {

        chatRequestRef.child(sender_UserID).child(receiver_UserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            chatRequestRef.child(receiver_UserID).child(sender_UserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                btn_SendMessage.setEnabled(true);
                                                current_State = "new";
                                                btn_SendMessage.setText("Send Message");

                                                btn_declineMessageRequeset.setVisibility(View.INVISIBLE);
                                                btn_declineMessageRequeset.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {

        chatRequestRef.child(sender_UserID).child(receiver_UserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            chatRequestRef.child(receiver_UserID).child(sender_UserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", sender_UserID);
                                                chatNotificationMap.put("type", "request");

                                                notificationRef.child(receiver_UserID).push()
                                                                .setValue(chatNotificationMap)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful()){
                                                                                    btn_SendMessage.setEnabled(true);
                                                                                    current_State = "request_sent";
                                                                                    btn_SendMessage.setText("Cancel Chat Request");
                                                                                }
                                                                            }
                                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
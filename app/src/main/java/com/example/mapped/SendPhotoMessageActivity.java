package com.example.mapped;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendPhotoMessageActivity extends AppCompatActivity {

    public CircleImageView civ_back, civ_sendphotomessage;
    public Button btn_addcontact;
    public ImageView iv_showphoto;
    public EditText et_getaddedmessage;

    public UserModel current_ChatPartner;
    private String messageSenderID, messageReceiverID;
    private DatabaseReference rootRef;
    public Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);

        civ_back = (CircleImageView) findViewById(R.id.civ_backsendphoto);
        civ_sendphotomessage = (CircleImageView) findViewById(R.id.civ_sendphoto);
        btn_addcontact = (Button) findViewById(R.id.btn_sendtomorecontacts);
        iv_showphoto = (ImageView) findViewById(R.id.iv_sendphoto);
        et_getaddedmessage = (EditText) findViewById(R.id.et_getphotomessagetext);

        rootRef = FirebaseDatabase.getInstance().getReference();


        current_ChatPartner = (UserModel) getIntent().getSerializableExtra("CURRENT_CHAT_PARTNER");
        messageReceiverID = current_ChatPartner.getUserId();
        messageSenderID = getIntent().getStringExtra("MESSAGE_SENDER_ID");
        Intent callingActivityIntent = getIntent();

        if(callingActivityIntent != null) {
            imageUri = callingActivityIntent.getData();
            if(imageUri != null && iv_showphoto != null) {
                Glide.with(this)
                        .load(imageUri)
                        .into(iv_showphoto);
            }
        }

        civ_sendphotomessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendChatPhotoMessage();
            }
        });



    }
    private void SendChatPhotoMessage () {

        String messageText = et_getaddedmessage.getText().toString();



        String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
        String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

        DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

        String messagePushID = userMessageKeyRef.getKey();

        MessageModel messageModel = new MessageModel();
        GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date gregnow = gregCalendar.getTime();

        if(TextUtils.isEmpty(messageText)) {

            messageModel.setFrom(messageSenderID);
            messageModel.setType("photo");
            messageModel.setIsseen(false);
            messageModel.setPhotoUri(imageUri.toString());
            messageModel.setTime(gregnow);

        } else {
            messageModel.setFrom(messageSenderID);
            messageModel.setMessage(messageText);
            messageModel.setType("photo and text");
            messageModel.setIsseen(false);
            messageModel.setPhotoUri(imageUri.toString());
            messageModel.setTime(gregnow);

        }

        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).child(messagePushID).setValue(messageModel).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    rootRef.child("Messages").child(messageReceiverID).child(messageSenderID).child(messagePushID)
                            .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SendPhotoMessageActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                        et_getaddedmessage.setText("");
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SendPhotoMessageActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
            }
        });
    }
}


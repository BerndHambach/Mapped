package com.example.mapped.ui.messaging.chats;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// import com.devlomi.record_view.RecordButton;
// import com.devlomi.record_view.RecordView;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.mapped.MessageModel;
import com.example.mapped.ui.messaging.MessagesAdapter;
import com.example.mapped.R;
import com.example.mapped.SendPhotoMessageActivity;
import com.example.mapped.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private static final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    private UserModel current_ChatPartner;
    private TextView chat_Name;
    private ImageView iv_back;
    private CircleImageView civ_sendbtn, civ_choosePhoto, civ_chooseVideo;
    private EditText chat_inputText;

    private FirebaseAuth mAuth;
    private String messageSenderID, messageReceiverID;

    private DatabaseReference rootRef, userRef, seenRef;

    private final List<MessageModel> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private RecyclerView userMessagesRecylerList;
    public String currentPhotoPath = null;
    public File takenPhotoFile = null;
    public Uri takenPhotoUri = null;
    public String takenPhotoUriString = null;

    public RecordButton btn_record;
    public RecordView record_view;

    public MediaRecorder mediaRecorder;

    public String audioPath;

    private String mLocalFilePath = null;



    ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_Name = (TextView) findViewById(R.id.name);
        iv_back =(ImageView) findViewById(R.id.chat_backBtn);
        civ_sendbtn = (CircleImageView) findViewById(R.id.chats_sendBtn);
        chat_inputText = (EditText) findViewById(R.id.chats_EditText);
        civ_choosePhoto = (CircleImageView) findViewById(R.id.cv_chooseFoto);
        civ_chooseVideo = (CircleImageView) findViewById(R.id.cv_chooseVideo);
        btn_record = (RecordButton) findViewById(R.id.btn_record);
        record_view = (RecordView) findViewById(R.id.recordView);

        rootRef = FirebaseDatabase.getInstance().getReference();
        current_ChatPartner = (UserModel) getIntent().getSerializableExtra("CURRENT_CHAT_PARTNER");

        userMessagesRecylerList = (RecyclerView) findViewById(R.id.recycler_chat);
        userMessagesRecylerList.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
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

        civ_choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();

            }
        });

        btn_record.setRecordView(record_view);
        btn_record.setListenForRecord(false);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAudioPermissions();
            }
        });


        seenMessage();
    }

    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        audioPath = getExternalCacheDir().getAbsolutePath();
        audioPath += "/audiorecordtest.3gp";
       /* File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Mapped/Media/Recording");

        if(!file.exists())
            file.mkdir();
        audioPath = file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".3gp";
*/
        mediaRecorder.setOutputFile(audioPath);
    }

    private void sendRecordingMessage(String audioPath) {
       // StorageReference storageReference = FirebaseStorage.getInstance().getReference("/Media/Recording/" + messageSenderID + "/" + System.currentTimeMillis());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("/Audio");

        Uri audioFile = Uri.fromFile(new File(audioPath));
        storageReference.putFile(audioFile).addOnSuccessListener(success -> {
            Task<Uri> audioUrl = success.getStorage().getDownloadUrl();

            audioUrl.addOnCompleteListener(path -> {
                if(path.isSuccessful()){

                    String url = path.getResult().toString();
                   // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(messageSenderID);
                    DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                            .child(messageSenderID).child(messageReceiverID).push();

                    String messagePushID = userMessageKeyRef.getKey();

                    GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
                    Date gregnow = gregCalendar.getTime();

                    MessageModel messageModel = new MessageModel(messageSenderID, "audio", false, url, gregnow);

                    rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).child(messagePushID)
                            .setValue(messageModel).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                                rootRef.child("Messages").child(messageReceiverID).child(messageSenderID).child(messagePushID)
                                        .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ChatActivity.this, "Audio Sent Successfully", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(ChatActivity.this, "Audio failed", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        MessageModel message = snapshot.getValue(MessageModel.class);
                        messagesList.add(message);
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

        GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date gregnow = gregCalendar.getTime();

        String s = "s";

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

            MessageModel messageModel = new MessageModel(messageSenderID, messageText, "text", false, gregnow);
/*
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("isseen", false);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);*/

            rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).child(messagePushID).setValue(messageModel).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        rootRef.child("Messages").child(messageReceiverID).child(messageSenderID).child(messagePushID)
                                .setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                    chat_inputText.setText("");
                }
            });
        }

    }

    private void seenMessage(){
        seenRef = FirebaseDatabase.getInstance().getReference("Messages").child(mAuth.getUid()).child(current_ChatPartner.getUserId());
        seenListener = seenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if(messageModel.getFrom().toString() == current_ChatPartner.getUserId() && messageModel.isIsseen() == false) {
                        messageModel.setIsseen(true);
                        String messageKey = seenRef.getKey();

                        Map isseen = new HashMap();
                        isseen.put("isseen", true);
                        DatabaseReference updateIsseenRef = FirebaseDatabase.getInstance().getReference("Messages").child(current_ChatPartner.getUserId()).child(mAuth.getUid()).child(messageKey);
                        updateIsseenRef.updateChildren(isseen);
                        updateIsseenRef.setValue(messageModel);

                    }




                }
                Map messageTextBody = new HashMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(current_ChatPartner.getUserId());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);

        userRef.updateChildren(hashMap);
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            recordAudio();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == MY_PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                recordAudio();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                takenPhotoFile = new File (currentPhotoPath);
                // selectedImage.setImageURI(Uri.fromFile(f));
                //  selectedImage.setVisibility(View.VISIBLE);
                Log.d("tag", "Absolute Url of Imagea is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                takenPhotoUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                sendToSendPhotoActivity(takenPhotoUri);



               /* selectedImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onImageClicked(takenPhotoUri);
                    }

                });*/

            }
        }
       /* if(requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                cameraUri = data.getData();
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName ="JPEG_" + timeStamp + "."+getFileExt(contentUri);
                selectedImage.setImageURI(contentUri);
            }
        }*/
        /*if (resultCode == RESULT_OK && requestCode == 1) {
            // on below line setting video uri for our video view.
            //  videoView.setVideoURI(data.getData());
            videouri = data.getData();
            videoView.setVisibility(View.VISIBLE);
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onVideoViewClicked(data.getData());
                }
            });
        }
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videouri = data.getData();
            String path = videouri.toString();
            videoView.setVisibility(View.VISIBLE);
            *//*Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                    MediaStore.Images.Thumbnails.DATA);
            videoView.setImageBitmap(thumb);*//*
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onVideoViewClicked(videouri);
                }
            });}
*/

    }
    public void recordAudio() {

        btn_record.setListenForRecord(true);
        record_view.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                setUpRecording();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                record_view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();
                record_view.setVisibility(View.GONE);
                sendRecordingMessage(audioPath);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.

                mediaRecorder.stop();
                mediaRecorder.release();
                record_view.setVisibility(View.GONE);
                sendRecordingMessage(audioPath);

            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists())
                    file.delete();

                mediaRecorder.reset();
                mediaRecorder.release();

                record_view.setVisibility(View.GONE);
            }

            @Override
            public void onLock() {
                //When Lock gets activated
                Log.d("RecordView", "onLock");
            }

        });

    }

    public void sendToSendPhotoActivity(Uri imageUri) {
        Intent sendPhotoIntent = new Intent(this, SendPhotoMessageActivity.class);
        sendPhotoIntent.setData(imageUri);
        sendPhotoIntent.putExtra("MESSAGE_SENDER_ID", messageSenderID);
        sendPhotoIntent.putExtra("CURRENT_CHAT_PARTNER", current_ChatPartner);
        startActivity(sendPhotoIntent);
    }
    private void dispatchTakePictureIntent() {

        //Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // rootRef.removeEventListener(seenListener);
        status("offline");
    }
}
package com.example.mapped;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUserInfo extends AppCompatActivity {

    EditText userInfo;
    Button btn_saveUserInfo, btn_back;
    private FirebaseDatabase mdb;
    private DatabaseReference mdbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        userInfo = findViewById(R.id.et_userInfo);
        btn_saveUserInfo = findViewById(R.id.btn_saveUserInfo);
        btn_back = findViewById(R.id.btn_back);
        String userID = getIntent().getStringExtra("userID");

        mdb = FirebaseDatabase.getInstance();
        mdbref = mdb.getReference("users");


        btn_saveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mdbref.child(userID).child("userInfo").setValue(userInfo.getText().toString());
                //mdbref.updateChildren(userInfo.getText().toString());
                //DatabaseReference dbref = fbDatabase.getReference("users");
                //dbref.child(userID).child("userProfilPhotoUrl").setValue(mUri.toString());
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
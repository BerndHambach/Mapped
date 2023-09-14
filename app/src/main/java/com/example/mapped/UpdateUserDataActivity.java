package com.example.mapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUserDataActivity extends AppCompatActivity {

    private Button btn_UpdateUsersData, btn_back;
    private EditText userName, userInfo;

    public UserModel currentUser;

    FirebaseDatabase fbdb;
    DatabaseReference dbrf;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_data);

        currentUser = (UserModel) getIntent().getSerializableExtra("USER_MODEL");

        fbdb = FirebaseDatabase.getInstance();
        dbrf = fbdb.getReference("users").child(currentUser.getUserId());

        InitializeFields();

        btn_UpdateUsersData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUserData();
                finish();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        userName.setText(currentUser.getUserName());
        userInfo.setText(currentUser.getUserInfo());
    }

    private void InitializeFields() {

        btn_UpdateUsersData = (Button) findViewById(R.id.btn_updateUserData);
        userName = (EditText) findViewById(R.id.et_updateUserName);
        userInfo = (EditText) findViewById(R.id.et_updateUserInfo);
        btn_back = (Button) findViewById(R.id.btn_backUpdateUserData);
    }

    private void UpdateUserData() {

        String setUserName = userName.getText().toString();
        String setUserInfo = userInfo.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your user name first...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserInfo)){
            Toast.makeText(this, "Please write your user info first...", Toast.LENGTH_SHORT).show();
        }
        else {
            dbrf.child("userInfo").setValue(setUserInfo);
            dbrf.child("userName").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()) {
                        Toast.makeText(UpdateUserDataActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(UpdateUserDataActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
package com.example.mapped.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapped.UserModel;
import com.example.mapped.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAccount;
    EditText inputName, inputEmail, inputPassword, inputConformPassword, inputMobile;
    Button btnRegister;
    String emailPattern =  "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    public String name, email, password, comformPassword;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    ActivityRegisterBinding binding;

    private FirebaseDatabase mfbdatabase;
    private DatabaseReference mfbreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressDialog = new ProgressDialog(RegisterActivity.this);

        mfbdatabase = FirebaseDatabase.getInstance();
        mfbreference = mfbdatabase.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        binding.alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.inputName.getText().toString();
                email = binding.inputEmail.getText().toString();
                password = binding.inputPassword.getText().toString();
                comformPassword = binding.inputConformPassword.getText().toString();

                signIn();
            }
        });
    }

    private void signIn() {

        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.updateProfile(userProfileChangeRequest);
                        UserModel userModel = new UserModel(FirebaseAuth.getInstance().getUid(),name, email, password, "", "", "offline", name.toLowerCase(),"");
                        mfbreference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);

                        String currentUserID = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseMessaging.getInstance().getToken().toString();

                        mfbreference.child(currentUserID).child("device_token")
                                        .setValue(deviceToken);
                        sendUserToNextActivity();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
        /*mfbreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("users").hasChild(mobile)){
                    Toast.makeText(RegisterActivity.this, "Mobile already exists", Toast.LENGTH_SHORT).show();
                }
                else{
                    mfbreference.child("users").child(mobile).child("name").setValue(name);
                    mfbreference.child("users").child(mobile).child("email").setValue(email);
                    mfbreference.child("users").child(mobile).child("profilePictureUrl").setValue("");
                    Toast.makeText(RegisterActivity.this, "Name saved in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       // if(email.matches(emailPattern))
        if(email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError("Enter Correct Email");
        } else if(password.isEmpty() || password.length()<6)
        {
           inputPassword.setError("Enter Proper Password");
        }else if (!password.equals(conformPassword))
        {
            inputConformPassword.setError("Password not match both fields");
        }else
        {
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit().putString("MYMOBILE", mobile).apply();
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this, "Registration sucessful", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("name", name);
        intent.putExtra("email", email);

        startActivity(intent);
        finish();
    }
}
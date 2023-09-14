package com.example.mapped.ui.MeinProfil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;


import com.example.mapped.CreateHangoutActivity;
import com.example.mapped.MainActivity;
import com.example.mapped.R;
import com.example.mapped.UpdateUserDataActivity;
import com.example.mapped.UpdateUserInfo;
import com.example.mapped.UserModel;
import com.example.mapped.UserProfilePage;
import com.example.mapped.databinding.FragmentProfilBinding;
import com.example.mapped.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment {

    private FragmentProfilBinding binding;
    private Button logoutbtn;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;

    private Uri filePath;

    private Uri imageUri;

    FirebaseStorage storage;
    StorageReference storageReference;

   // private ImageView profilePicImageView;

    int SELECT_PICTURE = 3;

    private final int PIC_IMAGE_REQUEST = 71;

    public String userID;

    FirebaseDatabase fbDatabase;
    DatabaseReference dbReference;

    CircleImageView profilePicImageView;

    public UserModel userModel;

    TextView userName;
    TextView tvPfbearbeiten;
    TextView userInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentProfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        profilePicImageView = root.findViewById(R.id.profilePicImageView);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fbAuth = FirebaseAuth.getInstance();
        user = fbAuth.getCurrentUser();

        userID = user.getUid();

        fbDatabase = FirebaseDatabase.getInstance();
        dbReference = fbDatabase.getReference("users").child(userID);

        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);

                Uri uri = Uri.parse(userModel.getUserProfilPhotoUrl());
                Picasso.get().load(uri).into(profilePicImageView);

                binding.userProfileName.setText(userModel.getUserName());

                binding.userInfo.setText(userModel.getUserInfo());

                Toast.makeText(root.getContext(), "Got User", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.btnUpdateUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UpdateUserInfo.class);
                intent.putExtra("userID", userID);
              //  intent.putExtra("longitude", p.getLongitude());
                startActivity(intent);
            }
        });

        binding.tvUpdataUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UpdateUserDataActivity.class);
                intent.putExtra("USER_MODEL", userModel);
                //  intent.putExtra("longitude", p.getLongitude());
                startActivity(intent);
            }
        });
        binding.tvProfilfotobearbeiten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // imageChooser();
                chooseImage();
              //  updateUserProfilePic();

                //binding.profilePicImageView.setImageURI(filePath);
            }
        });

        binding.conlayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfilePage.class);
                intent.putExtra("USER_MODEL", userModel);
                //  intent.putExtra("longitude", p.getLongitude());
                startActivity(intent);
            }
        });



       // fbDatabase = FirebaseDatabase.getInstance();
       // dbReference = fbDatabase.getReference("users").child(mobile);

        logoutbtn = root.findViewById(R.id.btn_logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(root.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }

    private void updateUserProfilePic(Uri mUri) {

        String userID = user.getUid();

        DatabaseReference dbref = fbDatabase.getReference("users");
        dbref.child(userID).child("userProfilPhotoUrl").setValue(mUri.toString());
       }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PIC_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {

                imageUri = data.getData();
                //Uri selectedImageUri = data.getData();
                filePath = data.getData();

                //Uri imageUri = data.getData();

                profilePicImageView.setImageURI(imageUri);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
           // String imageFileName ="JPEG_" + timeStamp + "."+getFileExt(selectedImageUri);
                    // update the preview image in the layout

                uploadImageToFirebaseStorage(imageUri);
                updateUserProfilePic(imageUri);
                //dbReference.child("profilePictures").push().setValue(selectedPicFromGalleryUri.toString());
                //dbReference.child("profilePictureUrl").push().setValue(selectedPicFromGalleryUri.toString());
        }
    }
    private void uploadImageToFirebaseStorage(Uri uri) {

                ProgressDialog progressDialog = new ProgressDialog(getView().getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                // Defining the child of storageReference
                //StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                // adding listeners on upload
                // or failure of image
                ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                             updateUserProfilePic(uri);
                           }
                       });
                        // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss();
                        Toast.makeText(getView().getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(getView().getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            // Progress Listener for loading
            // percentage on the dialog box
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }
        });
    }
    private String getFileExtension(Uri contentUri) {

        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
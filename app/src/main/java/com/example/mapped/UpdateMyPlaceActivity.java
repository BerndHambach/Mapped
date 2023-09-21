package com.example.mapped;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateMyPlaceActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    String currentPhotoPath = "";
    public File takenPhotoFile;
    public Uri takenPhotoUri;
    int starthour = 0, startminute = 0, endhour = 0, endminute = 0;
    public String startTime = "";
    public String endTime = "";
    public String date = "";
    public String selectedCategorie;
    AlertDialog selectCategorieAlertDialog;

    Calendar calendar;
    int year, month, dayOfMonth;
    DatePickerDialog datePickerDialog;

    public Button btnStartTimeButton, btnEndTimeButton, btnSelectDate, btnUpdatePlace, btnFoto, btnGallery, btnCategorie;

    public AppCompatEditText placeTitle, placeDescription;
    public ImageView placeImage;

    public String imageUri;
    public ImageButton btn_back, ib_savePlace;

    PlaceModel place, favoriteplace;
    FirebaseDatabase fbdb;
    DatabaseReference dbrf, placesRef, myPlacesRef;
    private StorageReference mstorageReference;
    private FirebaseStorage mfirebaseStorage;


    FirebaseAuth mAuth;
    String currentUserId;

    ConstraintLayout layout_Profile;
    TextView userName;
    CircleImageView userImage;
    String favoritekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_my_place);

        place = (PlaceModel) getIntent().getSerializableExtra("PLACE_MODEL");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();

        mfirebaseStorage = FirebaseStorage.getInstance();
        mstorageReference = mfirebaseStorage.getReference();

        fbdb = FirebaseDatabase.getInstance();
        dbrf = fbdb.getReference("users");
        placesRef = FirebaseDatabase.getInstance().getReference("Places");
        myPlacesRef = FirebaseDatabase.getInstance().getReference("Users Places").child(currentUserId).child("My Places");


        placeTitle = (AppCompatEditText) findViewById(R.id.myplaceTitle);
        placeDescription = (AppCompatEditText) findViewById(R.id.myplaceDescription);

        btnSelectDate = findViewById(R.id.update_dateButton);
        btnStartTimeButton = findViewById(R.id.update_startTimeButton);
        btnEndTimeButton = findViewById(R.id.update_endTimeButton);
        btnCategorie = findViewById(R.id.BtnUpdateCategorie);
        btnFoto = findViewById(R.id.update_cameraButton);
        btnGallery = findViewById(R.id.update_galleryButton);
        btnUpdatePlace = findViewById(R.id.updatePlace_Button);

        placeImage = (ImageView)  findViewById(R.id.myplaceImage);

        layout_Profile = (ConstraintLayout) findViewById(R.id.layout_Profile);
        userName = (TextView) findViewById(R.id.userProfileNameTextView);
        userImage = (CircleImageView) findViewById(R.id.userProfilePicImageView);
        btn_back = (ImageButton) findViewById(R.id.btn_backUpdateMyPlace);


        placeTitle.setText(place.getTitle());
        placeDescription.setText(place.getDescription());
        btnSelectDate.setText(place.getDate());
        btnStartTimeButton.setText(place.getStartTime());
        btnEndTimeButton.setText(place.getEndTime());
        btnCategorie.setText(place.getCategorie());


        imageUri = place.getImageUrl();
        Uri uri = Uri.parse(imageUri);
        placeImage.setImageURI(uri);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(UpdateMyPlaceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // btnSelectDate.setText(day + "/" + month + "/" + year);
                        btnSelectDate.setText(day + "/" + (month + 1) + "/" + year);
                        date = btnSelectDate.getText().toString();
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        btnCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] kategorieItems = {"Sport", "Nachtleben", "Verteiler"};

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMyPlaceActivity.this);
                builder.setTitle("Wähle eine Kategorie");
                builder.setSingleChoiceItems(kategorieItems, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch(item)
                        {
                            case 0:
                                selectedCategorie = kategorieItems[0];
                                break;
                            case 1:
                                // Your code when 2nd  option seletced
                                selectedCategorie = kategorieItems[1];
                                break;
                            case 2:
                                // Your code when 3rd option seletced
                                selectedCategorie = kategorieItems[2];
                                break;
                        }
                        btnCategorie.setText(selectedCategorie);
                        selectCategorieAlertDialog.dismiss();
                    }
                });
                selectCategorieAlertDialog = builder.create();
                selectCategorieAlertDialog.show();
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateMyPlaceActivity.this, "Camera Btn is Clicked.", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });


        if(place.getUserID() != "") {
            dbrf.child(place.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    layout_Profile.setVisibility(View.VISIBLE);
                    userName.setText(user.getUserName());

                    Uri uri = Uri.parse(user.getUserProfilPhotoUrl());
                    Picasso.get().load(uri).into(userImage);

                    layout_Profile.setClickable(true);
                    layout_Profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), UserProfilePage.class);
                            intent.putExtra("USER_MODEL", user);
                            //  intent.putExtra("longitude", p.getLongitude());
                            startActivity(intent);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpdatePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                place.setTitle(placeTitle.getText().toString());
                place.setDescription(placeDescription.getText().toString());
                place.setDate(btnSelectDate.getText().toString());
                place.setStartTime(btnStartTimeButton.getText().toString());
                place.setEndTime(btnEndTimeButton.getText().toString());
                place.setCategorie(btnCategorie.getText().toString());

               /* if(takenPhotoUri.toString() != place.getImageUrl()) {
                    uploadImageToFirebase(takenPhotoFile.getName(), takenPhotoUri);
                    place.setImageUrl(takenPhotoUri.toString());
                }*/
                //uploadImageToFirebase(takenPhotoFile.getName(), takenPhotoUri);
                //place.setImageUrl(takenPhotoUri.toString());

                placesRef.child(place.getPlaceID()).setValue(place).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(UpdateMyPlaceActivity.this, "Place Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                myPlacesRef.child(place.getPlaceID()).setValue(place);

                finish();

            }
        });
    }
    public void popStartTimePicker(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                starthour = selectedHour;
                startminute = selectedMinute;
                btnStartTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",starthour, startminute));
                startTime = String.format(Locale.getDefault(), "%02d:%02d",starthour, startminute);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, starthour, startminute, true);

        timePickerDialog.setTitle("Select Start Time");
        timePickerDialog.show();
    }
    public void popEndTimePicker(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endhour = selectedHour;
                endminute = selectedMinute;
                btnEndTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",endhour, endminute));
                endTime = String.format(Locale.getDefault(), "%02d:%02d",endhour, endminute);

            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, endhour, endminute, true);

        timePickerDialog.setTitle("Select End Time");
        timePickerDialog.show();
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
    }
    private void dispatchTakePictureIntent() {
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
    private void uploadImageToFirebase(String name, Uri takenPhotoUri) {
        StorageReference image = mstorageReference.child("pictures/" + name);
        image.putFile(takenPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(UpdateMyPlaceActivity.this, "onSuccess: Uploades Image URl is" + uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMyPlaceActivity.this, "Upload Image Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                takenPhotoFile = new File (currentPhotoPath);
                placeImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Imagea is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                takenPhotoUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

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
    }

}
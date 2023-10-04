package com.example.mapped.ui.findplaces;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mapped.FullScreenImageActivity;
import com.example.mapped.FullScreenVideoActivity;
import com.example.mapped.PlaceModel;
import com.example.mapped.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;


public class CreateHangoutActivity extends AppCompatActivity {


    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int CAMERA_PIC_REQUEST = 22;
    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    Button btnok, btncancel, btnselectImage, btnSelectCategorie, btnStartTimeButton, btnEndTimeButton, btnSelectDate, btnCamera, btnGallery;
    int starthour = 0, startminute = 0, endhour = 0, endminute = 0;

    String currentPhotoPath = null;
    int year, month, dayOfMonth;
    Calendar calendar;
    String title = null, description = null, selectedCategorie = null, categorie = null;
    public String startTime = null;
    public String endTime = null;

    public String userID = null;

    public String date = null;
    private ImageView selectedImage;
    private EditText etTitlePlace, etDescriptionPlace;
    private Uri cameraUri = null;
    DatePickerDialog datePickerDialog;

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference, usersPlacesRef;


    private FirebaseStorage mfirebaseStorage;
    private StorageReference mstorageReference;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;
    public File takenPhotoFile = null;
    public Uri takenPhotoUri = null;
    public String takenPhotoUriString = null;
    PlaceModel newplace;
    AlertDialog selectCategorieAlertDialog;


    SwitchCompat sc_Profile;

    public Boolean placeWithProfile = false;
    private Button recordVideoBtn, btn_getVideoFromGallery;
    private ImageView videoView;
    // private VideoView videoView;
    private boolean isImageFitToScreen;
    public Uri videouri = null;

    public String downloadUri = null;
    public String currentUserID;
    public Double latitude;
    public Double longitude;
    public Date dateStart;
    public Date dateEnd;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_hangout);

        fbAuth = FirebaseAuth.getInstance();
        user = fbAuth.getCurrentUser();

        currentUserID = user.getUid();

        // Get the GeoPoint coordinates from the intent
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);


        etTitlePlace = (EditText) findViewById(R.id.et);
        etDescriptionPlace = (EditText) findViewById(R.id.et2);

        btnStartTimeButton = findViewById(R.id.startTimeButton);
        btnEndTimeButton = findViewById(R.id.endTimeButton);
        recordVideoBtn = findViewById(R.id.btn_recordvideo);
        videoView = findViewById(R.id.vv_showrecordedvideo);
        btn_getVideoFromGallery = findViewById(R.id.btn_getVideoFromGallery);
        btnCamera = findViewById(R.id.cameraButton);
        btnSelectDate = findViewById(R.id.dateButton);
        btnGallery = findViewById(R.id.galleryButton);
        selectedImage = (ImageView) findViewById(R.id.displayImageView);
        sc_Profile = (SwitchCompat) findViewById(R.id.sc_Profile);
        btnSelectCategorie = (Button) findViewById(R.id.BtnSelectCategorie);
        btnok = (Button) findViewById(R.id.btnok);
        btncancel = (Button) findViewById(R.id.btncancel);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();

        mfirebaseStorage = FirebaseStorage.getInstance();
        mstorageReference = mfirebaseStorage.getReference();

        usersPlacesRef = FirebaseDatabase.getInstance().getReference("Users Places");

        Date today = Calendar.getInstance().getTime();
        String fromattedToday = DateFormat.getDateInstance(DateFormat.FULL).format(today);

        int yeartoday = today.getYear() + 1900;
        int monthtoday = today.getMonth() + 1;
        int daytoday = today.getDate();

        btnStartTimeButton.setText("16:00");
        btnEndTimeButton.setText("17:00");

        btnSelectDate.setText(daytoday + "/" + (monthtoday) + "/" + yeartoday);

        btnSelectCategorie.setText("Sport");

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateHangoutActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // btnSelectDate.setText(day + "/" + month + "/" + year);

                        // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        // Date strDate = sdf.parse(String.valueOf(calendar.getTime()));

                        btnSelectDate.setText(day + "/" + (month + 1) + "/" + year);
                        date = btnSelectDate.getText().toString();
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateHangoutActivity.this, "Camera Btn is Clicked.", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });

        btn_getVideoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseVideo();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateHangoutActivity.this, "Galerie Btn is Clicked.", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        btnSelectCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] kategorieItems = {"Sport", "Nachtleben", "Verteiler"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateHangoutActivity.this);
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
                        selectCategorieAlertDialog.dismiss();
                    }
                });
                selectCategorieAlertDialog = builder.create();
                selectCategorieAlertDialog.show();
            }
        });

        sc_Profile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                placeWithProfile = b;
            }
        });
        recordVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line opening an intent to capture a video.
                Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                // on below line starting an activity for result.
                startActivityForResult(i, 1);
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(CreateHangoutActivity.this);

                title = etTitlePlace.getText().toString();
                description = etDescriptionPlace.getText().toString();
                categorie = selectedCategorie;

                Boolean rb = placeWithProfile;

                if (rb == true) {
                    userID = user.getUid();
                }

                GregorianCalendar GregPlaceStart = new GregorianCalendar(year, month, dayOfMonth, starthour, startminute);
                dateStart = GregPlaceStart.getTime();

                GregorianCalendar GregPlaceEnd = new GregorianCalendar(year, month, dayOfMonth, endhour, endminute);
                dateEnd = GregPlaceStart.getTime();

                if (takenPhotoUri != null) {
                    uploadImageToFirebase(takenPhotoFile.getName(), takenPhotoUri);
                }


                if(videouri != null) {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    UploadVideo();
                }

                CreatePlace();



            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void CreatePlace () {
        if (starthour >= endhour ) {
            Toast.makeText(CreateHangoutActivity.this, "End must be after Start", Toast.LENGTH_SHORT).show();
        } else {

            if (takenPhotoUri != null) {
                takenPhotoUriString = takenPhotoUri.toString();
            }
            String key = mdatabaseReference.push().getKey();
            PlaceModel newplace = new PlaceModel(title, description, latitude, longitude, btnSelectCategorie.getText().toString(), takenPhotoUriString, btnStartTimeButton.getText().toString(), btnEndTimeButton.getText().toString(), btnSelectDate.getText().toString(), userID, key, dateStart, dateEnd, downloadUri);

            mdatabaseReference.child("Places").child(key).setValue(newplace);
            usersPlacesRef.child(currentUserID).child("My Places").child(key).setValue(newplace);


            finish();
        }
    }

    private void ChooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 5);
    }

    private void UploadVideo() {
        if (videouri != null) {
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videouri));
            reference.putFile(videouri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult().toString();
                        Toast.makeText(CreateHangoutActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();


                    } else {
                        Toast.makeText(CreateHangoutActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }
            });

        }
    }
    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void uploadImageToFirebase(String name, Uri takenPhotoUri) {
        StorageReference image = mstorageReference.child("pictures/" + name);
        image.putFile(takenPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(CreateHangoutActivity.this, "onSuccess: Uploades Image URl is" + uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateHangoutActivity.this, "Upload Image Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadPicture() {

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = mstorageReference.child("/images" + randomKey);
        riversRef.putFile(cameraUri);
    }
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                takenPhotoFile = new File (currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                selectedImage.setVisibility(View.VISIBLE);
                Log.d("tag", "Absolute Url of Imagea is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                takenPhotoUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                selectedImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onImageClicked(takenPhotoUri);
                    }

                });

            }
        }
        if(requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                cameraUri = data.getData();
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName ="JPEG_" + timeStamp + "."+getFileExt(contentUri);
                selectedImage.setImageURI(contentUri);
            }
        }
        if (resultCode == RESULT_OK && requestCode == 1) {
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
           /* Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                    MediaStore.Images.Thumbnails.MICRO_KIND);
            videoView.setImageBitmap(thumb);*/
            /*MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            Bitmap extractedImage = media.getFrameAtTime(1);
            videoView.setImageBitmap(extractedImage);*/
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onVideoViewClicked(videouri);
                }
            });}


    }

    public void onImageClicked(Uri imageUri) {
        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
    }

    public void onVideoViewClicked(Uri videoUri) {
        Intent fullScreenVideoIntent = new Intent(this, FullScreenVideoActivity.class);
        fullScreenVideoIntent.setData(videoUri);
        startActivity(fullScreenVideoIntent);
    }
    private String getFileExt(Uri contentUri) {

        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
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
    private void dispatchTakePictureIntent()  {


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


}

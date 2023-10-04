package com.example.mapped.ui.findplaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mapped.FullScreenImageActivity;
import com.example.mapped.FullScreenVideoActivity;
import com.example.mapped.PlaceModel;
import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.example.mapped.ui.profil.UserProfilePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceInfoActivity extends AppCompatActivity {

    public TextView placeTitle, placeDescription, placeDate, placeTimespan, placeCategorie;
    public ImageView placeImage;

    public String imageUri, videoUri;
    public ImageButton btn_back, ib_savePlace;
    public Button btn_playVideo;
    public VideoView vv_placeVideo;
    public Uri vuri, iuri;

    PlaceModel place, favoriteplace;
    FirebaseDatabase fbdb;
    DatabaseReference dbrf, usersPlacesRef, myPlacesRef;
    FirebaseAuth mAuth;
    String currentUserId;

    ConstraintLayout layout_Profile;
    TextView userName;
    CircleImageView userImage;
    String favoritekey;
    boolean isImageFitToScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        place = (PlaceModel) getIntent().getSerializableExtra("PLACE");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        usersPlacesRef = FirebaseDatabase.getInstance().getReference("Users Places");
        myPlacesRef = FirebaseDatabase.getInstance().getReference("Users Places").child(currentUserId).child("My Places");


        placeTitle = (TextView) findViewById(R.id.placeInfoTitle);
        placeDescription = (TextView) findViewById(R.id.placeInfoDescription);
        placeDate = (TextView) findViewById(R.id.placeInfoDate);
        placeImage = (ImageView)  findViewById(R.id.placeInfoImage);
        layout_Profile = (ConstraintLayout) findViewById(R.id.layout_Profile);
        btn_playVideo = (Button) findViewById(R.id.btn_watchvideo);
        vv_placeVideo = (VideoView) findViewById(R.id.placeVideoView);
        placeCategorie = (TextView) findViewById(R.id.placeInfoCategorie);


        ib_savePlace = (ImageButton) findViewById(R.id.ib_SavePlace);

        myPlacesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> myplacesIDsList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String placeID = dataSnapshot.getKey();
                    myplacesIDsList.add(placeID);

                }
                if(myplacesIDsList.contains(place.getPlaceID())){
                    ib_savePlace.setVisibility(View.GONE);
                } else {
                    usersPlacesRef.child(currentUserId).child("Favorites").child(place.getPlaceID())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // favoritekey  = snapshot.getKey();
                                    favoriteplace = snapshot.getValue(PlaceModel.class);
                                    if(favoriteplace != null){
                                        ib_savePlace.setVisibility(View.VISIBLE);
                                        ib_savePlace.setImageResource(R.drawable.favorite_green_24);
                                        ib_savePlace.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                usersPlacesRef.child(currentUserId).child("Favorites").child(place.getPlaceID()).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                        Toast.makeText(PlaceInfoActivity.this, "Fav Place removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        ib_savePlace.setVisibility(View.VISIBLE);
                                        ib_savePlace.setImageResource(R.drawable.favorite_24);
                                        ib_savePlace.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                usersPlacesRef.child(currentUserId).child("Favorites").child(place.getPlaceID()).setValue(place);
                                            }
                                        });
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



        userName = (TextView) findViewById(R.id.userProfileNameTextView);
        userImage = (CircleImageView) findViewById(R.id.userProfilePicImageView);
        btn_back = (ImageButton) findViewById(R.id.btn_backPlaceInfo);



        fbdb = FirebaseDatabase.getInstance();
        dbrf = fbdb.getReference("users");


        placeTitle.setText(place.getTitle());
        placeDescription.setText(place.getDescription());
        placeDate.setText(place.getDate() + "  " + place.getStartTime() +"-"+place.getEndTime());
        placeCategorie.setText(place.getCategorie());

        if(place.getImageUrl() != null) {
            imageUri = place.getImageUrl();
            iuri = Uri.parse(imageUri);
            placeImage.setImageURI(iuri);
        }

        if(place.getVideoUri() != null) {
            videoUri = place.getVideoUri();
            vuri = Uri.parse(videoUri);
            vv_placeVideo.setVideoURI(vuri);
        }

        if(place.getUserID() != null) {
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

        btn_playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "s";
                vv_placeVideo.start();
            }
        });
        vv_placeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoViewClicked(vuri);
            }
        });
        placeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri myUri = Uri.parse(place.getImageUrl());
                onImageClicked(myUri);
            }

        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





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
}
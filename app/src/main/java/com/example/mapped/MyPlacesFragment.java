package com.example.mapped;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyPlacesFragment extends Fragment {
    private View myPlacesView;
    private RecyclerView myPlacesRecyclerList;
    private DatabaseReference myPlacesRef;
    private MyPlacesAdapter myPlacesAdapter;
    public ArrayList<PlaceModel> myPlacesList;
    private FirebaseAuth mAuth;
    private String currentUserID;
    public MyPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myPlacesView = inflater.inflate(R.layout.fragment_my_places, container, false);

        myPlacesRecyclerList = (RecyclerView) myPlacesView.findViewById(R.id.myPlaces_list);
        myPlacesRecyclerList.setHasFixedSize(true);
        myPlacesRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        myPlacesRef = FirebaseDatabase.getInstance().getReference().child("Users Places").child(currentUserID);



        myPlacesList = new ArrayList<>();

        myPlacesRef.child("My Places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    PlaceModel place = dataSnapshot.getValue(PlaceModel.class);
                    myPlacesList.add(place);
                }

                myPlacesAdapter = new MyPlacesAdapter(getContext(), myPlacesList);
                myPlacesRecyclerList.setAdapter(myPlacesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return myPlacesView;
    }
}
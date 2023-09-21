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

public class FavoritesFragment extends Fragment {

    private View favoritesView;
    private RecyclerView favoritesRecyclerList;
    private DatabaseReference favoritesRef;
    private FavoritesAdapter favoritesAdapter;
    public ArrayList<PlaceModel> favoritesList;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favoritesView = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerList = (RecyclerView) favoritesView.findViewById(R.id.favorites_list);
        favoritesRecyclerList.setHasFixedSize(true);
        favoritesRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Users Places").child(currentUserID);



        favoritesList = new ArrayList<>();

        favoritesRef.child("Favorites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    PlaceModel place = dataSnapshot.getValue(PlaceModel.class);
                    favoritesList.add(place);
                }

                favoritesAdapter = new FavoritesAdapter(getContext(), favoritesList);
                favoritesRecyclerList.setAdapter(favoritesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return favoritesView;
    }


}
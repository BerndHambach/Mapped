package com.example.mapped;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;
    private DatabaseReference placesRef, favoritesRef;
    ArrayList<PlaceModel> list;

    private FirebaseAuth mAuth;

    private String currentUserId;

    public FavoritesAdapter(Context context, ArrayList<PlaceModel> list) {
        this.context = context;
        this.list = list;
    }

    public void add(PlaceModel placeModel){
        list.add(placeModel);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoritesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.favorite_row, parent, false);
        return new FavoritesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.MyViewHolder holder, int position) {

        PlaceModel placeModel = list.get(position);


        holder.title.setText(placeModel.getTitle());
        holder.description.setText(placeModel.getDescription());

        Uri uri = Uri.parse(placeModel.getImageUrl());
        holder.image.setImageURI(uri);
        // holder.email.setText(userModel.getUserEmail());

        holder.iv_deletefavplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth = FirebaseAuth.getInstance();
                currentUserId = mAuth.getUid();



                favoritesRef = FirebaseDatabase.getInstance().getReference("Users Places").child(currentUserId).child("Favorites").child(placeModel.getPlaceID());
                favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.fav_con_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PlaceInfoActivity.class);
                intent.putExtra("PLACE", placeModel);

                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description;
        private ImageView image;
        private ConstraintLayout fav_con_layout;
        private ImageButton iv_deletefavplace;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favplaceTitle);
            description = itemView.findViewById(R.id.favplaceDescription);
            image = itemView.findViewById(R.id.favplaceImg);
            iv_deletefavplace = itemView.findViewById(R.id.ib_deletefavplace);
            fav_con_layout = itemView.findViewById(R.id.fav_con_layout);
            //email = itemView.findViewById(R.id.userEmail);

        }

    }
}

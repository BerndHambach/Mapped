package com.example.mapped.ui.places.myplaces;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import com.example.mapped.PlaceModel;
import com.example.mapped.R;
import com.example.mapped.ui.places.UpdateMyPlaceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyPlacesAdapter extends RecyclerView.Adapter<MyPlacesAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;
    private DatabaseReference placesRef, myPlacesRef;
    ArrayList<PlaceModel> list;

    private FirebaseAuth mAuth;

    private String currentUserId;
    Calendar calendar = Calendar.getInstance();


    GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();

    Date datenow;



    public MyPlacesAdapter(Context context, ArrayList<PlaceModel> list) {
        this.context = context;
        this.list = list;
        datenow = gregCalendar.getTime();

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
    public MyPlacesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.myplaces_row, parent, false);
        return new MyPlacesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlacesAdapter.MyViewHolder holder, int position) {

        PlaceModel placeModel = list.get(position);

        //int date = Integer.parseInt(placeModel.getDate());
        Date placeDateStart = placeModel.getDateStart();
        Date placeDateEnd = placeModel.getDateEnd();

           if (datenow.after(placeDateEnd) && datenow.after(placeDateStart)) {
                holder.con_layout.setBackgroundColor(context.getResources().getColor(R.color.red_inpast));
            }
            if (datenow.before(placeDateStart)) {
                holder.con_layout.setBackgroundColor(context.getResources().getColor(R.color.yellow_inaktiv));
            }

            holder.title.setText(placeModel.getTitle());
            holder.description.setText(placeModel.getDescription());
            holder.categorie.setText(placeModel.getCategorie());
            holder.date.setText(placeModel.getDate());
            holder.timespan.setText(placeModel.getStartTime() + " - " + placeModel.getEndTime());
            if (placeModel.getImageUrl() != null) {
                Uri uri = Uri.parse(placeModel.getImageUrl());
                holder.image.setImageURI(uri);
            }

        holder.iv_deletemyplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth = FirebaseAuth.getInstance();
                currentUserId = mAuth.getUid();

                placesRef = FirebaseDatabase.getInstance().getReference("Places").child(placeModel.getPlaceID());
                placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        snapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                myPlacesRef = FirebaseDatabase.getInstance().getReference("Users Places").child(currentUserId).child("My Places").child(placeModel.getPlaceID());
                myPlacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        holder.con_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, UpdateMyPlaceActivity.class);
                intent.putExtra("PLACE_MODEL", placeModel);

                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, date, timespan, categorie;
        public ImageView image;
        public ConstraintLayout con_layout;

        private ImageButton iv_deletemyplace;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.myplaceTitle);
            description = itemView.findViewById(R.id.myplaceDescription);
            image = itemView.findViewById(R.id.myplaceImg);
            iv_deletemyplace = itemView.findViewById(R.id.ib_deletemyplace);
            con_layout = itemView.findViewById(R.id.con_layout);
            date = itemView.findViewById(R.id.tv_myPlaceDate);
            timespan = itemView.findViewById(R.id.tv_myPlaceTimeSpan);
            categorie = itemView.findViewById(R.id.tv_myPlaceCategorie);

            //email = itemView.findViewById(R.id.userEmail);

        }

    }
}

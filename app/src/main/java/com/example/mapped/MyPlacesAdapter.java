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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

public class MyPlacesAdapter extends RecyclerView.Adapter<MyPlacesAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;
    private DatabaseReference placesRef, myPlacesRef;
    ArrayList<PlaceModel> list;

    private FirebaseAuth mAuth;

    private String currentUserId;
    Calendar calendar = Calendar.getInstance();
    Date today = Calendar.getInstance().getTime();
    int todayday = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int todaymonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    int todayyear = calendar.get(Calendar.YEAR);

    GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
    Date gregtoday = gregCalendar.getTime();
    int gregtodayminute = gregCalendar.get(gregCalendar.MINUTE);
    int am_or_pm = gregCalendar.get(gregCalendar.AM_PM);
    int gregtodyhour = gregCalendar.get(gregCalendar.HOUR);



    public MyPlacesAdapter(Context context, ArrayList<PlaceModel> list) {
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
    public MyPlacesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.myplaces_row, parent, false);
        return new MyPlacesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlacesAdapter.MyViewHolder holder, int position) {

        PlaceModel placeModel = list.get(position);

        //int date = Integer.parseInt(placeModel.getDate());
        String placedate = placeModel.getDate();
        String[] arrSplit_2 = placedate.split("/");

        String placeDay = arrSplit_2[0];
        int pDay = Integer.parseInt(placeDay);

        String placeMonth = arrSplit_2[1];
        int pMonth = Integer.parseInt(placeMonth);

        String placeYear = arrSplit_2[2];
        int pYear = Integer.parseInt(placeYear);

        String placeStartTime = placeModel.getStartTime();
        String[] arrSplit_placeStartTime = placeStartTime.split(":");
        String placeStartHour = arrSplit_placeStartTime[0];
        int pStartHour = Integer.parseInt(placeStartHour);
        String placeStartMinute = arrSplit_placeStartTime[1];
        int pStartMinute = Integer.parseInt(placeStartMinute);

        String placeEndTime = placeModel.getEndTime();
        String[] arrSplit_placeEndTime = placeEndTime.split(":");
        String placeEndHour = arrSplit_placeEndTime[0];
        int pEndHour = Integer.parseInt(placeEndHour);
        String placeEndMinute = arrSplit_placeEndTime[1];
        int pEndMinute = Integer.parseInt(placeEndMinute);


        if(am_or_pm == 1) {
            gregtodyhour = gregtodyhour + 12;
        }


        if(pDay < todayday || pEndHour < gregtodyhour || (pEndHour == gregtodyhour && pEndMinute < gregtodayminute) ) {
            holder.con_layout.setBackgroundColor(context.getResources().getColor(R.color.red_inpast));
        }
        /*if(pDay == todayday){
            holder.con_layout.setBackgroundColor(context.getResources().getColor(R.color.green_aktiv));
        }
        if(pDay > todayday){
            holder.con_layout.setBackgroundColor(context.getResources().getColor(R.color.yellow_inaktiv));
        }*/

        holder.title.setText(placeModel.getTitle());
        holder.description.setText(placeModel.getDescription());
        holder.categorie.setText(placeModel.getCategorie());
        holder.date.setText(placeModel.getDate());
        holder.timespan.setText(placeModel.getStartTime() + " - " + placeModel.getEndTime());

        Uri uri = Uri.parse(placeModel.getImageUrl());
        holder.image.setImageURI(uri);



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

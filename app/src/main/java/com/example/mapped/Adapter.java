package com.example.mapped;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    //ArrayList courseImg, courseName;

    ArrayList<PlaceModel> placeArrayList;
    Context context;
    Uri uri;


    // Constructor for initialization
    public Adapter(Context context, ArrayList<PlaceModel> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;

    }
    public void clear() {
        placeArrayList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placecardinfolayout, parent, false);

        // Passing view to ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        uri = Uri.parse(placeArrayList.get(position).getImageUrl());
        holder.images.setImageURI(uri);

        holder.title.setText(placeArrayList.get(position).getTitle());
        holder.time.setText(placeArrayList.get(position).getStartTime()+ "-" + placeArrayList.get(position).getEndTime());



    }
    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return placeArrayList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView images;
        TextView title;
        TextView time;


        //LinearLayout layout_place;
        public ViewHolder(View view) {
            super(view);
            images = (ImageView) view.findViewById(R.id.myplaceImg);
            title = (TextView) view.findViewById(R.id.myplaceTitle);
            time = (TextView) view.findViewById(R.id.myplaceDescription);
           // layout_place = (LinearLayout) view.findViewById(R.id.layout_place);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int i = getAbsoluteAdapterPosition();
            PlaceModel place = placeArrayList.get(i);
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(context, PlaceInfoActivity.class);
           intent.putExtra("PLACE", place);

            context.startActivity(intent);
        }
    }
}
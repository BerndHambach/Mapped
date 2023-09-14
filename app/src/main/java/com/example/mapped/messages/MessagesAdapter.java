package com.example.mapped.messages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.example.mapped.chat.Chat;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    ArrayList<UserModel> usersArrayList;
    Context context;
    Uri uri;


    // Constructor for initialization
    public MessagesAdapter(Context context, ArrayList<UserModel> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;

    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);

        // Passing view to ViewHolder
        MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // uri = Uri.parse(usersArrayList.get(position).getUserProfilPhotoUrl());
      //  holder.userImage.setImageURI(uri);
        UserModel userModel = usersArrayList.get(position);
        holder.userName.setText(usersArrayList.get(position).getUserName());
       // holder.userEmail.setText(usersArrayList.get(position).getUserEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("id", userModel.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return usersArrayList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView userImage;
        private TextView userName;
        private TextView lastMessage;
        private TextView unseenMessages;



        //LinearLayout layout_place;
        public ViewHolder(View view) {
            super(view);
            userImage = view.findViewById(R.id.userImg);
            userName = (TextView) view.findViewById(R.id.userName);
            lastMessage = (TextView) view.findViewById(R.id.lastMessage);
            unseenMessages = (TextView) view.findViewById(R.id.unseenMessages);
            // layout_place = (LinearLayout) view.findViewById(R.id.layout_place);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

           /* int i = getAbsoluteAdapterPosition();
            Place place = placeArrayList.get(i);
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, PlaceInfoActivity.class);
            intent.putExtra("PLACE", place);

            context.startActivity(intent);*/
        }
    }
}

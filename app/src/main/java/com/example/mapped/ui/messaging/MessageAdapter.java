package com.example.mapped.ui.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.ChatModel;
import com.example.mapped.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<ChatModel> mChat;
    private Context mContext;

    private String imageurl;

    private FirebaseAuth mAuth;
    private FirebaseUser fuser;
    private DatabaseReference usersRef;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;



    // Constructor for initialization
    public MessageAdapter(Context mContext,List<ChatModel> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;

        public CircleImageView profile_image;
        private LinearLayout main;

        public ViewHolder(View view) {
            super(view);

            show_message = (TextView) view.findViewById(R.id.show_message);
            profile_image = (CircleImageView) view.findViewById(R.id.profile_image);

        }
    }




    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.right_chat_message_item_layout, viewGroup, false);


        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.left_chat_message_item_layout, viewGroup, false);


        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatModel chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        /*if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.w
        }*/

    }

    @Override
    public int getItemCount() {

        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

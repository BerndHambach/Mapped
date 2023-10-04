package com.example.mapped.ui.messaging;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.MessageModel;
import com.example.mapped.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private List<MessageModel> userMessagesList;
    Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser fuser;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    // Constructor for initialization
    public MessagesAdapter(List<MessageModel> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, txt_seen, tv_time, show_photo_message;
        public ImageView show_photo, iv_show_photo;
        public VoicePlayerView audio_view;

        public MessageViewHolder(View view) {
            super(view);

            show_message = (TextView) view.findViewById(R.id.show_message);
            show_photo_message = (TextView) view.findViewById(R.id.tv_show_message);

            txt_seen = (TextView) view.findViewById(R.id.txt_seen);
            show_photo = (ImageView) view.findViewById(R.id.iv_showphoto);
            iv_show_photo = (ImageView) view.findViewById(R.id.iv_showphotowithtext);

            tv_time = (TextView) view.findViewById(R.id.tv_time);
            audio_view = (VoicePlayerView) view.findViewById(R.id.voicePlayerView);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case MSG_TYPE_RIGHT:
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.right_chat_message_item_layout, viewGroup, false);
                return new MessageViewHolder(view);

            case 100:
                View view3 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.right_chat_audio_item_layout, viewGroup, false);
                return new MessageViewHolder(view3);

            case MSG_TYPE_LEFT:
                View view2 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.left_chat_message_item_layout, viewGroup, false);
                return new MessageViewHolder(view2);

            case 200:
                View view4 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.left_chat_audio_item_layout, viewGroup, false);
                return new MessageViewHolder(view4);

            case 90:
                View view5 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.right_chat_photo_item_layout, viewGroup, false);
                return new MessageViewHolder(view5);

            case 190:
                View view6 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.left_chat_photo_item_layout, viewGroup, false);
                return new MessageViewHolder(view6);

            case 80:
                View view7 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.right_chat_photo_and_text_item_layout, viewGroup, false);
                return new MessageViewHolder(view7);

            case 180:
                View view8 = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.left_chat_photo_and_text_item_layout, viewGroup, false);
                return new MessageViewHolder(view8);
        }
       /* if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageViewHolder(view);
        }*/
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = userMessagesList.get(position);
        switch(getItemViewType(position)){
            case MSG_TYPE_RIGHT:
                holder.show_message.setText(message.getMessage());
                break;

            case 100:
                String messagePhotoUri = message.getPhotoUri();
                if (messagePhotoUri != null) {
                    holder.audio_view.setAudio(messagePhotoUri);
                }
                break;

            case MSG_TYPE_LEFT:
                holder.show_message.setText(message.getMessage());
                break;

            case 200:
                holder.audio_view.setAudio(message.getPhotoUri());

            case 90:
                Uri photoUri = Uri.parse(message.getPhotoUri());
                holder.show_photo.setImageURI(photoUri);
                break;

            case 190:
                Uri photoUri2 = Uri.parse(message.getPhotoUri());
                holder.show_photo.setImageURI(photoUri2);
                break;

            case 80:
                Uri photoUri3 = Uri.parse(message.getPhotoUri());
                holder.iv_show_photo.setImageURI(photoUri3);
                holder.show_photo_message.setText(message.getMessage());

                break;
            case 180:
                Uri photoUri4 = Uri.parse(message.getPhotoUri());
                holder.iv_show_photo.setImageURI(photoUri4);
                holder.show_photo_message.setText(message.getMessage());
                break;

        }
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return userMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {

        MessageModel messageModel = userMessagesList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (userMessagesList.get(position).getFrom().equals(fuser.getUid())) {
            if (messageModel.getType().equals("audio")) {
                return 100;

            } else if (messageModel.getType().equals("photo")){
                return 90;

            } else if (messageModel.getType().equals("photo and text")){
                return 80;

            } else
                return MSG_TYPE_RIGHT;
        } else {
            if (messageModel.getType().equals("audio")) {
                return 200;

            } else if (messageModel.getType().equals("photo")){
                return 190;

            } else if (messageModel.getType().equals("photo and text")){
                return 180;

            } else
                return MSG_TYPE_LEFT;
            }
        }

        // Initializing the Views
    }


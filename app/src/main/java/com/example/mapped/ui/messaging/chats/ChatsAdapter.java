package com.example.mapped.ui.messaging.chats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.MessageModel;
import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;

    ArrayList<UserModel> list;

    private boolean ischat;

    private DatabaseReference lastMessageRef;

    public ChatsAdapter(Context context, ArrayList<UserModel> list, boolean ischat) {
        this.context = context;
        this.list = list;
        this.ischat = ischat;
    }

    public void add(UserModel userModel){
        list.add(userModel);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chats_row, parent, false);
        return new ChatsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.MyViewHolder holder, int position) {



        UserModel userModel = list.get(position);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        lastMessageRef = FirebaseDatabase.getInstance().getReference("Messages").child(currentUserId).child(userModel.getUserId());
        lastMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MessageModel> chatMessagesList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    MessageModel message = dataSnapshot.getValue(MessageModel.class);
                    chatMessagesList.add(message);
                }

                int size = chatMessagesList.size();

                if(size > 0) {
                    MessageModel lastMessageModel = chatMessagesList.get(size - 1);

                    if(lastMessageModel.getMessage() == null) {
                        holder.last_Message.setText("");
                    } else {
                        holder.last_Message.setText(lastMessageModel.getMessage().toString());
                    }
                } else {
                    holder.last_Message.setText("");
                }

                int unseenMessages = 0;
                for (int i = 0; i < size; i ++ ) {
                    if(chatMessagesList.get(i).isIsseen() == false) {
                        unseenMessages = unseenMessages + 1;
                    }
                }
                if (unseenMessages > 0) {
                    holder.unseenMessages.setText(String.valueOf(unseenMessages));
                } else {
                    holder.unseenMessages.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        holder.name.setText(userModel.getUserName());
        // holder.email.setText(userModel.getUserEmail());

        /*if(ischat){
            if (userModel.getUserStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CURRENT_CHAT_PARTNER", userModel);

                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, last_Message, unseenMessages;
        private CircleImageView img_on;
        private CircleImageView img_off;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            last_Message = itemView.findViewById(R.id.last_Message);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);


            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);


        }

    }
}
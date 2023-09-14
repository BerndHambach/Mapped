package com.example.mapped;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;

    ArrayList<UserModel> list;

    public ChatsAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
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

        View view = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);
        return new ChatsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.MyViewHolder holder, int position) {
        UserModel userModel = list.get(position);
        holder.name.setText(userModel.getUserName());
        // holder.email.setText(userModel.getUserEmail());

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
        private TextView name, email;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            //email = itemView.findViewById(R.id.userEmail);

        }

    }
}
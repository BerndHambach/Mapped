package com.example.mapped.ui.messaging.contacts;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.example.mapped.ui.profil.UserProfilePage;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;

    ArrayList<UserModel> list;

    public ContactsAdapter(Context context, ArrayList<UserModel> list) {
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
    public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.contacts_row, parent, false);
        return new ContactsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.MyViewHolder holder, int position) {

        UserModel userModel = list.get(position);

        holder.name.setText(userModel.getUserName());
        holder.info.setText(userModel.getUserInfo());
        // holder.email.setText(userModel.getUserEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, UserProfilePage.class);
                intent.putExtra("USER_MODEL", userModel);

                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, info;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            info = itemView.findViewById(R.id.userinfo);
            //email = itemView.findViewById(R.id.userEmail);

        }

    }
}

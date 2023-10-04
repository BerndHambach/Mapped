package com.example.mapped;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapped.databinding.GroupContactItemLayoutBinding;
import com.example.mapped.ui.messaging.contacts.ContactsAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupContactAdapter extends RecyclerView.Adapter<GroupContactAdapter.ViewHolder> {

    private ArrayList<UserModel> arrayList;
    private ContactItemInterface contactItemInterface;

    public GroupContactAdapter(ContactItemInterface contactItemInterface) {
        this.contactItemInterface = contactItemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        GroupContactItemLayoutBinding binding = GroupContactItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupContactAdapter.ViewHolder holder, int position) {
        if(arrayList!=null){
            UserModel userModel = arrayList.get(position);

           holder.binding.ciUsername.setText(userModel.getUserName());
           holder.binding.ciUserinfo.setText(userModel.getUserInfo());

            Uri uri = Uri.parse(userModel.getUserProfilPhotoUrl());
            holder.binding.ciProfileImage.setImageURI(uri);

            holder.itemView.setOnClickListener(view -> {
                contactItemInterface.onContactClick(userModel, position, false);
            });

        }
    }

    @Override
    public int getItemCount() {
        if(arrayList != null)
            return arrayList.size();
        else
            return 0;
    }

    public void setArrayList(ArrayList<UserModel> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GroupContactItemLayoutBinding binding;
        public ViewHolder(@NonNull GroupContactItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

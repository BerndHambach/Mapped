package com.example.mapped.ui.messaging.requests;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {

    //public class MyViewHolder extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    //private List<UserModel> userModelList;

    ArrayList<UserModel> list;

    private DatabaseReference contactsRef, chatRequestRef;


    public RequestsAdapter(Context context, ArrayList<UserModel> list) {
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
    public RequestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.request_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.MyViewHolder holder, int position) {

        chatRequestRef = FirebaseDatabase.getInstance().getReference("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference("Contacts");
        String currentUserID = FirebaseAuth.getInstance().getUid();
        UserModel userModel = list.get(position);
        holder.name.setText(userModel.getUserName());
        // holder.email.setText(userModel.getUserEmail());
        holder.itemView.findViewById(R.id.reqest_accept_btn).setVisibility(View.VISIBLE);
        holder.itemView.findViewById(R.id.reqest_cancel_btn).setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence options[] = new CharSequence[] {
                        "Accept",
                        "Cancel"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(userModel.getUserName() + " Chat Request");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0) {
                            contactsRef.child(currentUserID).child(userModel.getUserId()).child("Contacts")
                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                contactsRef.child(userModel.getUserId()).child(currentUserID).child("Contacts")
                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){

                                                                        chatRequestRef.child(currentUserID).child(userModel.getUserId())
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful()){

                                                                                            chatRequestRef.child(userModel.getUserId()).child(currentUserID)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if(task.isSuccessful()){
                                                                                                                Toast.makeText(view.getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        if(i == 1) {
                            chatRequestRef.child(currentUserID).child(userModel.getUserId())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                chatRequestRef.child(userModel.getUserId()).child(currentUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(view.getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }

                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private Button accept_btn, cancel_btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userRequestName);
            //accept_btn = itemView.findViewById(R.id.reqest_accept_btn);
            // cancel_btn = itemView.findViewById(R.id.reqest_cancel_btn);
            //email = itemView.findViewById(R.id.userEmail);

        }

    }
}

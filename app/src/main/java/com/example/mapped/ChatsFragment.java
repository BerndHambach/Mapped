package com.example.mapped;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    private View privateChatsView;
    private RecyclerView chatsRecyclerList;
    public ArrayList<UserModel> chatsList;
    private FirebaseAuth mAuth;
    private String currentUserID;
    DatabaseReference chatsRef, usersChatsRef;
    private ChatsAdapter chatsAdapter;
    private ArrayList<String> chats_contactsID_List;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatsView=  inflater.inflate(R.layout.fragment_chats, container, false);

        chatsRecyclerList = (RecyclerView) privateChatsView.findViewById(R.id.chats_list);
        chatsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersChatsRef = FirebaseDatabase.getInstance().getReference().child("users");


        chatsList = new ArrayList<>();
        chats_contactsID_List = new ArrayList<>();
       // request_usersID_List = new ArrayList<>();

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String userID = dataSnapshot.getKey();
                    chats_contactsID_List.add(userID);
                }
                // requestsAdapter.notifyDataSetChanged();

                usersChatsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            chatsList.add(user);
                        }
                            /*for (int i = 0; i<request_usersID_List.size(); i++) {
                                if(request_usersID_List.get(i)== snapshot.getKey()){
                                    UserModel requestUser = snapshot.getValue(UserModel.class);
                                    requestsList.add(requestUser);
                                }
                            }*/
                        chatsAdapter = new ChatsAdapter(getContext(), chatsList);
                        chatsRecyclerList.setAdapter(chatsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return privateChatsView;
    }
}
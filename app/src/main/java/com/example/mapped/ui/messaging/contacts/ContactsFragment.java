package com.example.mapped.ui.messaging.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactList;
    private DatabaseReference contactsRef, usersRef;
    private ContactsAdapter contactsAdapter;
    public ArrayList<UserModel> contactsList;

    //private ArrayList<String> contactsIDList;

    public List<String> contactsIDList;
    private FirebaseAuth mAuth;
    private String currentUserID;
    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);



        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");


        myContactList = (RecyclerView) contactsView.findViewById(R.id.contacts_list);
        myContactList.setHasFixedSize(true);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        contactsList = new ArrayList<>();
        contactsIDList = new ArrayList<String>();


        GetContacts();
        //usersRef.child(userIDs)
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String userKey = dataSnapshot.getKey();
                    contactsIDList.add(userKey);

                }
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot usersnapshot) {

                        ArrayList<UserModel> usersList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : usersnapshot.getChildren()) {

                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            usersList.add(user);
                        }

                        for (int i = 0; i < usersList.size(); i++) {

                            if(contactsIDList.contains(usersList.get(i).getUserId())){
                                UserModel contact = usersList.get(i);
                                contactsList.add(contact);
                            }
                        }
                           /* String userID = dataSnapshot.getKey();
                            if(contactsIDList.contains(currentUserID)) {

                                UserModel contact = dataSnapshot.getValue(UserModel.class);
                                contactsList.add(contact);

                            }
                            /*for (int i = 0; i < contactsIDList.size(); i ++) {
                                if(contactsIDList.get(i).toString() == dataSnapshot.getKey()) {
                                    UserModel contact = dataSnapshot.getValue(UserModel.class);
                                    contactsList.add(contact);
                                }

                            }*/

                        String s = "s";
                        contactsAdapter = new ContactsAdapter(getContext(), contactsList);
                        myContactList.setAdapter(contactsAdapter);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return contactsView;
    }

    private void GetContacts() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
package com.example.mapped;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapped.messages.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView myRequestsRecyclerView;
    DatabaseReference chatRequestRef;
    DatabaseReference usersRequestRef, getTypeRef, contactsRef;
    private RequestsAdapter requestsAdapter;
    public ArrayList<UserModel> requestsList;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private ArrayList<String> request_usersID_List;
    public RequestsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        requestFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestsRecyclerView = (RecyclerView)  requestFragmentView.findViewById(R.id.chat_request_list);
        myRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserID);
        usersRequestRef = FirebaseDatabase.getInstance().getReference().child("users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        requestsList = new ArrayList<>();
        request_usersID_List = new ArrayList<>();


        chatRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                String userID = dataSnapshot.getKey();
                request_usersID_List.add(userID);
            }
               // requestsAdapter.notifyDataSetChanged();

                usersRequestRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            requestsList.add(user);
                        }
                            /*for (int i = 0; i<request_usersID_List.size(); i++) {
                                if(request_usersID_List.get(i)== snapshot.getKey()){
                                    UserModel requestUser = snapshot.getValue(UserModel.class);
                                    requestsList.add(requestUser);
                                }
                            }*/
                            requestsAdapter = new RequestsAdapter(getContext(), requestsList);
                            myRequestsRecyclerView.setAdapter(requestsAdapter);
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

        myRequestsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return requestFragmentView;
    }


}
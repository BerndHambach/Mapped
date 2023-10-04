package com.example.mapped;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mapped.databinding.FragmentGroupMemberBinding;
import com.example.mapped.model.GroupMemberModel;
import com.example.mapped.model.GroupModel;
import com.example.mapped.ui.messaging.contacts.ContactsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class GroupMemberFragment extends Fragment implements ContactItemInterface {

    private View groupMemberView;
    private FragmentGroupMemberBinding binding;

    private RecyclerView myContactList, selectedContactsList;
    private DatabaseReference contactsRef, usersRef, userRef;
    private ContactsAdapter contactsAdapter;
    private GroupContactAdapter groupContactAdapter;
    private SelectedContactAdapter selectedContactAdapter;
    public ArrayList<UserModel> contactsList, selectedContacts;


    public List<String> contactsIDList;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private String imageUri, groupName;
    private Uri groupImage;
    private UserModel currentUser;
    private FloatingActionButton fb_done;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupMemberView = inflater.inflate(R.layout.fragment_group_member, container, false);

        fb_done = (FloatingActionButton) groupMemberView.findViewById(R.id.fabDone);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myContactList = (RecyclerView) groupMemberView.findViewById(R.id.recyclerViewContact);
        myContactList.setHasFixedSize(true);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedContactsList = (RecyclerView) groupMemberView.findViewById(R.id.groupMemberRecyclerView);

        groupContactAdapter = new GroupContactAdapter(this);
        selectedContactAdapter = new SelectedContactAdapter(this, requireContext());


        contactsList = new ArrayList<>();
        contactsIDList = new ArrayList<String>();
        selectedContacts = new ArrayList<>();

        Bundle bundle = getArguments();
        if(bundle != null){
            groupName = bundle.getString("GroupName");
            imageUri = bundle.getString("GroupImage");
            groupImage = Uri.parse(imageUri);
        }

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

                        groupContactAdapter.setArrayList(contactsList);
                        //myContactList.setAdapter(groupContactAdapter);

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

        fb_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedContacts.size() > 0){
                    createGroup();
                } else {
                    Toast.makeText(requireContext(), "Select Groupmember", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return groupMemberView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedContactsList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedContactsList.setAdapter(selectedContactAdapter);
        selectedContactAdapter.setUserModels(selectedContacts);
    }

    @Override
    public void onContactClick(UserModel userModel, int position, boolean isSelect) {

        if(isSelect){
            selectedContacts.remove(userModel);
            selectedContactAdapter.setUserModels(selectedContacts);
        } else {
            if(!selectedContacts.contains(userModel)){
                selectedContacts.add(userModel);
                selectedContactAdapter.setUserModels(selectedContacts);
            }
        }
    }

    private void createGroup(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group Detail");
        String groupId = databaseReference.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(groupId+AllConstants.GROUP_IMAGE).putFile(groupImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(requireContext(), "Working.......", Toast.LENGTH_SHORT).show();
                Task<Uri> image = taskSnapshot.getStorage().getDownloadUrl();
                image.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            String url = task.getResult().toString();
                            GroupModel groupModel = new GroupModel();
                            groupModel.adminId = mAuth.getUid();
                            groupModel.adminName = currentUser.getUserName();
                            groupModel.name = groupName;
                            groupModel.createdAt = String.valueOf(System.currentTimeMillis());
                            groupModel.image = url;
                            groupModel.id = groupId;

                            databaseReference.child(groupId).setValue(groupModel);
                            Toast.makeText(requireContext(), "Few seconds......", Toast.LENGTH_SHORT).show();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group Detail").child(groupId);
                            GroupMemberModel groupMemberModel = new GroupMemberModel();
                            groupMemberModel.id = currentUserID;
                            groupMemberModel.role = "admin";

                            reference.child(currentUserID).setValue(groupMemberModel);

                            for(UserModel userModel : selectedContacts){

                                GroupMemberModel memberModel = new GroupMemberModel();
                                memberModel.id = userModel.getUserId();
                                memberModel.role = "member";
                                reference.child(userModel.getUserId()).setValue(memberModel);
                            }

                            Toast.makeText(requireContext(), "Group created", Toast.LENGTH_SHORT).show();
                            getActivity().finish();

                        } else {
                            Toast.makeText(requireContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}
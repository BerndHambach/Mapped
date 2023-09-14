package com.example.mapped.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mapped.FindFriendsActivity;
import com.example.mapped.MessagesTapFragment;
import com.example.mapped.MessagesTapsAdapter;
import com.example.mapped.TabsAccessorAdapter;
import com.example.mapped.messages.MessagesAdapter;
import com.example.mapped.R;
import com.example.mapped.UserModel;
import com.example.mapped.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment {


    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";

    private boolean dataSet = false;

    FirebaseDatabase fbdatabase;
    DatabaseReference dbreference;
    RecyclerView recyclerView;

    RecyclerView.Adapter friendsAdapter;
    private FragmentDashboardBinding binding;
    public ArrayList<UserModel> usersArrayList;
    FirebaseAuth fbAuth;
    FirebaseUser currentUser;
    //UserAdapter userAdapter;
    MessagesAdapter fAdapter;

    private static final int NUM_PAGES = 3;
    View view;
    Adapter adapter;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private MessagesTapsAdapter pagerAdapter;

    private TabsAccessorAdapter myTabsAccessorAdapter;

    Button btn_FindFriends, btn_CreateGroup;


    /*public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState) {

    //binding = FragmentDashboardBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();

        btn_FindFriends = (Button) view.findViewById(R.id.btn_FindFriends);
        btn_CreateGroup = (Button) view.findViewById(R.id.btn_CreateGroup);

        mTabLayout = view.findViewById(R.id.tabs);
        mViewPager = view.findViewById(R.id.viewpager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getFragmentManager());

        mViewPager.setAdapter(myTabsAccessorAdapter);
       // mViewPager.setAdapter(createMessageAdapter());

        mTabLayout.setupWithViewPager(mViewPager);
        /*new TabLayoutMediator(tabLayout, mViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
                    }
                }).attach();*/


        btn_CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestNewGroup();
            }
        });


        btn_FindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToFriendsActivity();
            }
        });
        usersArrayList = new ArrayList<UserModel>();
        // recyclerView = (RecyclerView) root.findViewById(R.id.usersRecyclerView);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
       // recyclerView.setLayoutManager(linearLayoutManager);

        //final CircleImageView userProfilePic = root.findViewById(R.id.userProfilePic);


        fbAuth = FirebaseAuth.getInstance();

        fbdatabase = FirebaseDatabase.getInstance();
        dbreference = fbdatabase.getReference();


    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(getActivity());
        groupNameField.setHint("e.g Coding Cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(getActivity(), "Please write Group Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupName ) {
        dbreference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(),groupName + "is Created Successfully...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private MessagesTapsAdapter createMessageAdapter() {
        MessagesTapsAdapter adapter = new MessagesTapsAdapter(getActivity());
        return adapter;
    }


   /* private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag( QuestionsFragModel.newInstance(VolleyRequestHandler.RECENT_INT) , getResources().getString(R.string.newest));
        adapter.addFrag( QuestionsFragModel.newInstance(VolleyRequestHandler.UNANSWER_INT) , getResources().getString(R.string.notanswered));
        adapter.addFrag( QuestionsFragModel.newInstance(VolleyRequestHandler.ALLPOSTS_INT) , getResources().getString(R.string.allposts));
        mViewpager.setAdapter(adapter);
    }*/


   // @Override
    /*public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void SendUserToFriendsActivity() {
        Intent findFriendIntent = new Intent(getContext(), FindFriendsActivity.class);
        startActivity(findFriendIntent);
    }
}
package com.example.mapped;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mapped.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.mapped.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private  ActivityMainBinding binding;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;

    private DatabaseReference userRef;

    public String mobile;
    public String name;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         fbAuth = FirebaseAuth.getInstance();
         user = fbAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "User angemeldet", Toast.LENGTH_SHORT).show();

        } else {
            // No user is signed in
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }


       // mobile = getIntent().getStringExtra("mobile");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        mobile = PreferenceManager.getDefaultSharedPreferences(this).getString("MYMOBILE", "defaultStringIfNothingFound");


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profil)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // return null;
    }

    private void status(String status) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        userRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
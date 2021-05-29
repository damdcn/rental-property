package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private boolean isConnected, isLandlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(new HomeFragment());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user != null) userId = user.getUid();
        textViewWelcome = findViewById(R.id.welcomeTextView);
        isConnected = false;
        isLandlord = false;

        /*===============================================================*/
        /*====================== BOTTOM NAV BAR =========================*/
        /*===============================================================*/

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        if (user == null) {
            bottomNavigationView.getMenu().getItem(1).setVisible(false);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                FragmentManager fm;
                FragmentTransaction ft;

                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragment(new HomeFragment());
                        break;
                    case R.id.action_upload:
                        if(isConnected && isLandlord){
                            setFragment(new UploadFragment());
                        } else {
                            Toast.makeText(MainActivity.this, R.string.no_right_to_upload, Toast.LENGTH_LONG).show();
                            setCurrentChecked();
                        }
                        break;
                    case R.id.action_account:
                        if(isConnected){
                            setFragment(new ProfileFragment());
                        } else {
                            setFragment(new LoginFragment());
                        }
                        break;
                }
                return true;
            }
        });

        /*===============================================================*/
        /*====================== CUSTOM WELCOME =========================*/
        /*===============================================================*/

        if(user != null) {
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        isConnected = true;
                        isLandlord = userProfile.isLandlord;

                        if(!isLandlord) bottomNavigationView.getMenu().getItem(1).setVisible(false);

                        SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                        editor.putString("uid", user.getUid());
                        editor.putString("firstname", userProfile.firstname);
                        editor.putString("lastname", userProfile.lastname);
                        editor.putString("email", userProfile.email);
                        editor.putBoolean("isLandlord", userProfile.isLandlord);
                        editor.commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentChecked();
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.mainFragment, fragment)
            .commit();
    }

    public void setCurrentChecked(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFragment);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        if(currentFragment instanceof HomeFragment) bottomNavigationView.getMenu().getItem(0).setChecked(true);
        else if(currentFragment instanceof UploadFragment) bottomNavigationView.getMenu().getItem(1).setChecked(true);
        else if(currentFragment instanceof ProfileFragment) bottomNavigationView.getMenu().getItem(2).setChecked(true);
        else if(currentFragment instanceof LoginFragment) bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
}
package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                FragmentManager fm;
                FragmentTransaction ft;
                Bundle b = new Bundle();

                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragment(new HomeFragment());
                        break;
                    case R.id.action_upload:
                        if(isConnected && isLandlord){
                            setFragment(new UploadFragment());
                        } else {
                            Toast.makeText(MainActivity.this, R.string.no_right_to_upload, Toast.LENGTH_LONG).show();
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

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.mainFragment, fragment)
            .commit();
    }
}
package com.example.rentalproperty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    TextView textViewTitle, textViewSecondTitle, textViewEmail, textViewFullName;
    ImageView logOut;
    LinearLayout linearLayoutFavorites;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user != null) userId = user.getUid();
        textViewTitle = view.findViewById(R.id.profile_title);
        textViewSecondTitle = view.findViewById(R.id.profile_secondtitle);
        textViewEmail = view.findViewById(R.id.profile_email);
        textViewFullName = view.findViewById(R.id.profile_fullname);
        logOut = view.findViewById(R.id.profile_logout);
        linearLayoutFavorites = view.findViewById(R.id.profile_favorites);

        logOut.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().recreate();
        });

        linearLayoutFavorites.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FavoritesActivity.class));
        });



        if(user != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
            String uid = prefs.getString("uid", null);
            String firstname = prefs.getString("firstname", null);
            String lastname = prefs.getString("lastname", null);
            String email = prefs.getString("email", null);
            boolean isLandlord = prefs.getBoolean("isLandlord", false);

            textViewTitle.setText(firstname + " " + lastname);
            textViewSecondTitle.setText(isLandlord ? getText(R.string.landlord) : getText(R.string.tenant));
            textViewEmail.setText(email);
            textViewFullName.setText(firstname + " " + lastname);

        }




        return view;
    }
}
package com.example.rentalproperty;

import android.content.Intent;
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
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        textViewTitle.setText(userProfile.firstname + " " + userProfile.lastname);
                        textViewSecondTitle.setText(userProfile.isLandlord ? getText(R.string.landlord) : getText(R.string.tenant));
                        textViewEmail.setText(userProfile.email);
                        textViewFullName.setText(userProfile.firstname + " " + userProfile.lastname);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            });
        }




        return view;
    }
}
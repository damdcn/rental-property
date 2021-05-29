package com.example.rentalproperty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    TextView textViewTitle, textViewSecondTitle, textViewEmail, textViewFullName;
    ImageView logOut, editFullname, editEmail;
    LinearLayout linearLayoutFavorites, linearLayoutBookings;
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
        editFullname = view.findViewById(R.id.profile_edit_fullname);
        editEmail = view.findViewById(R.id.profile_edit_email);
        linearLayoutFavorites = view.findViewById(R.id.profile_favorites);
        linearLayoutBookings = view.findViewById(R.id.profile_bookings);

        logOut.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().recreate();
        });

        editFullname.setOnClickListener(v -> {
            View layout = getLayoutInflater().inflate(R.layout.dialog_edit_text_fullname, null);

            SharedPreferences prefs = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
            EditText editTextFirstName = layout.findViewById(R.id.edittext_dialog_firstname);
            EditText editTextLastName = layout.findViewById(R.id.edittext_dialog_lastname);
            editTextFirstName.setText(prefs.getString("firstname", null));
            editTextLastName.setText(prefs.getString("lastname", null));

            AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
            builder.setView(layout);
            builder.setTitle(R.string.edit_fullname);
            builder.setIcon(R.drawable.ic_baseline_edit_24);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newFirstname = editTextFirstName.getText().toString().trim();
                    String newLastname = editTextFirstName.getText().toString().trim();

                    reference.child(userId).child("firstname").setValue(newFirstname)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_data", MODE_PRIVATE).edit();
                                    editor.putString("firstname", newFirstname);
                                    editor.apply();

                                    reference.child(userId).child("lastname").setValue(newLastname)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    editor.putString("lastname", newLastname);
                                                    editor.apply();
                                                    Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
                                                    updateUi();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), R.string.update_fail, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.update_fail, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // Edit email
        editEmail.setOnClickListener(v -> {
            View layout = getLayoutInflater().inflate(R.layout.dialog_edit_text_email, null);

            SharedPreferences prefs = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
            String oldEmail = prefs.getString("email", null);
            EditText editTextEmail = layout.findViewById(R.id.edittext_dialog_email);
            editTextEmail.setText(oldEmail);

            // First dialog for email prompt
            AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
            builder.setView(layout);
            builder.setTitle(R.string.edit_email);
            builder.setIcon(R.drawable.ic_baseline_edit_24);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                // email prompt confirmed
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newEmail = editTextEmail.getText().toString().trim();

                    View layout2 = getLayoutInflater().inflate(R.layout.dialog_edit_text_password, null);
                    EditText editTextPassword = layout2.findViewById(R.id.edittext_dialog_password);

                    // second dialog for password prompt (mandatory to update auth information)
                    AlertDialog.Builder  builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setView(layout2);
                    builder2.setTitle(R.string.confirm_password);
                    builder2.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                        // Password prompt confirmed
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String password = editTextPassword.getText().toString();

                            // get auth credentials from the user for re-authentication
                            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // password confirmed
                                    if(task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                // successfully updated in FirebaseAuth
                                                if (task.isSuccessful()) {
                                                    reference.child(userId).child("email").setValue(newEmail)

                                                            // successfully updated in Firebase Realtime Database
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_data", MODE_PRIVATE).edit();
                                                                    editor.putString("email", newEmail);
                                                                    editor.apply();

                                                                    Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
                                                                    updateUi();
                                                                }
                                                            })
                                                            // update failed in Firebase Realtime Database
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getActivity(), R.string.update_fail, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                    // wrong password
                                    } else {
                                        Toast.makeText(getActivity(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    // password prompt canceled
                    builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                }
            });

            // email prompt canceled
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        linearLayoutFavorites.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FavoritesActivity.class));
        });

        linearLayoutBookings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), BookingsActivity.class));
        });

        updateUi();

        return view;
    }

    public void updateUi(){
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
    }
}
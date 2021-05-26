package com.example.rentalproperty;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rentalproperty.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBService {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private boolean isConnected;
    private User currentUser;

    public DBService() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        isConnected = false;

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            database.getReference("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        isConnected = true;
                        currentUser = userProfile;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("DBService", "Error while reading database");
                }
            });
        }
    }

    public boolean isConnected() {
        if (mAuth.getCurrentUser() != null)
            isConnected = true;
        else
            isConnected = false;

        return isConnected;
    }

    public User getUser() {
        if(isConnected) return currentUser;
        else return null;
    }

    public void toggleLike(String productId, MyCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ref = database.getReference().child("Users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("favorites")){
                    if(dataSnapshot.child("favorites").hasChild(productId)){
                        callback.onCallback(true);
                    } else {
                        callback.onCallback(false);
                    }
                } else {
                    callback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void checkLike(String productId, MyCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ref = database.getReference().child("Users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("favorites")){
                    if(dataSnapshot.child("favorites").hasChild(productId)){
                        callback.onCallback(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void like(String productId) {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ref = database.getReference().child("Users").child(user.getUid()).child("favorites");
        ref.child(productId).setValue(true);
    }

    public void undoLike(String productId) {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ref = database.getReference().child("Users").child(user.getUid()).child("favorites");
        ref.child(productId).getRef().removeValue();
    }
}
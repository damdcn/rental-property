package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.rentalproperty.adapters.GoodAdapter;
import com.example.rentalproperty.models.Booking;
import com.example.rentalproperty.models.Good;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerViewBookGoods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<Good> goodList = new ArrayList<Good>();

        DatabaseReference refG = database.getReference().child("Goods");
        refG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Good good = snapshot.getValue(Good.class);
                    good.setId(snapshot.getKey());
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getKey().equals("bookings")){
                            good.setBookings((HashMap<String, HashMap<String, Object>>) snap.getValue());
                        }
                    }
                    if(snapshot.hasChild("bookings")) {
                        for (DataSnapshot snap : snapshot.child("bookings").getChildren()){
                            Booking booking = snap.getValue(Booking.class);
                            if (booking.getClientId().equals(mAuth.getUid())) {
                                goodList.add(good);
                            }
                        }
                    }
                }
                GoodAdapter goodAdapter;
                Collections.sort(goodList, Good.GoodDateDescendingComparator);
                goodAdapter = new GoodAdapter(goodList, BookingsActivity.this);
                recyclerView.setAdapter(goodAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
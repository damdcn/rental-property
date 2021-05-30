package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.rentalproperty.adapters.BookingAdapter;
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
        ArrayList<Booking> bookingList = new ArrayList<Booking>();

        // retrieve data from db to put into adapter
        DatabaseReference refG = database.getReference().child("Goods");
        refG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodList.clear();
                bookingList.clear();
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
                            booking.setId(snap.getKey());
                            if (booking.getClientId().equals(mAuth.getUid())) {
                                goodList.add(good);
                                bookingList.add(booking);
                            }
                        }
                    }
                }
                BookingAdapter bookingAdapter;
                bookingAdapter = new BookingAdapter(bookingList, goodList, BookingsActivity.this);
                recyclerView.setAdapter(bookingAdapter);
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
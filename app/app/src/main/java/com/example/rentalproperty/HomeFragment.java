package com.example.rentalproperty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.adapters.GoodAdapter;
import com.example.rentalproperty.models.Booking;
import com.example.rentalproperty.models.Good;
import com.example.rentalproperty.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    TextView textViewTitle, textViewWelcome;
    Button buttonLoadMore;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user != null) userId = user.getUid();
        textViewWelcome = view.findViewById(R.id.welcomeTextView);
        textViewTitle = view.findViewById(R.id.textViewHome);
        buttonLoadMore = view.findViewById(R.id.buttonLoadMore);
        recyclerView = view.findViewById(R.id.recyclerViewRecentGoods);

        buttonLoadMore.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GoodsActivity.class));
        });

        /*===============================================================*/
        /*====================== PRINT CARDS ============================*/
        /*===============================================================*/

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Good> goodList = new ArrayList<Good>();

        DatabaseReference refG = FirebaseDatabase.getInstance().getReference().child("Goods");
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
                    goodList.add(good);
                }
                GoodAdapter goodAdapter;
                Collections.sort(goodList, Good.GoodDateDescendingComparator);
                if (goodList.size() > 3){
                    goodAdapter = new GoodAdapter(goodList.subList(0, 3), getActivity());
                } else {
                    goodAdapter = new GoodAdapter(goodList, getActivity());
                    if (goodList.size() == 0) textViewTitle.setText(R.string.no_good);
                }
                recyclerView.setAdapter(goodAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*Good[] goods = new Good[]{
                new Good("Maison bord de plage", 120, "Palavas", new Date()),
                new Good("Appart vue tour effeil", 150, "Paris", new Date()),
                new Good("Villa Cozy", 40, "Larzac", new Date())
        };*/

        /*===============================================================*/
        /*==================== CHECK FOR INTERNET =======================*/
        /*===============================================================*/

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // Connected to internet

        } else {
            // Not connected to internet
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            textViewTitle.setText(R.string.no_internet);
        }

        /*===============================================================*/
        /*====================== CUSTOM WELCOME =========================*/
        /*===============================================================*/

        if(user != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
            String uid = prefs.getString("uid", null);
            String firstname = prefs.getString("firstname", null);
            String lastname = prefs.getString("lastname", null);
            String email = prefs.getString("email", null);
            boolean isLandlord = prefs.getBoolean("isLandlord", false);

            textViewWelcome.setText(getText(R.string.welcome) + ", " + firstname + " !");
        }


        // Inflate the layout for this fragment
        return view;
    }
}
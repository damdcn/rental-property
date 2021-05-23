package com.example.rentalproperty;

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
import android.widget.ListView;

import com.example.rentalproperty.adapters.GoodAdapter;
import com.example.rentalproperty.models.Good;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView listView;
    private RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseDatabase.getInstance().getReference().child()
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // listView = view.findViewById(R.id.listViewRecentGoods);
        // ArrayList<String> list = new ArrayList<String>();
        // ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, list);
        // listView.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.recyclerViewRecentGoods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Good> goodList = new ArrayList<Good>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Goods");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Good good = snapshot.getValue(Good.class);
                    goodList.add(good);
                }
                GoodAdapter goodAdapter;
                if (goodList.size() > 3){
                    goodAdapter = new GoodAdapter(goodList.subList(0, 2), getActivity());
                } else {
                    goodAdapter = new GoodAdapter(goodList, getActivity());
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

        /*GoodAdapter goodAdapter = new GoodAdapter(goodList, getActivity());
        if (goodList.size() > 3){
            goodAdapter = new GoodAdapter(goodList.subList(0, 2), getActivity());
        } else {
            goodAdapter = new GoodAdapter(goodList, getActivity());
        }
        recyclerView.setAdapter(goodAdapter);*/



        // Inflate the layout for this fragment
        return view;
    }
}
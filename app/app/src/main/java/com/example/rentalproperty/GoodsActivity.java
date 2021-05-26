package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.widget.SearchView;

import com.example.rentalproperty.adapters.GoodAdapter;
import com.example.rentalproperty.models.Good;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoodsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoodAdapter goodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerViewSearchGoods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<Good> goodList = new ArrayList<Good>();
        ArrayList<String> goodIds = new ArrayList<String>();

        DatabaseReference refG = database.getReference().child("Goods");

        refG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Good good = snapshot.getValue(Good.class);
                    goodList.add(good);
                    goodIds.add(snapshot.getKey());
                }
                goodAdapter = new GoodAdapter(goodIds, goodList, GoodsActivity.this);
                recyclerView.setAdapter(goodAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+ getText(R.string.all)+ "</font>"));
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+ getText(R.string.all)+ "</font>"));
                else getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+ getText(R.string.results)+ "</font>"));
                goodAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
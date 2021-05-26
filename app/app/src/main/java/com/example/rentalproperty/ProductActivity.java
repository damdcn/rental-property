package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewTitle, textViewLocation, textViewPrice, textViewDescription, textViewRate, textViewCategory, textViewVisits;
    ImageView imageViewBack, imageViewCart, imageViewLike, imageViewProduct;
    Button buttonBookNow, buttonCall;
    RatingBar ratingBar;
    String productId;

    private boolean owner;
    private DBService dbService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mAuth = FirebaseAuth.getInstance();

        textViewTitle = findViewById(R.id.product_title);
        textViewLocation = findViewById(R.id.product_location);
        textViewPrice = findViewById(R.id.product_price);
        textViewDescription = findViewById(R.id.product_description);
        textViewRate = findViewById(R.id.product_rating);
        textViewCategory = findViewById(R.id.product_category);
        textViewVisits = findViewById(R.id.product_visits);
        imageViewProduct = findViewById(R.id.product_image);
        imageViewBack = findViewById(R.id.product_back);
        imageViewCart = findViewById(R.id.product_card);
        imageViewLike = findViewById(R.id.product_like);
        buttonBookNow = findViewById(R.id.product_add_button);
        buttonCall = findViewById(R.id.product_call_button);
        ratingBar = findViewById(R.id.product_ratingBar);

        productId = getIntent().getStringExtra("ID");
        String title = getIntent().getStringExtra("TITLE");
        String location = getIntent().getStringExtra("LOCATION");
        String date = getIntent().getStringExtra("DATE");
        String price = getIntent().getStringExtra("PRICE");
        String address = getIntent().getStringExtra("ADDRESS");
        String category = getIntent().getStringExtra("CATEGORY");
        String description = getIntent().getStringExtra("DESCRIPTION");
        int maxStay = getIntent().getIntExtra("MAXSTAY", 0);
        double rate = getIntent().getDoubleExtra("RATE", 0.0);
        String phone = getIntent().getStringExtra("PHONE");
        String imageUrl = getIntent().getStringExtra("IMG_URL");
        String authorId = getIntent().getStringExtra("AUTHOR_ID");

        dbService = new DBService();

        // Set a trash button if it's the owner's ad
        if(mAuth.getUid().equals(authorId)){
            owner = true;
            imageViewCart.setImageResource(R.drawable.ic_baseline_delete_24);
        }
        else{
            owner = false;
        }

        // Increment the visit counter of this ad
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Goods").child(productId).child("visits").setValue(ServerValue.increment(1));

        database.child("Goods").child(productId).child("visits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewVisits.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_baseline_house_24)
                .into(imageViewProduct);

        textViewTitle.setText(title);
        textViewLocation.setText(location);
        textViewPrice.setText(price);
        textViewDescription.setText(description);
        textViewCategory.setText(category);
        textViewRate.setText(Double.toString(rate));

        ratingBar.setRating((float) rate);
        ratingBar.setIsIndicator(true);




        imageViewLike.setOnClickListener(this);
        imageViewCart.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);
        buttonBookNow.setOnClickListener(this);

        if(dbService.isConnected()) {
            dbService.checkLike(productId, new MyCallback() {
                @Override
                public void onCallback(boolean isLiked) {
                    if (isLiked) imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dbService.isConnected()) {
            dbService.checkLike(productId, new MyCallback() {
                @Override
                public void onCallback(boolean isLiked) {
                    if (isLiked) imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.product_back:
                finish();
                break;
            case R.id.product_like:
                if(dbService.isConnected()){
                    dbService.toggleLike(productId, new MyCallback() {
                        @Override
                        public void onCallback(boolean isLiked) {
                            if(!isLiked){
                                dbService.like(productId);
                                imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                                Toast.makeText(getApplicationContext(), R.string.like, Toast.LENGTH_SHORT).show();
                            } else {
                                dbService.undoLike(productId);
                                imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                Toast.makeText(getApplicationContext(), R.string.undolike, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.product_add_button:
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.rentalproperty.models.Booking;
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
import java.text.SimpleDateFormat;
import java.util.HashMap;

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

        // Set a trash button and view count if it's the owner's ad
        if(mAuth.getUid() != null && mAuth.getUid().equals(authorId)){
            owner = true;
            textViewVisits.setVisibility(View.VISIBLE);
            imageViewCart.setImageResource(R.drawable.ic_baseline_delete_24);
        } else{
            owner = false;
            textViewVisits.setVisibility(View.GONE);
            imageViewCart.setVisibility(View.GONE);
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
        buttonCall.setOnClickListener(this);

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
                if(mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(this, BookActivity.class);
                    intent.putExtra("ID", getIntent().getStringExtra("ID"));
                    intent.putExtra("TITLE", getIntent().getStringExtra("TITLE"));
                    intent.putExtra("LOCATION", getIntent().getStringExtra("LOCATION"));
                    intent.putExtra("ADDRESS", getIntent().getStringExtra("ADDRESS"));
                    intent.putExtra("CATEGORY", getIntent().getStringExtra("CATEGORY"));
                    intent.putExtra("MAXSTAY", getIntent().getIntExtra("MAXSTAY", 0));
                    intent.putExtra("IMG_URL", getIntent().getStringExtra("IMG_URL"));
                    intent.putExtra("AUTHOR_ID", getIntent().getStringExtra("AUTHOR_ID"));
                    intent.putExtra("PRICE", Double.parseDouble(getIntent().getStringExtra("PRICE").trim().substring(0, getIntent().getStringExtra("PRICE").trim().length() - 2)));
                    intent.putExtra("BOOKINGS", getIntent().getSerializableExtra("BOOKINGS"));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.logged_to_continue, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.product_card:
                if(owner){

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods");
                                    ref.child(getIntent().getStringExtra("ID")).removeValue();
                                    Toast.makeText(getApplication(), getText(R.string.deleted_ad), Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.sure_delete_ad).setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener).show();
                } else {

                }
                break;
            case R.id.product_call_button:
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ getIntent().getStringExtra("PHONE")));
                startActivity(callIntent);
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:"+getIntent().getStringExtra("PHONE")));
//                startActivity(callIntent);
//                Toast.makeText(this, "tel:+"+getIntent().getStringExtra("PHONE"), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
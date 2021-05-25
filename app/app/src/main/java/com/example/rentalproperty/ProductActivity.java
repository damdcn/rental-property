package com.example.rentalproperty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewTitle, textViewLocation, textViewPrice, textViewDescription, textViewRate;
    ImageView imageViewBack, imageViewCart, imageViewLike;
    Button buttonBookNow;
    DBService dbService;
    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        textViewTitle = findViewById(R.id.product_title);
        textViewLocation = findViewById(R.id.product_location);
        textViewPrice = findViewById(R.id.product_price);
        textViewDescription = findViewById(R.id.product_description);
        textViewRate = findViewById(R.id.product_rating);
        imageViewBack = findViewById(R.id.product_back);
        imageViewCart = findViewById(R.id.product_card);
        imageViewLike = findViewById(R.id.product_like);
        buttonBookNow = findViewById(R.id.product_add_button);

        dbService = new DBService();

        productId = getIntent().getStringExtra("ID");
        String title = getIntent().getStringExtra("TITLE");
        String location = getIntent().getStringExtra("LOCATION");
        String date = getIntent().getStringExtra("DATE");
        String price = getIntent().getStringExtra("PRICE");


        textViewTitle.setText(title);
        textViewLocation.setText(location);
        textViewPrice.setText(price);
        imageViewLike.setOnClickListener(this);
        imageViewCart.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);
        buttonBookNow.setOnClickListener(this);


        dbService.checkLike(productId, new MyCallback() {
            @Override
            public void onCallback(boolean isLiked) {
                if(isLiked) imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.product_back:
                startActivity(new Intent(this, MainActivity.class));
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
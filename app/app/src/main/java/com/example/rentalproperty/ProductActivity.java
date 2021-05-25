package com.example.rentalproperty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewTitle, textViewLocation, textViewPrice, textViewDescription, textViewRate;
    ImageView imageViewBack, imageViewCart, imageViewLike;
    Button buttonBookNow;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.product_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.product_like:
                Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
                imageViewLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                break;
            case R.id.product_add_button:
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
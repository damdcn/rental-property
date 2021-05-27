package com.example.rentalproperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.models.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BookActivity extends AppCompatActivity {

    TextView textViewCalendarButton, textViewCalendarResult;
    EditText editTextCreditCard;
    Button buttonBook;
    ImageView imageViewPlace;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        textViewCalendarButton = findViewById(R.id.book_calendar_button);
        textViewCalendarResult = findViewById(R.id.book_calendar_result);
        editTextCreditCard = findViewById(R.id.book_credit_card);
        buttonBook = findViewById(R.id.book_continue_button);
        imageViewPlace = findViewById(R.id.book_round_imgview);

        String productId = getIntent().getStringExtra("ID");
        String title = getIntent().getStringExtra("TITLE");
        String location = getIntent().getStringExtra("LOCATION");
        double price = getIntent().getDoubleExtra("PRICE", 0.0);
        String address = getIntent().getStringExtra("ADDRESS");
        String category = getIntent().getStringExtra("CATEGORY");
        int maxStay = getIntent().getIntExtra("MAXSTAY", 0);
        String imageUrl = getIntent().getStringExtra("IMG_URL");
        String authorId = getIntent().getStringExtra("AUTHOR_ID");
        HashMap<String, HashMap<String, Object>> bookings =(HashMap<String, HashMap<String, Object>>) getIntent().getSerializableExtra("BOOKINGS");

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_baseline_home)
                .fit()
                .into(imageViewPlace);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCanceledOnTouchOutside(false);

        CalendarConstraints.Builder bookingConstraint = new CalendarConstraints.Builder();
        bookingConstraint.setValidator(new DateValidatorBookings(bookings));

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText(R.string.choose_dates);
        builder.setCalendarConstraints(bookingConstraint.build());

        final MaterialDatePicker materialDatePicker = builder.build();

        textViewCalendarButton.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Date arrival, departure;
                Pair<Long, Long> p = (Pair) selection;

                Long firstMillis = p.first;
                Long secondMillis = p.second;

                arrival = new Date(firstMillis);
                departure = new Date(secondMillis);

                long deltaMillis = Math.abs(secondMillis) - firstMillis;
                int delta = (int) TimeUnit.DAYS.convert(deltaMillis, TimeUnit.MILLISECONDS);

                if(delta <= maxStay && delta > 0) {
                    // valid range
                    double totalPrice = delta * price;

                    textViewCalendarResult.setText(
                            getText(R.string.arrival) + new SimpleDateFormat("dd/MM/yyyy").format(arrival) + "\n"
                                    + getText(R.string.departure) + new SimpleDateFormat("dd/MM/yyyy").format(departure) + "\n"
                                    + getText(R.string.time_of_stay) + delta + " " + getText(R.string.days)
                                    + "\n\n"
                                    + getText(R.string.price_per_night) + " : " + Double.toString(price) + " €\n"
                                    + getText(R.string.price_total) + " : " + Double.toString(totalPrice) + " €"
                    );

                    buttonBook.setOnClickListener(v -> {
                        String creditCard = editTextCreditCard.getText().toString().trim();

                        if(creditCard.isEmpty()){
                            editTextCreditCard.setError(getText(R.string.credit_card_required));
                            editTextCreditCard.requestFocus();
                            return;
                        }

                        progressDialog.setMessage(getText(R.string.adding_place));
                        progressDialog.show();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("arrival", firstMillis);
                        hashMap.put("departure", secondMillis);
                        hashMap.put("clientId", FirebaseAuth.getInstance().getUid());
                        hashMap.put("hostId", authorId);
                        hashMap.put("days", delta);
                        hashMap.put("price", totalPrice);
                        hashMap.put("creditCard", creditCard);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Goods").child(productId).child("bookings");
                        reference.push()
                                .setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // added to db
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), getText(R.string.added_book), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // fail adding to db
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                } else if (delta <= 0){
                    // invalid range : cannot stay for less than one day
                    textViewCalendarButton.setError(getText(R.string.min_stay_reached));
                    textViewCalendarButton.requestFocus();
                    Toast.makeText(getApplicationContext(), getText(R.string.min_stay_reached), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // host does not authorize stays of more than maxStay
                    textViewCalendarButton.setError(getText(R.string.max_stay_reached) + " " + maxStay + " " + getText(R.string.days));
                    textViewCalendarButton.requestFocus();
                    Toast.makeText(getApplicationContext(), getText(R.string.max_stay_reached) + " " + maxStay + " " + getText(R.string.days), Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
    }
}
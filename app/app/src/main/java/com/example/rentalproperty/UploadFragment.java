package com.example.rentalproperty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rentalproperty.models.Good;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class UploadFragment extends Fragment {

    private EditText etTitle, etLocation, etPrice;
    private Button add;

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        etTitle = view.findViewById(R.id.editTextTitle);
        etLocation = view.findViewById(R.id.editTextLocation);
        etPrice = view.findViewById(R.id.editTextPrice);
        add = view.findViewById(R.id.btnAdd);

        add.setOnClickListener(v -> {
            String txt_title = etTitle.getText().toString();
            String txt_location = etLocation.getText().toString();
            String txt_price = etPrice.getText().toString();
            double price = Double.parseDouble(txt_price);

            if(txt_location.isEmpty() || txt_title.isEmpty() || txt_price.isEmpty()){
                Toast.makeText(getActivity(), R.string.invalid_form, Toast.LENGTH_SHORT).show();
            }
            else {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Goods");

                // push to database
                reference.push().setValue(new Good(txt_title, price, txt_location, new Date()));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
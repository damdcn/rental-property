package com.example.rentalproperty;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.models.Booking;
import com.example.rentalproperty.models.Good;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UploadFragment extends Fragment {

    private ImageView imageViewIcon;
    private EditText editTextTitle, editTextLocation, editTextAdress, editTextDescription, editTextPhone, editTextMaxStay, editTextPrice;
    private TextView textViewCategory;
    private Button buttonAdd;
    private RelativeLayout relativeLayoutIcon;

    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private String[] storagePermissions;

    private Uri image_uri;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


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

        imageViewIcon = view.findViewById(R.id.upload_round_imgview);
        relativeLayoutIcon = view.findViewById(R.id.upload_round_img);
        editTextTitle = view.findViewById(R.id.upload_title);
        editTextLocation = view.findViewById(R.id.upload_location);
        editTextAdress = view.findViewById(R.id.upload_adress);
        editTextDescription = view.findViewById(R.id.upload_description);
        editTextPhone = view.findViewById(R.id.upload_contact_number);
        editTextMaxStay = view.findViewById(R.id.upload_max_stay);
        editTextPrice = view.findViewById(R.id.upload_price);
        textViewCategory = view.findViewById(R.id.upload_category);
        buttonAdd = view.findViewById(R.id.upload_button);

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCanceledOnTouchOutside(false);

        relativeLayoutIcon.setOnClickListener(v -> {
            showImagePickDialog();
        });

        textViewCategory.setOnClickListener(v -> {
            categoryDialog();
        });

        buttonAdd.setOnClickListener(v -> {
            inputData();
        });

        /*add.setOnClickListener(v -> {
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
        });*/

        // Inflate the layout for this fragment
        return view;
    }

    private String title, location, address, category, description, phone, maxStay, price;
    //check if inputs are correct
    private void inputData(){
        title = editTextTitle.getText().toString().trim();
        location = editTextLocation.getText().toString().trim();
        address = editTextAdress.getText().toString().trim();
        category = textViewCategory.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        maxStay = editTextMaxStay.getText().toString().trim();
        price = editTextPrice.getText().toString().trim();

        if(title.isEmpty()){
            editTextTitle.setError(getText(R.string.title_required));
            editTextTitle.requestFocus();
            return;
        }
        if(title.length() > 27){
            editTextTitle.setError(getText(R.string.title_toolong));
            editTextTitle.requestFocus();
            return;
        }
        if(location.isEmpty()){
            editTextLocation.setError(getText(R.string.location_required));
            editTextLocation.requestFocus();
            return;
        }
        if(address.isEmpty()){
            editTextAdress.setError(getText(R.string.address_required));
            editTextAdress.requestFocus();
            return;
        }
        if(category.isEmpty()){
            textViewCategory.setError(getText(R.string.category_required));
            textViewCategory.requestFocus();
            return;
        }
        if(description.isEmpty()){
            editTextDescription.setError(getText(R.string.description_required));
            editTextDescription.requestFocus();
            return;
        }
        if(description.length() > 250){
            editTextDescription.setError(getText(R.string.description_toolong));
            editTextDescription.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            editTextPhone.setError(getText(R.string.phone_required));
            editTextPhone.requestFocus();
            return;
        }
        if(phone.length() != 10){
            editTextPhone.setError(getText(R.string.phone_invalid));
            editTextPhone.requestFocus();
            return;
        }
        if(maxStay.isEmpty()){
            maxStay = "15";
        }
        if(price.isEmpty()){
            editTextPrice.setError(getText(R.string.price_required));
            editTextPrice.requestFocus();
            return;
        }

        addPlace();
    }

    //add to db
    public void addPlace(){
        progressDialog.setMessage(getText(R.string.adding_place));
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();

        if(image_uri == null){
            // upload without image
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", timestamp);
            hashMap.put("title", title);
            hashMap.put("location", location);
            hashMap.put("address", address);
            hashMap.put("category", category);
            hashMap.put("description", description);
            hashMap.put("contactNumber", phone);
            hashMap.put("maxStaying", Integer.parseInt(maxStay));
            hashMap.put("price", Double.parseDouble(price));
            hashMap.put("rate", 0.0);
            hashMap.put("imageUrl", "");
            hashMap.put("authorId", mAuth.getUid());
            hashMap.put("date", new Date());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Goods");
            reference.child(timestamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // added to db
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), getText(R.string.added_place), Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // fail adding to db
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            // upload with image
            String filePathAndName = "place_images/"+timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //url of img received
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", timestamp);
                                hashMap.put("title", title);
                                hashMap.put("location", location);
                                hashMap.put("address", address);
                                hashMap.put("category", category);
                                hashMap.put("description", description);
                                hashMap.put("contactNumber", phone);
                                hashMap.put("maxStaying", Integer.parseInt(maxStay));
                                hashMap.put("price", Double.parseDouble(price));
                                hashMap.put("rate", 0.0);
                                hashMap.put("imageUrl", ""+downloadImageUri);
                                hashMap.put("authorId", mAuth.getUid());
                                hashMap.put("date", new Date());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Goods");
                                reference.child(timestamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), getText(R.string.added_place), Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // fail adding to db
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to upload image
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void clearData(){
        editTextTitle.setText("");
        editTextLocation.setText("");
        editTextAdress.setText("");
        textViewCategory.setText("");
        editTextDescription.setText("");
        editTextPhone.setText("");
        editTextMaxStay.setText("");
        editTextPrice.setText("");
        image_uri = null;
        imageViewIcon.setImageResource(R.drawable.ic_baseline_image_24);
    }

    private void categoryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.category)
                .setItems(Constants.categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textViewCategory.setText(Constants.categories[which]);
                    }
                })
                .show();
    }

    private void showImagePickDialog(){
        if(checkStoragePermission()){
            Log.d("TAG", "Perm ok -> PICK");
            pickFromGallery();
        } else {
            Log.d("TAG", "Perm non ok -> ASK");
            requestStoragePermission();
        }
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
        //startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0){
            boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(storageAccepted){
                pickFromGallery();
            }else{
                Toast.makeText(getActivity(), R.string.storage_perms_req, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == -1){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                imageViewIcon.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
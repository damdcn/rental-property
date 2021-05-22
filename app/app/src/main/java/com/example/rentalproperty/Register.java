package com.example.rentalproperty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rentalproperty.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText etEmail, etFname, etLname, etPassword;
    Button registerBtn;
    CheckBox cbLandlord;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerBtn = findViewById(R.id.buttonRegisterSubmit);
        etEmail = findViewById(R.id.inputRegisterEmail);
        etFname = findViewById(R.id.inputRegisterFName);
        etLname = findViewById(R.id.inputRegisterLName);
        etPassword = findViewById(R.id.inputRegisterPassword);
        cbLandlord = findViewById(R.id.inputRegisterLandlord);

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String fname = etFname.getText().toString().trim();
        String lname = etLname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        Boolean isLandlord = cbLandlord.isChecked();

        if(email.isEmpty()){
            etEmail.setError(getText(R.string.email_required));
            etEmail.requestFocus();
            return;
        }
        if(fname.isEmpty()){
            etFname.setError(getText(R.string.fname_required));
            etFname.requestFocus();
            return;
        }
        if(lname.isEmpty()){
            etLname.setError(getText(R.string.lname_required));
            etLname.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etPassword.setError(getText(R.string.password_required));
            etPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getText(R.string.incorrect_email));
            etEmail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        User user = new User(email, fname, lname, isLandlord);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnCompleteListener(task2 -> {
                                    if(task2.isSuccessful()){
                                        Toast.makeText(this, R.string.register_success, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                    } else {
                                        Toast.makeText(this, R.string.register_failed, Toast.LENGTH_LONG).show();
                                    }
                        });
                    } else {
                        Toast.makeText(this, R.string.register_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
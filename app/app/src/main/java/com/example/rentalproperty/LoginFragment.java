package com.example.rentalproperty;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalproperty.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    TextView registerBtn;
    Button loginBtn;
    EditText etEmail, etPassword;

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        registerBtn = (TextView) view.findViewById(R.id.gotoRegister);
        loginBtn = (Button) view.findViewById(R.id.buttonLoginSubmit);
        etEmail = (EditText) view.findViewById(R.id.inputLoginEmail);
        etPassword = (EditText) view.findViewById(R.id.inputLoginPassword);

        registerBtn.setOnClickListener(v ->
            startActivity(new Intent(getActivity(), Register.class))
        );

        loginBtn.setOnClickListener(v->
            loginUser()
        );

        return view;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setError(getText(R.string.email_required));
            etEmail.requestFocus();
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_LONG).show();
                    getActivity().recreate();
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
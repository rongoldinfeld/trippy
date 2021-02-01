package com.colman.trippy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.colman.trippy.models.User;
import com.colman.trippy.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    EditText mEmail, mPassword, mConfirmPassword;
    ProgressBar mProgressBar;
    Button mRegisterBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.emailAddress);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        mRegisterBtn = findViewById(R.id.registerButton);
        mProgressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        UserModel.instance.isLoggedIn(result -> {
            if (result) {
                Toast.makeText(Register.this, "Already logged in!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mRegisterBtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Password length must be >=6 characters");
                return;
            }

            if (!TextUtils.equals(password, confirmPassword)) {
                mConfirmPassword.setError("Password are not the same");
                return;
            }

            User user = new User(email, password);
            mProgressBar.setVisibility(View.VISIBLE);
            UserModel.instance.registerUser(user, new UserModel.AddUserListener() {
                @Override
                public void onComplete(User result) {
                    Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onFailure(String message) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Register.this, "Error!" + message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
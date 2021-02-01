package com.colman.trippy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.colman.trippy.models.UserModel;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mButton;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.login_email_input);
        mPassword = findViewById(R.id.login_password_input);
        mButton = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.login_progress_bar);

        UserModel.instance.isLoggedIn(new UserModel.IsLoggedInListener() {
            @Override
            public void onComplete(Boolean result) {
                if (result) {
                    Toast.makeText(Login.this, "Already logged in!", Toast.LENGTH_SHORT).show();
                    UserModel.instance.logout();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "isLoggedIn in register, failed with reason: " + message);
            }
        });

        mButton.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);
            UserModel.instance.login(email, password, new UserModel.LoginUserListener() {
                @Override
                public void onComplete(Boolean result) {
                    Toast.makeText(Login.this, "Logging you in!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onFailure(String message) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}
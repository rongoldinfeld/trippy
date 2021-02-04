package com.colman.trippy.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.colman.trippy.Model.UserModel;
import com.colman.trippy.R;
import com.colman.trippy.View.Home.MainActivity;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mButton;
    TextView mRegisterLink;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.login_email_input);
        mPassword = findViewById(R.id.login_password_input);
        mButton = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.login_progress_bar);
        mRegisterLink = findViewById(R.id.register_now_text);

        mRegisterLink.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Register.class)));

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
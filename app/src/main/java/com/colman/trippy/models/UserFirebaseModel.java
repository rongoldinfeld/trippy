package com.colman.trippy.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class UserFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void register(User user, UserModel.AddUserListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TRIPLOG", "register successful" + user.getEmail());
                listener.onComplete(user);
            }
            if (task.getException() != null) {
                Log.d("TRIPLOG", "register failed" + user.getEmail());
                listener.onFailure(task.getException().getMessage());
            }
        });
    }

    public void login(String email, String password, UserModel.LoginUserListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TRIPLOG", "Login successful" + email);
                listener.onComplete(true);
            }
            if (task.getException() != null) {
                Log.d("TRIPLOG", "Login failed" + email);
                listener.onFailure(task.getException().getMessage());
            }
        });
    }

    public void isLoggedIn(UserModel.IsLoggedInListener listener) {
        listener.onComplete(firebaseAuth.getCurrentUser() != null);
    }

    public void logout() {
        firebaseAuth.signOut();
        Log.d("TRIPLOG", "User logged out");
    }
}

package com.colman.trippy.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public void register(User user, UserModel.AddUserListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TRIPLOG", "register successful" + user.getEmail());
                this.insertUser(user, new UserModel.Listener<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        listener.onComplete(user);
                    }

                    @Override
                    public void onFailure(String message) {
                        listener.onFailure(message);
                    }
                });
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

    private void insertUser(User user, UserModel.Listener<Boolean> listener) {
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d("TRIPLOG", "Tried to insert user when firebase auth current user returned null");
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fireStore.collection("users").document(userId);
        Map<String, Object> userToInsert = new HashMap();
        userToInsert.put("fullName", user.getFullName());
        userToInsert.put("email", user.getEmail());
        documentReference.set(userToInsert).addOnSuccessListener(aVoid -> {
            Log.d("TRIPLOG", "User inserted to user collection with id" + userId);
            listener.onComplete(true);
        }).addOnFailureListener(e -> {
            Log.d("TRIPLOG", "Error while inserting to users collection" + e.getMessage());
            listener.onFailure(e.getMessage());
        });
    }
}

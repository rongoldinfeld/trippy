package com.colman.trippy.Firebase;

import android.util.Log;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Location;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.User;
import com.colman.trippy.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public void register(User user, UserModel.AddUserListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TRIPLOG", "register successful" + user.getEmail());
                this.insertUser(user, new AppConsts.Listener<Boolean>() {
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
                String message = task.getException().getMessage();
                Log.d("TRIPLOG", "Login failed for email: " + email + " reason: " + message);
                listener.onFailure(message);
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

    private void insertUser(User user, AppConsts.Listener<Boolean> listener) {
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d("TRIPLOG", "Tried to insert user when firebase auth current user returned null");
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fireStore.collection("users").document(userId);
        HashMap<String, Object> userToInsert = new HashMap<>();
        userToInsert.put("fullName", user.getFullName());
        userToInsert.put("email", user.getEmail());
        userToInsert.put("trips", new ArrayList<Location>());
        documentReference.set(userToInsert).addOnSuccessListener(aVoid -> {
            Log.d("TRIPLOG", "User inserted to user collection with id" + userId);
            listener.onComplete(true);
        }).addOnFailureListener(e -> {
            Log.d("TRIPLOG", "Error while inserting to users collection" + e.getMessage());
            listener.onFailure(e.getMessage());
        });
    }

    public void getCurrentUserProfile(AppConsts.Listener<User> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentUserReference = fireStore.collection("users").document(userId);
        currentUserReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("TRIPLOG", "DocumentSnapshot data: " + document.getData());
                    User user = document.toObject(User.class);
                    listener.onComplete(user);
                } else {
                    Log.d("TRIPLOG", "No such document with id " + userId);
                }
            } else {
                Log.d("TRIPLOG", "get user with id (" + userId + ") failed with ", task.getException());
            }
        });
    }
}

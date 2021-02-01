package com.colman.trippy.models;

import com.google.firebase.auth.FirebaseAuth;

public class UserFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void register(User user, UserModel.AddUserListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(user);
            }
            if (task.getException() != null) {
                listener.onFailure(task.getException().getMessage());
            }
        });
    }

    public void login(String email, String password, UserModel.LoginUserListener listener) {
    }

    public void isLoggedIn(UserModel.IsLoggedInListener listener) {
        listener.onComplete(firebaseAuth.getCurrentUser() != null);
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}

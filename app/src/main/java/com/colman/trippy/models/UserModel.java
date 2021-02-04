package com.colman.trippy.models;

import com.colman.trippy.AppConsts;

public class UserModel {
    public final static UserModel instance = new UserModel();

    UserFirebaseModel modelFirebase = new UserFirebaseModel();

    private UserModel() {
    }

    public void getCurrentUser(AppConsts.Listener<User> listener) {
        modelFirebase.getCurrentUserProfile(listener);
    }

    public interface AddUserListener extends AppConsts.Listener<User> {

    }

    public void registerUser(final User user, final AddUserListener listener) {
        modelFirebase.register(user, listener);
    }


    public interface LoginUserListener extends AppConsts.Listener<Boolean> {

    }

    public void login(String email, String password, LoginUserListener listener) {
        modelFirebase.login(email, password, listener);
    }

    public void logout() {
        modelFirebase.logout();
    }

    public interface IsLoggedInListener extends AppConsts.Listener<Boolean> {
    }

    public void isLoggedIn(IsLoggedInListener listener) {
        modelFirebase.isLoggedIn(listener);
    }

    public void insertTrip(Trip trip, AppConsts.Listener<Boolean> booleanListener) {
        modelFirebase.insertTrip(trip, booleanListener);
    }

}

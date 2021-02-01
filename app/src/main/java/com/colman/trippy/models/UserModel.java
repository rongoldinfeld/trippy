package com.colman.trippy.models;

public class UserModel {
    public final static UserModel instance = new UserModel();

    UserFirebaseModel modelFirebase = new UserFirebaseModel();

    private UserModel() {
    }

    public interface Listener<T> {

        void onComplete(T result);

        void onFailure(String message);

    }

    public interface AddUserListener extends Listener<User> {

    }

    public void registerUser(final User user, final AddUserListener listener) {
        modelFirebase.register(user, listener);
    }


    public interface LoginUserListener extends Listener<Boolean> {

    }

    public void login(String email, String password, LoginUserListener listener) {
        modelFirebase.login(email, password, listener);
    }

    public void logout() {
        modelFirebase.logout();
    }

    public interface IsLoggedInListener extends Listener<Boolean> {
    }

    public void isLoggedIn(IsLoggedInListener listener) {
        modelFirebase.isLoggedIn(listener);
    }

}

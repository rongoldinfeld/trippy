package com.colman.trippy.models;

import android.util.Log;

import com.colman.trippy.AppConsts;

import java.util.ArrayList;

public class TripModel {

    public final static TripModel instance = new TripModel();
    private final UserModel userModel = UserModel.instance;

    public void addTrip(String tripName, Long fromDate, Long untilDate, ArrayList<Location> locations) {
        Trip trip = constructTripFromUserAndData(tripName, fromDate, untilDate, locations);
        userModel.insertTrip(trip, new AppConsts.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                Log.d("TRIPLOG", "Adding trip ended with result " + result.toString());
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private Trip constructTripFromUserAndData(String tripName, Long fromDate, Long untilDate, ArrayList<Location> locations) {
        return new Trip(new ArrayList<>(), tripName, fromDate, untilDate, false, locations);
    }
}

package com.colman.trippy.Model;

import android.util.Log;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Firebase.TripFirebaseModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SearchModel {
    public final static SearchModel instance = new SearchModel();
    TripFirebaseModel tripFirebaseModel = new TripFirebaseModel();
    MutableLiveData<List<Trip>> searchedTrips = new MutableLiveData<List<Trip>>();

    public SearchModel() {
        searchedTrips.setValue(new ArrayList<>());
    }

    public LiveData<List<Trip>> getSearchedTrips() {
        return searchedTrips;
    }

    public void searchTrips(String searchValue) {
        tripFirebaseModel.getSearchedTrips(new AppConsts.Listener<ArrayList<Trip>>() {
            @Override
            public void onComplete(ArrayList<Trip> result) {
                searchedTrips.setValue(result);
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to retrieve all trips. Error" + message);
            }
        }, searchValue);
    }
}

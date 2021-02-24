package com.colman.trippy.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Firebase.TripFirebaseModel;
import com.colman.trippy.Trippy;

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
        final SharedPreferences sp = Trippy.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long dataVersion = sp.getLong("dataVersion", 0);

        tripFirebaseModel.getSearchedTrips(dataVersion, new AppConsts.Listener<ArrayList<Trip>>() {
            @Override
            public void onComplete(ArrayList<Trip> result) {
                long lastDataVersion = 0;
                searchedTrips.setValue(result);
                sp.edit().putLong("dataVersion", lastDataVersion).apply();
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to retrieve all trips. Error" + message);
            }
        }, searchValue);
    }
}

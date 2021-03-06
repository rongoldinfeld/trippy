package com.colman.trippy.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;

import java.util.List;

public class TripViewModel extends ViewModel {
    private LiveData<List<Trip>> tripList;

    public TripViewModel() {
        tripList = TripModel.instance.getAllTrips();
    }

    public LiveData<List<Trip>> getTripList() {
        return tripList;
    }
}

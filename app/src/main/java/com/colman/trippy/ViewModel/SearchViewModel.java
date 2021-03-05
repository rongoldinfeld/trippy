package com.colman.trippy.ViewModel;

import com.colman.trippy.Model.SearchModel;
import com.colman.trippy.Model.Trip;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private LiveData<List<Trip>> tripList = SearchModel.instance.getSearchedTrips();

    public LiveData<List<Trip>> getTripList() {
        return tripList;
    }
}

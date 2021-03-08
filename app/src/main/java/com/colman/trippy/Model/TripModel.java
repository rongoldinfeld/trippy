package com.colman.trippy.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Firebase.TripFirebaseModel;
import com.colman.trippy.Sql.TripSqlModel;
import com.colman.trippy.Trippy;

import java.util.ArrayList;
import java.util.List;

public class TripModel {
    public final static TripModel instance = new TripModel();
    TripSqlModel tripSqlModel = new TripSqlModel();
    TripFirebaseModel tripFirebaseModel = new TripFirebaseModel();


    private TripModel() {

    }

    LiveData<List<Trip>> userTrips;

    public LiveData<List<Trip>> getAllTrips() {
        if (userTrips == null) {
            userTrips = tripSqlModel.getAllTrips();
            refreshTrips(null);
        }

        return userTrips;
    }

    public void refreshTrips(AppConsts.OnCompleteListener listener) {
        final SharedPreferences sp = Trippy.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long dataVersion = sp.getLong("dataVersion", 0);

        tripFirebaseModel.getAllTrips(dataVersion, new AppConsts.Listener<ArrayList<Trip>>() {
            @Override
            public void onComplete(ArrayList<Trip> result) {
                long lastDataVersion = 0;
                for (Trip t : result) {
                    tripSqlModel.addTrip(t, null);
                    if (t.getDataVersion() > lastDataVersion) {
                        lastDataVersion = t.getDataVersion();
                    }
                }
                sp.edit().putLong("dataVersion", lastDataVersion).apply();

                if (listener != null) {
                    listener.onComplete();
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to retrieve all trips. Error" + message);
            }
        });
    }

    public void addTrip(Trip trip, AppConsts.OnCompleteListener listener) {
        tripFirebaseModel.addTrip(trip, new AppConsts.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                Log.d("TRIPLOG", "Adding trip succeed with result: " + result.toString());
                refreshTrips(() -> {
                    Log.d("TRIPLOG", "Refreshing trips after adding, succeeded");
                    listener.onComplete();
                });
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to add trip. Error: " + message);
                listener.onComplete();
            }
        });
    }

    public void removeTrip(Trip trip, AppConsts.OnCompleteListener listener) {
        tripFirebaseModel.removeTrip(trip, new AppConsts.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                Log.d("TRIPLOG", "Successfully deleted trip with result: " + result.toString());
                tripSqlModel.deleteTrip(trip, () ->
                        refreshTrips(() -> {
                            Log.d("TRIPLOG", "Refreshing trips after deletion, succeeded");
                            listener.onComplete();
                        }));
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to delete trip. Error: " + message);
                listener.onComplete();
            }
        });
    }

    public void uploadImages(ArrayList<Location> locations, AppConsts.Listener<ArrayList<String>> listener) {
        ArrayList<String> imageUrls = new ArrayList<>();
        final int[] uploadCounter = {0};
        final int[] expectedToUpload = {locations.size()};
        for (Location location : locations) {

            if (!location.getImageUrl().contains("firebase")) {
                tripFirebaseModel.uploadImage(BitmapFactory.decodeFile(location.getImageUrl()), location.getLocationName() + " " + location.getDateVisited(), new AppConsts.Listener<String>() {
                    @Override
                    public void onComplete(String result) {
                        Log.d("TRIPLOG", "Done uploading image for location: " + location.getLocationName());
                        imageUrls.add(result);
                        location.setImageUrl(result);
                        uploadCounter[0]++;

                        if (uploadCounter[0] == expectedToUpload[0]) {
                            listener.onComplete(imageUrls);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d("TRIPLOG", "Failed uploading image for location: " + location.getLocationName());
                        listener.onFailure(message);
                    }
                });
            } else {
                expectedToUpload[0]--;
                if (uploadCounter[0] == expectedToUpload[0]) {
                    listener.onComplete(imageUrls);
                }
            }
        }

    }

    public void updateTrip(Trip initialTrip, Trip tripToUpdate, AppConsts.OnCompleteListener listener) {
        this.removeTrip(initialTrip, () -> {
            Log.d("TRIPLOG", "Removed trip... now inserting a new one");
            this.addTrip(tripToUpdate, () -> {
                Log.d("TRIPLOG", "Added trips and refreshed db");
                listener.onComplete();
            });
        });
    }
}

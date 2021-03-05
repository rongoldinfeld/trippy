package com.colman.trippy.Sql;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;

import java.util.List;

public class TripSqlModel {
    public LiveData<List<Trip>> getAllTrips() {
        return AppLocalDb.db.tripDao().getAllTrips();
    }

    public void addTrip(final Trip trip, final AppConsts.OnCompleteListener listener) {
        class AddTripAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.tripDao().insertAll(trip);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        AddTripAsyncTask task = new AddTripAsyncTask();
        task.execute();
    }

    public void deleteTrip(final Trip trip, final AppConsts.OnCompleteListener listener) {
        class RemoveTripAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.tripDao().delete(trip);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        RemoveTripAsyncTask task = new RemoveTripAsyncTask();
        task.execute();
    }
}

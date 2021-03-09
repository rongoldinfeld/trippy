package com.colman.trippy.Sql;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.colman.trippy.Model.Trip;

import java.util.List;

@Dao
public interface TripDao {
    @Query("select * from Trip order by fromDate ASC")
    LiveData<List<Trip>> getAllTrips();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Trip... trip);

    @Delete
    void delete(Trip trip);

    @Query("DELETE FROM Trip")
    void dropTripTable();
}

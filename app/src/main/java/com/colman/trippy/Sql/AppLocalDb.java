package com.colman.trippy.Sql;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.colman.trippy.Model.Trip;
import com.colman.trippy.Trippy;

@Database(entities = {Trip.class}, version = 5)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract TripDao tripDao();
}

public class AppLocalDb {
    static public AppLocalDbRepository db = Room
            .databaseBuilder(Trippy.context, AppLocalDbRepository.class, "db.db")
            .fallbackToDestructiveMigration()
            .build();
}


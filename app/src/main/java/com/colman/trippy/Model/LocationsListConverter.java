package com.colman.trippy.Model;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationsListConverter {
    @TypeConverter
    public static ArrayList<Location> storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new ArrayList<>(Collections.emptyList());
        }
        Type listType = new TypeToken<ArrayList<Location>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(ArrayList<Location> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
}

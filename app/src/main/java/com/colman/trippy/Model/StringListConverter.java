package com.colman.trippy.Model;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class StringListConverter {
    @TypeConverter
    public static ArrayList<String> storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new ArrayList<>(Collections.emptyList());
        }
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(ArrayList<String> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
}



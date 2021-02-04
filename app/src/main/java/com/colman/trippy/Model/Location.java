package com.colman.trippy.Model;

import java.util.ArrayList;

public class Location {
    private long dateVisited;
    private String locationName;

    public Location(long dateVisited, String locationName) {
        this.dateVisited = dateVisited;
        this.locationName = locationName;
    }

    public long getDateVisited() {
        return dateVisited;
    }

    public void setDateVisited(long dateVisited) {
        this.dateVisited = dateVisited;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public static ArrayList<Location> createLocationList(int num) {
        ArrayList<Location> arr = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            arr.add(new Location(System.currentTimeMillis(), "Israel" + i));
        }

        return arr;
    }

    public static Location generateEmptyLocation() {
        return new Location(0L, "");
    }
}

package com.colman.trippy.Model;

import java.util.ArrayList;

public class Location {
    private long dateVisited;
    private String locationName;

    public Location(long dateVisited, String locationName) {
        this.dateVisited = dateVisited;
        this.locationName = locationName;
    }

    public Location() {

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
}
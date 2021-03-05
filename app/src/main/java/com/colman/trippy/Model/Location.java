package com.colman.trippy.Model;

public class Location {
    private long dateVisited;
    private String locationName;
    private String imageUrl;

    public Location(long dateVisited, String locationName, String imageUrl) {
        this.dateVisited = dateVisited;
        this.locationName = locationName;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

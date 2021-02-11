package com.colman.trippy.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Trip {
    @PrimaryKey
    @NonNull
    private String name;

    @TypeConverters(StringListConverter.class)
    private ArrayList<String> participantsEmails;

    private long fromDate;
    private long untilDate;
    private boolean isPrivate;

    @TypeConverters(LocationsListConverter.class)
    private ArrayList<Location> locations;
    private Long lastUpdated;

    @Ignore
    public Trip(ArrayList<String> participantsEmails, String name, long fromDate, long untilDate, boolean isPrivate, ArrayList<Location> locations) {
        this.participantsEmails = participantsEmails;
        this.name = name;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        this.isPrivate = isPrivate;
        this.locations = locations;
    }

    public Trip() {

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("participantsEmails", this.participantsEmails);
        result.put("name", this.name);
        result.put("fromDate", this.fromDate);
        result.put("untilDate", this.untilDate);
        result.put("isPrivate", this.isPrivate);
        result.put("locations", this.locations);
        result.put("lastUpdated", System.currentTimeMillis() / 1000);
        return result;
    }

    public void fromMap(Map<String, Object> map) {
        this.participantsEmails = (ArrayList<String>) map.get("participantsEmails");
        this.name = (String) map.get("name");
        this.fromDate = (long) map.get("fromDate");
        this.untilDate = (long) map.get("untilDate");
        this.isPrivate = (Boolean) map.get("isPrivate");
        this.locations = (ArrayList<Location>) map.get("locations");
        Timestamp ts = (Timestamp) map.get("lastUpdated");
        this.lastUpdated = ts.getSeconds();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(long untilDate) {
        this.untilDate = untilDate;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getParticipantsEmails() {
        return participantsEmails;
    }

    public void setParticipantsEmails(ArrayList<String> participantsEmails) {
        this.participantsEmails = participantsEmails;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}


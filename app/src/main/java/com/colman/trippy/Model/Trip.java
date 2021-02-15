package com.colman.trippy.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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
    private boolean isTripPrivate;

    @TypeConverters(LocationsListConverter.class)
    private ArrayList<Location> locations;
    private Long dataVersion;

    @Ignore
    public Trip(ArrayList<String> participantsEmails, String name, long fromDate, long untilDate, boolean isTripPrivate, ArrayList<Location> locations) {
        this.participantsEmails = participantsEmails;
        this.name = name;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        this.isTripPrivate = isTripPrivate;
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
        result.put("tripPrivate", this.isTripPrivate);
        result.put("locations", this.locations);
        if (this.dataVersion != null) {
            result.put("dataVersion", this.dataVersion);
        } else {
            result.put("dataVersion", 0);
        }

        return result;
    }

    public void fromMap(Map<String, Object> map) {
        this.participantsEmails = (ArrayList<String>) map.get("participantsEmails");
        this.name = (String) map.get("name");
        this.fromDate = (long) map.get("fromDate");
        this.untilDate = (long) map.get("untilDate");
        this.isTripPrivate = (Boolean) map.get("tripPrivate");
        this.locations = (ArrayList<Location>) map.get("locations");
        this.dataVersion = (long) map.get("dataVersion");
        ;
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

    public boolean isTripPrivate() {
        return isTripPrivate;
    }

    public void setTripPrivate(boolean tripPrivate) {
        isTripPrivate = tripPrivate;
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

    public Long getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Long dataVersion) {
        this.dataVersion = dataVersion;
    }
}


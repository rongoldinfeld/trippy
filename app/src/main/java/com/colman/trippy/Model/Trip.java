package com.colman.trippy.Model;

import java.util.ArrayList;

public class Trip {
    private ArrayList<String> participantsEmails;
    private String name;
    private long fromDate;
    private long untilDate;
    private boolean isPrivate;
    private ArrayList<Location> locations;

    public Trip(ArrayList<String> participantsEmails, String name, long fromDate, long untilDate, boolean isPrivate, ArrayList<Location> locations) {
        this.participantsEmails = participantsEmails;
        this.name = name;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        this.isPrivate = isPrivate;
        this.locations = locations;
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
}

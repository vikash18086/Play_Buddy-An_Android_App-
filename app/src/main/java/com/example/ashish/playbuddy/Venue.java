package com.example.ashish.playbuddy;

public class Venue {

    private String venueId;
    private String venueName;
    private String sportId;

    public Venue() {
    }

    public Venue(String venueName, String sportId) {
        this.venueName = venueName;
        this.sportId = sportId;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }
}

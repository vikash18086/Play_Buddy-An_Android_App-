package com.example.ashish.playbuddy;

import android.util.Log;

public class PlayArea {
    String playAreaId;
    String sportId;
    String email;
    String venueId;
    String slotId;
    static String selectedSportId = null;
    static String selectedVenueId = null;
    static PlayArea selectedPlayarea = null;
    static boolean playerExist = false;

    public PlayArea(String playAreaId,String email,String sportId,String venueId,String slotId){
        setPlayAreaId(playAreaId);
        setSlotId(slotId);
        setVenueId(venueId);
        setEmail(email);
        setSportId(sportId);
    }

    boolean comparePlayarea(String email,String sportId,String venueId,String slotId)
    {
        Log.i("tag", "comparePlayarea: " +
                email + getEmail()+
                slotId + getSlotId() +
                venueId + getVenueId() +
                sportId + getSportId());

        if(getEmail().equalsIgnoreCase(email) &&
                getSportId().equalsIgnoreCase(sportId) &&
                getSlotId().equalsIgnoreCase(slotId)&&
                getVenueId().equalsIgnoreCase(venueId)
                )
            return true;
        return false;
    }

    boolean comparePlayarea(PlayArea pa)
    {
        Log.i("tag", "comparePlayarea: " +
                pa.getEmail() + getEmail()+
        pa.getSlotId() + getSlotId() +
        pa.getVenueId() + getVenueId() +
        pa.getSportId() + getSportId());

        if(pa.getEmail().equalsIgnoreCase(getEmail()) &&
                pa.getSportId().equalsIgnoreCase(getSportId()) &&
                        pa.getSlotId().equalsIgnoreCase(getSlotId())&&
                pa.getVenueId().equalsIgnoreCase(getVenueId())
                )
            return true;
        return false;
    }
    boolean comparePlaySportVenueSlot(String sportId,String venueId,String slotId)
    {
        if(getSportId().equalsIgnoreCase(sportId) && getSlotId().equalsIgnoreCase(slotId) && getVenueId().equalsIgnoreCase(venueId))
            return true;
        return false;
    }

    public PlayArea(String email,String sportId,String venueId,String slotId){
   //     setPlayAreaId(playAreaId);
        setSlotId(slotId);
        setVenueId(venueId);
        setEmail(email);
        setSportId(sportId);
    }
    public PlayArea()
    {

    }
    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getPlayAreaId() {
        return playAreaId;
    }

    public void setPlayAreaId(String playAreaId) {
        this.playAreaId = playAreaId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}

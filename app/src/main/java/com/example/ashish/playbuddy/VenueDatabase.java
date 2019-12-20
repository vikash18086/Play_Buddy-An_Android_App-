package com.example.ashish.playbuddy;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VenueDatabase {

    private DatabaseReference myDatabase;
    String venueId;
    // public static ArrayList<Sport> sports;

    public VenueDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj,String databaseName)
    {
        venueId=myDatabase.push().getKey();

        if(databaseName=="venue")
        {
            Venue venue=(Venue)obj;
            venue.setVenueId(venueId);
        }
        myDatabase.child(databaseName).child(venueId).setValue(obj);
    }

    public void updateVenue(Venue venue)
    {
        if(venue!=null) {
            Map<String,Object> taskMap = new HashMap<String, Object>();
            taskMap.put("venueName", venue.getVenueName());
            taskMap.put("sportId",venue.getSportId());

            myDatabase.child("venue").child(venue.getVenueId()).updateChildren(taskMap);
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

    public void remove(String venueId)
    {

        if(venueId!=null) {
            myDatabase.child("venue").child(venueId).removeValue();
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }
}

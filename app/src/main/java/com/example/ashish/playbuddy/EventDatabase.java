package com.example.ashish.playbuddy;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventDatabase {

    private DatabaseReference myDatabase;
    String eventId;
    public static ArrayList<Event> allEvent;

    public EventDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj,String databaseName)
    {
        eventId=myDatabase.push().getKey();

        if(databaseName=="event")
        {
            Event event=(Event) obj;
            event.setEventId(eventId);
        }
        myDatabase.child(databaseName).child(eventId).setValue(obj);
    }

    public void updateEvent(Event event)
    {
        if(event!=null) {
            Map<String,Object> taskMap = new HashMap<String, Object>();
            taskMap.put("eventDescription", event.getEventDescription());
            taskMap.put("eventTitle",event.getEventTitle());
            taskMap.put("eventDate",event.getEventDate());
            taskMap.put("sportId",event.getSportId());
            taskMap.put("venueId",event.getVenueId());
            taskMap.put("eventStartTime",event.getEventStartTime());
            taskMap.put("eventEndTime",event.getEventEndTime());
            myDatabase.child("event").child(event.getEventId()).updateChildren(taskMap);
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

    public void remove(String newsId)
    {

        if(newsId!=null) {
            myDatabase.child("event").child(newsId).removeValue();
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }


}
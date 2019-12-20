package com.example.ashish.playbuddy;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class TimeLineDatabase {


    private DatabaseReference myDatabase;
    String slotId;
   // public static ArrayList<Sport> sports;

    public TimeLineDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj,String databaseName)
    {
        slotId=myDatabase.push().getKey();

        if(databaseName=="timeline")
        {
            TimeLine timeLine=(TimeLine) obj;
            timeLine.setSlotId(slotId);
        }
        myDatabase.child(databaseName).child(slotId).setValue(obj);
    }

 /*   public void update(String sportId,String sportName)
    {
        Map<String,Object> taskMap = new HashMap<String, Object>();
        taskMap.put("sportName", sportName);

        if(sportId!=null) {
            myDatabase.child("sports").child(sportId).updateChildren(taskMap);
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }*/

    public void remove(String slotId)
    {

        if(slotId!=null) {
            myDatabase.child("timeline").child(slotId).removeValue();
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

}

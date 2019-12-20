package com.example.ashish.playbuddy;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class SportDatabase {


    private DatabaseReference myDatabase;
    String sportId;
   // public static ArrayList<Sport> sports;

    public SportDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj,String databaseName)
    {
        sportId=myDatabase.push().getKey();

        if(databaseName=="sports")
        {
            Sport sport=(Sport) obj;
            sport.setSportId(sportId);
        }
        myDatabase.child(databaseName).child(sportId).setValue(obj);
    }

    public void update(String sportId,String sportName)
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
    }

    public void remove(String sportId)
    {

        if(sportId!=null) {
            myDatabase.child("sports").child(sportId).removeValue();
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

}

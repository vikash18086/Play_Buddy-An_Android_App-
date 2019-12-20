package com.example.ashish.playbuddy;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayAreaDatabase {


    private DatabaseReference myDatabase;
    //  String playAreaId;
    public static ArrayList<PlayArea> playAreaArrayList;
   // public static PlayArea selectedPlayArea = null;
   // public static boolean playerExist = false;
    public static int tileCount = 0;
    public PlayAreaDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj, String databaseName) {
        String playAreaId = myDatabase.push().getKey();

        if (databaseName == "playarea") {
            PlayArea playArea = (PlayArea) obj;
            playArea.setPlayAreaId(playAreaId);
        }
        myDatabase.child(databaseName).child(playAreaId).setValue(obj);
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

    public void remove(PlayArea playArea) {

        getPlayAreabyemailId(playArea, new MyCallbackGetPlayareabyId() {
            @Override
            public void onCallback(PlayArea value) {
                PlayArea.selectedPlayarea = value;
                if (PlayArea.selectedPlayarea != null) {
                    myDatabase.child("playarea").child(PlayArea.selectedPlayarea.getPlayAreaId()).removeValue();
                    //  PlayArea.selectedPlayarea = null;
                } else {

                    Log.e("indus", value.getEmail()+" Data not updated as id is null");
                }
            }
        });

    }



        private void getPlayAreabyemailId(final PlayArea playArea, final MyCallbackGetPlayareabyId mycallback) {

        if (playArea != null) {
         //   PlayArea.selectedPlayarea = null;
            myDatabase.child("playarea").orderByChild("email").equalTo(playArea.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 //   PlayArea.selectedPlayarea = null;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        PlayArea pa = new PlayArea();

                        try {

                            pa.setSportId(ds.getValue(PlayArea.class).getSportId());
                            pa.setEmail(ds.getValue(PlayArea.class).getEmail());
                            pa.setVenueId(ds.getValue(PlayArea.class).getVenueId());
                            pa.setSlotId(ds.getValue(PlayArea.class).getSlotId());
                            pa.setPlayAreaId(ds.getValue(PlayArea.class).getPlayAreaId());

                        } catch (Exception e) {
                            Log.e("INDUS", "onDataChange: Exception in fetching from Db");
                            e.printStackTrace();
                        }

                        if (UserTImeLineRVFrag.classActive && playArea.comparePlayarea(pa)) {
                            PlayArea.selectedPlayarea = pa;
                            mycallback.onCallback(pa);
                            Log.i("TAG", "Set");
                            break;
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("error", "Failed to read value.", databaseError.toException());

                }
            });
        }

    }

    public interface MyCallbackGetPlayareabyId {
        void onCallback(PlayArea value);
    }

}

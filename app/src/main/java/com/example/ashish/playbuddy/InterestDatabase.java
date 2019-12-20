package com.example.ashish.playbuddy;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InterestDatabase {

    private DatabaseReference myDatabase;
    String interestId;

    public InterestDatabase() {

        myDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void write(Interest interest,String databaseName)
    {
        interestId=myDatabase.push().getKey();

        if(databaseName=="interest")
        {

            interest.setInterestId(interestId);
        }
        myDatabase.child(databaseName).child(interestId).setValue(interest);
    }


    public void remove(String email)
    {



        Query query = myDatabase.child("interest").orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot   snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }
}

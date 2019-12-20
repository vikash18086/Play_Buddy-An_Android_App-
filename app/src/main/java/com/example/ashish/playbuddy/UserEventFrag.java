package com.example.ashish.playbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class UserEventFrag extends Fragment {

    private DatabaseReference myDatabase,usereventDatabaseReference;

    public MyEventAdapter mAdapter;
    public static Event selectedEvent;
    public List<Event> eventList;
 //   public List<Sport> sportList = null;
    public List<Interest> interestList = null;
    AlertDialog.Builder showEvent;
    AlertDialog displayAlert;

    String sportName,venueName;

    private RecyclerView recyclerView;
    public static final String LOGTAG = "indus";

//    private UserEventFrag.OnFragmentInteractionListener mListener;

    public UserEventFrag() {
        // Required empty public constructor
    }

    Comparator<Event> cmp = new Comparator<Event>() {
        public int compare(Event o1, Event o2) {
            return o2.getEventDate().compareTo(o1.getEventDate());
        }
    };


    public static UserEventFrag newInstance(String param1, String param2) {
        UserEventFrag fragment = new UserEventFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Tag","passed layout created");
        myDatabase = FirebaseDatabase.getInstance().getReference();
        usereventDatabaseReference = FirebaseDatabase.getInstance().getReference();
        prepareInterestData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TAG","passed layout 0");

        View mview = inflater.inflate(R.layout.fragment_user_event,container,false);


        recyclerView = mview.findViewById(R.id.recycler_view_user_event2);
        prepareEventData();

        sportName = new String();
        venueName = new String();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG","passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                selectedEvent = eventList.get(position);
                sportName = new String("");
                venueName = new String("");

                //String sportid = selectedEvent.getSportId();

                String sportName3 =  prepareSportName(selectedEvent.getSportId(), new MyCallbackgetsName() {
                    @Override
                    public void onCallback(String value) {
                        sportName = value;

                        //sportName.concat(value);
                        /*sportName = value;
                                showEvent.setMessage("\n"
                                +"Date: "+selectedEvent.getEventDate().getDate()
                                +"-"+ selectedEvent.getEventDate().getMonth()
                                +"-"+ selectedEvent.getEventDate().getYear()
                                +"\nSport Name: "+ sportName
                                +"\nvenue: "+ venueName
                                +"\nEvent Start Time: "+ selectedEvent.getEventStartTime()
                                +"\nEvent End Time: "+ selectedEvent.getEventEndTime()
                                + "\n\n" +selectedEvent.getEventDescription());
                        */
                    }
                });
                String venueName3 = preparevenueName(selectedEvent.getVenueId(), new MyCallbackgetvName() {
                    @Override
                    public void onCallback(String value) {
                        venueName = value;
                        //venueName.concat(value);
                        showEvent.setMessage("\n"
                                +"Date: "+selectedEvent.getEventDate().getDate()
                                +"-"+ selectedEvent.getEventDate().getMonth()
                                +"-"+ selectedEvent.getEventDate().getYear()
                                +"\nSport Name: "+ sportName
                                +"\nVenue: "+ venueName
                                +"\nEvent Start Time: "+ selectedEvent.getEventStartTime()
                                +"\nEvent End Time: "+ selectedEvent.getEventEndTime()
                                + "\n\n" +selectedEvent.getEventDescription());
                        displayAlert = showEvent.create();
                        displayAlert.show();
                    }
                });

                showEvent = new AlertDialog.Builder(getActivity());
                /*showEvent.setMessage("\n"
                        +"Date: "+selectedEvent.getEventDate().getDate()
                        +"-"+ selectedEvent.getEventDate().getMonth()
                        +"-"+ selectedEvent.getEventDate().getYear()
                        +"\nSport Name: "+ sportName
                        +"\nvenue: "+ venueName
                        +"\nEvent Start Time: "+ selectedEvent.getEventStartTime()
                        +"\nEvent End Time: "+ selectedEvent.getEventEndTime()
                        + "\n\n" +selectedEvent.getEventDescription());*/
                showEvent.setTitle(selectedEvent.getEventTitle());

                showEvent.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

          //      displayAlert = showEvent.create();
          //      displayAlert.show();


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return mview;
    }

    private void prepareInterestData() {


        usereventDatabaseReference.child("interest").orderByChild("email").equalTo(NavigationDrawer.accountEmail).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                interestList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Interest interest = new Interest();
                    try {

                        interest.setSportId(ds.getValue(Interest.class).getSportId());
                        interest.setInterestId(ds.getValue(Interest.class).getInterestId());
                        interest.setEmail(ds.getValue(Interest.class).getEmail());
                    } catch (Exception e) {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }

                    interestList.add(interest);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }

    private void prepareEventData() {
        myDatabase.child("event").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Event event = new Event();
                    try {
                        event = ds.getValue(Event.class);
                       /* event.setVenueId(ds.getValue(Event.class).getVenueId());
                        event.setSportId(ds.getValue(Event.class).getSportId());
                        event.setEventDate(ds.getValue(Event.class).getEventDate());
                        event.setEventDescription(ds.getValue(Event.class).getEventDescription());
                        event.setEventEndTime(ds.getValue(Event.class).getEventEndTime());
                        event.setEventId(ds.getValue(Event.class).getEventId());
                        event.setEventStartTime(ds.getValue(Event.class).getEventStartTime());
                        event.setEventTitle(ds.getValue(Event.class).getEventTitle());*/

                    }
                    catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }



                    boolean match = false;
                    for (int j = 0; j < interestList.size();j++)
                    {
                        if(interestList.get(j).getSportId().equalsIgnoreCase(event.getSportId()))
                        {
                          //  Toast.makeText(getActivity(), eventList.size(), Toast.LENGTH_SHORT).show();

                            match = true;
                            eventList.add(event);
                            break;
                        }
                    }
                    /*if(match)
                    {
                    }*/
                }
            //    indusLog(eventList.size() + " " + eventList.get(0).getEventTitle());
                eventList.sort(cmp);
                if(mAdapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                mAdapter = new MyEventAdapter(eventList);

                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }


    private String preparevenueName(final String venueId, final MyCallbackgetvName mycallback) {
      //  sportName = null;
       // venueName = null;
        myDatabase.child("venue").orderByChild("venueId").equalTo(venueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            //    sportList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Venue venue = new Venue();
                    try {
                        venue = ds.getValue(Venue.class);
                      /*  venue.setSportId(ds.getValue(Venue.class).getSportId());
                        sport.setSportName(ds.getValue(Sport.class).getSportName());
*/
                    } catch (Exception e) {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }
                    /*if (sportid.equalsIgnoreCase(sport.getSportId())) {

                        sportName = sport.getSportName();
                    }*/

                    mycallback.onCallback(venue.getVenueName());
                  //  Log.i("TAG",venueName);
                    //   sportList.add(sport);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });
        return venueName;
    }


    private String prepareSportName(final String sportId,final MyCallbackgetsName mycallback) {
   //     sportName = null;

        myDatabase.child("sports").orderByChild("sportId").equalTo(sportId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

           //     sportList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Sport sport = new Sport();
                    try {
                        sport.setSportId(ds.getValue(Sport.class).getSportId());
                        sport.setSportName(ds.getValue(Sport.class).getSportName());

                    } catch (Exception e) {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }
                    /*if (sportid.equalsIgnoreCase(sport.getSportId())) {

                        sportName = sport.getSportName();
                    }*/

                    mycallback.onCallback(sport.getSportName());
                 //   sportList.add(sport);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });
        return sportName;
    }






    public interface MyCallbackgetsName {
        void onCallback(String value);
    }
    public interface MyCallbackgetvName {
        void onCallback(String value);
    }



    public void indusLog(String message)
    {
        Log.i(LOGTAG,message);
    }
    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}

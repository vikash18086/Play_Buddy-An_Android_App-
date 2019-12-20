package com.example.ashish.playbuddy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserFindBuddyFrag extends Fragment {



    private boolean classActive = false;

    private Spinner sportsSpinner,venueSpinner;
    String selectedVenueId = null;
    String selectedSportId = null;
    private DatabaseReference findbuddyMyDatabase;
    private DatabaseReference venueDatabase;
    private List<Sport> findbuddysportsList = null;
    private List<String> findbuddysportNameList=null;
    private List<Venue> findbuddyVenueList = null;
    private List<String> findbuddyVenueNameList=null;
    private ArrayAdapter<String> findbuddyadapter;
 //   int selectedVenueIndex,selectedSportIndex;
    Button selectSlot;
    private ArrayAdapter<String> findbuddyvenueadapter;
    public static final String LOGTAG = "indus";


   // private OnFragmentInteractionListener mListener;

    public UserFindBuddyFrag() {
        // Required empty public constructor
    }



    public static UserFindBuddyFrag newInstance(String param1, String param2) {
        UserFindBuddyFrag fragment = new UserFindBuddyFrag();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // db=new findbuddyDatabase();
        findbuddyMyDatabase= FirebaseDatabase.getInstance().getReference();
        venueDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_user_find_buddy, container, false);
        classActive = true;

        //find elements on view.
        selectSlot = rootview.findViewById(R.id.findbuddyselectSlot);
        sportsSpinner = rootview.findViewById(R.id.findbuddysportlist);
        venueSpinner = rootview.findViewById(R.id.findbuddyvenuelist);

        prepareSportData();
/*
        if(eventsportsList != null) {
            prepareVenueData(eventsportsList.get(sportsSpinner.getSelectedItemPosition()).getSportId());
        }
        if(AdminEventRecyclerViewFrag.selectedEvent !=null)
        {
            //  remove.setVisibility(View.VISIBLE);
            //save.setVisibility(View.INVISIBLE);
            remove.setVisibility(View.VISIBLE);
            title.setText(AdminEventRecyclerViewFrag.selectedEvent.getEventTitle());
            description.setText(AdminEventRecyclerViewFrag.selectedEvent.getEventTitle());
            starttime.setText("Start Time\n" + AdminEventRecyclerViewFrag.selectedEvent.getEventStartTime());
            endtime.setText("End Time\n" + AdminEventRecyclerViewFrag.selectedEvent.getEventEndTime());
            mDay = AdminEventRecyclerViewFrag.selectedEvent.getEventDate().getDate();
            mYear = AdminEventRecyclerViewFrag.selectedEvent.getEventDate().getYear();
            mMonth = AdminEventRecyclerViewFrag.selectedEvent.getEventDate().getMonth();
            startTime = AdminEventRecyclerViewFrag.selectedEvent.getEventStartTime();
            endTime = AdminEventRecyclerViewFrag.selectedEvent.getEventEndTime();

            eventdate.setText("Event Date\n" + mDay + "-"
                    + mMonth + "-"
                    +mYear
            );
            // Toast.makeText(getActivity(), ""+NewsAdminRecyclerViewFrag.selectedNews.getNews_id(), Toast.LENGTH_SHORT).show();
            //fill here
        }

*/

        sportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSportId = findbuddysportsList.get(sportsSpinner.getSelectedItemPosition()).getSportId();
                prepareVenueData(selectedSportId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(selectedSportId == null)
                {
                    Toast.makeText(getActivity(), "Select Sport First", Toast.LENGTH_SHORT).show();
                }
                else {
                    //    prepareVenueData(selectedSportId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
/*
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(AdminEventRecyclerViewFrag.selectedEvent!=null)
                {
                    String updatedDesc=description.getText().toString();
                    String updatedTitle=title.getText().toString();
                    AdminEventRecyclerViewFrag.selectedEvent.setEventDescription(updatedDesc);
                    AdminEventRecyclerViewFrag.selectedEvent.setEventTitle(updatedTitle);
                    AdminEventRecyclerViewFrag.selectedEvent.setEventStartTime(startTime);
                    AdminEventRecyclerViewFrag.selectedEvent.setEventEndTime(endTime);
                    //AdminEventRecyclerViewFrag.selectedEvent.setSportId("");
                    Date saveDate = new Date();
                    saveDate.setDate(mDay);
                    saveDate.setMonth(mMonth);
                    saveDate.setYear(mYear);
                    AdminEventRecyclerViewFrag.selectedEvent.setEventDate(saveDate);

                    //AdminEventRecyclerViewFrag.selectedEvent.setEventDescription(new Date());
                    if(endTime != null &&startTime != null &&eventVenueList != null && eventsportsList !=null && eventVenueList.size() > 0 && eventsportsList.size() > 0) {
                        sportspinnerPostion = sportsSpinner.getSelectedItemPosition();
                        venuespinnerPosition = venueSpinner.getSelectedItemPosition();
                        selectedSportId= eventsportsList.get(sportspinnerPostion).getSportId();
                        selectedVenueId = eventVenueList.get(venuespinnerPosition).getVenueId();
                        AdminEventRecyclerViewFrag.selectedEvent.setSportId(selectedSportId);
                        AdminEventRecyclerViewFrag.selectedEvent.setVenueId(selectedVenueId);
                        db.updateEvent(AdminEventRecyclerViewFrag.selectedEvent);
                        Toast.makeText(getActivity(), "Event Updated Successfully!!"+AdminEventRecyclerViewFrag.selectedEvent.getEventId(), Toast.LENGTH_SHORT).show();
                        callEventAdminRecyclerViewFrag();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    }
                    AdminEventRecyclerViewFrag.selectedEvent = null;
                }


                else {
                    String heading = title.getText().toString();
                    String desc = description.getText().toString();

                    if (heading.length() == 0 || desc.length() == 0) {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Event event = new Event();
                        AdminEventRecyclerViewFrag.selectedEvent = event;
                        AdminEventRecyclerViewFrag.selectedEvent.setEventDescription(desc);
                        AdminEventRecyclerViewFrag.selectedEvent.setEventTitle(heading);
                        AdminEventRecyclerViewFrag.selectedEvent.setEventStartTime(startTime);
                        AdminEventRecyclerViewFrag.selectedEvent.setEventEndTime(endTime);
                        //AdminEventRecyclerViewFrag.selectedEvent.setSportId("");


                        Date saveDate = new Date();
                        saveDate.setDate(mDay);
                        saveDate.setMonth(mMonth);
                        saveDate.setYear(mYear);
                        AdminEventRecyclerViewFrag.selectedEvent.setEventDate(saveDate);
                        //AdminEventRecyclerViewFrag.selectedEvent.setVenueId("");
                        if(endTime != null &&startTime != null && eventVenueList != null && eventsportsList !=null && eventVenueList.size() > 0 && eventsportsList.size() > 0) {
                            sportspinnerPostion = sportsSpinner.getSelectedItemPosition();
                            venuespinnerPosition = venueSpinner.getSelectedItemPosition();
                            selectedSportId= eventsportsList.get(sportspinnerPostion).getSportId();
                            selectedVenueId = eventVenueList.get(venuespinnerPosition).getVenueId();
                            AdminEventRecyclerViewFrag.selectedEvent.setSportId(selectedSportId);
                            AdminEventRecyclerViewFrag.selectedEvent.setVenueId(selectedVenueId);
                            db.write(AdminEventRecyclerViewFrag.selectedEvent, "event");
                            AdminEventRecyclerViewFrag.selectedEvent=null;
                            callEventAdminRecyclerViewFrag();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                        }
                        AdminEventRecyclerViewFrag.selectedEvent=null;

                    }
                }

            }
        });*/



        selectSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // title.setText(null);
                //description.setText(null);
                //AdminEventRecyclerViewFrag.selectedEvent=null;
                //callEventAdminRecyclerViewFrag();
                if( findbuddyVenueList != null && findbuddysportsList !=null && findbuddyVenueList.size() > 0 && findbuddysportsList.size() > 0) {
                    PlayArea.selectedSportId = findbuddysportsList.get(sportsSpinner.getSelectedItemPosition()).getSportId();
                    PlayArea.selectedVenueId = findbuddyVenueList.get(venueSpinner.getSelectedItemPosition()).getVenueId();
                    callUserTimeLineRVFrag();
                }
                else
                {
                    Toast.makeText(getActivity(), "Select Sport Name and Venue from List", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return rootview;

    }

    private void prepareVenueData(String selectedSportId) {

        if(selectedSportId != null)
        {
            venueDatabase.child("venue").orderByChild("sportId").equalTo(selectedSportId).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    findbuddyVenueList = new ArrayList<>();
                    findbuddyVenueNameList = new ArrayList<>();
/*
                    int count = 0;
*/
/*
                    selectedVenueIndex = -1;
*/
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Venue venue = new Venue();

                        try {

                            venue.setSportId(ds.getValue(Venue.class).getSportId());
                            venue.setVenueName(ds.getValue(Venue.class).getVenueName());
                            venue.setVenueId(ds.getValue(Venue.class).getVenueId());
                        } catch (Exception e) {
                            indusLog("Exception in fetching from Db");
                            e.printStackTrace();
                        }

                        findbuddyVenueNameList.add(venue.getVenueName());
                        findbuddyVenueList.add(venue);
                      /*  if(AdminEventRecyclerViewFrag.selectedEvent != null && AdminEventRecyclerViewFrag.selectedEvent.getVenueId().equalsIgnoreCase(venue.getVenueId()))
                        {
                            selectedVenueIndex = count;
                        }
                        count++;*/
                    }

                    if (findbuddyvenueadapter != null) {
                        //          indusToast(getActivity(),"new news added");
                    }
                    //spinner adapter
                    if (classActive) {
                        findbuddyvenueadapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                findbuddyVenueNameList);
                        findbuddyvenueadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        //set the adapter on the spinner
                        venueSpinner.setAdapter(findbuddyvenueadapter);
                       /* if(selectedVenueIndex != -1)
                        {
                            venueSpinner.setSelection(selectedVenueIndex);
                        }*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("error", "Failed to read value.", databaseError.toException());

                }
            });
        }
    }

    private void prepareSportData() {

        findbuddyMyDatabase.child("sports").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                findbuddysportsList = new ArrayList<>();
                findbuddysportNameList=new ArrayList<>();
                /*selectedSportIndex = -1;
                int count =0;*/
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Sport sport=new Sport();

                    try {

                        sport.setSportId(ds.getValue(Sport.class).getSportId());
                        sport.setSportName(ds.getValue(Sport.class).getSportName());
                    }
                    catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }

                    findbuddysportNameList.add(sport.getSportName());
                    findbuddysportsList.add(sport);
                  /*  if(sport.getSportId().equalsIgnoreCase(AdminEventRecyclerViewFrag.selectedEvent.getSportId()))
                    {
                        selectedSportIndex = count;
                    }
                    count++;*/
                }

                if(findbuddyadapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                //spinner adapter
                if (classActive) {
                    findbuddyadapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            findbuddysportNameList);
                    findbuddyadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //set the adapter on the spinner
                    sportsSpinner.setAdapter(findbuddyadapter);
                 /*   if(selectedSportIndex != -1)
                    {
                        sportsSpinner.setSelection(selectedSportIndex);
                    }*/
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }

    public void indusLog(String message)
    {
        Log.i(LOGTAG,message);
    }




    void callUserTimeLineRVFrag()
    {
        UserTImeLineRVFrag fr=new UserTImeLineRVFrag();

        FragmentManager fm = getFragmentManager();
        classActive = false;

        fm.beginTransaction().replace(R.id.frame_container,fr).commit();

    }

    /*//
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }*/
}

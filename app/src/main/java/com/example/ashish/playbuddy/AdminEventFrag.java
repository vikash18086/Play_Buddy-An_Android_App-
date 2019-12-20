package com.example.ashish.playbuddy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AdminEventFrag extends Fragment {
    Button save,cancel,remove;
    EditText title,description;

    private boolean classActive = false;

    private Spinner sportsSpinner,venueSpinner;
    String selectedVenueId = null;
    String selectedSportId = null;
    private DatabaseReference eventMyDatabase;
    private DatabaseReference venueDatabase;
    private List<Sport> eventsportsList = null;
    private List<String> eventsportNameList=null;
    private List<Venue> eventVenueList = null;
    private List<String> eventVenueNameList=null;
    private ArrayAdapter<String> eventadapter;
    int selectedVenueIndex,selectedSportIndex;
    private ArrayAdapter<String> eventvenueadapter;
    public static final String LOGTAG = "indus";

    String startTime,endTime;
    TextView eventdate,starttime,endtime;
    int mYear,mMonth,mDay,mHour,mMinute;
    EventDatabase db;
    private int sportspinnerPostion,venuespinnerPosition;
    public static final String LOGTAG12 = "indus";

    private AdminEventFrag.OnFragmentInteractionListener mListener;

    public AdminEventFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new EventDatabase();
        eventMyDatabase= FirebaseDatabase.getInstance().getReference();
        venueDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview=inflater.inflate(R.layout.fragment_admin_event, container, false);

        classActive = true;

        //find elements on view.
        title=rootview.findViewById(R.id.eventtitle);
        description=rootview.findViewById(R.id.eventdescription);
        save=rootview.findViewById(R.id.eventsave);
        cancel=rootview.findViewById(R.id.eventcancel);
        remove=rootview.findViewById(R.id.eventremove);
        starttime = rootview.findViewById(R.id.eventstarttime);
        endtime = rootview.findViewById(R.id.eventendtime);
        eventdate = rootview.findViewById(R.id.eventdate);
        sportsSpinner = rootview.findViewById(R.id.eventsportlist);
        venueSpinner = rootview.findViewById(R.id.eventvenuelist);

        prepareSportData();
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


        sportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSportId = eventsportsList.get(sportsSpinner.getSelectedItemPosition()).getSportId();
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
                        if (updatedDesc.length() == 0 || updatedTitle.length() == 0) {
                            Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                        db.updateEvent(AdminEventRecyclerViewFrag.selectedEvent);
                        Toast.makeText(getActivity(), "Event Updated Successfully!!", Toast.LENGTH_SHORT).show();
                        callEventAdminRecyclerViewFrag();
                        }
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
        });

        eventdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                eventdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                starttime.setText("Start Time\n" +hourOfDay + ":" + minute);
                                startTime = hourOfDay + ":" + minute;

                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();

            }
        });
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                endtime.setText("End Time\n" +hourOfDay + ":" + minute);
                                endTime = hourOfDay + ":" + minute;
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.remove(AdminEventRecyclerViewFrag.selectedEvent.getEventId());
                Toast.makeText(getActivity(), "Event Removed Successfully!!", Toast.LENGTH_SHORT).show();
                AdminEventRecyclerViewFrag.selectedEvent=null;
                callEventAdminRecyclerViewFrag();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(null);
                description.setText(null);
                AdminEventRecyclerViewFrag.selectedEvent=null;
                callEventAdminRecyclerViewFrag();

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

                eventVenueList = new ArrayList<>();
                eventVenueNameList = new ArrayList<>();
                int count = 0;
                selectedVenueIndex = -1;
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

                    eventVenueNameList.add(venue.getVenueName());
                    eventVenueList.add(venue);
                    if(AdminEventRecyclerViewFrag.selectedEvent != null && AdminEventRecyclerViewFrag.selectedEvent.getVenueId().equalsIgnoreCase(venue.getVenueId()))
                    {
                        selectedVenueIndex = count;
                    }
                count++;
                }

                if (eventvenueadapter != null) {
                    //          indusToast(getActivity(),"new news added");
                }
                //spinner adapter
                if (classActive) {
                    eventvenueadapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            eventVenueNameList);
                    eventvenueadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //set the adapter on the spinner
                    venueSpinner.setAdapter(eventvenueadapter);
                    if(selectedVenueIndex != -1)
                    {
                        venueSpinner.setSelection(selectedVenueIndex);
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

    private void prepareSportData() {

        eventMyDatabase.child("sports").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventsportsList = new ArrayList<>();
                eventsportNameList=new ArrayList<>();
                selectedSportIndex = -1;
                int count =0;
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

                    eventsportNameList.add(sport.getSportName());
                    eventsportsList.add(sport);
                    if(AdminEventRecyclerViewFrag.selectedEvent != null && sport.getSportId().equalsIgnoreCase(AdminEventRecyclerViewFrag.selectedEvent.getSportId()))
                    {
                        selectedSportIndex = count;
                    }
                    count++;
                }

                if(eventadapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                //spinner adapter
                if (classActive) {
                    eventadapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            eventsportNameList);
                    eventadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //set the adapter on the spinner
                    sportsSpinner.setAdapter(eventadapter);
                    if(selectedSportIndex != -1)
                    {
                        sportsSpinner.setSelection(selectedSportIndex);
                    }
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
        Log.i(LOGTAG12,message);
    }




    void callEventAdminRecyclerViewFrag()
    {
        AdminEventRecyclerViewFrag fr=new AdminEventRecyclerViewFrag();

        AdminEventRecyclerViewFrag.selectedEvent=null;
        FragmentManager fm = getFragmentManager();
        classActive = false;

        fm.beginTransaction().replace(R.id.frame_container,fr).commit();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminEventFrag.OnFragmentInteractionListener) {
            mListener = (AdminEventFrag.OnFragmentInteractionListener) context;
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

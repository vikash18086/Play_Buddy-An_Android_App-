package com.example.ashish.playbuddy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminEventRecyclerViewFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminEventRecyclerViewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEventRecyclerViewFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    public AdminEventRecyclerViewFrag() {
        // Required empty public constructor
    }

    private DatabaseReference myDatabase;
    public List<Event> eventList = null;
    private RecyclerView recyclerView;
    private MyEventAdapter mAdapter;
    FloatingActionButton addEvent;
    public static Event selectedEvent;
    //public static String title,description;
    // Database db;

    public static final String LOGTAG = "indus";
    //////

    Comparator<Event> cmp = new Comparator<Event>() {
        public int compare(Event o1, Event o2) {
            return o2.getEventDate().compareTo(o1.getEventDate());
        }
    };

    public static AdminEventRecyclerViewFrag newInstance(String param1, String param2) {
        AdminEventRecyclerViewFrag fragment = new AdminEventRecyclerViewFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","passed layout created");
        myDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bring data from database.
        prepareEventData();
        View mview = inflater.inflate(R.layout.fragment_fragment_admin_event_recyclerview, container, false);


        recyclerView = mview.findViewById(R.id.recycler_view_user_event);
        addEvent = mview.findViewById(R.id.addEvent);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG", "passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {


            public void onClick(View view, int position) {

                //selected event from recyclerView
                selectedEvent = eventList.get(position);

                AdminEventFrag fr = new AdminEventFrag();

                FragmentManager fm = getFragmentManager();

                fm.beginTransaction().replace(R.id.frame_container, fr).commit();


                // Toast.makeText(getActivity(), event + "", Toast.LENGTH_SHORT).show();
            }

            public void onLongClick(View view, int position) {

            }
        }));

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AdminEventFrag fr = new AdminEventFrag();

                FragmentManager fm = getFragmentManager();

                fm.beginTransaction().replace(R.id.frame_container, fr).commit();

            }
        });


        return mview;
    }


    //to read data from database and set it to recyclerView Adapter
    private void prepareEventData() {

        myDatabase.child("event").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Event event = new Event();
                    try {
                        event.setEventId(ds.getValue(Event.class).getEventId());
                        event.setEventDate(ds.getValue(Event.class).getEventDate());
                        event.setEventDescription(ds.getValue(Event.class).getEventDescription());
                        event.setEventTitle(ds.getValue(Event.class).getEventTitle());
                        event.setEventStartTime(ds.getValue(Event.class).getEventStartTime());
                        event.setSportId(ds.getValue(Event.class).getSportId());
                        event.setVenueId(ds.getValue(Event.class).getVenueId());
                        event.setEventEndTime(ds.getValue(Event.class).getEventEndTime());

                    } catch (Exception e) {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }


                    eventList.add(event);

                }
                eventList.sort(cmp);
                if (mAdapter != null) {
                    //          indusToast(getActivity(),"new Event added");
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminEventRecyclerViewFrag.OnFragmentInteractionListener) {
            mListener = (AdminEventRecyclerViewFrag.OnFragmentInteractionListener) context;
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

    public void indusLog(String message) {
        Log.i(LOGTAG, message);
    }

    public void indusToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        indusLog(message);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
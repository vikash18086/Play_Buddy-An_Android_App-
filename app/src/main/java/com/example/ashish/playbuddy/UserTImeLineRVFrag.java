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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.ashish.playbuddy.MyTimeLineAdapter.ClickListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class UserTImeLineRVFrag extends Fragment {
    //  my code
    private DatabaseReference myDatabase;
    public List<TimeLine> timeLineList = null;
    private RecyclerView recyclerView;
    private MyTimeLineAdapter mAdapter;
    //FloatingActionButton add;
    public static TimeLine selectedTile;
    static boolean classActive = false;
    Button changesport;
    TextView sportNameshow,venueNameshow;
    //public static String title,description;
    // Database db;

    public static final String LOGTAG = "indus";
    //////

    Comparator<TimeLine> cmp = new Comparator<TimeLine>() {
        public int compare(TimeLine o1, TimeLine o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    };

    public UserTImeLineRVFrag() {
        // Required empty public constructor
    }


    public static UserTImeLineRVFrag newInstance(String param1, String param2) {
        UserTImeLineRVFrag fragment = new UserTImeLineRVFrag();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        classActive = true;
        Log.i("TAG","passed layout 0");
        View mview = inflater.inflate(R.layout.fragment_user_time_line_rv, container, false);

        //bring data from database.
        prepareTimeLine();

        recyclerView = mview.findViewById(R.id.recycler_view_timelinetiles);
        changesport = mview.findViewById(R.id.timelinechangesportvenue);
        venueNameshow = mview.findViewById(R.id.timelinevenuename);
        sportNameshow = mview.findViewById(R.id.timelinesportname);

        prepareSportName(PlayArea.selectedSportId, new MyCallbackgetsName() {
            @Override
            public void onCallback(String value) {
                sportNameshow.setText("Sport: " + value);
            }
        });
        preparevenueName(PlayArea.selectedVenueId, new MyCallbackgetvName() {
            @Override
            public void onCallback(String value) {
                venueNameshow.setText("Venue: " +value);
            }
        });
        changesport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUserFindBuddyFrag();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG","passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {


            public void onClick(View view, int position) {

                //selected news from recyclerView
                selectedTile = timeLineList.get(position);
/*
                AdminNewsFrag fr = new AdminNewsFrag();

                FragmentManager fm = getFragmentManager();

                fm.beginTransaction().replace(R.id.frame_container,fr).commit();

*/
                // Toast.makeText(getActivity(), news + "", Toast.LENGTH_SHORT).show();
            }

            public void onLongClick(View view, int position) {

            }
        }));

        return mview;
    }


    //to read data from database and set it to recyclerView Adapter
    private void prepareTimeLine() {

        myDatabase.child("timeline").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                timeLineList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    TimeLine timeLine = new TimeLine();
                    try {
                        timeLine.setSlotId(ds.getValue(TimeLine.class).getSlotId());
                        timeLine.setEndTime(ds.getValue(TimeLine.class).getEndTime());
                        timeLine.setStartTime(ds.getValue(TimeLine.class).getStartTime());
                    }
                    catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }
                    timeLineList.add(timeLine);

                }
          //      timeLineList.sort(cmp);
                if(mAdapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                if(UserTImeLineRVFrag.classActive) {
                    mAdapter = new MyTimeLineAdapter(timeLineList, new ClickListener() {
                        @Override
                        public void onPositionClicked(int position) {

                        }
                    });

                    recyclerView.setAdapter(mAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }

    void callUserFindBuddyFrag()
    {
        UserFindBuddyFrag fr=new UserFindBuddyFrag();

        FragmentManager fm = getFragmentManager();
        classActive = false;

        fm.beginTransaction().replace(R.id.frame_container,fr).commit();
    }
/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserTImeLineRVFrag.OnFragmentInteractionListener) {
            mListener = (UserTImeLineRVFrag.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    private void preparevenueName(final String venueId, final MyCallbackgetvName mycallback) {
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
       // return venueName;
    }


    private void prepareSportName(final String sportId,final MyCallbackgetsName mycallback) {
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
      //  return sportName;
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

    public  void indusToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        indusLog(message);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

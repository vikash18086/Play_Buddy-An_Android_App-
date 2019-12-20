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


public class AdminVenueRecyclerViewfrag extends Fragment {

    private DatabaseReference myDatabase;
    public List<Venue> venueList = null;
    private RecyclerView recyclerView;
    private MyVenueAdapter mAdapter;
    FloatingActionButton addVenue;
    public static Venue selectedVenue;
    public static final String LOGTAG = "indus";


    public AdminVenueRecyclerViewfrag() {

    }

    public static AdminVenueRecyclerViewfrag newInstance(String param1, String param2) {
        AdminVenueRecyclerViewfrag fragment = new AdminVenueRecyclerViewfrag();
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

        Log.i("TAG","passed layout 0");
        View mview = inflater.inflate(R.layout.fragment_admin_venue_recycler_viewfrag, container, false);

        //bring data from database.
        prepareVenueData();

        recyclerView = mview.findViewById(R.id.recycler_view);
        addVenue=mview.findViewById(R.id.addVenue);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG","passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {


            public void onClick(View view, int position) {

                //selected news from recyclerView
                selectedVenue = venueList.get(position);

                AdminVenueFragment fr = new AdminVenueFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_container,fr).commit();
            }

            public void onLongClick(View view, int position) {

            }
        }));

        addVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AdminVenueFragment fr = new AdminVenueFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_container,fr).commit();

            }
        });


        return mview;

    }

    //to read data from database and set it to adapter.......

    private void prepareVenueData() {

        myDatabase.child("venue").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                venueList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Venue venue = new Venue();
                    try {
                        venue.setSportId(ds.getValue(Venue.class).getSportId());
                        venue.setVenueId(ds.getValue(Venue.class).getVenueId());
                        venue.setVenueName(ds.getValue(Venue.class).getVenueName());
                    }
                    catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }


                    venueList.add(venue);

                }

                if(mAdapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                mAdapter = new MyVenueAdapter(venueList);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
}
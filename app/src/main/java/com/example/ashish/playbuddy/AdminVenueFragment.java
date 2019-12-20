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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AdminVenueFragment extends Fragment {

   // private OnFragmentInteractionListener mListener;
   //log declaration
   public static final String LOGTAG = "indus";

    //xml data
    private Button save,cancel,venueremove;
    private EditText venueEdit;
    private Spinner sportsSpinner;
    private VenueDatabase venueDatabase;
    private List<Sport> sportsList = null;
    private List<String> sportNameList=null;
    private DatabaseReference myDatabase;
    private ArrayAdapter<String> adapter;
    int selectedSportIndex = -1;
    public boolean classActive = false;
    List <Transaction.Handler> handles= new ArrayList<>();
    //private  SportDatabase sportDatabase;
    //private VenueDatabase db;
    private int spinnerPosition;

    public AdminVenueFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        venueDatabase=new VenueDatabase();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        //sportDatabase=new SportDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View venueView= inflater.inflate(R.layout.fragment_admin_venue, container, false);
        classActive = true;
        save=venueView.findViewById(R.id.venuesave);
        cancel=venueView.findViewById(R.id.venuecancle);
        venueEdit=venueView.findViewById(R.id.venueEdit);
        sportsSpinner=venueView.findViewById(R.id.venue_sportlist);
        venueremove=venueView.findViewById(R.id.venueremove);

        //read data on spinner
        prepareSportsData();

        sportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                indusLog("position : "+i);
                 spinnerPosition=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(AdminVenueRecyclerViewfrag.selectedVenue !=null)
        {
            //  remove.setVisibility(View.VISIBLE);
            //save.setVisibility(View.INVISIBLE);
            venueremove.setVisibility(View.VISIBLE);
            venueEdit.setText(AdminVenueRecyclerViewfrag.selectedVenue.getVenueName());
            // Toast.makeText(getActivity(), ""+.selectedNews.getNews_id(), Toast.LENGTH_SHORT).show();
            //fill here
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(AdminVenueRecyclerViewfrag.selectedVenue!=null)
                {
                    String updatedVenueName=venueEdit.getText().toString();
                    String updatedSportsId=sportsList.get(spinnerPosition).getSportId();
                    Venue venue = new Venue();
                    venue.setVenueId(AdminVenueRecyclerViewfrag.selectedVenue.getVenueId());
                    venue.setSportId(updatedSportsId);
                    venue.setVenueName(updatedVenueName);
                    if(venue.getVenueName().length() == 0)
                    {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        venueDatabase.updateVenue(venue);
                        Toast.makeText(getActivity(), "Updated Venue " + venue.getVenueName() + " Successfully!", Toast.LENGTH_SHORT).show();
                        AdminVenueRecyclerViewfrag.selectedVenue = null;
                        callAdminVenueRecyclerViewfrag();
                    }
                }
                else {
                    Venue venue = new Venue();
                    venue.setVenueName(venueEdit.getText().toString());
                    venue.setSportId(sportsList.get(spinnerPosition).getSportId());


                    if (venueEdit.getText().toString().length() == 0 && sportsList.get(spinnerPosition).getSportId().length() == 0) {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Venue venue1 = new Venue(venueEdit.getText().toString(), sportsList.get(spinnerPosition).getSportId());
                        venueDatabase.write(venue1, "venue");
                        AdminVenueRecyclerViewfrag.selectedVenue=null;
                        callAdminVenueRecyclerViewfrag();
                    }
                }
            }
        });

        venueremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                venueDatabase.remove(AdminVenueRecyclerViewfrag.selectedVenue.getVenueId());
                Toast.makeText(getActivity(), "Venue Removed Successfully!!", Toast.LENGTH_SHORT).show();
                AdminVenueRecyclerViewfrag.selectedVenue=null;
                callAdminVenueRecyclerViewfrag();
            }
        });


        //cancel to go back
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueEdit.setText(null);

                AdminVenueRecyclerViewfrag.selectedVenue=null;
                callAdminVenueRecyclerViewfrag();

            }
        });
        return venueView;
    }

    //to read data from database and set it to recyclerView Adapter
    private void prepareSportsData() {

        myDatabase.child("sports").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //handles.add(myDatabase.child("sports").);
                sportsList = new ArrayList<>();
                sportNameList=new ArrayList<>();
                int count = 0;
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

                    sportNameList.add(sport.getSportName());
                    sportsList.add(sport);
                    if(AdminVenueRecyclerViewfrag.selectedVenue !=null && AdminVenueRecyclerViewfrag.selectedVenue.getSportId().equalsIgnoreCase(sport.getSportId()))
                    {
                       selectedSportIndex = count;
                    }
                count++;
                }

                if(adapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                //spinner adapter
                if(classActive) {
                    adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item,
                            sportNameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //set the adapter on the spinner
                    sportsSpinner.setAdapter(adapter);
                    if(selectedSportIndex !=-1)
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


    void callAdminVenueRecyclerViewfrag()
    {
        AdminVenueRecyclerViewfrag fr=new AdminVenueRecyclerViewfrag();

        FragmentManager fm = getFragmentManager();
        classActive = false;
        fm.beginTransaction().replace(R.id.frame_container,fr).commit();

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

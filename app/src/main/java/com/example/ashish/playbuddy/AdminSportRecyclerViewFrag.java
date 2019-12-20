package com.example.ashish.playbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdminSportRecyclerViewFrag extends Fragment {

    //log declaration
    public static final String LOGTAG = "indus";
    //declarations
    private DatabaseReference myDatabase;
    private RecyclerView recyclerView;
    private MySportsAdapter mAdapter;
    private FloatingActionButton addSports;
    private Sport selectedSport;
    private List<Sport> sportsList = null;
   // private  List<Venue> venueList=null;
    private SportDatabase sportDatabase;

    /*//frag listener
    private OnFragmentInteractionListener mListener;
*/
    public AdminSportRecyclerViewFrag() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","passed sports layout created");
        myDatabase = FirebaseDatabase.getInstance().getReference();
        sportDatabase=new SportDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview= inflater.inflate(R.layout.fragment_admin_sport_recycler_view, container, false);

        //prepare data
        prepareSportsData();

        //inflating xml
        recyclerView = mview.findViewById(R.id.sport_recycler_view);
        addSports=mview.findViewById(R.id.addSport);

        //setting recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG","passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        //on click of recyclerView
        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {


            public void onClick(final View view, int position) {

                //selected news from recyclerView
                selectedSport = sportsList.get(position);

                //add dialog here
                // Creating alert Dialog with one Button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Sport Name");

                // Setting Dialog Message
                alertDialog.setMessage("Enter Sports");


                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(selectedSport.getSportName());
                alertDialog.setView(input);



               alertDialog.setNeutralButton("Remove Sport", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sportDatabase.remove(selectedSport.getSportId());
                        indusLog("successfully removed from sports Database!!");
                    }
                });
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                String sportName=input.getText().toString();
                                if(sportName.length()==0)
                                {
                                    Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                                }
                                else if(sportName.equals(selectedSport.getSportName()))
                                {
                                    Toast.makeText(getActivity(), "Entered Same Sport Name!!ClickAgain to change.", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                else
                                {
                                    sportDatabase.update(selectedSport.getSportId(),sportName);
                                    indusLog("sports database updated successfully");
                                }

                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                // closed

                // Showing Alert Message
                alertDialog.show();



                // Toast.makeText(getActivity(), news + "", Toast.LENGTH_SHORT).show();
            }

            public void onLongClick(View view, int position) {

            }
        }));

        addSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //add dialog here
                // Creating alert Dialog with one Button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Sport Name");

                // Setting Dialog Message
                alertDialog.setMessage("Enter Sports");


                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);


               /* //removing from database
                alertDialog.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });*/

               // Setting Positive "Yes" Button

                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                String sportName=input.getText().toString();
                                if(sportName.length()==0)
                                {
                                    Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Sport sport=new Sport(sportName);
                                    sportDatabase.write(sport,"sports");
                                    indusLog("sports database written successfully");

                                }


                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                // closed

                // Showing Alert Message
                alertDialog.show();



            }
        });

        return mview;
    }


    //to read data from database and set it to recyclerView Adapter
    private void prepareSportsData() {

        myDatabase.child("sports").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sportsList = new ArrayList<>();
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


                    sportsList.add(sport);


                }
                if(mAdapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }

                mAdapter = new MySportsAdapter(sportsList);

                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }


  /*  // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

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

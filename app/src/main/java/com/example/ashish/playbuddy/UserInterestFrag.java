package com.example.ashish.playbuddy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class UserInterestFrag extends Fragment {
    private DatabaseReference myDatabase;
    private ListView listView;
    private Button saveInterest;
    private Set<Integer> selectedInterestList=null;
    private List<Sport> sportsList = null;
    private InterestDatabase interestDatabase;
    private List<String> sportsListNames = null;
    public static final String LOGTAG = "indus";
    private ArrayAdapter<String> adapterList;
    private String userEmail=NavigationDrawer.accountEmail;
    private List<Integer> sportsAlreadyInDb=null;

    public UserInterestFrag() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase = FirebaseDatabase.getInstance().getReference();
        selectedInterestList=new LinkedHashSet<>();
        interestDatabase=new InterestDatabase();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root=inflater.inflate(R.layout.fragment_user_interest, container, false);

        listView=root.findViewById(R.id.interestListview);
        saveInterest=root.findViewById(R.id.insterestSave);

        prepareInterestList(new MyCallbackinvokeSportData() {
            @Override
            public void onCallback() {
                prepareSportsData();
            }
        });




        //multiple choice
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //selectedInterestList.add(sportsListNames.get(position-1));
                indusLog("pos :"+position);


                if(selectedInterestList.contains(position))
                {
                    //if same item tabbed again then remove it from list to avoid blank data in db
                 selectedInterestList.remove(position);
                }
                else {
                    selectedInterestList.add(position);
                }

                }


        });


        saveInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sportID;

                if(selectedInterestList.size()!=0) {


                    //removing all interest from db

                        interestDatabase.remove(userEmail);
                        indusLog("items removed ");


                    //updating with new list of interest
                    for (int pos : selectedInterestList) {

                        sportID=sportsList.get(pos).getSportId();
                        Interest interest=new Interest(userEmail,sportID,pos);
                        interestDatabase.write(interest,"interest");
                        indusLog("interests updated succesfully");
                        Toast.makeText(getActivity(), "Interests updated successfully!!", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    indusLog("interests not given"+selectedInterestList.size());
                    Toast.makeText(getActivity(), "Please fill the Interest!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    //to read data from database and set it to recyclerView Adapter
    private void prepareSportsData() {
        myDatabase.child("sports").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sportsList = new ArrayList<>();
                sportsListNames=new ArrayList<>();
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
                    sportsListNames.add(sport.getSportName());


                }
               if(adapterList!=null)
                {
                    //        indusToast(getActivity(),"new news added");
                }
                adapterList=new  ArrayAdapter(getContext(),R.layout.interest_checkbox_tile,R.id.interestCheckedTextView,sportsListNames);



                listView.setAdapter(adapterList);

                //to show already checked data on list view and adding it to selected list.
                if(sportsAlreadyInDb != null && sportsAlreadyInDb.size()!=0) {
                    for (int i : sportsAlreadyInDb) {
                        // indusLog(" sport : "+sportsAlreadyInDb.get(i).toString());
                        listView.setItemChecked(i,true);
                        selectedInterestList.add(i);
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("error", "Failed to read value.", databaseError.toException());

            }
        });

    }


    void prepareInterestList(final MyCallbackinvokeSportData mycallback)
    {
        myDatabase.child("interest").orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {

            int positionOfSport=-1;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sportsAlreadyInDb=new ArrayList<>();
                //indusLog("in prepareInterestList : "+positionOfSport);

                for (DataSnapshot   snapshot: dataSnapshot.getChildren()) {
                    try {
                        positionOfSport = snapshot.getValue(Interest.class).getPositionOfSport();
                       indusLog("in prepareInterestList : "+positionOfSport);
                    } catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db in prepare Interest");
                        e.printStackTrace();
                    }

                    //check if something is present in db or not.
                    if(positionOfSport != -1) {
                        sportsAlreadyInDb.add(positionOfSport);
                    }
                }
                mycallback.onCallback();

                // indusLog("sport ls "+sportsAlreadyInDb.get(0));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectedInterestList.clear();


    }

    public interface MyCallbackinvokeSportData {
        void onCallback();
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

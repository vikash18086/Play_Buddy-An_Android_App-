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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AdminNewsFrag extends Fragment {

    //xml elemets
  private   Button save,cancel,remove;
    private EditText title,description;
    private Database db;
    private Spinner sportsSpinner;
    private DatabaseReference myDatabase;
    private List<Sport> newssportsList = null;
    private List<String> newssportNameList=null;
    private ArrayAdapter<String> newsadapter;
    private int spinnerPosition;
    private int selectedSportIndex = -1;
    private boolean classActive = false;
    private OnFragmentInteractionListener mListener;

    public AdminNewsFrag() {
        // Required empty public constructor
    }

    public static final String LOGTAG = "indus";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new Database();
        myDatabase= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview=inflater.inflate(R.layout.fragment_admin_news, container, false);

        classActive = true;
        //find elements on view.
        title=rootview.findViewById(R.id.title);
        description=rootview.findViewById(R.id.description);
        save=rootview.findViewById(R.id.save);
        cancel=rootview.findViewById(R.id.cancle);
        remove=rootview.findViewById(R.id.remove);
        sportsSpinner=rootview.findViewById(R.id.sportlist);

        prepareNewsData();

        if(NewsAdminRecyclerViewFrag.selectedNews !=null)
        {
          //  remove.setVisibility(View.VISIBLE);
            //save.setVisibility(View.INVISIBLE);
           /* int index = 0;
            for(int i = 0; i < newssportsList.size(); i++)
            {
                if(NewsAdminRecyclerViewFrag.selectedNews.getSportId().equalsIgnoreCase(newssportsList.get(i).getSportId()))
                {
                    index = i;
                    break;
                }
            }
            sportsSpinner.setSelection(index);*/
            remove.setVisibility(View.VISIBLE);
            title.setText(NewsAdminRecyclerViewFrag.selectedNews.getNewsTitle());
            description.setText(NewsAdminRecyclerViewFrag.selectedNews.getNewsDescription());
           // Toast.makeText(getActivity(), ""+NewsAdminRecyclerViewFrag.selectedNews.getNews_id(), Toast.LENGTH_SHORT).show();
            //fill here
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(NewsAdminRecyclerViewFrag.selectedNews!=null)
                {
                    spinnerPosition = sportsSpinner.getSelectedItemPosition();
                    String updatedDesc=description.getText().toString();
                    String updatedTitle=title.getText().toString();
                    String updatedSportsId=newssportsList.get(spinnerPosition).getSportId();
                    if (updatedDesc.length() == 0 || updatedTitle.length() == 0) {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    } else {
                        db.updateNews(NewsAdminRecyclerViewFrag.selectedNews.getNewsId(), updatedDesc, updatedTitle, updatedSportsId);
                        Toast.makeText(getActivity(), "News Updated Successfully!!" + NewsAdminRecyclerViewFrag.selectedNews.getNewsTitle(), Toast.LENGTH_SHORT).show();

                        callNewsAdminRecyclerViewFrag();
                    }
                }
                else {
                    spinnerPosition = sportsSpinner.getSelectedItemPosition();
                    String heading = title.getText().toString();
                    String desc = description.getText().toString();
                    String updatedSportsId=newssportsList.get(spinnerPosition).getSportId();
                    if (heading.length() == 0 || desc.length() == 0) {
                        Toast.makeText(getActivity(), "Please fill the fields!!", Toast.LENGTH_SHORT).show();
                    } else {
                        News news = new News(heading, desc, new Date(),updatedSportsId);
                        db.write(news, "news");
                        NewsAdminRecyclerViewFrag.selectedNews=null;
                        callNewsAdminRecyclerViewFrag();
                    }
                }

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.remove(NewsAdminRecyclerViewFrag.selectedNews.getNewsId());
                Toast.makeText(getActivity(), "News Removed Successfully!!", Toast.LENGTH_SHORT).show();
                NewsAdminRecyclerViewFrag.selectedNews=null;
                callNewsAdminRecyclerViewFrag();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(null);
                description.setText(null);
                NewsAdminRecyclerViewFrag.selectedNews=null;
                callNewsAdminRecyclerViewFrag();

            }
        });

        return rootview;
    }
    private void prepareNewsData() {

        final int index = 0;
        myDatabase.child("sports").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                newssportsList = new ArrayList<>();
                newssportNameList=new ArrayList<>();
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

                    newssportNameList.add(sport.getSportName());
                    newssportsList.add(sport);

                    if(NewsAdminRecyclerViewFrag.selectedNews !=null && sport.getSportId().equalsIgnoreCase(NewsAdminRecyclerViewFrag.selectedNews.getSportId()))
                    {
                        indusLog("index hit in adapter");

                        selectedSportIndex = count;
                    }
                    count++;
                }

                if(newsadapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                //spinner adapter
                if(classActive) {
                    newsadapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            newssportNameList);
                    newsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //set the adapter on the spinner
                    sportsSpinner.setAdapter(newsadapter);
                    if(selectedSportIndex != -1)
                    {
                        indusLog("index hit in adapter");
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
        Log.i(LOGTAG,message);
    }


    void callNewsAdminRecyclerViewFrag()
    {
        NewsAdminRecyclerViewFrag fr=new NewsAdminRecyclerViewFrag();

        NewsAdminRecyclerViewFrag.selectedNews = null;
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
    }
}

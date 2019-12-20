package com.example.ashish.playbuddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class fragment_news_user_recyclerview extends Fragment {
    private DatabaseReference myDatabase,interestDatabaseReference;
    public List<News> newsList = null;
    public List<Interest> interestList = null;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    public static News selectedNews;
    //public static String title,description;
    // Database db;

    public static final String LOGTAG = "indus";
    //////
    private fragment_news_user_recyclerview.OnFragmentInteractionListener mListener;

    public fragment_news_user_recyclerview() {
        // Required empty public constructor
    }

    Comparator<News> cmp = new Comparator<News>() {
        public int compare(News o1, News o2) {
            return o2.getNewsDate().compareTo(o1.getNewsDate());
        }
    };

    public static fragment_news_user_recyclerview newInstance(String param1, String param2) {
        fragment_news_user_recyclerview fragment = new fragment_news_user_recyclerview();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","passed layout created");
        myDatabase = FirebaseDatabase.getInstance().getReference();
        interestDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("TAG","passed layout 0");
        final View mview = inflater.inflate(R.layout.fragment_fragment_news_user_recyclerview, container, false);

        //bring data from database.

        //prepareInterestData();

        prepareInterestData(new MyCallbackPrepareNews() {
            @Override
            public void onCallback(List <Interest> values) {
                interestList = values;
                prepareNewsData();
            }
        });



        recyclerView = mview.findViewById(R.id.recycler_view_user_news);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.i("TAG","passed layout 1");
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        recyclerView.addOnItemTouchListener(new myRecyclerViewListner(getActivity(), recyclerView, new myRecyclerViewListner.ClickListener() {


            public void onClick(View view, int position) {
                //selected news from recyclerView
                selectedNews = newsList.get(position);
                /*AdminNewsFrag fr = new AdminNewsFrag();
                FragmentManager fm = getFragmentManager();*/
                //fm.beginTransaction().replace(R.id.frame_container,fr).commit();

                /////
                AlertDialog.Builder showNews = new AlertDialog.Builder(getActivity());
                showNews.setMessage("\n"
                        +"Date: "+selectedNews.getNewsDate().getDate()
                        +"-"+ selectedNews.getNewsDate().getMonth()
                        +"-"+ selectedNews.getNewsDate().getYear()
                        +"\nTime: "+ selectedNews.getNewsDate().getHours()
                        +":"+ selectedNews.getNewsDate().getMinutes()
                        + "\n\n" +selectedNews.getNewsDescription());
                showNews.setTitle(selectedNews.getNewsTitle());

                showNews.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog displayAlert = showNews.create();
                displayAlert.show();


            }
            public void onLongClick(View view, int position) {

            }
        }));


        return mview;
    }


    private void prepareInterestData(final MyCallbackPrepareNews mycallback) {


            interestDatabaseReference.child("interest").orderByChild("email").equalTo(NavigationDrawer.accountEmail).addListenerForSingleValueEvent(new ValueEventListener() {

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
                    mycallback.onCallback(interestList);

                    /*System.out.print("***********"+"\n\n\n\n");
                    //for (int i=0;i<interestList.size();i++) {
                        System.out.println(interestList.get(0));
                    //s}*/

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("error", "Failed to read value.", databaseError.toException());

                }
            });

    }


    //to read data from database and set it to recyclerView Adapter
    private void prepareNewsData() {
        final ProgressDialog pb = new ProgressDialog(getActivity());
        myDatabase.child("news").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                newsList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    News news = new News();
                    try {
                        news.setNewsId(ds.getValue(News.class).getNewsId());
                        news.setNewsTitle(ds.getValue(News.class).getNewsTitle());
                        news.setNewsDescription(ds.getValue(News.class).getNewsDescription());
                        news.setNewsDate(ds.getValue(News.class).getNewsDate());
                        news.setSportId(ds.getValue(News.class).getSportId());
                    }
                    catch (Exception e)
                    {
                        indusLog("Exception in fetching from Db");
                        e.printStackTrace();
                    }



                    boolean match = false;

                        for (int j = 0; j < interestList.size(); j++) {
                            if (interestList.get(j).getSportId().equalsIgnoreCase(news.getSportId())) {
                                match = true;
                                break;
                            }
                        }
                        if (match) {
                            newsList.add(news);

                        }

                }
                newsList.sort(cmp);
                if(mAdapter!=null)
                {
                    //          indusToast(getActivity(),"new news added");
                }
                mAdapter = new MyAdapter(newsList);

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
        if (context instanceof fragment_news_user_recyclerview.OnFragmentInteractionListener) {
            mListener = (fragment_news_user_recyclerview.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public interface MyCallbackPrepareNews {
        void onCallback(List<Interest> values);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

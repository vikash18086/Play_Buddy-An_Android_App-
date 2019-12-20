package com.example.ashish.playbuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
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

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyTimeLineAdapter extends RecyclerView.Adapter<MyTimeLineAdapter.MyViewHolder>  {

    private List<TimeLine> timeLineList;
    private ClickListener listener;
    PlayAreaDatabase playAreaDB;
    private DatabaseReference myDatabase;
    public int tilecount;



  /*  void iconTextViewOnClick(View v, int position);
    void iconImageViewOnClick(View v, int position);*/

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView starttime;
        public TextView endtime;
        public TextView count;
        public Button plus;
        public Button minus;
        Date currtime;
     //   public int tilecount = 0;
        private WeakReference<ClickListener> listenerRef;



        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            listenerRef = new WeakReference<>(listener);
            count =view.findViewById(R.id.timelineslotcount);
            plus = view.findViewById(R.id.timelineplus);
            minus = view.findViewById(R.id.timelineminus);
            starttime = view.findViewById(R.id.timelineslotstarttime);
            endtime = view.findViewById(R.id.timelineslotendtime);
            currtime = new Date();
            view.setOnClickListener(this);
            plus.setOnClickListener(this);
            minus.setOnClickListener(this);
            playAreaDB = new PlayAreaDatabase();
            myDatabase = FirebaseDatabase.getInstance().getReference();


        }




        public void onClick(View v) {

            if (v.getId() == minus.getId()) {

         //       Toast.makeText(v.getContext(), "minus pressed on " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                PlayArea pa = new PlayArea(NavigationDrawer.accountEmail,PlayArea.selectedSportId,PlayArea.selectedVenueId,timeLineList.get(getAdapterPosition()).getSlotId());
                playAreaDB.remove(pa);
                plus.setEnabled(true);
                minus.setEnabled(false);

            } else if(v.getId() == plus.getId()) {
                plus.setEnabled(false);
                minus.setEnabled(true);
                PlayArea pa = new PlayArea(NavigationDrawer.accountEmail,PlayArea.selectedSportId,PlayArea.selectedVenueId,timeLineList.get(getAdapterPosition()).getSlotId());
                playAreaDB.write(pa,"playarea");
      //          Toast.makeText(v.getContext(), "plus PRESSED on " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public MyTimeLineAdapter(List<TimeLine> time) {
        this.timeLineList = time;
    }
    public MyTimeLineAdapter(List<TimeLine> time, ClickListener listener) {
        this.timeLineList = time;
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timelinetile, parent, false);

        return new MyViewHolder(itemView,listener);
    }
    public static String javaDateToString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String s = null;
        s = dateFormat.format(date);
        return s;

    }

    public static Date stringToJavaDate(String dateString)
    {
        java.util.Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return date;

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TimeLine timeLine = timeLineList.get(position);
        holder.starttime.setText("Start Time: " + timeLine.getStartTime());
        holder.endtime.setText("End Time: " + timeLine.getEndTime());

        getPlayAreacount(timeLine.getSlotId(),holder, new MycallbackgetCount() {
            @Override
            public void onCallback(MyViewHolder holder, int value) {
                holder.count.setText("Players Count: " + value);
            }
        });

        holder.minus.setEnabled(false);
        holder.plus.setEnabled(false);


        Date endtime = stringToJavaDate(timeLine.getEndTime());
        holder.currtime = stringToJavaDate(javaDateToString(holder.currtime));

        if(endtime.compareTo(holder.currtime) >= 0 || timeLine.getEndTime().equals("0:00"))
        {
         //   holder.minus.setEnabled(true);
            holder.plus.setEnabled(true);
            PlayArea pa = new PlayArea(PlayArea.selectedSportId,PlayArea.selectedVenueId,NavigationDrawer.accountEmail,timeLine.getSlotId());
            checkPlayerExistinPlayArea(timeLine.getSlotId(),holder, new MycallbackcheckPlayerExistinPlayArea() {
                @Override
                public void onCallback(MyViewHolder holder, boolean value) {
                    holder.minus.setEnabled(true);
                    holder.plus.setEnabled(false);
                    PlayArea.playerExist = false;
                }
            });

        }

    }

    private void getPlayAreacount( final String slotId,final MyViewHolder holder,final MycallbackgetCount mycallback) {

        if (slotId != null) {
            myDatabase.child("playarea").orderByChild("slotId").equalTo(slotId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        //            int count = 0;
                    tilecount = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        PlayArea pa = new PlayArea();

                        try {
                            pa.setSportId(ds.getValue(PlayArea.class).getSportId());
                            pa.setEmail(ds.getValue(PlayArea.class).getEmail());
                            pa.setVenueId(ds.getValue(PlayArea.class).getVenueId());
                            pa.setSlotId(ds.getValue(PlayArea.class).getSlotId());
                            pa.setPlayAreaId(ds.getValue(PlayArea.class).getPlayAreaId());

                        } catch (Exception e) {
                            Log.e("INDUS", "onDataChange: Exception in fetching from Db");
                            e.printStackTrace();
                        }
                        if (UserTImeLineRVFrag.classActive && pa.comparePlaySportVenueSlot(PlayArea.selectedSportId,PlayArea.selectedVenueId,slotId)) {
                            tilecount++;
                        }

                    }
                    if(UserTImeLineRVFrag.classActive)
                        mycallback.onCallback(holder,tilecount);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("error", "Failed to read value.", databaseError.toException());

                }
            });
        }
    }

    public void checkPlayerExistinPlayArea(final String slotId,final MyViewHolder holder,final MycallbackcheckPlayerExistinPlayArea mycallback) {

        if (slotId != null) {
            Log.i("TAG","PlayerExist inside");

            PlayArea.playerExist = false;
            myDatabase.child("playarea").orderByChild("email").equalTo(NavigationDrawer.accountEmail).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("TAG","PlayerExist inside level 2");

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.i("TAG","PlayerExist inside level 3");
                        PlayArea pa = new PlayArea();

                        try {

                            pa.setSportId(ds.getValue(PlayArea.class).getSportId());
                            pa.setEmail(ds.getValue(PlayArea.class).getEmail());
                            pa.setVenueId(ds.getValue(PlayArea.class).getVenueId());
                            pa.setSlotId(ds.getValue(PlayArea.class).getSlotId());
                            pa.setPlayAreaId(ds.getValue(PlayArea.class).getPlayAreaId());

                        } catch (Exception e) {
                            Log.e("INDUS", "onDataChange: Exception in fetching from Db");
                            e.printStackTrace();
                        }
                        if (UserTImeLineRVFrag.classActive && pa.comparePlayarea(NavigationDrawer.accountEmail
                        ,PlayArea.selectedSportId
                                ,PlayArea.selectedVenueId,
                                slotId
                        )) {
                            PlayArea.playerExist = true;
                            Log.i("TAG","PlayerExist "+ pa.playAreaId);
                            mycallback.onCallback(holder,true);
                            break;
                        }
                        else
                        {
                    //        Log.i("TAG","Else "+ pa.playAreaId);
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
    public interface MycallbackcheckPlayerExistinPlayArea{
        void onCallback(MyViewHolder holder,boolean value);
    }
    public interface MycallbackgetCount{
        void onCallback(MyViewHolder holder,int value);
    }

    @Override
    public int getItemCount() {
        return timeLineList.size();
    }


    public interface ClickListener {

        void onPositionClicked(int position);

      //  void onLongClicked(int position);
    }
}

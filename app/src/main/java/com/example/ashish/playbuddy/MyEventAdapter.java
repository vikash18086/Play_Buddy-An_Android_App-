package com.example.ashish.playbuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.MyViewHolder> {

    private List<Event> eventList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        public TextView starttime,endtime;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.eventTileTitle);
            date = view.findViewById(R.id.eventTileDate);
            starttime = view.findViewById(R.id.eventTilestarttime);
            endtime = view.findViewById(R.id.eventTileendtime);
            //data to be displayed in tile
            //    add if any
        }
    }


    public MyEventAdapter(List<Event> event) {
        this.eventList = event;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventtile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText("Title: " + event.getEventTitle());
        holder.date.setText("Date: "+ event.getEventDate().getDate() + "-"
                            +event.getEventDate().getMonth() + "-"
                            +event.getEventDate().getYear()// + "\n"
               //             +"Start Time: "+ event.getEventStartTime() + "\n"
        //        +"End Time: "+ event.getEventEndTime()
        );
        holder.starttime.setText("Start Time: " +event.getEventStartTime());
        holder.endtime.setText("End Time: " +event.getEventEndTime());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

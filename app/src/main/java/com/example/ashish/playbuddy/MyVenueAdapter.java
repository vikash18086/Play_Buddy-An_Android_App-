package com.example.ashish.playbuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyVenueAdapter extends RecyclerView.Adapter<MyVenueAdapter.MyViewHolder> {

    private List<Venue> venuelist;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.venueTileTitle);
        //data to be displayed in tile
        //    add if any
        }
    }

    public MyVenueAdapter(List<Venue> venue) {
        this.venuelist = venue;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venuetile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Venue venue = venuelist.get(position);
        holder.title.setText(venue.getVenueName());

        //add data to holder
       // holder.genre.setText(movie.getGenre());
       // holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return venuelist.size();
    }
}

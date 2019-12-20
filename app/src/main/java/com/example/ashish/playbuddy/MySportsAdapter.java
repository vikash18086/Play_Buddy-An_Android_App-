package com.example.ashish.playbuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MySportsAdapter extends RecyclerView.Adapter<MySportsAdapter.MyViewHolder> {

    private List<Sport> sportsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.sportTileTitle);
            //data to be displayed in tile
            //    add if any
        }
    }


    public MySportsAdapter(List<Sport> sports) {
        this.sportsList = sports;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sporttile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sport sports = sportsList.get(position);
        holder.title.setText(sports.getSportName());

        //add data to holder
        // holder.genre.setText(movie.getGenre());
        // holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return sportsList.size();
    }
}

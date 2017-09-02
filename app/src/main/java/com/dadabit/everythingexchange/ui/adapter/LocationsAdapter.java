package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder>{


    private List<String> locations;
    private int homeLocationPosition;
    private Context context;
    private ClickListener mClickListener;

    public LocationsAdapter(Context context,
                            ClickListener mClickListener) {
        this.context = context;
        this.mClickListener = mClickListener;
    }

    public void setLocations(List<String> locations, int homeLocationPosition) {
        this.locations = locations;
        this.homeLocationPosition = homeLocationPosition;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocationsAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_location, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvLocation.setText(locations.get(position));

        if (position == homeLocationPosition){

            holder.ivHome.setColorFilter(
                    ContextCompat.getColor(context, R.color.black),
                    PorterDuff.Mode.SRC_ATOP);
        } else {

            holder.ivHome.setColorFilter(
                    ContextCompat.getColor(context, R.color.grey),
                    PorterDuff.Mode.SRC_ATOP);
        }



    }

    @Override
    public int getItemCount() {
        return locations == null ? 0 : locations.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_location_tvName) TextView tvLocation;
        @BindView(R.id.card_location_ivIcon) ImageView ivHome;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            ivHome.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.card_location_ivIcon:

                    if (homeLocationPosition != getLayoutPosition()){

                        homeLocationPosition = getLayoutPosition();

                        notifyDataSetChanged();

                        mClickListener.onHomeLocationChanged(
                                locations.get(getLayoutPosition()));

                    }

                    break;

                default:

                    mClickListener.onLocationClick(locations.get(getLayoutPosition()));

                    break;
            }


        }
    }

    public interface ClickListener {

        void onLocationClick(String location);

        void onHomeLocationChanged(String location);
    }
}

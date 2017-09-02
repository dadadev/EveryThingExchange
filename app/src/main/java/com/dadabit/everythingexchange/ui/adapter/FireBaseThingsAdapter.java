package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FireBaseThingsAdapter extends RecyclerView.Adapter<FireBaseThingsAdapter.ViewHolder>{

    private Context context;
    private List<FireBaseThingItem> things;
    private СlickCallback mClickCallback;
    private boolean isEmpty;



    public FireBaseThingsAdapter(Context context,
                                 @Nullable List<FireBaseThingItem> things,
                                 @Nullable СlickCallback mClickCallback) {
        Log.d("@@@", "FireBaseThingsAdapter.create");
        this.context = context;
        this.things = things;
        this.mClickCallback = mClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_thing, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (isEmpty){

            holder.mImageView.setVisibility(View.GONE);
            holder.tvDescription.setVisibility(View.INVISIBLE);

            holder.tvName.setText("Sorry There is no things yet");
        } else {

            Glide.with(context)
                    .load(things.get(position).getItemImgLink())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImageView);

            holder.tvName.setText(things.get(position).getItemName());
            holder.tvDescription.setText(things.get(position).getItemDescription());


        }

    }

    @Override
    public int getItemCount() {
        if (things == null || things.size() == 0){
            isEmpty = true;
            return 1;
        } else {
            isEmpty = false;
            return things.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_thing)
        CardView mCardView;
        @BindView(R.id.card_thing_imageView)
        public ImageView mImageView;
        @BindView(R.id.card_thing_tvName)
        TextView tvName;
        @BindView(R.id.card_thing_tvDescription) TextView tvDescription;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCardView.setOnClickListener(this);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (!isEmpty){

                mClickCallback.onThingClick(getLayoutPosition());

            }

        }
    }

    public interface СlickCallback {

        void onThingClick(int position);
    }
}

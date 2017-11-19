package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonThingsAdapter extends RecyclerView.Adapter<PersonThingsAdapter.ViewHolder> {


    private List<FireBaseThingItem> things;
    private Context mContext;
    private ClickCallback mCallback;

    public PersonThingsAdapter(Context mContext, ClickCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void addNewThing(FireBaseThingItem newThing){

        if (things == null){
            things = new ArrayList<>();
        }

        things.add(newThing);

        notifyItemInserted(getItemCount()-1);

    }



    public void setThings(final List<FireBaseThingItem> newThings){
        Log.d("@@@", "PersonThingsAdapter.setItems");
        if (things == null){
            things = newThings;
            notifyItemRangeInserted(0, newThings.size());
        } else {
            Log.d("@@@", "PersonThingsAdapter.setItems.change");

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return things.size();
                }

                @Override
                public int getNewListSize() {
                    return newThings.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return things.get(oldItemPosition).getFireBaseID()
                            .equals(newThings.get(newItemPosition).getFireBaseID());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return things.get(oldItemPosition).getDate() ==
                            newThings.get(newItemPosition).getDate();
                }
            });

            things = newThings;

            result.dispatchUpdatesTo(this);

            notifyDataSetChanged();


        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PersonThingsAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_thing, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mCardView.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.slide_in_left));

        Glide.with(mContext)
                .load(things.get(position).getItemImgLink())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);

        holder.tvName.setText(things.get(position).getItemName());
        holder.tvDescription.setText(things.get(position).getItemDescription());

    }

    @Override
    public int getItemCount() {
        return things == null ? 0 : things.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_thing) CardView mCardView;
        public @BindView(R.id.card_thing_imageView) ImageView mImageView;
        @BindView(R.id.card_thing_tvName) TextView tvName;
        @BindView(R.id.card_thing_tvDescription) TextView tvDescription;
        int position;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onClick(getLayoutPosition());
        }
    }


    public interface ClickCallback {
        void onClick(int position);
    }

}

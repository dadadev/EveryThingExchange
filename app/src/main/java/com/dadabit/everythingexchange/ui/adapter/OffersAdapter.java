package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.OfferItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder>{

    private List<OfferItem> offers;
    private Context context;
    private ClickListener clickListener;

    private boolean setInvisible = false;

    public OffersAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }


    public void addOffer(OfferItem offer, int position, boolean setInvisible){
        if (offers == null){
            offers = new ArrayList<>();
        }
        offers.add(position, offer);
        this.setInvisible = setInvisible;
        notifyItemInserted(position);
    }

    public void hideOffer(int position){
        notifyItemRemoved(position);
        offers.remove(position);
    }

    public void setOffers(final List<OfferItem> newOffers){

        if (offers == null){
            offers = newOffers;
            notifyItemRangeInserted(0, newOffers.size());
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return offers.size();
                }

                @Override
                public int getNewListSize() {
                    return newOffers.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return offers.get(oldItemPosition).getOfferThingId()
                            .equals(newOffers.get(newItemPosition).getOfferThingId());

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return offers.get(oldItemPosition).getDate() ==
                            newOffers.get(newItemPosition).getDate();
                }
            });

            offers = newOffers;
            result.dispatchUpdatesTo(this);

        }



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OffersAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_thing_with_name, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (setInvisible){
            holder.mCardView.setVisibility(View.GONE);
            setInvisible = false;
        }

        Glide.with(context)
                .load(offers.get(position).getItemImgLink())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.mImageView);

        holder.mTextView.setText(offers.get(position).getItemName());


    }

    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public @BindView(R.id.card_thingWithName) CardView mCardView;
        @BindView(R.id.card_thingWithName_imageView)
        ImageView mImageView;
        @BindView(R.id.card_thingWithName_textView)
        TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            assert clickListener != null;
            clickListener.onClick(getLayoutPosition());
        }
    }


    public interface ClickListener{

        void onClick(int position);

    }
}

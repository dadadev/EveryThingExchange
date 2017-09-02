package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.MyThingsAdapterItem;
import com.dadabit.everythingexchange.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyThingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<MyThingsAdapterItem> items;
    private Context context;

    @Nullable
    private final ClickListener mCallback;



    public MyThingsAdapter(List<MyThingsAdapterItem> items,
                           Context context,
                           @Nullable ClickListener mCallback) {
        Log.d("@@@", "MyThingsAdapter.create");
        this.items = items;
        this.context = context;
        this.mCallback = mCallback;
    }

    @Override
    public int getItemViewType(int position) {

        return items.get(position).getType();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

            return new ExchangeViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.view_card_my_thing_exchange, parent, false));

        } else {

            return new ThingViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.view_card_my_thing, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {

        switch (holder.getItemViewType()){
            case Constants.THING_STATUS_EXCHANGING_IN_PROCESS:

                if (items.get(position).getExchangeImages() != null){

                    ExchangeViewHolder exchangeViewHolder = (ExchangeViewHolder) holder;

                    Glide.with(context)
                            .load(items.get(position).getExchangeImages()[0])
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(exchangeViewHolder.ivThing1);


                    Glide.with(context)
                            .load(items.get(position).getExchangeImages()[1])
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(exchangeViewHolder.ivThing2);
                }

                break;
            default:

                ThingViewHolder thingViewHolder = (ThingViewHolder) holder;

                thingViewHolder.mImageView.setImageBitmap(items.get(position).getImgBitmap());
                thingViewHolder.tvName.setText(items.get(position).getName());

                if (items.get(position).getOffersCounter()>0){
                    thingViewHolder.tvOffers.setText(String.valueOf(items.get(position).getOffersCounter()));
                    thingViewHolder.tvOffers.setVisibility(View.VISIBLE);
                } else {
                    thingViewHolder.tvOffers.setVisibility(View.GONE);
                }


                break;
        }


    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void setItems(final List<MyThingsAdapterItem> newItems){

        if (items == null){
            items = newItems;
            notifyItemRangeInserted(0, items.size());
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return items.size();
                }

                @Override
                public int getNewListSize() {
                    return newItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return items.get(oldItemPosition).getThingId()
                            .equals(newItems.get(newItemPosition).getThingId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return items.get(oldItemPosition).getOffersCounter() ==
                            newItems.get(newItemPosition).getOffersCounter();
                }
            });

            items = newItems;
            result.dispatchUpdatesTo(this);

        }
    }

    public class ThingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.card_myThing_imageView) ImageView mImageView;
        @BindView(R.id.card_myThing_tvName) TextView tvName;
        @BindView(R.id.card_myThing_tvOffers) TextView tvOffers;
        @BindView(R.id.card_myThing) CardView mCardView;


        ThingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            assert mCallback != null;
            mCallback.onClick(
                    getLayoutPosition(),
                    items.get(getLayoutPosition()).getType(),
                    items.get(getLayoutPosition()).getThingId());
        }
    }

    public class ExchangeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.card_myExchangeItem_iv_thing1) ImageView ivThing1;
        public @BindView(R.id.card_myExchangeItem_iv_thing2) ImageView ivThing2;

        public ExchangeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            assert mCallback != null;
            mCallback.onClick(
                    getLayoutPosition(),
                    items.get(getLayoutPosition()).getType(),
                    items.get(getLayoutPosition()).getThingId());        }
    }

    public interface ClickListener {
        void onClick(int position, int type, String thingId);
    }


}

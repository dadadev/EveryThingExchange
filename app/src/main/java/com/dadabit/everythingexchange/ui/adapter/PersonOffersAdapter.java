package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonOffersAdapter extends RecyclerView.Adapter<PersonOffersAdapter.ViewHolder>{

    private List<OfferItem> offers;
    private Context context;
    @Nullable
    private OffersByPersonAdapter.ClickCallback mClickCallback;

    public PersonOffersAdapter(List<OfferItem> offers,
                               Context context,
                               OffersByPersonAdapter.ClickCallback mClickCallback) {
        this.offers = offers;
        this.context = context;
        this.mClickCallback = mClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_thing_with_name, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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
        return offers.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_thingWithName_imageView) ImageView mImageView;
        @BindView(R.id.card_thingWithName_textView) TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            assert mClickCallback != null;
            ImageView imageView = (ImageView) v;
            mClickCallback.onOfferClick(offers.get(getLayoutPosition()), imageView);
        }
    }
}

package com.dadabit.everythingexchange.ui.adapter;


import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyThingsIconsAdapter extends RecyclerView.Adapter<MyThingsIconsAdapter.ViewHolder>{


    @Nullable
    private final ClickListener mClickCallback;

    private List<ThingEntity> things;

    private Drawable addNewIcon;

    public MyThingsIconsAdapter(@Nullable List<ThingEntity> things, Drawable addNewIcon, @Nullable ClickListener mClickCallback) {
        this.mClickCallback = mClickCallback;
        this.addNewIcon = addNewIcon;
        this.things = things;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyThingsIconsAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_single_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == things.size()){

            holder.mImageView.setImageDrawable(addNewIcon);

        } else {

            holder.mImageView.setImageBitmap(things.get(position).getImgBitmap());

        }


    }

    @Override
    public int getItemCount() {
        return things.size() +1;
    }


    public void setThings(final List<ThingEntity> availableThings) {

        if (things == null){

            things  = availableThings;
            notifyItemRangeInserted(0, availableThings.size());

        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return things.size();
                }

                @Override
                public int getNewListSize() {
                    return availableThings.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return things.get(oldItemPosition).getId() ==
                            availableThings.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return things.get(oldItemPosition).getStatus() ==
                            availableThings.get(newItemPosition).getStatus();
                }
            });

            things = availableThings;
            result.dispatchUpdatesTo(this);
        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_single_icon_imageView)
        ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            assert mClickCallback != null;
            mClickCallback.onClick(getLayoutPosition());
        }
    }

    public interface ClickListener {
        void onClick(int position);
    }
}

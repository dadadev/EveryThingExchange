package com.dadabit.everythingexchange.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HashTagsAdapter extends RecyclerView.Adapter<HashTagsAdapter.ViewHolder>{

    private List<String> hashTags;
    private OnClickListener mClickListener;

    public HashTagsAdapter(List<String> hashTags, OnClickListener mClickListener) {
        this.hashTags = hashTags;
        this.mClickListener = mClickListener;
    }

    public HashTagsAdapter(String newHashTags){
        if (newHashTags != null){

            String[] hashTagArray = newHashTags.split("#");
            hashTags = new ArrayList<>();

            for (String hashTag :
                    hashTagArray) {
                if (hashTag.length()>0){
                    hashTags.add("#"+hashTag);
                }
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HashTagsAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_hashtag, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.hashTag.setText(hashTags.get(position));

    }

    @Override
    public int getItemCount() {
        return hashTags == null ? 0 : hashTags.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.view_hashtag) TextView hashTag;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mClickListener != null) {
                mClickListener.onClick(getLayoutPosition());
            }
        }
    }

    public interface OnClickListener{

        void onClick(int position);
    }
}

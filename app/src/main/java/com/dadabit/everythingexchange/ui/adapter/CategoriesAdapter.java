package com.dadabit.everythingexchange.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.ThingCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{

    @NonNull
    private final CategoriesClickCallback mClickCallback;

    private List<ThingCategory> categories;
    private Context context;

    public CategoriesAdapter(
            Context context,
            @NonNull CategoriesClickCallback mClickCallback) {
        Log.d("@@@", "CategoriesAdapter.create");
        this.context = context;
        this.mClickCallback = mClickCallback;
    }


    public void setCategories(List<ThingCategory> categories) {
        this.categories = categories;

//        notifyItemRangeInserted(0, categories.size());

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoriesAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_category, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {

        holder.mImageView.setImageBitmap(categories.get(position).getImgBitmap());
        holder.mTextView.setText(categories.get(position).getName());

        if (position % 2 == 0){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            animation.setStartOffset(position*100);
            holder.mCardView.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            animation.setStartOffset(position*100);
            holder.mCardView.startAnimation(animation);
        }

    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_category_imageView)
        ImageView mImageView;
        @BindView(R.id.card_category_textView)
        TextView mTextView;
        @BindView(R.id.card_category)
        public CardView mCardView;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickCallback.onClick(getLayoutPosition());
        }
    }


    public interface CategoriesClickCallback {

        void onClick(int position);

    }

}
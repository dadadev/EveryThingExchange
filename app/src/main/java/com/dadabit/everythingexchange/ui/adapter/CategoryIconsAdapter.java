package com.dadabit.everythingexchange.ui.adapter;


import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.ThingCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryIconsAdapter extends RecyclerView.Adapter<CategoryIconsAdapter.ViewHolder>{

    @Nullable
    private final CategoryIconsClickCallback mClickCallback;

    private List<ThingCategory> categories;

    public CategoryIconsAdapter(@Nullable List<ThingCategory> categories, @Nullable CategoryIconsClickCallback mClickCallback) {
        Log.d("@@@", "CategoriesAdapter.create");
        this.mClickCallback = mClickCallback;
        this.categories = categories;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryIconsAdapter.ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_single_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryIconsAdapter.ViewHolder holder, int position) {

        holder.mImageView.setImageBitmap(categories.get(position).getImgBitmap());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_single_icon_imageView) ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            assert mClickCallback != null;
            mClickCallback.onCategoryClick(getLayoutPosition());
        }
    }

    public interface CategoryIconsClickCallback {

        void onCategoryClick(int position);

    }

}

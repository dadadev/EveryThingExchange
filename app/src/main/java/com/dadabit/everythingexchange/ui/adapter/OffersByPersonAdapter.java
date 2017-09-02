package com.dadabit.everythingexchange.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.model.vo.OffersByPersonAdapterItem;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OffersByPersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<OffersByPersonAdapterItem> offersByPerson;

    private ThingEntity myThing;

    @Nullable
    private ClickCallback clickCallback;

    private final int TYPE_THING_INFO = 1;
    private boolean showThingInfo;


    public OffersByPersonAdapter(
            Context context,
            ThingEntity myThing,
            @NonNull ClickCallback clickCallback) {
        Log.d("@@@", "OffersByPersonAdapter.create");
        this.context = context;
        this.myThing = myThing;
        this.clickCallback = clickCallback;

    }

    public void setOffers(final List<OffersByPersonAdapterItem> newOffers){

        if (offersByPerson == null){
            offersByPerson = newOffers;
            notifyItemRangeInserted(0, newOffers.size());
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return offersByPerson.size();
                }

                @Override
                public int getNewListSize() {
                    return newOffers.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return offersByPerson.get(oldItemPosition).getOffererUid()
                            .equals(newOffers.get(newItemPosition).getOffererUid());

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return offersByPerson.get(oldItemPosition).getOffers().size() ==
                            newOffers.get(newItemPosition).getOffers().size();
                }
            });

            offersByPerson = newOffers;
            result.dispatchUpdatesTo(this);

        }



    }

    public void showThingInfo(){
        Log.d("@@@", "OffersByPersonAdapter.showThingInfo");

        showThingInfo = true;

        notifyItemInserted(0);

//        notifyDataSetChanged();


    }

    public void hideThingInfo(){
        Log.d("@@@", "OffersByPersonAdapter.hideThingInfo");

        showThingInfo = false;

//        notifyDataSetChanged();

        notifyItemRemoved(0);
    }


    @Override
    public int getItemCount() {
        Log.d("@@@", "OffersByPersonAdapter.getItemCount");

        if (showThingInfo){

            return offersByPerson == null ? 1 : offersByPerson.size()+1;

        } else {

            return offersByPerson == null ? 0 : offersByPerson.size();

        }

    }

    @Override
    public int getItemViewType(int position) {
        Log.d("@@@", "OffersByPersonAdapter.getItemViewType");

        if (showThingInfo
                && position == 0){

            return TYPE_THING_INFO;

        }
        return super.getItemViewType(position);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("@@@", "OffersByPersonAdapter.onCreateViewHolder");

        if (viewType == TYPE_THING_INFO){
            return new DescriptionViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.view_card_my_thing_description, parent, false));
        } else {
            return new OffersViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.view_card_offer, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("@@@", "OffersByPersonAdapter.onBindViewHolder");

        if (showThingInfo){

            switch (holder.getItemViewType()){

                case TYPE_THING_INFO:

                    if (myThing!= null){

                        DescriptionViewHolder descriptionViewHolder = (DescriptionViewHolder) holder;

                        descriptionViewHolder.tvCategory
                                .setText(context
                                        .getResources()
                                        .getStringArray(R.array.categories)[myThing.getCategory()]);

                        descriptionViewHolder.tvDescription.setText(myThing.getDescription());

                        descriptionViewHolder.tvDate.setText(Utils.timestampToString(myThing.getDate().getTime()));

                        if (myThing.getHashTags() != null
                                && myThing.getHashTags().length()>0){
                            descriptionViewHolder.mRecyclerView.setLayoutManager(
                                    new LinearLayoutManager(
                                            context,
                                            LinearLayoutManager.HORIZONTAL,false));

                            descriptionViewHolder.mRecyclerView.setAdapter(
                                    new HashTagsAdapter(myThing.getHashTags()));

                        }

                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_top);
                        animation.setDuration(600);

                        descriptionViewHolder.cardThingInfo
                                .startAnimation(animation);

                    }


                    break;

                default:

                    OffersViewHolder offersViewHolder = (OffersViewHolder) holder;

                    if (offersByPerson.get(position-1) != null){

                        Glide.with(context)
                                .load(offersByPerson.get(position-1).getImg())
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(offersViewHolder.mImageView);

                        offersViewHolder.mTextView.setText(offersByPerson.get(position-1).getName());

                        offersViewHolder.mRecyclerView.setLayoutManager(
                                new LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.HORIZONTAL,false));

                        offersViewHolder.mRecyclerView.setAdapter(
                                new PersonOffersAdapter(
                                        offersByPerson.get(position-1).getOffers(),
                                        context,
                                        clickCallback));

                    }

                    break;

            }

        } else {

            OffersViewHolder offersViewHolder = (OffersViewHolder) holder;

            Glide.with(context)
                    .load(offersByPerson.get(position).getImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(offersViewHolder.mImageView);

            offersViewHolder.mTextView.setText(offersByPerson.get(position).getName());

            offersViewHolder.mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            context,
                            LinearLayoutManager.HORIZONTAL,false));

            offersViewHolder.mRecyclerView.setAdapter(
                    new PersonOffersAdapter(
                            offersByPerson.get(position).getOffers(),
                            context,
                            clickCallback));

        }
    }

    public boolean isThingInfoShown() {
        return showThingInfo;
    }


    public class OffersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.card_offer_imageView) ImageView mImageView;
        @BindView(R.id.card_offer_tvPersonName) TextView mTextView;
        @BindView(R.id.card_offer_recyclerView) RecyclerView mRecyclerView;

        OffersViewHolder(View itemView) {
            super(itemView);
            Log.d("@@@", "OffersByPersonAdapter.OffersViewHolder.create");
            ButterKnife.bind(this, itemView);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("@@@", "OffersByPersonAdapter.mImageView.onClick: "+getLayoutPosition());

            if (clickCallback != null) {
                clickCallback.onPersonClick(
                        offersByPerson.get(getLayoutPosition()).getOffererUid(),
                        getLayoutPosition());
            }

        }
    }

    class DescriptionViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        @BindView(R.id.card_myThing_description) CardView cardThingInfo;
        @BindView(R.id.card_myThing_description_layout) LinearLayout layout;
        @BindView(R.id.card_myThing_description_tvCategory) TextView tvCategory;
        @BindView(R.id.card_myThing_description_tvDate) TextView tvDate;
        @BindView(R.id.card_myThing_description_tvDescription) TextView tvDescription;
        @BindView(R.id.card_myThing_description_hashTagRecyclerView) RecyclerView mRecyclerView;
        @BindView(R.id.card_myThing_description_btnChange) Button btnChange;
        @BindView(R.id.card_myThing_description_btnDelete) Button btnDelete;
        boolean isShown;

        DescriptionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            btnChange.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.card_myThing_description_btnChange:

                    if (clickCallback != null) {
                        clickCallback.onThingChangeClick(myThing.getId());
                    }

                    break;

                default:

                    if (isShown){
                        isShown = false;
                        Animation animRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
                        Animation animLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
                        animRight.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnChange.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        animLeft.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnDelete.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        btnDelete.startAnimation(animLeft);
                        btnChange.startAnimation(animRight);

                    } else {
                        isShown = true;
                        btnChange.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
                        btnDelete.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left));
                        btnChange.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                    }

                    break;
            }



        }
    }

    public interface ClickCallback {

        void onOfferClick(OfferItem chosenOffer, ImageView imageView);

        void onThingChangeClick(int thingId);

        void onThingDeleteClick(int thingId);

        void onPersonClick(String uid, int position);

    }

}

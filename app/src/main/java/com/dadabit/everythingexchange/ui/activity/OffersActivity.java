package com.dadabit.everythingexchange.ui.activity;


import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.adapter.OffersAdapter;
import com.dadabit.everythingexchange.ui.presenter.offers.OffersActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.offers.OffersActivityView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OffersActivity extends AppCompatActivity
        implements OffersActivityView, AppBarLayout.OnOffsetChangedListener{


    @BindView(R.id.thingOffers_appBar) AppBarLayout mAppBarLayout;
    @BindView(R.id.thingOffers_toolbar) Toolbar mToolbar;
    @BindView(R.id.thingOffers_imageView) ImageView toolbarImageView;
    @BindView(R.id.thingOffers_collapsingToolbarLayout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.thingOffers_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.thingOffers_progressBar) ProgressBar mProgressBar;
    @BindView(R.id.thingOffers_fab) FloatingActionButton mFab;

    @BindView(R.id.thingOffers_card_myThing_description) CardView cardThingInfo;
    @BindView(R.id.thingOffers_card_myThing_ic_exchange) ImageView card_icExchange;
    @BindView(R.id.thingOffers_card_myThing_description_tvCategory) TextView card_tvCategory;
    @BindView(R.id.thingOffers_card_myThing_description_tvDate) TextView card_tvDate;
    @BindView(R.id.thingOffers_card_myThing_description_tvDescription) TextView card_tvDescription;
    @BindView(R.id.thingOffers_card_myThing_description_hashTagRecyclerView) RecyclerView card_rvHashTags;
    @BindView(R.id.thingOffers_card_myThing_description_btnChange) Button card_btnChange;
    @BindView(R.id.thingOffers_card_myThing_description_btnDelete) Button card_btnDelete;

    @BindView(R.id.bottom_sh_confirm_exchange_thing1_imageView) ImageView bs_ivThing1;
    @BindView(R.id.bottom_sh_confirm_exchange_thing2_imageView) ImageView bs_ivThing2;
    @BindView(R.id.bottom_sh_confirm_exchange_tvName) TextView bs_tvName;
    @BindView(R.id.bottom_sh_confirm_exchange_tvDescription) TextView bs_tvDescription;
    @BindView(R.id.bottom_sh_confirm_exchange_offerer_imageView) ImageView bs_ivOffererImage;
    @BindView(R.id.bottom_sh_confirm_exchange_tvOffererName) TextView bs_tvOffererName;
    @BindView(R.id.bottom_sh_confirm_exchange_btnAccept) Button bs_btnConfirm;


    public static OffersActivityPresenter mPresenter;

    private BottomSheetBehavior bottomSheetBehavior;

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private int mMaxScrollSize;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("@@@", "OffersActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        ButterKnife.bind(this);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.bottom_sh_confirm_exchange);

        NestedScrollView nestedScrollView = (NestedScrollView)  findViewById(R.id.bottom_sh_confirm_exchange);

        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

//        mMaxScrollSize = mAppBarLayout.getTotalScrollRange();
//
//        mAppBarLayout.addOnOffsetChangedListener(this);


        if (mPresenter == null){
            mPresenter = new OffersActivityPresenter();
        }
        mPresenter.exit = false;
        mPresenter.attachView(this);



    }


    @Override
    protected void onDestroy() {
        Log.d("@@@", "OffersActivity.onDestroy");
        super.onDestroy();
        if (mPresenter!= null && mPresenter.exit){
            mPresenter.detachView();
            if (isFinishing()) {
                Log.d("@@@", "OffersActivity.onDestroy.isFinishing()");
                mPresenter.onActivityFinished();
                mPresenter = null;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null){

            if (mPresenter.exit){
                super.onBackPressed();
            } else {
                mPresenter.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            toolbarImageView.animate()
                    .scaleX(0).scaleY(0)
                    .setDuration(600)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            toolbarImageView.animate()
                    .scaleX(1).scaleY(1)
                    .setDuration(600)
                    .start();

        }
    }




    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return mCollapsingToolbarLayout;
    }

    @Override
    public ImageView getToolbarImageView() {
        return toolbarImageView;
    }

    @Override
    public FloatingActionButton getFab() {
        return mFab;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public BottomSheetBehavior getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    @Override
    public ImageView getThing1ImageView() {
        return bs_ivThing1;
    }

    @Override
    public ImageView getThing2ImageView() {
        return bs_ivThing2;
    }

    @Override
    public TextView getOfferNameTextView() {
        return bs_tvName;
    }

    @Override
    public TextView getOfferDescriptionTextView() {
        return bs_tvDescription;
    }

    @Override
    public ImageView getOffererImageView() {
        return bs_ivOffererImage;
    }

    @Override
    public TextView getOffererNameTextView() {
        return bs_tvOffererName;
    }

    @Override
    public Button getConfirmExchangeButton() {
        return bs_btnConfirm;
    }

    @Override
    public CardView getThingInfoCardView() {
        return cardThingInfo;
    }

    @Override
    public TextView getCategoryTextView() {
        return card_tvCategory;
    }

    @Override
    public TextView getDateTextView() {
        return card_tvDate;
    }

    @Override
    public TextView getDescriptionTextView() {
        return card_tvDescription;
    }

    @Override
    public RecyclerView getHashTagsRecyclerView() {
        return card_rvHashTags;
    }

    @Override
    public Button getChangeButton() {
        return card_btnChange;
    }

    @Override
    public Button getDeleteButton() {
        return card_btnDelete;
    }

    @Override
    public ImageView getExchangeIcon() {
        return card_icExchange;
    }


    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }


    @Override
    public void animateFab() {
        mFab.setSelected(!mFab.isSelected());

        if (mFab.isSelected()){
            mFab.startAnimation(
                    AnimationUtils.loadAnimation(
                            getApplicationContext(),
                            R.anim.rotate_clockwise));


            cardThingInfo.setVisibility(View.GONE);


        } else {
            mFab.startAnimation(
                    AnimationUtils.loadAnimation(
                            getApplicationContext(),
                            R.anim.rotate_anticlockwise));


            cardThingInfo.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void animateOfferChosen(int position, Animation.AnimationListener animationListener) {

        OffersAdapter.ViewHolder viewHolder = (OffersAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder != null){

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
            animation.setAnimationListener(animationListener);
            viewHolder.mCardView.startAnimation(animation);

        }
    }

    @Override
    public void animateOfferBack(int position) {
        OffersAdapter.ViewHolder viewHolder = (OffersAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder != null){

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);

            viewHolder.mCardView.startAnimation(animation);
            viewHolder.mCardView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void startChangeThingActivity(int thingId) {

        Intent intent = new Intent(this, AddThingActivity.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(
                            this,
                            toolbarImageView,
                            getString(R.string.transitions_id_thing1Img));

            startActivity(intent, options.toBundle());

        } else {

            startActivity(intent);
        }

    }

    @Override
    public void startPersonInfoActivity(String uid) {

                Intent intent = new Intent(this, PersonInfoActivity.class)
                        .putExtra(
                                PersonInfoActivity.ARGUMENT_FIREBASE_USER_UID,
                                uid);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(
                                    this,
                                    bs_ivOffererImage,
                                    getString(R.string.transitions_id_userPic));

                    startActivity(intent, options.toBundle());

                } else {

                    startActivity(intent);
                }
    }

    @Override
    public void animateThingInfoClicked() {


        if (card_btnChange.getVisibility() == View.GONE){
            card_btnChange.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
            card_btnDelete.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
            card_btnChange.setVisibility(View.VISIBLE);
            card_btnDelete.setVisibility(View.VISIBLE);
        } else {
            Animation animRight = AnimationUtils
                    .loadAnimation(this, R.anim.slide_out_right);
            Animation animLeft = AnimationUtils
                    .loadAnimation(this, R.anim.slide_out_left);
            animRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    card_btnChange.setVisibility(View.GONE);

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
                    card_btnDelete.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_btnChange.startAnimation(animRight);
            card_btnDelete.startAnimation(animLeft);
        }

    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void startChatActivity() {

        startActivity(new Intent(this, SingleChatActivity.class));

        finish();

    }

}

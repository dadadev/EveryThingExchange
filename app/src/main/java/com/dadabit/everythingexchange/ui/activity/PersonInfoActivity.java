package com.dadabit.everythingexchange.ui.activity;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.personInfo.PersonInfoActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.personInfo.PersonInfoActivityView;
import com.dadabit.everythingexchange.ui.viewmodel.PersonInfoActivityViewModel;
import com.dadabit.everythingexchange.ui.viewmodel.ViewModelFactory;
import com.dadabit.everythingexchange.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonInfoActivity extends AppCompatActivity
        implements PersonInfoActivityView,
        AppBarLayout.OnOffsetChangedListener{

    public static final String ARGUMENT_FIREBASE_USER_UID = "argument_fireBaseUserUid";

    @BindView(R.id.person_info_appbar) AppBarLayout mAppBarLayout;
    @BindView(R.id.person_info_toolbar) Toolbar mToolbar;
    @BindView(R.id.person_info_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.person_info_background_img) ImageView ivBackground;
    @BindView(R.id.person_info_title) TextView tvTitle;
    @BindView(R.id.person_info_iv_person_img) ImageView ivPerson;
    @BindView(R.id.person_info_iv_person_img_small) ImageView ivPersonSmall;
    @BindView(R.id.person_info_progressBar) ProgressBar mProgressBar;

    @BindView(R.id.person_info_fab) FloatingActionButton mFab;
    @BindView(R.id.person_info_thingCardView) CardView singeThingCard;
    @BindView(R.id.person_info_thingCardView_icon_exchange) ImageView icExchange;
    @BindView(R.id.person_info_thingCardView_thing1_imageView) ImageView ivThing1;
    @BindView(R.id.person_info_thingCardView_thing2_imageView) ImageView ivThing2;
    @BindView(R.id.person_info_thingCardView_tv_date) TextView tvThingDate;
    @BindView(R.id.person_info_thingCardView_tv_location) TextView tvLocation;
    @BindView(R.id.person_info_thingCardView_tv_name) TextView tvThingName;
    @BindView(R.id.person_info_thingCardView_tv_category) TextView tvCategory;
    @BindView(R.id.person_info_thingCardView_tv_description) TextView tvThingDescription;
    @BindView(R.id.person_info_thingCardView_hashTagRecyclerView) RecyclerView hashTagsRecyclerView;
    @BindView(R.id.person_info_myThingsRecyclerView) RecyclerView myThingsRecyclerView;

    public static PersonInfoActivityPresenter mPresenter;

    @Inject ViewModelFactory viewModelFactory;
        private PersonInfoActivityViewModel mViewModel;

    private int mMaxScrollSize;
    private boolean mIsAvatarShown = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("@@@", "PersonInfoActivity.onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_info);

        App.getComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PersonInfoActivityViewModel.class);

        ButterKnife.bind(this);


        mMaxScrollSize = mAppBarLayout.getTotalScrollRange();
        mAppBarLayout.addOnOffsetChangedListener(this);


        if (mPresenter == null){
            mPresenter = new PersonInfoActivityPresenter(
                    getIntent().getStringExtra(ARGUMENT_FIREBASE_USER_UID));
        }

        mPresenter.attachView(this);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@@@", "PersonInfoActivity.onResume");
        if (mPresenter != null){

            mPresenter.onActivityResumed();

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
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public ImageView getBackgroundImageView() {
        return ivBackground;
    }

    @Override
    public TextView getTitleTextView() {
        return tvTitle;
    }

    @Override
    public ImageView getPersonImageView() {
        return ivPerson;
    }

    @Override
    public ImageView getPersonSmallImageView() {
        return ivPersonSmall;
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public FloatingActionButton getFab() {
        return mFab;
    }

    @Override
    public CardView getSingeThingCard() {
        return singeThingCard;
    }

    @Override
    public ImageView getIvThing1() {
        return ivThing1;
    }

    @Override
    public ImageView getIvThing2() {
        return ivThing2;
    }

    @Override
    public TextView getTvThingDate() {
        return tvThingDate;
    }

    @Override
    public TextView getTvLocation() {
        return tvLocation;
    }

    @Override
    public TextView getTvThingName() {
        return tvThingName;
    }

    @Override
    public TextView getTvCategory() {
        return tvCategory;
    }

    @Override
    public TextView getTvThingDescription() {
        return tvThingDescription;
    }

    @Override
    public RecyclerView getHashTagsRecyclerView() {
        return hashTagsRecyclerView;
    }

    @Override
    public RecyclerView getMyThingsRecyclerView() {
        return myThingsRecyclerView;
    }


    @Override
    public PersonInfoActivityViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }


    @Override
    public void animateThingsOut(int position, Animation.AnimationListener animationListener) {


        Animation animUnClicked = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        Animation animClicked = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        animClicked.setStartOffset(100);
        animClicked.setDuration(400);
        animClicked.setAnimationListener(animationListener);

        for (int i = 0; i < mRecyclerView.getAdapter().getItemCount(); i++) {

            if (i == position){

                mRecyclerView.getChildAt(i).startAnimation(animClicked);
                mRecyclerView.getChildAt(i).setVisibility(View.GONE);

            } else {
                mRecyclerView.getChildAt(i).startAnimation(animUnClicked);
                mRecyclerView.getChildAt(i).setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void animateThingsIn() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

        mRecyclerView.setVisibility(View.VISIBLE);

        if (mRecyclerView.getAdapter() != null){

            for (int i = 0; i < mRecyclerView.getAdapter().getItemCount(); i++) {

                mRecyclerView.getChildAt(i).startAnimation(animation);
                mRecyclerView.getChildAt(i).setVisibility(View.VISIBLE);

            }

        }
    }


    @Override
    public void onBackPressed() {
        if (mPresenter != null){
            if (mPresenter.exit){
                mPresenter.detachView();
                mPresenter = null;
                finish();
            } else {
                mPresenter.onBackPressed();
            }
        } else {
            super.onBackPressed();

        }

    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void animateSingleThingOut(Animation.AnimationListener animationListener) {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        animation.setAnimationListener(animationListener);

        singeThingCard.startAnimation(animation);
        singeThingCard.setVisibility(View.GONE);

    }

    @Override
    public void animateMyThingsIn() {
        myThingsRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        myThingsRecyclerView.setVisibility(View.VISIBLE);

        mFab.hide();
    }


    @Override
    public void animateMyThingsOut() {

        Animation animation = AnimationUtils.loadAnimation(
                this,
                R.anim.slide_out_right);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                myThingsRecyclerView.setVisibility(View.GONE);

                mFab.show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        myThingsRecyclerView
                .startAnimation(animation);



    }

    @Override
    public void animateMyThingChosen() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                myThingsRecyclerView.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        myThingsRecyclerView.startAnimation(animation);


        ivThing1.animate()
                .scaleY(0.64f).scaleX(0.64f)
                .translationX(-ivThing1.getWidth()/2)
                .translationY(-50f)
                .setDuration(300);

        ivThing2.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        ivThing2.setVisibility(View.VISIBLE);


        icExchange.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
        icExchange.setVisibility(View.VISIBLE);

        mFab.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_done_black_24dp));
        mFab.show();
    }

    @Override
    public void animateHideMyThingChosen() {
        ivThing2.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
        ivThing2.setVisibility(View.GONE);

        icExchange.setVisibility(View.GONE);

        ivThing1.animate()
                .scaleY(1).scaleX(1)
                .translationX(0).translationY(0)
                .setDuration(300);

        mFab.hide();

        mFab.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_exchange_black_24dp));



    }

    @Override
    public void showMyThingIcon() {
        ivThing1.setScaleX(0.64f);
        ivThing1.setScaleY(0.64f);
        ivThing1.setTranslationX(-180f);
        ivThing1.setTranslationY(-50f);


        ivThing2.setVisibility(View.VISIBLE);

        icExchange.setVisibility(View.VISIBLE);


        mFab.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_done_black_24dp));
        mFab.show();
    }

    @Override
    public void startAddThingActivity() {

        startActivity(new Intent(this, AddThingActivity.class));

    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0){
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        }

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= Constants.PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            ivPerson.animate()
                    .scaleX(0).scaleY(0)
                    .setDuration(200)
                    .start();

            ivPersonSmall.setVisibility(View.VISIBLE);


            ivPersonSmall.animate()
                    .scaleX(1).scaleY(1)
                    .setDuration(600)
                    .start();


            tvTitle.animate()
                    .translationX(0)
                    .setDuration(200)
                    .start();


        }

        if (percentage <= Constants.PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            ivPerson.animate()
                    .scaleX(1).scaleY(1)
                    .setDuration(200)
                    .start();


            ivPersonSmall.animate()
                    .scaleX(0).scaleY(0)
                    .setDuration(600)
                    .start();

            ivPersonSmall.setVisibility(View.INVISIBLE);


            tvTitle.animate()
                    .translationX(-ivPersonSmall.getWidth())
                    .setDuration(200)
                    .start();

        }
    }
}

package com.dadabit.everythingexchange.ui.presenter.personInfo;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.presenter.MvpView;

public interface PersonInfoActivityView  extends MvpView{


    AppBarLayout getAppBarLayout();

    Toolbar getToolbar();

    RecyclerView getRecyclerView();

    ImageView getBackgroundImageView();

    TextView getTitleTextView();

    ImageView getPersonImageView();

    ImageView getPersonSmallImageView();

    ProgressBar getProgressBar();

    FloatingActionButton getFab();

    CardView getSingeThingCard();

    ImageView getIvThing1();

    ImageView getIvThing2();

    TextView getTvThingDate();

    TextView getTvLocation();

    TextView getTvThingName();

    TextView getTvCategory();

    TextView getTvThingDescription();

    RecyclerView getHashTagsRecyclerView();

    RecyclerView getMyThingsRecyclerView();

    void animateThingsIn();

    void animateThingsOut(int position, Animation.AnimationListener animationListener);

    void animateSingleThingOut(Animation.AnimationListener animationListener);

    void animateMyThingsIn();

    void animateMyThingsOut();

    void animateMyThingChosen();

    void animateHideMyThingChosen();

    void showMyThingIcon();

    void startAddThingActivity();

    void goBack();

}

package com.dadabit.everythingexchange.ui.presenter.offers;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.presenter.MvpView;

public interface OffersActivityView extends MvpView{

    AppBarLayout getAppBarLayout();

    Toolbar getToolbar();

    CollapsingToolbarLayout getCollapsingToolbarLayout();

    ImageView getToolbarImageView();

    FloatingActionButton getFab();

    RecyclerView getRecyclerView();


    BottomSheetBehavior getBottomSheetBehavior();

    ImageView getThing1ImageView();

    ImageView getThing2ImageView();

    TextView getOfferNameTextView();

    TextView getOfferDescriptionTextView();

    ImageView getOffererImageView();

    TextView getOffererNameTextView();

    Button getConfirmExchangeButton();


    CardView getThingInfoCardView();

    TextView getCategoryTextView();

    TextView getDateTextView();

    TextView getDescriptionTextView();

    RecyclerView getHashTagsRecyclerView();

    Button getChangeButton();

    Button getDeleteButton();

    ImageView getExchangeIcon();

    ProgressBar getProgressBar();

    void animateFab();

    void animateOfferChosen(int position, Animation.AnimationListener animationListener);

    void animateOfferBack(int position);

    void startChangeThingActivity(int thingId);

    void startPersonInfoActivity(String uid);

    void animateThingInfoClicked();

    void goBack();

    void startChatActivity();
}

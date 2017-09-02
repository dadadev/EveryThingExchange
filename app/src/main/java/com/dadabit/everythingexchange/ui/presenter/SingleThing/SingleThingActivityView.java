package com.dadabit.everythingexchange.ui.presenter.SingleThing;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.presenter.MvpView;

public interface SingleThingActivityView extends MvpView{

    RecyclerView getMyThingsRecyclerView();

    RecyclerView getHashTagsRecyclerView();

    Toolbar getToolbar();

    FloatingActionButton getFab();

    ImageView getThingImageView();

    ImageView getPersonImageView();

    TextView getPersonNameTextView();

    TextView getDateTextView();

    TextView getCategoryTextView();

    ImageView getMyThingImageView();

    TextView getTextViewName();

    TextView getTextViewDescription();

    void goBack();

    void startAddThingActivity();

    void startPersonInfoActivity(String uid);

    void showOfferIcon();

    void animateOfferThingsIn();

    void animateOfferThingsOut();

    void animateOfferChosen();

    void animateHideOfferChosen();

}

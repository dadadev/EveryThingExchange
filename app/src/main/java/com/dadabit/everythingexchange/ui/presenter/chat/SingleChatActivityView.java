package com.dadabit.everythingexchange.ui.presenter.chat;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.MvpView;


public interface SingleChatActivityView extends MvpView {

    AppBarLayout getAppBarLayout();

    Toolbar getToolbar();

    FloatingActionButton getFab();

    RecyclerView getRecyclerView();

    EditText getEditText();

    ImageView getButton();

    ImageView getCompanionImageView();

    TextSwitcher getDateTextSwitcher();

    ImageView getCardThing1ImageView();

    ImageView getCardThing2ImageView();

    ProgressBar getProgressBar();

    void animateDateChange(String newDate);

    void backButtonPressed();

    void startPersonInfoActivity(User person);

}

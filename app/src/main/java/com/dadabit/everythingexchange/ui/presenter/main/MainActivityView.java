package com.dadabit.everythingexchange.ui.presenter.main;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.adapter.FireBaseThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.MvpView;

public interface MainActivityView extends MvpView {

    BottomNavigationView getNavigationView();

    NavigationView getSideBar();

    DrawerLayout getDrawerLayout();

    RecyclerView getRecyclerView();

    AppBarLayout getAppBarLayout();

    Toolbar getToolbar();

    TextSwitcher getToolbarTitle();

    CollapsingToolbarLayout getCollapsingLayout();

    ImageView getChatsButton();

    TextView getChatsCountTextView();

    ImageView getCollapsingBackground();

    ProgressBar getProgressBar();

    void startAuthActivity();

    void startSingleThingActivity(int position);

    void startAddThingActivity();

    void startOffersActivity(int position);

    void startSingleChat(int position, int adapterType);

    void showToast(String message);

    void animateCategoriesClicked(int position, Animation.AnimationListener animationListener);

    ActionBar getActivityActionBar();

    void animateMenuIcon(boolean isBack);

    void animateTitleChange(String title);

    void animateRecyclerIn(int direction, Animation.AnimationListener animationListener);

    void animateRecyclerOut(int direction, Animation.AnimationListener animationListener);

    void showUserInfoDialog(String name, String imgUrl);
}

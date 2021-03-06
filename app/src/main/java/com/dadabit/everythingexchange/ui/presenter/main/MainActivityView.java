package com.dadabit.everythingexchange.ui.presenter.main;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.TextureView;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.adapter.FireBaseThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.MvpView;
import com.dadabit.everythingexchange.ui.viewmodel.MainActivityViewModel;

public interface MainActivityView extends MvpView {


    //    ====================GETTERS========================

    MainActivityViewModel getViewModel();

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

    ActionBar getActivityActionBar();

    TextureView getTextureView();

    FloatingActionButton getImageCaptureBtn();

//    ====================TRANSITIONS========================


    void startAuthActivity(int arg);

    void startSingleThingActivity(int position);

    void startAddThingActivity();

    void startOffersActivity(int position);

    void startSingleChat(int position, int adapterType);


    //    ====================ANIMATIONS========================

    void animateCategoriesClicked(int position, Animation.AnimationListener animationListener);

    void animateMenuIcon(boolean isBack);

    void animateTitleChange(String title);

    void animateRecyclerIn(int direction, Animation.AnimationListener animationListener);

    void animateRecyclerOut(int direction, int duration, Animation.AnimationListener animationListener);

    void animateCameraIn();

    void hideCamera(Animation.AnimationListener animationListener);

    void showToast(String message);

    void showUserInfoDialog(String name, String imgUrl);

    void vibrate(int duration);

    void setupToolbar();
}


package com.dadabit.everythingexchange.ui.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.chat.SingleChatActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.chat.SingleChatActivityView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SingleChatActivity extends AppCompatActivity implements SingleChatActivityView, AppBarLayout.OnOffsetChangedListener, TextSwitcher.ViewFactory{


    @BindView(R.id.single_chat_appbar) AppBarLayout mAppBarLayout;
    @BindView(R.id.single_chat_toolbar) Toolbar mToolbar;
    @BindView(R.id.single_chat_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.single_chat_msgEditText) EditText mEditText;
    @BindView(R.id.single_chat_fab_sendButton) ImageView mSendButton;
    @BindView(R.id.single_chat_fab) FloatingActionButton mFab;
    @BindView(R.id.single_chat_companionImg) ImageView ivCompanion;

    @BindView(R.id.single_chat_toolbar_text_switcher) TextSwitcher dateTextSwitcher;
    @BindView(R.id.single_chat_toolbar_ivThing1) ImageView ivCardThing1;
    @BindView(R.id.single_chat_toolbar_ivThing2) ImageView ivCardThing2;

    @BindView(R.id.single_chat_progressBar) ProgressBar mProgressBar;
    public static SingleChatActivityPresenter mPresenter;


    private static final int PERCENTAGE_TO_ANIMATE = 20;
    private boolean mIsCollapsingToolbarShown = true;
    private int mMaxScrollSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("@@@", "SingleChatActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mMaxScrollSize = mAppBarLayout.getTotalScrollRange();
        mAppBarLayout.addOnOffsetChangedListener(this);

        dateTextSwitcher.setFactory(this);

        if (mPresenter == null){
            mPresenter = new SingleChatActivityPresenter();
        }
        mPresenter.attachView(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mPresenter != null){

            mPresenter.onOptionItemSelected(item.getItemId());

        }


        return true;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE && mIsCollapsingToolbarShown) {
            mIsCollapsingToolbarShown = false;

            mFab.hide();

        }

        if (percentage <= PERCENTAGE_TO_ANIMATE && !mIsCollapsingToolbarShown) {
            mIsCollapsingToolbarShown = true;

            mFab.show();

        }

    }

    @Override
    public void onBackPressed() {
        Log.d("@@@", "SingleChatActivity.onBackPressed");
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d("@@@", "SingleChatActivity.onDestroy");
        if (mPresenter != null){
            mPresenter.detachView();
        }
        if (isFinishing()){
            mPresenter.detachRepository();
            mPresenter = null;
        }

        super.onDestroy();
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
    public FloatingActionButton getFab() {
        return mFab;
    }


    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public ImageView getButton() {
        return mSendButton;
    }

    @Override
    public ImageView getCompanionImageView() {
        return ivCompanion;
    }

    @Override
    public TextSwitcher getDateTextSwitcher() {
        return dateTextSwitcher;
    }

    @Override
    public ImageView getCardThing1ImageView() {
        return ivCardThing1;
    }

    @Override
    public ImageView getCardThing2ImageView() {
        return ivCardThing2;
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void animateDateChange(String newDate) {
        dateTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        dateTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));

        dateTextSwitcher.setText(newDate);

    }

    @Override
    public void backButtonPressed() {
        onBackPressed();
    }

    @Override
    public void startPersonInfoActivity(User person) {

        Intent intent = new Intent(this, PersonInfoActivity.class)
                .putExtra(PersonInfoActivity.ARGUMENT_FIREBASE_USER_UID, person.getUid());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(
                            this,
                            ivCompanion,
                            getString(R.string.transitions_id_userPic));


            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }

    }

    @Override
    public View makeView() {
        TextView textView = new TextView(this);
//        textView.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(20);
        return textView;
    }
}

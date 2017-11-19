package com.dadabit.everythingexchange.ui.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.presenter.SingleThing.SingleThingActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.SingleThing.SingleThingActivityView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SingleThingActivity extends AppCompatActivity implements SingleThingActivityView{



    @BindView(R.id.single_thing_toolbar) Toolbar mToolbar;
    @BindView(R.id.single_thing_userPic) ImageView ivUserPic;
    @BindView(R.id.single_thing_tvPersonName) TextView tvPersonName;
    @BindView(R.id.single_thing_tv_category) TextView tvCategory;
    @BindView(R.id.single_thing_tv_date) TextView tvDate;
    @BindView(R.id.single_thing_imageView) ImageView ivThing;
    @BindView(R.id.single_thing_thing2_imageView) ImageView myThingImageView;
    @BindView(R.id.single_thing_tv_name) TextView tvName;
    @BindView(R.id.single_thing_tv_description) TextView tvDescription;
    @BindView(R.id.single_thing_myThingsRecyclerView) RecyclerView myThingsRecyclerView;
    @BindView(R.id.single_thing_hashTagRecyclerView) RecyclerView hashTagsRecyclerView;
    @BindView(R.id.single_thing_fab) FloatingActionButton mFAB;
    @BindView(R.id.single_thing_icon_exchange) ImageView iconExchange;



    public static SingleThingActivityPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("@@@", "SingleThingActivity.Create");
        setContentView(R.layout.activity_single_thing);

        ButterKnife.bind(this);

        if (mPresenter == null){
            mPresenter = new SingleThingActivityPresenter();
        }
        mPresenter.attachView(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thing_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("@@@", "SingleThingActivity.onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@@@", "SingleThingActivity.onResume");
        if (mPresenter != null){

            mPresenter.onActivityResumed();

        }
    }

    @Override
    protected void onDestroy() {
        Log.d("@@@", "SingleThingActivity.onDestroy");
        super.onDestroy();
        if (mPresenter != null){

            mPresenter.detachView();

            if (isFinishing()){
                mPresenter.detachRepository();
                mPresenter = null;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mPresenter != null && mPresenter.blockExit){

            mPresenter.clearOffer();

        } else {

            super.onBackPressed();

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
    public RecyclerView getMyThingsRecyclerView() {
        return myThingsRecyclerView;
    }

    @Override
    public RecyclerView getHashTagsRecyclerView() {
        return hashTagsRecyclerView;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public FloatingActionButton getFab() {
        return mFAB;
    }

    @Override
    public ImageView getThingImageView() {
        return ivThing;
    }

    @Override
    public ImageView getPersonImageView() {
        return ivUserPic;
    }

    @Override
    public TextView getPersonNameTextView() {
        return tvPersonName;
    }

    @Override
    public TextView getDateTextView() {
        return tvDate;
    }

    @Override
    public TextView getCategoryTextView() {
        return tvCategory;
    }

    @Override
    public ImageView getMyThingImageView() {
        return myThingImageView;
    }

    @Override
    public TextView getTextViewName() {
        return tvName;
    }

    @Override
    public TextView getTextViewDescription() {
        return tvDescription;
    }

    @Override
    public void goBack() {

        onBackPressed();

    }

    @Override
    public void startAddThingActivity() {
        startActivity(new Intent(this, AddThingActivity.class));
    }

    @Override
    public void startPersonInfoActivity(String uid) {

        if (uid != null){

            Intent intent = new Intent(this, PersonInfoActivity.class)
                    .putExtra(PersonInfoActivity.ARGUMENT_FIREBASE_USER_UID, uid);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(
                                this,
                                ivUserPic,
                                getString(R.string.transitions_id_userPic));


                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void showOfferIcon() {

        ivThing.setScaleX(0.64f);
        ivThing.setScaleY(0.64f);
        ivThing.setTranslationX(-180f);
        ivThing.setTranslationY(-50f);


        myThingImageView.setVisibility(View.VISIBLE);

        iconExchange.setVisibility(View.VISIBLE);


        mFAB.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_done_black_24dp));

    }

    @Override
    public void animateOfferThingsIn() {
        Log.d("@@@@", "SingleThingPresenter.animateMyThingsIn");

        myThingsRecyclerView
                .startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.slide_in_left));

        myThingsRecyclerView.setVisibility(View.VISIBLE);

        mFAB.hide();
    }

    @Override
    public void animateOfferThingsOut() {

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

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        myThingsRecyclerView
                .startAnimation(animation);


        mFAB.show();

    }

    @Override
    public void animateOfferChosen() {

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


        ivThing.animate()
                .scaleY(0.64f).scaleX(0.64f)
                .translationX(-ivThing.getWidth()/2)
                .translationY(-50f)
                .setDuration(300);

        myThingImageView.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        myThingImageView.setVisibility(View.VISIBLE);


        iconExchange.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
        iconExchange.setVisibility(View.VISIBLE);

        mFAB.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_done_black_24dp));
        mFAB.show();

    }

    @Override
    public void animateHideOfferChosen() {

        myThingImageView.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
        myThingImageView.setVisibility(View.GONE);

        iconExchange.setVisibility(View.GONE);

        ivThing.animate()
                .scaleY(1).scaleX(1)
                .translationX(0).translationY(0)
                .setDuration(300);

        mFAB.setImageDrawable(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_exchange_black_24dp));

    }
}

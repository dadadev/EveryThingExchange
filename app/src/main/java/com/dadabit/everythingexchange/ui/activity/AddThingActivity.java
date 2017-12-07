package com.dadabit.everythingexchange.ui.activity;


import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.presenter.addThing.AddThingActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.addThing.AddThingActivityView;
import com.dadabit.everythingexchange.ui.viewmodel.AddThingActivityViewModel;
import com.dadabit.everythingexchange.ui.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddThingActivity extends AppCompatActivity implements AddThingActivityView{

    @BindView(R.id.addNew_toolbar) Toolbar mToolbar;
    @BindView(R.id.addNew_card) CardView mCardView;
    @BindView(R.id.addNew_imageView) ImageView mImageView;
    @BindView(R.id.addNew_etName) EditText etName;
    @BindView(R.id.addNew_etDescription) EditText etDescription;
    @BindView(R.id.addNew_btnCategory) Button btnCategory;
    @BindView(R.id.addNew_btnSave) FloatingActionButton btnSave;
    @BindView(R.id.addNew_progressBar) ProgressBar mProgressBar;
    @BindView(R.id.addNew_recyclerView) RecyclerView categoriesRecyclerView;
    @BindView(R.id.addNew_hashtagRecyclerView) RecyclerView hashTagRecyclerView;
    @BindView(R.id.addNew_btnHashtag) Button btnHashTag;
    @BindView(R.id.addNew_etHashTag) EditText etHashTag;

    public static AddThingActivityPresenter mPresenter;

    @Inject ViewModelFactory factory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("@@@", "AddThingActivity.onCreate");
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_new_thing);

        App.getComponent().inject(this);

        ButterKnife.bind(this);

        if (mPresenter == null){
            mPresenter = new AddThingActivityPresenter();
        }
        mPresenter.attachView(this);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setTitle(R.string.title_addNewThing);

    }


    @Override
    protected void onResume() {
        Log.d("@@@", "AddThingActivity.onResume");
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.d("@@@", "AddThingActivity.onDestroy");
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        if (isFinishing()) {
            Log.d("@@@", "AddThingActivity.onDestroy.isFinishing");
            mPresenter = null;
        }
    }

    @Override
    public void onBackPressed() {

        if (mPresenter != null && mPresenter.blockExit){

            animateCategoryChoose();

            mPresenter.blockExit = false;


        } else {

            super.onBackPressed();
        }



    }

    @Override
    public void animShowCategories() {

        Animation animation = AnimationUtils
                .loadAnimation(
                        this,
                        R.anim.slide_in_right);
        animation.setDuration(600);

        categoriesRecyclerView.startAnimation(animation);
        categoriesRecyclerView.setVisibility(View.VISIBLE);
//        btnSave.hide();

    }

    @Override
    public void animateCategoryChoose() {

//        btnSave.show();

        Animation animation = AnimationUtils
                .loadAnimation(
                        this,
                        R.anim.slide_out_right);
        animation.setDuration(400);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                categoriesRecyclerView.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        categoriesRecyclerView.startAnimation(animation);

    }

    @Override
    public void animateSaveData() {

        mCardView.startAnimation(
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_right));

        mCardView.setVisibility(View.GONE);
        btnSave.hide();

        mProgressBar.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    @Override
    public void goBack() {
        onBackPressed();
    }


    @Override
    public void showKeyboard() {
        Log.d("@@@", "AddThingActivity.showKeyboard");

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

    }

    @Override
    public ImageView getImageView() {
        return mImageView;
    }

    @Override
    public EditText getNameEditText() {
        return etName;
    }

    @Override
    public EditText getDescriptionEditText() {
        return etDescription;
    }

    @Override
    public Button getBtnCategory() {
        return btnCategory;
    }

    @Override
    public FloatingActionButton getBtnSave() {
        return btnSave;
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }


    @Override
    public RecyclerView getCategoriesRecyclerView() {
        return categoriesRecyclerView;
    }

    @Override
    public RecyclerView getHashTagRecyclerView() {
        return hashTagRecyclerView;
    }

    @Override
    public Button getBtnHashTag() {
        return btnHashTag;
    }

    @Override
    public EditText getHashTagEditText() {
        return etHashTag;
    }

    @Override
    public AddThingActivityViewModel getViewModel() {
        return ViewModelProviders
                .of(this, factory)
                .get(AddThingActivityViewModel.class);
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
}
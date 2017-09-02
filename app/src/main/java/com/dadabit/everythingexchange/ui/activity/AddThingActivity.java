package com.dadabit.everythingexchange.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.presenter.addThing.AddThingActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.addThing.AddThingActivityView;
import com.dadabit.everythingexchange.utils.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddThingActivity extends AppCompatActivity implements AddThingActivityView{

    public static final int REQUEST_IMAGE_CAPTURE = 1488;
    public static final String ARGUMENT_IMAGE = "argument_image";
    public static final String ARGUMENT_THING_ID = "argument_thing_id";


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

    private Bitmap image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_new_thing);

        Log.d("@@@", "AddThingActivity.onCreate");

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

        if (savedInstanceState == null){
            Log.d("@@@", "AddThingActivity.savedInstanceState == null");

            int thingId = getIntent().getIntExtra(ARGUMENT_THING_ID, -1);

            if (thingId != -1){

                mPresenter.changeThing(thingId);
                mToolbar.setTitle(R.string.title_change_thing);

            } else {

                captureImage();
            }

        } else {
            Log.d("@@@", "AddThingActivity.savedInstanceState != null");

            image = savedInstanceState.getParcelable(ARGUMENT_IMAGE);

            if (image != null){
                Log.d("@@@", "AddThingActivity.image != null");

                mPresenter.setImageCapture(image);
            }
        }




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Log.d("@@@", "AddThingActivity.onActivityResult.resultCode = OK");

            try {

                image = Utils.getImageBitmap(
                        getActivityContext(),
                        data.getData());

            } catch (IOException e) {
                e.printStackTrace();
                onBackPressed();
            }

            if (image != null
                    && mPresenter != null){

                mPresenter.setImageCapture(image);

            }

        } else if (resultCode == 0){
            Log.d("@@@", "AddThingActivity.onActivityResult.resultCode = 0 onBackPressed()");
            onBackPressed();
        }
        if (requestCode == Activity.RESULT_CANCELED){

            Log.d("@@@", "AddThingActivity.onActivityResult.resultCode = CANCELED");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d("@@@", "AddThingActivity.onSaveInstanceState");
        outState.putParcelable(ARGUMENT_IMAGE, image);

        super.onSaveInstanceState(outState, outPersistentState);
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
    public void animateCategoryChoose() {

        btnSave.show();

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
    public void captureImage() {
        Log.d("@@@", "AddThingActivity.startActivityForResult");
        startActivityForResult(
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE),
                REQUEST_IMAGE_CAPTURE);
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
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}
package com.dadabit.everythingexchange.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.adapter.CategoriesAdapter;
import com.dadabit.everythingexchange.ui.adapter.ChatsAdapter;
import com.dadabit.everythingexchange.ui.adapter.FireBaseThingsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsAdapter;
import com.dadabit.everythingexchange.ui.fragment.UserInfoDialog;
import com.dadabit.everythingexchange.ui.presenter.main.MainActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.main.MainActivityView;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements
        MainActivityView,
        TextSwitcher.ViewFactory,
        UserInfoDialog.UserChangeDialogListener{

    public static final int REQUEST_IMAGE_GALLERY = 1991;

    @BindView(R.id.main_appBar) AppBarLayout mAppBarLayout;
    @BindView(R.id.main_toolbar) Toolbar mToolbar;
    @BindView(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.main_navigation_view) NavigationView mSideBar;
    @BindView(R.id.main_collapsingLayout) CollapsingToolbarLayout mCollapsingLayout;
    @BindView(R.id.main_background_img) ImageView ivCollapsingBackground;
    @BindView(R.id.main_toolbar_icChat) ImageView chatsButton;
    @BindView(R.id.main_toolbar_icChat_badge_textView) TextView tvChatsCount;
    @BindView(R.id.main_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.main_bottomNavigationView) BottomNavigationView mNavigationView;
    @BindView(R.id.main_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.main_toolbar_text_switcher) TextSwitcher mToolbarTitle;


    public static MainActivityPresenter mPresenter;

    private AnimatedVectorDrawable mMenuDrawable;
    private AnimatedVectorDrawable mBackDrawable;
    private boolean goBack;

    private UserInfoDialog userInfoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("@@@", "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupToolbar();

        if (mPresenter == null){
            mPresenter = new MainActivityPresenter();
        }
        mPresenter.attachView(this);


    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_vector);

            mMenuDrawable = (AnimatedVectorDrawable) ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_menu_animatable);
            mBackDrawable = (AnimatedVectorDrawable) ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_back_animatable);

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbarTitle.setFactory(this);
        mToolbarTitle.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        mToolbarTitle.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@@@", "MainActivity.onResume");

        mPresenter.onActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("@@@", "MainActivity.onPause");

        mPresenter.onActivityPaused();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (goBack){
                mPresenter.onBackButtonPressed();
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        Log.d("@@@", "MainActivity.onDestroy");
        super.onDestroy();
        mPresenter.detachView();
        if (isFinishing()) {
            mPresenter.detachListeners();
            mPresenter = null;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("@@@", "MainActivity.onBackPressed()");
        if (mPresenter.exit){

            super.onBackPressed(); // finish activity
            finish();

        } else {

            mPresenter.onBackButtonPressed();

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
    public BottomNavigationView getNavigationView() {
        return mNavigationView;
    }

    @Override
    public NavigationView getSideBar() {
        return mSideBar;
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
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
    public TextSwitcher getToolbarTitle() {
        return mToolbarTitle;
    }


    @Override
    public CollapsingToolbarLayout getCollapsingLayout() {
        return mCollapsingLayout;
    }

    @Override
    public ImageView getChatsButton() {
        return chatsButton;
    }

    @Override
    public TextView getChatsCountTextView() {
        return tvChatsCount;
    }

    @Override
    public ImageView getCollapsingBackground() {
        return ivCollapsingBackground;
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public ActionBar getActivityActionBar() {
        return getSupportActionBar();
    }

    @Override
    public void startAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    public void startSingleThingActivity(int position) {

        Intent intent = new Intent(this, SingleThingActivity.class);

        FireBaseThingsAdapter.ViewHolder holder
                = (FireBaseThingsAdapter.ViewHolder) mRecyclerView
                .findViewHolderForAdapterPosition(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && holder != null) {


            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(
                            this,
                            holder.mImageView,
                            "transitionThingImg");

            startActivity(intent, options.toBundle());

        } else {

            startActivity(intent);
        }
    }


    @Override
    public void startAddThingActivity() {
        startActivity(new Intent(this, AddThingActivity.class));
    }


    @Override
    public void startOffersActivity(int position) {

        Intent intent = new Intent(this, OffersActivity.class);


        MyThingsAdapter.ThingViewHolder holder =
                (MyThingsAdapter.ThingViewHolder) mRecyclerView
                        .findViewHolderForAdapterPosition(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && holder != null) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(
                            this,
                            holder.mImageView,
                            getString(R.string.transitions_id_thing1Img));

            startActivity(intent, options.toBundle());
        } else {

            startActivity(intent);
        }


    }

    @Override
    public void startSingleChat(int position, int adapterType) {

        Intent intent = new Intent(this, SingleChatActivity.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            switch (adapterType){
                case Constants.ADAPTER_TYPE_CHATS:

                    ChatsAdapter.ViewHolder holder =
                            (ChatsAdapter.ViewHolder) mRecyclerView
                                    .findViewHolderForAdapterPosition(position);

                    if (holder != null){

                        Pair<View, String> p1 = Pair.create(
                                (View)holder.ivThing1,
                                getString(R.string.transitions_id_thing1Img));
                        Pair<View, String> p2 = Pair.create(
                                (View)holder.ivThing2,
                                getString(R.string.transitions_id_thing2Img));
                        Pair<View, String> p3 = Pair.create(
                                (View)holder.ivUserPic,
                                getString(R.string.transitions_id_userPic));
                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3);

                        startActivity(intent, options.toBundle());

                    }

                    break;

                case Constants.ADAPTER_TYPE_MY_THINGS:


                    MyThingsAdapter.ExchangeViewHolder exchangeViewHolder =
                            (MyThingsAdapter.ExchangeViewHolder) mRecyclerView
                                    .findViewHolderForAdapterPosition(position);

                    if (exchangeViewHolder != null){

                        Pair<View, String> p1 = Pair.create(
                                (View)exchangeViewHolder.ivThing1,
                                getString(R.string.transitions_id_thing1Img));
                        Pair<View, String> p2 = Pair.create(
                                (View)exchangeViewHolder.ivThing2,
                                getString(R.string.transitions_id_thing2Img));
                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);

                        startActivity(intent, options.toBundle());
                    }

                    break;
            }
        } else {

            startActivity(intent);

        }

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void animateCategoriesClicked(int position, Animation.AnimationListener animationListener) {

        CategoriesAdapter.ViewHolder holder
                = (CategoriesAdapter.ViewHolder) mRecyclerView
                .findViewHolderForAdapterPosition(position);

        if (holder != null){


            Animation animUp = AnimationUtils.loadAnimation(getActivityContext(), R.anim.slide_out_top);



            animUp.setStartOffset(200);
            animUp.setDuration(600);
            animUp.setAnimationListener(animationListener);


            holder.mCardView.startAnimation(animUp);
            holder.mCardView.setVisibility(View.GONE);


            for (int i = 0; i < mRecyclerView.getAdapter().getItemCount(); i++) {

                if (i != position){

                    holder = (CategoriesAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);

                    if (holder != null){

                        if (i % 2 == 0){
                            Animation animLeft = AnimationUtils.loadAnimation(getActivityContext(), R.anim.slide_out_left);
                            animLeft.setStartOffset(i*50);

                            holder.mCardView.startAnimation(animLeft);
                            holder.mCardView.setVisibility(View.GONE);

                        } else {

                            Animation animRight = AnimationUtils.loadAnimation(
                                    getActivityContext(), R.anim.slide_out_right);
                            animRight.setStartOffset(i*50);


                            holder.mCardView.startAnimation(animRight);
                            holder.mCardView.setVisibility(View.GONE);

                        }
                    }

                }
            }
        }
    }


    @Override
    public void animateMenuIcon(boolean isBack) {

        goBack = isBack;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (goBack){

                getSupportActionBar().setHomeAsUpIndicator(mMenuDrawable);
                mMenuDrawable.start();


            } else {

                getSupportActionBar().setHomeAsUpIndicator(mBackDrawable);
                mBackDrawable.start();

            }
        }
    }

    @Override
    public void animateTitleChange(String title) {
        Log.d("@@@", "MainActivity.animateTitleChange "+title);
        mToolbarTitle.setText(title);
    }


    @Override
    public void animateRecyclerIn(int direction, Animation.AnimationListener animationListener) {

        switch (direction){
            case Constants.DIRECTION_LEFT:

                Animation animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

                animLeft.setDuration(400);

                animLeft.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animLeft);
                mRecyclerView.setVisibility(View.VISIBLE);

                break;
            case Constants.DIRECTION_RIGHT:

                Animation animRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

                animRight.setDuration(400);

                animRight.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animRight);
                mRecyclerView.setVisibility(View.VISIBLE);

                break;
            case Constants.DIRECTION_UP:

                Animation animUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);

                animUp.setDuration(400);

                animUp.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animUp);
                mRecyclerView.setVisibility(View.VISIBLE);

                break;

            case Constants.DIRECTION_DOWN:

                Animation animDown = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);

                animDown.setDuration(400);

                animDown.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animDown);
                mRecyclerView.setVisibility(View.VISIBLE);

                break;
        }

    }

    @Override
    public void animateRecyclerOut(int direction, Animation.AnimationListener animationListener) {
        switch (direction){
            case Constants.DIRECTION_LEFT:

                Animation animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

                animLeft.setDuration(400);

                animLeft.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animLeft);
                mRecyclerView.setVisibility(View.GONE);

                break;
            case Constants.DIRECTION_RIGHT:
                Animation animRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

                animRight.setDuration(400);

                animRight.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animRight);
                mRecyclerView.setVisibility(View.GONE);

                break;

            case Constants.DIRECTION_UP:

                Animation animUp = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);

                animUp.setDuration(400);

                animUp.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animUp);
                mRecyclerView.setVisibility(View.GONE);

                break;

            case Constants.DIRECTION_DOWN:

                Animation animDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);

                animDown.setDuration(400);

                animDown.setAnimationListener(animationListener);

                mRecyclerView.startAnimation(animDown);
                mRecyclerView.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public void showUserInfoDialog(String name, String imgUrl) {
        Log.d("@@@", "MainActivity.showUserInfoDialog");

        userInfoDialog = UserInfoDialog.newInstance(name, imgUrl);
        userInfoDialog.show(getSupportFragmentManager(), "user_info_dialog");



    }


    //Toolbar Title TextSwitcher Factory
    @Override
    public View makeView() {
        TextView textView = new TextView(this);
//        textView.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(20);
        return textView;
    }

    @Override
    public void onUserNameChange(String name) {

        if (mPresenter!= null
                && mPresenter.isNewName(name)){

            showToast("New user name: "+name);

        }
    }

    @Override
    public void onUserImageChange(Bitmap bitmap) {

        if (bitmap == null){

            startActivityForResult(
                    new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                    REQUEST_IMAGE_GALLERY);
        } else {

            mPresenter.setUserImage(bitmap);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            switch (requestCode){
                case REQUEST_IMAGE_GALLERY:

                    try {

                        Bitmap bitmap = Utils.getImageBitmap(getActivityContext(), data.getData());

                        if (userInfoDialog == null){
                            Log.d("@@@", "MainActivity.userInfoDialog == NULL !!! ");

                            userInfoDialog = (UserInfoDialog) getSupportFragmentManager().findFragmentByTag("user_info_dialog");
                            userInfoDialog.changeImage(bitmap);

                        } else {
                            Log.d("@@@", "MainActivity.userInfoDialog != NULL ");
                            userInfoDialog.changeImage(bitmap);

                        }



                    } catch (Exception e) {
                        e.printStackTrace();

                        showToast("Sorry, something went wrong");
                    }


                    break;
            }

        }

    }
}

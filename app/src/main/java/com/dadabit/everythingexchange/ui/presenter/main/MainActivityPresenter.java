package com.dadabit.everythingexchange.ui.presenter.main;


import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.firebase.ImageUploader;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.MyThingsAdapterItem;
import com.dadabit.everythingexchange.model.vo.ThingCategory;
import com.dadabit.everythingexchange.ui.adapter.CategoriesAdapter;
import com.dadabit.everythingexchange.ui.adapter.ChatsAdapter;
import com.dadabit.everythingexchange.ui.adapter.FireBaseThingsAdapter;
import com.dadabit.everythingexchange.ui.adapter.LocationsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.ui.viewmodel.MainActivityViewModel;
import com.dadabit.everythingexchange.utils.BottomNavigationViewBehavior;
import com.dadabit.everythingexchange.utils.CameraHelper;
import com.dadabit.everythingexchange.utils.ChatItemsManager;
import com.dadabit.everythingexchange.utils.Constants;

import java.util.List;


public class MainActivityPresenter extends BasePresenter<MainActivityView>
        implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {


    private MainActivityViewModel mViewModel;

    private BottomNavigationViewBehavior navigationViewBehavior;

    private CategoriesAdapter categoriesAdapter;
    private ChatsAdapter chatsAdapter;
    private MyThingsAdapter myThingsAdapter;
    private LocationsAdapter locationsAdapter;

    public MainActivityPresenter() {
        Log.d("@@@", "MainActivityPresenter.create");
    }



    @Override
    public void attachView(MainActivityView mainActivityView) {
        super.attachView(mainActivityView);
        Log.d("@@@", "MainActivityPresenter.attachView");

        mViewModel = getView().getViewModel();

        if ( mViewModel.getUser() == null ) {

            getView().startAuthActivity(Constants.AUTH_LOG_IN);

        } else {

            setupViews();

        }
    }

    private void setupViews() {

        initToolbar();

        initSideBar();

        initNavigationView();

        setRecyclerAdapter(mViewModel.getState().getAdapterType());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.bottomMenu_categories:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_categories");

                if (mViewModel.getState().getAdapterType() != Constants.ADAPTER_TYPE_CATEGORIES){
                    Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_categories. show");


                    getView().vibrate(10);

                    showCategories(mViewModel.getState().getAdapterType());

                }



                break;
            case R.id.bottomMenu_myThings:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_myThings");

                if (mViewModel.getState().getAdapterType() != Constants.ADAPTER_TYPE_MY_THINGS){

                    getView().vibrate(10);

                    showMyThings(mViewModel.getState().getAdapterType());

                }

                break;
            case R.id.bottomMenu_addNewThing:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_addNew");

                getView().vibrate(30);

                showCamera();

                break;
            case R.id.side_bar_name:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_name");

                getView().vibrate(10);

                getView().getDrawerLayout().closeDrawer(GravityCompat.START);

                getUiHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()){
                            getView().showUserInfoDialog(
                                    mViewModel.getUser().getName(),
                                    mViewModel.getUser().getImgUrl());
                        }
                    }
                }, 300);


                break;
            case R.id.side_bar_location:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_location");

                getView().vibrate(10);

                getView().getDrawerLayout().closeDrawer(GravityCompat.START);

                showLocations(mViewModel.getState().getAdapterType());

                break;
            case R.id.side_bar_sign_out:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_sign_out");

                getView().vibrate(350);

                getView().getDrawerLayout().closeDrawer(GravityCompat.START);

                getView().startAuthActivity(Constants.AUTH_LOG_OUT);

                break;
        }
        return true;
    }

    private void showCamera() {
        Log.d("@@@", "MainActivityPresenter.showCamera");

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_CAMERA);

        getView().getAppBarLayout().setExpanded(false,true);

        getView().animateMenuIcon(true);

        mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_addNewThing));

        getView().animateRecyclerOut(
                Constants.DIRECTION_RIGHT,
                350,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        navigationViewBehavior.slideDown();


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

        mViewModel.getCameraHelper().setCallback(
                new CameraHelper.CamCallback() {
                    @Override
                    public void onCameraReady() {
                        Log.d("@@@", "MainActivityPresenter.CameraHelper.onCameraReady");

                        mViewModel.getCameraHelper()
                                .setCaptureSetting(
                                        CaptureRequest.CONTROL_MODE,
                                        CameraMetadata.CONTROL_MODE_AUTO);

                        mViewModel.getCameraHelper().startPreview();

                        getView().animateCameraIn();

                    }

                    @Override
                    public void onPicture(byte[] image) {
                        Log.d("@@@", "MainActivityPresenter.CameraHelper.onPicture");


//                        getUiHandler().post(new Runnable() {
//                            @Override
//                            public void run() {
//                                getView().getProgressBar().setVisibility(View.VISIBLE);
//
//                                getView().startAddThingActivity();
//                            }
//                        });

                        mViewModel.saveNewThing(image);


                        getView().startAddThingActivity();

//                        saveNewImage(image);

                    }

                    @Override
                    public void onError(String message) {
                        Log.e("@@@", "MainActivityPresenter.CameraHelper.onError");

                    }

                    @Override
                    public void onCameraDisconnected() {
                        Log.d("@@@", "MainActivityPresenter.CameraHelper.onCameraDisconnected");

                    }
                }
        );

        mViewModel.getCameraHelper().setTextureView(getView().getTextureView());

        getView().getTextureView().setVisibility(View.VISIBLE);

        getView().getImageCaptureBtn().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("@@@", "MainActivityPresenter.ImageCaptureBtn.OnClick");

                        getView().getProgressBar().setVisibility(View.VISIBLE);

                        mViewModel.getCameraHelper().takePic();
                    }
                });

    }


    private CategoriesAdapter.CategoriesClickCallback categoriesClickCallback = new CategoriesAdapter.CategoriesClickCallback() {
        @Override
        public void onClick(int position) {
//            Log.d("@@@", "MainActivityPresenter.CategoriesAdapter.onClick: "+mRepository.getCategories().get(position).getName());

            getView().vibrate(10);

            mViewModel.getState().setChosenCategory(position);

            getView().getAppBarLayout().setExpanded(false,true);

            getView().animateMenuIcon(true);

            getView().animateCategoriesClicked(position, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    setRecyclerAdapter(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }
    };

    private FireBaseThingsAdapter.СlickCallback fireBaseThingsClickCallback = new FireBaseThingsAdapter.СlickCallback() {
        @Override
        public void onThingClick(int position) {
            Log.d("@@@", "MainActivityPresenter.FireBaseThingsAdapter.onThingClick: "+position);

            getView().vibrate(10);

            mViewModel.getState().setChosenFireBaseThing(position);

            getView().startSingleThingActivity(position);
        }
    };

    private MyThingsAdapter.ClickListener myThingsClickCallback = new MyThingsAdapter.ClickListener() {
        @Override
        public void onClick(int position, int type, String thingId) {
            Log.d("@@@", "MainActivityPresenter.MyThingsClickCallback: "+position);


            getView().vibrate(10);

            if (type == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

                mViewModel.setChosenChatId(thingId);
//                mViewModel.getState().setChosenChatId(mRepository.getExchangeIdByThing(thingId));

                getView().startSingleChat(position, Constants.ADAPTER_TYPE_MY_THINGS);


            } else {

                mViewModel.getState().setChosenMyThing(position);

                getView().startOffersActivity(position);
            }
        }
    };

    private ChatsAdapter.ChatsClickCallback chatsClickCallback = new ChatsAdapter.ChatsClickCallback() {
        @Override
        public void onClick(int position, int id) {
            Log.d("@@@", "ChatsActivityPresenter.ChatsClickCallback.onClick: "+position +" id:" +id);

            getView().vibrate(10);

            mViewModel.setChosenChatId(id);
            getView().startSingleChat(position, Constants.ADAPTER_TYPE_CHATS);
        }
    };

    private LocationsAdapter.ClickListener locationsAdapterClickCallback =
            new LocationsAdapter.ClickListener() {
                @Override
                public void onLocationClick(String location) {
                    Log.d("@@@", "MainActivityPresenter.locationsAdapterClickCallback: "+location);

                    getView().vibrate(10);

                    mViewModel.getUser().setLocation(location);

                    onBackButtonPressed();

                }

                @Override
                public void onHomeLocationChanged(String location) {
                    Log.d("@@@", "MainActivityPresenter.onHomeLocationChanged: "+location);

                    mViewModel.setHomeLocation(location);

                    getView().getSideBar()
                            .getMenu()
                            .findItem(R.id.side_bar_location)
                            .setTitle(location);

                    getView().showToast("Home location changed to: "+location);


                }
            };




//    private MyThingsManager.MyThingsChangeObserver myThingsChangeObserver = new MyThingsManager.MyThingsChangeObserver() {
//        @Override
//        public void onChange(final int position) {
//            Log.d("@@@", "MainActivityPresenter.MyThingsAdapter.onChange: "+ position);
//
//            if (isViewAttached()
//                    && myThingsAdapter != null){
//
//
//                getUiHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (position == -1){
//
//                            myThingsAdapter.notifyDataSetChanged();
//                        } else {
//
//                            myThingsAdapter.notifyItemChanged(position);
//                        }
//                    }
//                });
//
//
//            }
//        }
//    };


    private ChatItemsManager.ChatsAdapterCallback chatsAdapterChangeListener =
            new ChatItemsManager.ChatsAdapterCallback() {
                @Override
                public void onChatsChange(List<ChatItem> changedChatItems) {
                    Log.d("@@@", "MainActivityPresenter.onChatsChange");

                    if (isViewAttached() && chatsAdapter != null){

                        chatsAdapter.setChatItems(changedChatItems);
                    }
                }
            };



    private void initToolbar() {
        Log.d("@@@", "MainActivityPresenter.init.start");

        getView().setupToolbar();

        mViewModel.getTitle()
                .observe(
                        getView().getLifecycleOwner(),
                        new Observer<String>() {
                            @Override
                            public void onChanged(@Nullable String newTitle) {
                                getView().animateTitleChange(newTitle);
                            }
                        });

        switch (mViewModel.getState().getAdapterType()){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                mViewModel.setTitle(mViewModel.getUser().getLocation());

                getView().getAppBarLayout().setExpanded(false,false);

                getView().getCollapsingLayout().setVisibility(View.GONE);

                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().getCollapsingLayout().setVisibility(View.VISIBLE);

                getView().animateMenuIcon(true);

                if (mViewModel.getCategories().getValue() != null){

                    mViewModel.setTitle(
                            mViewModel
                                    .getCategories()
                                    .getValue()
                                    .get(mViewModel.getState()
                                            .getChosenCategory())
                                    .getName()
                    );


                    getView().getCollapsingBackground()
                            .setImageBitmap(
                                    mViewModel
                                            .getCategories()
                                            .getValue()
                                            .get(mViewModel.getState()
                                                    .getChosenCategory())
                                            .getImgBitmap());

                }

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_my_things));

                getView().animateMenuIcon(true);

                break;

            case Constants.ADAPTER_TYPE_CHATS:

                mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_chats));

                getView().animateMenuIcon(true);

                break;

            case Constants.ADAPTER_TYPE_LOCATION:

                mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_locations));

                getView().animateMenuIcon(true);

                break;
        }

        getView().getChatsButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getView().vibrate(10);

                        showChats(mViewModel.getState().getAdapterType());
                    }
                });

        getView().getToolbarTitle().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewModel.getState().getAdapterType()
                                == Constants.ADAPTER_TYPE_CATEGORIES){

                            getView().vibrate(10);

                            showLocations(Constants.ADAPTER_TYPE_CATEGORIES);
                        }
                    }
                }
        );
    }

    private void initSideBar() {

        getView().getSideBar().setNavigationItemSelectedListener(this);

        Menu menu = getView().getSideBar().getMenu();

        View navHeader = getView().getSideBar().getHeaderView(0);

        Glide.with(getView().getActivityContext())
                .load(mViewModel.getUser().getImgUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) navHeader.findViewById(R.id.side_bar_imageView));

        menu.findItem(R.id.side_bar_name).setTitle(
                mViewModel.getUser().getName());
        menu.findItem(R.id.side_bar_location).setTitle(
                mViewModel.getHomeLocation());


    }

    private void initNavigationView() {

        CoordinatorLayout.LayoutParams layoutParams
                = (CoordinatorLayout.LayoutParams) getView().getNavigationView().getLayoutParams();

        if (navigationViewBehavior == null){
            navigationViewBehavior = new BottomNavigationViewBehavior();
        }

        layoutParams.setBehavior(navigationViewBehavior);

        getView().getNavigationView()
                .setOnNavigationItemSelectedListener(this);


        if (mViewModel.getState().getBottomNavigation() == Constants.IS_HIDDEN
                && mViewModel.getState().getAdapterType() == Constants.ADAPTER_TYPE_FIREBASE_THINGS){

            getView().getNavigationView().setVisibility(View.INVISIBLE);

        }

    }



    private void showCategories(int previousAdapterType) {

        getView().animateMenuIcon(false);

        mViewModel.getState().setAppBarLayout(Constants.IS_HIDDEN);

        mViewModel.setTitle(mViewModel.getUser().getLocation());

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CATEGORIES);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_RIGHT,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CATEGORIES);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_CHATS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CATEGORIES);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;



            case Constants.ADAPTER_TYPE_LOCATION:


                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CATEGORIES);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            default:

                setRecyclerAdapter(Constants.ADAPTER_TYPE_CATEGORIES);

                break;


        }

        if (mViewModel.getState().getAdapterType() == Constants.ADAPTER_TYPE_CATEGORIES){

            getView().getNavigationView().setSelectedItemId(R.id.bottomMenu_categories);


        }


    }

    private void showMyThings(int previousAdapterType) {

        mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_my_things));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:


                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_LEFT,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_LEFT,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_CHATS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                break;


            case Constants.ADAPTER_TYPE_LOCATION:


                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;


            case Constants.ADAPTER_TYPE_CAMERA:

                setRecyclerAdapter(Constants.ADAPTER_TYPE_MY_THINGS);

                break;


        }


    }

    private void showChats(int previousAdapterType) {

        mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_chats));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CHATS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CHATS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_RIGHT,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CHATS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_LOCATION:


                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_CHATS);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

        }
    }

    private void showLocations(int previousAdapterType) {


        mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_locations));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_LOCATION);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_LOCATION);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_RIGHT,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_LOCATION);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case Constants.ADAPTER_TYPE_CHATS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_UP,
                        400,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                setRecyclerAdapter(Constants.ADAPTER_TYPE_LOCATION);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

        }

    }


    private void setRecyclerAdapter(int type){

        switch (type){

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                setFireBaseThingsAdapter();

                break;

            case Constants.ADAPTER_TYPE_CATEGORIES:

                setCategoriesAdapter();

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                setMyThingsAdapter();

                break;
            case Constants.ADAPTER_TYPE_CHATS:

                setChatsAdapter();

                break;

            case Constants.ADAPTER_TYPE_LOCATION:

                setLocationsAdapter();

                break;

            case Constants.ADAPTER_TYPE_CAMERA:

                setCameraView();

                break;
        }


    }

    private void setCameraView() {



    }


    private void setFireBaseThingsAdapter() {

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

        getView().getCollapsingLayout().setVisibility(View.VISIBLE);

        getView().getAppBarLayout()
                .setExpanded(false, false);

        if (mViewModel.getCategories().getValue() != null){

            mViewModel.setTitle(
                    mViewModel.getCategories()
                            .getValue()
                            .get(mViewModel.getState().getChosenCategory())
                            .getName());

            getView().getCollapsingBackground()
                    .setImageBitmap(
                            mViewModel.getCategories()
                                    .getValue()
                                    .get(mViewModel.getState().getChosenCategory())
                                    .getImgBitmap());
        }

        getView().getCollapsingBackground().setVisibility(View.VISIBLE);

        getView().getProgressBar().setVisibility(View.VISIBLE);

        mViewModel.getThingItems().observe(
                getView().getLifecycleOwner(),
                new Observer<List<FireBaseThingItem>>() {
                    @Override
                    public void onChanged(@Nullable List<FireBaseThingItem> fireBaseThingItems) {
                        getView().getProgressBar().setVisibility(View.GONE);

                        if (fireBaseThingItems != null
                                && mViewModel.getState().getAdapterType() ==
                                        Constants.ADAPTER_TYPE_FIREBASE_THINGS){

                            getView().getRecyclerView().setLayoutManager(
                                    new LinearLayoutManager(
                                            getView().getActivityContext(),
                                            LinearLayoutManager.VERTICAL,false));

                            getView().getRecyclerView().setAdapter(
                                    new FireBaseThingsAdapter(
                                            getView().getActivityContext(),
                                            fireBaseThingItems,
                                            fireBaseThingsClickCallback));

                            getView().animateRecyclerIn(
                                    Constants.DIRECTION_DOWN,null);

                            getView().getAppBarLayout()
                                    .setExpanded(true, true);

                            mViewModel.getState()
                                    .setAppBarLayout(Constants.IS_SHOWN);
                        } else {

                            getView().showToast("LOADING ERROR");
                        }

                    }
                }
        );

        mViewModel.loadFireBaseThings();

//        mRepository.loadFireBaseThings(
//                new LoadThingsCallback() {
//                    @Override
//                    public void onLoaded(final boolean isDataExist) {
//
//                        if (isViewAttached() &&
//                                mRepository.getState().getAdapterType() ==
//                                        Constants.ADAPTER_TYPE_FIREBASE_THINGS){
//
//                            getUiHandler().post(new Runnable() {
//                                @Override
//                                public void run() {
//
//
//                                    if (isDataExist){
//
//                                        getView().getRecyclerView().setLayoutManager(
//                                                new LinearLayoutManager(
//                                                        getView().getActivityContext(),
//                                                        LinearLayoutManager.VERTICAL,false));
//
//                                        getView().getRecyclerView().setAdapter(
//                                                new FireBaseThingsAdapter(
//                                                        getView().getActivityContext(),
//                                                        mRepository.getFireBaseItems(),
//                                                        fireBaseThingsClickCallback));
//
//                                        getView().animateRecyclerIn(
//                                                Constants.DIRECTION_DOWN,null);
//
//                                        getView().getAppBarLayout()
//                                                .setExpanded(true, true);
//
//                                        mRepository.getState()
//                                                .setAppBarLayout(Constants.IS_SHOWN);
//                                    } else {
//
//                                        getView().showToast("LOADING ERROR");
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });


    }

    private void setCategoriesAdapter() {

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_CATEGORIES);

        getView().getAppBarLayout().setExpanded(false, true);

        getView().getRecyclerView().setVisibility(View.VISIBLE);


        getView().getRecyclerView().setLayoutManager(
                new GridLayoutManager(getView().getActivityContext(), 2));

        getView().getRecyclerView()
                .setAdapter(
                        categoriesAdapter = new CategoriesAdapter(
                                getView().getActivityContext(),
                                categoriesClickCallback));

        Log.d("@@@", "MAIN_PRESENTER.setCategoriesAdapter.observe");

        mViewModel.getCategories().observe(
                getView().getLifecycleOwner(),
                new Observer<List<ThingCategory>>() {
                    @Override
                    public void onChanged(@Nullable List<ThingCategory> thingCategories) {

                        Log.d("@@@", "MAIN_PRESENTER.setCategoriesAdapter.observe.onChanged");

                        if (categoriesAdapter != null){

                            categoriesAdapter.setCategories(thingCategories);

                        }
                    }
                }
        );

        if (navigationViewBehavior != null
                && navigationViewBehavior.isDown){
            navigationViewBehavior.slideUp();
        }


        getView().getCollapsingBackground().setVisibility(View.GONE);


    }

    private void setMyThingsAdapter() {

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_MY_THINGS);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(
                        getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL,false));

        getView().getRecyclerView().setAdapter(
                myThingsAdapter = new MyThingsAdapter(
                        getView().getAppContext(),
                        myThingsClickCallback));

        mViewModel.getMyThings().observe(
                getView().getLifecycleOwner(),
                new Observer<List<MyThingsAdapterItem>>() {
                    @Override
                    public void onChanged(@Nullable List<MyThingsAdapterItem> myThingsAdapterItems) {

                        if (myThingsAdapter != null
                                && mViewModel.getState().getAdapterType()
                                == Constants.ADAPTER_TYPE_MY_THINGS){

                            myThingsAdapter.setItems(myThingsAdapterItems);

                            getView().animateRecyclerIn(
                                    Constants.DIRECTION_LEFT, null);
                            getView().getAppBarLayout()
                                    .setExpanded(false, true);
                            getView().getCollapsingBackground()
                                    .setVisibility(View.GONE);
                        }


                    }
                }
        );
    }

    private void setChatsAdapter() {

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_CHATS);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL, false));

        getView().getRecyclerView().setAdapter(
                chatsAdapter = new ChatsAdapter(
                        getView().getAppContext(),
                        chatsClickCallback));

        getView().animateRecyclerIn(Constants.DIRECTION_DOWN, null);
        getView().getAppBarLayout().setExpanded(false, true);

//        mRepository.getChatItemsManager().attachChatsAdapterListener(chatsAdapterChangeListener);


        getView().getCollapsingBackground().setVisibility(View.GONE);

    }

    private void setLocationsAdapter() {

        mViewModel.getState().setAdapterType(Constants.ADAPTER_TYPE_LOCATION);

        getView().getCollapsingBackground().setVisibility(View.GONE);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL, false));


        getView().getRecyclerView().setAdapter(
                locationsAdapter = new LocationsAdapter(
                        getView().getAppContext(),
                        locationsAdapterClickCallback ));

        getView().getAppBarLayout().setExpanded(false, true);

        getView().getProgressBar().setVisibility(View.VISIBLE);

//        mRepository.loadLocations(new Repository.LocationsLoaderCallback() {
//            @Override
//            public void onLocationsLoad(final List<String> locations, final int homePosition) {
//                Log.d("@@@", "MainActivityPresenter.LocationsLoaderCallback.onLocationsLoad");
//
//                if (isViewAttached() && locationsAdapter != null){
//
//                    getUiHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            getView().getProgressBar().setVisibility(View.GONE);
//
//                            locationsAdapter.setLocations(locations, homePosition);
//                            getView().animateRecyclerIn(Constants.DIRECTION_DOWN, null);
//
//                        }
//                    });
//                }
//            }
//        });

    }







    public void onActivityResumed(){
        Log.d("@@@", "MainActivityPresenter.onActivityResumed");


//        mRepository.getChatItemsManager().attachNewMessagesListener(
//                new ChatItemsManager.MessagesCounterListener() {
//                    @Override
//                    public void onCounterChange(final int counter) {
//                        if (isViewAttached()){
//                            getUiHandler().post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (counter > 0){
//                                        getView().getChatsCountTextView().setVisibility(View.VISIBLE);
//                                        getView().getChatsCountTextView().setText(String.valueOf(counter));
//                                    } else {
//                                        getView().getChatsCountTextView().setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
//        );


//        if (chatsAdapter != null){
//            mRepository.getChatItemsManager().attachChatsAdapterListener(chatsAdapterChangeListener);
//        }
//
//        if (myThingsAdapter != null){
//
//            mRepository.getMyThingsManager().attachMyThingsChangeObserver(myThingsChangeObserver);
//
//        } else if (mRepository.getState().isNewThingAdd()){
//
//            mRepository.getState().setNewThingAdd(false);
//
//            showMyThings(mRepository.getState().getAdapterType());
//        }

    }

    public void onActivityPaused(){

//        mRepository.getChatItemsManager().detachNewMessagesListener();
//        mRepository.getChatItemsManager().detachChatsAdapterListener();

    }

    public Boolean exit = false;
    public void onBackButtonPressed(){
        Log.d("@@@", "MainActivityPresenter.onBackButtonPressed\nAdapter Type: "+mViewModel.getState().getAdapterType());

        getView().vibrate(15);

        switch (mViewModel.getState().getAdapterType()){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().showToast(getView().getActivityContext().getString(R.string.toast_close_app));
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3000);

                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                mViewModel.getState().getPreviousAdapterType();

//                getView().getAppBarLayout()
//                        .setExpanded(false,false);
//
//                getView().getCollapsingLayout().setVisibility(View.GONE);


//                if (type != Constants.ADAPTER_TYPE_FIREBASE_THINGS){
//
//                    getUiHandler().postDelayed(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    if (isViewAttached() &&
//                                            mRepository.getState().getAdapterType()
//                                                    != Constants.ADAPTER_TYPE_FIREBASE_THINGS){
//
//                                        getView().getAppBarLayout()
//                                                .setExpanded(false,false);
//
//                                        getView().getCollapsingLayout().setVisibility(View.GONE);
//                                    }
//
//
//                                }
//                            },
//                            1000
//                    );
//
//                }

                showCategories(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                switch (mViewModel.getState().getPreviousAdapterType()){

                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_RIGHT,
                                400,
                                new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                        setRecyclerAdapter(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                        break;

                    case Constants.ADAPTER_TYPE_CHATS:

                        showChats(Constants.ADAPTER_TYPE_MY_THINGS);
//                        getView().animateRecyclerOut(
//                                Constants.DIRECTION_RIGHT,
//                                new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//
//                                        setChatsAdapter();
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
                        break;

                    default:

                        showCategories(Constants.ADAPTER_TYPE_MY_THINGS);

                        break;
                }
                break;

            case Constants.ADAPTER_TYPE_CHATS:

                switch (mViewModel.getState().getPreviousAdapterType()){
                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_DOWN,
                                400,
                                new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                        setRecyclerAdapter(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });


                        break;
                    case Constants.ADAPTER_TYPE_MY_THINGS:

                        showMyThings(Constants.ADAPTER_TYPE_CHATS);

                        break;

                    default:

                        showCategories(Constants.ADAPTER_TYPE_CHATS);

                        break;
                }
//                mRepository.getChatItemsManager().detachChatsAdapterListener();
                chatsAdapter = null;
                break;

            case Constants.ADAPTER_TYPE_LOCATION:


                switch (mViewModel.getState().getPreviousAdapterType()){
                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_DOWN,
                                400,
                                new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                        setRecyclerAdapter(Constants.ADAPTER_TYPE_FIREBASE_THINGS);



                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });


                        break;
                    case Constants.ADAPTER_TYPE_MY_THINGS:

                        showMyThings(Constants.ADAPTER_TYPE_LOCATION);

                        break;

                    case Constants.ADAPTER_TYPE_CHATS:

                        showChats(Constants.ADAPTER_TYPE_LOCATION);

                        break;

                    default:

                        showCategories(Constants.ADAPTER_TYPE_LOCATION);

                        break;
                }
                locationsAdapter = null;


                break;


            case Constants.ADAPTER_TYPE_CAMERA:


                Log.d("@@@", "MainActivityPresenter.onBackButtonPressed.ADAPTER_TYPE_CAMERA");

                getView().hideCamera(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        Log.d("@@@", "MainActivityPresenter.onBackButtonPressed.ADAPTER_TYPE_CAMERA.onAnimationEnd");


                        getView().getTextureView().setVisibility(View.GONE);

                        mViewModel.closeCamera();

                        switch (mViewModel.getState().getPreviousAdapterType()){

                            case Constants.ADAPTER_TYPE_CATEGORIES:

                                Log.d("@@@", "MainActivityPresenter.onBackButtonPressed.ADAPTER_TYPE_CAMERA.onAnimationEnd.case: ADAPTER_TYPE_CATEGORIES");

                                getView().animateMenuIcon(false);

                                mViewModel.setTitle(mViewModel.getUser().getLocation());

                                setCategoriesAdapter();

                                getView().getNavigationView().setSelectedItemId(R.id.bottomMenu_categories);

                                break;

                            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                                setFireBaseThingsAdapter();


                                if (navigationViewBehavior != null && navigationViewBehavior.isDown){
                                    navigationViewBehavior.slideUp();
                                }

                                break;
                            case Constants.ADAPTER_TYPE_MY_THINGS:

                                mViewModel.setTitle(
                                        getView().getActivityContext().getString(R.string.title_my_things));



                                setMyThingsAdapter();

                                getView().getNavigationView().setSelectedItemId(R.id.bottomMenu_myThings);


                                if (navigationViewBehavior != null && navigationViewBehavior.isDown){
                                    navigationViewBehavior.slideUp();
                                }


                                break;

                            case Constants.ADAPTER_TYPE_CHATS:

                                mViewModel.setTitle(getView().getActivityContext().getString(R.string.title_chats));

                                setChatsAdapter();

                                break;

                            case Constants.ADAPTER_TYPE_LOCATION:

                                setLocationsAdapter();

                            default:

                                Log.d("@@@", "MainActivityPresenter.onBackButtonPressed.ADAPTER_TYPE_CAMERA.onAnimationEnd.case: default");


                                showCategories(Constants.ADAPTER_TYPE_LOCATION);

                                break;
                        }


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                break;

        }

    }

    @Override
    public void detachView() {
        Log.d("@@@", "MainActivityPresenter.detachView");
        super.detachView();

        mViewModel.closeCamera();

//        if (mRepository.getMyThingsManager()!= null){
//
//            mRepository.getMyThingsManager().detachMyThingsChangeObserver();
//
//        }
    }

    public void detachListeners() {
//        if (mRepository.getFireBaseManager()!=null){
//            mRepository.getFireBaseManager().removeListeners();
//        }
    }

    public boolean isNewNameAdded(String name) {

        if (name.equals(mViewModel.getUser().getName())){
            return false;
        } else {
            getView()
                    .getSideBar()
                    .getMenu()
                    .findItem(R.id.side_bar_name)
                    .setTitle(name);

            mViewModel.changeUserName(name);

            return true;
        }
    }

    public void setUserImage(final Bitmap bitmap) {

        ImageView imageView = (ImageView) getView().getSideBar()
                .getHeaderView(0)
                .findViewById(R.id.side_bar_imageView);
        if (imageView != null){
            imageView.setImageBitmap(bitmap);
        }

        setHandler(new Handler(getLooper()));
        getHandler().post(new Runnable() {
            @Override
            public void run() {

                mViewModel.changeUserImage(bitmap);

            }
        });

    }


    public void saveNewImage(byte[] image) {
        Log.d("@@@", "MainActivityPresenter.saveNewImage");

        mViewModel.initNewThing(BitmapFactory.decodeByteArray(image, 0, image.length));

        new ImageUploader(
                image,
                mViewModel.getUser().getUid(),
                System.currentTimeMillis(),
                new ImageUploader.OnFinishListener() {
                    @Override
                    public void onFinish(String url) {
                        Log.d("@@@", "MainActivityPresenter.saveNewImage.onFinish");

                        if (url != null){

                            mViewModel.addNewThingImageUrl(url);


                            getView().getProgressBar().setVisibility(View.GONE);

                        } else {
                            Log.e("@@@", "MainActivityPresenter.saveNewImage.uploadingERROR !!!");

                        }

                    }
                })
                .send();

    }


}

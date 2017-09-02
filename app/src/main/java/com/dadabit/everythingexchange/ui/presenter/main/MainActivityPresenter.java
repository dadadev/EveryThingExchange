package com.dadabit.everythingexchange.ui.presenter.main;


import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.firebase.LoadThingsCallback;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.ui.adapter.CategoriesAdapter;
import com.dadabit.everythingexchange.ui.adapter.ChatsAdapter;
import com.dadabit.everythingexchange.ui.adapter.FireBaseThingsAdapter;
import com.dadabit.everythingexchange.ui.adapter.LocationsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.BottomNavigationViewBehavior;
import com.dadabit.everythingexchange.utils.ChatItemsManager;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.MyThingsManager;

import java.util.List;

import javax.inject.Inject;


public class MainActivityPresenter extends BasePresenter<MainActivityView>
        implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {


    @Inject Repository mRepository;


    private BottomNavigationViewBehavior navigationViewBehavior;

    private ChatsAdapter chatsAdapter;
    private MyThingsAdapter myThingsAdapter;
    private LocationsAdapter locationsAdapter;

    public MainActivityPresenter() {
        Log.d("@@@", "MainActivityPresenter.create");
        App.getComponent().inject(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.bottomMenu_categories:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_categories");

                if (mRepository.getState().getAdapterType() != Constants.ADAPTER_TYPE_CATEGORIES){
                    Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_categories. show");

                    showCategories(mRepository.getState().getAdapterType());

                }



                break;
            case R.id.bottomMenu_myThings:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_myThings");

                if (mRepository.getState().getAdapterType() != Constants.ADAPTER_TYPE_MY_THINGS){

                    showMyThings(mRepository.getState().getAdapterType());

                }

                break;
            case R.id.bottomMenu_addNewThing:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: bottomMenu_addNew");
                getView().startAddThingActivity();
                break;
            case R.id.side_bar_name:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_name");

                getView().getDrawerLayout().closeDrawer(GravityCompat.START);

                getUiHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()){
                            getView().showUserInfoDialog(
                                    mRepository.getUser().getName(),
                                    mRepository.getUser().getImgUrl());
                        }
                    }
                }, 300);


                break;
            case R.id.side_bar_location:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_location");

                getView().getDrawerLayout().closeDrawer(GravityCompat.START);
                showLocations(mRepository.getState().getAdapterType());

                break;
            case R.id.side_bar_sign_out:
                Log.d("@@@", "MainActivityPresenter.onNavigationItemSelected: side_bar_sign_out");
                getView().getDrawerLayout().closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }


    private CategoriesAdapter.CategoriesClickCallback categoriesClickCallback = new CategoriesAdapter.CategoriesClickCallback() {
        @Override
        public void onClick(int position) {
            Log.d("@@@", "MainActivityPresenter.CategoriesAdapter.onClick: "+mRepository.getCategories().get(position).getName());

            mRepository.getState().setChosenCategory(position);

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

            mRepository.getState().setChosenFireBaseThing(position);

            getView().startSingleThingActivity(position);
        }
    };

    private MyThingsAdapter.ClickListener myThingsClickCallback = new MyThingsAdapter.ClickListener() {
        @Override
        public void onClick(int position, int type, String thingId) {
            Log.d("@@@", "MainActivityPresenter.MyThingsClickCallback: "+position);

            if (type == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

                mRepository.getState().setChosenChatId(mRepository.getExchangeIdByThing(thingId));

                getView().startSingleChat(position, Constants.ADAPTER_TYPE_MY_THINGS);


            } else {

                mRepository.getState().setChosenMyThing(position);

                getView().startOffersActivity(position);
            }
        }
    };

    private ChatsAdapter.ChatsClickCallback chatsClickCallback = new ChatsAdapter.ChatsClickCallback() {
        @Override
        public void onClick(int position, int id) {
            Log.d("@@@", "ChatsActivityPresenter.ChatsClickCallback.onClick: "+position +" id:" +id);

            mRepository.getState().setChosenChatId(id);
            getView().startSingleChat(position, Constants.ADAPTER_TYPE_CHATS);
        }
    };

    private LocationsAdapter.ClickListener locationsAdapterClickCallback =
            new LocationsAdapter.ClickListener() {
                @Override
                public void onLocationClick(String location) {
                    Log.d("@@@", "MainActivityPresenter.locationsAdapterClickCallback: "+location);


                    mRepository.getUser().setLocation(location);

                    onBackButtonPressed();

                }

                @Override
                public void onHomeLocationChanged(String location) {
                    Log.d("@@@", "MainActivityPresenter.onHomeLocationChanged: "+location);

                    mRepository.getSharedPreferences().setHomeLocation(location);

                    getView().getSideBar()
                            .getMenu()
                            .findItem(R.id.side_bar_location)
                            .setTitle(location);

                    getView().showToast("Home location changed to: "+location);


                }
            };




    private MyThingsManager.MyThingsChangeObserver myThingsChangeObserver = new MyThingsManager.MyThingsChangeObserver() {
        @Override
        public void onChange(final int position) {
            Log.d("@@@", "MainActivityPresenter.MyThingsAdapter.onChange: "+ position);

            if (isViewAttached()
                    && myThingsAdapter != null){


                getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        if (position == -1){

                            myThingsAdapter.notifyDataSetChanged();
                        } else {

                            myThingsAdapter.notifyItemChanged(position);
                        }
                    }
                });


            }
        }
    };


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


    @Override
    public void attachView(MainActivityView mainActivityView) {
        super.attachView(mainActivityView);
        Log.d("@@@", "MainActivityPresenter.attachView");

        if ( mRepository.getUser() == null ) {

            getView().startAuthActivity();

        } else {

            initToolbar();

            initSideBar();

            initNavigationView();

            setRecyclerAdapter(mRepository.getState().getAdapterType());

        }
    }



    private void initToolbar() {
        Log.d("@@@", "MainActivityPresenter.init.start");

        switch (mRepository.getState().getAdapterType()){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().animateTitleChange(
                        mRepository.getUser().getLocation());

                getView().getAppBarLayout().setExpanded(false,false);

                break;

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateMenuIcon(true);

                getView().animateTitleChange(
                        mRepository
                                .getCategories()
                                .get(mRepository.getState().getChosenCategory()).getName());

                getView().getCollapsingBackground()
                        .setImageBitmap(
                                mRepository
                                        .getBackgroundImages()
                                        .get(mRepository.getState().getChosenCategory()));

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                getView().animateTitleChange(getView().getActivityContext().getString(R.string.title_my_things));
                getView().animateMenuIcon(true);

                break;

            case Constants.ADAPTER_TYPE_CHATS:

                getView().animateTitleChange(getView().getActivityContext().getString(R.string.title_chats));
                getView().animateMenuIcon(true);

                break;

            case Constants.ADAPTER_TYPE_LOCATION:

                getView().animateTitleChange(getView().getActivityContext().getString(R.string.title_locations));
                getView().animateMenuIcon(true);

                break;
        }

        getView().getChatsButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChats(mRepository.getState().getAdapterType());
                    }
                });

        getView().getToolbarTitle().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mRepository.getState().getAdapterType()
                                == Constants.ADAPTER_TYPE_CATEGORIES){
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
                .load(mRepository.getUser().getImgUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) navHeader.findViewById(R.id.side_bar_imageView));

        menu.findItem(R.id.side_bar_name).setTitle(
                mRepository.getUser().getName());
        menu.findItem(R.id.side_bar_location).setTitle(
                mRepository.getSharedPreferences().getHomeLocation());


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


        if (mRepository.getState().getBottomNavigation() == Constants.IS_HIDDEN
                && mRepository.getState().getAdapterType() == Constants.ADAPTER_TYPE_FIREBASE_THINGS){

            getView().getNavigationView().setVisibility(View.INVISIBLE);

        }
    }



    private void showCategories(int previousAdapterType) {

        getView().animateMenuIcon(false);

        mRepository.getState().setAppBarLayout(Constants.IS_HIDDEN);

        getView().animateTitleChange(mRepository.getUser().getLocation());

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
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

        if (mRepository.getState().getAdapterType() == Constants.ADAPTER_TYPE_CATEGORIES){

            getView().getNavigationView().setSelectedItemId(R.id.bottomMenu_categories);


        }


    }

    private void showMyThings(int previousAdapterType) {


        getView().animateTitleChange(
                getView().getActivityContext().getString(R.string.title_my_things));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:


                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_LEFT,
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
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

//                                setMyThingsAdapter();
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

        }


    }

    private void showChats(int previousAdapterType) {

        getView().animateTitleChange(getView().getActivityContext().getString(R.string.title_chats));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

//                                setChatsAdapter();
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


        getView().animateTitleChange(getView().getActivityContext().getString(R.string.title_locations));

        switch (previousAdapterType){

            case Constants.ADAPTER_TYPE_CATEGORIES:

                getView().animateMenuIcon(true);

                getView().animateRecyclerOut(
                        Constants.DIRECTION_DOWN,
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
        }
    }


    private void setFireBaseThingsAdapter() {

        mRepository.getState().setAdapterType(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

        getView().animateTitleChange(
                mRepository.getCategories()
                        .get(mRepository.getState().getChosenCategory()).getName());

        getView().getCollapsingBackground()
                .setImageBitmap(mRepository.getBackgroundImages()
                        .get(mRepository.getState().getChosenCategory()));


        getView().getCollapsingBackground().setVisibility(View.VISIBLE);

        getView().getProgressBar().setVisibility(View.VISIBLE);

        mRepository.loadFireBaseThings(
                new LoadThingsCallback() {
                    @Override
                    public void onLoaded(final boolean isDataExist) {

                        if (isViewAttached() &&
                                mRepository.getState().getAdapterType() ==
                                        Constants.ADAPTER_TYPE_FIREBASE_THINGS){

                            getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {

                                    getView().getProgressBar().setVisibility(View.GONE);

                                    if (isDataExist){

                                        getView().getRecyclerView().setLayoutManager(
                                                new LinearLayoutManager(
                                                        getView().getActivityContext(),
                                                        LinearLayoutManager.VERTICAL,false));

                                        getView().getRecyclerView().setAdapter(
                                                new FireBaseThingsAdapter(
                                                        getView().getActivityContext(),
                                                        mRepository.getFireBaseItems(),
                                                        fireBaseThingsClickCallback));

                                        getView().animateRecyclerIn(
                                                Constants.DIRECTION_DOWN,null);

                                        getView().getAppBarLayout()
                                                .setExpanded(true, true);

                                        mRepository.getState()
                                                .setAppBarLayout(Constants.IS_SHOWN);
                                    } else {

                                        getView().showToast("LOADING ERROR");
                                    }
                                }
                            });
                        }
                    }
                });


    }

    private void setCategoriesAdapter() {

        mRepository.getState().setAdapterType(Constants.ADAPTER_TYPE_CATEGORIES);

        getView().getAppBarLayout().setExpanded(false, true);

        getView().getRecyclerView().setVisibility(View.VISIBLE);

        getView().getRecyclerView().setLayoutManager(
                new GridLayoutManager(getView().getActivityContext(), 2));

        getView().getRecyclerView().setAdapter(new CategoriesAdapter(
                getView().getActivityContext(),
                mRepository.getCategories(),
                categoriesClickCallback));

        if (navigationViewBehavior != null && navigationViewBehavior.isDown){
            navigationViewBehavior.slideUp();
        }


        getView().getCollapsingBackground().setVisibility(View.GONE);

    }

    private void setMyThingsAdapter() {

        mRepository.getState().setAdapterType(Constants.ADAPTER_TYPE_MY_THINGS);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(
                        getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL,false));

        getView().getRecyclerView().setAdapter(
                myThingsAdapter = new MyThingsAdapter(
                        mRepository.getMyThingsManager().getMyThingsAdapterItems(),
                        getView().getAppContext(),
                        myThingsClickCallback));

        mRepository.getMyThingsManager().attachMyThingsChangeObserver(myThingsChangeObserver);

        getView().animateRecyclerIn(Constants.DIRECTION_LEFT, null);
        getView().getAppBarLayout().setExpanded(false, true);


        getView().getCollapsingBackground().setVisibility(View.GONE);

    }

    private void setChatsAdapter() {

        mRepository.getState().setAdapterType(Constants.ADAPTER_TYPE_CHATS);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL, false));

        getView().getRecyclerView().setAdapter(
                chatsAdapter = new ChatsAdapter(
                        getView().getAppContext(),
                        mRepository.getChatItemsManager().getChatItems(),
                        chatsClickCallback));

        getView().animateRecyclerIn(Constants.DIRECTION_DOWN, null);
        getView().getAppBarLayout().setExpanded(false, true);

        mRepository.getChatItemsManager().attachChatsAdapterListener(chatsAdapterChangeListener);


        getView().getCollapsingBackground().setVisibility(View.GONE);

    }

    private void setLocationsAdapter() {

        mRepository.getState().setAdapterType(Constants.ADAPTER_TYPE_LOCATION);

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

        mRepository.loadLocations(new Repository.LocationsLoaderCallback() {
            @Override
            public void onLocationsLoad(final List<String> locations, final int homePosition) {
                Log.d("@@@", "MainActivityPresenter.LocationsLoaderCallback.onLocationsLoad");

                if (isViewAttached() && locationsAdapter != null){

                    getUiHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            getView().getProgressBar().setVisibility(View.GONE);

                            locationsAdapter.setLocations(locations, homePosition);
                            getView().animateRecyclerIn(Constants.DIRECTION_DOWN, null);

                        }
                    });
                }
            }
        });

    }







    public void onActivityResumed(){
        Log.d("@@@", "MainActivityPresenter.onActivityResumed");

        mRepository.getChatItemsManager().attachNewMessagesListener(
                new ChatItemsManager.MessagesCounterListener() {
                    @Override
                    public void onCounterChange(final int counter) {

                        if (isViewAttached()){

                            getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (counter > 0){
                                        getView().getChatsCountTextView().setVisibility(View.VISIBLE);
                                        getView().getChatsCountTextView().setText(String.valueOf(counter));
                                    } else {
                                        getView().getChatsCountTextView().setVisibility(View.GONE);
                                    }

                                }
                            });

                        }
                    }
                }
        );


        if (chatsAdapter != null){
            mRepository.getChatItemsManager().attachChatsAdapterListener(chatsAdapterChangeListener);
        }

        if (myThingsAdapter != null){

            mRepository.getMyThingsManager().attachMyThingsChangeObserver(myThingsChangeObserver);

        } else if (mRepository.getState().isNewThingAdd()){

            mRepository.getState().setNewThingAdd(false);

            showMyThings(mRepository.getState().getAdapterType());
        }

    }

    public void onActivityPaused(){

        mRepository.getChatItemsManager().detachNewMessagesListener();
        mRepository.getChatItemsManager().detachChatsAdapterListener();

    }

    public Boolean exit = false;
    public void onBackButtonPressed(){
        Log.d("@@@", "MainActivityPresenter.onBackButtonPressed");

        switch (mRepository.getState().getAdapterType()){

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

                mRepository.getState().getPreviousAdapterType();

                showCategories(Constants.ADAPTER_TYPE_FIREBASE_THINGS);

                break;

            case Constants.ADAPTER_TYPE_MY_THINGS:

                switch (mRepository.getState().getPreviousAdapterType()){

                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_RIGHT,
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

                switch (mRepository.getState().getPreviousAdapterType()){
                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_DOWN,
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
                mRepository.getChatItemsManager().detachChatsAdapterListener();
                chatsAdapter = null;
                break;

            case Constants.ADAPTER_TYPE_LOCATION:


                switch (mRepository.getState().getPreviousAdapterType()){
                    case Constants.ADAPTER_TYPE_FIREBASE_THINGS:

                        getView().animateRecyclerOut(
                                Constants.DIRECTION_DOWN,
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

        }

    }

    @Override
    public void detachView() {
        Log.d("@@@", "MainActivityPresenter.detachView");
        super.detachView();
        if (mRepository.getMyThingsManager()!= null){

            mRepository.getMyThingsManager().detachMyThingsChangeObserver();

        }
    }

    public void detachListeners() {
        if (mRepository.getFireBaseManager()!=null){
            mRepository.getFireBaseManager().removeListeners();
        }
    }

    public boolean isNewName(String name) {

        if (name.equals(mRepository.getUser().getName())){
            return false;
        } else {
            getView()
                    .getSideBar()
                    .getMenu()
                    .findItem(R.id.side_bar_name)
                    .setTitle(name);

            mRepository.changeUserName(name);

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

                mRepository.changeUserImage(bitmap);

            }
        });

    }
}

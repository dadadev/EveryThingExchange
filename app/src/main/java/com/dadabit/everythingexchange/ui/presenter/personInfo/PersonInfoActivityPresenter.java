package com.dadabit.everythingexchange.ui.presenter.personInfo;


import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsIconsAdapter;
import com.dadabit.everythingexchange.ui.adapter.PersonThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.ui.viewmodel.PersonInfoActivityViewModel;
import com.dadabit.everythingexchange.utils.Dialogs;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.List;

public class PersonInfoActivityPresenter extends BasePresenter<PersonInfoActivityView> implements View.OnClickListener{

    private PersonThingsAdapter personThingsAdapter;
    private MyThingsIconsAdapter myThingsAdapter;

    private PersonInfoActivityViewModel mViewModel;

    private String uid;


    public PersonInfoActivityPresenter(String uid) {
        Log.d("@@@", "PersonInfoActivityPresenter.onCreate");

        this.uid = uid;

    }


    @Override
    public void attachView(PersonInfoActivityView personInfoActivityView) {
        Log.d("@@@", "PersonInfoActivityPresenter.attachView");
        super.attachView(personInfoActivityView);

        if (uid != null){

            getView().getAppBarLayout()
                    .setExpanded(false, false);

            mViewModel = getView().getViewModel();

            observeData();

            setClickListeners();

        } else {

            exit = true;

            getView().goBack();

        }

    }

    private void observeData() {

        mViewModel
                .getPersonInfo(uid)
                .observe(
                        getView().getLifecycleOwner(),
                        new Observer<User>() {

                            @Override
                            public void onChanged(@Nullable User user) {
                                if (user != null){

                                    Log.d("@@@", "PersonInfoActivityPresenter.observeData.getPersonInfo: "+user.getName());

                                    showData(user);

                                } else {

                                    Log.d("@@@", "PersonInfoActivityPresenter.observeData.getPersonInfo: USERisNULL");

                                }
                            }
                        });
    }

    private void showData(User fireBaseUser) {

        Glide.with(getView().getActivityContext())
                .load(fireBaseUser.getImgUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getPersonImageView());


        Glide.with(getView().getActivityContext())
                .load(fireBaseUser.getImgUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getPersonSmallImageView());

        getView().getTitleTextView().setText(
                fireBaseUser.getName());


        switch (mViewModel.getState()){

            case PersonInfoActivityViewModel.STATE_PERSON_THINGS:

                loadPersonFireBaseThings();

                break;

            case PersonInfoActivityViewModel.STATE_SINGLE_THING:

                showSingleThing();

                break;

            case PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN:

                showSingleThing();

                showMyThings();

                break;

            case PersonInfoActivityViewModel.STATE_MY_THING_CHOSEN:


                if (mViewModel.getMyChosenThingPosition() != -1){

                    getView().getIvThing2().setImageBitmap(
                            mViewModel.getMyAvailableThings()
                                    .get(mViewModel.getMyChosenThingPosition())
                                    .getImgBitmap());

                    showSingleThing();

                    getView().showMyThingIcon();

                }


                break;

        }


        setClickListeners();


    }



    private PersonThingsAdapter.ClickCallback onThingClickListener =
            new PersonThingsAdapter.ClickCallback() {
                @Override
                public void onClick(final int position) {
                    getView().animateThingsOut(
                            position,
                            new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    mViewModel.setState(PersonInfoActivityViewModel.STATE_SINGLE_THING);

                                    mViewModel.setChosenThing(position);

                                    getView().getRecyclerView().setVisibility(View.GONE);


                                    showSingleThing();


                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                }
            };


    private MyThingsIconsAdapter.ClickListener myOfferClickListener =
            new MyThingsIconsAdapter.ClickListener() {
                @Override
                public void onClick(int position) {

                    if (position == mViewModel.getMyAvailableThings().size()){

                        getView().animateMyThingsOut();

                        mViewModel.setState(PersonInfoActivityViewModel.STATE_WAITING_NEW_THING);

                        getView().startAddThingActivity();

                    } else {

                        mViewModel.setState(PersonInfoActivityViewModel.STATE_MY_THING_CHOSEN);

                        mViewModel.setMyChosenThingPosition(position);

                        showMyThingChosen();

                    }

                }
            };


//    private void showContent() {
//
//        mViewModel.loadFireBaseUser(new LoadFireBaseUserCallback() {
//            @Override
//            public void onLoaded(User fireBaseUser) {
//                Log.d("@@@", "PersonInfoActivityPresenter.showContent.onLoaded");
//
//                if (isViewAttached()){
//
//                    if (fireBaseUser != null){
//
//                        Glide.with(getView().getActivityContext())
//                                .load(fireBaseUser.getImgUrl())
//                                .thumbnail(0.5f)
//                                .crossFade()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(getView().getPersonImageView());
//
//
//                        Glide.with(getView().getActivityContext())
//                                .load(fireBaseUser.getImgUrl())
//                                .thumbnail(0.5f)
//                                .crossFade()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(getView().getPersonSmallImageView());
//
//                        getView().getTitleTextView().setText(
//                                fireBaseUser.getName());
//
//
//                        switch (mRepository.getState()){
//
//                            case PersonInfoActivityRepo.STATE_PERSON_THINGS:
//
//                                loadPersonFireBaseThings();
//
//                                break;
//
//                            case PersonInfoActivityRepo.STATE_SINGLE_THING:
//
//                                showSingleThing();
//
//                                break;
//
//                            case PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN:
//
//                                showSingleThing();
//
//                                showMyThings();
//
//                                break;
//
//                            case PersonInfoActivityRepo.STATE_MY_THING_CHOSEN:
//
//
//                                if (mRepository.getMyChosenThingPosition() != -1){
//
//                                    getView().getIvThing2().setImageBitmap(
//                                            mRepository.getMyAvailableThings()
//                                                    .get(mRepository.getMyChosenThingPosition())
//                                                    .getImgBitmap());
//
//                                    showSingleThing();
//
//                                    getView().showMyThingIcon();
//
//                                }
//
//
//                                break;
//
//                        }
//
//
//                        setClickListeners();
//
//                    } else {
//                        exit = true;
//                        getView().goBack();
//                    }
//
//
//                }
//            }
//        });
//    }

    private void setClickListeners() {


        getView().getFab().setOnClickListener(this);
        getView().getIvThing1().setOnClickListener(this);
        getView().getIvThing2().setOnClickListener(this);

    }


    private void loadPersonFireBaseThings() {

        getView().getProgressBar().setVisibility(View.VISIBLE);

        mViewModel.getThings().observe(
                getView().getLifecycleOwner(),
                new Observer<List<FireBaseThingItem>>() {
                    @Override
                    public void onChanged(@Nullable List<FireBaseThingItem> fireBaseThingItems) {

                        if (fireBaseThingItems != null){

                            getView().getProgressBar().setVisibility(View.GONE);

                            getView().getAppBarLayout().setExpanded(true, true);


                            if (getView().getRecyclerView().getAdapter() == null){
                                prepareThingsAdapter();
                            }

                            personThingsAdapter.setThings(fireBaseThingItems);
                        }


                    }
                }
        );


    }

    private void showSingleThing() {

        if (mViewModel.getChosenThing() != null){

            Glide.with(getView().getActivityContext())
                    .load(mViewModel.getChosenThing().getItemImgLink())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(getView().getIvThing1());

            getView().getTvLocation()
                    .setText(mViewModel.getChosenThing().getItemArea());

            getView().getTvThingDate()
                    .setText(Utils.timestampToString(mViewModel.getChosenThing().getDate()));

            getView().getTvThingName()
                    .setText(mViewModel.getChosenThing().getItemName());

            getView().getTvCategory()
                    .setText(mViewModel.getCategoryName(
                            mViewModel.getChosenThing().getItemCategory()));

            getView().getTvThingDescription()
                    .setText(mViewModel.getChosenThing().getItemDescription());


            if (mViewModel.getChosenThing().getHashTags() != null){


                getView().getHashTagsRecyclerView().setLayoutManager(
                        new LinearLayoutManager(
                                getView().getActivityContext(),
                                LinearLayoutManager.HORIZONTAL,false));

                getView().getHashTagsRecyclerView()
                        .setAdapter(
                                new HashTagsAdapter(mViewModel.getChosenThing().getHashTags()));
            }


            getView().getSingeThingCard().startAnimation(
                    AnimationUtils.loadAnimation(
                            getView().getActivityContext(),
                            R.anim.slide_in_top));

            getView().getSingeThingCard().setVisibility(View.VISIBLE);

            getView().getAppBarLayout().setExpanded(false, true);

            getView().getFab().show();

        }
    }




    private void showMyThings() {

        mViewModel.setState(PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN);

        if (getView().getMyThingsRecyclerView().getAdapter() == null){

            getView().getMyThingsRecyclerView().setLayoutManager(
                    new LinearLayoutManager(getView().getActivityContext(), LinearLayoutManager.HORIZONTAL,false));

            getView().getMyThingsRecyclerView().setAdapter(
                    myThingsAdapter = new MyThingsIconsAdapter(
                            mViewModel.getMyAvailableThings(),
                            ContextCompat.getDrawable(
                                    getView().getActivityContext(),
                                    R.drawable.ic_add_black_24dp),
                            myOfferClickListener));
        }

        getView().animateMyThingsIn();

        getView().getAppBarLayout().setExpanded(false, true);
    }


    private void showMyThingChosen() {


        getView().getIvThing2().setImageBitmap(
                mViewModel.getMyAvailableThings()
                        .get(mViewModel.getMyChosenThingPosition())
                        .getImgBitmap());

        getView().animateMyThingChosen();



    }

    public boolean exit = false;
    public void onBackPressed() {

        switch (mViewModel.getState()){

            case PersonInfoActivityViewModel.STATE_PERSON_THINGS:

                exit = true;
                getView().goBack();

                break;

            case PersonInfoActivityViewModel.STATE_SINGLE_THING:


                getView().animateSingleThingOut(
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mViewModel.removeChosenThing();

                                mViewModel.setState(PersonInfoActivityViewModel.STATE_PERSON_THINGS);

                                getView().getFab().hide();

                                if (getView().getRecyclerView().getAdapter() == null){
                                    loadPersonFireBaseThings();
                                } else {
                                    getView().animateThingsIn();
                                }


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN:

                mViewModel.setState(PersonInfoActivityViewModel.STATE_SINGLE_THING);

                getView().animateMyThingsOut();

                break;

            case PersonInfoActivityViewModel.STATE_MY_THING_CHOSEN:

                mViewModel.setState(PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN);

                mViewModel.setMyChosenThingPosition(-1);

                getView().animateHideMyThingChosen();

                showMyThings();


                break;

        }
    }


    private void prepareThingsAdapter() {

        getView().getRecyclerView().setVisibility(View.VISIBLE);

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL, false));

        getView().getRecyclerView().setAdapter(
                personThingsAdapter = new PersonThingsAdapter(
                        getView().getActivityContext(),
                        onThingClickListener));


    }

    @Override
    public void detachView() {
        super.detachView();
    }


    @Override
    public void onClick(View v) {
        Log.d("@@@", "PersonInfoActivityPresenter.onClick");

        switch (v.getId()){

            case R.id.person_info_fab:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab");


                switch (mViewModel.getState()){
                    case PersonInfoActivityViewModel.STATE_MY_THING_CHOSEN:
                        Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab.sendOffer");

                        mViewModel.sendOffer();

                        exit = true;
                        getView().goBack();


                        break;

                    case PersonInfoActivityViewModel.STATE_SINGLE_THING:
                        Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab.showMyThings");

                        showMyThings();

                        break;
                }


                break;

            case R.id.person_info_thingCardView_thing1_imageView:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_thingCardView_thing1_imageView");

                if (mViewModel.getChosenThing() != null){

                    Dialogs.getUrlImageDialog(
                            getView().getActivityContext(),
                            mViewModel.getChosenThing().getItemImgLink())
                            .show();
                }
                break;

            case R.id.person_info_thingCardView_thing2_imageView:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_thingCardView_thing2_imageView");

                mViewModel.setState(PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN);

                getView().animateHideMyThingChosen();

                showMyThings();

                break;


        }

    }

    public void onActivityResumed() {


        if (mViewModel.getState() == PersonInfoActivityViewModel.STATE_WAITING_NEW_THING
                && myThingsAdapter != null){

            mViewModel.updateAvailableThings();

            myThingsAdapter.setThings(mViewModel.getMyAvailableThings());

            mViewModel.setState(PersonInfoActivityViewModel.STATE_MY_THINGS_SHOWN);

            showMyThings();


        }

    }
}

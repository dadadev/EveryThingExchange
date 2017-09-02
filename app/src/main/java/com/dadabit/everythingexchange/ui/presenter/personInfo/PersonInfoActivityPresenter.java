package com.dadabit.everythingexchange.ui.presenter.personInfo;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.PersonInfoActivityRepo;
import com.dadabit.everythingexchange.model.firebase.LoadFireBaseUserCallback;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsIconsAdapter;
import com.dadabit.everythingexchange.ui.adapter.PersonThingsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.Dialogs;
import com.dadabit.everythingexchange.utils.Utils;

public class PersonInfoActivityPresenter extends BasePresenter<PersonInfoActivityView> implements View.OnClickListener{

    private PersonInfoActivityRepo mRepository;
    private PersonThingsAdapter personThingsAdapter;
    private MyThingsIconsAdapter myThingsAdapter;


    public PersonInfoActivityPresenter(String uid) {
        Log.d("@@@", "PersonInfoActivityPresenter.onCreate");
        if (uid!= null){
            mRepository = new PersonInfoActivityRepo(uid);
        }
    }


    @Override
    public void attachView(PersonInfoActivityView personInfoActivityView) {
        Log.d("@@@", "PersonInfoActivityPresenter.attachView");
        super.attachView(personInfoActivityView);

        if (mRepository!= null){


            getView().getAppBarLayout().setExpanded(false, false);

            showContent();

            setClickListeners();

        } else {
            exit = true;
            getView().goBack();
        }
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

                                    mRepository.setChosenThing(position);

                                    getView().getRecyclerView().setVisibility(View.GONE);

                                    mRepository.setState(PersonInfoActivityRepo.STATE_SINGLE_THING);

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

                    if (position == mRepository.getMyAvailableThings().size()){

                        getView().animateMyThingsOut();

                        mRepository.setState(PersonInfoActivityRepo.STATE_WAITING_NEW_THING);

                        getView().startAddThingActivity();

                    } else {

                        mRepository.setState(PersonInfoActivityRepo.STATE_MY_THING_CHOSEN);

                        mRepository.setMyChosenThingPosition(position);

                        showMyThingChosen();

                    }

                }
            };


    private void showContent() {

        mRepository.loadFireBaseUser(new LoadFireBaseUserCallback() {
            @Override
            public void onLoaded(User fireBaseUser) {
                Log.d("@@@", "PersonInfoActivityPresenter.showContent.onLoaded");

                if (isViewAttached()){

                    if (fireBaseUser != null){

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


                        switch (mRepository.getState()){

                            case PersonInfoActivityRepo.STATE_PERSON_THINGS:

                                loadFireBaseData();

                                break;

                            case PersonInfoActivityRepo.STATE_SINGLE_THING:

                                showSingleThing();

                                break;

                            case PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN:

                                showSingleThing();

                                showMyThings();

                                break;

                            case PersonInfoActivityRepo.STATE_MY_THING_CHOSEN:


                                if (mRepository.getMyChosenThingPosition() != -1){

                                    getView().getIvThing2().setImageBitmap(
                                            mRepository.getMyAvailableThings()
                                                    .get(mRepository.getMyChosenThingPosition())
                                                    .getImgBitmap());

                                    showSingleThing();

                                    getView().showMyThingIcon();

                                }


                                break;

                        }


                        setClickListeners();

                    } else {
                        exit = true;
                        getView().goBack();
                    }


                }
            }
        });
    }

    private void setClickListeners() {


        getView().getFab().setOnClickListener(this);
        getView().getIvThing1().setOnClickListener(this);
        getView().getIvThing2().setOnClickListener(this);

    }


    private void loadFireBaseData() {

        getView().getProgressBar().setVisibility(View.VISIBLE);

        mRepository.attachNewThingCallback(new PersonThingsObserver() {
            @Override
            public void onThingLoaded(FireBaseThingItem thing) {

                if (isViewAttached()){

                    getView().getProgressBar().setVisibility(View.GONE);


                    getView().getAppBarLayout().setExpanded(true, true);

                    if (getView().getRecyclerView().getAdapter() == null){
                        prepareThingsAdapter();
                    }

                    personThingsAdapter.addNewThing(thing);
                }
            }
        });

    }

    private void showSingleThing() {

        if (mRepository.getChosenThing() > -1){

            Glide.with(getView().getActivityContext())
                    .load(mRepository.getThings().get(mRepository.getChosenThing()).getItemImgLink())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(getView().getIvThing1());

            getView().getTvLocation()
                    .setText(mRepository.getThings()
                            .get(mRepository.getChosenThing()).getItemArea());

            getView().getTvThingDate()
                    .setText(Utils.timestampToString(mRepository.getThings()
                            .get(mRepository.getChosenThing()).getDate()));

            getView().getTvThingName()
                    .setText(mRepository.getThings()
                            .get(mRepository.getChosenThing()).getItemName());

            getView().getTvCategory()
                    .setText(mRepository.getCategoryName(
                            mRepository.getThings()
                                    .get(mRepository.getChosenThing()).getItemCategory()));

            getView().getTvThingDescription()
                    .setText(mRepository.getThings()
                            .get(mRepository.getChosenThing()).getItemDescription());


            if (mRepository.getThings().get(mRepository.getChosenThing()).getHashTags() != null){


                getView().getHashTagsRecyclerView().setLayoutManager(
                        new LinearLayoutManager(
                                getView().getActivityContext(),
                                LinearLayoutManager.HORIZONTAL,false));

                getView().getHashTagsRecyclerView()
                        .setAdapter(
                                new HashTagsAdapter(
                                        mRepository.getThings()
                                                .get(mRepository.getChosenThing()).getHashTags()));

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

        mRepository.setState(PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN);

        if (getView().getMyThingsRecyclerView().getAdapter() == null){


            getView().getMyThingsRecyclerView().setLayoutManager(
                    new LinearLayoutManager(getView().getActivityContext(), LinearLayoutManager.HORIZONTAL,false));

            getView().getMyThingsRecyclerView().setAdapter(
                    myThingsAdapter = new MyThingsIconsAdapter(
                            mRepository.getMyAvailableThings(),
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
                mRepository.getMyAvailableThings()
                        .get(mRepository.getMyChosenThingPosition())
                        .getImgBitmap());

        getView().animateMyThingChosen();



    }

    public boolean exit = false;
    public void onBackPressed() {

        switch (mRepository.getState()){

            case PersonInfoActivityRepo.STATE_PERSON_THINGS:

                exit = true;
                getView().goBack();

                break;

            case PersonInfoActivityRepo.STATE_SINGLE_THING:


                getView().animateSingleThingOut(
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mRepository.setChosenThing(-1);

                                mRepository.setState(PersonInfoActivityRepo.STATE_PERSON_THINGS);

                                getView().getFab().hide();

                                if (getView().getRecyclerView().getAdapter() == null){
                                    loadFireBaseData();
                                } else {
                                    getView().animateThingsIn();
                                }


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                break;

            case PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN:

                mRepository.setState(PersonInfoActivityRepo.STATE_SINGLE_THING);

                getView().animateMyThingsOut();

                break;

            case PersonInfoActivityRepo.STATE_MY_THING_CHOSEN:

                mRepository.setState(PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN);

                mRepository.setMyChosenThingPosition(-1);

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
        mRepository.detachNewThingCallback();
    }

    public void detachRepo() {
        Log.d("@@@", "PersonInfoActivityPresenter.detachRepo");
        mRepository = null;
    }

    @Override
    public void onClick(View v) {
        Log.d("@@@", "PersonInfoActivityPresenter.onClick");

        switch (v.getId()){

            case R.id.person_info_fab:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab");


                switch (mRepository.getState()){
                    case PersonInfoActivityRepo.STATE_MY_THING_CHOSEN:
                        Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab.sendOffer");

                        mRepository.sendOffer();

                        exit = true;
                        getView().goBack();


                        break;

                    case PersonInfoActivityRepo.STATE_SINGLE_THING:
                        Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_fab.showMyThings");

                        showMyThings();


                        break;
                }


                break;

            case R.id.person_info_thingCardView_thing1_imageView:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_thingCardView_thing1_imageView");

                if (mRepository.getChosenThing() > -1){

                    Dialogs.getUrlImageDialog(
                            getView().getActivityContext(),
                            mRepository.getThings()
                                    .get(mRepository.getChosenThing())
                                    .getItemImgLink())
                            .show();

                }
                break;

            case R.id.person_info_thingCardView_thing2_imageView:
                Log.d("@@@", "PersonInfoActivityPresenter.onClick.person_info_thingCardView_thing2_imageView");

                mRepository.setState(PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN);

                getView().animateHideMyThingChosen();

                showMyThings();

                break;


        }

    }

    public void onActivityResumed() {


        if (mRepository.getState() == PersonInfoActivityRepo.STATE_WAITING_NEW_THING
                && myThingsAdapter != null){


            mRepository.updateAvailableThings();

            myThingsAdapter.setThings(mRepository.getMyAvailableThings());

            mRepository.setState(PersonInfoActivityRepo.STATE_MY_THINGS_SHOWN);

            showMyThings();


        }

    }
}

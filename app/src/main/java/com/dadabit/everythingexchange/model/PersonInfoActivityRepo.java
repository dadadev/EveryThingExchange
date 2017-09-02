package com.dadabit.everythingexchange.model;


import android.util.Log;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.firebase.LoadFireBaseUserCallback;
import com.dadabit.everythingexchange.model.vo.FireBaseOfferItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.personInfo.PersonThingsObserver;
import com.dadabit.everythingexchange.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PersonInfoActivityRepo {

    public static final int STATE_PERSON_THINGS = 0;
    public static final int STATE_SINGLE_THING = 1;
    public static final int STATE_MY_THINGS_SHOWN = 2;
    public static final int STATE_MY_THING_CHOSEN = 3;
    public static final int STATE_WAITING_NEW_THING = 4;


    @Inject Repository mainRepository;

    private String userUid;

    private User personInfo;

    private List<FireBaseThingItem> things;
    private List<ThingEntity> availableThings;


    private int chosenThing = -1;
    private int chosenMyThing;

    private int state;

    private PersonThingsObserver mCallback;

    public PersonInfoActivityRepo(String uid) {
        Log.d("@@@", "PersonInfoActivityRepo.onCreate");
        userUid = uid;
        App.getComponent().inject(this);
    }

    public void loadFireBaseUser(final LoadFireBaseUserCallback callback){

        if (personInfo == null){

            mainRepository.getFireBaseManager()
                    .loadUserInfo(
                            String.format("users/%s/metadata", userUid),
                            new LoadFireBaseUserCallback() {
                                @Override
                                public void onLoaded(User fireBaseUser) {

                                    callback.onLoaded(personInfo = fireBaseUser);

                                }
                            });

        } else {
            callback.onLoaded(personInfo);
        }

    }

    private void loadPersonThings(){

        things = new ArrayList<>();


        mainRepository.getFireBaseManager()
                .loadPersonThings(
                        String.format("users/%s/things", personInfo.getUid()),
                        new PersonThingsObserver() {
                            @Override
                            public void onThingLoaded(FireBaseThingItem thing) {

                                things.add(thing);

                                if (mCallback!=null){
                                    mCallback.onThingLoaded(thing);
                                }

                            }
                        });

    }



    public void attachNewThingCallback(PersonThingsObserver callback){

        mCallback = callback;

        if (things == null){

            loadPersonThings();

        } else {

            for (FireBaseThingItem thing : things) {
                mCallback.onThingLoaded(thing);
            }

        }
    }

    public void detachNewThingCallback(){
        mCallback = null;
    }


    public List<FireBaseThingItem> getThings() {
        return things;
    }

    public String getCategoryName(int itemCategory) {
        return mainRepository.getCategories().get(itemCategory).getName();
    }

    public int getChosenThing() {
        return chosenThing;
    }

    public int getMyChosenThingPosition() {
        return chosenMyThing;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setChosenThing(int chosenThing) {
        this.chosenThing = chosenThing;
    }

    public List<ThingEntity> getMyAvailableThings(){

        if (availableThings == null){
            availableThings = new ArrayList<>();
            for (ThingEntity thing : mainRepository.getMyThingsManager().getMyThings()) {

                if (thing.getStatus()!= Constants.THING_STATUS_EXCHANGING_IN_PROCESS){
                    availableThings.add(thing);
                }
            }
        }

        return availableThings;

    }

    public void setMyChosenThingPosition(int position) {

        chosenMyThing = position;

    }

    public void updateAvailableThings() {
        availableThings = new ArrayList<>();
        for (ThingEntity thing : mainRepository.getMyThingsManager().getMyThings()) {

            if (thing.getStatus()!= Constants.THING_STATUS_EXCHANGING_IN_PROCESS){
                availableThings.add(thing);
            }
        }
    }

    public void sendOffer() {


        if (things.get(chosenThing) != null
                && availableThings.get(chosenMyThing) != null){

            mainRepository
                    .getFireBaseManager()
                    .sendOffer(
                            String.format("users/%s/offers/%s/%s",
                                    things.get(chosenThing).getUserUid(),
                                    things.get(chosenThing).getFireBaseID(),
                                    mainRepository.getUser().getUid()),
                            new FireBaseOfferItem(
                                    availableThings.get(chosenMyThing).getFireBasePath(),
                                    System.currentTimeMillis()));

            mainRepository.getFireBaseManager().sendNotification(
                    mainRepository.getAppContext()
                            .getString(R.string.notification_offer_msg, things.get(chosenThing).getItemName()),
                    mainRepository.getUser().getUid(),
                    things.get(chosenThing).getUserUid());

        }

    }
}

package com.dadabit.everythingexchange.ui.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.firebase.LoadFireBaseUserCallback;
import com.dadabit.everythingexchange.model.vo.FireBaseOfferItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.personInfo.PersonThingsObserver;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class PersonInfoActivityViewModel extends ViewModel {

    public static final int STATE_PERSON_THINGS = 0;
    public static final int STATE_SINGLE_THING = 1;
    public static final int STATE_MY_THINGS_SHOWN = 2;
    public static final int STATE_MY_THING_CHOSEN = 3;
    public static final int STATE_WAITING_NEW_THING = 4;

    private Repository mRepository;

    private MutableLiveData<User> personInfo;
    private MutableLiveData<List<FireBaseThingItem>> things;


    private List<ThingEntity> availableThings;
    private int state;

    private int myChosenThingPosition;
    private String uid;

    private FireBaseThingItem chosenThing;
    private int chosenThingPosition = -1;

    public PersonInfoActivityViewModel(Repository mRepository) {
        this.mRepository = mRepository;
        personInfo = new MutableLiveData<>();
        things = new MutableLiveData<>();
    }



    public MutableLiveData<User> getPersonInfo(String userUid){
        Log.d("@@@", "PersonInfoActivityViewModel.getPersonInfo");

        if (personInfo.getValue() == null){
            Log.d("@@@", "PersonInfoActivityViewModel.getPersonInfo.LOAD");

            mRepository.getFireBaseManager()
                    .loadUserInfo(
                            String.format("users/%s/metadata", userUid),
                            new LoadFireBaseUserCallback() {
                                @Override
                                public void onLoaded(User fireBaseUser) {
                                    Log.d("@@@", "PersonInfoActivityViewModel.getPersonInfo.LOADED");

                                    if (fireBaseUser != null){
                                        personInfo.setValue(fireBaseUser);
                                    }
                                }
                            }
                    );
        }
        return personInfo;
    }

    public MutableLiveData<List<FireBaseThingItem>> getThings() {
        if (things.getValue() == null){
            things.setValue(new ArrayList<FireBaseThingItem>());

            mRepository.getFireBaseManager()
                    .loadPersonThings(
                            String.format("users/%s/things", personInfo.getValue().getUid()),
                            new PersonThingsObserver() {
                                @Override
                                public void onThingLoaded(FireBaseThingItem thing) {

                                    if (things.getValue() == null){
                                        ArrayList<FireBaseThingItem> newThings = new ArrayList<>();
                                        newThings.add(thing);
                                        things.setValue(newThings);
                                    } else {

                                        ArrayList<FireBaseThingItem> newThings = (ArrayList<FireBaseThingItem>) things.getValue();

                                        newThings.add(thing);

                                        things.setValue(newThings);
                                    }

                                }
                            }
                    );
        }
        return things;
    }


    public int getState(){
        return state;
    }

    public int getMyChosenThingPosition() {
        return myChosenThingPosition;
    }


    public List<ThingEntity> getMyAvailableThings(){

        if (availableThings == null){
            availableThings = new ArrayList<>();
            for (ThingEntity thing : mRepository.getMyThingsManager().getMyThings()) {

                if (thing.getStatus()!= Constants.THING_STATUS_EXCHANGING_IN_PROCESS){
                    availableThings.add(thing);
                }
            }
        }

        return availableThings;

    }

    public void setChosenThingPosition(int chosenThingPosition) {
        this.chosenThingPosition = chosenThingPosition;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMyChosenThingPosition(int myChosenThingPosition) {
        this.myChosenThingPosition = myChosenThingPosition;
    }

    public int getChosenThingPosition() {
        return chosenThingPosition;
    }

    public FireBaseThingItem getChosenThing() {
        return chosenThing;
    }

    public void removeChosenThing(){
        chosenThingPosition = -1;
        chosenThing = null;
    }


    public void sendOffer() {

        if (chosenThing != null
                && availableThings.get(myChosenThingPosition) != null){

            mRepository
                    .getFireBaseManager()
                    .sendOffer(
                            String.format("users/%s/offers/%s/%s/%s",
                                    chosenThing.getUserUid(),
                                    chosenThing.getFireBaseID(),
                                    mRepository.getUser().getUid(),
                                    Utils.fireBasePathToId(availableThings.get(myChosenThingPosition).getFireBasePath())),
                            new FireBaseOfferItem(
                                    availableThings.get(myChosenThingPosition).getFireBasePath(),
                                    System.currentTimeMillis()));

            mRepository.getFireBaseManager().sendNotification(
                    mRepository.getAppContext()
                            .getString(R.string.notification_offer_msg, chosenThing.getItemName()),
                    mRepository.getUser().getUid(),
                    chosenThing.getUserUid());

        }
    }

    public void updateAvailableThings() {

        availableThings = new ArrayList<>();
        for (ThingEntity thing : mRepository.getMyThingsManager().getMyThings()) {

            if (thing.getStatus()!= Constants.THING_STATUS_EXCHANGING_IN_PROCESS){
                availableThings.add(thing);
            }
        }
    }

    public void setChosenThing(int chosenThingPosition) {
        this.chosenThingPosition = chosenThingPosition;

        if (things.getValue() != null){
            chosenThing = things.getValue().get(chosenThingPosition);
        }
    }

    public String getCategoryName(int category){
        return mRepository.getCategories().get(category).getName();
    }
}

package com.dadabit.everythingexchange.ui.viewmodel;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.firebase.LoadThingsCallback;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.MainActivityState;
import com.dadabit.everythingexchange.model.vo.MyThingsAdapterItem;
import com.dadabit.everythingexchange.model.vo.ThingCategory;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.utils.MyThingsManager;

import java.util.List;

public class MainActivityViewModel extends ViewModel{

    private Repository mRepository;

    private MainActivityState activityState;

    private MutableLiveData<Integer> newMessagesCounter;

    private MutableLiveData<List<ThingCategory>> categories;

    private MutableLiveData<String> title;

    private MutableLiveData<List<FireBaseThingItem>> thingItems;
    private int thingItemsCategory;

    private MutableLiveData<List<MyThingsAdapterItem>> myThings;


    public MainActivityViewModel(Repository mRepository) {
        Log.d("@@@", "MainActivityViewModel.create");
        this.mRepository = mRepository;
    }

    public MutableLiveData<Integer> getNewMessagesCounter(){
        Log.d("@@@", "MainActivityViewModel.getNewMessagesCounter");
        return mRepository
                .getChatItemsManager()
                .getNewMessagesCounter();
    }




//    ==============GETTERS===================

    public User getUser (){
        return mRepository.getUser();
    }

    public MainActivityState getState() {
        return activityState == null ? activityState = new MainActivityState() : activityState;
    }

    public MutableLiveData<List<ThingCategory>> getCategories() {
        return mRepository.getCategoriesLive();
    }


    public String getHomeLocation() {
        return mRepository.getSharedPreferences().getHomeLocation();
    }

    public MutableLiveData<String> getTitle() {
        return title == null ? title = new MutableLiveData<>() : title;
    }

    public MutableLiveData<List<FireBaseThingItem>> getThingItems() {
        return thingItems == null ? thingItems = new MutableLiveData<>() : thingItems;
    }

    public MutableLiveData<List<MyThingsAdapterItem>> getMyThings() {
        return mRepository.getMyThingsManager().getMyThingAdapterItems();
    }

    //    ==============SETTERS===================


    public void setChosenChatId(String thingId) {
        mRepository.setChosenChat(mRepository.getExchangeIdByThing(thingId));
    }

    public void setChosenChatId(int thingId) {
        mRepository.setChosenChat(thingId);
    }

    public void setHomeLocation(String location) {
        mRepository.getSharedPreferences().setHomeLocation(location);
    }

    public void setTitle(@NonNull String newtitle){
        if (title == null){
            title = new MutableLiveData<>();
        }
        title.setValue(newtitle);
    }




    public void loadFireBaseThings() {

        mRepository.loadFireBaseThings(getState().getChosenCategory(),
                new LoadThingsCallback() {
                    @Override
                    public void onLoaded(boolean isDataExist) {

                        if (isDataExist){

                            new android.os.Handler(Looper.getMainLooper()).post(
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                            thingItems.setValue(mRepository.getFireBaseItems());
                                        }
                                    }
                            );

                        } else {

                            thingItems.setValue(null);
                        }

                    }
                });


    }

    public void changeUserName(String name) {
        //TODO
    }

    public void changeUserImage(Bitmap bitmap) {

        //TODO

    }
}

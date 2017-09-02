package com.dadabit.everythingexchange.ui.presenter.personInfo;


import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;

public interface PersonThingsObserver {

    void onThingLoaded(FireBaseThingItem thing);
}

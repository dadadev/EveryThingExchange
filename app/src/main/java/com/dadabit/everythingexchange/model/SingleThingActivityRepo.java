package com.dadabit.everythingexchange.model;


import android.util.Log;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.FireBaseOfferItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SingleThingActivityRepo {

    private FireBaseOfferItem currentOffer;

    private List<ThingEntity> availableThings;

    private int chosenOfferPosition = -1;

    public boolean isWaitingNewThing = false;

    @Inject
    Repository mainRepository;

    public SingleThingActivityRepo() {
        Log.d("@@@", "SingleThingActivityRepo.create");
        App.getComponent().inject(this);
    }


    public List<ThingEntity> getAvailableThings(){

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

    public Repository getMainRepository() {
        return mainRepository;
    }


    public FireBaseThingItem getThing(){

        return mainRepository.getFireBaseItems() == null ? null :
                mainRepository.getFireBaseItems()
                        .get(mainRepository.getState().getChosenFireBaseThing());

    }


    public int getChosenOfferPosition() {
        return chosenOfferPosition;
    }

    public void setChosenOfferPosition(int chosenOfferPosition) {
        this.chosenOfferPosition = chosenOfferPosition;
    }



    public void setCurrentOffer(ThingEntity offerThing){



        if (offerThing != null){

            currentOffer = new FireBaseOfferItem(
                    offerThing.getFireBasePath(),
                    System.currentTimeMillis());

        } else {

            currentOffer = null;
            chosenOfferPosition = -1;

        }


    }

    public boolean isOfferDataExist(){
        return currentOffer != null;
    }

    public void sendOfferToFireBase(){

        if (currentOffer != null && chosenOfferPosition != -1){

            mainRepository
                    .getFireBaseManager()
                    .sendOffer(
                            String.format("users/%s/offers/%s/%s/%s",
                                    getThing().getUserUid(),
                                    getThing().getFireBaseID(),
                                    mainRepository.getUser().getUid(),
                                    Utils.fireBasePathToId(currentOffer.getFireBaseThingPath())),
                            currentOffer);

            mainRepository.getFireBaseManager().sendNotification(
                    mainRepository.getAppContext()
                            .getString(R.string.notification_offer_msg, getThing().getItemName()),
                    mainRepository.getUser().getUid(),
                    getThing().getUserUid());

        }


    }


    public void removeAvailableThings() {

        availableThings = null;
    }

}

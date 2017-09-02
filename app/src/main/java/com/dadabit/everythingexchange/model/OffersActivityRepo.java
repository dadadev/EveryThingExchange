package com.dadabit.everythingexchange.model;


import android.support.annotation.IntDef;
import android.util.Log;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ExchangePathEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.model.vo.ChatMessage;
import com.dadabit.everythingexchange.model.vo.FireBaseExchangeItem;
import com.dadabit.everythingexchange.model.vo.Notification;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.MyThingsManager;
import com.dadabit.everythingexchange.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class OffersActivityRepo {


    @Inject Repository mainRepository;

    private List<OfferItem> offers;

    private int chosenOfferPosition = -1;

    private MyThingsManager.OffersChangeObserver mCallback;

    private int state;

    private ThingEntity currentThing;


    @IntDef(value = {
            State.CREATED,
            State.SHOW_INFO,
            State.HIDE_INFO,
            State.SHOW_BOTTOM_SHEET
    })

    public @interface State {
        int CREATED = 0;
        int SHOW_INFO = 1;
        int HIDE_INFO = 2;
        int SHOW_BOTTOM_SHEET = 3;
    }


    public OffersActivityRepo() {
        Log.d("@@@", "OffersActivityRepo.create");
        App.getComponent().inject(this);
    }


    public ThingEntity getThing(){

        if (currentThing == null){
            if (mainRepository.getMyThingsManager().getMyThings() != null){
                currentThing = mainRepository.getMyThingsManager().getMyThings()
                        .get(mainRepository.getState().getChosenMyThing());

            }
        }
        return currentThing;
    }

    public List<OfferItem> getOffers() {
        return offers;
    }

    public Repository getMainRepository() {
        return mainRepository;
    }



    public void attachOffersObserver(MyThingsManager.OffersChangeObserver mCallback){
        this.mCallback = mCallback;

        if (offers != null){

            for (OfferItem offer :
                    offers) {
                mCallback.onChange(offer);
            }

        } else {

            loadOffers();

        }



    }

    private void loadOffers() {

        mainRepository.getMyThingsManager().attachOffersByThingObserver(
                Utils.fireBasePathToId(currentThing.getFireBasePath()),
                new MyThingsManager.OffersChangeObserver() {
                    @Override
                    public void onChange(OfferItem newOffer) {

                        Log.d("@@@", "OffersActivityRepo.loadOffers.onChange");

                        if (newOffer != null){

                            if (offers == null){
                                offers = new ArrayList<>();
                            }

                            offers.add(newOffer);

                        }

                        if (mCallback!= null){
                            mCallback.onChange(newOffer);
                        }
                    }
                });

    }


    public void detachOffersObserver(){
        this.mCallback = null;
    }

    public void clean() {

        mainRepository.getMyThingsManager().detachOffersByPersonObserver();

    }

    public String getThingCategoryName() {
        return mainRepository
                .getCategories()
                .get(getThing().getCategory())
                .getName();
    }

    public String getThingDate() {
        return Utils.timestampToString(getThing().getDate().getTime());
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setChosenOfferPosition(int chosenOfferPosition) {
        this.chosenOfferPosition = chosenOfferPosition;
    }

    public int getChosenOfferPosition() {
        return chosenOfferPosition;
    }



    public void confirmExchange(Calendar chosenDate, ConfirmExchangeCompleteCallback callback) {
        Log.d("@@@", "OffersActivityRepo.confirmExchange");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        final long startDate = System.currentTimeMillis();

        final String chatPath = String.format("users/%s/chats/%s",
                offers.get(chosenOfferPosition).getOffererUid(),
                offers.get(chosenOfferPosition).getOfferThingId());

        final String offerPath = String.format("users/%s/offers/%s/%s/%s",
                mainRepository.getUser().getUid(),
                Utils.fireBasePathToId(currentThing.getFireBasePath()),
                offers.get(chosenOfferPosition).getOffererUid(),
                offers.get(chosenOfferPosition).getOfferThingId());

        final String exchangePath =
                String.format("users/%s/exchanges", offers.get(chosenOfferPosition).getOffererUid());

        final String fireBaseKey =
                mainRepository.getFireBaseManager().getNewKey(exchangePath);


        final String message = mainRepository.getAppContext()
                .getString(R.string.first_message, dateFormat.format(chosenDate.getTime()));

        //generate exchange

        FireBaseExchangeItem exchangeItem =
                new FireBaseExchangeItem(
                        chatPath,
                        offers.get(chosenOfferPosition).getItemThingPath(),
                        currentThing.getFireBasePath(),
                        "",
                        startDate,
                        chosenDate.getTimeInMillis(),
                        Constants.EXCHANGE_STATUS_CREATED);

        ExchangeEntity exchange =
                new ExchangeEntity(
                        fireBaseKey,
                        chatPath,
                        currentThing.getFireBasePath(),
                        currentThing.getName(),
                        currentThing.getImgLink(),
                        mainRepository.getUser().getUid(),
                        mainRepository.getUser().getName(),
                        mainRepository.getUser().getImgUrl(),
                        offers.get(chosenOfferPosition).getItemThingPath(),
                        offers.get(chosenOfferPosition).getItemName(),
                        offers.get(chosenOfferPosition).getItemImgLink(),
                        offers.get(chosenOfferPosition).getOffererUid(),
                        offers.get(chosenOfferPosition).getOffererName(),
                        offers.get(chosenOfferPosition).getOffererImg(),
                        "",
                        startDate,
                        chosenDate.getTimeInMillis(),
                        Constants.EXCHANGE_STATUS_CREATED);


        //save new exchange in db
        int dbID = (int) mainRepository.getDb().exchangeDao().insertExchange(exchange);
        exchange.setId(dbID);

        //save exchange path in db
        mainRepository.getDb().exchangePathDao().insertPath(
                new ExchangePathEntity(
                        dbID,
                        exchangePath+"/"+fireBaseKey));


        mainRepository.getExchanges().add(exchange);

        //Update myThing status in db
        mainRepository.getDb().myThingDao().updateThing(
                mainRepository.getMyThingsManager()
                        .updateThing(
                                exchange,
                                Constants.THING_STATUS_EXCHANGING_IN_PROCESS));

        mainRepository.getState().setChosenChatId(exchange.getId());

        // send new exchange to fireBase
        mainRepository.getFireBaseManager()
                .sendExchange(
                        exchangePath+"/"+fireBaseKey,
                        exchangeItem);


        //update things status in FireBase
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put(
                currentThing.getFireBasePath()+"/status",
                Constants.THING_STATUS_EXCHANGING_IN_PROCESS);
        updateMap.put(
                offers.get(chosenOfferPosition).getItemThingPath()+"/status",
                Constants.THING_STATUS_EXCHANGING_IN_PROCESS);
        mainRepository.getFireBaseManager()
                .updateValues(updateMap);

        //remove offer
        mainRepository.getFireBaseManager().removeFromFireBase(offerPath);
        mainRepository.getMyThingsManager().removeOffer(
                mainRepository.getState().getChosenMyThing(),
                Utils.fireBasePathToId(currentThing.getFireBasePath()),
                offers.get(chosenOfferPosition).getOfferThingId());


        //sendFirstMessageToChat
        mainRepository.getFireBaseManager().sendChatMessage(chatPath,
                new ChatMessage(
                        message,
                        mainRepository.getUser().getUid(),
                        startDate),
                new Notification(
                        startDate,
                        message,
                        mainRepository.getUser().getUid(),
                        offers.get(chosenOfferPosition).getOffererUid()));


        mainRepository.getChatItemsManager().addChatItem(
                new ChatItem(
                        exchange.getId(),
                        exchange.getThing2_ownerImg(),
                        exchange.getThing2_ownerName(),
                        exchange.getThing1_ownerImg(),
                        exchange.getThing1_ownerName(),
                        exchange.getThing1_img(),
                        exchange.getThing2_img(),0));

        mainRepository.getFireBaseManager().addChatListener(
                exchange.getChatPath(),
                mainRepository.getExchanges().size()-1);


        callback.onComplete(true);

    }



    public interface ConfirmExchangeCompleteCallback {
        void onComplete(boolean isComplete);
    }

}

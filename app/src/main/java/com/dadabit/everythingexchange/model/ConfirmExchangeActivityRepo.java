package com.dadabit.everythingexchange.model;


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
import com.dadabit.everythingexchange.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

public class ConfirmExchangeActivityRepo {


    @Inject Repository mainRepository;

    private int myThingPosition;
    private String offerId;
    private OfferItem currentOffer;

    public ConfirmExchangeActivityRepo(int myThingPosition, String offerId) {
        Log.d("@@@", "ConfirmExchangeActivityRepo.create");
        App.getComponent().inject(this);
        this.myThingPosition = myThingPosition;
        this.offerId = offerId;
    }


    public ThingEntity getMyThing() {

        return mainRepository.getMyThingsManager().getMyThings().get(myThingPosition);
    }

    public OfferItem getOffer() {

        if (currentOffer == null){

            currentOffer = mainRepository.getMyThingsManager().getOfferById(offerId);

        }
        return currentOffer;
    }

    public void sendToFireBase(Calendar chosenDate, ConfirmExchangeCompleteCallback callback) {
        Log.d("@@@", "ConfirmExchangeActivityRepo.sendToFireBase");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        final long startDate = System.currentTimeMillis();

        final String chatPath = String.format("users/%s/chats/%s",
                currentOffer.getOffererUid(),
                currentOffer.getOfferThingId());

        final String offerPath = String.format("users/%s/offers/%s/%s/%s",
                mainRepository.getUser().getUid(),
                getMyThing().getFireBasePath()
                        .substring(getMyThing().getFireBasePath().lastIndexOf('/')+1),
                currentOffer.getOffererUid(),
                currentOffer.getOfferThingId());

        final String exchangePath =
                String.format("users/%s/exchanges", currentOffer.getOffererUid());

        final String fireBaseKey =
                mainRepository.getFireBaseManager().getNewKey(exchangePath);


        final String message = mainRepository.getAppContext()
                .getString(R.string.first_message, dateFormat.format(chosenDate.getTime()));

        //generate exchange

        FireBaseExchangeItem exchangeItem =
                new FireBaseExchangeItem(
                        chatPath,
                        currentOffer.getItemThingPath(),
                        getMyThing().getFireBasePath(),
                        "",
                        startDate,
                        chosenDate.getTimeInMillis(),
                        Constants.EXCHANGE_STATUS_CREATED);

        ExchangeEntity exchange =
                new ExchangeEntity(
                        fireBaseKey,
                        chatPath,
                        getMyThing().getFireBasePath(),
                        getMyThing().getName(),
                        getMyThing().getImgLink(),
                        mainRepository.getUser().getUid(),
                        mainRepository.getUser().getName(),
                        mainRepository.getUser().getImgUrl(),
                        currentOffer.getItemThingPath(),
                        currentOffer.getItemName(),
                        currentOffer.getItemImgLink(),
                        currentOffer.getOffererUid(),
                        currentOffer.getOffererName(),
                        currentOffer.getOffererImg(),
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
                mainRepository.getMyThingsManager().updateThing(
                        exchange,
                        Constants.THING_STATUS_EXCHANGING_IN_PROCESS));

        mainRepository.setChosenChat(exchange.getId());

        // send new exchange to fireBase
        mainRepository.getFireBaseManager()
                .sendExchange(
                        exchangePath+"/"+fireBaseKey,
                        exchangeItem);


        //update things status in FireBase
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put(
                getMyThing().getFireBasePath()+"/status",
                Constants.THING_STATUS_EXCHANGING_IN_PROCESS);
        updateMap.put(
                currentOffer.getItemThingPath()+"/status",
                Constants.THING_STATUS_EXCHANGING_IN_PROCESS);
        mainRepository.getFireBaseManager()
                .updateValues(updateMap);

        //remove offer
        mainRepository.getFireBaseManager().removeFromFireBase(offerPath);
        mainRepository.getMyThingsManager().removeOffer(
                myThingPosition,
                Utils.fireBasePathToId(getMyThing().getFireBasePath()),
                offerId);


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
                        currentOffer.getOffererUid()));


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

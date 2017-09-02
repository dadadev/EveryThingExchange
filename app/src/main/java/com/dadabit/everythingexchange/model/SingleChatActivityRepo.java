package com.dadabit.everythingexchange.model;


import android.util.Log;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.vo.ChatMessage;
import com.dadabit.everythingexchange.model.vo.ChatMessageItem;
import com.dadabit.everythingexchange.model.vo.Notification;
import com.dadabit.everythingexchange.utils.ChatItemsManager;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class SingleChatActivityRepo {



    private ExchangeEntity currentExchange;

    private List<ChatMessageItem> chatItems;

    @Inject Repository mainRepository;


    public SingleChatActivityRepo() {
        Log.d("@@@", "SingleChatActivityRepo.create");
        App.getComponent().inject(this);
    }

    public List<ChatMessageItem> getChatItems() {

        if (chatItems == null
                && getExchange() != null){

            chatItems = mainRepository.getChatItemsManager().getMessageItems(getExchange().getId());

        }

        return chatItems;
    }

    public void setChatItems(List<ChatMessageItem> chatItems) {
        this.chatItems = chatItems;
    }

    public void sendMessage(String message){

        if (message.length()>0){
            final long timestamp = System.currentTimeMillis();

            mainRepository.getFireBaseManager().sendChatMessage(
                    getExchange().getChatPath(),
                    new ChatMessage(
                            message,
                            getExchange().getThing1_ownerId(),
                            timestamp),
                    new Notification(
                            timestamp,
                            message,
                            getExchange().getThing1_ownerId(),
                            getExchange().getThing2_ownerId()));

        }
    }

    public void cleanChatCounter(){

        if (mainRepository != null){
            mainRepository.cleanChatCounter(getExchange().getId());
        }

    }

    public ExchangeEntity getExchange(){

        if (currentExchange == null){
            currentExchange = mainRepository.getExchangeById(
                    mainRepository.getState().getChosenChat());
        }
        return currentExchange;

    }

    public void attachListener(ChatItemsManager.NewMessageCallback newMessageCallback){

        mainRepository.getChatItemsManager()
                .attachSingleChatListener(
                        getExchange().getId(),
                        newMessageCallback);
    }

    public void detachListener(){
        mainRepository.getChatItemsManager().detachSingleChatListener();
    }


    public void changeDate(long newDate) {
        Log.d("@@@", "SingleChatActivityRepo.changeDate");

        if (getExchange() != null){

            HashMap<String, Object> updatedValues = new HashMap<>();

            final String myExchangePath = String.format("users/%s/exchanges/%s",
                    getExchange().getThing1_ownerId(),
                    getExchange().getFireBaseId());

            final String offererExchangePath = String.format("users/%s/exchanges/%s",
                    getExchange().getThing2_ownerId(),
                    getExchange().getFireBaseId());

            updatedValues.put(
                    myExchangePath+"/status",
                    Constants.EXCHANGE_STATUS_DATE_CHANGED);
            updatedValues.put(
                    myExchangePath+"/endDate",
                    newDate);

            updatedValues.put(
                    offererExchangePath+"/status",
                    Constants.EXCHANGE_STATUS_DATE_CHANGED);
            updatedValues.put(
                    offererExchangePath+"/endDate",
                    newDate);

            mainRepository.getFireBaseManager().updateValues(updatedValues);


            final long timestamp = System.currentTimeMillis();

            mainRepository.getFireBaseManager().sendChatMessage(
                    getExchange().getChatPath(),
                    new ChatMessage(
                            "Exchange date changed to "+Utils.timestampToString(newDate),
                            getExchange().getThing1_ownerId(),
                            timestamp),
                    new Notification(
                            timestamp,
                            "Exchange date changed to "+Utils.timestampToString(newDate),
                            getExchange().getThing1_ownerId(),
                            getExchange().getThing2_ownerId()));

        }
    }

    public void cancelExchange(boolean sendToOfferer) {

        if (getExchange() != null){

            HashMap<String, Object> updatedValues = new HashMap<>();

            updatedValues.put(
                    String.format("users/%s/exchanges/%s/status",
                            getExchange().getThing1_ownerId(),
                            getExchange().getFireBaseId()),
                    Constants.EXCHANGE_STATUS_CANCELED);


            updatedValues.put(
                    getExchange().getThing1_path()+"/status",
                    Constants.THING_STATUS_AVAILABLE);


            if (sendToOfferer){

                updatedValues.put(
                        String.format("users/%s/exchanges/%s/status",
                                getExchange().getThing2_ownerId(),
                                getExchange().getFireBaseId()),
                        Constants.EXCHANGE_STATUS_CANCELED);


                updatedValues.put(
                        getExchange().getThing2_path()+"/status",
                        Constants.THING_STATUS_AVAILABLE);

            }

            mainRepository.getFireBaseManager().updateValues(updatedValues);

        }
    }

    public void endExchange() {

        if (getExchange() != null){

            HashMap<String, Object> updatedValues = new HashMap<>();

            updatedValues.put(
                    String.format("users/%s/exchanges/%s/status",
                            getExchange().getThing2_ownerId(),
                            getExchange().getFireBaseId()),
                    Constants.EXCHANGE_STATUS_ENDED);

            mainRepository.getFireBaseManager().updateValues(updatedValues);

            removeExchange();

        }
    }

    public void removeExchange() {

        mainRepository.getFireBaseManager().removeFromFireBase(
                String.format("users/%s/exchanges/%s",
                        getExchange().getThing1_ownerId(),
                        getExchange().getFireBaseId()));

        mainRepository.removeMyThing(getExchange().getThing1_path());

        for (int i = 0; i < mainRepository.getExchanges().size(); i++) {
            if (mainRepository.getExchanges().get(i).getId() == currentExchange.getId()){

                mainRepository.removeExchange(i);
                break;

            }
        }



    }

    public void loadChatItems() {

        mainRepository.getChatItemsManager().loadChatItems(getExchange().getId());


    }
}

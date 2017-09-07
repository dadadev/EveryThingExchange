package com.dadabit.everythingexchange.model.firebase;


import android.support.annotation.NonNull;
import android.util.Log;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.vo.ChatMessage;
import com.dadabit.everythingexchange.model.vo.FireBaseExchangeItem;
import com.dadabit.everythingexchange.model.vo.FireBaseOfferItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.Notification;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.model.vo.ThingPath;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.personInfo.PersonThingsObserver;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireBaseManager {

    private OffersCallback offersCallback;
    private ExchangesListener exchangesCallback;
    private NewChatMessageCallback newChatMessageCallback;


    private DatabaseReference mDatabaseReference;

    private FireBaseValueEventListenerRef offersListener;
    private FireBaseChildEventListenerRef fireBaseExchangesListener;
    private List<FireBaseChildEventListenerRef> chatsListeners;


    public FireBaseManager() {
        Log.d("@@@", "FireBaseManager.create");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void initOffersListener(String path,
                                   final OffersCallback offersCallback){
        Log.d("@@@", "FireBaseManager.initOffersListener");

        this.offersCallback = offersCallback;

        offersListener = new FireBaseValueEventListenerRef(
                mDatabaseReference.child(path),
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("@@@", "FireBaseManager.initExchangesListener.onDataChange");

                        if (dataSnapshot.exists()){

                            loadThingsData(Utils.convertSnapshotToOffers(dataSnapshot));

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("@@@", "FireBaseManager.initOffersListener.ERROR (!!!) "+ databaseError.getDetails());

                    }
                });


    }

    public void initExchangesListener(String path,
                                      ExchangesListener callback){
        Log.d("@@@", "FireBaseManager.initExchangesListener");

        this.exchangesCallback = callback;


        if (fireBaseExchangesListener != null){
            fireBaseExchangesListener.detach();
            fireBaseExchangesListener = null;
        }

        fireBaseExchangesListener = new FireBaseChildEventListenerRef(
                mDatabaseReference.child(path),
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot.exists()){

                            final FireBaseExchangeItem exchangeItem =
                                    dataSnapshot.getValue(FireBaseExchangeItem.class);

                            switch (exchangeItem.getStatus()){
                                case Constants.EXCHANGE_STATUS_CREATED:


                                    new ExchangeEntityLoader(
                                            exchangeItem,
                                            dataSnapshot.getKey(),
                                            new ExchangeEntityLoader.LoaderCallback() {
                                                @Override
                                                public void onExchangeLoaded(ExchangeEntity exchange) {
                                                    if (exchangesCallback!= null){
                                                        exchangesCallback.onNew(exchange);
                                                    }
                                                }
                                            })
                                            .load(
                                                    mDatabaseReference
                                                            .child(exchangeItem.getThing1_path()),
                                                    mDatabaseReference
                                                            .child(exchangeItem.getThing2_path()));


                                    break;

                                case Constants.EXCHANGE_STATUS_DATE_CHANGED:

                                    exchangesCallback.onDateChange(
                                            dataSnapshot.getKey(),
                                            exchangeItem.getEndDate());


                                    break;

                                case Constants.EXCHANGE_STATUS_CANCELED:

                                    if (exchangesCallback!= null){

                                        exchangesCallback.onCancelExchange(dataSnapshot.getKey());
                                    }

                                    break;

                                case Constants.EXCHANGE_STATUS_ENDED:

                                    if (exchangesCallback!= null){

                                        exchangesCallback.onEndExchange(dataSnapshot.getKey());
                                    }

                                    break;
                            }
                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot.exists()){

                            final FireBaseExchangeItem exchangeItem =
                                    dataSnapshot.getValue(FireBaseExchangeItem.class);

                            switch (exchangeItem.getStatus()){

                                case Constants.EXCHANGE_STATUS_DATE_CHANGED:

                                    if (exchangesCallback != null){

                                        exchangesCallback.onDateChange(
                                                dataSnapshot.getKey(),
                                                exchangeItem.getEndDate());
                                    }

                                    break;

                                case Constants.EXCHANGE_STATUS_CANCELED:

                                    if (exchangesCallback!= null){

                                        exchangesCallback.onCancelExchange(dataSnapshot.getKey());
                                    }

                                    break;

                                case Constants.EXCHANGE_STATUS_ENDED:

                                    if (exchangesCallback!= null){

                                        exchangesCallback.onEndExchange(dataSnapshot.getKey());
                                    }


                                    break;
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );


    }

    public void initChatsListeners(List<ExchangeEntity> exchanges,
                                   NewChatMessageCallback newChatMessageCallback){
        Log.d("@@@", "FireBaseManager.initChatsListeners");

        this.newChatMessageCallback = newChatMessageCallback;

        if (chatsListeners != null){
            for (FireBaseChildEventListenerRef listener :
                    chatsListeners) {
                listener.detach();
            }
            chatsListeners = null;
        }

        chatsListeners = new ArrayList<>();

        for (int i = 0; i < exchanges.size(); i++) {
            Log.d("@@@", "FireBaseManager.initChatListener.position "+i+" of "+(exchanges.size()-1));

            addChatListener(exchanges.get(i).getChatPath(), i);

        }

    }



    public void loadFireBaseThings(String path, ValueEventListener eventListener){

        mDatabaseReference
                .child(path)
                .addListenerForSingleValueEvent(eventListener);


    }

    public void loadUserInfo(String path, final LoadFireBaseUserCallback callback){
        Log.d("@@@", "FireBaseManager.loadUserInfo");

        mDatabaseReference.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("@@@", "FireBaseManager.loadUserInfo.onDataChange.isExist: "+ dataSnapshot.exists());

                        if (dataSnapshot.exists()){

                            callback.onLoaded(dataSnapshot.getValue(User.class));

                        } else {

                            callback.onLoaded(null);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("@@@", "FireBaseManager.loadUserInfo.onCancelled (!!!)");

                    }
                });
    }

    public void loadPersonThings(String path, final PersonThingsObserver callback){

        mDatabaseReference.child(path).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            for (DataSnapshot snapshot :
                                    dataSnapshot.getChildren()) {

                                mDatabaseReference
                                        .child(snapshot.getValue(ThingPath.class).getPath())
                                        .addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        FireBaseThingItem item = dataSnapshot.getValue(FireBaseThingItem.class);

                                                        if (item.getStatus() != Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

                                                            callback.onThingLoaded(item);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                }
                                        );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    public void loadLocations(final String country, final LocationsLoaderCallback callback) {

        //TODO REMOVE
        final long startTimestamp = System.currentTimeMillis();

        Log.d("@@@@", "FireBaseManager.loadLocations.start: "+ startTimestamp);

        mDatabaseReference.child(String.format("things/%s", country))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //TODO REMOVE
                                final long getDataTimestamp = System.currentTimeMillis();
                                Log.d("@@@@", "FireBaseManager.loadLocations.onDataChange: "+ getDataTimestamp);
                                Log.d("@@@@", "FireBaseManager.loadLocations.requestTime: "+ + (startTimestamp - getDataTimestamp));

                                List<String> locations = new ArrayList<>();

                                for (DataSnapshot snapshot :
                                        dataSnapshot.getChildren()) {
                                    locations.add(snapshot.getKey());

                                }

                                //TODO REMOVE
                                final long endTimestamp = System.currentTimeMillis();
                                Log.d("@@@@", "FireBaseManager.loadLocations.end: "+ endTimestamp);
                                Log.d("@@@@", "FireBaseManager.loadLocations.saveTime: "+ (getDataTimestamp-endTimestamp));
                                Log.d("@@@@", "FireBaseManager.loadLocations.takenTime: "+ (startTimestamp-endTimestamp));

                                callback.onLoadLocations(locations);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
    }

    private void loadThingsData(final List<FireBaseOfferItem> offers) {

        Log.d("@@@", "FireBaseManager.Offers.loadThingsData");

        if (offers.size() > 0){

            final List<OfferItem> newOffers = new ArrayList<>();

            for (int i = 0; i < offers.size(); i++) {

                final int position = i;
                Log.d("@@@", "FireBaseManager.Offers.loadThingsData.position: "+position);

                mDatabaseReference.child(offers.get(i).getFireBaseThingPath())
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()){

                                            FireBaseThingItem item =
                                                    dataSnapshot.getValue(FireBaseThingItem.class);
                                            item.setFireBaseID(dataSnapshot.getKey());


                                            Log.d("@@@", "----"+position+"-------");
                                            Log.d("@@@", "--NAME--"+item.getItemName()+"-------");
                                            Log.d("@@@", "--ID--"+item.getFireBaseID()+"-------");
                                            Log.d("@@@", "----------------");


                                            if (item.getStatus() !=
                                                    Constants.THING_STATUS_EXCHANGING_IN_PROCESS
                                                    && offersCallback != null){

                                                offersCallback.onOffersChanged(
                                                        new OfferItem(
                                                                offers.get(position).getOfferThingId(),
                                                                offers.get(position).getMainThingId(),
                                                                item.getUserUid(),
                                                                item.getUserName(),
                                                                item.getUserImg(),
                                                                offers.get(position).getFireBaseThingPath(),
                                                                item.getItemName(),
                                                                item.getItemDescription(),
                                                                item.getItemImgLink(),
                                                                item.getItemCategory(),
                                                                item.getItemArea(),
                                                                offers.get(position).getDate()));
                                            }


                                        } else {
                                            // TODO: Handle deleted thing
                                            Log.d("@@@", "FireBaseManager.Offers.loadThingsData.THING WAS DELETED !!!");

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("@@@", "FireBaseManager.checkOffers.onDataChange.ERROR (!!!) "+databaseError.getDetails());

                                    }
                                }
                        );
            }
        }
    }



    public void sendThing(String path, FireBaseThingItem fireBaseThingItem) {
        Log.d("@@@", "FireBaseManager.sendThing");

        mDatabaseReference
                .child(path)
                .setValue(fireBaseThingItem);

    }

    public void sendUserInfo(User fireBaseUser){
        Log.d("@@@", "FireBaseManager.sendUserInfo");

        mDatabaseReference
                .child(String.format("users/%s/metadata", fireBaseUser.getUid()))
                .setValue(fireBaseUser);

    }

    public void sendOffer(String path, FireBaseOfferItem offerItem){
        Log.d("@@@", "FireBaseManager.sendOffer");

        mDatabaseReference
                .child(path)
                .setValue(offerItem);

    }

    public void sendExchange(String path, FireBaseExchangeItem exchange){

        mDatabaseReference.child(path).setValue(exchange);

    }

    public void sendNotification(String notification, String firstUid, String secondUid){

        mDatabaseReference
                .child("notifications")
                .child("messages")
                .push()
                .setValue(
                        new Notification(
                                System.currentTimeMillis(),
                                notification,
                                firstUid,
                                secondUid));


    }

    public void sendChatMessage(String path, ChatMessage message, Notification notification){
        mDatabaseReference
                .child(path)
                .push()
                .setValue(message);
        mDatabaseReference
                .child("notifications")
                .child("messages")
                .push()
                .setValue(notification);
    }


    public void addChatListener(String path, int position){
        Log.d("@@@", "FireBaseManager.addChatListener");
        chatsListeners.add(
                new FireBaseChildEventListenerRef(
                        mDatabaseReference.child(path),
                        createNewChatListener(position)
                ));
    }

    private ChildEventListener createNewChatListener(final int i) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()){
                    newChatMessageCallback.onNewMessage(
                            i,
                            dataSnapshot.getValue(ChatMessage.class));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("@@@", "FireBaseManager.createNewChatListener.onCancelled (!!!) position: "+i+ " error: "+databaseError.getDetails());

            }
        };
    }

    public void updateValues(HashMap<String, Object> updateMap){
        mDatabaseReference.updateChildren(updateMap);
    }

    public String getNewKey(String path){
        return mDatabaseReference.child(path).push().getKey();
    }



    public void removeFromFireBase(String path){
        mDatabaseReference.child(path).removeValue();
    }

    public void removeFromStorage(String imgLink) {

        FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://everythingexchange-29da5.appspot.com/")
                .getStorage()
                .getReferenceFromUrl(imgLink)
                .delete()
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("@@@", "FireBaseManager.removeFromStorage.onSuccess");

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("@@@", "FireBaseManager.removeFromStorage.onFailure (!!!) ");

                            }
                        }
                );

    }
    public void removeListeners() {
        Log.d("@@@", "FireBaseManager.removeListeners");

        if (offersListener != null){
            offersListener.detach();
            offersListener = null;
        }

        if (fireBaseExchangesListener != null){
            fireBaseExchangesListener.detach();
            fireBaseExchangesListener = null;
        }
        if (chatsListeners != null){
            for (FireBaseChildEventListenerRef chatListener :
                    chatsListeners) {
                chatListener.detach();
            }
            chatsListeners = null;
        }

    }

    public void removeChatListener(int i) {
        if (chatsListeners.get(i) != null){
            chatsListeners.get(i).detach();
            chatsListeners.remove(i);
        }
    }



    public interface LocationsLoaderCallback{
        void onLoadLocations(List<String> locations);
    }
}

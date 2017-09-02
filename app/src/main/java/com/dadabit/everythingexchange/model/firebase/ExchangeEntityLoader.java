package com.dadabit.everythingexchange.model.firebase;


import android.util.Log;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.vo.FireBaseExchangeItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ExchangeEntityLoader {

    private FireBaseExchangeItem exchangeItem;
    private String exchangeId;

    private FireBaseThingItem thing1;
    private FireBaseThingItem thing2;


    private LoaderCallback mCallback;


    public ExchangeEntityLoader(
            FireBaseExchangeItem exchangeItem,
            String exchangeId,
            LoaderCallback mCallback) {
        this.exchangeItem = exchangeItem;
        this.exchangeId = exchangeId;
        this.mCallback = mCallback;
    }


    public void load(DatabaseReference thing1Reference,
                     DatabaseReference thing2Reference){
        Log.d("@@@", "ExchangeEntityLoader.load: "+exchangeId);

        thing1Reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            thing1 = dataSnapshot.getValue(FireBaseThingItem.class);
                            thing1.setFireBaseID(dataSnapshot.getKey());

                            initExchange();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("@@@", "ExchangeEntityLoader.load.thing1Reference.onCancelled: "+databaseError.getDetails());

                    }
                }
        );

        thing2Reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        thing2 = dataSnapshot.getValue(FireBaseThingItem.class);
                        thing2.setFireBaseID(dataSnapshot.getKey());

                        initExchange();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("@@@", "ExchangeEntityLoader.load.thing2Reference.onCancelled: "+databaseError.getDetails());

                    }
                }
        );


    }

    private void initExchange() {

        if (thing1 != null && thing2 != null){

            mCallback.onExchangeLoaded(
                    new ExchangeEntity(
                            exchangeId,
                            exchangeItem.getChatPath(),
                            exchangeItem.getThing1_path(),
                            thing1.getItemName(),
                            thing1.getItemImgLink(),
                            thing1.getUserUid(),
                            thing1.getUserName(),
                            thing1.getUserImg(),
                            exchangeItem.getThing2_path(),
                            thing2.getItemName(),
                            thing2.getItemImgLink(),
                            thing2.getUserUid(),
                            thing2.getUserName(),
                            thing2.getUserImg(),
                            exchangeItem.getLocation(),
                            exchangeItem.getStartDate(),
                            exchangeItem.getEndDate(),
                            Constants.EXCHANGE_STATUS_SAVED));
        }
    }

    interface LoaderCallback {
        void onExchangeLoaded(ExchangeEntity exchange);
    }
}

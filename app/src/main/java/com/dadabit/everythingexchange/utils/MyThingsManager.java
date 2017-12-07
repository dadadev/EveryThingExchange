package com.dadabit.everythingexchange.utils;


import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.MyThingsAdapterItem;
import com.dadabit.everythingexchange.model.vo.NewThingCreation;
import com.dadabit.everythingexchange.model.vo.OfferItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class MyThingsManager {

    private MutableLiveData<List<MyThingsAdapterItem>> myThingAdapterItems;

    private List<ThingEntity> myThings;

//    private List<MyThingsAdapterItem> myThingsAdapterItems;
    private HashMap<String, List<OfferItem>> offersByThing;

    private MyThingsChangeObserver myThingsChangeObserver;
    private OffersChangeObserver offersByPersonObserver;


    private NewThingCreation newThing;



    public void init(List<ThingEntity> myThings, List<ExchangeEntity> exchanges){
        Log.d("@@@", "MyThingsManager.init");

        this.myThings = myThings;

        offersByThing = new HashMap<>();

        initAdapterItems(exchanges);

    }

    private void initAdapterItems(List<ExchangeEntity> exchanges) {

        myThingAdapterItems = new MutableLiveData<>();
        List<MyThingsAdapterItem> adapterItems = new ArrayList<>();

        for (ThingEntity thing : myThings) {

            final String thingId = Utils.fireBasePathToId(thing.getFireBasePath());

            MyThingsAdapterItem item = new MyThingsAdapterItem(
                    thing.getStatus(),
                    thingId,
                    thing.getName(),
                    thing.getImgBitmap(),
                    0,
                    null);

            if (thing.getStatus() == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

                for (ExchangeEntity exchange :
                        exchanges) {
                    if (exchange.getThing1_path()
                            .equals(thing.getFireBasePath())){

                        item.setExchangeImages(
                                new String[] {
                                        exchange.getThing1_img(),
                                        exchange.getThing2_img()});
                        break;
                    }
                }
            }

            adapterItems.add(item);
        }


        myThingAdapterItems.postValue(adapterItems);



    }

    public void addNewOffer(OfferItem newOffer, int chosenThingPosition) {

        if (offersByThing.get(newOffer.getMainThingId()) == null){
            offersByThing.put(newOffer.getMainThingId(), new ArrayList<OfferItem>());
        }

        offersByThing.get(newOffer.getMainThingId()).add(newOffer);

        //update offers counter

        if (myThingAdapterItems.getValue() != null){
            for (int i = 0; i < myThingAdapterItems.getValue().size(); i++) {
                if (myThingAdapterItems.getValue().get(i).getThingId()
                        .equals(newOffer.getMainThingId()))
                {
                    myThingAdapterItems.getValue().get(i)
                            .setOffersCounter(
                                    myThingAdapterItems.getValue().get(i).getOffersCounter()+1);
                }
            }
        }
//        if (offersByPersonObserver != null){
//
//            if (newOffer.getMainThingId()
//                    .equals(getMyThingAdapterItems().getValue().get(chosenThingPosition).getThingId())){
//
//                offersByPersonObserver.onChange(newOffer);
//
//            }
//        }

    }
//    public void updateOffers(List<OfferItem> offers, int chosenThingPosition){
//        Log.d("@@@", "MyThingsManager.updateOffers.size: "+ offers.size());
//
//        offersByThing = Utils.groupOffersByThings(offers);
//
//        for (String key: offersByThing.keySet()) {
//
//            Log.d("@@@", "MyThingsManager.updateOffers.offersByThing:_____"+key+"_____size___"+offersByThing.get(key).size());
//
//
//        }
//
//
//        //update offers counters
//        for (int i = 0; i < myThingsAdapterItems.size(); i++) {
//            if (offersByThing.containsKey(myThingsAdapterItems.get(i).getThingId())){
//
//
//                Log.d("@@@", "MyThingsManager.updateOffers.offersByThing.containsKey: "+myThingsAdapterItems.get(i).getThingId()+"____");
//                Log.d("@@@", "__Size:"+offersByThing.get(myThingsAdapterItems.get(i).getThingId()).size()+"____");
//
//
//                myThingsAdapterItems.get(i).setOffersCounter(
//                        offersByThing
//                                .get(myThingsAdapterItems.get(i).getThingId())
//                                .size());
//
//
//                if (myThingsChangeObserver != null){
//                    myThingsChangeObserver.onChange(i);
//                }
//
//            }
//        }
//
//
//
//    }



    public ThingEntity updateThing(ExchangeEntity exchange, int status) {
        Log.d("@@@", "MyThingsManager.updateThing");

        ThingEntity result = null;

        for (int i = 0; i < myThings.size(); i++) {

            if (myThings.get(i).getFireBasePath()
                    .equals(exchange.getThing1_path())){

                myThings.get(i).setStatus(status);

                result = myThings.get(i);

                if (myThingAdapterItems.getValue() != null
                        && myThingAdapterItems.getValue().get(i) != null){

                    myThingAdapterItems.getValue().get(i).setType(status);

                    if (status == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){

                        myThingAdapterItems.getValue().get(i).setExchangeImages(
                                new String[] {
                                        exchange.getThing1_img(),
                                        exchange.getThing2_img()});

                    }
                }
//                if (myThingsChangeObserver != null){
//                    myThingsChangeObserver.onChange(i);
//                }
                break;
            }
        }

        return result;
    }

    public void insertThing(ThingEntity newThing){

        if (newThing != null
                &&  myThingAdapterItems.getValue() != null){

            myThings.add(0, newThing);

            myThingAdapterItems.getValue()
                    .add(0,
                            new MyThingsAdapterItem(
                                    newThing.getStatus(),
                                    Utils.fireBasePathToId(newThing.getFireBasePath()),
                                    newThing.getName(),
                                    newThing.getImgBitmap(),
                                    0,
                                    null));

//            if (myThingsChangeObserver != null){
//                myThingsChangeObserver.onChange(-1);
//            }
        }
    }

    public void removeThing(int position) {

        myThings.remove(position);

        if (myThingAdapterItems.getValue() != null){
            myThingAdapterItems.getValue().remove(position);
        }

        if (myThingsChangeObserver != null){
            myThingsChangeObserver.onChange(-1);
        }

    }


    public OfferItem getOfferById(String id){

        for (String thingId : offersByThing.keySet()) {
            for (OfferItem offer : offersByThing.get(thingId)) {
                if (offer.getOfferThingId().equals(id)){
                    return offer;
                }
            }
        }

        return null;
    }

    public void removeOffer(int thingPosition, String thingId, String offerId) {

        if (offersByThing.containsKey(thingId)){

            offersByThing.get(thingId).remove(getOfferById(offerId));


            if (myThingAdapterItems.getValue() != null){
                myThingAdapterItems.getValue().get(thingPosition).setOffersCounter(
                        offersByThing.get(thingId).size());
            }

        }

    }


    public void attachMyThingsChangeObserver(MyThingsChangeObserver myThingsChangeObserver){
        this.myThingsChangeObserver = myThingsChangeObserver;
    }
    public void detachMyThingsChangeObserver(){
        myThingsChangeObserver = null;
    }

    public void attachOffersByThingObserver(String thingId,
                                            OffersChangeObserver offersChangeObserver){
        offersByPersonObserver = offersChangeObserver;

        if (offersByThing.containsKey(thingId)){

            if ((offersByThing.get(thingId) != null)){

                for (OfferItem offerItem :
                        offersByThing.get(thingId)) {

                    offersByPersonObserver.onChange(offerItem);

                }

            } else {

                offersByPersonObserver.onChange(null);

            }
        } else {

            offersByPersonObserver.onChange(null);

        }


    }
    public void detachOffersByPersonObserver(){
        offersByPersonObserver = null;
    }

    public MutableLiveData<List<MyThingsAdapterItem>> getMyThingAdapterItems() {
        return myThingAdapterItems;
    }

    public List<ThingEntity> getMyThings() {
        return myThings;
    }


    public HashMap<String, List<OfferItem>> getOffersByThing() {
        return offersByThing;
    }

    public void changeThing(ThingEntity thingToChange) {

        for (int i = 0; i < myThings.size(); i++) {

            if (myThings.get(i).getId() == thingToChange.getId()){

                myThings.set(i, thingToChange);

                if (myThingAdapterItems.getValue() != null
                        && myThingAdapterItems.getValue().get(i) != null){
                    myThingAdapterItems.getValue().get(i).setName(thingToChange.getName());
                    myThingAdapterItems.getValue().get(i).setImgBitmap(thingToChange.getImgBitmap());

                }
            }
        }
    }


    public void initNewThing(Bitmap bitmap){
        newThing = new NewThingCreation(bitmap);

    }

    public void setNewThingImageUrl(String url){
        if (newThing != null){
            newThing.setUrl(url);
        }
    }

    public NewThingCreation getNewThing(){
        return newThing;
    }


    public interface MyThingsChangeObserver {

        void onChange(int position);

    }

    public interface OffersChangeObserver {

        void onChange(OfferItem newOffer);
    }

}

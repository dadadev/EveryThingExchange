package com.dadabit.everythingexchange.model.firebase;


import com.dadabit.everythingexchange.model.vo.OfferItem;

public interface OffersCallback {

    void onOffersChanged(OfferItem newOffer);

}

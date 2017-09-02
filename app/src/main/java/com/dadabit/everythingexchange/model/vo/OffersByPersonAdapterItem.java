package com.dadabit.everythingexchange.model.vo;


import java.util.List;

public class OffersByPersonAdapterItem {

    private String offererUid;
    private String name;
    private String img;

    private List<OfferItem> offers;

    public OffersByPersonAdapterItem(String offererUid,
                                     String name,
                                     String img,
                                     List<OfferItem> offers) {
        this.offererUid = offererUid;
        this.name = name;
        this.img = img;
        this.offers = offers;
    }

    public String getOffererUid() {
        return offererUid;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public List<OfferItem> getOffers() {
        return offers;
    }

    public void setOffererUid(String offererUid) {
        this.offererUid = offererUid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setOffers(List<OfferItem> offers) {
        this.offers = offers;
    }
}

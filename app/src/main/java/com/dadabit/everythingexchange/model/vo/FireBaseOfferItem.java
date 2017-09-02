package com.dadabit.everythingexchange.model.vo;


public class FireBaseOfferItem {


    private String fireBaseThingPath;
    private long date;

    private String offerThingId;
    private String mainThingId;


    public FireBaseOfferItem(
            String fireBaseThingPath,
            long date) {
        this.fireBaseThingPath = fireBaseThingPath;
        this.date = date;
    }

    public FireBaseOfferItem() {
    }

    public String getFireBaseThingPath() {
        return fireBaseThingPath;
    }

    public long getDate() {
        return date;
    }


    public String getOfferThingId() {
        return offerThingId;
    }

    public String getMainThingId() {
        return mainThingId;
    }


    public void setOfferThingId(String offerThingId) {
        this.offerThingId = offerThingId;
    }

    public void setMainThingId(String mainThingId) {
        this.mainThingId = mainThingId;
    }
}

package com.dadabit.everythingexchange.model.vo;


import android.graphics.Bitmap;

public class MyThingsAdapterItem {

    private int type;
    private String thingId;
    private String name;
    private Bitmap imgBitmap;
    private int offersCounter;
    private String[] exchangeImages;


    public MyThingsAdapterItem(int type,
                               String thingId,
                               String name,
                               Bitmap imgBitmap,
                               int offersCounter,
                               String[] exchangeImages) {
        this.type = type;
        this.thingId = thingId;
        this.name = name;
        this.imgBitmap = imgBitmap;
        this.offersCounter = offersCounter;
        this.exchangeImages = exchangeImages;
    }


    public int getType() {
        return type;
    }

    public String getThingId() {
        return thingId;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public int getOffersCounter() {
        return offersCounter;
    }

    public String[] getExchangeImages() {
        return exchangeImages;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public void setOffersCounter(int offersCounter) {
        this.offersCounter = offersCounter;
    }

    public void setExchangeImages(String[] exchangeImages) {
        this.exchangeImages = exchangeImages;
    }
}

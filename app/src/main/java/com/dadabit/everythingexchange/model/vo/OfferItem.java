package com.dadabit.everythingexchange.model.vo;


public class OfferItem {

    private String offerThingId;
    private String mainThingId;

    private String offererUid;
    private String offererName;
    private String offererImg;

    private String itemThingPath;
    private String itemName;
    private String itemDescription;
    private String itemImgLink;
    private int itemCategory;
    private String itemArea;
    private long date;

    public OfferItem(String offerThingId,
                     String mainThingId,
                     String offererUid,
                     String offererName,
                     String offererImg,
                     String itemThingPath,
                     String itemName,
                     String itemDescription,
                     String itemImgLink,
                     int itemCategory,
                     String itemArea,
                     long date) {
        this.offerThingId = offerThingId;
        this.mainThingId = mainThingId;
        this.offererUid = offererUid;
        this.offererName = offererName;
        this.offererImg = offererImg;
        this.itemThingPath = itemThingPath;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemImgLink = itemImgLink;
        this.itemCategory = itemCategory;
        this.itemArea = itemArea;
        this.date = date;
    }

    public String getOfferThingId() {
        return offerThingId;
    }

    public String getMainThingId() {
        return mainThingId;
    }

    public String getOffererUid() {
        return offererUid;
    }

    public String getOffererName() {
        return offererName;
    }

    public String getOffererImg() {
        return offererImg;
    }

    public String getItemThingPath() {
        return itemThingPath;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemImgLink() {
        return itemImgLink;
    }

    public int getItemCategory() {
        return itemCategory;
    }

    public String getItemArea() {
        return itemArea;
    }

    public long getDate() {
        return date;
    }


    public void setOfferThingId(String offerThingId) {
        this.offerThingId = offerThingId;
    }

    public void setMainThingId(String mainThingId) {
        this.mainThingId = mainThingId;
    }

    public void setOffererUid(String offererUid) {
        this.offererUid = offererUid;
    }

    public void setOffererName(String offererName) {
        this.offererName = offererName;
    }

    public void setOffererImg(String offererImg) {
        this.offererImg = offererImg;
    }

    public void setItemThingPath(String itemThingPath) {
        this.itemThingPath = itemThingPath;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemImgLink(String itemImgLink) {
        this.itemImgLink = itemImgLink;
    }

    public void setItemCategory(int itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemArea(String itemArea) {
        this.itemArea = itemArea;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

package com.dadabit.everythingexchange.model.vo;

public class FireBaseThingItem {

    private String userUid;
    private String userName;
    private String userImg;

    private String itemName;
    private String itemDescription;
    private int itemCategory;

    private String itemCountry;
    private String itemArea;

    private String itemImgLink;
    private String hashTags;

    private int status;
    private long date;


    private String fireBaseID;
    public void setFireBaseID(String fireBaseID) {
        this.fireBaseID = fireBaseID;
    }
    public String getFireBaseID() {
        return fireBaseID;
    }



    public FireBaseThingItem(String userUid,
                             String userName,
                             String userImg,
                             String itemName,
                             String itemDescription,
                             int itemCategory,
                             String itemCountry,
                             String itemArea,
                             String itemImgLink,
                             String hashTags,
                             int status,
                             long date) {
        this.userUid = userUid;
        this.userName = userName;
        this.userImg = userImg;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.itemCountry = itemCountry;
        this.itemArea = itemArea;
        this.itemImgLink = itemImgLink;
        this.hashTags = hashTags;
        this.status = status;
        this.date = date;
    }

    public FireBaseThingItem() {
    }

    public String getUserUid() {
        return userUid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemCategory() {
        return itemCategory;
    }

    public String getItemCountry() {
        return itemCountry;
    }

    public String getItemArea() {
        return itemArea;
    }

    public String getItemImgLink() {
        return itemImgLink;
    }

    public String getHashTags() {
        return hashTags;
    }

    public int getStatus() {
        return status;
    }

    public long getDate() {
        return date;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemCategory(int itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemCountry(String itemCountry) {
        this.itemCountry = itemCountry;
    }

    public void setItemArea(String itemArea) {
        this.itemArea = itemArea;
    }

    public void setItemImgLink(String itemImgLink) {
        this.itemImgLink = itemImgLink;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDate(long date) {
        this.date = date;
    }


}

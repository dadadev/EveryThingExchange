package com.dadabit.everythingexchange.model.vo;


public class User {


    private String uid;
    private String token;
    private String name;
    private String imgUrl;
    private String location;
    private String country;

    public User(String uid, String token, String name, String imgUrl, String location, String country) {
        this.uid = uid;
        this.token = token;
        this.name = name;
        this.imgUrl = imgUrl;
        this.location = location;
        this.country = country;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCountry() {
        return country;
    }

    public String getLocation() {
        return location;
    }

    public String getToken() {
        return token;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setToken(String token) {
        this.token = token;
    }


}

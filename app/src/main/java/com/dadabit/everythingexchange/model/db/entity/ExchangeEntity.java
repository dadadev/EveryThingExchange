package com.dadabit.everythingexchange.model.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "exchanges")
public class ExchangeEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fireBaseId;

    private String chatPath;

    private String thing1_path;
    private String thing1_name;
    private String thing1_img;
    private String thing1_ownerId;
    private String thing1_ownerName;
    private String thing1_ownerImg;

    private String thing2_path;
    private String thing2_name;
    private String thing2_img;
    private String thing2_ownerId;
    private String thing2_ownerName;
    private String thing2_ownerImg;

    private String location;
    private long startDate;
    private long endDate;
    private int status;


    public ExchangeEntity(
            String fireBaseId,
            String chatPath,
            String thing1_path,
            String thing1_name,
            String thing1_img,
            String thing1_ownerId,
            String thing1_ownerName,
            String thing1_ownerImg,
            String thing2_path,
            String thing2_name,
            String thing2_img,
            String thing2_ownerId,
            String thing2_ownerName,
            String thing2_ownerImg,
            String location,
            long startDate,
            long endDate,
            int status) {
        this.fireBaseId = fireBaseId;
        this.chatPath = chatPath;
        this.thing1_path = thing1_path;
        this.thing1_name = thing1_name;
        this.thing1_img = thing1_img;
        this.thing1_ownerId = thing1_ownerId;
        this.thing1_ownerName = thing1_ownerName;
        this.thing1_ownerImg = thing1_ownerImg;
        this.thing2_path = thing2_path;
        this.thing2_name = thing2_name;
        this.thing2_img = thing2_img;
        this.thing2_ownerId = thing2_ownerId;
        this.thing2_ownerName = thing2_ownerName;
        this.thing2_ownerImg = thing2_ownerImg;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @Ignore
    public ExchangeEntity() {
    }

    public int getId() {
        return id;
    }

    public String getChatPath() {
        return chatPath;
    }

    public String getThing1_path() {
        return thing1_path;
    }

    public String getThing1_name() {
        return thing1_name;
    }

    public String getThing1_img() {
        return thing1_img;
    }

    public String getThing1_ownerId() {
        return thing1_ownerId;
    }

    public String getThing1_ownerName() {
        return thing1_ownerName;
    }

    public String getThing1_ownerImg() {
        return thing1_ownerImg;
    }

    public String getThing2_path() {
        return thing2_path;
    }

    public String getThing2_name() {
        return thing2_name;
    }

    public String getThing2_img() {
        return thing2_img;
    }

    public String getThing2_ownerId() {
        return thing2_ownerId;
    }

    public String getThing2_ownerName() {
        return thing2_ownerName;
    }

    public String getThing2_ownerImg() {
        return thing2_ownerImg;
    }

    public String getLocation() {
        return location;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChatPath(String chatPath) {
        this.chatPath = chatPath;
    }

    public void setThing1_path(String thing1_path) {
        this.thing1_path = thing1_path;
    }

    public void setThing1_name(String thing1_name) {
        this.thing1_name = thing1_name;
    }

    public void setThing1_img(String thing1_img) {
        this.thing1_img = thing1_img;
    }

    public void setThing1_ownerId(String thing1_ownerId) {
        this.thing1_ownerId = thing1_ownerId;
    }

    public void setThing1_ownerName(String thing1_ownerName) {
        this.thing1_ownerName = thing1_ownerName;
    }

    public void setThing1_ownerImg(String thing1_ownerImg) {
        this.thing1_ownerImg = thing1_ownerImg;
    }

    public void setThing2_path(String thing2_path) {
        this.thing2_path = thing2_path;
    }

    public void setThing2_name(String thing2_name) {
        this.thing2_name = thing2_name;
    }

    public void setThing2_img(String thing2_img) {
        this.thing2_img = thing2_img;
    }

    public void setThing2_ownerId(String thing2_ownerId) {
        this.thing2_ownerId = thing2_ownerId;
    }

    public void setThing2_ownerName(String thing2_ownerName) {
        this.thing2_ownerName = thing2_ownerName;
    }

    public void setThing2_ownerImg(String thing2_ownerImg) {
        this.thing2_ownerImg = thing2_ownerImg;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFireBaseId() {
        return fireBaseId;
    }

    public void setFireBaseId(String fireBaseId) {
        this.fireBaseId = fireBaseId;
    }


    @Override
    public String toString() {
        return "ExchangeEntity{" +
                "id=" + id +
                ", fireBaseId='" + fireBaseId + '\'' +
                ", chatPath='" + chatPath + '\'' +
                ", thing1_path='" + thing1_path + '\'' +
                ", thing1_name='" + thing1_name + '\'' +
                ", thing1_img='" + thing1_img + '\'' +
                ", thing1_ownerId='" + thing1_ownerId + '\'' +
                ", thing1_ownerName='" + thing1_ownerName + '\'' +
                ", thing1_ownerImg='" + thing1_ownerImg + '\'' +
                ", thing2_path='" + thing2_path + '\'' +
                ", thing2_name='" + thing2_name + '\'' +
                ", thing2_img='" + thing2_img + '\'' +
                ", thing2_ownerId='" + thing2_ownerId + '\'' +
                ", thing2_ownerName='" + thing2_ownerName + '\'' +
                ", thing2_ownerImg='" + thing2_ownerImg + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}

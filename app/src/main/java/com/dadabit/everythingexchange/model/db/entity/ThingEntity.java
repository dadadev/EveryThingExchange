package com.dadabit.everythingexchange.model.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.util.Date;

@Entity(tableName = "things")
public class ThingEntity  {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private int category;
    private Bitmap imgBitmap;
    private Date date;
    private String fireBasePath;
    private String imgLink;
    private String hashTags;
    private String pointerPath;
    private int status;


    public ThingEntity(String name,
                       String description,
                       int category,
                       Bitmap imgBitmap,
                       Date date,
                       String fireBasePath,
                       String imgLink,
                       String hashTags,
                       String pointerPath,
                       int status) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.imgBitmap = imgBitmap;
        this.date = date;
        this.fireBasePath = fireBasePath;
        this.imgLink = imgLink;
        this.hashTags = hashTags;
        this.pointerPath = pointerPath;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public Date getDate() {
        return date;
    }

    public String getFireBasePath() {
        return fireBasePath;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getHashTags() {
        return hashTags;
    }

    public String getPointerPath() {
        return pointerPath;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFireBasePath(String fireBasePath) {
        this.fireBasePath = fireBasePath;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public void setPointerPath(String pointerPath) {
        this.pointerPath = pointerPath;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

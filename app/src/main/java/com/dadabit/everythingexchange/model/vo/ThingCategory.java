package com.dadabit.everythingexchange.model.vo;

import android.graphics.Bitmap;

public class ThingCategory {
    private int id;
    private String name;
    private Bitmap imgBitmap;

    public ThingCategory(int id, String name, Bitmap imgBitmap) {
        this.id = id;
        this.name = name;
        this.imgBitmap = imgBitmap;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }
}
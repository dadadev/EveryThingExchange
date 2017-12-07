package com.dadabit.everythingexchange.model.vo;


import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;

public class NewThingCreation {

    private Bitmap bitmap;

    private MutableLiveData<String> url;


    public NewThingCreation(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public MutableLiveData<String>  getUrl() {
        return url == null ? url = new MutableLiveData<>() : url;
    }

    public void setUrl(String imgLink) {
        if (url == null){
            url = new MutableLiveData<>();
        }
        url.setValue(imgLink);
    }
}

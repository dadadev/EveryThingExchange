package com.dadabit.everythingexchange.model.db.converter;


import android.arch.persistence.room.TypeConverter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageConverter {
    @TypeConverter
    public static Bitmap toBitmap(byte[] img) {
        return img == null ? null : BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    @TypeConverter
    public static byte[] toByteArray(Bitmap bitmap) {
        if (bitmap==null){
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}

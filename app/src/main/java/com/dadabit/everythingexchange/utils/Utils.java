package com.dadabit.everythingexchange.utils;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.FireBaseOfferItem;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.model.vo.OffersByPersonAdapterItem;
import com.google.firebase.database.DataSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String timestampToString(long date) {

        String result;
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        result = dateFormat.format(calendar.getTime());

        return result;

    }

    public static String fireBasePathToId(String fireBasePathI) {
        final String[] splitPath = fireBasePathI.split("/");
        return splitPath[splitPath.length - 1];
    }

    public static ExchangeEntity revertExchangeEntity(ExchangeEntity exchange){
        ExchangeEntity result =
                new ExchangeEntity(
                        exchange.getFireBaseId(),
                        exchange.getChatPath(),
                        exchange.getThing2_path(),
                        exchange.getThing2_name(),
                        exchange.getThing2_img(),
                        exchange.getThing2_ownerId(),
                        exchange.getThing2_ownerName(),
                        exchange.getThing2_ownerImg(),
                        exchange.getThing1_path(),
                        exchange.getThing1_name(),
                        exchange.getThing1_img(),
                        exchange.getThing1_ownerId(),
                        exchange.getThing1_ownerName(),
                        exchange.getThing1_ownerImg(),
                        exchange.getLocation(),
                        exchange.getStartDate(),
                        exchange.getEndDate(),
                        exchange.getStatus());
        result.setId(exchange.getId());
        return result;
    }

    public static List<FireBaseOfferItem> convertSnapshotToOffers(DataSnapshot dataSnapshot){
        Log.d("@@@", "Utils.convertSnapshotToOffers");

        List<FireBaseOfferItem> result = new ArrayList<>();
        String thingId;

        for (DataSnapshot userThingsSnapshot : dataSnapshot.getChildren()) {

            thingId = userThingsSnapshot.getKey();

            for (DataSnapshot offererSnapshot : userThingsSnapshot.getChildren()) {

                for (DataSnapshot offerSnapshot : offererSnapshot.getChildren()) {

                    FireBaseOfferItem offer =
                            offerSnapshot.getValue(FireBaseOfferItem.class);

                    offer.setOfferThingId(offerSnapshot.getKey());
                    offer.setMainThingId(thingId);

                    result.add(offer);

                }
            }
        }

        return result;
    }

//    public static HashMap<String, List<OfferItem>> convertSnapshotToRawOffersItems(DataSnapshot dataSnapshot){
//
//        HashMap<String, List<OfferItem>> result = new HashMap<>();
//        String thingId;
//        String offererUid;
//
//        for (DataSnapshot userThingsSnapshot : dataSnapshot.getChildren()) {
//            thingId = userThingsSnapshot.getKey();
//            result.put(thingId, new ArrayList<OfferItem>());
//
//            for (DataSnapshot offererSnapshot : userThingsSnapshot.getChildren()) {
//
//                offererUid = offererSnapshot.getKey();
//
//                List<OfferThing> things = new ArrayList<>();
//
//                String offererName = null;
//                String offererImg = null;
//
//                for (DataSnapshot offersSnapshot : offererSnapshot.getChildren()) {
//
//                    FireBaseOfferItem offer =
//                            offersSnapshot.getValue(FireBaseOfferItem.class);
//
//                    things.add(
//                            new OfferThing(
//                                    offersSnapshot.getKey(),
//                                    offer.getFireBaseThingPath()));
//
//                    if (offererName == null && offererImg == null){
//                        offererName = offer.getOffererName();
//                        offererImg = offer.getOffererImg();
//                    }
//
//                }
//
//                if (things.size()>0){
//                    result.get(thingId).add(
//                            new OfferItem(
//                                    offererUid,
//                                    offererName,
//                                    offererImg,
//                                    things));
//
//                }
//            }
//        }
//
//        return result;
//    }

//    public static HashMap<String, List<FireBaseOfferItem>> convertSnapshotToOffersByThings(DataSnapshot dataSnapshot){
//
//        HashMap<String, List<FireBaseOfferItem>> result = new HashMap<>();
//        String thingId;
//
//        for (DataSnapshot userThingsSnapshot : dataSnapshot.getChildren()) {
//
//            thingId = userThingsSnapshot.getKey();
//            result.put(thingId, new ArrayList<FireBaseOfferItem>());
//
//            for (DataSnapshot offererSnapshot : userThingsSnapshot.getChildren()) {
//                for (DataSnapshot offersSnapshot : offererSnapshot.getChildren()) {
//
//                    FireBaseOfferItem offer =
//                            offersSnapshot.getValue(FireBaseOfferItem.class);
//
//                    offer.setId(offersSnapshot.getKey());
//
//                    result.get(thingId).add(offer);
//
//                }
//            }
//        }
//
//        return result;
//    }

    public static HashMap<String, List<OfferItem>> groupOffersByThings(List<OfferItem> offers) {


        Log.d("@@@", "Utils.groupOffersByThings.size: "+ offers.size());

        HashMap<String, List<OfferItem>> result = new HashMap<>();

        for (OfferItem offer : offers) {
            Log.d("@@@", "--MAIN_THING--"+ offer.getMainThingId()+"--ITEM_NAME--"+ offer.getItemName()+"----");
            if ( !result.containsKey(offer.getMainThingId())){

                result.put(offer.getMainThingId(), new ArrayList<OfferItem>());

            }

            result.get(offer.getMainThingId()).add(offer);
        }
        return result;
    }

    public static HashMap<String, List<OfferItem>> getOffersByPerson(List<OfferItem> offers) {

        HashMap<String, List<OfferItem>> result = null;

        if (offers != null){
            result = new HashMap<>();
            for (OfferItem offer : offers) {
                if ( !result.containsKey(offer.getOffererUid())){

                    result.put(offer.getOffererUid(), new ArrayList<OfferItem>());

                }
                result.get(offer.getOffererUid()).add(offer);
            }
        }

        return result;
    }

    public static SparseArray<String[]> getExchangesImgs(List<ThingEntity> things, List<ExchangeEntity> exchanges) {

        SparseArray<String[]> result = new SparseArray<>();

        for (int i = 0; i < things.size(); i++) {
            if (things.get(i).getStatus() == Constants.THING_STATUS_EXCHANGING_IN_PROCESS){
                for (int j = 0; j < exchanges.size(); j++) {
                    if (exchanges.get(j).getThing1_path().equals(things.get(i).getFireBasePath())){
                        result.put(i, new String[] {
                                exchanges.get(j).getThing1_img(),
                                exchanges.get(j).getThing2_img()});
                    }
                }
            }
        }

        HashMap<String, Integer> map = new HashMap<>();
        return result;
    }

    public static int[] convertOffersToCounters(
            HashMap<String, List<OfferItem>> offersByThing,
            List<ThingEntity> things){

        int[] result = new int[things.size()];
        String thingId;

        for (int i = 0; i < things.size(); i++) {

            thingId = fireBasePathToId(things.get(i).getFireBasePath());

            if (offersByThing.containsKey(thingId)){

                result[i] = offersByThing.get(thingId).size();

            }

        }

        return result;
    }


    public static HashMap<String,List<FireBaseOfferItem>> snapshotToOffersMap(DataSnapshot dataSnapshot) {
        HashMap<String,List<FireBaseOfferItem>> result = new HashMap<>();
        String thingId;

        if (dataSnapshot != null){

            for (DataSnapshot userThingsSnapshot : dataSnapshot.getChildren()) {
                thingId = userThingsSnapshot.getKey();
                for (DataSnapshot offererSnapshot : userThingsSnapshot.getChildren()) {
                    for (DataSnapshot offersSnapshot : offererSnapshot.getChildren()) {

                        FireBaseOfferItem offer =
                                offersSnapshot.getValue(FireBaseOfferItem.class);

                        offer.setOfferThingId(offersSnapshot.getKey());
                        offer.setMainThingId(thingId);

                        if (!result.containsKey(thingId)){
                            result.put(thingId, new ArrayList<FireBaseOfferItem>());
                        }
                        result.get(thingId).add(offer);

                    }
                }
            }
        }


        return result;
    }


    public static List<OffersByPersonAdapterItem> getOffersByPersonAdapterItems(List<OfferItem> offerEntities) {

        List<OffersByPersonAdapterItem> result = new ArrayList<>();
        HashMap<String, List<OfferItem>> offersByPerson = getOffersByPerson(offerEntities);

        if (offersByPerson != null){

            for (String offererUid : offersByPerson.keySet()) {

                result.add(
                        new OffersByPersonAdapterItem(
                                offererUid,
                                offersByPerson.get(offererUid).get(0).getOffererName(),
                                offersByPerson.get(offererUid).get(0).getOffererImg(),
                                offersByPerson.get(offererUid)));
            }
        }
        return result;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static byte[] resizeImage(byte[] input) {
        Bitmap original = BitmapFactory.decodeByteArray(input , 0, input.length);
//        Bitmap resized = Bitmap.createScaledBitmap(original, RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT, true);
        Bitmap resized = Bitmap.createScaledBitmap(
                original,
                (int)(original.getWidth()*0.8),
                (int)(original.getHeight()*0.8),
                true);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return blob.toByteArray();
    }

    public static Bitmap getBitmap(Uri uri, Context context) throws IOException {

        BitmapFactory.Options options=new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        inputStream.close();

        return bitmap;
    }

    public static Bitmap getImageBitmap(Context context, Uri photoUri) throws IOException {
        int MAX_IMAGE_DIMENSION = 400;
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

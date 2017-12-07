package com.dadabit.everythingexchange.model;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.firebase.ImageUploader;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.ThingCategory;
import com.dadabit.everythingexchange.model.vo.ThingPath;
import com.dadabit.everythingexchange.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class AddThingActivityRepo {

    private Bitmap imgBitmap;

    private String name;
    private String description;
    private int chosenCategory = -1;

    private List<String> hashTagsList;

    private ThingEntity thingToChange;

    @Inject Repository mainRepository;

    private SaveThingCallback successCallback;


    public AddThingActivityRepo() {
        Log.d("@@@", "AddThingActivityRepo.create");
        App.getComponent().inject(this);
    }


    public void saveThing(SaveThingCallback mCallback) {

        successCallback = mCallback;

        if (thingToChange == null){

            uploadImage();

        } else {

            checkThingChanges();

        }
    }

    private void checkThingChanges() {

        if (imgBitmap != null){

            changeUploadedImage();

        } else {

            changeFireBaseThing(
                    new FireBaseThingItem(
                            mainRepository.getUser().getUid(),
                            mainRepository.getUser().getName(),
                            mainRepository.getUser().getImgUrl(),
                            name,
                            description,
                            chosenCategory,
                            mainRepository.getUser().getCountry(),
                            mainRepository.getSharedPreferences().getHomeLocation(),
                            thingToChange.getImgLink(),
                            getHashTags(),
                            Constants.THING_STATUS_AVAILABLE,
                            System.currentTimeMillis())
            );

        }







    }

    private void changeFireBaseThing(final FireBaseThingItem fireBaseThingItem) {


        mainRepository.getHandler().post(new Runnable() {
            @Override
            public void run() {

                thingToChange.setName(fireBaseThingItem.getItemName());
                thingToChange.setDescription(fireBaseThingItem.getItemDescription());
                thingToChange.setCategory(fireBaseThingItem.getItemCategory());
                thingToChange.setHashTags(fireBaseThingItem.getHashTags());

                mainRepository.getFireBaseManager()
                        .sendThing(
                                thingToChange.getFireBasePath(),
                                fireBaseThingItem);

                mainRepository.getDb().myThingDao().updateThing(thingToChange);

                mainRepository.getMyThingsManager().changeThing(thingToChange);

                successCallback.onSuccess(true);

            }
        });




    }

    private void changeUploadedImage() {

    }


    private void uploadImage() {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        final byte[] imgBlob = stream.toByteArray();
        final long timeStamp = System.currentTimeMillis();

        new ImageUploader(
                imgBlob,
                mainRepository.getUser().getUid(),
                timeStamp,
                new ImageUploader.OnFinishListener() {
                    @Override
                    public void onFinish(String imgLink) {
                        Log.d("@@@", "AddThingPresenter.uploadToFireBase.onSuccess");

                        if (imgLink != null && imgLink.length() > 0){

                            sendFireBaseThing(
                                    new FireBaseThingItem(
                                            mainRepository.getUser().getUid(),
                                            mainRepository.getUser().getName(),
                                            mainRepository.getUser().getImgUrl(),
                                            name,
                                            description,
                                            chosenCategory,
                                            mainRepository.getUser().getCountry(),
                                            mainRepository.getSharedPreferences().getHomeLocation(),
                                            imgLink,
                                            getHashTags(),
                                            Constants.THING_STATUS_AVAILABLE,
                                            timeStamp));


                        } else {
                            successCallback.onSuccess(false);
                        }
                    }
                })
                .send();

    }

    private void sendFireBaseThing(FireBaseThingItem thing) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String thingPath = String.format(Locale.getDefault(),
                "things/%s/%s/%d/%s",
                thing.getItemCountry(),
                thing.getItemArea(),
                thing.getItemCategory(),
                databaseReference
                        .child("things")
                        .child(thing.getItemCountry())
                        .child(thing.getItemArea())
                        .child(thing.getItemCategory() + "")
                        .push()
                        .getKey());

        final String thingPointerPath = String.format(Locale.getDefault(),
                "users/%s/things/%s",
                thing.getUserUid(),
                databaseReference
                        .child("users")
                        .child(thing.getUserUid())
                        .child("things")
                        .push()
                        .getKey());

        databaseReference
                .child(thingPath)
                .setValue(thing);

        databaseReference
                .child(thingPointerPath)
                .setValue(new ThingPath(thingPath));

        Log.d("@@@", "AddThingPresenter.uploadToDB.FireBaseItemSaved");

        new AsyncTask<ThingEntity, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(ThingEntity... params) {

                int thingId =
                        (int) mainRepository.getDb().myThingDao()
                                .insertThing(params[0]);

                params[0].setId(thingId);

                mainRepository.getMyThingsManager().insertThing(params[0]);

                mainRepository.getState().setNewThingAdd(true);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSaved) {
                super.onPostExecute(isSaved);

                successCallback.onSuccess(isSaved);
            }
        }.execute(new ThingEntity(
                name,
                description,
                chosenCategory,
                imgBitmap,
                new Date(thing.getDate()),
                thingPath,
                thing.getItemImgLink(),
                thing.getHashTags(),
                thingPointerPath,
                thing.getStatus()));


    }



    private String getHashTags() {
        if (hashTagsList == null){
            return null;
        } else {
            StringBuilder result = new StringBuilder();

            for (String hashTag :
                    hashTagsList) {
                result.append(hashTag);
            }
            return result.toString();
        }
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChosenCategory(int chosenCategory) {
        this.chosenCategory = chosenCategory;
    }

    public void setHashTag(String hashTag){
        if (hashTagsList == null){
            hashTagsList = new ArrayList<>();
        }
        hashTagsList.add(0, hashTag);
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }



    public String getName() {
        return name;
    }

    public int getChosenCategory() {
        return chosenCategory;
    }

    public List<String> getHashTagsList() {
        return hashTagsList;
    }

    public void setThingToChange(int thingId) {

        for (ThingEntity thing :
                mainRepository.getMyThingsManager().getMyThings()) {

            if (thing.getId() == thingId){

                thingToChange = thing;

                break;
            }

        }
    }

    public ThingEntity getThingToChange() {
        return thingToChange;
    }

    public interface SaveThingCallback {

        void onSuccess(boolean isSaved);
    }
}

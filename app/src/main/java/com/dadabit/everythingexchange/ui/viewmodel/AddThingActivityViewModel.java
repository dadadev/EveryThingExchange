package com.dadabit.everythingexchange.ui.viewmodel;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.ThingCategory;
import com.dadabit.everythingexchange.model.vo.ThingPath;
import com.dadabit.everythingexchange.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddThingActivityViewModel extends ViewModel{


    private Repository mRepository;

    private MutableLiveData<Bitmap> imgBitmap;

    private MutableLiveData<Integer> chosenCategory;

    private MutableLiveData<FireBaseThingItem> newThingLiveData;

    private List<String> hashTagsList;

    private List<ThingCategory> categories;

    private boolean isSaved;


    public AddThingActivityViewModel(Repository mRepository) {
        Log.d("@@@", "AddThingActivityViewModel.create");
        this.mRepository = mRepository;
    }


    public MutableLiveData<Bitmap> getImage(){
        if (imgBitmap == null){
            imgBitmap = new MutableLiveData<>();
            imgBitmap.setValue(mRepository.getMyThingsManager().getNewThing().getBitmap());
        }

        return imgBitmap;
    }

    public MutableLiveData<String> getImageUrl(){
        return mRepository.getMyThingsManager().getNewThing().getUrl();
    }


    public void setChosenCategory(int category){

        if (chosenCategory == null){
            chosenCategory = new MutableLiveData<>();
        }
        chosenCategory.setValue(category);

        FireBaseThingItem thingItem = newThingLiveData.getValue();

        if (thingItem != null){
            thingItem.setItemCategory(category);
            newThingLiveData.setValue(thingItem);
        }
    }

    public MutableLiveData<Integer> getChosenCategory(){
        return chosenCategory == null ? chosenCategory = new MutableLiveData<>() : chosenCategory;
    }

    public List<ThingCategory> getCategories() {
        if (categories == null){
            categories = mRepository.getCategoriesLive().getValue();
        }
        return categories;
    }

    public MutableLiveData<FireBaseThingItem> getNewFireBaseThing(){
//        if (newThingLiveData == null){
////            initNewFirebaseThing();
//            newThingLiveData = new MutableLiveData<>();
//        }
        return newThingLiveData;

    }

    public void createNewThing(){
        newThingLiveData = new MutableLiveData<>();
    }

    public void initNewFirebaseThing() {
        Log.d("@@@", "AddThingActivityViewModel.initNewFireBaseThing");

        if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.e("@@@", "--------UI TREAD");
        } else {
            Log.e("@@@", "--------BACKGROUND TREAD");

        }

        FireBaseThingItem newFireBaseThingItem = new FireBaseThingItem();

        newFireBaseThingItem.setUserUid(mRepository.getUser().getUid());
        newFireBaseThingItem.setUserName(mRepository.getUser().getName());
        newFireBaseThingItem.setUserImg(mRepository.getUser().getImgUrl());
        newFireBaseThingItem.setItemCountry(mRepository.getUser().getCountry());
        newFireBaseThingItem.setItemArea(mRepository.getSharedPreferences().getHomeLocation());
        newFireBaseThingItem.setStatus(Constants.THING_STATUS_AVAILABLE);
        newFireBaseThingItem.setItemCategory(-1);

        newThingLiveData.postValue(newFireBaseThingItem);


    }


    public void setImageUrl(String url){

        FireBaseThingItem newFireBaseThingItem = getNewFireBaseThing().getValue();

        if (newFireBaseThingItem != null
                && newFireBaseThingItem.getItemImgLink() == null){
            newFireBaseThingItem.setItemImgLink(url);

            newThingLiveData.setValue(newFireBaseThingItem);
        }
    }


    public void setHashTag(String hashTag){
        if (hashTagsList == null){
            hashTagsList = new ArrayList<>();
        }
        hashTagsList.add(0, hashTag);
    }

    public List<String> getHashTagsList() {
        return hashTagsList;
    }






    public boolean isReadyToSave() {
        FireBaseThingItem fireBaseThingItem = newThingLiveData.getValue();
        return fireBaseThingItem != null
                && fireBaseThingItem.getItemCategory() != -1
                && fireBaseThingItem.getItemName() != null
                && fireBaseThingItem.getItemDescription() != null;

    }

    public boolean isThingSaved() {
        return isSaved;
    }

    public void saveThing() {
        Log.d("@@@", "AddThingActivityViewModel.saveThing");

        FireBaseThingItem thing = newThingLiveData.getValue();

        if (thing != null){

            thing.setDate(System.currentTimeMillis());

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

            ThingEntity thingEntity =
                    new ThingEntity(
                            thing.getItemName(),
                            thing.getItemDescription(),
                            thing.getItemCategory(),
                            imgBitmap.getValue(),
                            new Date(thing.getDate()),
                            thingPath,
                            thing.getItemImgLink(),
                            thing.getHashTags(),
                            thingPointerPath,
                            thing.getStatus()
                    );

            int thingId =
                    (int) mRepository.getDb().myThingDao()
                            .insertThing(thingEntity);

            thingEntity.setId(thingId);

            mRepository.getMyThingsManager().insertThing(thingEntity);

            mRepository.getState().setNewThingAdd(true);

            isSaved = true;

            newThingLiveData.setValue(thing);



        }




    }

    public void setName(String name) {
        Log.d("@@@", "AddThingActivityViewModel.setName: "+name);

        FireBaseThingItem thingItem = newThingLiveData.getValue();

        if (thingItem != null){
            thingItem.setItemName(name);
            newThingLiveData.setValue(thingItem);
        }

    }

    public void setDescription(String description) {
        Log.d("@@@", "AddThingActivityViewModel.setDescription: "+description);

        FireBaseThingItem thingItem = newThingLiveData.getValue();

        if (thingItem != null){
            thingItem.setItemDescription(description);
            newThingLiveData.setValue(thingItem);
        }
    }

    public boolean isCategoryChosen() {

        return chosenCategory != null
                && chosenCategory.getValue() != null
                && chosenCategory.getValue() != -1;
    }
}

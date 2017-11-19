package com.dadabit.everythingexchange.model;


import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.db.AppDatabase;
import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;
import com.dadabit.everythingexchange.model.firebase.ExchangesListener;
import com.dadabit.everythingexchange.model.firebase.FireBaseManager;
import com.dadabit.everythingexchange.model.firebase.ImageUploader;
import com.dadabit.everythingexchange.model.firebase.LoadFireBaseUserCallback;
import com.dadabit.everythingexchange.model.firebase.LoadThingsCallback;
import com.dadabit.everythingexchange.model.firebase.NewChatMessageCallback;
import com.dadabit.everythingexchange.model.firebase.OffersCallback;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.model.vo.ChatMessage;
import com.dadabit.everythingexchange.model.vo.FireBaseExchangeItem;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.model.vo.MainActivityState;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.model.vo.ThingCategory;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.utils.ChatItemsManager;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.MyThingsManager;
import com.dadabit.everythingexchange.utils.SharedPreferencesManager;
import com.dadabit.everythingexchange.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {

    private Context appContext;
    private AppDatabase db;
    private SharedPreferencesManager sharedPrefs;

    private FireBaseManager mFireBaseManager;
    private ChatItemsManager mChatItemsManager;
    private MyThingsManager myThingsManager;

    private User user;

    private MainActivityState activityState;

    private List<ThingCategory> categories;
    private List<Bitmap> backgroundImages;

    private List<ExchangeEntity> exchanges;
    private List<String> locations;


    private List<FireBaseThingItem> thingItems;
    private int thingItemsCategory;


    private int chosenChat;


    private HandlerThread mThread;
    private Handler mHandler;


    private MutableLiveData<List<ThingCategory>> categoriesLive;



    @Inject
    public Repository(AppDatabase db, SharedPreferencesManager sharedPrefs, Context appContext) {
        Log.d("@@@", "Repository.create");
        this.db = db;
        this.sharedPrefs = sharedPrefs;
        this.appContext = appContext;

        init();
    }


    public void init(){

        user = sharedPrefs.getUser();

        if (user != null){
            Log.d("@@@", "Repository.init");

//            loadCategories();

            activityState = new MainActivityState();

            categoriesLive = new MutableLiveData<>();

            mFireBaseManager = new FireBaseManager();
            mChatItemsManager = new ChatItemsManager(user.getUid());
            myThingsManager = new MyThingsManager();

            mThread = new HandlerThread(
                    getClass().getName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mThread.start();

            mHandler = new Handler(mThread.getLooper());

            mHandler.post(asyncInitiation());

        }
    }

    private Runnable asyncInitiation(){
        return new Runnable() {
            @Override
            public void run() {

                Log.d("@@@", "Repository.AsyncInitiation.start");

                loadLiveCategories();

                exchanges = db.exchangeDao().loadAllExchanges();

                mChatItemsManager.initChatItems(
                        exchanges,
                        sharedPrefs.getAllCounters(exchanges));

                myThingsManager.init(
                        db.myThingDao().loadAllThings(),
                        exchanges);

                mFireBaseManager.initChatsListeners(
                        exchanges,
                        newChatMessageListener);

                mFireBaseManager.initExchangesListener(
                        String.format("users/%s/exchanges", user.getUid()),
                        exchangesListener);

                mFireBaseManager.initOffersListener(
                        String.format("users/%s/offers", user.getUid()),
                        offersListener);


                //Check token change
                if ( sharedPrefs.isTokenChanged(
                        FirebaseInstanceId.getInstance().getToken())){

                    user.setToken(sharedPrefs.getUser().getToken());
//                    //update user token in FireBase
                    HashMap<String, Object> updateMap = new HashMap<>();
                    updateMap.put(
                            String.format("users/%s/metadata/token", user.getUid()),
                            user.getToken());

                    mFireBaseManager.updateValues(updateMap);
                }

                mFireBaseManager.loadUserInfo(
                        String.format("users/%s/metadata", user.getUid()),
                        new LoadFireBaseUserCallback() {
                            @Override
                            public void onLoaded(User fireBaseUser) {

                                if (fireBaseUser == null
                                        || ! user.getToken().equals(fireBaseUser.getToken())){
                                    Log.d("@@@", "Repository.sendUserInfo");

                                    mFireBaseManager.sendUserInfo(user);
                                }
                            }
                        });


                loadLocations(null);


                Log.d("@@@", "Repository.AsyncInitiation.stop");
            }
        };
    }



    private ExchangesListener exchangesListener = new ExchangesListener() {
        @Override
        public void onNew(final ExchangeEntity newExchange) {
            Log.d("@@@", "Repository.ExchangesListener.onNew: "+ newExchange.toString());

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    addNewExchange(newExchange);
                }
            });
        }

        @Override
        public void onDateChange(final String fireBaseId, final long newDate) {
            Log.d("@@@", "Repository.ExchangesListener.onDateChange");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    changeExchangeDate(fireBaseId, newDate);
                }
            });

        }

        @Override
        public void onCancelExchange(final String fireBaseId) {
            Log.d("@@@", "Repository.ExchangesListener.onCancelExchange");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    cancelExchange(fireBaseId);
                }
            });

        }

        @Override
        public void onEndExchange(final String fireBaseId) {
            Log.d("@@@", "Repository.ExchangesListener.onEndExchange");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    endExchange(fireBaseId);
                }
            });

        }
    };



    private NewChatMessageCallback newChatMessageListener = new NewChatMessageCallback() {
        @Override
        public void onNewMessage(int position, ChatMessage message) {
            Log.d("@@@", "Repository.newChatMessageListener.onNewMessage: "+message.getMessage());

            if (exchanges.get(position) != null){

                mChatItemsManager.addMessage(
                        exchanges.get(position).getId(),
                        message);
            }
        }
    };

    private OffersCallback offersListener = new OffersCallback() {
        @Override
        public void onOffersChanged(final OfferItem newOffer) {
            Log.d("@@@", "Repository.offersCallback.onChange.newOffer: "+newOffer.getItemName());

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    myThingsManager.addNewOffer(newOffer, getState().getChosenMyThing());
                }
            });

        }
    };


    private void loadLiveCategories() {

        Log.d("@@@", "REPO.loadCategories.start");

        final List<ThingCategory> categories = new ArrayList<>();
        List<Bitmap> backgroundImages = new ArrayList<>();

        TypedArray imgIds = appContext.getResources().obtainTypedArray(R.array.img_categories);
        String [] names = appContext.getResources().getStringArray(R.array.categories);

        for (int i = 0; i < imgIds.length(); i++) {

            backgroundImages.add(BitmapFactory.decodeResource(
                    appContext.getResources(),
                    imgIds.getResourceId(i, -1)));

            categories.add(
                    new ThingCategory(
                            i,
                            names[i],
                            backgroundImages.get(i)));
        }
        imgIds.recycle();


        categoriesLive.postValue(categories);


        Log.d("@@@", "REPO.loadCategories.end");

    }

    public MutableLiveData<List<ThingCategory>> getCategoriesLive() {
        return categoriesLive;
    }

    private void loadCategories(){
        backgroundImages = new ArrayList<>();
        categories = new ArrayList<>();
        TypedArray imgIds = appContext.getResources().obtainTypedArray(R.array.img_categories);
        String [] names = appContext.getResources().getStringArray(R.array.categories);

        for (int i = 0; i < imgIds.length(); i++) {

            backgroundImages.add(BitmapFactory.decodeResource(
                    appContext.getResources(),
                    imgIds.getResourceId(i, -1)));

            categories.add(
                    new ThingCategory(
                            i,
                            names[i],
                            backgroundImages.get(i)));
        }
        imgIds.recycle();
    }

    public MutableLiveData<List<ThingCategory>> loadThingCategories(){
        Log.d("@@@", "REPO.loadCategories.init");

        final MutableLiveData<List<ThingCategory>> result = new MutableLiveData<>();

        Log.d("@@@", "REPO.loadCategories.start");

        List<ThingCategory> categories = new ArrayList<>();
        List<Bitmap> backgroundImages = new ArrayList<>();

        TypedArray imgIds = appContext.getResources().obtainTypedArray(R.array.img_categories);
        String [] names = appContext.getResources().getStringArray(R.array.categories);

        for (int i = 0; i < imgIds.length(); i++) {

            backgroundImages.add(BitmapFactory.decodeResource(
                    appContext.getResources(),
                    imgIds.getResourceId(i, -1)));

            categories.add(
                    new ThingCategory(
                            i,
                            names[i],
                            backgroundImages.get(i)));
        }
        imgIds.recycle();

        result.setValue(categories);

        Log.d("@@@", "REPO.loadCategories.end");


//        getHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("@@@", "REPO.loadCategories.start");
//
//                final List<ThingCategory> categories = new ArrayList<>();
//                List<Bitmap> backgroundImages = new ArrayList<>();
//
//                TypedArray imgIds = appContext.getResources().obtainTypedArray(R.array.img_categories);
//                String [] names = appContext.getResources().getStringArray(R.array.categories);
//
//                for (int i = 0; i < imgIds.length(); i++) {
//
//                    backgroundImages.add(BitmapFactory.decodeResource(
//                            appContext.getResources(),
//                            imgIds.getResourceId(i, -1)));
//
//                    categories.add(
//                            new ThingCategory(
//                                    i,
//                                    names[i],
//                                    backgroundImages.get(i)));
//                }
//                imgIds.recycle();
//
//
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("@@@", "REPO.loadCategories.postToUi");
//
//                        result.setValue(categories);
//                    }
//                });
//
//                Log.d("@@@", "REPO.loadCategories.end");
//            }
//        });

        return result;
    }


    public void loadFireBaseThings(final int category, final LoadThingsCallback mCallback) {

        if (thingItems != null
                && activityState.getChosenCategory() == thingItemsCategory
                && user.getLocation().equals(activityState.getChosenLocation())){
            Log.d("@@@", "MainRepository.loadFireBaseThings.fromCache ");

            mCallback.onLoaded(true);

        } else {

            mFireBaseManager.loadFireBaseThings(
                    String.format(Locale.getDefault(), "things/%s/%s/%d",
                            user.getCountry(),
                            user.getLocation(),
                            category),
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            Log.d("@@@", "MainRepository.FireBaseDatabase.onDataChange.count: "+dataSnapshot.getChildrenCount());

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    thingItems = new ArrayList<>();
                                    thingItemsCategory = activityState.getChosenCategory();
                                    activityState.setChosenLocation(user.getLocation());

                                    for (DataSnapshot snapshot :
                                            dataSnapshot.getChildren()) {

                                        FireBaseThingItem item = snapshot.getValue(FireBaseThingItem.class);
                                        item.setFireBaseID(snapshot.getKey());

                                        if (item.getStatus() != Constants.THING_STATUS_EXCHANGING_IN_PROCESS
                                                && item.getUserUid() != null
                                                && !item.getUserUid().equals(getUser().getUid())){

                                            thingItems.add(item);
                                        }


                                    }
                                    Collections.reverse(thingItems);

                                    mCallback.onLoaded(true);


                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("@@@", "MainRepository.FireBaseDatabase.onCancelled: "+databaseError.getDetails());

                            mCallback.onLoaded(false);

                        }
                    });

        }
    }


    private void addNewExchange(ExchangeEntity exchange) {
        Log.d("@@@", "Repository.addNewExchange");

        exchange.setId(
                (int) db.exchangeDao().insertExchange(exchange));

        exchanges.add(exchange);


        db.myThingDao().updateThing(
                myThingsManager.updateThing(exchange, Constants.THING_STATUS_EXCHANGING_IN_PROCESS));


        mChatItemsManager.addChatItem(
                new ChatItem(
                        exchange.getId(),
                        exchange.getThing2_ownerImg(),
                        exchange.getThing2_ownerName(),
                        exchange.getThing1_ownerImg(),
                        exchange.getThing1_ownerName(),
                        exchange.getThing1_img(),
                        exchange.getThing2_img(),0));

        mFireBaseManager.addChatListener(
                exchange.getChatPath(),
                exchanges.size() - 1);


        HashMap<String, Object> updatedValues = new HashMap<>();
        updatedValues.put(
                String.format("users/%s/exchanges/%s/status",
                        getUser().getUid(),
                        exchange.getFireBaseId()),
                Constants.EXCHANGE_STATUS_SAVED);

        mFireBaseManager.updateValues(updatedValues);

        mFireBaseManager.sendExchange(
                String.format("users/%s/exchanges/%s",
                        exchange.getThing2_ownerId(),
                        exchange.getFireBaseId()),
                new FireBaseExchangeItem(
                        exchange.getChatPath(),
                        exchange.getThing2_path(),
                        exchange.getThing1_path(),
                        exchange.getLocation(),
                        exchange.getStartDate(),
                        exchange.getEndDate(),
                        Constants.EXCHANGE_STATUS_SAVED));


    }




    private void changeExchangeDate(String fireBaseId, long newDate) {

        for (ExchangeEntity exchange : exchanges) {
            if (exchange.getFireBaseId().equals(fireBaseId)
                    && exchange.getEndDate() != newDate) {
                Log.d("@@@", "Repository.changeExchangeEndDate");

                exchange.setEndDate(newDate);

                db.exchangeDao().updateExchange(exchange);

                mChatItemsManager.changeChatEndDate(
                        exchange.getId(),
                        exchange.getEndDate());

                HashMap<String, Object> updatedValues = new HashMap<>();
                updatedValues.put(
                        String.format("users/%s/exchanges/%s/status",
                                getUser().getUid(),
                                exchange.getFireBaseId()),
                        Constants.EXCHANGE_STATUS_SAVED);

                mFireBaseManager.updateValues(updatedValues);

                break;
            }
        }
    }


    private void cancelExchange(String fireBaseId) {

        for (int i = 0; i < exchanges.size(); i++) {
            if (exchanges.get(i).getFireBaseId().equals(fireBaseId)){

                db.myThingDao().updateThing(
                        myThingsManager.updateThing(
                                exchanges.get(i),
                                Constants.THING_STATUS_AVAILABLE));

                removeExchange(i);

                break;
            }
        }


        mFireBaseManager.removeFromFireBase(
                String.format("users/%s/exchanges/%s",
                        user.getUid(),
                        fireBaseId));

    }


    private void endExchange(String fireBaseId) {

        for (int i = 0; i < exchanges.size(); i++) {
            if (exchanges.get(i).getFireBaseId().equals(fireBaseId)){

                exchanges.get(i).setStatus(Constants.EXCHANGE_STATUS_ENDED);

                db.exchangeDao().updateExchange(exchanges.get(i));

                break;
            }
        }

    }


    public void cleanChatCounter(final int id){
        Log.d("@@@", "Repository.cleanChatCounter");

        sharedPrefs.addNewCounter(
                id,
                mChatItemsManager.cleanNewMessagesCounter(id));

    }

    public int getExchangeIdByThing(String thingId) {

        for (ExchangeEntity exchange :
                exchanges) {
            if (Utils.fireBasePathToId(exchange.getThing1_path()).equals(thingId)){
                return exchange.getId();
            }
        }
        return 0;
    }

    public Context getAppContext() {
        return appContext;
    }

    public AppDatabase getDb() {
        return db;
    }

    public User getUser() {
        return user == null ? sharedPrefs.getUser() : user;
    }



    public List<ExchangeEntity> getExchanges() {
        return exchanges;
    }

    public ExchangeEntity getExchangeById(int id) {

        for (ExchangeEntity exchange : exchanges) {

            if (exchange.getId() == id){
                return exchange;
            }

        }
        return null;
    }


    public void setChosenChat(int chosenChat) {
        this.chosenChat = chosenChat;
    }

    public int getChosenChat() {
        return chosenChat;
    }

    public SharedPreferencesManager getSharedPreferences() {
        return sharedPrefs;
    }

    public FireBaseManager getFireBaseManager() {
        return mFireBaseManager;
    }

    public ChatItemsManager getChatItemsManager() {
        return mChatItemsManager;
    }

    public MyThingsManager getMyThingsManager() {
        return myThingsManager;
    }


    public List<FireBaseThingItem> getFireBaseItems() {
        return thingItems;
    }

    public List<Bitmap> getBackgroundImages() {
        return backgroundImages;
    }

    public List<ThingCategory> getCategories() {
        return categories;
    }

    public MainActivityState getState() {
        return activityState;
    }


    public void loadLocations(final LocationsLoaderCallback callback) {
        Log.d("@@@", "Repository.loadLocations");

        final String homeLocation = sharedPrefs.getHomeLocation();

        if (locations != null
                && callback != null){
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i).equals(homeLocation)){
                    callback.onLocationsLoad(locations, i);
                    break;
                }
            }

        } else {

            mFireBaseManager.loadLocations(user.getCountry(),
                    new FireBaseManager.LocationsLoaderCallback() {
                        @Override
                        public void onLoadLocations(List<String> newLocations) {
                            Log.d("@@@", "Repository.loadLocations.mFireBaseManager.onLoadLocations: "
                                    +newLocations.size());

                            locations = newLocations;


                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    int homeLocationPosition=0;
                                    boolean containHomeLocation = false;
                                    for (int i = 0; i < locations.size(); i++) {
                                        if (locations.get(i).equals(homeLocation)){
                                            homeLocationPosition = i;
                                            containHomeLocation = true;
                                            break;
                                        }
                                    }
                                    if (!containHomeLocation){
                                        locations.add(user.getLocation());
                                        homeLocationPosition = locations.size()-1;
                                    }


                                    if (callback != null){

                                        callback.onLocationsLoad(locations, homeLocationPosition);
                                    }
                                }
                            });


                        }
                    });
        }
    }

    public void removeMyThing(String thing1_path) {

        for (int i = 0; i < myThingsManager.getMyThings().size(); i++) {
            if (myThingsManager.getMyThings().get(i).getFireBasePath().equals(thing1_path)){

                final ThingEntity thingToRemove = myThingsManager.getMyThings().get(i);

                List<OfferItem> offers = myThingsManager.getOffersByThing().get(
                        Utils.fireBasePathToId(thingToRemove.getFireBasePath()));

                if (offers != null){
                    for (OfferItem offer : offers) {

                        mFireBaseManager.removeFromFireBase(
                                String.format("users/%s/offers/%s/%s/%s",
                                        user.getUid(),
                                        offer.getMainThingId(),
                                        offer.getOffererUid(),
                                        offer.getOfferThingId()));
                    }
                }

                mFireBaseManager.removeFromFireBase(thingToRemove.getFireBasePath());
                mFireBaseManager.removeFromFireBase(thingToRemove.getPointerPath());
                mFireBaseManager.removeFromStorage(thingToRemove.getImgLink());

                db.myThingDao().delete(thingToRemove);

                myThingsManager.removeThing(i);

                break;
            }

        }


    }

    public void removeExchange(int position) {

        mFireBaseManager.removeChatListener(position);

        mFireBaseManager.removeFromFireBase(
                exchanges.get(position).getChatPath());


        mChatItemsManager.removeChatItem(exchanges.get(position).getId());

        db.exchangeDao().delete(exchanges.get(position));

        exchanges.remove(position);

    }

    public void changeUserName(final String name) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> updatedValues = new HashMap<>();
                updatedValues.put(
                        String.format("users/%s/metadata/name", getUser().getUid()),
                        name);


                user.setName(name);

                sharedPrefs.changeName(name);

                mFireBaseManager.updateValues(updatedValues);


            }
        });



    }

    public void changeUserImage(Bitmap bitmap) {
        Log.d("@@@", "Repository.changeUserImage");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        new ImageUploader(
                stream.toByteArray(),
                user.getUid(),
                System.currentTimeMillis(),
                new ImageUploader.OnFinishListener() {
                    @Override
                    public void onFinish(String imgLink) {
                        if (imgLink != null && imgLink.length()>0){
                            HashMap<String, Object> updatedValues = new HashMap<>();
                            updatedValues.put(
                                    String.format("users/%s/metadata/imgUrl", getUser().getUid()),
                                    imgLink);

                            user.setImgUrl(imgLink);

                            sharedPrefs.saveImage(imgLink);

                            mFireBaseManager.updateValues(updatedValues);
                        }
                    }
                }).send();

    }

    public Handler getHandler() {
        return mHandler;
    }

    public void removeUser() {

        user = null;

        sharedPrefs.removeUser();

    }


    public interface LocationsLoaderCallback {
        void onLocationsLoad(List<String> locations, int homePosition);
    }
}

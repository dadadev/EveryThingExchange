package com.dadabit.everythingexchange.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.utils.geocode.GeocodeManager;
import com.dadabit.everythingexchange.utils.geocode.LocationResponseCallback;

import java.util.List;

import javax.inject.Singleton;


@Singleton
public class SharedPreferencesManager {

    public static final String APP_PREFERENCES = "ca7eed44-2709-4dd7-b329-52507af76734";
    public static final String APP_PREFERENCES_COUNTRY = "shared_preferences_country";
    public static final String APP_PREFERENCES_HOME_LOCATION = "shared_preferences_home_location";
    public static final String APP_PREFERENCES_UID = "shared_preferences_uid";
    public static final String APP_PREFERENCES_TOKEN = "shared_preferences_token";
    public static final String APP_PREFERENCES_USER_NAME = "shared_preferences_user_name";
    public static final String APP_PREFERENCES_USER_IMG = "shared_preferences_user_image";

    private SharedPreferences sharedPrefs;

    public SharedPreferencesManager(Context context) {
        Log.d("@@@", "SharedPreferencesManager.create");
        sharedPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

//    private void checkLocation(Context context) {
//
//        if (isLocationExist()){
//
//            SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putString(
//                    APP_PREFERENCES_HOME_LOCATION,
//                    sharedPrefs.getString(APP_PREFERENCES_HOME_LOCATION, ""));
//            editor.apply();
//
//        } else {
//
//            new GeocodeManager(context, new LocationResponseCallback() {
//                @Override
//                public void onResponse(String[] location) {
//
//                    SharedPreferences.Editor editor = sharedPrefs.edit();
//                    editor.putString(APP_PREFERENCES_HOME_LOCATION, location[0]);
//                    editor.putString(APP_PREFERENCES_HOME_LOCATION, location[0]);
//                    editor.putString(APP_PREFERENCES_COUNTRY, location[1]);
//                    editor.apply();
//
//                }
//            });
//
//        }
//
//    }

    public boolean isTokenChanged(String newToken) {

        String oldToken = sharedPrefs.getString(APP_PREFERENCES_TOKEN,"");

        if ( newToken != null &&  !newToken.equals(oldToken) && newToken.length() > 0 ){
            Log.d("@@@", "SharedPreferencesManager.oldToken: "+oldToken);
            Log.d("@@@", "SharedPreferencesManager.newToken: "+newToken);
            Log.d("@@@", "SharedPreferencesManager.saveNewToken");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(APP_PREFERENCES_TOKEN, newToken);
            editor.apply();
            return true;
        }
        return false;
    }

    public void addNewCounter(int id, int counter) {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (counter > -1){

            editor.putInt(String.valueOf(id), counter);

        } else {
            Log.d("@@@", "SharedPreferencesManager.removeCounter: "+id);

            editor.remove(String.valueOf(id));
        }


        editor.apply();


    }

    public int getCounter(int id){
        return sharedPrefs.getInt(String.valueOf(id), 0);
    }

    public int[] getAllCounters(List<ExchangeEntity> exchanges){

        int[] result = new int[exchanges.size()];

        for (int i = 0; i < exchanges.size(); i++) {
            result[i] = sharedPrefs.getInt(String.valueOf(exchanges.get(i).getId()), 0);
        }
        return result;
    }

//    public static synchronized SharedPreferencesManager getInstance(Context context){
//        if(instance == null)
//            instance = new SharedPreferencesManager(context);
//
//        return instance;
//    }


    public boolean isLocationExist(){
        return sharedPrefs.contains(APP_PREFERENCES_HOME_LOCATION)
                && sharedPrefs.contains(APP_PREFERENCES_COUNTRY)
                && sharedPrefs.contains(APP_PREFERENCES_HOME_LOCATION);
    }


//    public String getCurrentLocation(){
//        return sharedPrefs.getString(APP_PREFERENCES_HOME_LOCATION,"");
//    }
//
//    public String getHomeLocation(){
//        return sharedPrefs.getString(APP_PREFERENCES_HOME_LOCATION,"");
//    }
//
//    public String getCountry(){
//        return sharedPrefs.getString(APP_PREFERENCES_COUNTRY,"");
//    }

    public boolean isUserExist(){
        return sharedPrefs.contains(APP_PREFERENCES_UID)
                && sharedPrefs.contains(APP_PREFERENCES_USER_NAME)
                && sharedPrefs.contains(APP_PREFERENCES_USER_IMG) ;
    }

    public boolean saveUser(User user){


        if (user.getUid().length() == 0
                || user.getName().length() == 0
                || user.getImgUrl().length() == 0
                || user.getCountry().length() == 0
                || user.getLocation().length() == 0
                || user.getToken().length() == 0){
            Log.d("@@@", "SharedPreferencesManager.saveUser.NO_DATA (!)");
            return false;
        } else {
            Log.d("@@@", "SharedPreferencesManager.saveUser");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(APP_PREFERENCES_UID, user.getUid());
            editor.putString(APP_PREFERENCES_USER_NAME, user.getName());
            editor.putString(APP_PREFERENCES_USER_IMG, user.getImgUrl());
            editor.putString(APP_PREFERENCES_HOME_LOCATION, user.getLocation());
            editor.putString(APP_PREFERENCES_COUNTRY, user.getCountry());
            editor.putString(APP_PREFERENCES_TOKEN, user.getToken());
            editor.apply();

            return true;
        }

    }

    public void removeUser(){
        Log.d("@@@", "SharedPreferencesManager.removeUser");
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(APP_PREFERENCES_UID);
        editor.remove(APP_PREFERENCES_USER_IMG);
        editor.remove(APP_PREFERENCES_USER_NAME);
        editor.remove(APP_PREFERENCES_HOME_LOCATION);
        editor.remove(APP_PREFERENCES_COUNTRY);
        editor.remove(APP_PREFERENCES_TOKEN);
        editor.apply();

    }

    public boolean saveLocation(String[] fullLocation){

        if (fullLocation[0] != null && fullLocation[0].length() != 0
                && fullLocation[1] != null && fullLocation[1].length() != 0){
            Log.d("@@@", "SharedPreferencesManager.saveLocation: "+fullLocation[0]);
            Log.d("@@@", "SharedPreferencesManager.saveCountry: "+fullLocation[1]);

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(APP_PREFERENCES_HOME_LOCATION, fullLocation[0]);
            editor.putString(APP_PREFERENCES_COUNTRY, fullLocation[1]);
            editor.apply();

            return true;
        } else {
            Log.d("@@@", "SharedPreferencesManager.saveLocation.NO_DATA (!)");
            return false;
        }
    }

    public void setHomeLocation(String location){

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(APP_PREFERENCES_HOME_LOCATION, location);
        editor.apply();

    }

    public String getHomeLocation() {
        return sharedPrefs.getString(APP_PREFERENCES_HOME_LOCATION, "");
    }

    public User getUser(){

        return isUserExist() ?
                new User(
                        sharedPrefs.getString(APP_PREFERENCES_UID, ""),
                        sharedPrefs.getString(APP_PREFERENCES_TOKEN,""),
                        sharedPrefs.getString(APP_PREFERENCES_USER_NAME, ""),
                        sharedPrefs.getString(APP_PREFERENCES_USER_IMG,""),
                        sharedPrefs.getString(APP_PREFERENCES_HOME_LOCATION,""),
                        sharedPrefs.getString(APP_PREFERENCES_COUNTRY,""))
                : null;

    }

    public void changeName(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(APP_PREFERENCES_USER_NAME, name);
        editor.apply();

    }

    public void saveImage(String imgLink) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(APP_PREFERENCES_USER_IMG, imgLink);
        editor.apply();
    }
}

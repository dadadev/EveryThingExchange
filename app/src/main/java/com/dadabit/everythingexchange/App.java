package com.dadabit.everythingexchange;

import android.app.Application;
import android.util.Log;

import com.dadabit.everythingexchange.di.AppComponent;
import com.dadabit.everythingexchange.di.AppModule;
import com.dadabit.everythingexchange.di.DaggerAppComponent;


public class App extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        Log.d("@@@", "App.onCreate");
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }






    public static AppComponent getComponent(){
        return appComponent;
    }
}

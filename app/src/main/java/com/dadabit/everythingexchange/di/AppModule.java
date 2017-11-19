package com.dadabit.everythingexchange.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.db.AppDatabase;
import com.dadabit.everythingexchange.ui.viewmodel.MainActivityViewModel;
import com.dadabit.everythingexchange.ui.viewmodel.ViewModelFactory;
import com.dadabit.everythingexchange.utils.SharedPreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        Log.d("@@@", "AppModule.onCreate");
        this.application = application;
    }

    @Provides @Singleton Context providesAppContext() {
        return application.getApplicationContext();
    }

    @Provides @Singleton
    AppDatabase providesDatabase() {
//        Log.d("@@@", "AppModule.providesDatabase");
        return Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, "db_things_exchange").build();
    }

    @Provides
    @Singleton
    SharedPreferencesManager provideSharedPreferences(){
//        Log.d("@@@", "AppModule.provideSharedPreferences");
        return new SharedPreferencesManager(application.getApplicationContext());
    }

    @Provides
    @Singleton
    Repository provideRepository(AppDatabase db, SharedPreferencesManager sharedPrefs){
//        Log.d("@@@", "AppModule.provideRepository");
        return new Repository(db, sharedPrefs, application.getApplicationContext());
    }



    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(Repository repository) {
        return new ViewModelFactory(repository);
    }


}
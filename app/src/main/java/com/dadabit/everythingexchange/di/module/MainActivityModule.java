package com.dadabit.everythingexchange.di.module;


import com.dadabit.everythingexchange.di.ActivityScope;
import com.dadabit.everythingexchange.ui.activity.MainActivity;
import com.dadabit.everythingexchange.ui.presenter.main.MainActivityPresenter;
import com.dadabit.everythingexchange.utils.SharedPreferencesManager;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    private MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
//
//    @Provides
//    @ActivityScope
//    MainActivity provideMainActivity() {
//        return mainActivity;
//    }



}

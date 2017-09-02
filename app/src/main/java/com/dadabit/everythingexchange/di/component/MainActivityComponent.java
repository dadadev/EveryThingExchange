package com.dadabit.everythingexchange.di.component;

import com.dadabit.everythingexchange.di.ActivityScope;
import com.dadabit.everythingexchange.di.module.MainActivityModule;
import com.dadabit.everythingexchange.ui.activity.MainActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent {

    MainActivity inject(MainActivity mainActivity);

}

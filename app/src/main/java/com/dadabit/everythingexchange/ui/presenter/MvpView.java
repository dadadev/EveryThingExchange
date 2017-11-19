package com.dadabit.everythingexchange.ui.presenter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;


public interface MvpView {

    Context getAppContext();

    Context getActivityContext();

    LifecycleOwner getLifecycleOwner();

}

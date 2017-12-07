package com.dadabit.everythingexchange.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.dadabit.everythingexchange.model.Repository;


public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;

    public ViewModelFactory(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(MainActivityViewModel.class)){
            return (T) new MainActivityViewModel(mRepository);
        } else if (modelClass.equals(PersonInfoActivityViewModel.class)){
            return (T) new PersonInfoActivityViewModel(mRepository);
        } else if (modelClass.equals(AddThingActivityViewModel.class)){
            return (T) new AddThingActivityViewModel(mRepository);
        }

        return super.create(modelClass);
    }
}

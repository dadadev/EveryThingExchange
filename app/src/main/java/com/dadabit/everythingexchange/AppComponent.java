package com.dadabit.everythingexchange;

import com.dadabit.everythingexchange.model.AddThingActivityRepo;
import com.dadabit.everythingexchange.model.ConfirmExchangeActivityRepo;
import com.dadabit.everythingexchange.model.OffersActivityRepo;
import com.dadabit.everythingexchange.model.PersonInfoActivityRepo;
import com.dadabit.everythingexchange.model.SingleChatActivityRepo;
import com.dadabit.everythingexchange.model.SingleThingActivityRepo;
import com.dadabit.everythingexchange.ui.activity.AuthActivity;
import com.dadabit.everythingexchange.ui.presenter.auth.AuthActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.main.MainActivityPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(AuthActivityPresenter authActivityPresenter);

    void inject(MainActivityPresenter mainActivityPresenter);

    void inject(AddThingActivityRepo addThingActivityRepo);

    void inject(SingleThingActivityRepo singleThingActivityRepo);

    void inject(OffersActivityRepo offersActivityRepo);

    void inject(ConfirmExchangeActivityRepo confirmExchangeActivityRepo);

    void inject(SingleChatActivityRepo singleChatActivityRepo);

    void inject(PersonInfoActivityRepo personInfoActivityRepo);


}
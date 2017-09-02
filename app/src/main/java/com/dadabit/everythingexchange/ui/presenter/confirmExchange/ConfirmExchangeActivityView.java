package com.dadabit.everythingexchange.ui.presenter.confirmExchange;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dadabit.everythingexchange.ui.presenter.MvpView;


public interface ConfirmExchangeActivityView extends MvpView {

    ImageView getFirstThingImageView();
    ImageView getSecondThingImageView();
    TextView getThingNameTextView();
    TextView getThingDescriptionTextView();
    ImageView getOffererImageView();
    TextView getOffererNameTextView();
    Button getOkButton();



    void goBack();

    void startChatActivity();

    void startPersonInfoActivity(String uid);


}

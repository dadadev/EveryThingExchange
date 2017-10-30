package com.dadabit.everythingexchange.ui.presenter.auth;

import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dadabit.everythingexchange.ui.presenter.MvpView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public interface AuthActivityView extends MvpView {


    SignInButton getSignInButton();

    CardView getCardView();

    EditText getEditText();

    ImageView getImageView();

    ProgressBar getProgressBar();

    boolean isLogOut();

    GoogleApiClient getGoogleApiClient();

    void authorizeGoogle();

    void startMainActivity();

    void showToast(int resId);

    void animateUserInfoCardIn();

    void animateUserInfoCardOut();

    void showBottomSheet();

    void hideBottomSheet();

    Button getChangeUserPicButton();

    Button getDefaultUserPicButton();

    void getImageFromGallery();

    void vibrate(int duration);
}

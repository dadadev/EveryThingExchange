package com.dadabit.everythingexchange.ui.presenter.auth;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.geocode.GeocodeManager;
import com.dadabit.everythingexchange.utils.geocode.LocationResponseCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

public class AuthActivityPresenter extends BasePresenter<AuthActivityView>{


    @Inject Repository mRepository;


    private FirebaseAuth mFireBaseAuth;

    public AuthActivityPresenter() {
        Log.d("@@@", "AuthActivityPresenter.create");
        App.getComponent().inject(this);
    }

    @Override
    public void attachView(AuthActivityView authActivityView) {
        Log.d("@@@", "AuthActivityPresenter.attachView");
        super.attachView(authActivityView);

        mFireBaseAuth = FirebaseAuth.getInstance();

        if (getView().isLogOut()){

            getView().getGoogleApiClient()
                    .registerConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    logOut();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    Log.d("@@@", "AuthActivityPresenter.registerConnectionCallbacks.onConnectionSuspended");

                                }
                            });


        }

        getView().getSignInButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getView().authorizeGoogle();
                    }
                }
        );

    }

    public void signInGoogleAccount(GoogleSignInAccount signInAccount) {

        getView().getProgressBar().setVisibility(View.VISIBLE);

        mFireBaseAuth
                .signInWithCredential(
                        GoogleAuthProvider
                                .getCredential(
                                        signInAccount.getIdToken(),
                                        null))
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    logIn();

                                } else {

                                    getView().showAuthFailToast();

                                }
                            }
                        });
    }

    private void logOut() {

        mFireBaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(getView().getGoogleApiClient());

        mRepository.removeUser();

    }

    private void logIn() {
        Log.d("@@@", "AuthActivityPresenter.logIn");

        final FirebaseUser mFireBaseUser = mFireBaseAuth.getCurrentUser();


        if (mFireBaseUser != null
                && mFireBaseUser.getPhotoUrl() != null){

            Glide.with(getView().getAppContext())
                    .load(mFireBaseUser.getPhotoUrl().toString())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.d("@@@", "AuthActivity.mFireBaseUser.getPhotoUrl().onException");


                            // TODO: 10/09/2017  Handle no UserPick !!!

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d("@@@", "AuthActivityPresenter.mFireBaseUser.getPhotoUrl().onResourceReady");


                            getView().getImageView().setVisibility(View.VISIBLE);
                            getView().getCardView().setVisibility(View.VISIBLE);

                            saveUser(mFireBaseUser);

                            return false;
                        }
                    })
            .into(getView().getImageView());






        }
    }

    private void saveUser(final FirebaseUser mFireBaseUser) {

        final String token = FirebaseInstanceId.getInstance().getToken();

        new GeocodeManager(getView().getAppContext(), new LocationResponseCallback() {
            @Override
            public void onResponse(String[] location) {

                Log.d("@@@", "AuthActivityPresenter.GeocodeManager.onResponse");


                getView().getProgressBar().setVisibility(View.GONE);



                Log.d("@@@", "AuthActivityPresenter.getPhotoUrl: "+ mFireBaseUser.getPhotoUrl().toString());
                Log.d("@@@", "AuthActivityPresenter.getPhotoUrl.getPath: "+ mFireBaseUser.getPhotoUrl().getPath());



                if (mRepository.getSharedPreferences().saveUser(
                        new User(
                                mFireBaseUser.getUid(),
                                token,
                                mFireBaseUser.getDisplayName(),
                                mFireBaseUser.getPhotoUrl().toString(),
                                location[0],
                                location[1]))){

                    mRepository.init();


                    getView().startMainActivity();
                }
            }
        });
    }


    @Override
    public void detachView() {
        Log.d("@@@", "AuthActivityPresenter.detachView");
        super.detachView();

    }
}

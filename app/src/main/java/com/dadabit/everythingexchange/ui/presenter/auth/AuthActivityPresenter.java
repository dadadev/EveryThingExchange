package com.dadabit.everythingexchange.ui.presenter.auth;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.firebase.ImageUploader;
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

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

public class AuthActivityPresenter extends BasePresenter<AuthActivityView> implements View.OnClickListener{


    @Inject Repository mRepository;


    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;

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

            logOut();

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

        getView().animateUserInfoCardIn();



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

        getView().getGoogleApiClient()
                .registerConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {

                                mFireBaseAuth.signOut();

                                Auth.GoogleSignInApi.signOut(getView().getGoogleApiClient());

                                mRepository.removeUser();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.d("@@@", "AuthActivityPresenter.registerConnectionCallbacks.onConnectionSuspended");

                            }
                        });





    }

    private void logIn() {
        Log.d("@@@", "AuthActivityPresenter.logIn");

        mFireBaseUser = mFireBaseAuth.getCurrentUser();

        if (mFireBaseUser != null
                && mFireBaseUser.getPhotoUrl() != null){

            getView().getEditText().setText(mFireBaseUser.getDisplayName());

            Glide.with(getView().getAppContext())
                    .load(mFireBaseUser.getPhotoUrl().toString())
                    .crossFade()
                    .error(R.drawable.ic_person_default)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.d("@@@", "AuthActivity.mFireBaseUser.getPhotoUrl().onException");

                            askUserPicChange();

                            return false;
                        }



                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d("@@@", "AuthActivityPresenter.mFireBaseUser.getPhotoUrl().onResourceReady");

                            saveUser(mFireBaseUser.getPhotoUrl().toString());

                            return false;
                        }
                    })
                    .into(getView().getImageView());






        }
    }

    private void askUserPicChange() {

        getView().showBottomSheet();

        getView().getDefaultUserPicButton().setOnClickListener(this);

        getView().getChangeUserPicButton().setOnClickListener(this);

    }

    private void saveUser(final String userPicUrl) {


        new GeocodeManager(getView().getAppContext(), new LocationResponseCallback() {
            @Override
            public void onResponse(final String[] location) {

                Log.d("@@@", "AuthActivityPresenter.GeocodeManager.onResponse");


                getView().animateUserInfoCardOut();


                setHandler(new Handler(getLooper()));

                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("@@@", "AuthActivityPresenter.getPhotoUrl: "+ mFireBaseUser.getPhotoUrl().toString());
                        Log.d("@@@", "AuthActivityPresenter.getPhotoUrl.getPath: "+ mFireBaseUser.getPhotoUrl().getPath());

                        final String token = FirebaseInstanceId.getInstance().getToken();


                        if (mRepository
                                .getSharedPreferences()
                                .saveUser(
                                        new User(
                                                mFireBaseUser.getUid(),
                                                token,
                                                mFireBaseUser.getDisplayName(),
                                                userPicUrl,
                                                location[0],
                                                location[1]))){

                            mRepository.init();

                            getView().startMainActivity();

                        }

                    }
                }, 400);
            }
        });
    }

    public void changeUserImage(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        new ImageUploader(
                stream.toByteArray(),
                mFireBaseUser.getUid(),
                System.currentTimeMillis(),
                new ImageUploader.OnFinishListener() {
                    @Override
                    public void onFinish(String imgLink) {
                        if (imgLink != null && imgLink.length()>0){

                            saveUser(imgLink);

                        }
                    }
                }).send();

    }


    @Override
    public void detachView() {
        Log.d("@@@", "AuthActivityPresenter.detachView");
        super.detachView();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.bottom_sh_no_userpick_btn_ok:

                getView().hideBottomSheet();

                getView().getImageFromGallery();

                break;

            case R.id.bottom_sh_no_userpick_btn_cancel:

                getView().hideBottomSheet();

                changeUserImage(
                        BitmapFactory.decodeResource(
                                getView().getActivityContext().getResources(),
                                R.drawable.ic_person_default));

                break;

        }

    }
}

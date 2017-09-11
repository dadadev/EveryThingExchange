package com.dadabit.everythingexchange.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.presenter.auth.AuthActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.auth.AuthActivityView;
import com.dadabit.everythingexchange.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        AuthActivityView {

    private static final int RC_SIGN_IN = 9001;

    public static final String AUTH_ARGUMENT = "auth_arg";

    @BindView(R.id.auth_button) SignInButton mAuthButton;
    @BindView(R.id.auth_progressBar) ProgressBar mProgressBar;
    @BindView(R.id.auth_card) CardView userInfoCard;
    @BindView(R.id.auth_card_editText) EditText etName;
    @BindView(R.id.auth_card_imageView) ImageView ivUserPic;

    private GoogleApiClient mGoogleApiClient;

    public static AuthActivityPresenter mPresenter;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Log.d("@@@", "AuthActivity.onCreate");

        ButterKnife.bind(this);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (mPresenter == null){
            mPresenter = new AuthActivityPresenter();
        }
        mPresenter.attachView(this);

    }


    @Override
    protected void onDestroy() {
        Log.d("@@@", "AuthActivity.onDestroy");
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        if (isFinishing()) {
            Log.d("@@@", "AuthActivity.onDestroy.isFinishing");
            mPresenter = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()
                    && mPresenter != null) {

                mPresenter.signInGoogleAccount(result.getSignInAccount());

            } else {
                showAuthFailToast();
            }
        }
    }

//    private void fireBaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
//
//        mFireBaseAuth
//                .signInWithCredential(
//                        GoogleAuthProvider
//                                .getCredential(
//                                        signInAccount.getIdToken(),
//                                        null))
//                .addOnCompleteListener(
//                        this,
//                        new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(AuthActivity.this, R.string.toast_auth_fail,
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//
//                                    Log.d("@@@", "AuthActivity.fireBaseAuthWithGoogle.onComplete");
//
//                                    final FirebaseUser mFireBaseUser= mFireBaseAuth.getCurrentUser();
//                                    final String token = FirebaseInstanceId.getInstance().getToken();
//
//                                    if (mFireBaseUser != null
//                                            && mFireBaseUser.getPhotoUrl() != null){
//
//                                        new GeocodeManager(getApplicationContext(), new LocationResponseCallback() {
//                                            @Override
//                                            public void onResponse(String[] location) {
//
//                                                Log.d("@@@", "AuthActivity.GeocodeManager.onResponse");
//
//
//                                                mProgressBar.setVisibility(View.GONE);
//
//                                                Glide.with(getApplicationContext())
//                                                        .load(mFireBaseUser.getPhotoUrl().toString())
//                                                        .listener(new RequestListener<String, GlideDrawable>() {
//                                                            @Override
//                                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                                                Log.d("@@@", "AuthActivity.mFireBaseUser.getPhotoUrl().onException");
//                                                                return false;
//                                                            }
//
//                                                            @Override
//                                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                                                Log.d("@@@", "AuthActivity.mFireBaseUser.getPhotoUrl().onException");
//                                                                return false;
//                                                            }
//                                                        });
//
//                                                Log.d("@@@", "AuthActivity.getPhotoUrl: "+ mFireBaseUser.getPhotoUrl().toString());
//                                                Log.d("@@@", "AuthActivity.getPhotoUrl.getPath: "+ mFireBaseUser.getPhotoUrl().getPath());
//
//
//
//                                                if (mRepository.getSharedPreferences().saveUser(
//                                                        new User(
//                                                                mFireBaseUser.getUid(),
//                                                                token,
//                                                                mFireBaseUser.getDisplayName(),
//                                                                mFireBaseUser.getPhotoUrl().toString(),
//                                                                location[0],
//                                                                location[1]))){
//
//                                                    mRepository.init();
//
//                                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
//                                                    finish();
//                                                }
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        });
//    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.toast_play_service_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public SignInButton getSignInButton() {
        return mAuthButton;
    }

    @Override
    public CardView getCardView() {
        return userInfoCard;
    }

    @Override
    public EditText getEditText() {
        return etName;
    }

    @Override
    public ImageView getImageView() {
        return ivUserPic;
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public boolean isLogOut() {
        return getIntent().hasExtra(AUTH_ARGUMENT)
                && getIntent().getIntExtra(AUTH_ARGUMENT, 0) == Constants.AUTH_LOG_OUT;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void authorizeGoogle() {

        Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(authorizeIntent, RC_SIGN_IN);
    }

    @Override
    public void startMainActivity() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void showAuthFailToast() {
        Toast.makeText(this, R.string.toast_auth_fail,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void animateUserInfoCardIn() {

        userInfoCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        userInfoCard.setVisibility(View.VISIBLE);

        mProgressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        mProgressBar.setVisibility(View.VISIBLE);


        ivUserPic.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        ivUserPic.setVisibility(View.VISIBLE);


    }

    @Override
    public void showBottomSheet() {


        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.bottom_sh_no_userpick);



//        NestedScrollView nestedScrollView = (NestedScrollView)  findViewById(R.id.bottom_sh_confirm_exchange);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);



    }
}


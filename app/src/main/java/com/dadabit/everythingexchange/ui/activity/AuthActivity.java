package com.dadabit.everythingexchange.ui.activity;


import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.ui.presenter.auth.AuthActivityPresenter;
import com.dadabit.everythingexchange.ui.presenter.auth.AuthActivityView;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Utils;
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

    private static final int RC_SIGN_IN = 9119;
    private static final int RC_IMAGE_GALLERY = 1991;

    public static final String AUTH_ARGUMENT = "auth_arg";

    @BindView(R.id.auth_button) SignInButton mAuthButton;
    @BindView(R.id.auth_progressBar) ProgressBar mProgressBar;
    @BindView(R.id.auth_card) CardView userInfoCard;
    @BindView(R.id.auth_card_editText) EditText etName;
    @BindView(R.id.auth_card_imageView) ImageView ivUserPic;

    @BindView(R.id.bottom_sh_no_userpick) ConstraintLayout mBottomSheet;
    @BindView(R.id.bottom_sh_no_userpick_tv_message) TextView tvBottomSheet;
    @BindView(R.id.bottom_sh_no_userpick_btn_ok) Button btnChangeUserPic;
    @BindView(R.id.bottom_sh_no_userpick_btn_cancel) Button btnDefaultUserPic;


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

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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

        switch (requestCode){

            case RC_SIGN_IN:


                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()
                        && mPresenter != null) {

                    mPresenter.signInGoogleAccount(result.getSignInAccount());

                } else {

                    showToast(R.string.toast_auth_fail);

                }

                break;

            case RC_IMAGE_GALLERY:

                try {

                    Bitmap bitmap = Utils.getImageBitmap(getActivityContext(), data.getData());

                    ivUserPic.setImageBitmap(bitmap);

                    mPresenter.changeUserImage(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();

                    showToast(R.string.toast_error);

                }

                break;
        }

    }



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
    public LifecycleOwner getLifecycleOwner() {
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
    public void showToast(int resId) {

        vibrate(350);

        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();

    }


    @Override
    public void animateUserInfoCardIn() {

        mAuthButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        mAuthButton.setVisibility(View.GONE);

        userInfoCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        userInfoCard.setVisibility(View.VISIBLE);

        mProgressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        mProgressBar.setVisibility(View.VISIBLE);


        ivUserPic.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_top));
        ivUserPic.setVisibility(View.VISIBLE);


    }

    @Override
    public void animateUserInfoCardOut() {

        Animation animSlideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        animSlideOut.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        userInfoCard.setVisibility(View.GONE);
                        ivUserPic.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );


        mProgressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        mProgressBar.setVisibility(View.GONE);

        userInfoCard.startAnimation(animSlideOut);
        ivUserPic.startAnimation(animSlideOut);

    }

    @Override
    public void showBottomSheet() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void hideBottomSheet() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public Button getChangeUserPicButton() {
        return btnChangeUserPic;
    }

    @Override
    public Button getDefaultUserPicButton() {
        return btnDefaultUserPic;
    }

    @Override
    public void getImageFromGallery() {

        startActivityForResult(
                new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                RC_IMAGE_GALLERY);
    }

    @Override
    public void vibrate(int duration) {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null &&
                vibrator.hasVibrator()){

            vibrator.vibrate(duration);

        }
    }
}


package com.dadabit.everythingexchange.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dadabit.everythingexchange.App;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.Repository;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.geocode.GeocodeManager;
import com.dadabit.everythingexchange.utils.geocode.LocationResponseCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 9001;

    public static final String AUTH_ARGUMENT = "auth_arg";

    private FirebaseAuth mFireBaseAuth;
    private GoogleApiClient mGoogleApiClient;


    @BindView(R.id.auth_button) SignInButton mAuthButton;
    @BindView(R.id.auth_progressBar) ProgressBar mProgressBar;


    @Inject Repository mRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Log.d("@@@", "AuthActivity.onCreate");

        ButterKnife.bind(this);

        App.getComponent().inject(this);

        mFireBaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (getIntent().hasExtra(AUTH_ARGUMENT)
                && getIntent().getIntExtra(AUTH_ARGUMENT, 0) == Constants.AUTH_LOG_OUT){

            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    Log.d("@@@", "AuthActivity.registerConnectionCallbacks.onConnected");

                    logOut();
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.d("@@@", "AuthActivity.registerConnectionCallbacks.onConnectionSuspended");

                }
            });


        }

        mAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorize();
            }
        });
    }

    private void logOut() {

        mFireBaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        mRepository.removeUser();

    }


    private void Authorize() {
        Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(authorizeIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                mProgressBar.setVisibility(View.VISIBLE);

                fireBaseAuthWithGoogle(result.getSignInAccount());


            } else {
                Toast.makeText(AuthActivity.this, R.string.toast_auth_fail,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        mFireBaseAuth
                .signInWithCredential(
                        GoogleAuthProvider
                                .getCredential(
                                        signInAccount.getIdToken(),
                                        null))
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(AuthActivity.this, R.string.toast_auth_fail,
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Log.d("@@@", "AuthActivity.fireBaseAuthWithGoogle.onComplete");

                                    final FirebaseUser mFireBaseUser= mFireBaseAuth.getCurrentUser();
                                    final String token = FirebaseInstanceId.getInstance().getToken();

                                    if (mFireBaseUser != null
                                            && mFireBaseUser.getPhotoUrl() != null){

                                        new GeocodeManager(getApplicationContext(), new LocationResponseCallback() {
                                            @Override
                                            public void onResponse(String[] location) {

                                                Log.d("@@@", "AuthActivity.GeocodeManager.onResponse");


                                                if (mRepository.getSharedPreferences().saveUser(
                                                        new User(
                                                                mFireBaseUser.getUid(),
                                                                token,
                                                                mFireBaseUser.getDisplayName(),
                                                                mFireBaseUser.getPhotoUrl().toString(),
                                                                location[0],
                                                                location[1]))){

                                                    mRepository.init();

                                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.toast_play_service_error, Toast.LENGTH_SHORT).show();
    }
}


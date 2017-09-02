package com.dadabit.everythingexchange.ui.presenter.confirmExchange;


import android.app.DatePickerDialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.ConfirmExchangeActivityRepo;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.Dialogs;

import java.util.Calendar;

public class ConfirmExchangePresenter extends BasePresenter<ConfirmExchangeActivityView> implements View.OnClickListener{

    private ConfirmExchangeActivityRepo mRepository;

    private Handler uiHandler;

    public ConfirmExchangePresenter(int myThingPosition, String offerId) {
        Log.d("@@@", "ConfirmExchangePresenter.onCreate");

        mRepository = new ConfirmExchangeActivityRepo(myThingPosition, offerId);

    }

    @Override
    public void attachView(ConfirmExchangeActivityView confirmExchangeActivityView) {
        Log.d("@@@", "ConfirmExchangePresenter.attachView");
        super.attachView(confirmExchangeActivityView);

        showContent();

        getView().getOkButton().setOnClickListener(this);
        getView().getSecondThingImageView().setOnClickListener(this);
        getView().getFirstThingImageView().setOnClickListener(this);
        getView().getOffererImageView().setOnClickListener(this);

        uiHandler = new Handler();

    }

    private void showContent() {

        if (mRepository.getMyThing() != null && mRepository.getOffer() != null){

            Glide.with(getView().getActivityContext())
                    .load(mRepository.getOffer().getItemImgLink())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(getView().getSecondThingImageView());

            Glide.with(getView().getActivityContext())
                    .load(mRepository.getOffer().getOffererImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(getView().getOffererImageView());

            getView().getOffererNameTextView().setText(mRepository.getOffer().getOffererName());
            getView().getFirstThingImageView().setImageBitmap(mRepository.getMyThing().getImgBitmap());
            getView().getThingNameTextView().setText(mRepository.getMyThing().getName());
            getView().getThingDescriptionTextView().setText(mRepository.getMyThing().getDescription());

        } else {
            Log.d("@@@", "ConfirmExchangePresenter.showContent.NO_DATA_ERROR (!!!)");
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_exchange_btnAccept:

                setHandler(new Handler(getLooper()));

                Dialogs.getDateDialog(
                        getView().getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  final int year,
                                                  final int month,
                                                  final int dayOfMonth) {



                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Calendar chosenDate = Calendar.getInstance();
                                        chosenDate.set(year, month, dayOfMonth);

                                        mRepository.sendToFireBase(
                                                chosenDate,
                                                onCompleteListener);
                                    }
                                });

                            }
                        })
                        .show();

                break;
            case R.id.confirm_exchange_thing1_imageView:

                Dialogs.getImageDialog(
                        getView().getActivityContext(),
                        mRepository.getMyThing().getImgBitmap())
                        .show();

                break;
            case R.id.confirm_exchange_thing2_imageView:

                Dialogs.getUrlImageDialog(
                        getView().getActivityContext(),
                        mRepository.getOffer().getItemImgLink())
                        .show();

                break;
            case R.id.confirm_exchange_offerer_imageView:

                getView().startPersonInfoActivity(mRepository.getOffer().getOffererUid());


        }
    }

    private ConfirmExchangeActivityRepo.ConfirmExchangeCompleteCallback onCompleteListener = new ConfirmExchangeActivityRepo.ConfirmExchangeCompleteCallback() {
        @Override
        public void onComplete(boolean isComplete) {

            if (isComplete && isViewAttached() && uiHandler != null){


                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        getView().startChatActivity();
                    }
                });
            }
        }
    };


    @Override
    public void detachView() {
        super.detachView();
        uiHandler = null;
    }
}

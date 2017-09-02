package com.dadabit.everythingexchange.ui.presenter.offers;

import android.app.DatePickerDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.OffersActivityRepo;
import com.dadabit.everythingexchange.model.vo.OfferItem;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.adapter.OffersAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.Dialogs;
import com.dadabit.everythingexchange.utils.MyThingsManager;
import com.dadabit.everythingexchange.utils.TurnLayoutManager;

import java.util.Calendar;


public class OffersActivityPresenter extends BasePresenter<OffersActivityView> implements View.OnClickListener{


    private OffersActivityRepo mRepository;
    private OffersAdapter mAdapter;
    public boolean exit;

    public OffersActivityPresenter() {
        Log.d("@@@", "OffersActivityPresenter.Create");
        mRepository = new OffersActivityRepo();
    }

    @Override
    public void attachView(OffersActivityView offersActivityView) {
        Log.d("@@@", "OffersActivityPresenter.attachView");
        super.attachView(offersActivityView);

        if (mRepository.getThing() != null){

            showContent();

            setListeners();

        }
    }

    private OffersAdapter.ClickListener offersClickListener =
            new OffersAdapter.ClickListener() {
                @Override
                public void onClick(int position) {

                    if (mRepository.getChosenOfferPosition() == -1){

                        setChosenOffer(position);


                    } else {

                        changeChosenOffer(position);

                    }

                }
            };

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //  Floating action button
            case R.id.thingOffers_fab:

                getView().animateFab();

                break;

            //  Change my thing button
            case R.id.thingOffers_card_myThing_description_btnChange:

                getView().startChangeThingActivity(mRepository.getThing().getId());

                break;

            //  Delete my thing button
            case R.id.thingOffers_card_myThing_description_btnDelete:

                //TODO FINISH IT!

                break;

            //  Show my thing buttons
            case R.id.thingOffers_card_myThing_description:

                getView().animateThingInfoClicked();

                break;

            case R.id.bottom_sh_confirm_exchange_thing1_imageView:


                Dialogs.getImageDialog(
                        getView().getActivityContext(),
                        mRepository.getThing().getImgBitmap())
                        .show();

                break;

            case R.id.thingOffers_imageView:

                Dialogs.getImageDialog(
                        getView().getActivityContext(),
                        mRepository.getThing().getImgBitmap())
                        .show();


                break;

            case R.id.bottom_sh_confirm_exchange_thing2_imageView:

                Dialogs.getUrlImageDialog(
                        getView().getActivityContext(),
                        mRepository.getOffers()
                                .get(mRepository.getChosenOfferPosition())
                                .getItemImgLink())
                        .show();

                break;

            case R.id.bottom_sh_confirm_exchange_offerer_imageView:

                getView().startPersonInfoActivity(
                        mRepository.getOffers()
                                .get(mRepository.getChosenOfferPosition())
                                .getOffererUid());

                break;

            case R.id.bottom_sh_confirm_exchange_btnAccept:

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

                                        mRepository.confirmExchange(
                                                chosenDate,
                                                new OffersActivityRepo.ConfirmExchangeCompleteCallback() {
                                                    @Override
                                                    public void onComplete(boolean isComplete) {
                                                        if (isComplete && isViewAttached()){

                                                            getUiHandler().post(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    getView().startChatActivity();
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                    }
                                });

                            }
                        })
                        .show();

                break;

        }


    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetListener =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    if (newState == BottomSheetBehavior.STATE_COLLAPSED
                            && mRepository.getState() ==
                            OffersActivityRepo.State.SHOW_BOTTOM_SHEET){

                        getOfferBack();

                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };



    private void showContent() {

        showMyThingInfo();

        initAdapter();



        if (mRepository.getChosenOfferPosition() > -1){

            loadOffers();

            showExchangeCard();

            mAdapter.hideOffer(mRepository.getChosenOfferPosition());

        } else {

            setHandler(new Handler(getLooper()));

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadOffers();
                }
            }, 1000);

        }

    }

    private void showMyThingInfo() {

        getView().getToolbarImageView().setImageBitmap(mRepository.getThing().getImgBitmap());

        getView().getToolbar().setTitle(mRepository.getThing().getName());

        getView().getCategoryTextView().setText(mRepository.getThingCategoryName());

        getView().getDateTextView().setText(mRepository.getThingDate());

        getView().getDescriptionTextView().setText(mRepository.getThing().getDescription());

        getView().getHashTagsRecyclerView().setAdapter(
                new HashTagsAdapter(mRepository.getThing().getHashTags()));
    }

    private void initAdapter(){

        getView().getRecyclerView().setLayoutManager(
                new TurnLayoutManager(
                        getView().getActivityContext(),
                        TurnLayoutManager.Gravity.START,
                        TurnLayoutManager.Orientation.HORIZONTAL,
                        900,
                        200,
                        false));

        getView().getRecyclerView().setAdapter(
                mAdapter = new OffersAdapter(
                        getView().getActivityContext(),
                        offersClickListener));


    }


    private void loadOffers() {


        mRepository.attachOffersObserver(
                new MyThingsManager.OffersChangeObserver() {
                    @Override
                    public void onChange(final OfferItem newOffer) {
                        Log.d("@@@", "OffersActivityPresenter.loadOffers.onChange");

                        if (isViewAttached()){

                            getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {

                                    if (getView().getProgressBar().getVisibility() == View.VISIBLE){

                                        getView().getProgressBar().setVisibility(View.GONE);

                                        if (newOffer != null){

                                            getView().getExchangeIcon().setVisibility(View.VISIBLE);

                                            getView().animateFab();


                                        }
                                    }


                                    if (newOffer != null){

                                        mAdapter.addOffer(newOffer, mAdapter.getItemCount(), false);

                                    }
                                }
                            });

                        }
                    }
                });
    }


    private void changeChosenOffer(final int position) {

        mRepository.setState(OffersActivityRepo.State.HIDE_INFO);

        getView().getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);

        mAdapter.addOffer(
                mRepository.getOffers().get(mRepository.getChosenOfferPosition()),
                mRepository.getChosenOfferPosition(),
                true);

        getView().getRecyclerView().scrollToPosition(mRepository.getChosenOfferPosition());


        getUiHandler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getView().animateOfferBack(mRepository.getChosenOfferPosition());

                setChosenOffer(position >= mRepository.getChosenOfferPosition()
                        ? position + 1 : position);


            }
        }, 300);

    }

    private void setChosenOffer(int position) {

        mRepository.setChosenOfferPosition(position);

        getView().getAppBarLayout().setExpanded(false, true);

        getView().getRecyclerView().scrollToPosition(position);

        getView().animateOfferChosen(position,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        showExchangeCard();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

        mAdapter.hideOffer(position);


//        getUiHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if (isViewAttached()){
//
//                    showExchangeCard();
//                }
//
//            }
//        }, 300);

    }

    private void getOfferBack() {

        if (mRepository.getChosenOfferPosition() != -1
                && mAdapter != null){

            getView().getAppBarLayout().setExpanded(true, true);

            mAdapter.addOffer(
                    mRepository.getOffers()
                            .get(mRepository.getChosenOfferPosition()),
                    mRepository.getChosenOfferPosition(),
                    true);

            getView().getRecyclerView().scrollToPosition(mRepository.getChosenOfferPosition());


            getUiHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isViewAttached()){

                        getView().animateOfferBack(mRepository.getChosenOfferPosition());

                        mRepository.setChosenOfferPosition(-1);

                    }
                }
            }, 300);

        }


    }

    private void showExchangeCard() {
        Log.d("@@@", "OffersActivityPresenter.showExchangeCard");





        mRepository.setState(OffersActivityRepo.State.SHOW_BOTTOM_SHEET);

        Glide.with(getView().getActivityContext())
                .load(mRepository
                        .getOffers()
                        .get(mRepository.getChosenOfferPosition())
                        .getOffererImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(getView().getOffererImageView());

        Glide.with(getView().getActivityContext())
                .load(mRepository
                        .getOffers()
                        .get(mRepository.getChosenOfferPosition())
                        .getItemImgLink())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(getView().getThing2ImageView());


        getView().getThing1ImageView()
                .setImageBitmap(mRepository.getThing().getImgBitmap());

        getView().getOfferNameTextView()
                .setText(mRepository.getOffers()
                        .get(mRepository.getChosenOfferPosition())
                        .getItemName());


        getView().getOfferDescriptionTextView()
                .setText(mRepository.getOffers()
                        .get(mRepository.getChosenOfferPosition())
                        .getItemDescription());


        getView().getOffererNameTextView()
                .setText(mRepository.getOffers()
                        .get(mRepository.getChosenOfferPosition())
                        .getOffererName());

        getView().getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);


    }





//    private void showAdapter() {
//
//        if (mRepository.getOffers() != null){
//
//            mRepository.setState(OffersActivityRepo.State.HIDE_INFO);
//
//            getView().getRecyclerView().setLayoutManager(
//                    new TurnLayoutManager(
//                            getView().getActivityContext(),
//                            TurnLayoutManager.Gravity.START,
//                            TurnLayoutManager.Orientation.HORIZONTAL,
//                            900,
//                            200,
//                            false));
//
//            getView().getRecyclerView().setAdapter(
//                    mAdapter = new OffersAdapter(
//                            getView().getActivityContext(),
//                            offersClickListener));
//
//
//            getUiHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (isViewAttached()){
//
//                        getView().getExchangeIcon()
//                                .startAnimation(AnimationUtils
//                                        .loadAnimation(getView().getActivityContext(),R.anim.slide_in_top));
//
//                        getView().getExchangeIcon().setVisibility(View.VISIBLE);
//
//                        for (int i = 0; i < mRepository.getOffers().size(); i++) {
//                            mAdapter.addOffer(mRepository.getOffers().get(i), i, false);
//                        }
//                    }
//                }
//            }, 300);
//
//
////            switch (mRepository.getState()){
////
////                case OffersActivityRepo.State.CREATED:
////
////                    mRepository.setState(OffersActivityRepo.State.HIDE_INFO);
////
////
////
////                    break;
////
////                case OffersActivityRepo.State.SHOW_INFO:
////
////
////                    break;
////
////                case OffersActivityRepo.State.HIDE_INFO:
////
////
////                    break;
////
////            }
//        } else {
//
//            getView().animateFab();
//
//            getUiHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isViewAttached()){
//
//                        getView().getFab().hide();
//
//                    }
//                }
//            },
//            300);
//
//        }
//    }


    private void setListeners() {
        getView().getFab().setOnClickListener(this);
        getView().getChangeButton().setOnClickListener(this);
        getView().getDeleteButton().setOnClickListener(this);
        getView().getThingInfoCardView().setOnClickListener(this);
        getView().getThing1ImageView().setOnClickListener(this);
        getView().getThing2ImageView().setOnClickListener(this);
        getView().getOffererImageView().setOnClickListener(this);
        getView().getConfirmExchangeButton().setOnClickListener(this);
        getView().getToolbarImageView().setOnClickListener(this);

        getView().getBottomSheetBehavior().setBottomSheetCallback(bottomSheetListener);
    }


    public void onBackPressed(){

        if (mRepository.getChosenOfferPosition() == -1){
            exit = true;
            getView().goBack();
        } else {
            getView().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_COLLAPSED);
        }



    }


    @Override
    public void detachView() {
        Log.d("@@@", "OffersActivityPresenter.attachView");
        super.detachView();

        mRepository.detachOffersObserver();
        mAdapter = null;
    }

    public void onActivityFinished(){
        mRepository.clean();
    }

}

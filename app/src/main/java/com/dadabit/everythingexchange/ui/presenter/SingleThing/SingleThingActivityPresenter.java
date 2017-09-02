package com.dadabit.everythingexchange.ui.presenter.SingleThing;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.SingleThingActivityRepo;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.adapter.MyThingsIconsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.Dialogs;
import com.dadabit.everythingexchange.utils.Utils;

public class SingleThingActivityPresenter extends BasePresenter<SingleThingActivityView> implements View.OnClickListener{

    private SingleThingActivityRepo mRepository;

    public boolean blockExit;
    private boolean isAdapterShown;

    private MyThingsIconsAdapter mAdapter;

    public SingleThingActivityPresenter() {
        Log.d("@@@", "SingleThingActivityPresenter.Create");
        mRepository = new SingleThingActivityRepo();
    }


    @Override
    public void attachView(SingleThingActivityView singleThingActivityView) {
        Log.d("@@@", "SingleThingActivityPresenter.attachView");
        super.attachView(singleThingActivityView);

        if (mRepository.getThing() != null){

            showContent();

            setListeners();

        }
    }



    private MyThingsIconsAdapter.ClickListener myOfferClickListener =
            new MyThingsIconsAdapter.ClickListener() {
                @Override
                public void onClick(int position) {

                    if (position == mRepository.getAvailableThings().size()){

                        mRepository.isWaitingNewThing = true;
                        getView().startAddThingActivity();

                    } else {

                        showChosenOffer(position);

                    }

                }
            };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.single_thing_fab:
                Log.d("@@@", "SingleThingPresenter.fab.onClick");

                if (mRepository.isOfferDataExist()){

                    blockExit = false;

                    mRepository.sendOfferToFireBase();

                    getView().goBack();

                } else {

                    showMyThings();

                }


                break;
            case R.id.single_thing_userPic:
                Log.d("@@@", "SingleThingPresenter.single_thing_userPic.onClick");

                getView().startPersonInfoActivity(mRepository.getThing().getUserUid());

                break;

            case R.id.single_thing_tvPersonName:
                Log.d("@@@", "SingleThingPresenter.single_thing_tvPersonName.onClick");

                getView().startPersonInfoActivity(mRepository.getThing().getUserUid());

            case R.id.single_thing_imageView:
                Log.d("@@@", "SingleThingPresenter.single_thing_imageView.onClick");

                Dialogs.getUrlImageDialog(
                        getView().getActivityContext(),
                        mRepository.getThing().getItemImgLink())
                        .show();

                break;

            case R.id.single_thing_thing2_imageView:
                Log.d("@@@", "SingleThingPresenter.single_thing_thing2_imageView.onClick");

                clearOffer();

                showMyThings();

                break;
        }


    }



    private void showContent() {

       getView().getCategoryTextView().setText((
               mRepository.getMainRepository().getCategories()
                       .get(mRepository.getThing().getItemCategory()).getName()));

        Glide.with(getView().getActivityContext())
                .load(mRepository.getThing().getItemImgLink())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getThingImageView());

        Glide.with(getView().getActivityContext())
                .load(mRepository.getThing().getUserImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getPersonImageView());

        getView().getPersonNameTextView()
                .setText(mRepository.getThing().getUserName());

        getView().getTextViewName()
                .setText(mRepository.getThing().getItemName());

        getView().getTextViewDescription()
                .setText(mRepository.getThing().getItemDescription());

        getView().getDateTextView()
                .setText(Utils.timestampToString(mRepository.getThing().getDate()));

        if (mRepository.getThing().getHashTags() != null){

            showHashTags();
        }

        if (mRepository.getChosenOfferPosition() != -1){

            Log.d("@@@", "SingleThingActivityPresenter.SHOW_OFFER");

            getView().getMyThingImageView().setImageBitmap(
                    mRepository.getAvailableThings()
                            .get(mRepository.getChosenOfferPosition())
                            .getImgBitmap());

            getView().showOfferIcon();

        } else if (isAdapterShown){

            showMyThings();

        }

    }

    private void showHashTags() {

        getView().getHashTagsRecyclerView().setVisibility(View.VISIBLE);

        getView().getHashTagsRecyclerView().setLayoutManager(
                new LinearLayoutManager(
                        getView().getActivityContext(),
                        LinearLayoutManager.HORIZONTAL,false));

        getView().getHashTagsRecyclerView().setAdapter(
                new HashTagsAdapter(mRepository.getThing().getHashTags()));


    }


    private void setListeners() {

        getView().getFab().setOnClickListener(this);
        getView().getThingImageView().setOnClickListener(this);
        getView().getMyThingImageView().setOnClickListener(this);
        getView().getPersonImageView().setOnClickListener(this);
        getView().getPersonNameTextView().setOnClickListener(this);
    }


    private void showChosenOffer(int position) {

        mRepository.setCurrentOffer(
                mRepository.getAvailableThings().get(position));

        getView().getMyThingImageView().setImageBitmap(
                mRepository.getAvailableThings().get(position).getImgBitmap());

        getView().animateOfferChosen();

        isAdapterShown = false;

        mRepository.setChosenOfferPosition(position);


    }


    private void showMyThings() {

        if (mAdapter == null){

            getView().getMyThingsRecyclerView().setLayoutManager(
                    new LinearLayoutManager(getView().getActivityContext(), LinearLayoutManager.HORIZONTAL,false));

            getView().getMyThingsRecyclerView().setAdapter(
                    mAdapter = new MyThingsIconsAdapter(
                            mRepository.getAvailableThings(),
                            ContextCompat.getDrawable(
                                    getView().getActivityContext(),
                                    R.drawable.ic_add_black_24dp),
                            myOfferClickListener));

        }

        getView().animateOfferThingsIn();

        blockExit = true;
        isAdapterShown = true;
    }

    public void clearOffer(){

        if (mRepository.isOfferDataExist()){

            mRepository.setCurrentOffer(null);

            getView().animateHideOfferChosen();

            showMyThings();

        } else {

            getView().animateOfferThingsOut();

            blockExit = false;

        }

    }

    public void onActivityResumed() {

        if (mRepository== null){
            Log.d("@@@", "SingleThingActivityPresenter.onActivityResumed.CreateRepository--------");
            mRepository = new SingleThingActivityRepo();
        }

        if (mRepository.isWaitingNewThing && mAdapter != null){

            mRepository.isWaitingNewThing = false;

            mRepository.removeAvailableThings();

            mAdapter.setThings(mRepository.getAvailableThings());

            mRepository.getMainRepository().getState().setNewThingAdd(false);

        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mAdapter = null;
    }

    public void detachRepository() {
        Log.d("@@@", "SingleThingActivityPresenter.detachRepository--------");

        mRepository = null;
    }

}

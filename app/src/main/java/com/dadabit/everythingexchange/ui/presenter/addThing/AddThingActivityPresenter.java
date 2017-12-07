package com.dadabit.everythingexchange.ui.presenter.addThing;


import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.AddThingActivityRepo;
import com.dadabit.everythingexchange.model.vo.FireBaseThingItem;
import com.dadabit.everythingexchange.ui.adapter.CategoryIconsAdapter;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.ui.viewmodel.AddThingActivityViewModel;
import com.dadabit.everythingexchange.utils.Dialogs;

public class AddThingActivityPresenter extends BasePresenter<AddThingActivityView>
        implements View.OnClickListener, TextView.OnEditorActionListener{


    private AddThingActivityViewModel mViewModel;

    private CategoryIconsAdapter categoriesAdapter;
    private HashTagsAdapter hashTagsAdapter;



    public boolean blockExit = false;

    private boolean etHashTagShown = false;
    private boolean btnHashTagShown = false;

//    public AddThingActivityPresenter() {
//        Log.d("@@@", "AddThingActivityPresenter.create");
//        mRepository = new AddThingActivityRepo();
//    }

    @Override
    public void attachView(AddThingActivityView addThingActivityView) {
        super.attachView(addThingActivityView);
        Log.d("@@@", "AddThingActivityPresenter.attachView");

        mViewModel = getView().getViewModel();

        showContent();

        setClickListeners();


    }

    private void showContent(){
        Log.d("@@@", "AddThingActivityPresenter.showContent");

        if (mViewModel.getNewFireBaseThing() == null){
            loadThing();
        }

        mViewModel.getNewFireBaseThing()
                .observe(
                        getView().getLifecycleOwner(),
                        new Observer<FireBaseThingItem>() {
                            @Override
                            public void onChanged(@Nullable FireBaseThingItem fireBaseThingItem) {
                                Log.d("@@@", "AddThingActivityPresenter.getNewFireBaseThing.onChanged");

                                if (mViewModel.isReadyToSave()){
                                    Log.d("@@@", "AddThingActivityPresenter.mViewModel.isReadyToSave");
                                    getView().getBtnSave().show();
                                    showBtnHashTag();

                                }



                                if (mViewModel.isThingSaved()){
                                    Log.d("@@@", "AddThingActivityPresenter.mViewModel.isThingSaved");
                                    getView().getProgressBar().setVisibility(View.GONE);

                                    getView().goBack();
                                }
                            }
                        }
                );


        mViewModel.getImage().observe(
                getView().getLifecycleOwner(),
                new Observer<Bitmap>() {
                    @Override
                    public void onChanged(@Nullable Bitmap bitmap) {
                        Log.d("@@@", "NewThingActivityPresenter.ShowImageBitmap");

                        if (bitmap != null){

                            getView().getImageView().setImageBitmap(bitmap);
                        }


                    }
                }
        );

        mViewModel.getImageUrl().observe(
                getView().getLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String url) {
                        Log.d("@@@", "NewThingActivityPresenter.showContent.OnImageUrlLoaded");


                        mViewModel.setImageUrl(url);

                    }
                }
        );


        mViewModel.getChosenCategory().observe(
                getView().getLifecycleOwner(),
                new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer position) {
                        if (position != null){
                            getView().getBtnCategory()
                                    .setText(
                                            mViewModel
                                                    .getCategories()
                                                    .get(position)
                                                    .getName());


                            getView().animateCategoryChoose();

                            showBtnHashTag();
                        }
                    }
                });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()){

                    getView().getNameEditText().requestFocus();

                    getView().showKeyboard();

                }
            }
        }, 300);




    }

    private void loadThing() {

        mViewModel.createNewThing();
        setHandler(new Handler(getLooper()));
        getHandler().post(
                new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.initNewFirebaseThing();
                    }
                }
        );
    }


    private void setClickListeners() {
        getView().getBtnCategory().setOnClickListener(this);
        getView().getBtnSave().setOnClickListener(this);
        getView().getImageView().setOnClickListener(this);
        getView().getBtnHashTag().setOnClickListener(this);
        getView().getHashTagEditText().setOnEditorActionListener(this);
        getView().getNameEditText().addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(getView().getNameEditText().getText())){
                            mViewModel.setName(getView().getNameEditText().getText().toString());
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        getView().getDescriptionEditText().addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(getView().getDescriptionEditText().getText())){
                            mViewModel.setDescription(getView().getDescriptionEditText().getText().toString());
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        getView().getHashTagEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(getView().getHashTagEditText().getText())){
                    getView().getHashTagEditText().setText("#");
                    getView().getHashTagEditText().setSelection(1);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private CategoryIconsAdapter.CategoryIconsClickCallback categoryClickCallback = new CategoryIconsAdapter.CategoryIconsClickCallback() {
        @Override
        public void onCategoryClick(int position) {

            blockExit = false;

            mViewModel.setChosenCategory(position);


        }
    };

    private  HashTagsAdapter.OnClickListener hashTagClickCallback = new HashTagsAdapter.OnClickListener() {
        @Override
        public void onClick(int position) {

            mViewModel.getHashTagsList().remove(position);
            hashTagsAdapter.notifyItemRemoved(position);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addNew_btnCategory:
                Log.d("@@@", "AddThingPresenter.btnCategory.onClick");


                showCategories();

                blockExit = true;

                break;

            case R.id.addNew_btnSave:
                Log.d("@@@", "AddThingPresenter.btnSave.onClick");

                if (isFullData()){

                    setHashTag(getView().getHashTagEditText().getText().toString());

                    getView().animateSaveData();

                    getHandler().post(
                            new Runnable() {
                                @Override
                                public void run() {

                                    mViewModel.saveThing();
                                }
                            }
                    );

                }
                break;

            case R.id.addNew_imageView:
                Log.d("@@@", "AddThingPresenter.ImageView.onClick");
                Dialogs.getImageDialog(
                        getView().getActivityContext(),
                        mViewModel.getImage().getValue())
                        .show();
                break;

            case R.id.addNew_btnHashtag:

                if (etHashTagShown){

                    setHashTag(getView().getHashTagEditText().getText().toString());

                    etHashTagShown = false;
                    getView().getBtnHashTag().setText("#HashTag");
                    getView().getHashTagEditText().setVisibility(View.INVISIBLE);
                    getView().getHashTagEditText().setText("#");

                } else {
                    etHashTagShown = true;
                    getView().getBtnHashTag()
                            .setText("#Add");
                    getView().getHashTagEditText()
                            .setVisibility(View.VISIBLE);

                    getView().getHashTagEditText()
                            .requestFocus();
                    getView().getHashTagEditText()
                            .setSelection(getView().getHashTagEditText().length());

//                    getView().showKeyboard();
                }


                break;
        }
    }

    private void setHashTag(String newHashTags) {


        if (newHashTags != null && newHashTags.length() > 1){

            newHashTags = newHashTags.replaceAll("\\s+","");
            String[] hashTagsSplit = newHashTags.split("#");

            for (String hashTag : hashTagsSplit) {

                if (hashTag.length()>0){

                    hashTag = "#"+hashTag;

                    mViewModel.setHashTag(hashTag);

                    if (hashTagsAdapter == null){

                        getView().getHashTagRecyclerView().setLayoutManager(
                                new LinearLayoutManager(
                                        getView().getActivityContext(),
                                        LinearLayoutManager.HORIZONTAL,false));

                        getView().getHashTagRecyclerView().setAdapter(
                                hashTagsAdapter = new HashTagsAdapter(
                                        mViewModel.getHashTagsList(),
                                        hashTagClickCallback));

                    } else {
                        hashTagsAdapter.notifyItemInserted(0);
                        getView().getHashTagRecyclerView().scrollToPosition(0);
                    }
                }

            }
        }
    }


    private boolean isFullData() {

        if (TextUtils.isEmpty(getView().getNameEditText().getText())){
            Log.d("@@@", "AddThingPresenter.onSave.isFullData = false, (no name");

            Toast.makeText(getView().getActivityContext(), "Please write name", Toast.LENGTH_SHORT).show();

            getView().getNameEditText().requestFocus();

            return false;

        } else {
            mViewModel.setName(getView().getNameEditText().getText().toString());
        }

        if (TextUtils.isEmpty(getView().getDescriptionEditText().getText())){
            Log.d("@@@", "AddThingPresenter.onSave.isFullData = false, (no description)");

            Toast.makeText(getView().getActivityContext(), "Please describe your thing", Toast.LENGTH_SHORT).show();

            getView().getDescriptionEditText().requestFocus();

            return false;

        } else {
            mViewModel.setDescription(getView().getDescriptionEditText().getText().toString());
        }

        if (!mViewModel.isCategoryChosen()){
            Log.d("@@@", "AddThingPresenter.onSave.isFullData = false (no category)");

            Toast.makeText(getView().getActivityContext(), "Please choose category", Toast.LENGTH_SHORT).show();

            showCategories();

            return false;

        }

        Log.d("@@@", "AddThingPresenter.onSave.isFullData = true");
        return true;
    }

    private void showCategories() {
        Log.d("@@@", "AddThingPresenter.showCategories");

        if (categoriesAdapter == null){

            getView().getCategoriesRecyclerView().setLayoutManager(
                    new LinearLayoutManager(
                            getView().getActivityContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false));

            getView().getCategoriesRecyclerView().setAdapter(
                    categoriesAdapter = new CategoryIconsAdapter(
                            mViewModel.getCategories(),
                            categoryClickCallback));

        }

        getView().animShowCategories();


    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (v.getId()){

            case R.id.addNew_etHashTag:


                if (actionId == EditorInfo.IME_ACTION_DONE){

                    setHashTag(getView().getHashTagEditText().getText().toString());

                    etHashTagShown = false;
                    getView().getBtnHashTag().setText("#HashTag");
                    getView().getHashTagEditText().setVisibility(View.INVISIBLE);
                    getView().getHashTagEditText().setText("#");

                }

                break;

            case R.id.addNew_etName:



                break;



        }

        return false;
    }


    private void showBtnHashTag(){
        if (!btnHashTagShown
                && !TextUtils.isEmpty(getView().getNameEditText().getText())
                && !TextUtils.isEmpty(getView().getDescriptionEditText().getText())
                && mViewModel.isCategoryChosen()){

            btnHashTagShown = true;
            getView().getBtnHashTag().setVisibility(View.VISIBLE);
        }
    }

    public void changeThing(int thingId) {

//        mRepository.setThingToChange(thingId);
//
//        getView().getImageView()
//                .setImageBitmap(mRepository.getThingToChange().getImgBitmap());
//
//        getView().getNameEditText()
//                .setText(mRepository.getThingToChange().getName());
//
//        getView().getDescriptionEditText()
//                .setText(mRepository.getThingToChange().getDescription());
//
//        getView().getBtnCategory()
//                .setText(mRepository.getCategories()
//                        .get(mRepository.getThingToChange().getCategory())
//                        .getName());
//
//        setHashTag(mRepository.getThingToChange().getHashTags());

    }
}

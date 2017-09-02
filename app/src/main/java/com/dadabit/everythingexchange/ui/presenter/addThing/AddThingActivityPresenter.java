package com.dadabit.everythingexchange.ui.presenter.addThing;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
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

import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.AddThingActivityRepo;
import com.dadabit.everythingexchange.ui.adapter.CategoryIconsAdapter;
import com.dadabit.everythingexchange.ui.adapter.HashTagsAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.Dialogs;

public class AddThingActivityPresenter extends BasePresenter<AddThingActivityView>
        implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher{


    private CategoryIconsAdapter categoriesAdapter;
    private HashTagsAdapter hashTagsAdapter;

    private AddThingActivityRepo mRepository;

    public boolean blockExit = false;

    private boolean etHashTagShown = false;
    private boolean btnHashTagShown = false;

    public AddThingActivityPresenter() {
        Log.d("@@@", "AddThingActivityPresenter.create");
        mRepository = new AddThingActivityRepo();
    }

    @Override
    public void attachView(AddThingActivityView addThingActivityView) {
        super.attachView(addThingActivityView);
        Log.d("@@@", "AddThingActivityPresenter.attachView");


        if (mRepository.getImgBitmap() != null){

            showContent();

        }


        setClickListeners();


    }

    private void showContent(){
        Log.d("@@@", "AddThingActivityPresenter.showContent");

        getView().getImageView()
                .setImageBitmap(mRepository.getImgBitmap());


        setHandler(new Handler());

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()){

                    getView().getNameEditText().requestFocus();

                    getView().showKeyboard();

                }
            }
        }, 300);

    }


    public void setImageCapture(Bitmap image){
        Log.d("@@@", "AddThingActivityPresenter.setImageCapture");

        mRepository.setImgBitmap(image);

        showContent();

    }

    private void setClickListeners() {
        getView().getBtnCategory().setOnClickListener(this);
        getView().getBtnSave().setOnClickListener(this);
        getView().getImageView().setOnClickListener(this);
        getView().getBtnHashTag().setOnClickListener(this);
        getView().getHashTagEditText().setOnEditorActionListener(this);
        getView().getNameEditText().addTextChangedListener(this);
        getView().getDescriptionEditText().addTextChangedListener(this);
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

            mRepository.setChosenCategory(position);

            getView().getBtnCategory().setText(mRepository.getCategories().get(position).getName());

            getView().animateCategoryChoose();

            showBtnHashTag();

        }
    };

    private  HashTagsAdapter.OnClickListener hashTagClickCallback = new HashTagsAdapter.OnClickListener() {
        @Override
        public void onClick(int position) {

            mRepository.getHashTagsList().remove(position);
            hashTagsAdapter.notifyItemRemoved(position);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addNew_btnCategory:
                Log.d("@@@", "AddThingPresenter.btnCategory.onClick");

                if (mRepository.getCategories() != null){

                    showCategories();

                    blockExit = true;

                }
                break;

            case R.id.addNew_btnSave:
                Log.d("@@@", "AddThingPresenter.btnSave.onClick");

                if (isFullData()){

                    setHashTag(getView().getHashTagEditText().getText().toString());

                    getView().animateSaveData();

                    mRepository.saveThing(new AddThingActivityRepo.SaveThingCallback() {
                        @Override
                        public void onSuccess(boolean isSaved) {

                            if (isSaved && isViewAttached()){
                                Log.d("@@@", "AddThingPresenter.SaveThing.onSuccess");

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        getView().getProgressBar().setVisibility(View.GONE);

                                        getView().goBack();

                                    }
                                });

                            } else {
                                Log.d("@@@", "AddThingPresenter.SaveThing.onError (!)");
                            }
                        }
                    });
                }
                break;

            case R.id.addNew_imageView:
                Log.d("@@@", "AddThingPresenter.ImageView.onClick");
                Dialogs.getImageDialog(
                        getView().getActivityContext(),
                        mRepository.getImgBitmap())
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

                    mRepository.setHashTag(hashTag);

                    if (hashTagsAdapter == null){

                        getView().getHashTagRecyclerView().setLayoutManager(
                                new LinearLayoutManager(
                                        getView().getActivityContext(),
                                        LinearLayoutManager.HORIZONTAL,false));

                        getView().getHashTagRecyclerView().setAdapter(
                                hashTagsAdapter = new HashTagsAdapter(
                                        mRepository.getHashTagsList(),
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
            mRepository.setName(getView().getNameEditText().getText().toString());
        }

        if (TextUtils.isEmpty(getView().getDescriptionEditText().getText())){
            Log.d("@@@", "AddThingPresenter.onSave.isFullData = false, (no description)");

            Toast.makeText(getView().getActivityContext(), "Please describe your thing", Toast.LENGTH_SHORT).show();

            getView().getDescriptionEditText().requestFocus();

            return false;

        } else {
            mRepository.setDescription(getView().getDescriptionEditText().getText().toString());
        }

        if (mRepository.getChosenCategory() == -1){
            Log.d("@@@", "AddThingPresenter.onSave.isFullData = false (no category)");

            Toast.makeText(getView().getActivityContext(), "Please choose category", Toast.LENGTH_SHORT).show();

            showCategories();

            return false;

        }

        Log.d("@@@", "AddThingPresenter.onSave.isFullData = true");
        return true;
    }

    private void showCategories() {
        Log.d("@@@", "AddThingPresenter.showCategories: "+mRepository.getCategories().size());

        if (categoriesAdapter == null){

            getView().getCategoriesRecyclerView().setLayoutManager(
                    new LinearLayoutManager(getView().getActivityContext(), LinearLayoutManager.HORIZONTAL,false));

            getView().getCategoriesRecyclerView().setAdapter(
                    categoriesAdapter = new CategoryIconsAdapter(
                            mRepository.getCategories(),
                            categoryClickCallback));

        }

        Animation animation = AnimationUtils
                .loadAnimation(
                        getView().getActivityContext(),
                        R.anim.slide_in_right);
        animation.setDuration(600);

        getView().getCategoriesRecyclerView().startAnimation(animation);
        getView().getCategoriesRecyclerView().setVisibility(View.VISIBLE);
        getView().getBtnSave().hide();


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

        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        showBtnHashTag();

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void showBtnHashTag(){
        if (!btnHashTagShown
                && !TextUtils.isEmpty(getView().getNameEditText().getText())
                && !TextUtils.isEmpty(getView().getDescriptionEditText().getText())
                && mRepository.getChosenCategory() != -1
                ){

            btnHashTagShown = true;
            getView().getBtnHashTag().setVisibility(View.VISIBLE);
        }
    }

    public void changeThing(int thingId) {

        mRepository.setThingToChange(thingId);

        getView().getImageView()
                .setImageBitmap(mRepository.getThingToChange().getImgBitmap());

        getView().getNameEditText()
                .setText(mRepository.getThingToChange().getName());

        getView().getDescriptionEditText()
                .setText(mRepository.getThingToChange().getDescription());

        getView().getBtnCategory()
                .setText(mRepository.getCategories()
                        .get(mRepository.getThingToChange().getCategory())
                        .getName());

        setHashTag(mRepository.getThingToChange().getHashTags());

    }
}

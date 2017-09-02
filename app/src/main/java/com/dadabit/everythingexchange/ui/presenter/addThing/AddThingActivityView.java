package com.dadabit.everythingexchange.ui.presenter.addThing;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dadabit.everythingexchange.ui.presenter.MvpView;

public interface AddThingActivityView extends MvpView{

    void animateCategoryChoose();

    void animateSaveData();

    void goBack();

    void captureImage();

    void showKeyboard();

    ImageView getImageView();

    EditText getNameEditText();

    EditText getDescriptionEditText();

    Button getBtnCategory();

    FloatingActionButton getBtnSave();

    ProgressBar getProgressBar();

    RecyclerView getCategoriesRecyclerView();

    RecyclerView getHashTagRecyclerView();

    Button getBtnHashTag();

    EditText getHashTagEditText();

}

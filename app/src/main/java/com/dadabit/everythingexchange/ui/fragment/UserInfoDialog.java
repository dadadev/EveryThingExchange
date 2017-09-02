package com.dadabit.everythingexchange.ui.fragment;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoDialog extends DialogFragment implements TextWatcher, View.OnClickListener{


    @BindView(R.id.dialog_user_info_imageView) ImageView ivUserPic;
    @BindView(R.id.dialog_user_info_editText) EditText etUserName;
    @BindView(R.id.dialog_user_info_button) Button btnChange;
    @BindView(R.id.dialog_user_info_progressBar) ProgressBar progressBar;

    public static final String ARGUMENT_NAME = "user_info_dialog_name";
    public static final String ARGUMENT_IMAGE = "user_info_dialog_image";

    public static final int ARGUMENT_SHOW_IMAGE = 1;
    public static final int ARGUMENT_SEND_IMAGE = 2;

    private Bitmap changedBitmap;


    public static UserInfoDialog newInstance(String name, String imgUrl){

        UserInfoDialog dialog = new UserInfoDialog();
        Bundle args = new Bundle();

        args.putString(ARGUMENT_NAME, name);
        args.putString(ARGUMENT_IMAGE, imgUrl);

        dialog.setArguments(args);

        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("@@@", "UserInfoDialog.onCreateDialog");

        View view = View.inflate(getActivity(), R.layout.dialog_user_info, null);
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        if (dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogUserInfo;
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        }


        ButterKnife.bind(this, view);


        etUserName.setText(getArguments().getString(ARGUMENT_NAME));
        etUserName.setSelection(etUserName.getText().length());
        etUserName.addTextChangedListener(this);

        btnChange.setOnClickListener(this);
        ivUserPic.setOnClickListener(this);

        Glide.with(getActivity())
                .load(getArguments().getString(ARGUMENT_IMAGE))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivUserPic);


        return dialog;
    }


    @Override
    public void onClick(View v) {

        UserChangeDialogListener listener = (UserChangeDialogListener) getActivity();

        switch (v.getId()){
            case R.id.dialog_user_info_button:

                if (changedBitmap != null){
                    listener.onUserImageChange(changedBitmap);
                }

                final String name = String.valueOf(etUserName.getText());

                if (name.length()>0){

                    listener.onUserNameChange(name);

                }

                getDialog().dismiss();


                break;

            case R.id.dialog_user_info_imageView:

                progressBar.setVisibility(View.VISIBLE);

                listener.onUserImageChange(null);

                break;
        }

    }


    public void changeImage(Bitmap bitmap) {

        if (isAdded()){

            progressBar.setVisibility(View.GONE);

            ivUserPic.setImageBitmap(bitmap);

            changedBitmap = bitmap;

            showButton();
        }




    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        showButton();


    }

    private void showButton() {
        btnChange.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top));
        btnChange.setVisibility(View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public interface UserChangeDialogListener {

        void  onUserNameChange(String name);

        void onUserImageChange(Bitmap bitmap);
    }


}

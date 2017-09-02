package com.dadabit.everythingexchange.utils;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;

import java.util.Calendar;

public class Dialogs {

    public static Dialog getImageDialog(final Context context, Bitmap bitmap) {
        final Dialog mDialog = new Dialog(context, R.style.Dialog_Fullscreen);
        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setContentView(imageView);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        return mDialog;
    }

    public static Dialog getUrlImageDialog(final Context context, String url) {
        final Dialog mDialog = new Dialog(context, R.style.Dialog_Fullscreen);
        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setContentView(imageView);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        Glide.with(context)
                .load(url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        return mDialog;
    }


//    public static void getCategoryDialog(Context context, DialogInterface.OnClickListener clickListener){
//        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
//        //alt_bld.setIcon(R.drawable.icon);
//        alt_bld.setTitle("Select a category");
//        alt_bld.setSingleChoiceItems(
//                context.getResources().getStringArray(R.array.categories),
//                -1,
//                clickListener);
//        AlertDialog alert = alt_bld.create();
//        alert.show();
//
//    }

//    public static void getCategoryDialog(final Context context, final Button button, final AddThingPresenter presenter){
//        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
//        //alt_bld.setIcon(R.drawable.icon);
//        alt_bld.setTitle("Select a category");
//        alt_bld.setSingleChoiceItems(
//                context.getResources().getStringArray(R.array.categories),
//                -1,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        button.setText(context.getResources().getStringArray(R.array.categories)[item]);
//                        presenter.setCategory(item);
//                        dialog.dismiss();// dismiss the alertbox after chose option
//                    }
//                });
//        AlertDialog alert = alt_bld.create();
//        alert.show();
//
//    }

    public static DatePickerDialog getDateDialog(Context context, DatePickerDialog.OnDateSetListener changeEndDateListener) {
        Log.d("@@@", "Dialogs.showDateDialog");

        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                changeEndDateListener,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("Choose date of Exchange");
        datePickerDialog.getWindow()
                .getAttributes()
                .windowAnimations = R.style.Slide_inOut_animation;
        return datePickerDialog;
    }

    public static void showDateDialog(Context context, DatePickerDialog.OnDateSetListener changeEndDateListener) {
        Log.d("@@@", "Dialogs.showDateDialog");

        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                changeEndDateListener,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("Choose date of Exchange");
        datePickerDialog.getWindow()
                .getAttributes()
                .windowAnimations = R.style.Slide_inOut_animation;
        datePickerDialog.show();
    }

    public static AlertDialog getAlertDialog(Context context,
                                             String message,
                                             DialogInterface.OnClickListener positiveListener,
                                             DialogInterface.OnClickListener negativeListener){
        Log.d("@@@", "Dialogs.AlertDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", positiveListener);
        if (negativeListener != null){

            builder.setNegativeButton("No", negativeListener);

        } else {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("@@@", "Dialogs.AlertDialog.NegativeButton.onClick");
                }
            });

        }

        return builder.create();
    }

    public static AlertDialog getExchangeCompleteDialog (Context context,
                                                         DialogInterface.OnClickListener positiveListener,
                                                         DialogInterface.OnClickListener negativeListener){
        Log.d("@@@", "Dialogs.ExchangeCompleteDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.chats_dialog_message_complete);
        builder.setPositiveButton("Yes", positiveListener);
        builder.setNegativeButton("Change date", negativeListener);
        builder.setCancelable(false);

        return builder.create();
    }
}
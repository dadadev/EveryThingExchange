package com.dadabit.everythingexchange.model.firebase;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageUploader {


    private byte[] imgBlob;
    private String uid;
    private long timestamp;
    private OnFinishListener mCallback;



    public ImageUploader(byte[] imgBlob,
                         String uid,
                         long timestamp,
                         OnFinishListener mCallback) {
        this.imgBlob = imgBlob;
        this.uid = uid;
        this.timestamp = timestamp;
        this.mCallback = mCallback;
    }

    public void send(){
        Log.d("@@@", "FireBaseStorage.uploadImage.send");

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        firebaseStorage.setMaxUploadRetryTimeMillis(2000);

        StorageReference storageReference =
                firebaseStorage
                        .getReferenceFromUrl("gs://everythingexchange-29da5.appspot.com/")
                        .child(uid)
                        .child(timestamp+"");


        storageReference
                .putBytes(imgBlob)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("@@@", "FireBaseStorage.OnSuccessListener.onSuccess");

                                try {
                                    @SuppressWarnings("VisibleForTests")
                                    final String imgLink = taskSnapshot.getDownloadUrl().toString();

                                    mCallback.onFinish(imgLink);

                                } catch (NullPointerException e){

                                    mCallback.onFinish(null);
                                }

                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mCallback.onFinish(null);
                                Log.e("@@@", "--- (!!!) --- FireBaseStorage.uploadImage.onFailure");

                            }
                        })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {



                            }
                });

    }

    public interface OnFinishListener{
        void onFinish(String url);
    }
}

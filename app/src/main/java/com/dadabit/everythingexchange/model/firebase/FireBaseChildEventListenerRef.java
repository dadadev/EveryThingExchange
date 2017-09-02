package com.dadabit.everythingexchange.model.firebase;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

public class FireBaseChildEventListenerRef {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;

    public FireBaseChildEventListenerRef(DatabaseReference mDatabaseReference, ChildEventListener childEventListener) {
        this.mDatabaseReference = mDatabaseReference;
        this.childEventListener = childEventListener;

        // Start listening on this ref.
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    public void detach() {
        mDatabaseReference.removeEventListener(childEventListener);
        Log.d("@@@", "FireBaseChildEventListenerRef.remove");
    }
}

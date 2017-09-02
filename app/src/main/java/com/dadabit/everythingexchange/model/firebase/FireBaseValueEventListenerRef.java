package com.dadabit.everythingexchange.model.firebase;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FireBaseValueEventListenerRef {

    private DatabaseReference mDatabaseReference;
    private ValueEventListener valueEventListener;

    public FireBaseValueEventListenerRef(DatabaseReference mDatabaseReference, ValueEventListener valueEventListener) {
        this.mDatabaseReference = mDatabaseReference;
        this.valueEventListener = valueEventListener;

        // Start listening on this ref.
        mDatabaseReference.addValueEventListener(valueEventListener);
    }

    public void detach() {
        mDatabaseReference.removeEventListener(valueEventListener);
        Log.d("@@@", "FireBaseValueEventListenerRef.remove");
    }
}

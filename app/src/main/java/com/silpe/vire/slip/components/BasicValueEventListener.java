package com.silpe.vire.slip.components;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class BasicValueEventListener implements ValueEventListener {

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

}

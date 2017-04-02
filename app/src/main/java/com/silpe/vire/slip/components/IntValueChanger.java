package com.silpe.vire.slip.components;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class IntValueChanger implements Transaction.Handler {

    private final boolean increment;

    public IntValueChanger(boolean increment) {
        this.increment = increment;
    }

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        Integer value = mutableData.getValue(Integer.class);
        if (value == null) return Transaction.success(mutableData);
        if (increment) value++;
        else value--;
        mutableData.setValue(value);
        return Transaction.success(mutableData);
    }

    @Override
    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
    }

}

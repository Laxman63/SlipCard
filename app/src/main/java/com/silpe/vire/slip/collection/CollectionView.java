package com.silpe.vire.slip.collection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.silpe.vire.slip.dtos.User;

public class CollectionView extends RecyclerView {

    public CollectionView(Context context) {
        this(context, null);
    }

    public CollectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void insertNew(User user) {
        /*
         * TODO
         * -- Scroll to the top only when the user is near the top
         * -- Immediately scroll to top, not smoothly, when doing initial load
         */
        CollectionAdapter adapter = (CollectionAdapter) getAdapter();
        adapter.insertNew(user);
        smoothScrollToPosition(0);
    }

}

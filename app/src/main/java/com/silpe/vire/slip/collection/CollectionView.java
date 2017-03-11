package com.silpe.vire.slip.collection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.silpe.vire.slip.dtos.User;

import java.util.List;

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

    void update(User user) {
        CollectionAdapter adapter = (CollectionAdapter) getAdapter();
        if (adapter.update(user)) {
            smoothScrollToPosition(0);
        }
    }

    void update(List<User> users) {
        CollectionAdapter adapter = (CollectionAdapter) getAdapter();
        adapter.update(users);
    }

    void remove(String uid) {
        CollectionAdapter adapter = (CollectionAdapter) getAdapter();
        adapter.remove(uid);
    }

}

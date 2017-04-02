package com.silpe.vire.slip.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;

public class MemeFragment extends Fragment {

    public static MemeFragment newInstance(User user) {
        MemeFragment frag = new MemeFragment();
        frag.user = user;
        return frag;
    }

    private User user;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;

    public MemeFragment() {
        this.user = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_meme, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        listView = (RecyclerView) view.findViewById(R.id.itemsRecyclerView);
        return view;
    }
}

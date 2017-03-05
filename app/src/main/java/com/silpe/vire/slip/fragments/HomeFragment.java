package com.silpe.vire.slip.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.silpe.vire.slip.CollectionListAdapter;
import com.silpe.vire.slip.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView collectionList = (ListView) view.findViewById(R.id.collectionList);
        final List<String> values = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
            values.add("xd" + i);
        }
        final CollectionListAdapter collectionAdapter = new CollectionListAdapter(this.getContext(), R.layout.collection_card_preview, R.id.secondLine, values);
        collectionList.setAdapter(collectionAdapter);
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                final ViewPropertyAnimator viewPropertyAnimator = view.animate().setDuration(1000).alpha(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            values.remove(item);
                            collectionAdapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                } else {
                    viewPropertyAnimator.withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            values.remove(item);
                            collectionAdapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                }
            }
        });
        return view;
    }

}

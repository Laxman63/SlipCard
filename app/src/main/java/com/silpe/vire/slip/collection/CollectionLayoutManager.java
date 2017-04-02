package com.silpe.vire.slip.collection;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class CollectionLayoutManager extends LinearLayoutManager {

    public CollectionLayoutManager(Context context) {
        super(context, VERTICAL, false);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView view, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller scroller = new CollectionSmoothScroller(view.getContext());
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    private class CollectionSmoothScroller extends LinearSmoothScroller {

        /**
         * Milliseconds of time to scroll for the number of {@code dp} units specified below.
         */
        private static final float X = 50;
        /**
         * Number of {@code dp} units to scroll per time interval specified above.
         */
        private static final float Y = 15;

        private CollectionSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return CollectionLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return X / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Y, displayMetrics);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

    }

}

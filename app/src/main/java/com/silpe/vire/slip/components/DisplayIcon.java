package com.silpe.vire.slip.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DisplayIcon extends android.support.v7.widget.AppCompatImageView {

    public DisplayIcon(final Context context) {
        super(context);
    }

    public DisplayIcon(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DisplayIcon(final Context context, final AttributeSet attributeSet, final int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        if (measuredWidth > measuredHeight) {
            setMeasuredDimension(measuredHeight, measuredHeight);
        } else {
            setMeasuredDimension(measuredWidth, measuredWidth);
        }
    }

}

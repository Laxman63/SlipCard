package com.silpe.vire.slip.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * This class is used with {@code Glide} to create a rounded profile picture.
 */
public class RoundedBitmap extends BitmapImageViewTarget {

    private final Context context;

    public RoundedBitmap(ImageView view, Context context) {
        super(view);
        this.context = context;
    }

    @Override
    protected void setResource(Bitmap resource) {
        RoundedBitmapDrawable circularDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
        circularDrawable.setCircular(true);
        getView().setImageDrawable(circularDrawable);

    }

}

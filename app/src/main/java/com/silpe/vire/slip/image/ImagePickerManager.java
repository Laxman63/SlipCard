package com.silpe.vire.slip.image;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.silpe.vire.slip.R;

class ImagePickerManager extends PickerManager {

    ImagePickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.image_selectImageApp)), REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void setUri(Uri uri) {
        mProcessingPhotoUri = uri;
    }

}

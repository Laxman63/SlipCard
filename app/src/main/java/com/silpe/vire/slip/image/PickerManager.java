package com.silpe.vire.slip.image;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.silpe.vire.slip.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

abstract class PickerManager {

    static final int REQUEST_CODE_SELECT_IMAGE = 200;
    static final int REQUEST_CODE_IMAGE_PERMISSION = 201;

    Uri mProcessingPhotoUri;
    Activity activity;

    private int cropActivityColor;
    private boolean withTimeStamp;
    private String folder;
    private String imageName;

    private UCrop uCrop;
    private PickerBuilder.onImageReceivedListener imageReceivedListener;
    private PickerBuilder.onPermissionRefusedListener permissionRefusedListener;

    private UCrop.Options options;

    PickerManager(Activity activity) {
        this.activity = activity;
        this.imageName = activity.getString(R.string.app_name);
        cropActivityColor = R.color.colorPrimary;
        withTimeStamp = true;
        folder = null;
        options = new UCrop.Options();
    }

    PickerManager setOnImageReceivedListener(PickerBuilder.onImageReceivedListener listener) {
        this.imageReceivedListener = listener;
        return this;
    }

    PickerManager setOnPermissionRefusedListener(PickerBuilder.onPermissionRefusedListener listener) {
        this.permissionRefusedListener = listener;
        return this;
    }

    protected abstract void sendToExternalApp();

    void pickPhotoWithPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_IMAGE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_IMAGE_PERMISSION);
            }
        } else {
            sendToExternalApp();
        }
    }

    void handlePermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted
            sendToExternalApp();

        } else {
            // permission denied
            if (permissionRefusedListener != null) {
                permissionRefusedListener.onPermissionRefused();
            }
            activity.finish();
        }
    }


    Uri getImageFile() {
        String imagePathStr = Environment.getExternalStorageDirectory() + "/" + (folder == null ? Environment.DIRECTORY_DCIM + "/" + activity.getString(R.string.app_name) : folder);

        File path = new File(imagePathStr);
        if (!path.exists()) {
            boolean success = path.mkdir();
            if (!success) {
                return null;
            }
        }

        String finalPhotoName = imageName + (withTimeStamp ? "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(System.currentTimeMillis())) : "") + ".jpg";
        File photo = new File(path, finalPhotoName);
        return Uri.fromFile(photo);
    }

    public void setUri(Uri uri) {
    }

    void startCropActivity() {
        if (uCrop == null) {
            uCrop = UCrop.of(mProcessingPhotoUri, getImageFile());
            uCrop = uCrop.useSourceImageAspectRatio();
            options.setFreeStyleCropEnabled(true);

            int color = ContextCompat.getColor(activity, cropActivityColor);
            options.setToolbarColor(color);
            options.setStatusBarColor(color);
            options.setActiveWidgetColor(color);
            uCrop = uCrop.withOptions(options);
        }
        uCrop.start(activity);
    }

    void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (imageReceivedListener != null)
            imageReceivedListener.onImageReceived(resultUri);

        activity.finish();
    }


    PickerManager setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    PickerManager setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    PickerManager setCropActivityColor(int cropActivityColor) {
        this.cropActivityColor = cropActivityColor;
        return this;
    }

    PickerManager withTimeStamp(boolean withTimeStamp) {
        this.withTimeStamp = withTimeStamp;
        return this;
    }

    PickerManager setImageFolderName(String folder) {
        this.folder = folder;
        return this;
    }

    PickerManager setCustomizedUcrop(UCrop customizedUcrop) {
        this.uCrop = customizedUcrop;
        return this;
    }

    PickerManager withAspectRatio(AspectRatio aspectRatio) {
        options.setAspectRatioOptions(0, aspectRatio);
        return this;
    }

}

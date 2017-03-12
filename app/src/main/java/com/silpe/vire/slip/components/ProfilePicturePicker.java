package com.silpe.vire.slip.components;


import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.PickerBuilder;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

import java.io.File;

/**
 * This class handles a callback that starts the picture
 * picker and cropper activity, allowing the user to select
 * a profile picture from his gallery.
 */
public class ProfilePicturePicker implements View.OnClickListener {

    private final ProfileDisplayComponent display;

    public ProfilePicturePicker(ProfileDisplayComponent display) {
        this.display = display;
    }

    @Override
    public void onClick(View v) {
        new PickerBuilder(display.getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(new ProfilePictureReceivedListener())
                .setImageName(display.getString(R.string.profile_picture))
                .setImageFolderName(display.getString(R.string.picture_folder))
                .start();
    }

    private class ProfilePictureReceivedListener implements PickerBuilder.onImageReceivedListener {
        @Override
        public void onImageReceived(Uri imageUri) {
            User user = SessionModel.get().getUser(display.getContext());
            String databaseUsers = display.getString(R.string.database_users);
            // Upload the selected file
            FirebaseStorage.getInstance()
                    .getReference()
                    .child(databaseUsers)
                    .child(user.getUid())
                    .child(display.getString(R.string.database_profile_picture))
                    .putFile(imageUri);
            // Update the image signature
            TimestampSignature signature = new TimestampSignature();
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(databaseUsers)
                    .child(user.getUid())
                    .child(display.getString(R.string.database_user_signature))
                    .setValue(signature.getSignature());
            // Update the user profile picture
            Glide.with(display.getActivity())
                    .load(new File(imageUri.getPath()))
                    .signature(signature)
                    .into(display.getProfilePicture());
        }
    }

}
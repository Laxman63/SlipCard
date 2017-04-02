package com.silpe.vire.slip.components;

import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
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
                .withAspectRatio(1, 1)
                .start();
    }

    private class ProfilePictureReceivedListener implements PickerBuilder.onImageReceivedListener {
        @Override
        public void onImageReceived(Uri imageUri) {
            User user = SessionModel.get().getUser(display.getContext());
            // Upload the selected file
            user.getProfilePictureReference(display.getContext()).putFile(imageUri);
            // Update the image signature
            TimestampSignature signature = new TimestampSignature();
            user.getDatabaseReference(display.getContext()).child(display.getString(R.string.database_user_signature)).setValue(signature.getSignature());
            user.setSignature(signature.getSignature());
            SessionModel.get().setUser(user, display.getContext());
            // Update the user profile picture
            Glide.with(display.getActivity())
                    .load(new File(imageUri.getPath()))
                    .asBitmap()
                    .centerCrop()
                    .signature(signature)
                    .into(new RoundedBitmap(display.getProfilePicture(), display.getContext()));
        }
    }

}
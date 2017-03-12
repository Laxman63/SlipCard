package com.silpe.vire.slip.components;

import android.app.Activity;
import android.content.Context;

/**
 * This abstract class describes any fragment that
 * might be displaying the active user's profile picture.
 */
public interface ProfileDisplayComponent {

    Context getContext();

    Activity getActivity();

    ProfilePictureView getProfilePicture();

    String getString(int stringId);

}

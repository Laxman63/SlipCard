package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfileDisplayComponent;
import com.silpe.vire.slip.components.ProfilePicturePicker;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.components.RoundedBitmap;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.models.SessionModelListener;

public class MyCardFragment extends Fragment
        implements SessionModelListener<User>, ProfileDisplayComponent {

    // References to the user information views
    private TextView mDescription;
    private TextView mEmailView;
    private TextView mPhoneNumberView;
    private TextView mFullname;

    /**
     * Reference to the user profile picture {@code ImageView}.
     * Clicking this image view should enable the user to
     * immediately go select his profile picture, and it will
     * be promptly updated.
     */
    private ProfilePictureView mProfilePictureView;

    public MyCardFragment() {
        super();
    }

    /**
     * Upon creation, this fragment will add itself as a {@code User}
     * listener to the {@code SessionModel}.
     */
    @Override
    public void onStart() {
        super.onStart();
        SessionModel.get().addListener(this);
    }

    /**
     * When stopping, this fragment will remove itself as a
     * {@code User} listener.
     */
    @Override
    public void onStop() {
        super.onStop();
        SessionModel.get().removeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        mFullname = ((TextView) view.findViewById(R.id.connection_fullName));
        mDescription =  ((TextView) view.findViewById(R.id.connection_description));
        mEmailView = ((TextView) view.findViewById(R.id.connection_email));
        mPhoneNumberView = ((TextView) view.findViewById(R.id.connection_phoneNumber));
        mProfilePictureView = ((ProfilePictureView) view.findViewById(R.id.connection_profilePicture));
        updateUser();

        mProfilePictureView.setOnClickListener(new ProfilePicturePicker(this));
        return view;
    }

    /**
     * Get the reference to the profile picture {@code ImageView}. This
     * method enables outside classes to modify the user's profile picture
     *
     * @return the profile picture view
     */
    @Override
    public ProfilePictureView getProfilePicture() {
        return mProfilePictureView;
    }

    /**
     * Calling this method will prompt the fragment to refresh
     * the display of the user's information. This method is called
     * on creation to populate the view with the user's data and
     * whenever the user changes his data.
     */
    private void updateUser() {
        User user = SessionModel.get().getUser(getContext());

        // TODO Improve the display of user information
        mFullname.setText(user.getFullName());
        mDescription.setText(user.getDescription());
        mEmailView.setText(user.getEmail());
        // TODO Add clickable prompts to add missing info
        String phoneNumber = user.getPhoneNumber();
        mPhoneNumberView.setText(phoneNumber.isEmpty() ? "+ Add a phone number" : phoneNumber);
        if (user.getSignature() > 0) {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(user.getProfilePictureReference(getContext()))
                    .asBitmap()
                    .centerCrop()
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(new RoundedBitmap(mProfilePictureView, getContext()));
        } else {
            mProfilePictureView.setImageResource(R.drawable.empty_profile_round);
        }
        // TODO End
    }

    /**
     * This callback fires whenever the {@code SessionModel} receives
     * a call that changes the {@code User} model. We require a refresh
     * of the user's information.
     *
     * @param user the new value
     */
    @Override
    public void valueUpdated(User user) {
        updateUser();
    }

}
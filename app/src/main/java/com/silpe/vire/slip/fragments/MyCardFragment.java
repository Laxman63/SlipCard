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
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.models.SessionModelListener;

public class MyCardFragment extends Fragment
        implements SessionModelListener<User>, ProfileDisplayComponent {

    // References to the user information views
    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mOccupationView;
    private TextView mCompanyView;
    private TextView mEmailView;
    private TextView mPhoneNumberView;

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
        mFirstNameView = ((TextView) view.findViewById(R.id.show_firstName));
        mLastNameView = ((TextView) view.findViewById(R.id.show_lastName));
        mOccupationView = ((TextView) view.findViewById(R.id.show_occupation));
        mCompanyView = ((TextView) view.findViewById(R.id.show_company));
        mEmailView = ((TextView) view.findViewById(R.id.show_email));
        mPhoneNumberView = ((TextView) view.findViewById(R.id.show_phone));
        mProfilePictureView = ((ProfilePictureView) view.findViewById(R.id.show_profile_picture));
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
        mFirstNameView.setText(user.getFirstName());
        mLastNameView.setText(user.getLastName());
        mOccupationView.setText(user.getOccupation());
        mCompanyView.setText(user.getCompany());
        mEmailView.setText(user.getEmail());
        // TODO Add clickable prompts to add missing info
        String phoneNumber = user.getPhoneNumber();
        mPhoneNumberView.setText(phoneNumber.isEmpty() ? "+ Add a phone number" : phoneNumber);
        if (user.getSignature() > 0) {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(user.getProfilePictureReference(getContext()))
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(mProfilePictureView);
        } else {
            mProfilePictureView.setImageResource(R.drawable.empty_profile);
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
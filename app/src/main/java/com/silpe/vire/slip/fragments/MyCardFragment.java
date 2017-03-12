package com.silpe.vire.slip.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.PickerBuilder;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

import java.io.File;

public class MyCardFragment extends Fragment {

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
    private ImageView mProfilePictureView;

    public MyCardFragment() {
        super();
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
        mProfilePictureView = ((ImageView) view.findViewById(R.id.show_profile_picture));
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
    ImageView getProfilePictureView() {
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
            StorageReference sRef = FirebaseStorage.getInstance().getReference();
            sRef = sRef.child(getString(R.string.database_users)).child(user.getUid()).child(getString(R.string.database_profile_picture));
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(sRef)
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(mProfilePictureView);
        } else {
            mProfilePictureView.setImageResource(R.drawable.empty_profile);
        }
        // TODO End
    }

}

class ProfilePicturePicker implements View.OnClickListener {

    private final MyCardFragment fragment;

    ProfilePicturePicker(MyCardFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        new PickerBuilder(fragment.getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(new ProfilePictureReceivedListener())
                .setImageName(fragment.getString(R.string.profile_picture))
                .setImageFolderName(fragment.getString(R.string.picture_folder))
                .start();
    }

    private class ProfilePictureReceivedListener implements PickerBuilder.onImageReceivedListener {
        @Override
        public void onImageReceived(Uri imageUri) {
            User user = SessionModel.get().getUser(fragment.getContext());
            String databaseUsers = fragment.getString(R.string.database_users);
            // Upload the selected file
            FirebaseStorage.getInstance()
                    .getReference()
                    .child(databaseUsers)
                    .child(user.getUid())
                    .child(fragment.getString(R.string.database_profile_picture))
                    .putFile(imageUri);
            // Update the image signature
            TimestampSignature signature = new TimestampSignature();
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(databaseUsers)
                    .child(user.getUid())
                    .child(fragment.getString(R.string.database_user_signature))
                    .setValue(signature.getSignature());
            // Update the user profile picture
            Glide.with(fragment)
                    .load(new File(imageUri.getPath()))
                    .signature(signature)
                    .into(fragment.getProfilePictureView());
        }
    }

}
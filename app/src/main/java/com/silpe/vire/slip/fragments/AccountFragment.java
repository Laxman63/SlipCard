package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfileDisplayComponent;
import com.silpe.vire.slip.components.ProfilePicturePicker;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.dtos.Validator;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

/**
 * This fragment is an entire screen that allows the user to
 * edit with more freedom his user information. The user must
 * click "Accept" in order to apply the changes. The exception
 * is for changing the profile picture.
 */
public class AccountFragment extends Fragment implements ProfileDisplayComponent {

    // User information input fields
    private TextInputEditText mFirstNameField;
    private TextInputEditText mLastNameField;
    private TextInputEditText mOccupationField;
    private TextInputEditText mCompanyField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPhoneNumberField;

    /**
     * A reference to the profile picture view. Tapping this
     * view will prompt the user to select a new profile picture.
     */
    private ProfilePictureView mProfilePictureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Obtain references to input fields
        mFirstNameField = (TextInputEditText) view.findViewById(R.id.account_firstNameField);
        mLastNameField = (TextInputEditText) view.findViewById(R.id.account_lastNameField);
        mOccupationField = (TextInputEditText) view.findViewById(R.id.account_occupationField);
        mCompanyField = (TextInputEditText) view.findViewById(R.id.account_companyField);
        mEmailField = (TextInputEditText) view.findViewById(R.id.account_emailField);
        mPhoneNumberField = (TextInputEditText) view.findViewById(R.id.account_phoneNumberField);
        // Populate the input fields
        User user = SessionModel.get().getUser(getContext());
        mFirstNameField.setText(user.getFirstName());
        mLastNameField.setText(user.getLastName());
        mOccupationField.setText(user.getOccupation());
        mCompanyField.setText(user.getCompany());
        mEmailField.setText(user.getEmail());
        mPhoneNumberField.setText(user.getPhoneNumber());
        // Obtain reference to profile picture view and load the image
        mProfilePictureView = (ProfilePictureView) view.findViewById(R.id.account_profile_picture);
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
        // Bind click listeners to the accept button and profile picture
        Button acceptButton = (Button) view.findViewById(R.id.account_accept_button);
        acceptButton.setOnClickListener(new AcceptButtonListener());
        mProfilePictureView.setOnClickListener(new ProfilePicturePicker(this));
        return view;
    }

    /**
     * Check the user's inputs in the fields. If there are any errors,
     * indicate them to the user. This method will check the fields in reverse
     * so that the user is directed to the first field with an error. If
     * there are no errors, this method will update the database.
     * <p>
     * Required fields: first name, last name, occupation, company, email.
     */
    private void doUpdate() {
        String firstName = mFirstNameField.getText().toString();
        String lastName = mLastNameField.getText().toString();
        String occupation = mOccupationField.getText().toString();
        String company = mCompanyField.getText().toString();
        String email = mEmailField.getText().toString();
        String phoneNumber = mPhoneNumberField.getText().toString();
        View focusView = null;
        boolean cancel = false;
        // Validate required fields
        String errorFieldRequired = getString(R.string.error_field_required);
        if (!TextUtils.isEmpty(phoneNumber) && !Validator.isValidPhoneNumber(phoneNumber)) {
            mPhoneNumberField.setError(getString(R.string.error_invalid_phoneNumber));
        }
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(errorFieldRequired);
            focusView = mEmailField;
            cancel = true;
        } else if (!Validator.isValidEmail(email)) {
            mEmailField.setError(getString(R.string.error_invalid_email));
            focusView = mEmailField;
            cancel = true;
        }
        if (TextUtils.isEmpty(company)) {
            mCompanyField.setError(errorFieldRequired);
            focusView = mCompanyField;
            cancel = true;
        }
        if (TextUtils.isEmpty(occupation)) {
            mOccupationField.setError(errorFieldRequired);
            focusView = mOccupationField;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastName)) {
            mLastNameField.setError(errorFieldRequired);
            focusView = mLastNameField;
            cancel = true;
        }
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameField.setError(errorFieldRequired);
            focusView = mFirstNameField;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            doUpdate(firstName, lastName, occupation, company, email, phoneNumber);
        }
    }

    /**
     * Send the updated {@code} user model to the database.
     *
     * @param firstName   updated first name
     * @param lastName    updated last name
     * @param occupation  updated occupation
     * @param company     updated company
     * @param email       updated email
     * @param phoneNumber email phone number if any
     */
    private void doUpdate(
            String firstName, String lastName,
            String occupation, String company,
            String email, String phoneNumber) {
        User user = SessionModel.get().getUser(getContext());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setOccupation(occupation);
        user.setCompany(company);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        SessionModel.get().setUser(user, getContext());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.database_users))
                .child(user.getUid())
                .setValue(user);
    }

    /**
     * Obtain the reference to the {@code ProfilePictureView}
     * as required by the {@code ProfileDisplayComponent} interface.
     */
    @Override
    public ProfilePictureView getProfilePicture() {
        return mProfilePictureView;
    }

    /**
     * Listener for the accept button that will start an update.
     */
    private class AcceptButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            doUpdate();
        }
    }

}
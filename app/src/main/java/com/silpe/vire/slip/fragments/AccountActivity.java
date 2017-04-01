package com.silpe.vire.slip.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfileDisplayComponent;
import com.silpe.vire.slip.components.ProfilePicturePicker;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.dtos.Validator;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

public class AccountActivity extends AppCompatActivity implements ProfileDisplayComponent {

    public static final String RESULT_USER = "user";

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
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_account);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarAccount));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mFirstNameField = (TextInputEditText) findViewById(R.id.account_firstNameField);
        mLastNameField = (TextInputEditText) findViewById(R.id.account_lastNameField);
        mOccupationField = (TextInputEditText) findViewById(R.id.account_occupationField);
        mCompanyField = (TextInputEditText) findViewById(R.id.account_companyField);
        mEmailField = (TextInputEditText) findViewById(R.id.account_emailField);
        mPhoneNumberField = (TextInputEditText) findViewById(R.id.account_phoneNumberField);
        User user = SessionModel.get().getUser(this);
        mFirstNameField.setText(user.getFirstName());
        mLastNameField.setText(user.getLastName());
        mOccupationField.setText(user.getOccupation());
        mCompanyField.setText(user.getCompany());
        mEmailField.setText(user.getEmail());
        mPhoneNumberField.setText(user.getPhoneNumber());
        // Obtain reference to profile picture view and load the image
        mProfilePictureView = (ProfilePictureView) findViewById(R.id.account_profile_picture);
        if (user.getSignature() > 0) {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(user.getProfilePictureReference(this))
                    .signature(new TimestampSignature(user.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(mProfilePictureView);
        } else {
            mProfilePictureView.setImageResource(R.drawable.empty_profile);
        }
        // Bind click listeners to the accept button and profile picture
        Button acceptButton = (Button) findViewById(R.id.account_accept_button);
        acceptButton.setOnClickListener(new AcceptButtonListener());
        mProfilePictureView.setOnClickListener(new ProfilePicturePicker(this));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public ProfilePictureView getProfilePicture() {
        return mProfilePictureView;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(RESULT_USER, SessionModel.get().getUser(this));
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
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

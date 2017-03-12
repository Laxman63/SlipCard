package com.silpe.vire.slip.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silpe.vire.slip.R;

public class AccountFragment extends Fragment {

    private TextInputEditText mFirstNameField;
    private TextInputEditText mLastNameField;
    private TextInputEditText mOccupationField;
    private TextInputEditText mCompanyField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPhoneNumberField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Obtain references to input fields
        mFirstNameField = (TextInputEditText) view.findViewById(R.id.account_firstNameField);
        mLastNameField = (TextInputEditText) view.findViewById(R.id.account_lastNameField);
        mOccupationField = (TextInputEditText) view.findViewById(R.id.account_occupationField);
        mCompanyField = (TextInputEditText) view.findViewById(R.id.account_companyField);
        mEmailField = (TextInputEditText) view.findViewById(R.id.account_emailField);
        mPhoneNumberField = (TextInputEditText) view.findViewById(R.id.account_phoneNumberField);
        // Populate the input fields
        return view;
    }

}
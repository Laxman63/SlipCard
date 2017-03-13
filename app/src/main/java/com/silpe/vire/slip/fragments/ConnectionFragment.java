package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;

public class ConnectionFragment extends Fragment {

    public static ConnectionFragment newInstance(User user) {
        ConnectionFragment fragment = new ConnectionFragment();
        fragment.mUser = user;
        return fragment;
    }

    private User mUser;

    public ConnectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        TextView fullNameView = (TextView) view.findViewById(R.id.connection_fullName);
        TextView descriptionView = (TextView) view.findViewById(R.id.connection_description);
        TextView connectionsView = (TextView) view.findViewById(R.id.connection_connections);
        TextView phoneNumberView = (TextView) view.findViewById(R.id.connection_phoneNumber);
        TextView emailView = (TextView) view.findViewById(R.id.connection_email);
        fullNameView.setText(mUser.getFullName());
        descriptionView.setText(mUser.getDescription());
        connectionsView.setText("TODO: User has 57 connections");
        phoneNumberView.setText(mUser.getPhoneNumber().isEmpty() ? "TODO: remove when no phone" : mUser.getPhoneNumber());
        emailView.setText(mUser.getEmail());
        ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.connection_profilePicture);
        StorageReference reference = FirebaseStorage.getInstance()
                .getReference()
                .child(getString(R.string.database_users))
                .child(mUser.getUid())
                .child(getString(R.string.database_profilePicture));
        if (mUser.getSignature() > 0) {
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(reference)
                    .signature(new TimestampSignature(mUser.getSignature()))
                    .into(profilePictureView);
        } else {
            profilePictureView.setImageResource(R.drawable.empty_profile);
        }
        return view;
    }

}

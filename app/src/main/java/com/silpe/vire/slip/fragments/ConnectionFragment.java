package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

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

        // Bind button listeners
        Button removeButton = (Button) view.findViewById(R.id.connection_removeButton);
        removeButton.setOnClickListener(new RemoveUserListener());

        // Populate the text views with the user's information
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

        // Load and set the user's profile picture, if he has one
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
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile, null))
                    .into(profilePictureView);
        } else {
            profilePictureView.setImageResource(R.drawable.empty_profile);
        }
        return view;
    }

    private class RemoveUserListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final User user = SessionModel.get().getUser(getContext());
            final DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getContext().getString(R.string.database_connections))
                    .child(user.getUid());
            reference.orderByValue()
                    .equalTo(mUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // The mapping (key) -> (UID) should be bijective
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                reference.child(child.getKey()).setValue(null);
                            }
                            ConnectionFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO Handle error
                        }
                    });
        }
    }

}
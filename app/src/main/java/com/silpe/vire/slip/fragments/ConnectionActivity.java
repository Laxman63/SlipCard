package com.silpe.vire.slip.fragments;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.components.RoundedBitmap;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

public class ConnectionActivity extends AppCompatActivity {

    private User mUser;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_connection);
        mUser = getIntent().getParcelableExtra(AccountActivity.RESULT_USER);
        Button removeButton = (Button) findViewById(R.id.connection_removeButton);
        removeButton.setOnClickListener(new RemoveUserListener());

        // Populate the text views with the user's information
        TextView fullNameView = (TextView) findViewById(R.id.connection_fullName);
        TextView descriptionView = (TextView) findViewById(R.id.connection_description);
        //TextView connectionsView = (TextView) findViewById(R.id.connection_connections);
        TextView phoneNumberView = (TextView) findViewById(R.id.connection_phoneNumber);
        TextView emailView = (TextView) findViewById(R.id.connection_email);
        fullNameView.setText(mUser.getFullName());
        descriptionView.setText(mUser.getDescription());
        //connectionsView.setText("TODO: User has 57 connections");
        phoneNumberView.setText(mUser.getPhoneNumber().isEmpty() ? "TODO: remove when no phone" : mUser.getPhoneNumber());
        emailView.setText(mUser.getEmail());
        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.connection_profilePicture);

        // Load and set the user's profile picture, if he has one
        if (mUser.getSignature() > 0) {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(mUser.getProfilePictureReference(this))
                    .asBitmap()
                    .centerCrop()
                    .signature(new TimestampSignature(mUser.getSignature()))
                    .error(ResourcesCompat.getDrawable(getResources(), R.drawable.empty_profile_round, null))
                    .into(new RoundedBitmap(profilePictureView, this));
        } else {
            profilePictureView.setImageResource(R.drawable.empty_profile_round);
        }
    }

    private class RemoveUserListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final User user = SessionModel.get().getUser(ConnectionActivity.this);
            final DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_connections))
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
                            onBackPressed();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO Handle error
                        }
                    });
        }
    }

}

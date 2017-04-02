package com.silpe.vire.slip.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.collection.CollectionActivity;
import com.silpe.vire.slip.components.BasicValueEventListener;
import com.silpe.vire.slip.components.IntValueChanger;
import com.silpe.vire.slip.components.LocationDisplayComponent;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.components.RoundedBitmap;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;

import java.util.concurrent.atomic.AtomicInteger;

public class ActivityConnection extends AppCompatActivity implements LocationDisplayComponent {

    private TextView mCityView;
    private TextView mLocationView;
    private User mUser;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_connection);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle("");
            actionBar.setSubtitle("");
        }
        mUser = getIntent().getParcelableExtra(AccountActivity.RESULT_USER);
        Button removeButton = (Button) findViewById(R.id.connection_removeButton);
        removeButton.setOnClickListener(new RemoveUserListener());
        Button collectionButton = (Button) findViewById(R.id.connection_collectionButton);
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityConnection.this, CollectionActivity.class);
                intent.putExtra(AccountActivity.RESULT_USER, mUser);
                ActivityConnection.this.startActivity(intent);
            }
        });

        // Populate the text views with the user's information
        TextView fullNameView = (TextView) findViewById(R.id.connection_fullName);
        TextView descriptionView = (TextView) findViewById(R.id.connection_description);
        //TextView connectionsView = (TextView) findViewById(R.id.connection_connections);
        TextView phoneNumberView = (TextView) findViewById(R.id.connection_phoneNumber);
        TextView emailView = (TextView) findViewById(R.id.connection_email);
        TextView connectionView = (TextView) findViewById(R.id.show_number_connections);
        mCityView = (TextView) findViewById(R.id.show_location_city);
        mLocationView = (TextView) findViewById(R.id.show_location);
        connectionView.setText(String.valueOf(mUser.getConnections()));
        fullNameView.setText(mUser.getFullName());
        descriptionView.setText(mUser.getDescription());
        MyCardFragment.populateLocation(mUser, this);
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

    @Override
    public TextView getCityView() {
        return mCityView;
    }

    @Override
    public TextView getLocationView() {
        return mLocationView;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private class RemoveUserListener implements View.OnClickListener {
        private final AtomicInteger counter;

        private RemoveUserListener() {
            counter = new AtomicInteger(2);
        }

        private void removeUser(String mUid, String tUid) {
            /*DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_connections))
                    .child(mUid);
            reference.orderByValue()
                    .equalTo(tUid)
                    .addListenerForSingleValueEvent(new RemovalListener(reference, counter));*/
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_users))
                    .child(mUid)
                    .child(getString(R.string.database_connections))
                    .runTransaction(new IntValueChanger(false));
        }

        @Override
        public void onClick(View v) {
            String tUid = SessionModel.get().getUser(ActivityConnection.this).getUid();
            String mUid = mUser.getUid();
            final DatabaseReference tRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_connections))
                    .child(tUid);
            tRef.orderByValue()
                    .equalTo(mUid)
                    .addListenerForSingleValueEvent(new BasicValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                tRef.child(child.getKey()).setValue(null);
                            }
                        }
                    });
            final DatabaseReference mRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_connections))
                    .child(mUid);
            mRef.orderByValue()
                    .equalTo(tUid)
                    .addListenerForSingleValueEvent(new BasicValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                mRef.child(child.getKey()).setValue(null);
                            }
                        }
                    });
            removeUser(mUid, tUid);
            removeUser(tUid, mUid);
            onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class RemovalListener extends BasicValueEventListener {

        private final DatabaseReference baseReference;
        private final AtomicInteger counter;

        RemovalListener(DatabaseReference baseReference, AtomicInteger counter) {
            this.baseReference = baseReference;
            this.counter = counter;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                baseReference.child(child.getKey()).setValue(null);
            }
            if (counter.decrementAndGet() == 0) onBackPressed();
        }

    }

}
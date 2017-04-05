package com.silpe.vire.slip.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.BasicValueEventListener;
import com.silpe.vire.slip.components.LocationDisplayComponent;
import com.silpe.vire.slip.components.ProfileDisplayComponent;
import com.silpe.vire.slip.components.ProfilePicturePicker;
import com.silpe.vire.slip.components.ProfilePictureView;
import com.silpe.vire.slip.components.RoundedBitmap;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.image.TimestampSignature;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.models.SessionModelListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyCardFragment extends Fragment
        implements SessionModelListener<User>, ProfileDisplayComponent, LocationDisplayComponent {

    // References to the user information views
    private TextView mDescriptionView;
    private TextView mEmailView;
    private TextView mPhoneNumberView;
    private TextView mFullNameView;
    private TextView mConnectionsView;
    private TextView mCityView;
    private TextView mLocationView;

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
        View view = inflater.inflate(R.layout.fragment_mycard, container, false);
        mFullNameView = ((TextView) view.findViewById(R.id.connection_fullName));
        mDescriptionView = ((TextView) view.findViewById(R.id.connection_description));
        mEmailView = ((TextView) view.findViewById(R.id.connection_email));
        mPhoneNumberView = ((TextView) view.findViewById(R.id.connection_phoneNumber));
        mConnectionsView = ((TextView) view.findViewById(R.id.show_number_connections));
        mCityView = ((TextView) view.findViewById(R.id.show_location_city));
        mLocationView = ((TextView) view.findViewById(R.id.show_location));
        mProfilePictureView = ((ProfilePictureView) view.findViewById(R.id.connection_profilePicture));
        final User user = SessionModel.get().getUser(getContext());
        user.getDatabaseReference(getContext())
                .child(getContext().getString(R.string.database_connections))
                .addValueEventListener(new BasicValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer connections = dataSnapshot.getValue(Integer.class);
                        if (connections == null || connections < 0)
                            user.getDatabaseReference(getContext())
                                    .child(getContext().getString(R.string.database_connections))
                                    .setValue(0);
                        else mConnectionsView.setText(String.valueOf(connections));
                    }
                });
        updateUser(user);
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
    private void updateUser(User user) {
        if (user == null) return;
        // TODO Improve the display of user information
        mFullNameView.setText(user.getFullName());
        mDescriptionView.setText(user.getDescription());
        mEmailView.setText(user.getEmail());
        mConnectionsView.setText(String.valueOf(user.getConnections()));
        // TODO Add clickable prompts to add missing info
        String phoneNumber = user.getPhoneNumber();
        mPhoneNumberView.setText(phoneNumber.isEmpty() ? "+ Add a phone number" : phoneNumber);
        mConnectionsView.setText(String.valueOf(user.getConnections()));
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
        if (user.getLatitude() != null && user.getLongitude() != null) {
            populateLocation(user, this);
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
        updateUser(user);
    }

    protected static void populateLocation(final @NonNull User user,
                                           final @NonNull LocationDisplayComponent locationComponent) {
        getAddresses(locationComponent.getContext(), user, new GeoDecodeListener() {
            @Override
            protected void onFinished(List<Address> results) {
                if (results.isEmpty()) return;
                Address address = results.get(0);
                String city = address.getSubLocality();
                String location = "";
                if (city == null) city = address.getLocality();
                if (city == null) city = address.getSubAdminArea();
                if (city == null) {
                    city = address.getAdminArea();
                    if (city != null) city += ", " + address.getCountryName();
                    else city = address.getCountryName();
                    if (city == null) return;
                } else {
                    location = address.getAdminArea();
                    if (location != null) location += ", " + address.getCountryName();
                    else location = address.getCountryName();
                    if (location == null) return;
                }
                locationComponent.getCityView().setText(city);
                locationComponent.getLocationView().setText(location);
            }
        });
    }

    protected static void getAddresses(@NonNull final Context context,
                                       @NonNull final User user,
                                       @NonNull final GeoDecodeListener listener) {
        if (user.getLatitude() == null || user.getLongitude() == null) return;
        new AsyncTask<Void, Integer, List<Address>>() {
            @Override
            @Nullable
            protected List<Address> doInBackground(Void... params) {
                Geocoder coder = new Geocoder(context, Locale.getDefault());
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(user.getLatitude(), user.getLongitude(), 1);
                } catch (IOException ignored) {
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Address> results) {
                if (results != null) listener.onFinished(results);
            }
        }.execute();
    }

    @Override
    public TextView getCityView() {
        return mCityView;
    }

    @Override
    public TextView getLocationView() {
        return mLocationView;
    }

    protected static abstract class GeoDecodeListener {
        protected abstract void onFinished(List<Address> results);
    }

}
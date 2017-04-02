package com.silpe.vire.slip.fragments;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.components.LocationDisplayComponent;
import com.silpe.vire.slip.dtos.GeoMessage;
import com.silpe.vire.slip.dtos.User;

import java.util.Locale;

public class LeaveNoteActivity extends AppCompatActivity implements LocationDisplayComponent {

    private TextView cityView;
    private TextView locationView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_leave_note);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        cityView = (TextView) findViewById(R.id.show_location_city);
        locationView = (TextView) findViewById(R.id.show_location);
        Button noteBtn = (Button) findViewById(R.id.message_writeButton);
        final User user = getIntent().getParcelableExtra(AccountActivity.RESULT_USER);
        if (user != null) {
            final Double latitude = user.getLatitude();
            final Double longitude = user.getLongitude();
            if (latitude == null || longitude == null) return;
            final TextInputEditText messageInput = (TextInputEditText) findViewById(R.id.message_message);
            MyCardFragment.populateLocation(user, this);
            TextView latlong = (TextView) findViewById(R.id.messenger_latitude_longitude);
            latlong.setText(String.format(Locale.getDefault(), "%.2f, %.2f", latitude, longitude));
            noteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = messageInput.getText().toString();
                    DatabaseReference ref = newMessage(LeaveNoteActivity.this, user.getUid());
                    GeoMessage msg = new GeoMessage(user.getUid(), ref.getKey(),
                            message, latitude, longitude, System.currentTimeMillis());
                    ref.setValue(msg);
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public TextView getCityView() {
        return cityView;
    }

    @Override
    public TextView getLocationView() {
        return locationView;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private static DatabaseReference newMessage(Context context, String uid) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(context.getString(R.string.database_messages))
                .child(uid)
                .push();
    }

}

package com.silpe.vire.slip;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.components.IntValueChanger;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.AccountActivity;
import com.silpe.vire.slip.fragments.QRFragment;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;
import com.silpe.vire.slip.scanner.BarcodeCaptureActivity;

import java.util.concurrent.atomic.AtomicInteger;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks {

    private static final int REQUEST_ACCESS_COARSE = 3384;
    private static final int SCAN_FRAGMENT = 5508;
    private static final int ACCOUNT_FRAGMENT = 4705;
    private static final String QR_FRAGMENT = "fragment_qr";

    private GoogleApiClient googleApiClient;
    private NavigationPagerAdapter mNavigationPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleApiClient == null) googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        // Check whether the user has properly logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuth.getInstance().signOut();
            SessionModel.get().setUser(null, this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (SessionModel.get().getUser(this) == null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_users))
                    .child(user.getUid())
                    .addListenerForSingleValueEvent(new UserStateListener());
        } else {
            construct();
        }
    }

    /**
     * Separate method called to build the {@code MainActivity} layout.
     */
    private void construct() {
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the QR Code floating action button
        final FloatingActionButton scanFab = (FloatingActionButton) findViewById(R.id.scanFab);
        scanFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                startActivityForResult(intent, SCAN_FRAGMENT);
            }
        });
        final FloatingActionButton qrFab = (FloatingActionButton) findViewById(R.id.qrFab);
        qrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentByTag(QR_FRAGMENT) == null) {
                    User user = SessionModel.get().getUser(MainActivity.this);
                    final FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.toplevel, QRFragment.newInstance(user.getUid()), QR_FRAGMENT)
                            .addToBackStack(QR_FRAGMENT);
                    qrFab.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            transaction.commit();
                        }
                    }, 70);
                }
            }
        });

        // Set up the pagination and tab navigation
        mNavigationPager = new NavigationPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.toplevelPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.toplevelTabs);
        viewPager.setAdapter(mNavigationPager);
        tabLayout.setupWithViewPager(viewPager);

        // User is logged in, but dispatch an update call anyway
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.database_users))
                .child(SessionModel.get().getUser(this).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        SessionModel.get().setUser(user, MainActivity.this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_FRAGMENT) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //Toast.makeText(this, R.string.barcode_success, Toast.LENGTH_SHORT).show();
                    String uid = barcode.displayValue;
                    doAddUser(uid);
                } else {
                    Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ACCOUNT_FRAGMENT) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                User user = data.getParcelableExtra(AccountActivity.RESULT_USER);
                mNavigationPager.notifyUserUpdated(user);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_logout) {
            SessionModel.get().setUser(null, this);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_account) {
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_up, R.anim.slide_out_up)
                    .replace(R.id.toplevel, new AccountFragment(), ACCOUNT_FRAGMENT)
                    .addToBackStack(ACCOUNT_FRAGMENT)
                    .commit();*/
            Intent intent = new Intent(this, AccountActivity.class);
            startActivityForResult(intent, ACCOUNT_FRAGMENT);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * If there is a fragment stack, such as {@code QRFragment}
     * or {@code AccountFragment}, then pressing "Back" will
     * return to the previous fragment. Otherwise, pressing
     * "Back" will do nothing in the main screen.
     */
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
    }

    private boolean hasLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            Snackbar.make(toolbar, R.string.permission_location_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, REQUEST_ACCESS_COARSE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, REQUEST_ACCESS_COARSE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE
                && grantResults.length == 2
                && grantResults[0] == PERMISSION_GRANTED
                && grantResults[1] == PERMISSION_GRANTED) {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (!hasLocationPermissions())
            return;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
            return;
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnownLocation != null) {
            User user = SessionModel.get().getUser(this);
            user.setLatitude(lastKnownLocation.getLatitude());
            user.setLongitude(lastKnownLocation.getLongitude());
            SessionModel.get().setUser(user, this);
        }
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(3_000L)
                .setFastestInterval(1_000L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Activity $this = MainActivity.this;
                User user = SessionModel.get().getUser($this);
                user.setLatitude(location.getLatitude());
                user.setLongitude(location.getLongitude());
                SessionModel.get().setUser(user, $this);
            }
        });
        LocationRequest longLocationRequest = new LocationRequest()
                .setInterval(10_000L)
                .setFastestInterval(1_000L)
                //.setSmallestDisplacement(15_000f)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, longLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Activity $this = MainActivity.this;
                DatabaseReference ref = SessionModel.get()
                        .getUser($this)
                        .getDatabaseReference($this);
                ref.child($this.getString(R.string.database_user_latitude))
                        .setValue(location.getLatitude());
                ref.child($this.getString(R.string.database_user_longitude))
                        .setValue(location.getLongitude());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private class UserStateListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            if (user == null) {
                FirebaseAuth.getInstance().signOut();
                SessionModel.get().setUser(null, MainActivity.this);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                SessionModel.get().setUser(user, MainActivity.this);
                construct();
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            FirebaseAuth.getInstance().signOut();
            SessionModel.get().setUser(null, MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * @param tUid the UID of the user to add
     */
    private void doAddUser(final String tUid) {
        final String mUid = SessionModel.get().getUser(this).getUid();
        if (mUid.equals(tUid)) {
            Toast.makeText(this, R.string.error_add_self, Toast.LENGTH_SHORT).show();
        } else {
            final AtomicInteger mCounter = new AtomicInteger(2);
            final AtomicInteger tCounter = new AtomicInteger(2);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.database_users))
                    .child(tUid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (mCounter.decrementAndGet() == 0) addUid(tUid, mUid, reference);
                                if (tCounter.decrementAndGet() == 0) addUid(mUid, tUid, reference);
                            } else {
                                Toast.makeText(MainActivity.this, R.string.error_invalid_uid, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            reference.child(getString(R.string.database_connections))
                    .child(mUid)
                    .orderByValue()
                    .equalTo(tUid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Toast.makeText(MainActivity.this, R.string.error_user_added, Toast.LENGTH_SHORT).show();
                            } else if (mCounter.decrementAndGet() == 0) {
                                addUid(tUid, mUid, reference);
                            }
                        }
                    });
            reference.child(getString(R.string.database_connections))
                    .child(tUid)
                    .orderByValue()
                    .equalTo(mUid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0 && tCounter.decrementAndGet() == 0) {
                                addUid(mUid, tUid, reference);
                            }
                        }
                    });
        }
    }

    private void addUid(String uid, String mUid, DatabaseReference reference) {
        reference.child(getString(R.string.database_connections))
                .child(mUid)
                .push()
                .setValue(uid);
        reference.child(getString(R.string.database_users))
                .child(mUid)
                .child(getString(R.string.database_connections))
                .runTransaction(new IntValueChanger(true));
    }

    private abstract class UserQueryListener implements ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

}
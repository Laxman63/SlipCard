package com.silpe.vire.slip;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.AccountFragment;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;
import com.silpe.vire.slip.scanner.BarcodeCaptureActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final int QR_FRAGMENT = 5508;
    private static final String ACCOUNT_FRAGMENT = "fragment_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether the user has properly logged in
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        if (authUser == null) {
            SessionModel.get().setUser(null, this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (SessionModel.get().getUser(this) == null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.database_users))
                    .child(authUser.getUid())
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the QR Code floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.scanFab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, QR_FRAGMENT);
                /*if (getSupportFragmentManager().findFragmentByTag(QR_FRAGMENT) == null) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.toplevel, QRFragment.newInstance(user.getUid()), SEARCH_FRAGMENT)
                            .addToBackStack(SEARCH_FRAGMENT)
                            .commit();
                }*/
            }
        });

        // Set up the pagination and tab navigation
        NavigationPagerAdapter navigationPagerAdapter = new NavigationPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.toplevelPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.toplevelTabs);
        viewPager.setAdapter(navigationPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_FRAGMENT) {
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.toplevel, new AccountFragment(), ACCOUNT_FRAGMENT)
                    .addToBackStack(ACCOUNT_FRAGMENT)
                    .commit();
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
        }
    }

    private void doAddUser(final String uid) {
        final String mUid = SessionModel.get().getUser(this).getUid();
        if (mUid.equals(uid)) {
            Toast.makeText(this, R.string.error_add_self, Toast.LENGTH_SHORT).show();
        } else {
            final AtomicInteger counter = new AtomicInteger(2);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.database_users))
                    .child(uid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (counter.decrementAndGet() == 0) addUid(uid, mUid, reference);
                            } else {
                                Toast.makeText(MainActivity.this, R.string.error_invalid_uid, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            reference.child(getString(R.string.database_connections))
                    .child(mUid)
                    .orderByValue()
                    .equalTo(uid)
                    .addListenerForSingleValueEvent(new UserQueryListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Toast.makeText(MainActivity.this, R.string.error_user_added, Toast.LENGTH_SHORT).show();
                            } else if (counter.decrementAndGet() == 0) {
                                addUid(uid, mUid, reference);
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
        Toast.makeText(this, R.string.addUser_success, Toast.LENGTH_SHORT).show();
    }

    private abstract class UserQueryListener implements ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

}
package com.silpe.vire.slip;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.QRFragment;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String QR_FRAGMENT = "fragment_qr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            onBackPressed();
        } else {
            // TODO Move this handling into a separate listener class
            if (SessionModel.get().getUser(this) == null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref = ref.child(getString(R.string.database_users)).child(fbUser.getUid());
                // TODO Add a timeout feature
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        SessionModel.get().setUser(user, MainActivity.this);
                        construct();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            } else {
                construct();
            }
        }
    }

    private void construct() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the QR Code floating action button
        final User user = SessionModel.get().getUser(this);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().findFragmentByTag(QR_FRAGMENT) == null) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.toplevel, QRFragment.newInstance(user.getUid()), QR_FRAGMENT)
                            .addToBackStack(QR_FRAGMENT)
                            .commit();
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_logout) {
            SessionModel.get().setUser(null, this);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (id == R.id.action_account) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO
     * -- Our back button does some strange stuff
     * -- It may back into previous user sessions when logging out
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}

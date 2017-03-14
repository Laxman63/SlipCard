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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.AccountFragment;
import com.silpe.vire.slip.fragments.QRFragment;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String QR_FRAGMENT = "fragment_qr";
    private static final String SEARCH_FRAGMENT = "fragment_search";
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
        final User user = SessionModel.get().getUser(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.scanFab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().findFragmentByTag(QR_FRAGMENT) == null) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.toplevel, QRFragment.newInstance(user.getUid()), SEARCH_FRAGMENT)
                            .addToBackStack(SEARCH_FRAGMENT)
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

}
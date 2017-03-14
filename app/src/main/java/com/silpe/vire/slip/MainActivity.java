package com.silpe.vire.slip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.AccountFragment;
import com.silpe.vire.slip.fragments.QRFragment;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;

public class MainActivity extends AppCompatActivity {


    static final String QR_FRAGMENT = "fragment_qr";
    private static final String SEARCH_FRAGMENT = "fragment_search";
    private static final String ACCOUNT_FRAGMENT = "fragment_account";

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    NavigationPagerAdapter navigationPagerAdapter;
    SearchView  searchView;
    LinearLayout body, searchlist;
    Context contxt;
    User user;
    FloatingActionButton fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contxt = getApplicationContext();

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            SessionModel.get().setUser(null, this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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

        user = SessionModel.get().getUser(this);
        fab = (FloatingActionButton) findViewById(R.id.scanFab);
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
        // Retrieve the SearchView and plug it into SearchManager
        final MenuItem searchItem = menu.findItem(R.id.action_search);




        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(contxt, SearchList.class);
                startActivity(intent);
                return false;
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
            return true;
        } else if (id == R.id.action_account) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.toplevel, new AccountFragment(), ACCOUNT_FRAGMENT)
                    .addToBackStack(ACCOUNT_FRAGMENT)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
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

}

package com.silpe.vire.slip;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.silpe.vire.slip.fragments.SearchlistFragment;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        body = (LinearLayout)findViewById(R.id.body);
        searchlist = (LinearLayout)findViewById(R.id.searchList);

        // Set up the QR Code floating action button
        user = SessionModel.get().getUser(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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

        navigationPagerAdapter = new NavigationPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager = (ViewPager) findViewById(R.id.toplevelPager);
        tabLayout = (TabLayout) findViewById(R.id.toplevelTabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("YOLO", ((String)" "+tab.getPosition()) );
                switch(tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle(getString(R.string.fragment0title));
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle(getString(R.string.fragment1title));
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle(getString(R.string.fragment2title));
                        break;

                    default:
                        viewPager.setCurrentItem(tab.getPosition());
                        toolbar.setTitle("Fragment Star");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAdapter(navigationPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Todo  1. implement a list for the search view lol.
    //Todo  2. Replace current icon with material
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Retrieve the SearchView and plug it into SearchManager

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.toplevel,  new SearchlistFragment(), SEARCH_FRAGMENT)
                            .addToBackStack(SEARCH_FRAGMENT)
                            .commit();
                    return true;
                }
            });
        }





        return true;
    }

    private void buildSearch(SearchView searchView){


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
<<<<<<< HEAD
     * TODO
     * -- Our back button does some strange stuff
     * -- It may back into previous user sessions when logging out
     *  ^- lol, true
=======
     * If there is a fragment stack, such as {@code QRFragment}
     * or {@code AccountFragment}, then pressing "Back" will
     * return to the previous fragment. Otherwise, pressing
     * "Back" will do nothing in the main screen.
>>>>>>> c23ca94a2d375c440f916c9f777d1963eaf9c587
     */
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
    }

}

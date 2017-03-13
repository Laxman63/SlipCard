package com.silpe.vire.slip.fragments;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.silpe.vire.slip.LoginActivity;
import com.silpe.vire.slip.MainActivity;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.models.SessionModel;
import com.silpe.vire.slip.navigation.NavigationPagerAdapter;

import static android.content.Context.SEARCH_SERVICE;

public class MainFragment extends Fragment {
/*
    private MainActivity mainActivity;

    public static MainFragment newInstance(MainActivity mainActivity) {
        MainFragment fragment = new MainFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        // Set up the QR Code floating action button
        final User user = SessionModel.get().getUser(getContext());
        final FloatingActionButton scanFab = (FloatingActionButton) view.findViewById(R.id.scanFab);
        scanFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clickedFab(user);
            }
        });

        // Set up the pagination and tab navigation
        NavigationPagerAdapter navigationPagerAdapter = new NavigationPagerAdapter(getActivity().getSupportFragmentManager(), getContext());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.toplevelPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.toplevelTabs);

        viewPager.setAdapter(navigationPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {

                @Override
                public boolean onClose() {
                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });
            EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getContext().getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_logout) {
            SessionModel.get().setUser(null, getContext());
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account) {
            mainActivity.clickedAccount();
        }
        return super.onOptionsItemSelected(item);
    }

*/
}

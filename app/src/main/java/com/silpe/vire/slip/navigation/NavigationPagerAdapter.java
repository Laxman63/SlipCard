package com.silpe.vire.slip.navigation;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silpe.vire.slip.fragments.CollectionFragment;
import com.silpe.vire.slip.fragments.ShowFragment;
import com.silpe.vire.slip.fragments.NetworkFragment;

public class NavigationPagerAdapter extends FragmentPagerAdapter {

    public NavigationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ShowFragment();
            case 1:
                return new CollectionFragment();
            case 2:
                return new NetworkFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "My Cards";
            case 1:
                return "Collection";
            case 2:
                return "Network";
        }
        return null;
    }

}

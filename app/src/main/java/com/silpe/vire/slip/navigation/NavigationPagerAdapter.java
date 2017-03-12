package com.silpe.vire.slip.navigation;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;

import com.silpe.vire.slip.MainActivity;
import com.silpe.vire.slip.R;
import com.silpe.vire.slip.collection.CollectionFragment;
import com.silpe.vire.slip.fragments.ShowFragment;
import com.silpe.vire.slip.fragments.NetworkFragment;

public class NavigationPagerAdapter extends FragmentPagerAdapter {
    Fragment show, collect, network;
    Context context;

    public NavigationPagerAdapter(FragmentManager fm, Context contxt) {
        super(fm);
        context = contxt;
        show = new ShowFragment();
        collect =  new CollectionFragment();
        network = new NetworkFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return show;
            case 1:
                return collect;
            case 2:
                return network;
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
                return context.getString(R.string.fragment0title);
            case 1:
                return context.getString(R.string.fragment1title);
            case 2:
                return context.getString(R.string.fragment2title);
        }
        return null;
    }

}

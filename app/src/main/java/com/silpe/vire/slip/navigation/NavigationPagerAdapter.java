package com.silpe.vire.slip.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silpe.vire.slip.R;
import com.silpe.vire.slip.collection.CollectionFragment;
import com.silpe.vire.slip.fragments.ShowFragment;
import com.silpe.vire.slip.fragments.NetworkFragment;

public class NavigationPagerAdapter extends FragmentPagerAdapter {

    private Fragment mMyCardFragment;
    private Fragment mCollectionFragment;
    private Fragment mNetworkFragment;
    private Context mContext;

    public NavigationPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
        mMyCardFragment = new ShowFragment();
        mCollectionFragment = new CollectionFragment();
        mNetworkFragment = new NetworkFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mMyCardFragment;
            case 1:
                return mCollectionFragment;
            case 2:
                return mNetworkFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.myCardTitle);
            case 1:
                return mContext.getString(R.string.collectionTitle);
            case 2:
                return mContext.getString(R.string.networkTitle);
        }
        return null;
    }

}

package com.silpe.vire.slip.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silpe.vire.slip.R;
import com.silpe.vire.slip.collection.CollectionFragment;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.fragments.MemeFragment;
import com.silpe.vire.slip.fragments.MyCardFragment;
import com.silpe.vire.slip.models.SessionModel;

public class NavigationPagerAdapter extends FragmentPagerAdapter {

    private MyCardFragment mMyCardFragment;
    private CollectionFragment mCollectionFragment;
    private MemeFragment memeFragment;
    private Context mContext;

    public NavigationPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        User user = SessionModel.get().getUser(context);
        mContext = context;
        mMyCardFragment = new MyCardFragment();
        mCollectionFragment = CollectionFragment.newInstance(user);
        memeFragment = MemeFragment.newInstance(user);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mMyCardFragment;
            case 1:
                return mCollectionFragment;
            case 2:
                return memeFragment;
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
                return mContext.getString(R.string.messageTitle);
        }
        return null;
    }

    public void notifyUserUpdated(User user) {
        mMyCardFragment.valueUpdated(user);
    }

}

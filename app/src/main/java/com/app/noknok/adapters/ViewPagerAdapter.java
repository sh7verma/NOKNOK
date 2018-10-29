package com.app.noknok.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.noknok.fragments.FriendsConfessionsFragment;
import com.app.noknok.fragments.HomeFragment;
import com.app.noknok.fragments.MyConfessionsFragment;
import com.app.noknok.fragments.OthersConfessionsFragment;

/**
 * Created by dev on 2/8/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MyConfessionsFragment();
            case 1:
                return new FriendsConfessionsFragment();
            case 2:
                return new OthersConfessionsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}
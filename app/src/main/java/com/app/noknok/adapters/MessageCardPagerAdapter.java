package com.app.noknok.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.noknok.fragments.NewMessagesFragment;
import com.app.noknok.fragments.OldMessagesFragment;

/**
 * Created by dev on 17/8/17.
 */

public class MessageCardPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;

    public MessageCardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new NewMessagesFragment();
            case 1:
                return new OldMessagesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}
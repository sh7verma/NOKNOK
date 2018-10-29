package com.app.noknok.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

/**
 * Created by dev on 20/6/17.
 */

public class BaseFragment extends Fragment {

    protected int mScreenwidth, mScreenheight;
    protected SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDefaults();
    }

    void getDefaults() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenheight = dm.heightPixels;
        mScreenwidth = dm.widthPixels;

        sp = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
    }
}

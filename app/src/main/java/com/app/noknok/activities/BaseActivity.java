package com.app.noknok.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.app.noknok.services.FirebaseListeners;

/**
 * Created by dev on 19/6/17.
 */

public class BaseActivity extends AppCompatActivity {

    protected int mScreenwidth, mScreenheight;
    protected SharedPreferences sp;
    //  protected Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        getDefaults();
        FirebaseListeners.initListener(getApplicationContext());
    }

    void getDefaults() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenwidth = dm.widthPixels;
        mScreenheight = dm.heightPixels;
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //   db = new Database(BaseActivity.this);
    }
}


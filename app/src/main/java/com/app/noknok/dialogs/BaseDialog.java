package com.app.noknok.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dev on 10/7/17.
 */

public class BaseDialog extends AppCompatActivity {


    protected int mScreenwidth, mScreenheight;
    protected SharedPreferences sp;
    Context mContext;

    public BaseDialog(Context context) {
        mContext = context;
    }
    //  protected Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDefaults();
    }

    void getDefaults() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mScreenwidth = dm.widthPixels;
        mScreenheight = dm.heightPixels;
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //   db = new Database(BaseActivity.this);
    }
}


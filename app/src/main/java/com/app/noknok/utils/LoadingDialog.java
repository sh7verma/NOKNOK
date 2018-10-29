package com.app.noknok.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.noknok.R;

/**
 * Created by dev on 20/6/17.
 */


public class LoadingDialog {

    static LoadingDialog mInstance;
    Dialog mProgress;
    TextView txtLoading;

    public static void initLoader() {
        if (mInstance == null)
            mInstance = new LoadingDialog();
    }

    public static LoadingDialog getLoader() {
        if (mInstance != null)
            return mInstance;
        else {
            mInstance = new LoadingDialog();
            return mInstance;
        }
    }

    public void showLoader(Context con) {
        if (mProgress != null) {
            mProgress.cancel();
        }
        mProgress = new Dialog(con);
        mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams wmlp = mProgress.getWindow().getAttributes();

        wmlp.gravity = Gravity.CENTER;
        mProgress.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgress.setCancelable(false);
        mProgress.setContentView(R.layout.dialog_loader);

        txtLoading = (TextView) mProgress.findViewById(R.id.txt_processing);
        txtLoading.setPadding(30, 0, 0, 0);
//        txtLoading.setVisibility(View.GONE);
//        (new Handler()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
        txtLoading.setVisibility(View.VISIBLE);
//            }
//        }, 100);

        mProgress.show();
    }

    public void setText(String text) {
        try {
            txtLoading.setText(text);
        } catch (Exception e) {
        }
    }

    public void dismissLoader() {
        try {
            if (mProgress != null) {
                mProgress.cancel();
                mProgress = null;
            }
        } catch (Exception e) {
        }
    }
}

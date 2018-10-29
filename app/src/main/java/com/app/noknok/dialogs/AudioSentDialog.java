package com.app.noknok.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.noknok.R;

/**
 * Created by dev on 3/7/17.
 */

public class AudioSentDialog {

    Context mContext;
    Dialog mDialog, mParentDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle;
    ImageView ivIcon;

    public AudioSentDialog(Context context, Dialog parentDialog) {
        mContext = context;
        mParentDialog = parentDialog;
    }

    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_audio_sent);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        mDialog.show();
        init();

    }


    private void init() {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_dialog_audio_sent_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_dialog_audio_sent_circle_container);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_dialog_audio_sent_circle);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_dialog_audio_sent_icon);

        int circleRadius = width - (width / 4);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleRadius, circleRadius);
        llCircle.setLayoutParams(circleParams);


        new Handler().postDelayed(new Runnable() {
           @Override
            public void run() {
                if (mContext.getClass().getSimpleName().equals("DirectMessageActivity"))
                    ((Activity) mContext).finish();
                else {
                    mDialog.dismiss();
                    mParentDialog.dismiss();
                }
            }
        }, 1000);

    }
}
package com.app.noknok.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;

/**
 * Created by dev on 29/8/17.
 */

public class GuessNoticeDialog implements View.OnClickListener {

    Context mContext;
    Dialog mDialog;

    RelativeLayout rlCircleMainContainer, rlMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvConfessionDone,  tvGuessNoticeText;
    ImageView ivIcon;
    int i;
    String timeLeft;

    public GuessNoticeDialog(Context mContext,int i,String timeLeft) {

        this.mContext = mContext;
        this.i = i;
        this.timeLeft = timeLeft;

    }


    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_guess_notice);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        mDialog.show();


        init();
        initListeners();
    }


    private void init() {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_guess_notice_circle_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_notice_circle);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_guess_notice_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_notice_done_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_notice_circle);
        tvConfessionDone = (TextView) mDialog.findViewById(R.id.tv_guess_notice_done);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_guess_notice_icon);
        rlMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_guess_notice_main_container);
        tvGuessNoticeText = (TextView) mDialog.findViewById(R.id.tv_guess_notice_text);

        int circleRadius = width - (width / 4);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleRadius, circleRadius);
        llCircle.setLayoutParams(circleParams);
        LinearLayout.LayoutParams headerTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int headerTextMargin = (circleRadius / 2) / 5;
        llHeaderTextContainer.setGravity(Gravity.BOTTOM);
        llHeaderTextContainer.setPadding(0, 0, 0, headerTextMargin);
        llOptionsContainer.setPadding(0, 0, 0, headerTextMargin);
        headerTextParams.setMargins(0, headerTextMargin / 2, 0, 0);
        headerTextParams.gravity = Gravity.CENTER;

        tvConfessionDone.setLayoutParams(headerTextParams);
        if (i == 0) {
            tvGuessNoticeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.70));
            tvGuessNoticeText.setText(mContext.getResources().getString(R.string.you_can_select_maximum_3_contacts_at_a_time_next_chance_will_be_given_after_6_hours));
        } else if (i == 1) {
            tvGuessNoticeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.80));
            tvGuessNoticeText.setText(mContext.getResources().getString(R.string.new_guess_will_be_available_after) + " "+timeLeft);
        }

        tvConfessionDone.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));
        tvConfessionDone.setText(R.string.done);
    }

    private void initListeners() {
        tvConfessionDone.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_guess_notice_done:
                mDialog.dismiss();
                break;


        }
    }

}

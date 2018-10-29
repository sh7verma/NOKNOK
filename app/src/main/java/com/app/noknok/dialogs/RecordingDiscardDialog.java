package com.app.noknok.dialogs;

import android.app.Activity;
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
 * Created by dev on 3/7/17.
 */

public class RecordingDiscardDialog implements View.OnClickListener {


    Context mContext;
    Dialog mDialog;
    Dialog mParentDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvConfessionAsDiscard, tvCancel;
    ImageView ivIcon;


    public RecordingDiscardDialog(Context context, Dialog parentDialog) {
        mContext = context;
        mParentDialog = parentDialog;
    }

    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_message_discard);
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

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_confession_discard_type_circle_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_discard_type_circle_container);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_confession_discard_type_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_options_discard_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_discard_type_text_container);
        tvConfessionAsDiscard = (TextView) mDialog.findViewById(R.id.tv_confession_discard_as_discard);

        tvCancel = (TextView) mDialog.findViewById(R.id.tv_confession_discard_type_cancel);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_confession_discard_type_icon);


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
        tvConfessionAsDiscard.setLayoutParams(headerTextParams);

        tvCancel.setLayoutParams(headerTextParams);
        tvConfessionAsDiscard.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));

        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));
    }

    private void initListeners() {
        tvConfessionAsDiscard.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confession_discard_as_discard:

                if (mContext.getClass().getSimpleName().equals("DirectMessageActivity"))
                    ((Activity) mContext).finish();
                else {
                    mDialog.dismiss();
                    mParentDialog.dismiss();
                }

                break;

            case R.id.tv_confession_discard_type_cancel:
                mDialog.dismiss();
                break;

        }
    }
}
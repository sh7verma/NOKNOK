package com.app.noknok.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.app.noknok.activities.BaseActivity;
import com.app.noknok.definitions.Config;

import io.realm.Realm;

/**
 * Created by dev on 18/8/17.
 */

public class GuessResponseDialog extends BaseActivity implements View.OnClickListener {

    public static Context mContext;
    Dialog mDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvCancel, tvReply, tvTitle, tvDesc;
    ImageView ivIcon;
    Realm mRealm;
    boolean isGuessRight;

    public GuessResponseDialog(Context context, boolean isGuessRight) {
        mContext = context;
        this.isGuessRight = isGuessRight;

    }

    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_guess_response);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        mDialog.show();
        mDialog.setCancelable(false);

        mRealm = Realm.getDefaultInstance();

        init();
        initListeners();
    }


    private void init() {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_guess_response_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_response_circle_container);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_guess_response_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_response_options_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_guess_response_text_container);
        tvTitle = (TextView) mDialog.findViewById(R.id.tv_guess_response_title);
        tvDesc = (TextView) mDialog.findViewById(R.id.tv_guess_response_text);
        tvReply = (TextView) mDialog.findViewById(R.id.tv_guess_response_reply);
        tvCancel = (TextView) mDialog.findViewById(R.id.tv_guess_response_cancel);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_guess_response_icon);

        int circleRadius = width - (width / 4);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleRadius, circleRadius);
        llCircle.setLayoutParams(circleParams);


        LinearLayout.LayoutParams headerTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int headerTextMargin = (circleRadius / 2) / 5;
        llHeaderTextContainer.setGravity(Gravity.BOTTOM);
        llHeaderTextContainer.setPadding(0, 0, 0, headerTextMargin);

        tvTitle.setPadding(headerTextMargin, 0, headerTextMargin, 0);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.8));

        tvDesc.setPadding(headerTextMargin, 0, headerTextMargin, 0);
        tvDesc.setGravity(Gravity.CENTER);
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.7));


        llOptionsContainer.setPadding(0, 0, 0, headerTextMargin);
        headerTextParams.setMargins(0, headerTextMargin / 2, 0, 0);
        headerTextParams.gravity = Gravity.CENTER;
        tvReply.setLayoutParams(headerTextParams);
        tvReply.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));

        tvCancel.setLayoutParams(headerTextParams);
        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));

        if(isGuessRight){
            tvDesc.setText(mContext.getResources().getString(R.string.you_guessed_it_right));
            tvTitle.setText(mContext.getResources().getString(R.string.congrats));
            ivIcon.setImageResource(R.drawable.ill_right_guess);
        }else{
            tvDesc.setText(mContext.getResources().getString(R.string.you_guessed_it_wrong));
            tvTitle.setText(mContext.getResources().getString(R.string.sorry));
            ivIcon.setImageResource(R.drawable.ill_wrong_guess);
        }

    }

    private void initListeners() {
        tvReply.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guess_response_cancel:
                mDialog.dismiss();
                ((Activity)mContext).finish();
                break;

            case R.id.tv_guess_response_reply:

                Intent intent = new Intent();
                intent.putExtra(Config.MESSAGE_REPLY,true);
                ((Activity)mContext).setResult(Activity.RESULT_OK, intent);
                mDialog.dismiss();
                ((Activity)mContext).finish();

                mDialog.dismiss();
                break;


        }
    }
}
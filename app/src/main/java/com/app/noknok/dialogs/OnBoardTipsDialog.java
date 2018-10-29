package com.app.noknok.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.BaseActivity;
import com.app.noknok.definitions.Config;

/**
 * Created by dev on 3/7/17.
 */

public class OnBoardTipsDialog implements View.OnClickListener {

    Context mContext;
    Dialog mDialog;
    int mHeight, mWidth;
    int mNextCount = 0;
    String mFullName;


    LinearLayout llYellowBackgroundHeader, llMainHeaderContainer,
            llRightGuessFriendsContainer, llRightGuessContainer,
            llFriendsContainer, llMicContainer, llMessagesCountContainer,
            llConfessionsCountContainer, llOnboardIntroContainer;

    LinearLayout llOnboardFriendsContainer, llOnboardMessagesContainer;

    RelativeLayout rlHomeMainContainer;

    ImageView ivLogo, ivSettings, ivMessages, ivConfessions, ivMic, ivOnboardIntroImage;

    TextView tvName, tvRightGuessCount, tvRightGuessText, tvFriendsCount,
            tvFriendsText, tvMessagesCount, tvMessagesText,
            tvConfessionsCount, tvConfessionsText, tvOnboardIntroName,
            tvOnboardIntroMessage, tvOnboardIntroLetsgo, tvOnboardNextButton;

    TextView tvOnboardFriends, tvOnboardFriendsMessage, tvOnboardMessages, tvOnboardMessagesMessage;

    public OnBoardTipsDialog(Context context) {
        mContext = context;
    }

    public void showDialog(int mHeight, int mWidth, String fullName) {

        this.mWidth = mWidth;
        this.mHeight = mHeight;
        mFullName = fullName;

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.onboardtips_layout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

        init();
        initListeners();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_onboard_intro_letsgo:
                llOnboardIntroContainer.setVisibility(View.GONE);
                llOnboardFriendsContainer.setVisibility(View.VISIBLE);
                tvOnboardNextButton.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_onboard_next_button:
                nextTip();
                break;


        }

    }


    void nextTip() {

        if (mNextCount == 0) {

            llOnboardMessagesContainer.setVisibility(View.VISIBLE);
            llFriendsContainer.setVisibility(View.INVISIBLE);
            llOnboardFriendsContainer.setVisibility(View.GONE);
            llMessagesCountContainer.setVisibility(View.VISIBLE);
            ivMessages.setVisibility(View.VISIBLE);

        } else if (mNextCount == 1) {

            llMessagesCountContainer.setVisibility(View.INVISIBLE);
            ivMessages.setVisibility(View.INVISIBLE);
            llOnboardMessagesContainer.setVisibility(View.GONE);
            llConfessionsCountContainer.setVisibility(View.VISIBLE);
            ivConfessions.setVisibility(View.VISIBLE);
            llOnboardFriendsContainer.setVisibility(View.VISIBLE);
            tvOnboardFriends.setText(R.string.confessions);
            tvOnboardFriendsMessage.setText(R.string.lorem_ipsum);

            FrameLayout.LayoutParams onBoardfriendsContainerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            onBoardfriendsContainerParams.gravity = Gravity.BOTTOM;

            llOnboardFriendsContainer.setLayoutParams(onBoardfriendsContainerParams);
        } else if (mNextCount == 2) {

            llConfessionsCountContainer.setVisibility(View.INVISIBLE);
            ivConfessions.setVisibility(View.INVISIBLE);

            llOnboardFriendsContainer.setVisibility(View.GONE);


            tvOnboardMessages.setText(R.string.record_message);
            tvOnboardMessagesMessage.setText(R.string.lorem_ipsum);
            FrameLayout.LayoutParams onBoardfriendsContainerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            onBoardfriendsContainerParams.gravity = Gravity.BOTTOM;

            llOnboardMessagesContainer.setVisibility(View.VISIBLE);
            llOnboardMessagesContainer.setLayoutParams(onBoardfriendsContainerParams);
            llMicContainer.setVisibility(View.VISIBLE);

            tvOnboardNextButton.setText(R.string.done);

        } else if (mNextCount == 3) {

            mDialog.dismiss();

        }

        mNextCount++;
    }

    private void initListeners() {

        llFriendsContainer.setOnClickListener(this);
        ivMic.setOnClickListener(this);
        ivSettings.setOnClickListener(this);
        tvOnboardIntroLetsgo.setOnClickListener(this);
        tvOnboardNextButton.setOnClickListener(this);

    }

    private void init() {

        llOnboardIntroContainer = (LinearLayout) mDialog.findViewById(R.id.ll_onboard_intro_container);
        ivOnboardIntroImage = (ImageView) mDialog.findViewById(R.id.iv_onboard_intro_image);
        tvOnboardIntroName = (TextView) mDialog.findViewById(R.id.tv_onboard_intro_name);
        tvOnboardIntroMessage = (TextView) mDialog.findViewById(R.id.tv_onboard_intro_message);
        tvOnboardIntroLetsgo = (TextView) mDialog.findViewById(R.id.tv_onboard_intro_letsgo);
        llOnboardFriendsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_onboard_friends_container);
        llOnboardMessagesContainer = (LinearLayout) mDialog.findViewById(R.id.ll_onboard_messages_container);
        tvOnboardNextButton = (TextView) mDialog.findViewById(R.id.tv_onboard_next_button);
        tvOnboardFriends = (TextView) mDialog.findViewById(R.id.tv_onboard_friends);
        tvOnboardFriendsMessage = (TextView) mDialog.findViewById(R.id.tv_onboard_friends_message);
        tvOnboardMessages = (TextView) mDialog.findViewById(R.id.tv_onboard_messages);
        tvOnboardMessagesMessage = (TextView) mDialog.findViewById(R.id.tv_onboard_messages_message);

        llOnboardIntroContainer.setPadding((int) (mWidth * 0.08), (int) (mWidth * 0.08),
                (int) (mWidth * 0.08), (int) (mWidth * 0.08));

        LinearLayout.LayoutParams onboardIntroImageParams = new LinearLayout.LayoutParams((int)(mWidth * 0.25), (int)(mWidth * 0.25));
        onboardIntroImageParams.gravity = Gravity.LEFT;
        ivOnboardIntroImage.setLayoutParams(onboardIntroImageParams);


        tvOnboardIntroName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(mWidth * 0.04));
        tvOnboardIntroMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(mWidth * 0.03));


        tvOnboardIntroLetsgo.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mWidth * 0.060));

        tvOnboardIntroLetsgo.setPadding((int) (mWidth * 0.08), (int) (mHeight * 0.03),
                (int) (mWidth * 0.08), (int) (mHeight * 0.03));


        rlHomeMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_home_main_container);
        llYellowBackgroundHeader = (LinearLayout) mDialog.findViewById(R.id.ll_yellow_background_header);
        llMainHeaderContainer = (LinearLayout) mDialog.findViewById(R.id.ll_main_header_container);
        llRightGuessFriendsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_rightguess_friends_container);
        llRightGuessContainer = (LinearLayout) mDialog.findViewById(R.id.ll_right_guess_container);
        llFriendsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_friends_container);
        llMicContainer = (LinearLayout) mDialog.findViewById(R.id.ll_mic_container);
        ivLogo = (ImageView) mDialog.findViewById(R.id.iv_logo);
        ivSettings = (ImageView) mDialog.findViewById(R.id.iv_settings);
        ivMessages = (ImageView) mDialog.findViewById(R.id.iv_messages);
        ivConfessions = (ImageView) mDialog.findViewById(R.id.iv_confessions);
        llMessagesCountContainer = (LinearLayout) mDialog.findViewById(R.id.ll_messages_count_container);
        llConfessionsCountContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confessions_count_container);
        ivMic = (ImageView) mDialog.findViewById(R.id.iv_mic);
        tvName = (TextView) mDialog.findViewById(R.id.tv_name);
        tvRightGuessCount = (TextView) mDialog.findViewById(R.id.tv_right_guess_count);
        tvRightGuessText = (TextView) mDialog.findViewById(R.id.tv_right_guess_text);
        tvFriendsCount = (TextView) mDialog.findViewById(R.id.tv_friends_count);
        tvFriendsText = (TextView) mDialog.findViewById(R.id.tv_friends_text);
        tvMessagesCount = (TextView) mDialog.findViewById(R.id.tv_messages_count);
        tvMessagesText = (TextView) mDialog.findViewById(R.id.tv_messages_text);
        tvConfessionsCount = (TextView) mDialog.findViewById(R.id.tv_confessions_count);
        tvConfessionsText = (TextView) mDialog.findViewById(R.id.tv_confessions_text);


        ivMessages.setVisibility(View.INVISIBLE);
        ivConfessions.setVisibility(View.INVISIBLE);



        tvOnboardIntroName.setText(mFullName);



        llOnboardIntroContainer.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams introContainerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mHeight / 2);
        llOnboardIntroContainer.setLayoutParams(introContainerParams);


        tvOnboardIntroName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.055));
        tvOnboardIntroMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.04));
        tvOnboardIntroLetsgo.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mWidth * 0.040));


        tvOnboardIntroLetsgo.setPadding((int) (mWidth * 0.07), (int) (mHeight * 0.03),
                (int) (mWidth * 0.07), (int) (mHeight * 0.03));
        tvOnboardIntroLetsgo.setGravity(Gravity.BOTTOM);


        float boldTextSize = (int) (mWidth * 0.045);
        float regularTextSize = (int) (mWidth * 0.035);


        RelativeLayout.LayoutParams yellowBackgroundHeaderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                mHeight / 3);
        llYellowBackgroundHeader.setLayoutParams(yellowBackgroundHeaderParams);

        llMainHeaderContainer.setPadding((int) (mWidth * 0.05), (int) (mHeight * 0.025),
                (int) (mWidth * 0.05), 0);


        tvRightGuessCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        tvRightGuessText.setTextSize(TypedValue.COMPLEX_UNIT_PX, regularTextSize);
        tvFriendsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        tvFriendsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, regularTextSize);


        final ViewTreeObserver observer = llMicContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int micContainerHeight = (int) (llMicContainer.getHeight() * 0.7);
                        LinearLayout.LayoutParams micParams = new LinearLayout.LayoutParams(micContainerHeight, micContainerHeight);
                        ivMic.setLayoutParams(micParams);

                    }
                });


        int countTextSize = (int) (mHeight * 0.1);
        Log.d("MEASURE", "countextsie: " + countTextSize);
        tvMessagesCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        tvConfessionsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        tvMessagesText.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);
        tvConfessionsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);


        int logoHeight = mWidth / 8;
        RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(logoHeight, logoHeight);
        logoParams.setMargins(0, 0, logoHeight / 5, 0);
        ivLogo.setLayoutParams(logoParams);


        int settingsHeight = (int) (logoHeight * 0.8);
        RelativeLayout.LayoutParams settingsParams = new RelativeLayout.LayoutParams(settingsHeight, settingsHeight);
        settingsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        settingsParams.addRule(RelativeLayout.CENTER_VERTICAL);
        ivSettings.setLayoutParams(settingsParams);


        llMessagesCountContainer.setPadding((int) (mWidth * 0.18), 0, 0, 0);
        llConfessionsCountContainer.setPadding((int) (mWidth * 0.18), 0, 0, 0);


        int paddingForMicContainer = (int) (mWidth * 0.02);
        llMicContainer.setPadding(paddingForMicContainer, paddingForMicContainer,
                paddingForMicContainer, paddingForMicContainer);


        final ViewTreeObserver observer1 = llRightGuessFriendsContainer.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int guessFriendsContainer = (int) (llRightGuessFriendsContainer.getHeight() / 2.5);
                        llRightGuessFriendsContainer.setPadding(0, guessFriendsContainer / 2, 0, guessFriendsContainer / 2);

                    }
                });


    }
}

package com.app.noknok.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.app.noknok.activities.LoginActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.services.FirebaseListeners;

import io.realm.Realm;

/**
 * Created by dev on 4/7/17.
 */

public class SessionExpireDialog extends BaseActivity implements View.OnClickListener {

    public static Context mContext;
    Dialog mDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvHeaderText, tvUserProceed;
    ImageView ivIcon;
    Realm mRealm;
    SharedPreferences preferences;


    public SessionExpireDialog(Context context) {
        mContext = context;

    }

    public void showDialog() {

        preferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);

        if (FirebaseListeners.getListenerClass(mContext) != null) {
            FirebaseListeners.getListenerClass(mContext).removeProfileListener();
            FirebaseListeners.getListenerClass(mContext).removeConfessionListener(preferences.getString(Config.USER_ID,""));
            FirebaseListeners.getListenerClass(mContext).removeRemovedConfesListeners();
            FirebaseListeners.getListenerClass(mContext).removeConfesMessageListener();
            FirebaseListeners.getListenerClass(mContext).removeChatMessageListener();

        }

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_user_session_check);
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

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_confession_type_circle_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_circle_container);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_options_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_text_container);
        tvHeaderText = (TextView) mDialog.findViewById(R.id.tv_user_session_type_text);
        tvUserProceed = (TextView) mDialog.findViewById(R.id.tv_user_session_proceed);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_confession_type_icon);

        int circleRadius = width - (width / 4);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleRadius, circleRadius);
        llCircle.setLayoutParams(circleParams);


        LinearLayout.LayoutParams headerTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int headerTextMargin = (circleRadius / 2) / 5;
        llHeaderTextContainer.setGravity(Gravity.BOTTOM);
        llHeaderTextContainer.setPadding(0, 0, 0, headerTextMargin);

        tvHeaderText.setPadding(headerTextMargin, 0, headerTextMargin, 0);
        tvHeaderText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.7));


        llOptionsContainer.setPadding(0, 0, 0, headerTextMargin);
        headerTextParams.setMargins(0, headerTextMargin / 2, 0, 0);
        headerTextParams.gravity = Gravity.CENTER;
        tvUserProceed.setLayoutParams(headerTextParams);
        tvUserProceed.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));

    }

    private void initListeners() {
        tvUserProceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_session_proceed:
                preferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);

//                if (FirebaseListeners.getListenerClass(mContext) != null) {
//                    FirebaseListeners.getListenerClass(mContext).removeProfileListener();
//                    FirebaseListeners.getListenerClass(mContext).removeConfessionListener(preferences.getString(Config.USER_ID,""));
//                    FirebaseListeners.getListenerClass(mContext).removeRemovedConfesListeners();
//                    FirebaseListeners.getListenerClass(mContext).removeConfesMessageListener();
//                    FirebaseListeners.getListenerClass(mContext).removeChatMessageListener();
//
//                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
//                mRealm.beginTransaction();
//                mRealm.delete(ChatRealm.class);
//                mRealm.delete(MessageRealm.class);
//                mRealm.delete(FriendsModelRealm.class);
//                mRealm.delete(ContactsRealm.class);
//
//                mRealm.commitTransaction();
                mDialog.dismiss();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                ((Activity)mContext).finish();
                System.exit(2);

        }
    }
}
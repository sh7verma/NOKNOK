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
import com.app.noknok.activities.LoginActivity;

import io.realm.Realm;

/**
 * Created by dev on 21/7/17.
 */

public class LogoutDialog implements View.OnClickListener {

    Context mContext;
    Dialog mDialog;
    Dialog mParentDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvConfessionAsDiscard, tvCancel;
    ImageView ivIcon;
    Realm mRealm;


    public LogoutDialog(Context context, Dialog parentDialog) {
        mContext = context;
        mParentDialog = parentDialog;
    }


    public LogoutDialog(Context context) {
        mContext = context;
    }

    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_message_discard);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        mDialog.show();

        mRealm = Realm.getDefaultInstance();

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
        tvConfessionAsDiscard.setText(R.string.log_out);

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

//                if (FirebaseListeners.getListenerClass(mContext) != null) {
//                    FirebaseListeners.getListenerClass(mContext).removeProfileListener();
//                    FirebaseListeners.getListenerClass(mContext).removeConfessionListener(mUserId);
//                    FirebaseListeners.getListenerClass(mContext).removeRemovedConfesListeners();
//                    FirebaseListeners.getListenerClass(mContext).removeConfesMessageListener();
//                    FirebaseListeners.getListenerClass(mContext).removeChatMessageListener();
//
//                }



                SharedPreferences.Editor editor = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

//                mRealm.beginTransaction();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
//                mRealm.delete(ChatRealm.class);
//                mRealm.delete(FriendsModelRealm.class);
//                mRealm.delete(MessageRealm.class);
//                mRealm.delete(ContactsRealm.class);
//                mRealm.commitTransaction();

                mDialog.dismiss();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                ((Activity) mContext).finish();

                break;

            case R.id.tv_confession_discard_type_cancel:
                mDialog.dismiss();
                break;

        }
    }
}
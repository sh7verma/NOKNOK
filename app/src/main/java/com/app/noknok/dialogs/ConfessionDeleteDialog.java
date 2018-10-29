package com.app.noknok.dialogs;

/**
 * Created by dev on 1/8/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import com.app.noknok.adapters.ConfessionsCardPagerAdapter;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import io.realm.Realm;


public class ConfessionDeleteDialog implements View.OnClickListener{

    Context mContext;
    Dialog mDialog;

    RelativeLayout rlCircleMainContainer, rlMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvConfessionDelete, tvCancel;
    ImageView ivIcon;
    Realm mRealm;
    String messageId;
    DatabaseReference confessionsReference;
    DatabaseReference messageReference;
    boolean isThisFromChatActivity = false;
    ConfessionsCardPagerAdapter confessionsCardPagerAdapter;


    public ConfessionDeleteDialog(Context mContext, String messageId, DatabaseReference confessionsReference, DatabaseReference messageReference, boolean isThisFromChatActivity, ConfessionsCardPagerAdapter confessionsCardPagerAdapter) {
        this.mContext=mContext;
        this.messageId=messageId;
        this.confessionsReference=confessionsReference;
        this.messageReference=messageReference;
        this.isThisFromChatActivity = isThisFromChatActivity;
        this.confessionsCardPagerAdapter = confessionsCardPagerAdapter;
    }

    public ConfessionDeleteDialog(Context mContext, String messageId, DatabaseReference confessionReference, DatabaseReference messageReference, boolean isThisFromChatActivity) {
        this.mContext=mContext;
        this.messageId=messageId;
        this.confessionsReference = confessionReference;
        this.messageReference=messageReference;
        this.isThisFromChatActivity = isThisFromChatActivity;
    }

    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_confession_delete);
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

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_confession_delete_circle_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_delete_circle);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_confession_delete_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_options_delete_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_delete_circle);
        tvConfessionDelete = (TextView) mDialog.findViewById(R.id.tv_confession_delete_delete);
        tvCancel = (TextView) mDialog.findViewById(R.id.tv_confession_delete_cancel);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_confession_delete_icon);
        rlMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_confession_delete_main_container);

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
        tvConfessionDelete.setLayoutParams(headerTextParams);
        tvCancel.setLayoutParams(headerTextParams);
        tvConfessionDelete.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));
        tvConfessionDelete.setText("DELETE");
        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (headerTextMargin * 0.75));
    }

    private void initListeners() {
        tvConfessionDelete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confession_delete_delete:

                if ((new ConnectionDetector(mContext).isConnectingToInternet())) {
                    LoadingDialog.getLoader().showLoader(mContext);
                    confessionsReference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            messageReference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

//                                    if(!checkIfChatExists(messageId)) {
//                                        mRealm.beginTransaction();
//                                        RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", messageId).findAll();
//                                        messageRealm.deleteFirstFromRealm();
//                                        mRealm.commitTransaction();
//                                    }

                                    mDialog.dismiss();
                                    LoadingDialog.getLoader().dismissLoader();
                                    if (isThisFromChatActivity) {
                                        ((Activity) mContext).finish();
                                    }else{

                                     //   confessionsCardPagerAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }else{
                    ServerAPIs.noInternetConnection(rlMainContainer);
                }

                break;

            case R.id.tv_confession_delete_cancel:
                mDialog.dismiss();

                break;

        }
    }

}

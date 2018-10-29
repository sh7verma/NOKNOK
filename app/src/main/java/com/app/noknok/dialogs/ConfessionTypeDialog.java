package com.app.noknok.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.activities.BaseActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.models.ConfessionsContact;

import java.util.ArrayList;

/**
 * Created by dev on 23/6/17.
 */

public class ConfessionTypeDialog extends BaseActivity implements View.OnClickListener {

    Context mContext;
    Dialog mDialog;

    RelativeLayout rlCircleMainContainer;
    LinearLayout llCircleContainer, llCircle, llOptionsContainer, llHeaderTextContainer;
    TextView tvHeaderText, tvConfessionAsName, tvConfessionAsAnon,
                tvCancel;
    ImageView ivIcon;
    ArrayList<ConfessionsContact> confessionsContactsList;


    public ConfessionTypeDialog(Context context, ArrayList<ConfessionsContact> confessionsContactsList) {
        mContext = context;
        this.confessionsContactsList = confessionsContactsList;
    }

    public  void showDialog(){

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_confession_type);
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

        rlCircleMainContainer = (RelativeLayout) mDialog.findViewById(R.id.rl_confession_type_circle_main_container);
        llCircleContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_circle_container);
        llCircle = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_circle);
        llOptionsContainer = (LinearLayout) mDialog.findViewById(R.id.ll_options_container);
        llHeaderTextContainer = (LinearLayout) mDialog.findViewById(R.id.ll_confession_type_text_container);
        tvHeaderText = (TextView) mDialog.findViewById(R.id.tv_confession_type_text);
        tvConfessionAsName = (TextView) mDialog.findViewById(R.id.tv_confession_as_name);
        tvConfessionAsAnon = (TextView) mDialog.findViewById(R.id.tv_confession_as_anonymous);
        tvCancel = (TextView) mDialog.findViewById(R.id.tv_confession_type_cancel);
        ivIcon = (ImageView) mDialog.findViewById(R.id.iv_confession_type_icon);


        int circleRadius = width - (width/6);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleRadius, circleRadius);
        llCircle.setLayoutParams(circleParams);


        LinearLayout.LayoutParams headerTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int headerTextMargin = (circleRadius/2)/5;
        llHeaderTextContainer.setGravity(Gravity.BOTTOM);
        llHeaderTextContainer.setPadding(0,0,0,headerTextMargin);
        tvHeaderText.setPadding(headerTextMargin,0,headerTextMargin,0);
        tvHeaderText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(headerTextMargin * 0.7));

        llOptionsContainer.setPadding(0,0,0,headerTextMargin);
        headerTextParams.setMargins(headerTextMargin/2 ,headerTextMargin/2 ,headerTextMargin/2,0);
        headerTextParams.gravity = Gravity.CENTER;
        tvConfessionAsName.setLayoutParams(headerTextParams);
        tvConfessionAsAnon.setLayoutParams(headerTextParams);
        tvCancel.setLayoutParams(headerTextParams);
        tvConfessionAsName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(headerTextMargin * 0.65));
        tvConfessionAsAnon.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(headerTextMargin * 0.65));
        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(headerTextMargin * 0.60));

        SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(),MODE_PRIVATE);

        tvConfessionAsName.setText(sp.getString(Config.FULL_NAME,"").split(" ")[0]);
    }

    private void initListeners() {

        tvConfessionAsName.setOnClickListener(this);
        tvConfessionAsAnon.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confession_as_name:
                new NewAudioRecordingDialog(mContext, confessionsContactsList, false).showDialog();
                mDialog.dismiss();
                break;

            case R.id.tv_confession_type_cancel:
                mDialog.dismiss();
                break;

            case R.id.tv_confession_as_anonymous:
               new NewAudioRecordingDialog(mContext,confessionsContactsList, true).showDialog();
                mDialog.dismiss();
                break;
        }
    }
}

package com.app.noknok.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.adapters.DirectMessageAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.NewAudioRecordingDialog;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.MutualFriendsModel;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;

import java.util.ArrayList;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dev on 27/6/17.
 */
public class DirectMessageActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS = 101;
    SessionExpireDialog sessionExpireDialog;
    ArrayList<MutualFriendsModel> mFriendsList = new ArrayList<>();
    MutualFriendsModel receiverUserID;
    //    ArrayList<String> mNameArraylist = new ArrayList<>();
    ListView lvFriendList;
    TextView tvFriendsTitle;
    RelativeLayout rlRegistrationFormContainer;
    ImageView imDirectMessageHeader;
    LinearLayout llTxtTitleContainer, llNameSelectionContainer;
    TextView tvFriendSelection, tvDirectMessageTitle1, tvDirectMessageTitle2;
    Handler handler = new Handler();
    RelativeLayout rlNoResultFound;
    ImageView imgNoResultFound;
    TextView tvNoResultFound;
    LinearLayout btDirectCancel, llDirectNoResultFound, llnameselectioninnercontainer;
    Runnable runnable;
    FrameLayout flDirectMessageContainer;
    int delayTime = 100;
    Button btNext;
    //  {"#ED5565", "FC6E51", "#FFCE54", "#A0D468", "#48CFAD", "4FC1E9", "#5D9CEC", "#AC92EC", "#EC87C0", "#CCD1D9", "#656D78"};
    ArrayList<String> mColorArrayList = new ArrayList<>();
    ImageView ivIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direct_message);

        initUI();
        loadFriends();
        initListeners();
    }

    private void initListeners() {
        btDirectCancel.setOnClickListener(this);
        btNext.setOnClickListener(this);
    }

    public void loadFriends() {

        String access_token = sp.getString(Config.ACCESS_TOKEN, "");

        if ((new ConnectionDetector(this).isConnectingToInternet())) {
            LoadingDialog.getLoader().showLoader(this);
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.get_mutual_friends(access_token, new Callback<RetroHelper.GetMutualFriends>() {
                @Override
                public void success(RetroHelper.GetMutualFriends getMutualFriends, Response response) {
                    if (getMutualFriends != null) {
                        for (int i = 0; i < getMutualFriends.response.size(); i++) {
                            RetroHelper.GetMutualFriends.Response res = getMutualFriends.response.get(i);
                            MutualFriendsModel mutualFriendsModel = new MutualFriendsModel();
                            mutualFriendsModel.setName_in_contact(res.name_in_contact.toUpperCase());
                            mutualFriendsModel.setPhone_number(res.phone_number);
                            mutualFriendsModel.setFullname(res.fullname.toUpperCase());
                            mutualFriendsModel.setCountry_code(res.country_code);
                            mutualFriendsModel.setAccess_token(res.access_token);
                            mutualFriendsModel.setUser_id(res.user_id);
                            mutualFriendsModel.setDevice_token(res.device_token);
                            mFriendsList.add(mutualFriendsModel);
//                            mNameArraylist.add(mutualFriendsModel.getName_in_contact());
                            Log.d("responghjhsee", String.valueOf(mFriendsList.get(i).getName_in_contact()));
                        }

                        for (int k = 0; k < (mFriendsList.size() / Config.GETCOLOR().size()) + 1; k++) {
                            mColorArrayList.addAll(Config.GETCOLOR());
                            Log.d("colorsize", mColorArrayList.size() + "--" + mFriendsList.size());
                        }

                        if (mFriendsList.size() > 0) {
                            flDirectMessageContainer.setVisibility(View.VISIBLE);
                            rlNoResultFound.setVisibility(View.GONE);
                            lvFriendList.setAdapter(new DirectMessageAdapter(DirectMessageActivity.this, mFriendsList, mColorArrayList));
                            generateRandomName();

                        } else {
                            flDirectMessageContainer.setVisibility(View.GONE);
                            rlNoResultFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        rlNoResultFound.setVisibility(View.VISIBLE);
                        flDirectMessageContainer.setVisibility(View.GONE);
                    }
                    LoadingDialog.getLoader().dismissLoader();
                }

                @Override
                public void failure(RetrofitError error) {
                    ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, "");
                    LoadingDialog.getLoader().dismissLoader();
                }
            });
        } else {
            ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
            LoadingDialog.getLoader().dismissLoader();
        }
    }

    public void initUI() {

        btDirectCancel = (LinearLayout) findViewById(R.id.bt_direct_cancel);
        rlNoResultFound = (RelativeLayout) findViewById(R.id.rl_direct_no_result);
        imgNoResultFound = (ImageView) findViewById(R.id.img_direct_no_result);
        tvNoResultFound = (TextView) findViewById(R.id.tv_direct_no_result_found);
        flDirectMessageContainer = (FrameLayout) findViewById(R.id.fl_direct_message);
        llDirectNoResultFound = (LinearLayout) findViewById(R.id.ll_direct_no_result_found);

//        flDirectMessageContainer.setVisibility(View.INVISIBLE);
//        rlNoResultFound.setVisibility(View.VISIBLE);

        tvFriendsTitle = (TextView) findViewById(R.id.tv_friends_title);
        tvDirectMessageTitle1 = (TextView) findViewById(R.id.txt_direct_message_title1);
        tvDirectMessageTitle2 = (TextView) findViewById(R.id.txt_direct_message_title2);
        imDirectMessageHeader = (ImageView) findViewById(R.id.img_direct_message);
        lvFriendList = (ListView) findViewById(R.id.lv_direct_message_friends_list);
        tvFriendSelection = (TextView) findViewById(R.id.tv_name_selection);
        btNext = (Button) findViewById(R.id.bt_direct_message_next_button);
        rlRegistrationFormContainer = (RelativeLayout) findViewById(R.id.rl_direct_message_main_container);
        ivIcon = (ImageView) findViewById(R.id.iv_icon_selection);
        llnameselectioninnercontainer = (LinearLayout) findViewById(R.id.ll_name_selection_inner_container);

        LinearLayout.LayoutParams imgParamsIcon = new LinearLayout.LayoutParams(mScreenwidth / 10, mScreenheight / 15);
        imgParamsIcon.setMargins(mScreenwidth / 25, 0, mScreenwidth / 25, 0);
        ivIcon.setLayoutParams(imgParamsIcon);

        tvFriendsTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 28);
        tvFriendsTitle.setPadding(mScreenwidth / 80, mScreenheight / 90, mScreenwidth / 80, mScreenheight / 90);

        llDirectNoResultFound.setGravity(Gravity.CENTER);

        tvNoResultFound.setTextColor(getResources().getColor(R.color.grey));
        tvNoResultFound.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 35);

        llTxtTitleContainer = (LinearLayout) findViewById(R.id.txt_direct_message_title_container);
        llNameSelectionContainer = (LinearLayout) findViewById(R.id.ll_name_selection_container);

        LinearLayout.LayoutParams txtContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtContainerParams.setMargins(0, mScreenheight / 35, 0, 0);
        llTxtTitleContainer.setLayoutParams(txtContainerParams);
        LinearLayout.LayoutParams lvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lvParams.setMargins(mScreenwidth / 8, 0, 0, 0);
        lvFriendList.setLayoutParams(lvParams);

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mScreenheight / 6);
        imgParams.setMargins(0, mScreenheight / 18, 0, 0);
        imDirectMessageHeader.setLayoutParams(imgParams);

        LinearLayout.LayoutParams txtContainerInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        txtContainerInnerParams.setMargins(mScreenheight / 25, 0, mScreenheight / 25, 0);
        llnameselectioninnercontainer.setLayoutParams(txtContainerInnerParams);
        llnameselectioninnercontainer.setBackgroundResource(R.drawable.circularcorners);

        LinearLayout.LayoutParams nameSelectionContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameSelectionContainerParams.setMargins(0, mScreenheight / 10, 0, 0);
        llNameSelectionContainer.setLayoutParams(nameSelectionContainerParams);

        float txtSize = mScreenwidth / 25;
        float txtSizeSelection = mScreenwidth / 20;

        LinearLayout.LayoutParams tvSelectionContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mScreenheight / 8);
        nameSelectionContainerParams.setMargins(mScreenwidth / 10, mScreenheight / 10, mScreenwidth / 10, 0);
        tvFriendSelection.setLayoutParams(tvSelectionContainerParams);

        tvFriendSelection.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSizeSelection);
//        tvFriendSelection.setTypeface(Typeface.BOLD);
        tvDirectMessageTitle1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);
        tvDirectMessageTitle2.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);

        LinearLayout.LayoutParams btnextParams = new LinearLayout.LayoutParams(mScreenwidth / 3, mScreenheight / 12);
        nameSelectionContainerParams.setMargins(mScreenwidth / 10, mScreenheight / 10, mScreenwidth / 10, 0);
        btnextParams.setMargins(0, mScreenheight / 4, 0, 0);
        btnextParams.gravity = Gravity.CENTER;
        btNext.setLayoutParams(btnextParams);
    }

    public void generateRandomName() {
        if (mFriendsList.size() > 0) {
            if (mFriendsList.size() == 1) {

                tvFriendSelection.setText(mFriendsList.get(0).getName_in_contact());
//                Drawable color = getResources().getDrawable(R.drawable.bg_round);
//                color.setColorFilter(Color.parseColor(mColorArrayList.get(1)),Mo);
//                ivIcon.setBackgroundColor(Color.parseColor(mColorArrayList.get(1)));

                ivIcon.setBackgroundResource(R.drawable.bg_round);
                GradientDrawable roundDrawable = (GradientDrawable) ivIcon.getBackground();
                roundDrawable.setCornerRadius(5);
                roundDrawable.setColor(Color.parseColor(mColorArrayList.get(1)));

                receiverUserID = mFriendsList.get(0);
                btNext.setVisibility(View.VISIBLE);
                llnameselectioninnercontainer.setBackgroundResource(R.drawable.white_round_corner);
                tvFriendSelection.setTextColor(getResources().getColor(R.color.black));

            } else {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        final int value;

                        if (mFriendsList.size() > 1) {

                            value = random.nextInt(mFriendsList.size());

                        } else {

                            value = 0;

                        }

                        Log.e("runable", "timer " + delayTime);
                        if (delayTime == 2000) {
                            handler.removeCallbacks(runnable);
                            lvFriendList.smoothScrollToPosition(value);

                            tvFriendSelection.setText(mFriendsList.get(value).getName_in_contact());
//                            ivIcon.setBackgroundColor(Color.parseColor(mColorArrayList.get(value)));
                            ivIcon.setBackgroundResource(R.drawable.bg_round);
                            GradientDrawable roundDrawable = (GradientDrawable) ivIcon.getBackground();
                            roundDrawable.setCornerRadius(5);
                            roundDrawable.setColor(Color.parseColor(mColorArrayList.get(value)));

                            receiverUserID = mFriendsList.get(value);
                            btNext.setVisibility(View.VISIBLE);
                            llnameselectioninnercontainer.setBackgroundResource(R.drawable.white_round_corner);
                            tvFriendSelection.setTextColor(getResources().getColor(R.color.black));

                            handler.removeCallbacks(runnable);
                        } else {
                            lvFriendList.smoothScrollToPosition(value);

                            tvFriendSelection.setText(mFriendsList.get(value).getName_in_contact());
                            tvFriendSelection.setTextColor(getResources().getColor(R.color.white));

//                            ivIcon.setBackgroundColor(Color.parseColor(mColorArrayList.get(value)));
                            ivIcon.setBackgroundResource(R.drawable.bg_round);
                            GradientDrawable roundDrawable = (GradientDrawable) ivIcon.getBackground();
                            roundDrawable.setCornerRadius(5);
                            roundDrawable.setColor(Color.parseColor(mColorArrayList.get(value)));

                            handler.postDelayed(this, 100);
                            delayTime = delayTime + 50;
                        }

                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } else {
            Toast.makeText(this, "No Mutual Friends", Toast.LENGTH_SHORT).show();
            rlNoResultFound.setVisibility(View.VISIBLE);
            flDirectMessageContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_direct_cancel:
                onBackPressed();
                this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;

            case R.id.bt_direct_message_next_button:
                new NewAudioRecordingDialog(this, receiverUserID).showDialog();


                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        permissionCheck();
        sessionExpireDialog = new SessionExpireDialog(DirectMessageActivity.this);
    }
}
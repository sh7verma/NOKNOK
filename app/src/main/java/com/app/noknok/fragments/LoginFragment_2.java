package com.app.noknok.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.HomeActivity;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.interfaces.AsyncResponse;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.Profile;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.services.GetContacts;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.app.noknok.fragments.HomeFragment.mPermission;

/**
 * Created by dev on 20/6/17.
 */

public class LoginFragment_2 extends BaseFragment implements View.OnClickListener {

    public static final String ANDROID_PLATFORM_STATUS = "2";
    private static final int REQUEST_PERMISSIONS = 101;
    private static final String TAG = "LOGINFRAGMENT2";
    RelativeLayout rlRegistrationFormContainer;
    LinearLayout llRegistrationChild, llOtpContainer, llBackButton;
    TextView tvName, tvHelpingText, tvOTP, tvOtpTimer, tvDoneButton;
    EditText etEnterOTP;
    ImageView ivResend, ivCall;
    View view;
    String mFullName, mPhoneNumber, mCountryCode;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mProfileReference, mChatReference;
    String mPushToken;
    String androidAccountType = "0"; // 0 for android and 1 for iphone
    Profile profile = new Profile();
    String mContacts = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login_2, container, false);

        initUI();
        initListeners();
        permissionCheck();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mPushToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "oncreate token: " + mPushToken);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProfileReference = mFirebaseDatabase.getReference("profile").getRef();
        mChatReference = mFirebaseDatabase.getReference("Chats");

        Bundle bundle = getArguments();
        mFullName = bundle.getString("Name");
        mPhoneNumber = bundle.getString("Phone");
        mCountryCode = bundle.getString("CountryCode");

//        if (!mFullName.contains(" ")) {
        tvName.setText("HELLO " + mFullName.split(" ")[0].replace(" ","")+"!");
        //mFullName = mFullName.split(" ")[0];
//        }


        String verifyOTPString = getResources().getString(R.string.verify_your_phone);
        tvHelpingText.setText(verifyOTPString + " " + mCountryCode + " " + mPhoneNumber);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        countDownTimer();
    }

    void countDownTimer() {

        tvOtpTimer.setVisibility(View.VISIBLE);
        ivCall.setEnabled(false);
        ivResend.setEnabled(false);
        ivCall.setAlpha(0.5f);
        ivResend.setAlpha(0.5f);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvOtpTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                tvOtpTimer.setText("0");
                ivCall.setAlpha(1.0f);
                ivResend.setAlpha(1.0f);
                ivCall.setEnabled(true);
                ivResend.setEnabled(true);
                tvOtpTimer.setVisibility(View.INVISIBLE);
            }

        }.start();
    }

    private void initUI() {

        rlRegistrationFormContainer = (RelativeLayout) view.findViewById(R.id.rl_registration_form_container);
        llRegistrationChild = (LinearLayout) view.findViewById(R.id.ll_registration_form_child);
        llOtpContainer = (LinearLayout) view.findViewById(R.id.ll_otp_container);
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_back_button);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvHelpingText = (TextView) view.findViewById(R.id.tv_otp_helping_text);
        tvOTP = (TextView) view.findViewById(R.id.tv_otp);
        tvOtpTimer = (TextView) view.findViewById(R.id.tv_otp_timer);
        tvDoneButton = (TextView) view.findViewById(R.id.tv_done_button);
        etEnterOTP = (EditText) view.findViewById(R.id.et_enter_otp);
        ivResend = (ImageView) view.findViewById(R.id.iv_resend);
        ivCall = (ImageView) view.findViewById(R.id.iv_call);

        float boldTextSize = (int) (mScreenwidth * 0.050);

        RelativeLayout.LayoutParams registrationFormParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        registrationFormParams.setMargins(mScreenwidth / 10, mScreenheight / 4, mScreenwidth / 10, (int) (mScreenheight / 5.5));
        rlRegistrationFormContainer.setLayoutParams(registrationFormParams);


        LinearLayout.LayoutParams OTPContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        OTPContainerParams.setMargins(mScreenwidth / 15, mScreenheight / 20, mScreenwidth / 15, 0);
        llOtpContainer.setLayoutParams(OTPContainerParams);


        RelativeLayout.LayoutParams registrationChildParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        registrationChildParams.setMargins(0, 0, 0, mScreenheight / 22);
        llRegistrationChild.setLayoutParams(registrationChildParams);

        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.055));

        tvHelpingText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.040));

        tvDoneButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.060));

        tvDoneButton.setPadding((int) (mScreenwidth * 0.08), (int) (mScreenheight * 0.03),
                (int) (mScreenwidth * 0.08), (int) (mScreenheight * 0.03));


        tvOTP.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.040));

        tvOtpTimer.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.040));

        etEnterOTP.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);


    }

    private void initListeners() {
        llBackButton.setOnClickListener(this);
        tvDoneButton.setOnClickListener(this);
        ivCall.setOnClickListener(this);
        ivResend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back_button:
                getFragmentManager().popBackStack();
                break;

            case R.id.tv_done_button:

                verifyOTP();
//              permissionCheck();
//                getContacts();


                break;

            case R.id.iv_resend:
                resendOTP();
                break;

            case R.id.iv_call:
                callToGetOTP();
        }
    }

    private void callToGetOTP() {

        if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
            LoadingDialog.getLoader().showLoader(getActivity());
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.call_to_verify(mFullName, mCountryCode, mPhoneNumber, new Callback<RetroHelper.RegisterNumber>() {
                @Override
                public void success(RetroHelper.RegisterNumber registerNumber, Response response) {
                    LoadingDialog.getLoader().dismissLoader();
                    if (registerNumber.response != null) {

                        ServerAPIs.showSuccessSnackbar(rlRegistrationFormContainer, getString(R.string.pickup_your_phone), getResources().getColor(R.color.green));
                        countDownTimer();

                    } else {

                        ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, registerNumber.error);

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    LoadingDialog.getLoader().dismissLoader();
                    ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
                }
            });
        } else {
            ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
        }

    }

    private void resendOTP() {

        if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
            LoadingDialog.getLoader().showLoader(getActivity());
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.register_number(mFullName, mCountryCode, mPhoneNumber, new Callback<RetroHelper.RegisterNumber>() {
                @Override
                public void success(RetroHelper.RegisterNumber registerNumber, Response response) {
                    LoadingDialog.getLoader().dismissLoader();
                    if (registerNumber.response != null) {
                        ServerAPIs.showSuccessSnackbar(rlRegistrationFormContainer, getString(R.string.otp_sent), getResources().getColor(R.color.green));
                        countDownTimer();

                    } else {
                        ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, registerNumber.error);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    LoadingDialog.getLoader().dismissLoader();
                    ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
                }
            });
        } else {
            ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
        }

    }


    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission
                            .READ_CONTACTS},
                    REQUEST_PERMISSIONS);
        } else {
            mPermission = true;
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Log.d("prrmissijaod", "given " + requestCode);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("prrmissijaod", "given " + HomeFragment.mPermission);
                    HomeFragment.mPermission = true;
                    getContacts();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale
                        (getActivity(), Manifest.permission.READ_CONTACTS)) {

                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                        permissionCheck();
                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale
                        (getActivity(), Manifest.permission.READ_CONTACTS)) {
                    snackView();
                }
                return;
            }
        }
    }

    public void snackView() {

        Snackbar.make(getActivity().findViewById(android.R.id.content), "Enable Contacts Permissions from settings",
                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).show();
    }

    public void getContacts() {
        new GetContacts(getActivity(), new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("responsss", output);
                mContacts = output;
//                if (!sp.getBoolean(Config.SESSION_EXPIRE, false)) {
//                   verifyOTP();
//                }
            }
        }).execute();
    }

    private void verifyOTP() {

        String otp = etEnterOTP.getText().toString();
        String longitude = sp.getString(Config.LONGITUDE, "");
        String latitude = sp.getString(Config.LATITUDE, "");

        Log.d("LOCATIONTAG","in verify longitude: "+longitude);
        Log.d("LOCATIONTAG","in verify latitude: "+latitude);

        mPushToken = sp.getString(Config.DEVICE_TOKEN, "");

        Log.d(TAG, "verify pushtoken: " + mPushToken);
        if (isJSONValid(mPushToken)) {
            try {
                JSONObject jsonObject = new JSONObject(mPushToken);
                mPushToken = jsonObject.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "pushtoken: " + mPushToken);
        FirebaseInstanceId.getInstance().getToken();

        if (otp.equals("")) {
            showError(etEnterOTP);
            etEnterOTP.requestFocus();
        } else {

            noError(etEnterOTP);

            if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
                LoadingDialog.getLoader().showLoader(getActivity());


                RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
                RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
                mRetroInterface.verify_number(mCountryCode, mPhoneNumber, otp, ANDROID_PLATFORM_STATUS,
                        latitude, longitude, mContacts, mPushToken, new Callback<RetroHelper.VerifyAccount>() {
                            @Override
                            public void success(final RetroHelper.VerifyAccount verifyAccount, final Response response) {

                                if (verifyAccount.response != null) {
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName(),
                                            Context.MODE_PRIVATE).edit();
                                    editor.putString(Config.FULL_NAME, mFullName.toUpperCase());
                                    editor.putString(Config.PHONE_NUMBER, mPhoneNumber);
                                    editor.putString(Config.COUNTRY_CODE, mCountryCode);
                                    editor.putString(Config.ACCESS_TOKEN, verifyAccount.response.access_token);
                                    editor.putString(Config.USER_ID, verifyAccount.response.user_id);
                                    editor.putString(Config.DEVICE_TOKEN, mPushToken);
                                    editor.putBoolean(Config.LOGGED_IN, true);
                                    editor.putBoolean(Config.FIRSTLOGIN, true);
                                    editor.apply();
                                    profile.setAccess_token(verifyAccount.response.access_token);
                                    profile.setAccount_type(androidAccountType);
                                    profile.setCountry_code(mCountryCode);
                                    profile.setName(mFullName.toUpperCase());
                                    profile.setOnline_status("ONLINE");
                                    profile.setPush_token(mPushToken);
                                    profile.setPhone_number(mCountryCode + mPhoneNumber);
                                    profile.setUser_id(verifyAccount.response.user_id);
                                    profile.setNumber(mPhoneNumber);
                                    mProfileReference.child(verifyAccount.response.user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {

                                                Profile profile1 = dataSnapshot.getValue(Profile.class);

                                                if (verifyAccount.response.user_id.equals(profile1.getUser_id())) {
                                                    profile1.setAccess_token(verifyAccount.response.access_token);
                                                    profile1.setPush_token(mPushToken);
                                                    profile = profile1;

                                                    mProfileReference.child(verifyAccount.response.user_id).setValue(profile);

                                                    if (profile1.getChat_dialogue_id() != null) {
                                                        HashMap<String, String> chatDialogueMap = profile1.getChat_dialogue_id();
                                                        Set<String> keySet = chatDialogueMap.keySet();
                                                        Iterator<String> iterator1 = keySet.iterator();
                                                        while (iterator1.hasNext()) {
                                                            String s = iterator1.next();
                                                            mChatReference.child(s).child("access_tokens").child(profile1.getUser_id()).setValue(verifyAccount.response.access_token);
                                                            mChatReference.child(s).child("push_tokens").child(profile1.getUser_id()).setValue(mPushToken);


                                                        }
                                                        mProfileReference.removeEventListener(this);

                                                    }
                                                } else {
                                                    mProfileReference.child(verifyAccount.response.user_id).setValue(profile);
                                                }


                                            } else {
                                                mProfileReference.child(verifyAccount.response.user_id).setValue(profile);
                                            }

//                                            BaseClass.mBaseClass.receiveMessages();
//                                            BaseClass.mBaseClass.getConfessions();

                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                            getActivity().finish();
                                            LoadingDialog.getLoader().dismissLoader();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, verifyAccount.error);
                                }
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                LoadingDialog.getLoader().dismissLoader();
                                ServerAPIs.showOtpErrorSnackbar(rlRegistrationFormContainer, "Please enter a valid OTP.");
                            }
                        });
            } else {
                ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
            }

        }

    }


    private void showError(View v) {
        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        rlRegistrationFormContainer.startAnimation(shake);
        vibe.vibrate(200);
        ((GradientDrawable) v.getBackground()).setColor(getResources().getColor(R.color.red));
    }


    private void noError(View v) {
        ((GradientDrawable) v.getBackground()).setColor(Color.TRANSPARENT);
        v.setBackgroundResource(R.drawable.edit_text_round_corner);
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


}

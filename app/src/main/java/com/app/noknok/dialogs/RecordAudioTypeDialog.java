package com.app.noknok.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.app.noknok.activities.DirectMessageActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.fragments.HomeFragment;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.ConfessionsContact;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.app.noknok.activities.CountryCodeActivity.h;

/**
 * Created by dev on 23/6/17.
 */

public class RecordAudioTypeDialog extends Activity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS = 101;
    public static Boolean mPermissionCheck = false;
    Context mContext;
    TextView tvPostAsConfession, tvDirectionMessage;
    ImageView ivCancel, ivCartoonFace;
    Dialog mDialog;
    LinearLayout llRecordAudioTypeContainer;
    LinearLayout llMain;

//    public RecordAudioTypeDialog(Context context) {
//        mContext = context;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_transparent);
        llMain = (LinearLayout) findViewById(R.id.ll_main);
            showmDialog();

    }

    public void showmDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_record_audio_type);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        mDialog.show();

        init();
        initListeners();

    }

    private void initListeners() {

        tvPostAsConfession.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        tvDirectionMessage.setOnClickListener(this);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
                HomeFragment.mRecordTypeDialogOpen=true;
            }
        });

    }

    void init() {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        tvPostAsConfession = (TextView) mDialog.findViewById(R.id.tv_post_as_confession);
        tvDirectionMessage = (TextView) mDialog.findViewById(R.id.tv_direct_message);
        ivCancel = (ImageView) mDialog.findViewById(R.id.iv_record_type_cancel);

        llRecordAudioTypeContainer = (LinearLayout) mDialog.findViewById(R.id.ll_record_audio_type_container);
        ivCartoonFace = (ImageView) mDialog.findViewById(R.id.iv_record_type_face);


        int containerHeight = height - (height / 3);
        RelativeLayout.LayoutParams audioTypeContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                containerHeight);

        audioTypeContainerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        audioTypeContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            audioTypeContainerParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        }

        llRecordAudioTypeContainer.setLayoutParams(audioTypeContainerParams);


        LinearLayout.LayoutParams cartoonFaceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cartoonFaceParams.setMargins(0, (int) (containerHeight * 0.06), 0, (int) (containerHeight * 0.02));
        cartoonFaceParams.gravity = Gravity.CENTER;
        ivCartoonFace.setLayoutParams(cartoonFaceParams);

        tvPostAsConfession.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (width * 0.04));
        tvPostAsConfession.setPadding((int) (width * 0.08) * 2, (int) (width * 0.04),
                (int) (width * 0.08) * 2, (int) (width * 0.04));

        ivCancel.setPadding(0, (int) (width * 0.04), 0, 0);

        tvDirectionMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (width * 0.04));
        tvDirectionMessage.setPadding((int) (width * 0.08) * 2, (int) (width * 0.04),
                (int) (width * 0.08) * 2, (int) (width * 0.04));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_post_as_confession:
                permissionCheck();
                break;

            case R.id.tv_direct_message:
                if ((new ConnectionDetector(mContext).isConnectingToInternet())) {
                    Intent intent = new Intent(mContext, DirectMessageActivity.class);
                    startActivity(intent);
                    mDialog.dismiss();
                    finish();
                    HomeFragment.mRecordTypeDialogOpen=true;
                } else {
//                    ServerAPIs.noInternetConnection(llMain);
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_record_type_cancel:
                mDialog.dismiss();
                finish();
                HomeFragment.mRecordTypeDialogOpen=true;
                break;

        }
    }

    private void getConfessionMember() {

        final ArrayList<ConfessionsContact> confessionsContactsList = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        if ((new ConnectionDetector(mContext).isConnectingToInternet())) {
            LoadingDialog.getLoader().showLoader(mContext);
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.get_contact_for_confession(sp.getString(Config.ACCESS_TOKEN, ""), new Callback<RetroHelper.GetConfessionContacts>() {

                @Override
                public void success(RetroHelper.GetConfessionContacts getConfessionContacts, Response response) {
                    LoadingDialog.getLoader().dismissLoader();
                    if (getConfessionContacts != null) {
                        for (int i = 0; i < getConfessionContacts.response.size(); i++) {
                            RetroHelper.GetConfessionContacts.Response res = getConfessionContacts.response.get(i);
                            ConfessionsContact confessionsContact = new ConfessionsContact();
                            confessionsContact.setUser_id(res.user_id);
                            confessionsContact.setAccess_token(res.access_token);
                            confessionsContact.setCountry_code(res.country_code);
                            confessionsContact.setDevice_token(res.device_token);
                            confessionsContact.setFriend(res.friend);
                            confessionsContact.setLongitude(res.longitude);
                            confessionsContact.setLatitude(res.latitude);
                            confessionsContact.setProfile_status(res.profile_status);
                            confessionsContact.setFullname(res.fullname);
                            confessionsContact.setPlatform_status(res.platform_status);
                            confessionsContactsList.add(confessionsContact);
                        }
                        String confModel = new Gson().toJson(confessionsContactsList);
                        Intent intent = new Intent();
                        intent.putExtra("model", confModel);
                        setResult(RESULT_OK, intent);
                        finish();
                        HomeFragment.mRecordTypeDialogOpen=true;
                    } else {
//                        ServerAPIs.showServerErrorSnackbar(llMain, getConfessionContacts.error);
                        Toast.makeText(mContext, getConfessionContacts.error, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    LoadingDialog.getLoader().dismissLoader();
//                    ServerAPIs.showServerErrorSnackbar(llMain, error.getMessage());
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
//            noInternetConnection(llRecordAudioTypeContainer);
            Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    void permissionCheck() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission
                            .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS);

        } else {

            showLocationAlert();

//                mContext.startService(new Intent(mContext, LocationUpdaterService.class));

//            gps = new GpsTracker(LoginActivity.this);
//            GpsTracker.setGetLocationCallback(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

//                        mContext.startService(new Intent(mContext, LocationUpdaterService.class));

                    showLocationAlert();
//                    gps = new GpsTracker(LoginActivity.this);
//                    GpsTracker.setGetLocationCallback(this);
                    mPermissionCheck = true;

                } else if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                        permissionCheck();
                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    snackBarLocation();
                }
                return;
            }
        }
    }

    public void snackBarLocation() {

        Snackbar.make(findViewById(android.R.id.content), "Enable Location Permissions from settings",
                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).show();


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void showLocationAlert() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(mContext.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(mContext.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        } else {
            getConfessionMember();
        }
    }


}
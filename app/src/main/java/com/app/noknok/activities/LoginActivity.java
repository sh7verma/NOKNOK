package com.app.noknok.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.app.noknok.R;
import com.app.noknok.definitions.Config;
import com.app.noknok.fragments.LoginFragment_1;
import com.app.noknok.services.LocationUpdaterService;
import com.app.noknok.utils.GpsTracker;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by dev on 19/6/17.
 */

public class LoginActivity extends BaseActivity implements GpsTracker.GetLocationCallback {


    private static final int REQUEST_PERMISSIONS = 101;
    public static Boolean mPermissionCheck = false;
    GoogleMap gMap;
    double mLatitude = 0.0, mLongitude = 0.0;
    GpsTracker gps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (sp.getBoolean(Config.LOGGED_IN, false)) {
            sp.edit().putBoolean(Config.FIRSTLOGIN, false).apply();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        permissionCheck();
        initUI();

    }


    void permissionCheck() {

        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission
                            .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS);

        } else {
            if (!isMyServiceRunning(LocationUpdaterService.class)) {
                startService(new Intent(this, LocationUpdaterService.class));
            }
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

                    if (!isMyServiceRunning(LocationUpdaterService.class)) {
                        startService(new Intent(this, LocationUpdaterService.class));
                    }
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

    private void initUI() {

        LoginFragment_1 loginFragment_1 = new LoginFragment_1();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_login_fragment_container, loginFragment_1, "LoginFragment_1");
        fragmentTransaction.commit();

    }


    @Override
    public void onLocationRetrieved(Location location) {
        try {
            if (mLatitude == 0.0 && mLongitude == 0.0) {
                mLatitude = gps.getLatitude();
                mLongitude = gps.getLongitude();

                Log.d("LOCATIONTAG", "in loginactivty longitude: " + mLongitude);
                Log.d("LOCATIONTAG", "in loginactivity latitude: " + mLatitude);

                SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
                editor.putString(Config.LATITUDE, String.valueOf(mLatitude));
                editor.putString(Config.LONGITUDE, String.valueOf(mLongitude));
                editor.apply();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionCheck = true;
        }

    }


    void showAlert() {
        final AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
        build.setTitle(getResources().getString(R.string.location_disabled));
        build.setMessage(getResources().getString(R.string.location_disabled_text));
        build.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        build.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //     Toast.makeText(LoginActivity.this, getResources().getString(R.string.cannot_find_location), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        build.show();
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

}

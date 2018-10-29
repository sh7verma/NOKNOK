package com.app.noknok.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.app.noknok.R;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.fragments.HomeFragment;
import com.app.noknok.services.FirebaseListenerService;
import com.app.noknok.services.FirebaseListeners;
import com.app.noknok.services.LocationUpdaterService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import io.realm.Realm;

import static com.app.noknok.activities.LoginActivity.mPermissionCheck;
import static com.app.noknok.fragments.HomeFragment.mLocationPermission;


/**
 * Created by dev on 20/6/17.
 */

public class HomeActivity extends BaseActivity implements FirebaseListeners.ProfileListenersInterface {

    private static final int REQUEST_PERMISSIONS = 101;
    private static final int REQUEST_LOCATION_PERMISSIONS = 102;
    HomeFragment homeFragment;
    // FirebaseDatabase mDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    Context mContext;
    SessionExpireDialog sessionExpireDialog;
    Realm mRealm;
    boolean isDialogShowing = false;
    Intent intentService;

//    ChildEventListener mListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            if (dataSnapshot.getKey().equals(Config.ACCESS_TOKEN)) {
//
//                Log.d("ACCESSCHECK", "new access: " + dataSnapshot.getValue() + ", old access: " + sp.getString(Config.ACCESS_TOKEN, ""));
//
//                if (!dataSnapshot.getValue().equals(sp.getString(Config.ACCESS_TOKEN, ""))) {
//
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean(Config.SESSION_EXPIRE, true);
//                    editor.apply();
//
//
//                    databaseReference.removeEventListener(this);
//                    sessionExpireDialog.showDialog();
//                    isDialogShowing = true;
//
//                }
//            }
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            if (dataSnapshot.getKey().equals(Config.ACCESS_TOKEN)) {
//
//                Log.d("ACCESSCHECK", "new access: " + dataSnapshot.getValue() + ", old access: " + sp.getString(Config.ACCESS_TOKEN, ""));
//
//                if (!dataSnapshot.getValue().equals(sp.getString(Config.ACCESS_TOKEN, ""))) {
//
//
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean(Config.SESSION_EXPIRE, true);
//                    editor.apply();
//
////                    databaseReference.removeEventListener(this);
//                    sessionExpireDialog.showDialog();
//                    isDialogShowing = true;
//                }
//            }
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mRealm = Realm.getDefaultInstance();
        //  mDatabase = FirebaseDatabase.getInstance();



        BaseClass.mBaseClass.setPorfileChangeListener(this);
        mContext = HomeActivity.this;
//        startService(new Intent(this, LocationUpdaterService.class));
        intentService = new Intent(this, FirebaseListenerService.class);
        intentService.putExtra(Config.USER_ID, sp.getString(Config.USER_ID, ""));
        if (isMyServiceRunning(FirebaseListenerService.class)) {
            stopService(intentService);
        }
        initUI();
    }


    private void initUI() {
        homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_home_fragment_container, homeFragment, "HomeFragment");
        fragmentTransaction.commit();
//      permissionCheck();

        if (sp.getBoolean(Config.SESSION_EXPIRE, false)) {
            if (!isDialogShowing) {
//                sessionExpireDialog = new SessionExpireDialog(HomeActivity.this);
                sessionExpireDialog = new SessionExpireDialog(HomeActivity.this);
                sessionExpireDialog.showDialog();
                isDialogShowing = true;
            }
        } else {

            BaseClass.mBaseClass.checkLogin();

            // checkLogin();
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
                    homeFragment.getContacts();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.READ_CONTACTS)) {

                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                        homeFragment.permissionCheck();
                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.READ_CONTACTS)) {
                    snackView();
                }
                return;
            }
            case REQUEST_LOCATION_PERMISSIONS: {

                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (!isMyServiceRunning(LocationUpdaterService.class)) {
                        mContext.startService(new Intent(mContext, LocationUpdaterService.class));
                    }
                    mLocationPermission = true;

                } else if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                        homeFragment.locationPermissionCheck();
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
                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" +mContext.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        mContext.startActivity(intent);
                    }
                }).show();
    }


    public void snackView() {

        Snackbar.make(findViewById(android.R.id.content), "Enable Contacts Permissions from settings",
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseListeners.getListenerClass(this).removeProfileListener();
        if (!sp.getString(Config.USER_ID, "").equals(""))
            startService(intentService);
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

    @Override
    public void onProfileChange(boolean profileChanged) {
        Log.d("dialogue","profile changed");
        if (profileChanged) {

            if (!isDialogShowing) {
                Log.d("dialogue","profile changed dial");
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(Config.SESSION_EXPIRE, true);
                editor.apply();

                sessionExpireDialog = new SessionExpireDialog(HomeActivity.this);
                sessionExpireDialog.showDialog();
                isDialogShowing=true;


            }
        }
    }
}
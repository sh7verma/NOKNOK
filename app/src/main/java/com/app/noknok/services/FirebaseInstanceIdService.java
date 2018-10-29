package com.app.noknok.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.app.noknok.definitions.Config;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.zzd;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dev on 20/6/17.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "FirebaseNotification";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.putString(Config.DEVICE_TOKEN,refreshedToken);
        editor.apply();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }
}

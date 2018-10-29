package com.app.noknok.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dev on 25/7/17.
 */

public class LocationUpdaterService extends Service {


    public static final String TAG = "LOCUPDATETAG";

    public static final int TWO_MINUTES = 120000; // 120 seconds
    public static Boolean isRunning = false;
    public LocationManager mLocationManager;
    public LocationUpdaterListener mLocationListener;
    public Location previousBestLocation = null;
    SharedPreferences sp;
    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) {
                startListening();
            }
            mHandler.postDelayed(mHandlerTask, TWO_MINUTES);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationUpdaterListener();
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListening();
        mHandler.removeCallbacks(mHandlerTask);
        super.onDestroy();
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        isRunning = true;
    }

    private void stopListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        isRunning = false;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void updateLocation(final String latitude, final String longitude) {

        if ((new ConnectionDetector(this).isConnectingToInternet())) {

            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.update_user_location(sp.getString(Config.ACCESS_TOKEN, ""), latitude, longitude, new Callback<RetroHelper.UpdateLocation>() {
                @Override
                public void success(RetroHelper.UpdateLocation updateLocation, Response response) {

                    if (updateLocation.response!= null) {

                        Log.d(TAG, "location has been updated: " + updateLocation.response);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Config.LONGITUDE, longitude);
                        editor.putString(Config.LATITUDE, latitude);
                        editor.apply();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "location update failed" + error.getMessage());

                }
            });


        }
    }

    public class LocationUpdaterListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, previousBestLocation)) {
                previousBestLocation = location;
                try {

                    String defaultLongitude = sp.getString(Config.LONGITUDE, "");
                    String defaultLatitude = sp.getString(Config.LATITUDE, "");

                    String longitude = String.valueOf(previousBestLocation.getLongitude());
                    String latitude = String.valueOf(previousBestLocation.getLatitude());


                    Log.d(TAG, "latitude after change: " + latitude);
                    Log.d(TAG, "latitude before change: " + defaultLatitude);

                    if (!longitude.equals(defaultLongitude)
                            && !latitude.equals(defaultLatitude)) {

                        if(sp.getBoolean(Config.LOGGED_IN,false)) {
                            updateLocation(latitude, longitude);
                        }else{
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Config.LONGITUDE, longitude);
                            editor.putString(Config.LATITUDE, latitude);
                            editor.apply();
                        }

                    }

                    // Script to post location data to server..
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stopListening();
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            stopListening();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
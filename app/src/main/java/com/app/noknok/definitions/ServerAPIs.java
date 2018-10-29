package com.app.noknok.definitions;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by dev on 20/6/17.
 */

public class ServerAPIs {

    public static final String BASE_URL = "http://13.228.223.105:3030";
    public static final String REGISTER_NUMBER = "/users/create_profile";
    public static final String CALL_TO_VERIFY = "/users/call";
    public static final String VERIFY_NUMBER = "/users/verify_number";
    public static final String GET_FRIENDS_LIST = "/users/get_friend_list";
    public static final String GET_USER_DETAIL = "/users/user_detail";
    public static final String GET_MUTUAL_FRIENDS="/users/mutual_friends";
    public static final String UPDATE_LOCATION = "/users/update_location";
    public static final String GET_CONTACTS_FOR_CONFESSION = "/posts/get_contacts";
    public static final String UPDATE_RIGHT_GUESS="/users/update_right_guess";
    public static final String REST_BADGE_COUNT = "/push/reset_badge_count";
    public static final String CONFESSION_PUSH = "/push/confession_push";




    public static void noInternetConnection(View view) {
        Snackbar snackbar = Snackbar.make(view, "Internet connection not available", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }


    public static void noInternetConnectionIndefinite(View view) {
        Snackbar snackbar = Snackbar.make(view, "Internet connection not available", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public static void showServerErrorSnackbar(View view, String message) {

        Snackbar snackbar = Snackbar.make(view, "Server Problem!", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public static void showReloadErrorSnackbar(View view, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }
    public static void showOtpErrorSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.RED);
        snackbar.show();
    }
    public static void showSuccessSnackbar(View view, String message, int color) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }
}


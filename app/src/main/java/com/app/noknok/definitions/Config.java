package com.app.noknok.definitions;

import java.util.ArrayList;

/**
 * Created by dev on 21/6/17.
 */

public class Config {
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String FULL_NAME = "fullname";
    public static final String COUNTRY_CODE = "country_code";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String USER_ID = "user_id";
    public static final String LOGGED_IN = "logged_in";
    public static final String RIGHT_GUESS_COUNT = "right_guess_count";
    public static final String CHAT_DIALOGUE_ID = "chat_dialogue_id";
    public static final String LAST_MESSAGE_TIME = "last_message_time";
    public static final String LAST_MESSAGE_ID = "last_message_id";
    public static String MYCOUNTRYCODE = "my_country_code";




    public static final String FIRSTLOGIN = "firstlogin";
    public static final String SESSION_EXPIRE = "session_expire";

    public static final String MESSAGE_SEND = "message_send";
    public static final String MESSAGE_REPLY = "message_reply";
    public static final String CONFESSIONS = "confessions";
    public static final String THISISFRIEND = "1";
    public static final String CONFESSIONS_LIST = "confessions_list";
    public static final String CURRENT_POSITION = "current_position";
    public static final String LAST_MESSAGE_GUESS_STATUS = "last_guess_status";

    public static final String MY_CONFESSIONS = "my_confessions";
    public static final String OTHERS_CONFESSIONS = "others_confessions";
    public static final String FRIENDS_CONFESSIONS = "friends_confessions";
    public static final String CONFESSION_TYPE = "confession_type";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String RECEIVER_NUMBER = "receiver_number";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String NOTIFICATION_TYPE = "notification_type";

    public static final String MESSAGE_TYPE_RECEIVED = "message_type_received";
    public static final String MESSAGE_TYPE_SENT = "message_type_sent";
    public static final String MESSAGE_TYPE_GUESSED = "message_type_GUESSED";
    public static final String MESSAGE_ICON = "message_icon";
    public static final String MESSAGE_COLOR = "message_color";
    public static final String LAST_GUESS_TIME = "last_guess_time";
    public static final String MESSAGE = "message";
    public static final String FRIENDSDETAILS = "friends_details";
    public static final String SELECTED_COUNTER = "selected_counter";
    public static final String BADGE = "badge";
    public static final String MESSAGE_ID = "message_id";
    public static final String COMING_FROM = "coming_from";
    public static final String OLD_MESSAGE_COUNT = "old_message_count";
    public static final String NEW_MESSAGE_COUNT = "new_message_count";
    public static final String DELETED_CONFESSION ="deleted_confession" ;
    public static final String NEW_CONFESSION_COUNT ="deleted_confession_count" ;
    public static final String NOTIFICATION_ON = "notification_on";


    public static final ArrayList<String> GETCOLOR() {
        ArrayList<String> colorArray = new ArrayList<>();
        colorArray.add("#ed5565");
        colorArray.add("#fc6e51");
        colorArray.add("#ffce54");
        colorArray.add("#a0d468");
        colorArray.add("#48cfad");
        colorArray.add("#4fc1e9");
        colorArray.add("#5d9cec");
        colorArray.add("#ac92ec");
        colorArray.add("#ec87c0");
        colorArray.add("#ccd1d9");
        colorArray.add("#656d78");

        return colorArray;
    }
}


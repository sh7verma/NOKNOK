package com.app.noknok.models;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit.client.Response;

/**
 * Created by dev on 20/6/17.
 */

public class RetroHelper {

    public static class RegisterNumber {
        public Response response;
        public String error;
    }

    public static class VerifyAccount {
        public Response response;
        public String error;

        public class Response {
            public String user_id;
            public String fullname;
            public String phone_number;
            public String country_code;
            public String access_token;
        }
    }

    public static class GetFriendsList {
        public ArrayList<Response> response;
        public String error;

        public class Response implements Serializable {
            public String user_id, number, name_in_contact, name_on_app, name_guessed, right_guess;

        }
    }

    public static class GetUserDetail {
        public Response response;
        public String error;

        public class Response {
            public String friends_count;
            public String right_guesses_count;
            public String new_messages_count;
            public String confession_count;
        }
    }

    public static class UpdateRightGuess {

        public Response response;
        public String error;

    }

    public static class GetMutualFriends {
        public ArrayList<Response> response;
        public String error;

        public class Response {

            public String user_id;
            public String access_token;
            public String phone_number;
            public String fullname;
            public String device_token;
            public String name_in_contact;
            public String country_code;

        }
    }


    public static class UpdateLocation {
        public String response;
        public String error;

    }


    public static class GetConfessionContacts {

        public ArrayList<Response> response;
        public String error;

        public class Response implements Serializable {
            public String user_id, fullname, phone_number, country_code, access_token, device_token, platform_status, latitude, longitude, profile_status, friend;
        }

    }


    public static class ResetBadgeCount  {

        public Response response;
        public String error;

    }

    public static class ConfessionPush {

        public Response response;
        public String error;

    }
}

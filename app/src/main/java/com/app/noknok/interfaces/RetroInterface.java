package com.app.noknok.interfaces;

import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.models.RetroHelper;

import kotlin.jvm.JvmMultifileClass;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by dev on 20/6/17.
 */

public interface RetroInterface {

    @FormUrlEncoded
    @POST(ServerAPIs.REGISTER_NUMBER)
    void register_number(@Field("fullname") String fullname, @Field("country_code") String country_code, @Field("phone_number") String phone_number,
                         Callback<RetroHelper.RegisterNumber> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.VERIFY_NUMBER)
    void verify_number(@Field("country_code") String country_code, @Field("phone_number") String phone_number,
                       @Field("otp") String otp_code, @Field("platform_status") String platform_status,
                       @Field("latitude") String latitude, @Field("longitude") String longitude, @Field("contacts") String contacts,
                       @Field("device_token") String device_token, Callback<RetroHelper.VerifyAccount> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.CALL_TO_VERIFY)
    void call_to_verify(@Field("fullname") String fullname, @Field("country_code") String country_code, @Field("phone_number") String phone_number,
                    Callback<RetroHelper.RegisterNumber> callback);


    @FormUrlEncoded
    @POST(ServerAPIs.GET_FRIENDS_LIST)
    void get_friends_list(@Field("access_token") String access_token, @Field("contacts") String contacts,
                        Callback<RetroHelper.GetFriendsList> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.GET_USER_DETAIL)
    void get_user_detail(@Field("access_token") String access_token, @Field("contacts") String contacts, Callback<RetroHelper.GetUserDetail> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.GET_MUTUAL_FRIENDS)
    void get_mutual_friends(@Field("access_token") String access_token,  Callback<RetroHelper.GetMutualFriends> callback);


    @FormUrlEncoded
    @POST(ServerAPIs.UPDATE_LOCATION)
    void update_user_location(@Field("access_token") String access_token, @Field("latitude") String latitude,
                              @Field("longitude") String longitude, Callback<RetroHelper.UpdateLocation> callback);


    @FormUrlEncoded
    @POST(ServerAPIs.GET_CONTACTS_FOR_CONFESSION)
    void get_contact_for_confession(@Field("access_token") String access_token,  Callback<RetroHelper.GetConfessionContacts> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.UPDATE_RIGHT_GUESS)
    void update_right_guess(@Field("access_token") String access_token, Callback<RetroHelper.UpdateRightGuess> callback);


    @FormUrlEncoded
    @POST(ServerAPIs.REST_BADGE_COUNT)
    void reset_badge_count(@Field("access_token") String access_token, Callback<RetroHelper.ResetBadgeCount> callback);

    @FormUrlEncoded
    @POST(ServerAPIs.CONFESSION_PUSH)
    void confession_push(@Field("receiver_ids") String receiver_ids, @Field("audio_url") String audio_url, @Field("sender_id") int sender_id,
                         @Field("message_id") String message_id, @Field("user_type") String user_type, Callback<RetroHelper.ConfessionPush> callback);

}
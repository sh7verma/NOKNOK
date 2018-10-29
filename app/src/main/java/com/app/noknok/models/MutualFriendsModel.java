package com.app.noknok.models;

/**
 * Created by dev on 29/6/17.
 */

public class MutualFriendsModel {


    public String user_id;
    public String access_token;
    public String phone_number;
    public String fullname;
    public String name_in_contact;
    public String country_code;
    public String device_token; //push token for android

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getName_in_contact() {
        return name_in_contact;
    }

    public void setName_in_contact(String name_in_contact) {
        this.name_in_contact = name_in_contact;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }






}

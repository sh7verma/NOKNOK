package com.app.noknok.models;

import java.util.HashMap;


/**
 * Created by dev on 26/6/17.
 */

public class Profile {

    private String name;
    private String country_code;
    private String number;
    private String phone_number;
    private String online_status;
    private String account_type;
    private HashMap<String, String> chat_dialogue_id;
    private String access_token;
    private String push_token;
    private String user_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public HashMap<String, String> getChat_dialogue_id() {
        return chat_dialogue_id;
    }

    public void setChat_dialogue_id(HashMap<String, String> chat_dialogue_id) {
        this.chat_dialogue_id = chat_dialogue_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getPush_token() {
        return push_token;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

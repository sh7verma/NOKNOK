package com.app.noknok.models;

import io.realm.RealmObject;

/**
 * Created by dev on 27/7/17.
 */

public class FriendsModelRealm extends RealmObject {

    public  String user_id,number,name_in_contact,name_on_app,name_guessed,right_guess;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number;
    }

    public String getName_in_contact() {
        return name_in_contact;
    }

    public void setName_in_contact(String name_in_contact) {
        this.name_in_contact = name_in_contact;
    }

    public String getName_on_app() {
        return name_on_app;
    }

    public void setName_on_app(String name_on_app) {
        this.name_on_app = name_on_app;
    }

    public String getName_guessed() {
        return name_guessed;
    }

    public void setName_guessed(String name_guessed) {
        this.name_guessed = name_guessed;
    }

    public String getRight_guess() {
        return right_guess;
    }

    public void setRight_guess(String right_guess) {
        this.right_guess = right_guess;
    }
}

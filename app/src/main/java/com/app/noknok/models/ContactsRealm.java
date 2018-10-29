package com.app.noknok.models;

import io.realm.RealmObject;

/**
 * Created by dev on 26/7/17.
 */

public class ContactsRealm extends RealmObject {

    String name, number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

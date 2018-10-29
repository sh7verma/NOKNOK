package com.app.noknok.models;

import java.util.HashMap;

/**
 * Created by dev on 11/7/17.
 */

public class Chat {


    String chat_dialogue_id, last_message_time, last_message_id;
    HashMap<String, String> access_tokens, push_tokens, contact_numbers, delete_dialogue_time,
                            anonymous_ids, direct_ids, guess_ids, last_guess_time, unread_map;

    public HashMap<String, String> getUnread_map() {
        return unread_map;
    }

    public void setUnread_map(HashMap<String, String> unread_map) {
        this.unread_map = unread_map;
    }

    public HashMap<String, String> getLast_guess_time() {
        return last_guess_time;
    }

    public void setLast_guess_time(HashMap<String, String> last_guess_time) {
        this.last_guess_time = last_guess_time;
    }

    public String getChat_dialogue_id() {
        return chat_dialogue_id;
    }

    public void setChat_dialogue_id(String chat_dialogue_id) {
        this.chat_dialogue_id = chat_dialogue_id;
    }

    public String getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(String last_message_time) {
        this.last_message_time = last_message_time;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }

    public HashMap<String, String> getAccess_tokens() {
        return access_tokens;
    }

    public void setAccess_tokens(HashMap<String, String> access_tokens) {
        this.access_tokens = access_tokens;
    }

    public HashMap<String, String> getPush_tokens() {
        return push_tokens;
    }

    public void setPush_tokens(HashMap<String, String> push_tokens) {
        this.push_tokens = push_tokens;
    }

    public HashMap<String, String> getContact_numbers() {
        return contact_numbers;
    }

    public void setContact_numbers(HashMap<String, String> contact_numbers) {
        this.contact_numbers = contact_numbers;
    }

    public HashMap<String, String> getDelete_dialogue_time() {
        return delete_dialogue_time;
    }

    public void setDelete_dialogue_time(HashMap<String, String> delete_dialogue_time) {
        this.delete_dialogue_time = delete_dialogue_time;
    }

    public HashMap<String, String> getAnonymous_ids() {
        return anonymous_ids;
    }

    public void setAnonymous_ids(HashMap<String, String> anonymous_ids) {
        this.anonymous_ids = anonymous_ids;
    }

    public HashMap<String, String> getDirect_ids() {
        return direct_ids;
    }

    public void setDirect_ids(HashMap<String, String> direct_ids) {
        this.direct_ids = direct_ids;
    }

    public HashMap<String, String> getGuess_ids() {
        return guess_ids;
    }

    public void setGuess_ids(HashMap<String, String> guess_ids) {
        this.guess_ids = guess_ids;
    }
}

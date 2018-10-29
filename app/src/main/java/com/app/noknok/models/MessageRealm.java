package com.app.noknok.models;

import io.realm.RealmObject;

/**
 * Created by dev on 18/7/17.
 */

public class MessageRealm extends RealmObject {

    private String chat_dialogue_id, audio_length, message_time, audio_url, audio_pitch, audio_rate, sender_id,
            message_id, guess_status, sender_number, sender_appname, local_url = "", message_theme, confession_type;

    private int message_icon, message_color;

    private boolean user_type, message_type, message_read;


    public int getMessage_color() {
        return message_color;
    }

    public void setMessage_color(int message_color) {
        this.message_color = message_color;
    }

    public String getConfession_type() {
        return confession_type;
    }

    public void setConfession_type(String confession_type) {
        this.confession_type = confession_type;
    }

    public int getMessage_icon() {
        return message_icon;
    }

    public void setMessage_icon(int message_icon) {
        this.message_icon = message_icon;
    }

    public String getMessage_theme() {
        return message_theme;
    }

    public void setMessage_theme(String message_theme) {
        this.message_theme = message_theme;
    }

    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        this.local_url = local_url;
    }

    public boolean isMessage_read() {
        return message_read;
    }

    public void setMessage_read(boolean message_read) {
        this.message_read = message_read;
    }

    public String getChat_dialogue_id() {
        return chat_dialogue_id;
    }

    public void setChat_dialogue_id(String chat_dialogue_id) {
        this.chat_dialogue_id = chat_dialogue_id;
    }

    public String getAudio_length() {
        return audio_length;
    }

    public void setAudio_length(String audio_length) {
        this.audio_length = audio_length;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getAudio_pitch() {
        return audio_pitch;
    }

    public void setAudio_pitch(String audio_pitch) {
        this.audio_pitch = audio_pitch;
    }

    public String getAudio_rate() {
        return audio_rate;
    }

    public void setAudio_rate(String audio_rate) {
        this.audio_rate = audio_rate;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getGuess_status() {
        return guess_status;
    }

    public void setGuess_status(String guess_status) {
        this.guess_status = guess_status;
    }

    public String getSender_number() {
        return sender_number;
    }

    public void setSender_number(String sender_number) {
        this.sender_number = sender_number;
    }

    public String getSender_appname() {
        return sender_appname;
    }

    public void setSender_appname(String sender_appname) {
        this.sender_appname = sender_appname;
    }


    public boolean isUser_type() {
        return user_type;
    }

    public void setUser_type(boolean user_type) {
        this.user_type = user_type;
    }

    public boolean isMessage_type() {
        return message_type;
    }

    public void setMessage_type(boolean message_type) {
        this.message_type = message_type;
    }


    @Override
    public String toString() {

        System.out.println("TO String realm ::: " + chat_dialogue_id + " " + audio_length + " " + message_time + " " + audio_url + " " + audio_pitch + " " + audio_rate + " " + sender_id + " " +
                message_id + " " + guess_status + " " + sender_number + " " + sender_appname + " " + local_url + " " + message_theme + " " + confession_type + " " + message_icon + " " + message_color + " " + user_type + " " + message_type + " " + message_read);
        return super.toString();
    }
}

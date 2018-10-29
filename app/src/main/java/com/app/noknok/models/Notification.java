package com.app.noknok.models;

/**
 * Created by dev on 30/8/17.
 */

public class Notification {

    String receiver_id, audio_url, dialogue_id, sender_id, notification_id, message_id;


    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getDialogue_id() {
        return dialogue_id;
    }

    public void setDialogue_id(String dialogue_id) {
        this.dialogue_id = dialogue_id;
    }
}

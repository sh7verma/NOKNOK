package com.app.noknok.models;

import io.realm.RealmObject;

/**
 * Created by dev on 17/7/17.
 */

public class ChatRealm extends RealmObject {
    private String chatDialogueId, randomName, lastMessageId, lastMessageTime, guessId, receiverName, receiverId,
                    lastGuessTime;
    private int messageIcon, messageColor, myMessageColor, myMessageIcon;

    public int getMyMessageColor() {
        return myMessageColor;
    }

    public void setMyMessageColor(int myMessageColor) {
        this.myMessageColor = myMessageColor;
    }

    public int getMyMessageIcon() {
        return myMessageIcon;
    }

    public void setMyMessageIcon(int myMessageIcon) {
        this.myMessageIcon = myMessageIcon;
    }

    public String getLastGuessTime() {
        return lastGuessTime;
    }

    public void setLastGuessTime(String lastGuessTime) {
        this.lastGuessTime = lastGuessTime;
    }

    public int getMessageIcon() {
        return messageIcon;
    }

    public void setMessageIcon(int messageIcon) {
        this.messageIcon = messageIcon;
    }

    public int getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    public String getGuessId() {
        return guessId;
    }

    public void setGuessId(String guessId) {
        this.guessId = guessId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatDialogueId() {
        return chatDialogueId;
    }

    public void setChatDialogueId(String chatDialogueId) {
        this.chatDialogueId = chatDialogueId;
    }

    public String getRandomName() {
        return randomName;
    }

    public void setRandomName(String randomName) {
        this.randomName = randomName;
    }
}

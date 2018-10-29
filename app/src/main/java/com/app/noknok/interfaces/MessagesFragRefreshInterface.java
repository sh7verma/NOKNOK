package com.app.noknok.interfaces;

import com.app.noknok.models.Message;

/**
 * Created by dev on 17/8/17.
 */
public interface MessagesFragRefreshInterface {
    void addMessage(Message message);
    void onMessageChange(Message message);

}

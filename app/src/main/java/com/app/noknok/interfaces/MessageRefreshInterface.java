package com.app.noknok.interfaces;

import com.app.noknok.models.Message;

/**
 * Created by dev on 22/8/17.
 */

public interface MessageRefreshInterface {
    void addMessage();
    void onMessageChange(Message message);

}

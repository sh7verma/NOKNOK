package com.app.noknok.interfaces;

import com.app.noknok.models.Message;

/**
 * Created by dev on 8/8/17.
 */
public interface ConfesFragmentRefreshInterface {
    void removeMessage(Message message, String confessionType);
    void addMessage(Message message, String confessionType);
    void removedMessage(String messageId, String confessionType);
}

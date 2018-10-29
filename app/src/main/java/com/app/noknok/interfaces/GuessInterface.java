package com.app.noknok.interfaces;

import com.app.noknok.models.FriendsModel;

/**
 * Created by dev on 28/8/17.
 */
public interface GuessInterface {
    void checkedItem(FriendsModel friendsModel);
    void unCheckedItem(FriendsModel friendsModel);
}

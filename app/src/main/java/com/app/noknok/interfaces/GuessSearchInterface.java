package com.app.noknok.interfaces;

import com.app.noknok.models.FriendsModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dev on 28/8/17.
 */
public interface GuessSearchInterface {
    void selectedFriends(ArrayList<FriendsModel> selectedFriendsList, int selectedCounter, HashMap<String, Boolean> checkMap);
}

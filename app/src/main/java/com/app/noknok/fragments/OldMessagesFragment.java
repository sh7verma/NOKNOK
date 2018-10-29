package com.app.noknok.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.noknok.R;
import com.app.noknok.activities.MessageActivity;
import com.app.noknok.adapters.CardPagerAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.ChatDialogueInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.app.noknok.fragments.NewMessagesFragment.fixRandomName;

/**
 * Created by dev on 17/7/17.
 */

public class OldMessagesFragment extends BaseFragment implements MessagesFragRefreshInterface, ChatDialogueInterface {

    public static Activity mcActivity;

    View mView;
    ViewPager mViewPager;
    CardPagerAdapter cardPagerAdapter;
    public static ArrayList<HashMap<String, String>> chatDialogueList = new ArrayList<>();
    Realm mRealm;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mChatReference;

    LinearLayout llNoResult;

    //MessageSearchInterface messageSearchInterface = new MessageSearchActivity();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_old_messages, container, false);
        mRealm = Realm.getDefaultInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mChatReference = mFirebaseDatabase.getReference("Chats").getRef();

        mcActivity = getActivity();
        MessageActivity.mMessageActivity.setOldMessageRefreshListener(this);
        MessageActivity.mMessageActivity.setChatDialogueRefreshListener(this);

        initUI();

        return mView;
    }

    private void initUI() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);

        cardPagerAdapter = new CardPagerAdapter(getActivity(), chatDialogueList, false);

        mViewPager.setAdapter(cardPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        llNoResult = (LinearLayout) mView.findViewById(R.id.ll_no_result);

        if (chatDialogueList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

        getChatDialogues();

    }

    @Override
    public void onResume() {
        super.onResume();
        cardPagerAdapter.notifyDataSetChanged();
        if (chatDialogueList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

    }

    private void getChatDialogues() {

        if (chatDialogueList != null)
            chatDialogueList.clear();

        final RealmResults<ChatRealm> realmResultsGuessZero = mRealm.where(ChatRealm.class).findAll();
        for (ChatRealm chatRealm : realmResultsGuessZero.sort("lastMessageTime", Sort.DESCENDING)) {

            if (chatRealm.getGuessId().equals("1")) {
                RealmResults<MessageRealm> realmResultsGuessZeroReadZero = mRealm.where(MessageRealm.class)
                        .equalTo("message_type", false)
                        .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);

                if (realmResultsGuessZeroReadZero.size() > 0) {
                    HashMap<String, String> chatMap = new HashMap<>();
                    chatMap.put(Config.FULL_NAME, chatRealm.getReceiverName());
                    chatMap.put(Config.LAST_MESSAGE_ID, chatRealm.getLastMessageId());
                    chatMap.put(Config.LAST_MESSAGE_TIME, chatRealm.getLastMessageTime());
                    chatMap.put(Config.CHAT_DIALOGUE_ID, chatRealm.getChatDialogueId());
                    chatMap.put(Config.RECEIVER_ID, chatRealm.getReceiverId());
                    chatMap.put(Config.MESSAGE_TYPE, Config.MESSAGE_TYPE_GUESSED);
                    chatMap.put(Config.LAST_GUESS_TIME, chatRealm.getLastGuessTime());
                    chatMap.put(Config.MESSAGE_COLOR, String.valueOf(chatRealm.getMessageColor()));
                    chatMap.put(Config.MESSAGE_ICON, String.valueOf(chatRealm.getMessageIcon()));
                    chatDialogueList.add(chatMap);
             //       cardPagerAdapter.notifyDataSetChanged();
                }
            } else if (chatRealm.getGuessId().equals("0")) {

                RealmResults<MessageRealm> realmResultsGuessZeroReadZero = mRealm.where(MessageRealm.class)
                        .equalTo("guess_status", "0").equalTo("message_type", false)
                        .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);

                RealmResults<MessageRealm> realmResultsGuessOneReadZero = mRealm.where(MessageRealm.class)
                        .equalTo("guess_status", "1").equalTo("message_type", false)
                        .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);


//                if (Long.parseLong(realmResultsGuessZeroReadZero.get(0).getMessage_time())
//                        > Long.parseLong(realmResultsGuessOneReadZero.get(0).getMessage_time())) {

                if (realmResultsGuessZeroReadZero.size() > 0) {
                    HashMap<String, String> chatMap = new HashMap<>();
                    chatMap.put(Config.FULL_NAME, fixRandomName(chatRealm.getRandomName()));
                    chatMap.put(Config.LAST_MESSAGE_ID, chatRealm.getLastMessageId());
                    chatMap.put(Config.LAST_MESSAGE_TIME, realmResultsGuessZeroReadZero.get(0).getMessage_time());
                    chatMap.put(Config.CHAT_DIALOGUE_ID, chatRealm.getChatDialogueId());
                    chatMap.put(Config.RECEIVER_ID, chatRealm.getReceiverId());
                    chatMap.put(Config.MESSAGE_TYPE, Config.MESSAGE_TYPE_RECEIVED);
                    chatMap.put(Config.LAST_GUESS_TIME, chatRealm.getLastGuessTime());
                    chatMap.put(Config.MESSAGE_COLOR, String.valueOf(chatRealm.getMessageColor()));
                    chatMap.put(Config.MESSAGE_ICON, String.valueOf(chatRealm.getMessageIcon()));
                    chatDialogueList.add(chatMap);
                 //   cardPagerAdapter.notifyDataSetChanged();
                }
//                } else {
                if (realmResultsGuessOneReadZero.size() > 0) {
                    HashMap<String, String> chatMap = new HashMap<>();
                    chatMap.put(Config.FULL_NAME, chatRealm.getReceiverName());
                    chatMap.put(Config.LAST_MESSAGE_ID, chatRealm.getLastMessageId());
                    chatMap.put(Config.LAST_MESSAGE_TIME, realmResultsGuessOneReadZero.get(0).getMessage_time());
                    chatMap.put(Config.CHAT_DIALOGUE_ID, chatRealm.getChatDialogueId());
                    chatMap.put(Config.RECEIVER_ID, chatRealm.getReceiverId());
                    chatMap.put(Config.MESSAGE_TYPE, Config.MESSAGE_TYPE_SENT);
                    chatMap.put(Config.LAST_GUESS_TIME, chatRealm.getLastGuessTime());
                    chatMap.put(Config.MESSAGE_COLOR, String.valueOf(chatRealm.getMyMessageColor()));
                    chatMap.put(Config.MESSAGE_ICON, String.valueOf(chatRealm.getMyMessageIcon()));
                    chatDialogueList.add(chatMap);
                  //  cardPagerAdapter.notifyDataSetChanged();
                }
//                }
            }

            long[] messTime = new long[chatDialogueList.size()];
            for (int i = 0; i < chatDialogueList.size(); i++) {
                messTime[i] = Long.parseLong(chatDialogueList.get(i).get(Config.LAST_MESSAGE_TIME));
            }
            Arrays.sort(messTime);

            long[] reverseMessTime = new long[messTime.length];
            int k=0;
            for(int i = reverseMessTime.length - 1; i>=0; i--){
                reverseMessTime[k] = messTime[i];
                k++;
            }


            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            for (int j = 0; j < reverseMessTime.length; j++) {
                for (int i = 0; i < chatDialogueList.size(); i++) {
                    if (chatDialogueList.get(i).containsValue(String.valueOf(reverseMessTime[j]))){
                        arrayList.add(chatDialogueList.get(i));
                        break;
                    }
                }
            }

            chatDialogueList.clear();
            chatDialogueList.addAll(arrayList);
            cardPagerAdapter.notifyDataSetChanged();
            if (chatDialogueList.size() > 0) {
                llNoResult.setVisibility(View.GONE);
            } else {
                llNoResult.setVisibility(View.VISIBLE);
            }

//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString(Config.OLD_MESSAGE_COUNT, String.valueOf(chatDialogueList.size()));
//            editor.apply();




        }

        //messageSearchInterface.getChatDialogueList(chatDialogueList);

    }


    @Override
    public void addMessage(Message message) {
        getChatDialogues();
    }

    @Override
    public void onMessageChange(Message message) {

//        RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class)
//                .equalTo("chat_dialogue_id", message.getChat_dialogue_id())
//                .equalTo("message_id", message.getMessage_id()).findAll();
//
//
//
//        String unreadMessageCount = "";
//        mRealm.beginTransaction();
//
//        for (int i = 0; i < results.size(); i++) {
//            MessageRealm messageRealm = results.get(i);
//            messageRealm.setMessage_read(true);
//
//            unreadMessageCount = String.valueOf(mRealm.where(MessageRealm.class).equalTo("chat_dialogue_id",message.getChat_dialogue_id())
//                    .equalTo("message_read",false).count());
//            mChatReference.child(message.getChat_dialogue_id()).child("unread_map").child(sp.getString(Config.USER_ID,"")).setValue(unreadMessageCount);
//        }
//
//        mRealm.commitTransaction();


        //}

        getChatDialogues();

    }

    @Override
    public void onUpdate() {
       // Log.d("CHATDIALOGUEUPDATE", "in oldmess onupdate");
        getChatDialogues();
    }
}
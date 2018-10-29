package com.app.noknok.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.ConfessionsActivity;
import com.app.noknok.adapters.ConfessionsCardPagerAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.ConfesFragmentRefreshInterface;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dev on 26/7/17.
 */

public class FriendsConfessionsFragment extends BaseFragment implements ConfesFragmentRefreshInterface {

    public static Activity mcActivity;
    public static Realm mRealm;
    public static ArrayList<Message> friendsConfessionsList = new ArrayList<>();
    RealmResults<MessageRealm> realmResults;
    View mView;
    ViewPager mViewPager;
    ConfessionsCardPagerAdapter confessionsCardPagerAdapter;
    ArrayList<String> firebaseNameList = new ArrayList<>();
    ArrayList<String> friendsList = new ArrayList<>();
    RealmResults<FriendsModelRealm> friendsRealmsResults;
    RealmResults<MessageRealm> friendsRealmResults;

    LinearLayout llNoResult;
    TextView txtNoResult;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private ConfesFragmentRefreshInterface fragmentRefreshListener;

    public ConfesFragmentRefreshInterface getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(ConfesFragmentRefreshInterface fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_my_confessions, container, false);
        mRealm = Realm.getDefaultInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Messages").getRef();

        mcActivity = getActivity();

        ConfessionsActivity.mConfessionActivity.setListener(this);

        initUI();


        return mView;
    }

    private void initUI() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        confessionsCardPagerAdapter = new ConfessionsCardPagerAdapter(getActivity(), friendsConfessionsList, Config.FRIENDS_CONFESSIONS);
        mViewPager.setAdapter(confessionsCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        txtNoResult = (TextView) mView.findViewById(R.id.txt_no_result);
        txtNoResult.setText(getResources().getString(R.string.no_friends_confessions));
        llNoResult = (LinearLayout) mView.findViewById(R.id.ll_no_confession);

        if (friendsConfessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

        getAllMessages();

//
//        ((ConfessionsActivity) getActivity()).setFragmentRefreshListener(new ConfesFragmentRefreshInterface() {
//
//            @Override
//            public void removeMessage(Message message) {
//
//                Toast.makeText(getActivity(), "mess in friends fragment", Toast.LENGTH_SHORT).show();
//
//                getMessages();
//                //   Log.d(TAG, "my confession removed");
//
////                for (int i = 0; i < friendsConfessionsList.size(); i++) {
////                    if (friendsConfessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
////
////                        friendsConfessionsList.remove(i);
////                        confessionsCardPagerAdapter.notifyDataSetChanged();
////                        break;
////                    }
////                }
//            }
//
//            @Override
//            public void addMessage(Message message) {
//
//                Log.d("CONFESSIONTAG", "friends confession added");
//                Toast.makeText(getActivity(), "mess in friends fragment", Toast.LENGTH_SHORT).show();
//                getMessages();
////                friendsConfessionsList.add(message);
////                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//        });

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
////        mRealm.removeAllChangeListeners();
//
//        //   getData();
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
////        mRealm.removeAllChangeListeners();
////        if (realmResults != null)
////            realmResults.removeAllChangeListeners();
////
////
////        if (friendsRealmsResults != null)
////            friendsRealmsResults.removeAllChangeListeners();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//    }

    private void getAllMessages() {
        if (friendsConfessionsList != null) {
            friendsConfessionsList.clear();
        }

//        realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
//                .equalTo("confession_type", Config.FRIENDS_CONFESSIONS).findAll();

        realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true).findAll();

        ArrayList<Message> tempMessageList = new ArrayList<>();
        HashMap<String, String> friendsMap = friendsUserId();

        for (MessageRealm messageRealm : realmResults.sort("message_time", Sort.DESCENDING)) {
//

            if (friendsMap.containsKey(messageRealm.getSender_id()) && !messageRealm.isUser_type() && !messageRealm.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
                Message message = new Message();
                message.setAudio_length(messageRealm.getAudio_length());
                message.setMessage_time(messageRealm.getMessage_time());
                message.setAudio_url(messageRealm.getAudio_url());
                message.setAudio_pitch(messageRealm.getAudio_pitch());
                message.setAudio_rate(messageRealm.getAudio_rate());
                message.setSender_id(messageRealm.getSender_id());
                message.setMessage_id(messageRealm.getMessage_id());
                // message.setGuess_status(messageRealm.getGuess_status());
                message.setSender_number(messageRealm.getSender_number());
                message.setSender_appname(messageRealm.getSender_appname());
                message.setLocal_url(messageRealm.getLocal_url());
                message.setUser_type(messageRealm.isUser_type());
                message.setMessage_icon(messageRealm.getMessage_icon());
                message.setMessage_color(messageRealm.getMessage_color());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
                message.setMessage_theme(hashMap);
                tempMessageList.add(message);
//            friendsConfessionsList.add(message);

                Log.d("FRIENDSCONFESFRAG", "confession added in FriendsFragment " + message.getSender_appname());
                //  confessionsCardPagerAdapter.notifyDataSetChanged();
            }
        }

        Log.d("FRIENDSCONFESFRAG", "temp count: " + tempMessageList.size());
        //   friendsConfessionsList.clear();
        friendsConfessionsList.addAll(tempMessageList);
        confessionsCardPagerAdapter.notifyDataSetChanged();
        if (friendsConfessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }
        Log.d("FRIENDSCONFESFRAG", "friensconfes count: " + friendsConfessionsList.size());
//        friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAll();
//        if (friendsRealmsResults.size() > 0) {
//
//            String[] friendsArray = new String[friendsRealmsResults.size()];
//
//            int j = 0;
//            for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
//                friendsArray[j] = friendsModelRealm.getUser_id();
//                j++;
//            }
//
//
//            realmResults = mRealm.where(MessageRealm.class)
//                    .equalTo("message_type", true).findAll();
//
//
//            friendsRealmResults = mRealm.where(MessageRealm.class)
//                    .equalTo("message_type", true).in("sender_id", friendsArray).findAll();
//
//            for (MessageRealm messageRealm : friendsRealmResults.sort("message_time", Sort.DESCENDING)) {
//
//                firebaseNameList.add(messageRealm.getMessage_theme());
//
//                Message message = new Message();
//                message.setAudio_length(messageRealm.getAudio_length());
//                message.setMessage_time(messageRealm.getMessage_time());
//                message.setAudio_url(messageRealm.getAudio_url());
//                message.setAudio_pitch(messageRealm.getAudio_pitch());
//                message.setAudio_rate(messageRealm.getAudio_rate());
//                message.setSender_id(messageRealm.getSender_id());
//
//                message.setMessage_id(messageRealm.getMessage_id());
//                message.setGuess_status(messageRealm.getGuess_status());
//                message.setSender_number(messageRealm.getSender_number());
//                message.setSender_appname(messageRealm.getSender_appname());
//                message.setLocal_url(messageRealm.getLocal_url());
//                message.setUser_type(messageRealm.isUser_type());
//                message.setMessageIcon(messageRealm.getMessageIcon());
//
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
//                message.setMessage_theme(hashMap);
//                friendsConfessionsList.add(message);
//            }
//
//            RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
//            ArrayList<String> randomNameList = randomNameGenerator.add();
//
//            randomNameList.removeAll(firebaseNameList);
//
//            for (int i = 0; i < friendsConfessionsList.size(); i++) {
//                final int index = i;
//                final Message message = friendsConfessionsList.get(i);
//                final String userId = sp.getString(Config.USER_ID, "");
//                final String messageId = message.getMessage_id();
//                if (message.getMessage_theme().get(userId).equals("")) {
//                    final String themeName = randomNameGenerator.get(randomNameList);
//                    mDatabaseReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
//                                    .equalTo("message_type", true).equalTo("message_id", messageId).findAll();
//
//                            mRealm.beginTransaction();
//
//                            for (int i = 0; i < msgResults.size(); i++) {
//                                MessageRealm messageRealm = msgResults.get(i);
//                                messageRealm.setMessage_theme(themeName);
//                            }
//
//                            mRealm.commitTransaction();
//                            mRealm.close();
//
//                            HashMap<String, String> hashMap = new HashMap<String, String>();
//                            hashMap.put(userId, themeName);
//                            message.setMessage_theme(hashMap);
//                            friendsConfessionsList.set(index, message);
//                            confessionsCardPagerAdapter.notifyDataSetChanged();
//
//                        }
//                    });
//
//                }
//
//                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//
////            realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
////                @Override
////                public void onChange(RealmResults<MessageRealm> realms) {
////                    if (friendsConfessionsList != null)
////                        friendsConfessionsList.clear();
////
////                    if (firebaseNameList != null)
////                        firebaseNameList.clear();
////
////
////                    String[] friendsArray = new String[friendsRealmsResults.size()];
////
////                    int j = 0;
////                    for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
////                        friendsArray[j] = friendsModelRealm.getUser_id();
////                        j++;
////                    }
////
////                    friendsRealmResults = mRealm.where(MessageRealm.class)
////                            .equalTo("message_type", true).in("sender_id", friendsArray).findAll();
////
////
////                    for (MessageRealm messageRealm : friendsRealmResults.sort("message_time", Sort.DESCENDING)) {
////                        Message message = new Message();
////                        message.setAudio_length(messageRealm.getAudio_length());
////                        message.setMessage_time(messageRealm.getMessage_time());
////                        message.setAudio_url(messageRealm.getAudio_url());
////                        message.setAudio_pitch(messageRealm.getAudio_pitch());
////                        message.setAudio_rate(messageRealm.getAudio_rate());
////                        message.setSender_id(messageRealm.getSender_id());
////                        message.setMessage_id(messageRealm.getMessage_id());
////                        message.setGuess_status(messageRealm.getGuess_status());
////                        message.setSender_number(messageRealm.getSender_number());
////                        message.setSender_appname(messageRealm.getSender_appname());
////                        message.setLocal_url(messageRealm.getLocal_url());
////                        message.setUser_type(messageRealm.isUser_type());
////                        message.setMessageIcon(messageRealm.getMessageIcon());
////
////                        HashMap<String, String> hashMap = new HashMap<>();
////                        hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
////                        message.setMessage_theme(hashMap);
////                        friendsConfessionsList.add(message);
////                    }
////
////
////                    RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
////                    ArrayList<String> randomNameList = randomNameGenerator.add();
////
////                    randomNameList.removeAll(firebaseNameList);
////
////
////                    for (int i = 0; i < friendsConfessionsList.size(); i++) {
////                        final int index = i;
////                        final Message message = friendsConfessionsList.get(i);
////                        final String userId = sp.getString(Config.USER_ID, "");
////                        final String messageId = message.getMessage_id();
////                        if (message.getMessage_theme().get(userId).equals("")) {
////                            final String themeName = randomNameGenerator.get(randomNameList);
////                            mDatabaseReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////
////                                    RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
////                                            .equalTo("message_type", true).equalTo("message_id", messageId).findAll();
////
////                                    mRealm.beginTransaction();
////
////                                    for (int i = 0; i < msgResults.size(); i++) {
////                                        MessageRealm messageRealm = msgResults.get(i);
////                                        messageRealm.setMessage_theme(themeName);
////                                    }
////
////                                    mRealm.commitTransaction();
////                                    mRealm.close();
////
////                                    HashMap<String, String> hashMap = new HashMap<String, String>();
////                                    hashMap.put(userId, themeName);
////                                    message.setMessage_theme(hashMap);
////                                    friendsConfessionsList.set(index, message);
////                                    confessionsCardPagerAdapter.notifyDataSetChanged();
////                                }
////                            });
////
////                        }
////
////
////                        // ((ConfessionsActivity) mcActivity).getFriendsCount();
////                        confessionsCardPagerAdapter.notifyDataSetChanged();
////                    }
////
////                }
////            });
//        }
    }

    void getMessages(String messageId) {

        realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
                .equalTo("confession_type", Config.FRIENDS_CONFESSIONS).equalTo("message_id", messageId).findAll();

        ArrayList<Message> tempMessageList = new ArrayList<>();

        for (MessageRealm messageRealm : realmResults.sort("message_time", Sort.DESCENDING)) {
//

            Message message = new Message();
            message.setAudio_length(messageRealm.getAudio_length());
            message.setMessage_time(messageRealm.getMessage_time());
            message.setAudio_url(messageRealm.getAudio_url());
            message.setAudio_pitch(messageRealm.getAudio_pitch());
            message.setAudio_rate(messageRealm.getAudio_rate());
            message.setSender_id(messageRealm.getSender_id());

            message.setMessage_id(messageRealm.getMessage_id());
            //     message.setGuess_status(messageRealm.getGuess_status());
            message.setSender_number(messageRealm.getSender_number());
            message.setSender_appname(messageRealm.getSender_appname());
            message.setLocal_url(messageRealm.getLocal_url());
            message.setUser_type(messageRealm.isUser_type());
            message.setMessage_icon(messageRealm.getMessage_icon());
            message.setMessage_color(messageRealm.getMessage_color());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
            message.setMessage_theme(hashMap);
            tempMessageList.add(message);
            friendsConfessionsList.add(message);

            Log.d("FRIENDSCONFESFRAG", "confession added in FriendsFragment " + message.getSender_appname());

            confessionsCardPagerAdapter.notifyDataSetChanged();
            if (friendsConfessionsList.size() > 0) {
                llNoResult.setVisibility(View.GONE);
            } else {
                llNoResult.setVisibility(View.VISIBLE);
            }
        }

    }

    HashMap<String, String> friendsUserId() {
        HashMap<String, String> friendsUserIdMap = new HashMap<>();

        RealmResults<FriendsModelRealm> friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAll();
        if (friendsRealmsResults.size() > 0) {
            for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
                friendsUserIdMap.put(friendsModelRealm.getUser_id(), friendsModelRealm.getUser_id());

            }
        }

        return friendsUserIdMap;
    }

    @Override
    public void removeMessage(Message message, String confessionType) {

        getAllMessages();
        //    getMessages(message.getMessage_id());
        //      Toast.makeText(getActivity(), "mess in friends fragment", Toast.LENGTH_SHORT).show();

//        if (friendsConfessionsList.contains(message))
//            friendsConfessionsList.remove(message);
//        confessionsCardPagerAdapter.notifyDataSetChanged();


        //   Log.d(TAG, "my confession removed");

//                for (int i = 0; i < friendsConfessionsList.size(); i++) {
//                    if (friendsConfessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
//
//                        friendsConfessionsList.remove(i);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
    }

    @Override
    public void addMessage(Message message, String confessionType) {

        getAllMessages();
        //  getMessages(message.getMessage_id());
//        friendsConfessionsList.add(message);
//        confessionsCardPagerAdapter.notifyDataSetChanged();

        Log.d("CONFESSIONTAG", "friends confession added");
        //   Toast.makeText(getActivity(), "mess in friends fragment", Toast.LENGTH_SHORT).show();
//                friendsConfessionsList.add(message);
//                confessionsCardPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {
        getAllMessages();
    }
}

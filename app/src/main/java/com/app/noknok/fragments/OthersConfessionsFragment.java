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

public class OthersConfessionsFragment extends BaseFragment implements ConfesFragmentRefreshInterface {

    public static Realm mRealm;
    public static Activity mcActivity;
    public static ArrayList<Message> othersCofessionsList = new ArrayList<>();
    RealmResults<MessageRealm> myConfessionsRealmResults;
    View mView;
    ViewPager mViewPager;
    ConfessionsCardPagerAdapter confessionsCardPagerAdapter;
    RealmResults<FriendsModelRealm> friendsRealmsResults;
    ArrayList<String> firebaseNameList = new ArrayList<>();

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    LinearLayout llNoResult;
    TextView txtNoResult;

    boolean isTaskCompleted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_my_confessions, container, false);
        mRealm = Realm.getDefaultInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Messages").getRef();

        //  mcActivity = getActivity();

        ConfessionsActivity.mConfessionActivity.setListener(this);


        initUI();
        return mView;

    }

    private void initUI() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        confessionsCardPagerAdapter = new ConfessionsCardPagerAdapter(getActivity(), othersCofessionsList, Config.OTHERS_CONFESSIONS);
        mViewPager.setAdapter(confessionsCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        txtNoResult = (TextView) mView.findViewById(R.id.txt_no_result);
        txtNoResult.setText(getResources().getString(R.string.no_other_confessions));
        llNoResult = (LinearLayout) mView.findViewById(R.id.ll_no_confession);

        if (othersCofessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

        getMessages();

        // LoadingDialog.getLoader().showLoader(getActivity());
        //    new MyAsyncTask().execute();
        // getMessages();

//        ((ConfessionsActivity) getActivity()).setFragmentRefreshListener(new ConfesFragmentRefreshInterface() {
//            @Override
//            public void removeMessage(Message message) {
//           //     Log.d(TAG, "my confession removed");
//
//                getMessages();
////                for (int i = 0; i < othersCofessionsList.size(); i++) {
////                    if (othersCofessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
////
////                        othersCofessionsList.remove(i);
////                        confessionsCardPagerAdapter.notifyDataSetChanged();
////                        break;
////                    }
////                }
//            }
//
//            @Override
//            public void addMessage(Message message) {
//                Log.d("CONFESSIONTAG", "others confession added");
//                getMessages();
//
////                othersCofessionsList.add(message);
////                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getMessages() {

        if (othersCofessionsList != null) {
            othersCofessionsList.clear();
        }

//        myConfessionsRealmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
//                .equalTo("confession_type", Config.OTHERS_CONFESSIONS).findAll();

        myConfessionsRealmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true).findAll();

        ArrayList<Message> tempMessageList = new ArrayList<>();
        HashMap<String, String> friendsMap = friendsUserId();

        for (MessageRealm messageRealm : myConfessionsRealmResults.sort("message_time", Sort.DESCENDING)) {
//           if (!friendsMap.containsKey(messageRealm.getSender_id()) && !messageRealm.getSender_id().equals(sp.getString(Config.USER_ID,""))) {
            if ((!friendsMap.containsKey(messageRealm.getSender_id()) || messageRealm.isUser_type()) && !messageRealm.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {

                Message message = new Message();
                message.setAudio_length(messageRealm.getAudio_length());
                message.setMessage_time(messageRealm.getMessage_time());
                message.setAudio_url(messageRealm.getAudio_url());
                message.setAudio_pitch(messageRealm.getAudio_pitch());
                message.setAudio_rate(messageRealm.getAudio_rate());
                message.setSender_id(messageRealm.getSender_id());
                message.setMessage_id(messageRealm.getMessage_id());
                //  message.setGuess_status(messageRealm.getGuess_status());
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

                if (!messageRealm.getMessage_theme().equals(""))
                    Log.d("otherConfession", messageRealm.getMessage_theme());
                else {
                    Log.d("otherempty", messageRealm.getSender_id());
                }

            }
        }

        othersCofessionsList.addAll(tempMessageList);
        confessionsCardPagerAdapter.notifyDataSetChanged();
        if (othersCofessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

//        if (firebaseNameList != null)
//            firebaseNameList.clear();

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//        friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAllAsync();
//        myConfessionsRealmResults = mRealm.where(MessageRealm.class)
//                .equalTo("message_type", true)
//                .notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).findAllAsync();
//        for (MessageRealm messageRealm : myConfessionsRealmResults.sort("message_time", Sort.DESCENDING)) {
//
//
//            firebaseNameList.add(messageRealm.getMessage_theme());
//
//            Message message = new Message();
//            message.setAudio_length(messageRealm.getAudio_length());
//            message.setMessage_time(messageRealm.getMessage_time());
//            message.setAudio_url(messageRealm.getAudio_url());
//            message.setAudio_pitch(messageRealm.getAudio_pitch());
//            message.setAudio_rate(messageRealm.getAudio_rate());
//            message.setSender_id(messageRealm.getSender_id());
//            message.setMessage_id(messageRealm.getMessage_id());
//            message.setGuess_status(messageRealm.getGuess_status());
//            message.setSender_number(messageRealm.getSender_number());
//            message.setSender_appname(messageRealm.getSender_appname());
//            message.setLocal_url(messageRealm.getLocal_url());
//            message.setUser_type(messageRealm.isUser_type());
//            message.setMessageIcon(messageRealm.getMessageIcon());
//
//            HashMap<String, String> hashMap = new HashMap<>();
//            hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
//            message.setMessage_theme(hashMap);
//            othersCofessionsList.add(message);
//        }
//
//        if (friendsRealmsResults != null && friendsRealmsResults.size() > 0) {
//            ArrayList<Message> othersCofessionsList1 = new ArrayList<>();
//            for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
//                for (int i = 0; i < othersCofessionsList.size(); i++) {
//                    if (othersCofessionsList.get(i).getSender_id().equals(friendsModelRealm.user_id)) {
//                        othersCofessionsList1.add(othersCofessionsList.get(i));
//                    }
//                }
//            }
//            othersCofessionsList.removeAll(othersCofessionsList1);
//        }
//
//
//        RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
//        ArrayList<String> randomNameList = randomNameGenerator.add();
//
//        randomNameList.removeAll(firebaseNameList);
//
//        for (int i = 0; i < othersCofessionsList.size(); i++) {
//            final int index = i;
//            final Message message = othersCofessionsList.get(i);
//            final String userId = sp.getString(Config.USER_ID, "");
//            final String messageId = message.getMessage_id();
//            if (message.getMessage_theme().get(userId).equals("")) {
//                final String themeName = randomNameGenerator.get(randomNameList);
//                mDatabaseReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//
//                        RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
//                                .equalTo("message_type", true).equalTo("message_id", messageId).findAllAsync();
//
//                        mRealm.beginTransaction();
//
//                        for (int i = 0; i < msgResults.size(); i++) {
//                            MessageRealm messageRealm = msgResults.get(i);
//                            messageRealm.setMessage_theme(themeName);
//                        }
//
//                        mRealm.commitTransaction();
//
//
//                        HashMap<String, String> hashMap = new HashMap<String, String>();
//                        hashMap.put(userId, themeName);
//                        message.setMessage_theme(hashMap);
//                        othersCofessionsList.set(index, message);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                    }
//                });
//
//            }
//
//            //  isTaskCompleted = true;
//
//            confessionsCardPagerAdapter.notifyDataSetChanged();
//
//        }


//        myConfessionsRealmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//                if (othersCofessionsList != null) {
//                    othersCofessionsList.clear();
//                }
//
//                if (firebaseNameList != null)
//                    firebaseNameList.clear();
//
//                friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAllAsync();
//                myConfessionsRealmResults = mRealm.where(MessageRealm.class)
//                        .equalTo("message_type", true)
//                        .notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).findAllAsync();
//
//                for (MessageRealm messageRealm : myConfessionsRealmResults.sort("message_time", Sort.DESCENDING)) {
//                    Message message = new Message();
//                    message.setAudio_length(messageRealm.getAudio_length());
//                    message.setMessage_time(messageRealm.getMessage_time());
//                    message.setAudio_url(messageRealm.getAudio_url());
//                    message.setAudio_pitch(messageRealm.getAudio_pitch());
//                    message.setAudio_rate(messageRealm.getAudio_rate());
//                    message.setSender_id(messageRealm.getSender_id());
//                    message.setMessage_id(messageRealm.getMessage_id());
//                    message.setGuess_status(messageRealm.getGuess_status());
//                    message.setSender_number(messageRealm.getSender_number());
//                    message.setSender_appname(messageRealm.getSender_appname());
//                    message.setLocal_url(messageRealm.getLocal_url());
//                    message.setUser_type(messageRealm.isUser_type());
//                    message.setMessageIcon(messageRealm.getMessageIcon());
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
//                    message.setMessage_theme(hashMap);
//                    othersCofessionsList.add(message);
//                }
//
//
//                if (friendsRealmsResults != null && friendsRealmsResults.size() > 0) {
//                    ArrayList<Message> othersCofessionsList1 = new ArrayList<>();
//                    for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
//                        for (int i = 0; i < othersCofessionsList.size(); i++) {
//                            if (othersCofessionsList.get(i).getSender_id().equals(friendsModelRealm.user_id)) {
//                                othersCofessionsList1.add(othersCofessionsList.get(i));
//                            }
//                        }
//                    }
//                    othersCofessionsList.removeAll(othersCofessionsList1);
//                }
//
//
//                RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
//                ArrayList<String> randomNameList = randomNameGenerator.add();
//
//                randomNameList.removeAll(firebaseNameList);
//
//
//                for (int i = 0; i < othersCofessionsList.size(); i++) {
//                    final int index = i;
//                    final Message message = othersCofessionsList.get(i);
//                    final String userId = sp.getString(Config.USER_ID, "");
//                    final String messageId = message.getMessage_id();
//                    if (message.getMessage_theme().get(userId).equals("")) {
//                        final String themeName = randomNameGenerator.get(randomNameList);
//                        mDatabaseReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                                RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
//                                        .equalTo("message_type", true).equalTo("message_id", messageId).findAllAsync();
//
//                                mRealm.beginTransaction();
//
//                                for (int i = 0; i < msgResults.size(); i++) {
//                                    MessageRealm messageRealm = msgResults.get(i);
//                                    messageRealm.setMessage_theme(themeName);
//                                }
//
//                                mRealm.commitTransaction();
//
//
//                                HashMap<String, String> hashMap = new HashMap<String, String>();
//                                hashMap.put(userId, themeName);
//                                message.setMessage_theme(hashMap);
//                                othersCofessionsList.set(index, message);
//                                confessionsCardPagerAdapter.notifyDataSetChanged();
//                            }
//                        });
//
//                    }
//
//
//                    //  ((ConfessionsActivity) mcActivity).getMessageCount();
//                    confessionsCardPagerAdapter.notifyDataSetChanged();
//                }
//
//            }
//        });


        // }
//        });


        //  return isTaskCompleted;
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
        //     Log.d(TAG, "my confession removed");

        getMessages();
//                for (int i = 0; i < othersCofessionsList.size(); i++) {
//                    if (othersCofessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
//
//                        othersCofessionsList.remove(i);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
    }

    @Override
    public void addMessage(Message message, String confessionType) {
        Log.d("CONFESSIONTAG", "others confession added");
        getMessages();

//                othersCofessionsList.add(message);
//                confessionsCardPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {
        getMessages();
    }


}

package com.app.noknok.fragments;

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
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by dev on 26/7/17.
 */

public class MyConfessionsFragment extends BaseFragment implements ConfesFragmentRefreshInterface {

    private static final String TAG = "MYCONFESTAG";
    public static ConfessionsCardPagerAdapter confessionsCardPagerAdapter;
    public static ArrayList<Message> myConfessionsList = new ArrayList<>();
    View mView;
    ViewPager mViewPager;
    Realm mRealm;
    LinearLayout llNoResult;
    TextView txtNoResult;

//    private ConfesFragmentRefreshInterface confesFragmentRefreshInterface;
//
//    public ConfesFragmentRefreshInterface getFragmentRefreshListener() {
//        return confesFragmentRefreshInterface;
//    }
//
//    public void setFragmentRefreshListener(
//            ConfesFragmentRefreshInterface confesFragmentRefreshInterface) {
//        this.confesFragmentRefreshInterface = confesFragmentRefreshInterface;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_my_confessions, container, false);
        mRealm = Realm.getDefaultInstance();

        ConfessionsActivity.mConfessionActivity.setListener(this);


        initUI();
        return mView;
    }

    private void initUI() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        confessionsCardPagerAdapter = new ConfessionsCardPagerAdapter(getActivity(), myConfessionsList, Config.MY_CONFESSIONS);
        mViewPager.setAdapter(confessionsCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        txtNoResult = (TextView) mView.findViewById(R.id.txt_no_result);
        txtNoResult.setText(getResources().getString(R.string.no_my_confessions));
        llNoResult = (LinearLayout) mView.findViewById(R.id.ll_no_confession);

        if (myConfessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }

        getMessages();

//        getMessages();


//        ((ConfessionsActivity) getActivity()).setFragmentRefreshListener(new ConfesFragmentRefreshInterface() {
//            @Override
//            public void removeMessage(Message message) {
//                Log.d(TAG, "my confession removed");
//
//                for (int i = 0; i < myConfessionsList.size(); i++) {
//                    if (myConfessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
//
//                        myConfessionsList.remove(i);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void addMessage(Message message) {
//
//                Log.d("CONFESSIONTAG", "my confession added");
//
//                myConfessionsList.add(message);
//                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//        });


        // getMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        //   confessionsCardPagerAdapter.notifyDataSetChanged();
    }

    public void getMessages() {

        if (myConfessionsList != null) {
            myConfessionsList.clear();
        }

        RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
                .equalTo("confession_type", Config.MY_CONFESSIONS).findAll();
        ArrayList<Message> tempMessageList = new ArrayList<>();
        for (MessageRealm messageRealm : realmResults.sort("message_time", Sort.DESCENDING)) {

            Message message = new Message();
            message.setAudio_length(messageRealm.getAudio_length());
            message.setMessage_time(messageRealm.getMessage_time());
            message.setAudio_url(messageRealm.getAudio_url());
            message.setAudio_pitch(messageRealm.getAudio_pitch());
            message.setAudio_rate(messageRealm.getAudio_rate());
            message.setSender_id(messageRealm.getSender_id());
            message.setMessage_id(messageRealm.getMessage_id());
            //   message.setGuess_status(messageRealm.getGuess_status());
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

        }

        myConfessionsList.addAll(tempMessageList);
        if (myConfessionsList.size() > 0) {
            llNoResult.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.VISIBLE);
        }
        confessionsCardPagerAdapter.notifyDataSetChanged();

//        realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//
//
//                if (myConfessionsList != null) {
//                    myConfessionsList.clear();
//                }
//
//                for (MessageRealm messageRealm : messageRealms.sort("message_time", Sort.DESCENDING)) {
//
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
//
//                    myConfessionsList.add(message);
//                    confessionsCardPagerAdapter.notifyDataSetChanged();
//                }
//
//            }
//        });


//        realmResults.removeChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//
//                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//        });

    }

    @Override
    public void removeMessage(Message message, String confessionType) {
        Log.d(TAG, "my confession removed");

        getMessages();

//        for (int i = 0; i < myConfessionsList.size(); i++) {
//            if (myConfessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
//
//                myConfessionsList.remove(i);
//                confessionsCardPagerAdapter.notifyDataSetChanged();
//                break;
//            }
//        }
    }

    @Override
    public void addMessage(Message message, String confessionType) {

        Log.d("CONFESSIONTAG", "my confession added");
        getMessages();

//        myConfessionsList.add(message);
//        confessionsCardPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {
        getMessages();
    }
}

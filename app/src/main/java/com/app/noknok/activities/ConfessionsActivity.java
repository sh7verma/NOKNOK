package com.app.noknok.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.ViewPagerAdapter;
import com.app.noknok.customviews.CustomViewPager;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.fragments.FriendsConfessionsFragment;
import com.app.noknok.fragments.MyConfessionsFragment;
import com.app.noknok.fragments.OthersConfessionsFragment;
import com.app.noknok.interfaces.ConfesFragmentRefreshInterface;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.utils.RandomIllustrator;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.app.noknok.services.FirebaseNotificationService.cancelNotification;


/**
 * Created by dev on 26/7/17.
 */

public class ConfessionsActivity extends BaseActivity implements View.OnClickListener, ConfesFragmentRefreshInterface {


    private static final String TAG = "CONFESSACTTAG";
    public static ConfessionsActivity mConfessionActivity;
    TextView tvConfessionsTitle, tvMyCount, tvMyText, tvFriendsCount, tvFriendsText,
            tvOthersCount, tvOthersText;
    LinearLayout llConfessionsMy, llConfessionsFriends, llConfessionsOthers, llConfessionsCancel, llConfessionsSearch, llMainContainer;
    Realm mRealm;
    long friendsCount = 0;
    long otherCount = 0;
    long myCount = 0;
    RealmResults<FriendsModelRealm> friendsRealmsResults;
    CustomViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;

    RealmResults<MessageRealm> mMessageRealmRealmResults;

//    HashMap<String, String> friendsIdMap = new HashMap<>();

    //RealmResults<FriendsModelRealm> mFriendsModelRealm;

    SessionExpireDialog sessionExpireDialog;
    ArrayList<String> messageIdList = new ArrayList<>();
    String mUserId;
    //    FirebaseListeners mFirebaseListeners;
    SharedPreferences mSharedPreferences;

    RandomIllustrator randomIllustrator;
    private ConfesFragmentRefreshInterface mMyFragmentRefreshInterface, mOtherRefreshInterface, mFriendsRefreshInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confessions);
        mRealm = Realm.getDefaultInstance();
//        mFirebaseListeners = new FirebaseListeners(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mUserId = mSharedPreferences.getString(Config.USER_ID, "");
        randomIllustrator = new RandomIllustrator();

        BaseClass.mBaseClass.setConfessionsListener(this);
        mConfessionActivity = this;
        cancelNotification(this);

        initUI();
        initListeners();
        getMessageCount();
        //getConfessions();

        String confessionType = getIntent().getStringExtra(Config.CONFESSION_TYPE);
        if (confessionType != null && !confessionType.equals("")) {

            if (confessionType.equals(Config.MY_CONFESSIONS)) {
                myConfessions();
            } else if (confessionType.equals(Config.FRIENDS_CONFESSIONS)) {
                friendsConfessions();
            } else if (confessionType.equals(Config.OTHERS_CONFESSIONS)) {
                othersConfessions();
            }

        } else {

            friendsConfessions();
        }


    }

    private void initListeners() {
        llConfessionsCancel.setOnClickListener(this);
        llConfessionsOthers.setOnClickListener(this);
        llConfessionsFriends.setOnClickListener(this);
        llConfessionsMy.setOnClickListener(this);
    }

    private void initUI() {

        tvConfessionsTitle = (TextView) findViewById(R.id.tv_confessions_title);
        tvMyCount = (TextView) findViewById(R.id.tv_activity_confessions_my_count);
        tvMyText = (TextView) findViewById(R.id.tv_activity_confessions_my_text);
        tvFriendsCount = (TextView) findViewById(R.id.tv_activity_confessions_friends_count);
        tvFriendsText = (TextView) findViewById(R.id.tv_activity_confessions_friends_text);
        tvOthersText = (TextView) findViewById(R.id.tv_activity_confessions_others_text);
        tvOthersCount = (TextView) findViewById(R.id.tv_activity_confessions_others_count);
        llConfessionsCancel = (LinearLayout) findViewById(R.id.ll_confessions_cancel);
        llConfessionsMy = (LinearLayout) findViewById(R.id.ll_activity_confessions_my);
        llConfessionsFriends = (LinearLayout) findViewById(R.id.ll_activity_confessions_friends);
        llConfessionsOthers = (LinearLayout) findViewById(R.id.ll_activity_confessions_others);
//        llConfessionsSearch = (LinearLayout) findViewById(R.id.ll_activity_confessions_search);
        llMainContainer = (LinearLayout) findViewById(R.id.ll_activity_confessions_main_container);
        mViewPager = (CustomViewPager) findViewById(R.id.fl_activity_confessions_fragment_container);


        //   if ((new ConnectionDetector(this).isConnectingToInternet())) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

//        ((Homea) getActivity()).setFragmentRefreshListener(new ConfesFragmentRefreshInterface() {
//            @Override
//            public void removeMessage(Message message) {
//                //     Log.d(TAG, "my confession removed");
//
//                for (int i = 0; i < othersCofessionsList.size(); i++) {
//                    if (othersCofessionsList.get(i).getMessage_id().equals(message.getMessage_id())) {
//
//                        othersCofessionsList.remove(i);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void addMessage(Message message) {
//                othersCofessionsList.add(message);
//                confessionsCardPagerAdapter.notifyDataSetChanged();
//            }
//        });

//        ((HomeActivity)this).setFragmentRefreshListener(new ConfesFragmentRefreshInterface() {
//            @Override
//            public void removeMessage(Message message) {
//                Log.d(TAG,"my confession removed");
//
//                for(int i=0; i<myConfessionsList.size(); i++){
//                    if(myConfessionsList.get(i).getMessage_id().equals(message.getMessage_id())){
//
//                        myConfessionsList.remove(i);
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
//            }
//        });
        //  friendsConfessions();
        // mViewPager.setCurrentItem(1);

        //  }else{
        //       ServerAPIs.noInternetConnection(llMainContainer);
        //   }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_activity_confessions_my:
                // mViewPager.setCurrentItem(0);
                myConfessions();
                break;
            case R.id.ll_activity_confessions_others:
                //  mViewPager.setCurrentItem(1);
                othersConfessions();
                break;
            case R.id.ll_activity_confessions_friends:
                //   mViewPager.setCurrentItem(2);
                friendsConfessions();
                break;
//            case R.id.ll_activity_confessions_search:
//                Toast.makeText(this, "work in progress", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.ll_confessions_cancel:
                onBackPressed();
                break;
        }

    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        super.onBackPressed();

    }

    private void friendsConfessions() {
        changeBackground("FRIENDS");
        mViewPager.setCurrentItem(1);


//        FragmentManager fm = getSupportFragmentManager();
//        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//            fm.popBackStack();
//        }
//
//        FriendsConfessionsFragment friendsConfessionsFragment = new FriendsConfessionsFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_activity_confessions_fragment_container, friendsConfessionsFragment, "FriendsConfessionsFragment");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

    }

    private void othersConfessions() {
        changeBackground("OTHERS");

        mViewPager.setCurrentItem(2);

//        FragmentManager fm = getSupportFragmentManager();
//        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//            fm.popBackStack();
//        }
//
//        OthersConfessionsFragment othersConfessionsFragment = new OthersConfessionsFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_activity_confessions_fragment_container, othersConfessionsFragment, "OthersConfessionsFragment");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

    }

    private void myConfessions() {
        changeBackground("MY");

        mViewPager.setCurrentItem(0);


//        FragmentManager fm = getSupportFragmentManager();
//        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//            fm.popBackStack();
//        }
//
//        MyConfessionsFragment myConfessionsFragment = new MyConfessionsFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_activity_confessions_fragment_container, myConfessionsFragment, "MyConfessionsFragment");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

    }

    void changeBackground(String type) {

        int unClickedBackground = R.drawable.bg_round_corners_dark;
        int clickedBackground = R.drawable.bg_round_corners_solid;
        int blackText = R.color.black;
        int greyText = R.color.darkGrey;

        if (type.equals("MY")) {

            llConfessionsFriends.setBackgroundResource(unClickedBackground);
            llConfessionsOthers.setBackgroundResource(unClickedBackground);
            llConfessionsMy.setBackgroundResource(clickedBackground);

            tvFriendsCount.setTextColor(getResources().getColor(greyText));
            tvOthersCount.setTextColor(getResources().getColor(greyText));
            tvMyCount.setTextColor(getResources().getColor(blackText));

            tvFriendsText.setTextColor(getResources().getColor(greyText));
            tvOthersText.setTextColor(getResources().getColor(greyText));
            tvMyText.setTextColor(getResources().getColor(blackText));

        } else if (type.equals("FRIENDS")) {

            llConfessionsFriends.setBackgroundResource(clickedBackground);
            llConfessionsOthers.setBackgroundResource(unClickedBackground);
            llConfessionsMy.setBackgroundResource(unClickedBackground);

            tvFriendsCount.setTextColor(getResources().getColor(blackText));
            tvOthersCount.setTextColor(getResources().getColor(greyText));
            tvMyCount.setTextColor(getResources().getColor(greyText));

            tvFriendsText.setTextColor(getResources().getColor(blackText));
            tvOthersText.setTextColor(getResources().getColor(greyText));
            tvMyText.setTextColor(getResources().getColor(greyText));

        } else if (type.equals("OTHERS")) {

            llConfessionsFriends.setBackgroundResource(unClickedBackground);
            llConfessionsOthers.setBackgroundResource(clickedBackground);
            llConfessionsMy.setBackgroundResource(unClickedBackground);

            tvFriendsCount.setTextColor(getResources().getColor(greyText));
            tvOthersCount.setTextColor(getResources().getColor(blackText));
            tvMyCount.setTextColor(getResources().getColor(greyText));

            tvFriendsText.setTextColor(getResources().getColor(greyText));
            tvOthersText.setTextColor(getResources().getColor(blackText));
            tvMyText.setTextColor(getResources().getColor(greyText));
        }
    }

    public void getMessageCount() {
        myCount = 0;
        otherCount = 0;
        friendsCount = 0;

        HashMap<String, String> friendsMap = friendsUserId();

        myCount = mRealm.where(MessageRealm.class)
                .equalTo("message_type", true).equalTo("confession_type", Config.MY_CONFESSIONS).count();

//        friendsCount = mRealm.where(MessageRealm.class)
//                .equalTo("message_type", true).equalTo("confession_type", Config.FRIENDS_CONFESSIONS).count();
//
//        otherCount = mRealm.where(MessageRealm.class)
//                .equalTo("message_type", true).equalTo("confession_type", Config.OTHERS_CONFESSIONS).count();


        mMessageRealmRealmResults = mRealm.where(MessageRealm.class)
                .equalTo("message_type", true).findAll();

        tvMyCount.setText(String.valueOf(myCount));

        for (MessageRealm messageRealm : mMessageRealmRealmResults) {
            if (friendsMap.containsKey(messageRealm.getSender_id()) && !messageRealm.isUser_type() && !messageRealm.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
                tvFriendsCount.setText(String.valueOf(++friendsCount));
            } else if ((!friendsMap.containsKey(messageRealm.getSender_id()) || messageRealm.isUser_type()) && !messageRealm.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
                tvOthersCount.setText(String.valueOf(++otherCount));
            }
        }

//        friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAll();
//
//        if (friendsRealmsResults != null && friendsRealmsResults.size() > 0) {
//            for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
//                friendsCount += mRealm.where(MessageRealm.class)
//                        .equalTo("message_type", true).equalTo("sender_id", friendsModelRealm.getUser_id()).count();
//            }
//            tvFriendsCount.setText(String.valueOf(friendsCount));
//        }
//        otherCount = mRealm.where(MessageRealm.class).equalTo("message_type", true).count();
//        tvOthersCount.setText(String.valueOf(otherCount - (myCount + friendsCount)));
        mViewPagerAdapter.notifyDataSetChanged();

//        RealmResults<MessageRealm> newMessageResults = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", true).findAll();
//        newMessageResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//                Log.d(TAG, "new confession received");
//                myCount = 0;
//                friendsCount = 0;
//                otherCount = 0;
////                for (MessageRealm messageRealm : messageRealms) {
////                    otherCount++;
////                }
////                myCount = mRealm.where(MessageRealm.class)
////                        .equalTo("message_type", true).equalTo("sender_id", sp.getString(Config.USER_ID, "")).count();
////
////                RealmResults<FriendsModelRealm> friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAll();
////                for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
////                    friendsCount += mRealm.where(MessageRealm.class)
////                            .equalTo("message_type", true).equalTo("sender_id", friendsModelRealm.getUser_id()).count();
////                }
////                otherCount = otherCount - (myCount + friendsCount);
////                tvFriendsCount.setText(String.valueOf(friendsCount));
////                tvOthersCount.setText(String.valueOf(otherCount));
////                tvMyCount.setText(String.valueOf(myCount));
//                mRealm.removeAllChangeListeners();
//                getMessageCount();
//
//            }
//        });


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

    public void getFriendsCount() {

        myCount = 0;
        otherCount = 0;
        friendsCount = 0;
        Log.d("listner", "friendsCount");
        myCount = mRealm.where(MessageRealm.class)
                .equalTo("message_type", true).equalTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        tvMyCount.setText(String.valueOf(myCount));

        friendsRealmsResults = mRealm.where(FriendsModelRealm.class).findAll();

        if (friendsRealmsResults != null && friendsRealmsResults.size() > 0) {
            for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
                friendsCount += mRealm.where(MessageRealm.class)
                        .equalTo("message_type", true).equalTo("sender_id", friendsModelRealm.getUser_id()).count();
            }
            tvFriendsCount.setText(String.valueOf(friendsCount));
        }
        otherCount = mRealm.where(MessageRealm.class).equalTo("message_type", true).count();
        tvOthersCount.setText(String.valueOf(otherCount - (myCount + friendsCount)));

        mViewPagerAdapter.notifyDataSetChanged();
    }


//    void getConfessions() {
//
//        FirebaseListeners.ConfessionsListenersInterface confessionsListenersInterface = new FirebaseListeners.ConfessionsListenersInterface() {
//            @Override
//            public void onMessageAdd(Message message) {
//                //     Log.d(TAG, "new conefssion received: " + message.getMessage_id());
//
//                if (!checkIfMessageExists(message.getMessage_id())) {
//
//                    messageIdList.add(message.getMessage_id());
//
//                    final Random rand = new Random();
//                    int randInt = rand.nextInt(randomIllustrator.size());
//
//                    mRealm.beginTransaction();
//                    MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
//                    messageRealm.setAudio_length(message.getAudio_length());
//                    messageRealm.setAudio_pitch(message.getAudio_pitch());
//                    messageRealm.setAudio_rate(message.getAudio_rate());
//                    messageRealm.setAudio_url(message.getAudio_url());
//                    messageRealm.setUser_type(message.isUser_type());
//
//                    messageRealm.setMessageIcon(randInt);
//
//                    if (message.isUser_type()) {
//                        messageRealm.setMessage_theme(message.getMessage_theme().get(mUserId));
//                    } else {
//                        messageRealm.setMessage_theme(message.getSender_appname());
//                    }
//
//                    boolean messageRead;
//
//                    if (!message.getRead_ids().containsKey(mUserId)) {
//                        messageRead = false;
//                    } else {
//                        messageRead = true;
//                    }
//                    messageRealm.setMessage_read(messageRead);
//                    messageRealm.setMessage_id(message.getMessage_id());
//                    //  Log.d(TAG, "new message received" + message.getMessage_id());
//                    messageRealm.setSender_id(message.getSender_id());
//                    messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
//                    messageRealm.setSender_appname(message.getSender_appname());
//                    messageRealm.setMessage_time(message.getMessage_time());
//                    messageRealm.setMessage_type(message.isMessage_type());
//                    mRealm.commitTransaction();
//
//                    if(getFragmentRefreshListener()!=null){
//                        getFragmentRefreshListener().removeMessage(message);
//                    }
//                     getMessageCount();
//
//                    if(getFragmentRefreshListener()!=null){
//                        getFragmentRefreshListener().addMessage(message);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onMessageRemove(Message message) {
//
//                if (checkIfMessageExists(message.getMessage_id())) {
//                    mRealm.beginTransaction();
//                    RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", message.getMessage_id()).findAll();
//                    messageRealm.deleteFirstFromRealm();
//                    mRealm.commitTransaction();
//
//                    //  Log.d(TAG, "message remove in confessiona activity from realm");
//
//                       getMessageCount();
//                    if(getFragmentRefreshListener()!=null){
//                        getFragmentRefreshListener().removeMessage(message);
//                    }
//                }
//            }
//        };
//
//
//        mFirebaseListeners.setConfessionsListener(confessionsListenersInterface);
//        mFirebaseListeners.setConfessionListener(sp.getString(Config.USER_ID, ""));
//
//    }

    private boolean checkIfMessageExists(String message_id) {
        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
                .equalTo("message_id", message_id);
        return query.count() != 0;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewPagerAdapter.notifyDataSetChanged();

//        mRealm.removeAllChangeListeners();
        // getMessageCount();
        sessionExpireDialog = new SessionExpireDialog(ConfessionsActivity.this);
    }


    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public void removeMessage(Message message, String confessionType) {

        Log.d(TAG, "confession removed in confession actvity " + message.getSender_appname());

//        if(getFragmentRefreshListener()!=null){
//            getFragmentRefreshListener().removeMessage(message);
//        }

        if (confessionType.equals(Config.MY_CONFESSIONS)) {
            if (mMyFragmentRefreshInterface != null) {
                mMyFragmentRefreshInterface.removeMessage(message, confessionType);
            }
        }

        if (confessionType.equals(Config.OTHERS_CONFESSIONS)) {
            if (mOtherRefreshInterface != null) {
                mOtherRefreshInterface.removeMessage(message, confessionType);
            }
        }

        if (confessionType.equals(Config.FRIENDS_CONFESSIONS)) {
            if (mFriendsRefreshInterface != null) {
                mFriendsRefreshInterface.removeMessage(message, confessionType);
            }
        }

        getMessageCount();

    }

    @Override
    public void addMessage(Message message, String confessionType) {

        Log.d(TAG, "new confession recived in confession actvity " + message.getSender_appname());

//        if(getFragmentRefreshListener()!=null){
//            getFragmentRefreshListener().addMessage(message);
//        }

        if (confessionType.equals(Config.MY_CONFESSIONS)) {
            if (mMyFragmentRefreshInterface != null) {
                mMyFragmentRefreshInterface.addMessage(message, confessionType);
            }
        }

        if (confessionType.equals(Config.OTHERS_CONFESSIONS)|| confessionType.equals(Config.FRIENDS_CONFESSIONS)) {
            if (mOtherRefreshInterface != null) {
                mOtherRefreshInterface.addMessage(message, confessionType);
            }
            if (mFriendsRefreshInterface != null) {
                mFriendsRefreshInterface.addMessage(message, confessionType);
            }
        }

//        if (confessionType.equals(Config.FRIENDS_CONFESSIONS)) {
//            if (mFriendsRefreshInterface != null) {
//                mFriendsRefreshInterface.addMessage(message, confessionType);
//            }
//        }

        // Log.d(TAG, "new confession rec");
        //  Toast.makeText(sessionExpireDialog, "confession received", Toast.LENGTH_SHORT).show();
        getMessageCount();
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {

        if (confessionType.equals(Config.MY_CONFESSIONS)) {
            if (mMyFragmentRefreshInterface != null) {
                mMyFragmentRefreshInterface.removedMessage(messageId, confessionType);
            }
        }

        if (confessionType.equals(Config.OTHERS_CONFESSIONS)) {
            if (mOtherRefreshInterface != null) {
                mOtherRefreshInterface.removedMessage(messageId, confessionType);
            }
        }

        if (confessionType.equals(Config.FRIENDS_CONFESSIONS)) {
            if (mFriendsRefreshInterface != null) {
                mFriendsRefreshInterface.removedMessage(messageId, confessionType);
            }
        }

        getMessageCount();

    }


    public void setListener(FriendsConfessionsFragment con) {
        mFriendsRefreshInterface = con;
    }

    public void setListener(MyConfessionsFragment con) {
        mMyFragmentRefreshInterface = con;
    }

    public void setListener(OthersConfessionsFragment con) {
        mOtherRefreshInterface = con;
    }


}

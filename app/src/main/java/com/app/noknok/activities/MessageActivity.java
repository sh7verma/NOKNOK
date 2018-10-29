package com.app.noknok.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.MessageCardPagerAdapter;
import com.app.noknok.customviews.CustomViewPager;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.fragments.NewMessagesFragment;
import com.app.noknok.fragments.OldMessagesFragment;
import com.app.noknok.interfaces.ChatDialogueInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.app.noknok.services.FirebaseNotificationService.cancelNotification;

/**
 * Created by dev on 10/7/17.
 */

public class MessageActivity extends BaseActivity implements View.OnClickListener, MessagesFragRefreshInterface, ChatDialogueInterface {

    private static final String TAG = "MESSAGEACTIVITYLOG";
    public static MessageActivity mMessageActivity;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    LinearLayout llMessagesCancel, llMessagesSearch, llNewMessagesContainer, llOldMessagesContainer;
    TextView tvMessagesTitle, tvNewMessagesCount, tvNewMessagesText,
            tvOldMessagesCount, tvOldMessagesText;
    Realm mRealm;
    int oldMessageCount = 0;
    SessionExpireDialog sessionExpireDialog;
    CustomViewPager mViewPager;
    MessageCardPagerAdapter mViewPagerAdapter;
    private MessagesFragRefreshInterface mNewMessageRefreshInterface, mOldMessageRefreshInterface, mMessTimelineRefreshInterface;
    private ChatDialogueInterface mChatDialogueInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRealm = Realm.getDefaultInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        BaseClass.mBaseClass.setMessagesListener(this);
        BaseClass.mBaseClass.setChatDialogueListener(this);

        mMessageActivity = this;
        cancelNotification(this);

        initUI();
        initListeners();
        getMessagesCount();

    }

    private void initListeners() {

        llMessagesCancel.setOnClickListener(this);
        llNewMessagesContainer.setOnClickListener(this);
        llOldMessagesContainer.setOnClickListener(this);
        llMessagesSearch.setOnClickListener(this);
    }

    private void initUI() {

        llMessagesCancel = (LinearLayout) findViewById(R.id.ll_messages_cancel);
        llMessagesSearch = (LinearLayout) findViewById(R.id.ll_activity_messages_search);
        tvMessagesTitle = (TextView) findViewById(R.id.tv_messages_title);
        tvNewMessagesCount = (TextView) findViewById(R.id.tv_new_messages_count);
        tvNewMessagesText = (TextView) findViewById(R.id.tv_new_messages_text);
        tvOldMessagesCount = (TextView) findViewById(R.id.tv_old_messages_count);
        tvOldMessagesText = (TextView) findViewById(R.id.tv_old_messages_text);
        llNewMessagesContainer = (LinearLayout) findViewById(R.id.ll_activity_messages_new_messages);
        llOldMessagesContainer = (LinearLayout) findViewById(R.id.ll_activity_messages_old_message);
        mViewPager = (CustomViewPager) findViewById(R.id.fl_activity_messages_fragment_container);

        mViewPagerAdapter = new MessageCardPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


    }


    private void newMessages() {
        changeBackground(true);
        mViewPager.setCurrentItem(0);
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

    private void oldMessages() {
        changeBackground(false);
        mViewPager.setCurrentItem(1);
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


//    public void receiveMessages() {
//
//        final String userId = sp.getString(Config.USER_ID, "");
//
//        final DatabaseReference profileReference = mMessageReference.child("profile").getRef();
//        final DatabaseReference chatReference = mMessageReference.child("Chats").getRef();
//        mMessageReference = mMessageReference.child("Messages").getRef();
//
//        profileReference.child(userId).child("chat_dialogue_id").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                final String commonID = String.valueOf(dataSnapshot.getKey());
//
//                if (dataSnapshot.getValue().equals("")) {
//
//                    final ArrayList<String> FirebaseNames = new ArrayList<>();
//
//                    profileReference.child(userId).child("chat_dialogue_id").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            ArrayList<String> TotalNames;
//
//                            RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
//                            TotalNames = randomNameGenerator.add();
//
//                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
//                                Collection<String> set = hashMap.values();
//                                Iterator<String> iterator = set.iterator();
//
//                                while (iterator.hasNext()) {
//                                    FirebaseNames.add(iterator.next());
//                                }
//
//                                Log.d("dtata", String.valueOf(FirebaseNames));
//                                TotalNames.removeAll(FirebaseNames);
//
//                                if (!hashMap.containsKey(commonID) || hashMap.get(commonID).equals("")) {
//
//
//                                    profileReference.child(userId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames));
//
//                                }
//
//
//                            } else {
//
//                                profileReference.child(userId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames));
//
//
//                            }
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                } else {
//
//                    mChatDialogueMap.put(dataSnapshot.getKey(), String.valueOf(dataSnapshot.getValue()));
//                    Log.d(TAG, "chatdialoguemap: " + mChatDialogueMap);
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//
//            }
//        });
//
//
//        chatReference.addChildEventListener(new ChildEventListener() {
//
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                HashMap<String, String> messagesMap = new HashMap<>();
//
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                if (mChatDialogueMap.containsKey(dataSnapshot.getKey())) {
//
//                    messagesMap.put(Config.FULL_NAME, mChatDialogueMap.get(chat.getChat_dialogue_id()));
//                    messagesMap.put(Config.LAST_MESSAGE_ID, chat.getLast_message_id());
//                    messagesMap.put(Config.LAST_MESSAGE_TIME, chat.getLast_message_time());
//                    messagesMap.put(Config.CHAT_DIALOGUE_ID, chat.getChat_dialogue_id());
//
//
//                    if (!checkIfExists(dataSnapshot.getKey())) {
//
//                        mRealm.beginTransaction();
//                        final ChatRealm chatRealm = mRealm.createObject(ChatRealm.class);
//                        chatRealm.setChatDialogueId(chat.getChat_dialogue_id());
//                        chatRealm.setRandomName(mChatDialogueMap.get(chat.getChat_dialogue_id()));
//                        chatRealm.setLastMessageId(chat.getLast_message_id());
//                        chatRealm.setLastMessageTime(chat.getLast_message_time());
//                        mRealm.commitTransaction();
//
//                    }
//
//                    getData();
//
////                    mMessageList.add(messagesMap);
////                    mCardAdapter.notifyDataSetChanged();
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                HashMap<String, String> messagesMap = new HashMap<>();
//
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                if (mChatDialogueMap.containsKey(dataSnapshot.getKey())) {
//
//                    messagesMap.put(Config.FULL_NAME, mChatDialogueMap.get(chat.getChat_dialogue_id()));
//                    messagesMap.put(Config.LAST_MESSAGE_ID, chat.getLast_message_id());
//                    messagesMap.put(Config.LAST_MESSAGE_TIME, chat.getLast_message_time());
//                    messagesMap.put(Config.CHAT_DIALOGUE_ID, chat.getChat_dialogue_id());
//
////                    for (int i = 0; i < mMessageList.size(); i++) {
////                        if (mMessageList.get(i).containsValue(dataSnapshot.getKey())) {
//
//                    RealmResults<ChatRealm> results = mRealm.where(ChatRealm.class).equalTo("chatDialogueId", dataSnapshot.getKey()).findAll();
//
//                    mRealm.beginTransaction();
//
//                    for (int i = 0; i < results.size(); i++) {
//                        ChatRealm chatMetaData = results.get(i);
//                        chatMetaData.setLastMessageTime(chat.getLast_message_time());
//                        chatMetaData.setLastMessageId(chat.getLast_message_id());
//                    }
//
//                    mRealm.commitTransaction();
//
//                    getData();
////                            mMessageList.set(i, messagesMap);
////                            mCardAdapter.notifyDataSetChanged();
////                            break;
////                        }
////                    }
//
//                }
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        messageChildEventListener =  mMessageReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Message message = dataSnapshot.getValue(Message.class);
//                if (mChatDialogueMap.containsKey(message.getChat_dialogue_id())) {
//
//
//                    if (!checkIfMessageExists(message.getMessage_id())) {
//                        mRealm.beginTransaction();
//                        MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
//                        messageRealm.setAudio_length(message.getAudio_length());
//                        messageRealm.setAudio_pitch(message.getAudio_pitch());
//                        messageRealm.setAudio_rate(message.getAudio_rate());
//                        messageRealm.setAudio_url(message.getAudio_url());
//                        messageRealm.setUser_type(message.isUser_type());
//
//                        //          Set<String> keys = message.getRead_ids().keySet();
//                        boolean messageRead = false;
//
//                        if (message.getRead_ids().size() < 2 && !message.getRead_ids().containsKey(userId)) {
//                            messageRead = false;
//                        } else {
//                            messageRead = true;
//                        }
////                        Iterator<String> iterator = keys.iterator();
////                        while (iterator.hasNext()) {
////                            if (!iterator.next().equals(message.getSender_id())) {
////                                messageRead = true;
////                            } else {
////                                messageRead = false;
////                            }
////
////                        }
//                        messageRealm.setMessage_read(messageRead);
//                        messageRealm.setMessage_id(message.getMessage_id());
//                        messageRealm.setSender_id(message.getSender_id());
//                        messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
//                        messageRealm.setSender_appname(message.getSender_appname());
//                        messageRealm.setMessage_time(message.getMessage_time());
//                        messageRealm.setMessage_type(message.isMessage_type());
//                        mRealm.commitTransaction();
//                        Log.d(TAG,"new message received");
//                    }
//                    getMessagesCount();
//                    newMessage();
//
//
////                    if(!message.getRead_ids().containsKey(sp.getString(Config.USER_ID,""))){
////                        unReadMessageCount++;
////                        Log.d(TAG,"messagecount: "+unReadMessageCount);
////                        tvNewMessagesCount.setText(String.valueOf(unReadMessageCount));
////                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        //   final ArrayList<Message> unreadMessageList = new ArrayList<>();
//
//
////        messageReference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////
////
////                final HashMap<String, ArrayList<Message>> sendersHashMap = new HashMap<>();
////
////                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
////                Iterator<DataSnapshot> iterator = iterable.iterator();
////                while (iterator.hasNext()) {
////                    DataSnapshot dataSnapshot1 = iterator.next();
////
////                    final ArrayList<Message> msgList = new ArrayList<>();
////
////                    Message message = dataSnapshot1.getValue(Message.class);
////                    String dialogueID = message.getChat_dialogue_id();
////                    for (int i = 0; i < mChatDialogueMap.size(); i++) {
////
////                        if (mChatDialogueMap.containsKey(dialogueID)) {
////                            // HashMap<String, String> readMap = message.getRead_ids();
////                            //   if(!readMap.containsKey(sp.getString(Config.USER_ID,""))){
////                            //      unreadMessageList.add(message);
////                            //   }else{
////                            msgList.add(message);
//////                        }
////
////                        }
////                    }
////
////                    sendersHashMap.put(dialogueID, msgList);
////
////
////
////                }
////
////                mMessageList.add(sendersHashMap);
////                mCardAdapter.notifyDataSetChanged();
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
////        new ChildEventListener() {
////            @Override
////            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
////                final ArrayList<Message> msgList = new ArrayList<>();
////                final HashMap<String, ArrayList<Message>> sendersHashMap = new HashMap<>();
////
////                Message message = dataSnapshot.getValue(Message.class);
////                String dialogueID = message.getChat_dialogue_id();
////                for (int i = 0; i < mChatDialogueMap.size(); i++) {
////
////                    if (mChatDialogueMap.containsKey(dialogueID)) {
////                        // HashMap<String, String> readMap = message.getRead_ids();
////
////                        //   if(!readMap.containsKey(sp.getString(Config.USER_ID,""))){
////                        //      unreadMessageList.add(message);
////
////                        //   }else{
////                        msgList.add(message);
//////                        }
////
////                        sendersHashMap.put(dialogueID, msgList);
////
////                    }
////                }
////
////
////                mMessageList.add(sendersHashMap);
////                mCardAdapter.notifyDataSetChanged();
//
////            }
////
////            @Override
////            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////
////            }
////
////            @Override
////            public void onChildRemoved(DataSnapshot dataSnapshot) {
////
////            }
////
////            @Override
////            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//
//    }

//    private boolean checkIfMessageExists(String message_id) {
//        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
//                .equalTo("message_id", message_id);
//        return query.count() != 0;
//    }

    private void getMessagesCount() {
       long unreadMessageCount = mRealm.where(MessageRealm.class).equalTo("message_type", false).equalTo("message_read", false).notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        tvNewMessagesCount.setText(String.valueOf(unreadMessageCount));
        tvOldMessagesCount.setText(String.valueOf(oldMessageCount()));
    }

//    private String newMessageCount() {
//        String newMessageCount = "0";
//
//        newMessageCount = sp.getString(Config.NEW_MESSAGE_COUNT,"0")
//
//
//        return newMessageCount;
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_messages_cancel:
                onBackPressed();
                break;

            case R.id.ll_activity_messages_new_messages:
                newMessages();
                break;

            case R.id.ll_activity_messages_old_message:
                oldMessages();
                break;

            case R.id.ll_activity_messages_search:
                startActivity(new Intent(this, MessageSearchActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
        }
    }


//    void newMessage() {
//        changeBackground(true);
//        NewMessagesFragment newMessagesFragment = new NewMessagesFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_activity_messages_fragment_container, newMessagesFragment, "NewMessagesFragment");
//        fragmentTransaction.commit();
//    }
//
//    void oldMessage() {
//        changeBackground(false);
//        OldMessagesFragment oldMessagesFragment = new OldMessagesFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_activity_messages_fragment_container, oldMessagesFragment, "OldMessagesFragment");
//        fragmentTransaction.commit();
//    }


    void changeBackground(boolean newOrOld) {
        int backColor = R.drawable.bg_round_corners;
        int backLightColor = R.drawable.bg_round_corners_light;

        int textColor = R.color.black;
        int textLightColor = R.color.lighterGrey;

        if (newOrOld) {

            llNewMessagesContainer.setBackgroundResource(backColor);
            llOldMessagesContainer.setBackgroundResource(backLightColor);

            
            tvNewMessagesText.setTextColor(ContextCompat.getColor(this,textColor));
            tvNewMessagesCount.setTextColor(ContextCompat.getColor(this,textColor));
            tvOldMessagesText.setTextColor(ContextCompat.getColor(this,textLightColor));
            tvOldMessagesCount.setTextColor(ContextCompat.getColor(this,textLightColor));


        } else {

            llNewMessagesContainer.setBackgroundResource(backLightColor);
            llOldMessagesContainer.setBackgroundResource(backColor);

            tvNewMessagesText.setTextColor(ContextCompat.getColor(this,textLightColor));
            tvNewMessagesCount.setTextColor(ContextCompat.getColor(this,textLightColor));
            tvOldMessagesText.setTextColor(ContextCompat.getColor(this,textColor));
            tvOldMessagesCount.setTextColor(ContextCompat.getColor(this,textColor));
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
//        mMessageReference.removeEventListener(messageChildEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // receiveMessages();
//        newMessage();
//        getMessagesCount();
//        getMessagesCount();
//        newMessage();
        sessionExpireDialog = new SessionExpireDialog(MessageActivity.this);
    }

    public void setNewMessageRefreshListener(NewMessagesFragment con) {
        mNewMessageRefreshInterface = con;
    }

    public void setMessageTimelineRefreshListener(MessageTimelineActivity con){
        mMessTimelineRefreshInterface = con;
    }

    public void setOldMessageRefreshListener(OldMessagesFragment con) {
        mOldMessageRefreshInterface = con;
    }

    public void setChatDialogueRefreshListener(OldMessagesFragment con){
        mChatDialogueInterface = con;
    }



    @Override
    public void addMessage(Message message) {

        Log.d("MESSCHECK","inside addmessage in messageactivity");

        if (mNewMessageRefreshInterface != null) {
            mNewMessageRefreshInterface.addMessage(message);
        }

        if(mOldMessageRefreshInterface!=null){
            mOldMessageRefreshInterface.addMessage(message);
        }

        if(mMessTimelineRefreshInterface!=null){
            mMessTimelineRefreshInterface.addMessage(message);
        }

        getMessagesCount();

    }

    @Override
    public void onMessageChange(Message message) {

        Log.d("MessageCheck", "in messactivity onchange");

        if (mNewMessageRefreshInterface != null) {
            mNewMessageRefreshInterface.onMessageChange(message);
        }

        if(mOldMessageRefreshInterface!=null){
            mOldMessageRefreshInterface.onMessageChange(message);
        }

        if(mMessTimelineRefreshInterface!=null){
            mMessTimelineRefreshInterface.onMessageChange(message);
        }

        getMessagesCount();
    }


    int oldMessageCount() {



//        oldMessageCount = sp.getString(Config.OLD_MESSAGE_COUNT,"0");
        oldMessageCount = 0;

        final RealmResults<ChatRealm> realmResultsGuessZero = mRealm.where(ChatRealm.class).findAll();
        for (ChatRealm chatRealm : realmResultsGuessZero.sort("lastMessageTime", Sort.DESCENDING)) {

            if(chatRealm!=null && chatRealm.getGuessId()!=null) {
                if (chatRealm.getGuessId().equals("1")) {
                    RealmResults<MessageRealm> realmResultsGuessZeroReadZero = mRealm.where(MessageRealm.class)
                            .equalTo("message_type", false)
                            .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);

                    if (realmResultsGuessZeroReadZero.size() > 0) {
                        oldMessageCount++;
                    }
                } else {
                    RealmResults<MessageRealm> realmResultsGuessZeroReadZero = mRealm.where(MessageRealm.class)
                            .equalTo("guess_status", "0").equalTo("message_type", false)
                            .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);

                    if (realmResultsGuessZeroReadZero.size() > 0) {
                        oldMessageCount++;
                    }

                    RealmResults<MessageRealm> realmResultsGuessOneReadZero = mRealm.where(MessageRealm.class)
                            .equalTo("guess_status", "1").equalTo("message_type", false)
                            .equalTo("chat_dialogue_id", chatRealm.getChatDialogueId()).findAllSorted("message_time", Sort.DESCENDING);
                    if (realmResultsGuessOneReadZero.size() > 0) {
                        oldMessageCount++;
                    }
                }
            }
        }
        return oldMessageCount;
    }

    @Override
    public void onUpdate() {
        Log.d("CHATDIALOGUEUPDATE","in messageactivity onupdate");

        if(mChatDialogueInterface!=null){
            mChatDialogueInterface.onUpdate();
        }

        getMessagesCount();
    }
}


package com.app.noknok.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.ChatDialogueInterface;
import com.app.noknok.interfaces.ConfessionRemoveInterface;
import com.app.noknok.models.Chat;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;
import com.app.noknok.utils.RandomNameGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.R.id.message;

/**
 * Created by dev on 4/8/17.
 */

public class FirebaseListeners {


    private static final String TAG = "FIREBASELISTAG";
    public static ConfessionsListenersInterface mConfessionsInterface;
    public static ProfileListenersInterface mProfileListenersInterface;
    public static RemovedConfesListenersInterface mRemovedConfesListenersInterface;
    public static MessageListenerInterface mMessageListenerInterface;
    public static ChatDialogueInterface mChatDialogueInterface;
    public static ConfessionRemoveInterface confessionRemoveInterface;
    static FirebaseListeners mListeners;
    Context con;
    Realm mRealm;
    SharedPreferences mSharedprePreferences;
    String mUserId, mChatGuessStatus;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mMessagesReference = firebaseDatabase.getReference("Messages").getRef();
    DatabaseReference mProfileReference = firebaseDatabase.getReference("profile").getRef();
    DatabaseReference mChatsReference = firebaseDatabase.getReference("Chats").getRef();
    DatabaseReference mConfessionsReference = firebaseDatabase.getReference("Confessions").getRef();
    DatabaseReference mRemovedConfReference = firebaseDatabase.getReference("RemovedConfessions").getRef();
    HashMap<String, ChildEventListener> messagesListenerMap = new HashMap<>();
    HashMap<String, ChildEventListener> confessionsListenerMap = new HashMap<>();
    HashMap<String, ChildEventListener> chatDialogueNameListnerHashMap = new HashMap<>();
    HashMap<String, ChildEventListener> profilechatDialogueListenerHashMap = new HashMap<>(); //done
    HashMap<String, ValueEventListener> chatsDialogueKeyListenerHashMap = new HashMap<>();  //done
    HashMap<String, ChildEventListener> chatMessageListenerHashMap = new HashMap<>();  //done

    //    HashMap<String, ChildEventListener> removeConfesListenerMap = new HashMap<>();
    HashMap<String, String> mChatDialogueMap = new HashMap<>();
    ArrayList<ChildEventListener> mProfileListenerList = new ArrayList<>();
    ArrayList<ChildEventListener> mRemoveConfesListenerList = new ArrayList<>();
    RandomColor randomColor;
    RandomIllustrator randomIllustrator;
    ArrayList<String> mFirebaseAnonNamesList = new ArrayList<>();
    ChildEventListener mProfileChildListner = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getKey().equals(Config.ACCESS_TOKEN)) {
                Log.d("ACCESSCHECK", "new access: " + dataSnapshot.getValue() + ", old access: " + mSharedprePreferences.getString(Config.ACCESS_TOKEN, ""));
                if (!dataSnapshot.getValue().equals(mSharedprePreferences.getString(Config.ACCESS_TOKEN, ""))) {
                    SharedPreferences.Editor editor = mSharedprePreferences.edit();
                    editor.putBoolean(Config.SESSION_EXPIRE, true);
                    editor.apply();
                    if (mSharedprePreferences.getBoolean(Config.SESSION_EXPIRE, false))
                        mProfileListenersInterface.onProfileChange(true);
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getKey().equals(Config.ACCESS_TOKEN)) {
                Log.d("ACCESSCHECK", "new access: " + dataSnapshot.getValue() + ", old access: " + mSharedprePreferences.getString(Config.ACCESS_TOKEN, ""));
                if (!dataSnapshot.getValue().equals(mSharedprePreferences.getString(Config.ACCESS_TOKEN, ""))) {
                    SharedPreferences.Editor editor = mSharedprePreferences.edit();
                    editor.putBoolean(Config.SESSION_EXPIRE, true);
                    editor.apply();
                    if (mSharedprePreferences.getBoolean(Config.SESSION_EXPIRE, false))
                        mProfileListenersInterface.onProfileChange(true);
                }
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

//    HashMap<String, Query> messageQuery = new HashMap<>();
//    HashMap<String, Query> chatQuery = new HashMap<>();
//    HashMap<String, Query> profileQuery = new HashMap<>();


    ////////////////////// Confessions /////////////////

    //    public static void initListener(Context con) {
//        if (mListeners == null)
//            mListeners = new FirebaseListeners();
//        mListeners.initListener(con);
//    }
//
//    public static FirebaseListeners getListenerClass(Context con) {
//        if (mListeners != null)
//            return mListeners;
//        else {
//            mListeners = new FirebaseListeners();
//            mListeners.initDefaults(con);
//            return mListeners;
//        }
//    }

    String confessionType;

    ChildEventListener mConfesMessagesChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Message message = dataSnapshot.getValue(Message.class);


            if (!checkIfMessageExists(message.getMessage_id())) {
                final Random rand = new Random();
                int randInt = rand.nextInt(randomIllustrator.size());
                int randIntColor = rand.nextInt(randomColor.size());
                mRealm.beginTransaction();
                MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
                messageRealm.setAudio_length(message.getAudio_length());
                messageRealm.setAudio_pitch(message.getAudio_pitch());
                messageRealm.setAudio_rate(message.getAudio_rate());
                messageRealm.setAudio_url(message.getAudio_url());
                messageRealm.setUser_type(message.isUser_type());

                messageRealm.setMessage_icon(randInt);
                messageRealm.setMessage_color(randIntColor);

                if (message.isUser_type()) {
                    messageRealm.setMessage_theme(message.getMessage_theme().get(mUserId));
                } else {
                    messageRealm.setMessage_theme(message.getSender_appname());
                }
                boolean messageRead;

                if (!message.getRead_ids().containsKey(mUserId)) {
                    messageRead = false;
                } else {
                    messageRead = true;
                }
                messageRealm.setMessage_read(messageRead);
                messageRealm.setMessage_id(message.getMessage_id());
                //  Log.d(TAG, "new message received" + message.getMessage_id());
                messageRealm.setSender_id(message.getSender_id());
                messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
                messageRealm.setSender_appname(message.getSender_appname());
                messageRealm.setMessage_time(message.getMessage_time());
                messageRealm.setMessage_type(message.isMessage_type());
                mRealm.commitTransaction();
                getConfessionMessages(message);
                //   mConfessionsInterface.onMessageAdd(message);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Message message = dataSnapshot.getValue(Message.class);
            String confessionType;
            Log.d(TAG, "onchildremoved" + message.getMessage_id());
            if (checkIfMessageExists(message.getMessage_id())) {
                Log.d(TAG, "confess removed from realm " + message.getMessage_id());
                mRealm.beginTransaction();

                RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class)
                        .equalTo("message_id", message.getMessage_id()).findAll();
                confessionType = messageRealm.get(0).getConfession_type();
                messageRealm.deleteFirstFromRealm();
                mRealm.commitTransaction();

                mConfessionsInterface.onMessageRemove(message, confessionType);
            }


        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ChildEventListener mConfesChildListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            setConfesMessageListener(dataSnapshot.getKey());

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            mRemovedConfReference.child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue());
            Log.d(TAG, "confession data: " + dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ChildEventListener mRemoveConfesListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            String messageId = dataSnapshot.getKey();
            String confessionType;
            Log.d("DATATSfession", dataSnapshot.toString());


            if (checkIfMessageExists(messageId)) {

                boolean deleted = false;
                RealmResults<MessageRealm> messageRealmPath = mRealm.where(MessageRealm.class)
                        .equalTo("message_id", messageId).findAll();

                Log.d("messageRealmPath", String.valueOf(messageRealmPath));

                for (MessageRealm messageRealm : messageRealmPath) {
                    File file = new File(messageRealm.getLocal_url());
                    deleted = file.delete();
                    if (!messageRealm.getSender_id().equals(mSharedprePreferences.getString(Config.USER_ID, ""))) {

                        if (confessionRemoveInterface != null) {

                            confessionRemoveInterface.confessionRemoved(deleted, messageRealm.getLocal_url());

                        }
                    }

                }
                mRealm.beginTransaction();
                RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class)
                        .equalTo("message_id", messageId).findAll();

                confessionType = messageRealm.get(0).getConfession_type();
                messageRealm.deleteFirstFromRealm();

                mRealm.commitTransaction();


                if (mSharedprePreferences.getString(Config.DELETED_CONFESSION, "").equals(messageId)) {

                    NotificationManager notificationManager = (NotificationManager) con.getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(0);
                }

                mRemovedConfReference.child(messageId)
                        .child(mSharedprePreferences.getString(Config.USER_ID, ""))
                        .removeValue();

                mRemovedConfesListenersInterface.onMessageRemoved(messageId, confessionType);
            }


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ChildEventListener chatDialogueNameListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final String commonID = String.valueOf(dataSnapshot.getKey());
            final ArrayList<String> FirebaseNames = new ArrayList<>();
            ArrayList<String> TotalNames;

            RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
            TotalNames = randomNameGenerator.add();

            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                Collection<String> set = hashMap.values();
                Iterator<String> iterator = set.iterator();

                while (iterator.hasNext()) {
                    FirebaseNames.add(iterator.next());
                }

                Log.d("dtata", String.valueOf(FirebaseNames));
                TotalNames.removeAll(FirebaseNames);

                if (!hashMap.containsKey(commonID) || hashMap.get(commonID).equals("")) {
                    mProfileReference.child(mUserId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames));
                }
            } else {
                mProfileReference.child(mUserId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames));
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ChildEventListener chatMessageListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Message message = dataSnapshot.getValue(Message.class);

            Log.d("MESSCHECK", "inside chatmessagelistener: " + message.getMessage_id());

            if (!checkIfMessageExists(message.getMessage_id())) {
                mRealm.beginTransaction();
                MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
                messageRealm.setAudio_length(message.getAudio_length());
                messageRealm.setAudio_pitch(message.getAudio_pitch());
                messageRealm.setAudio_rate(message.getAudio_rate());
                messageRealm.setAudio_url(message.getAudio_url());
                messageRealm.setUser_type(message.isUser_type());

                if (message.getGuess_status().get(mUserId).equals("0")) {
                    messageRealm.setGuess_status("0");
                } else {
                    messageRealm.setGuess_status("1");
                }
                boolean messageRead;

                if (message.getRead_ids().size() < 2 && !message.getRead_ids().containsKey(mUserId)) {
                    messageRead = false;
                } else {
                    messageRead = true;
                }
                messageRealm.setMessage_read(messageRead);
                messageRealm.setMessage_id(message.getMessage_id());
                messageRealm.setSender_id(message.getSender_id());
                messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
                messageRealm.setSender_appname(message.getSender_appname());
                messageRealm.setMessage_time(message.getMessage_time());
                messageRealm.setMessage_type(message.isMessage_type());

//                Log.d("NOTIFTAG", "new message saved in realm" + messageRealm.getMessage_id());

                mRealm.commitTransaction();
                Log.d("MESSAGETIMELINETAG", "new message received");

                mMessageListenerInterface.onMessageAdd(message);

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Message message = dataSnapshot.getValue(Message.class);
            mMessageListenerInterface.onMessageChange(message);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    ValueEventListener chatDialogueKeyListner = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // HashMap<String, String> messagesMap = new HashMap<>();
            Chat chat = dataSnapshot.getValue(Chat.class);

//            Log.d("MESSCHECK", "inside chatdialoguelistener: " + chat.getChat_dialogue_id());

//            messagesMap.put(Config.FULL_NAME, mChatDialogueMap.get(chat.getChat_dialogue_id()));
//            messagesMap.put(Config.LAST_MESSAGE_ID, chat.getLast_message_id());
//            messagesMap.put(Config.LAST_MESSAGE_TIME, chat.getLast_message_time());
//            messagesMap.put(Config.CHAT_DIALOGUE_ID, chat.getChat_dialogue_id());

            if (!checkIfChatExists(dataSnapshot.getKey())) {
                mRealm.beginTransaction();

                final Random rand = new Random();
                int randInt = rand.nextInt(randomIllustrator.size());
                int randIntColor = rand.nextInt(randomColor.size());
                int myRandInt = rand.nextInt(randomIllustrator.size());
                int myRandIntColor = rand.nextInt(randomColor.size());

                final ChatRealm chatRealm = mRealm.createObject(ChatRealm.class);
                chatRealm.setChatDialogueId(chat.getChat_dialogue_id());

                chatRealm.setRandomName(mChatDialogueMap.get(chat.getChat_dialogue_id()));

                chatRealm.setLastMessageId(chat.getLast_message_id());
                chatRealm.setLastMessageTime(chat.getLast_message_time());
                chatRealm.setGuessId(chat.getGuess_ids().get(mUserId));

                chatRealm.setReceiverName(getReceiverName(chat.getContact_numbers()));

//              chatRealm.setReceiverName(getReceiverName(chat.getGuess_ids(), chat.getContact_numbers()));

//              chatRealm.setReceiverName(String.valueOf(chat.getContact_numbers()));

                chatRealm.setReceiverId(getReceiverId(chat.getGuess_ids()));
                chatRealm.setMessageColor(randIntColor);
                chatRealm.setMessageIcon(randInt);
                chatRealm.setMyMessageColor(myRandIntColor);
                chatRealm.setMyMessageIcon(myRandInt);

                chatRealm.setLastGuessTime(chat.getLast_guess_time().get(mUserId));

                mRealm.commitTransaction();

            } else {

                RealmResults<ChatRealm> results = mRealm.where(ChatRealm.class).equalTo("chatDialogueId", dataSnapshot.getKey()).findAll();

                mRealm.beginTransaction();

                for (int i = 0; i < results.size(); i++) {
                    ChatRealm chatRealm = results.get(i);
                    chatRealm.setLastMessageTime(chat.getLast_message_time());
                    chatRealm.setLastMessageId(chat.getLast_message_id());
                }
                mRealm.commitTransaction();

                mChatDialogueInterface.onUpdate();

            }
            mChatGuessStatus = chat.getGuess_ids().get(mUserId);

            setChatMessageListener(chat.getChat_dialogue_id());

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ChildEventListener mProfileChatDialogueListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.d("MESSCHECK", "inside profilelistener: " + dataSnapshot);

            if (dataSnapshot.getValue().equals("")) {
                setChatDialogueNameListener(mUserId);
            } else {

                mChatDialogueMap.put(dataSnapshot.getKey(), String.valueOf(dataSnapshot.getValue()));
                Log.d("ffhfhfhffhfhfhf", mChatDialogueMap.toString());
//                Set<String> keys = mChatDialogueMap.keySet();
//                Iterator<String> iterator = keys.iterator();
//
//                while (iterator.hasNext()) {
//                    String dialogueKey = iterator.next();
//                    Log.d(TAG, "dialoguekey: " + dialogueKey);
//
                setChatsDialogueKeyListener(String.valueOf(dataSnapshot.getKey()));
//
            }


            //  }
        }


        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            Log.d("MESSCHECK", "inside profilelistener: " + dataSnapshot);

            if (dataSnapshot.getValue().equals("")) {
                setChatDialogueNameListener(mUserId);
            } else {

                mChatDialogueMap.put(dataSnapshot.getKey(), String.valueOf(dataSnapshot.getValue()));
                Log.d("ffhfhfhffhfhfhf", mChatDialogueMap.toString());
//                Set<String> keys = mChatDialogueMap.keySet();
//                Iterator<String> iterator = keys.iterator();
//
//                while (iterator.hasNext()) {
//                    String dialogueKey = iterator.next();
//                    Log.d(TAG, "dialoguekey: " + dialogueKey);
//
                setChatsDialogueKeyListener(String.valueOf(dataSnapshot.getKey()));
//
            }


        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public FirebaseListeners(Context context) {
        mRealm = Realm.getDefaultInstance();
        con = context;
        mSharedprePreferences = con.getSharedPreferences(con.getPackageName(), Context.MODE_PRIVATE);
        mUserId = mSharedprePreferences.getString(Config.USER_ID, "");
        randomIllustrator = new RandomIllustrator();
        randomColor = new RandomColor();
    }

    //////////////////////////////////////// profile listeners/////////////////////////////

    public static void setInterRefer(ConfessionRemoveInterface mCon) {
        confessionRemoveInterface = mCon;
    }

    public static void initListener(Context con) {

        if (mListeners == null)
            mListeners = new FirebaseListeners(con);
        mListeners.initDefaults(con);
    }

    public static FirebaseListeners getListenerClass(Context con) {

        if (mListeners != null)
            return mListeners;
        else {
            mListeners = new FirebaseListeners(con);
            return mListeners;
        }
    }


    /////////////////////////////////profile listener ends ////////////////////////////////

    /////////////////////////// remove confessions listener ///////////////////////////////

    void initDefaults(Context context) {
        mSharedprePreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        mUserId = mSharedprePreferences.getString(Config.USER_ID, "");
        con = context;
    }

    public void removeProfileListener() {
        if (mProfileListenerList != null && mProfileListenerList.size() > 0 && mProfileReference != null) {
            for (int i = 0; i < mProfileListenerList.size(); i++) {
//                if (mProfileListenerList.get(i) != null && mProfileReference != null) {
//                    mProfileReference.removeEventListener(mProfileListenerList.get(i));
                mProfileReference.child(mUserId).removeEventListener(mProfileListenerList.get(i));
//                mProfileListenerList.remove(mProfileListenerList.get(i));
//                }
            }
            mProfileListenerList.clear();
        }
    }

    public void setmProfileListenersInterface(ProfileListenersInterface listenersInterface) {
        mProfileListenersInterface = listenersInterface;
    }


    /////////////////////////// remove confessions listener ends /////////////////////////////


    //////////////////////////// messages listener ////////////////////////////////////////

    public void setProfileListener(String userId) {
        mProfileReference.child(userId).addChildEventListener(mProfileChildListner);
        mProfileListenerList.add(mProfileChildListner);
    }

    public void removeRemovedConfesListeners() {
        for (int i = 0; i < mRemoveConfesListenerList.size(); i++) {
            if (mRemoveConfesListenerList.get(i) != null && mRemovedConfReference != null) {
                mRemovedConfReference.removeEventListener(mRemoveConfesListenerList.get(i));
                mRemoveConfesListenerList.remove(mRemoveConfesListenerList.get(i));
            }
        }
    }

    public void setmRemovedConfesListenersInterface(RemovedConfesListenersInterface listenersInterface) {
        mRemovedConfesListenersInterface = listenersInterface;
    }

    public void setRemoveConfesListener(String userId) {
        mRemovedConfReference.orderByChild(userId).equalTo(userId).addChildEventListener(mRemoveConfesListener);
        mRemoveConfesListenerList.add(mRemoveConfesListener);
    }

    public void setProfileChatDialogueListener(String userId) {

        mProfileReference.child(userId).child("chat_dialogue_id").addChildEventListener(mProfileChatDialogueListener);
        profilechatDialogueListenerHashMap.put(userId, mProfileChatDialogueListener);

    }

    private void removeProfileChatDialogueListener(String userId) {

        ChildEventListener childEventListener = profilechatDialogueListenerHashMap.get(userId);
        if (childEventListener != null && mMessagesReference != null && profilechatDialogueListenerHashMap.size() > 0) {
            mProfileReference.removeEventListener(childEventListener);
            profilechatDialogueListenerHashMap.remove(userId);
        }

    }

    private boolean checkIfChatExists(String id) {
        RealmQuery<ChatRealm> query = mRealm.where(ChatRealm.class)
                .equalTo("chatDialogueId", id);
        return query.count() != 0;
    }

    private void setChatsDialogueKeyListener(String dialogueKey) {
        mChatsReference.child(dialogueKey).addValueEventListener(chatDialogueKeyListner);
        chatsDialogueKeyListenerHashMap.put(dialogueKey, chatDialogueKeyListner);
    }

    private void removeChatsDialogueKeyListener() {

        Collection<ValueEventListener> valueEventListener = chatsDialogueKeyListenerHashMap.values();
        if (mMessagesReference != null) {
            Iterator<ValueEventListener> iterator = valueEventListener.iterator();
            while (iterator.hasNext()) {
                mChatsReference.removeEventListener(iterator.next());
            }
            chatsDialogueKeyListenerHashMap.clear();
        }


    }

    public void removeChatMessageListener() {
        Collection<ChildEventListener> childEventListener = chatMessageListenerHashMap.values();
        Set<String> set = chatMessageListenerHashMap.keySet();
        ArrayList<String> chatDialogueList = new ArrayList<>();
        for (String s : set) {
            chatDialogueList.add(s);
        }

        int i = 0;

        if (mMessagesReference != null) {
            Iterator<ChildEventListener> iterator = childEventListener.iterator();
            while (iterator.hasNext()) {
                mMessagesReference.child(chatDialogueList.get(i))
                        .removeEventListener(iterator.next());
                i++;
            }
            chatMessageListenerHashMap.clear();
        }
        removeChatsDialogueKeyListener();
        removeProfileChatDialogueListener(mUserId);
        removeChatDialogueNameListner(mUserId);
    }

    private void setChatDialogueNameListener(String mUserId) {

        mProfileReference.child(mUserId).child("chat_dialogue_id").addChildEventListener(chatDialogueNameListener);
        chatDialogueNameListnerHashMap.put(mUserId, chatDialogueNameListener);

    }

    private void removeChatDialogueNameListner(String mUserId) {
        ChildEventListener childEventListener = chatDialogueNameListnerHashMap.get(mUserId);
        if (childEventListener != null && mMessagesReference != null && chatDialogueNameListnerHashMap.size() > 0) {
            mProfileReference.removeEventListener(childEventListener);
            chatDialogueNameListnerHashMap.remove(mUserId);
        }

    }

    public void setChatMessageListener(String dialogueKey) {
        mMessagesReference.orderByChild("chat_dialogue_id").equalTo(dialogueKey).addChildEventListener(chatMessageListener);
        chatMessageListenerHashMap.put(dialogueKey, chatMessageListener);
    }

    public void setMessagesListenerInterface(MessageListenerInterface Listener) {
        mMessageListenerInterface = Listener;
    }

    public void setmChatDialogueInterface(ChatDialogueInterface listener) {
        mChatDialogueInterface = listener;
    }

    private String getReceiverName(HashMap<String, String> contact_numbers) {
//        String recId = "";
//        Set<String> keys = guessIds.keySet();
//        for (String id : keys) {
//            if (!id.equals(mUserId)) {
//                recId = id;
//            }
//        }
//        FriendsModelRealm friendsModelRealms = mRealm.where(FriendsModelRealm.class).equalTo("user_id", recId).findFirst();
//        if (friendsModelRealms != null)
//            return friendsModelRealms.getName_in_contact();
//        else {

        Set<String> contactKeys = contact_numbers.keySet();
        String number = "";
        for (String s : contactKeys) {
            if (!s.equals(mUserId)) {
                number = contact_numbers.get(s);
                Log.d("recieverNumber", number);
            }
        }
        return number;
//        }
    }

    private String getReceiverId(HashMap<String, String> guessIds) {
        String recId = "";
        Set<String> keys = guessIds.keySet();
        for (String id : keys) {
            if (!id.equals(mUserId)) {
                recId = id;
            }
        }
        return recId;
    }

    //////////////////////////////////////// profile listeners/////////////////////////////

    private boolean checkIfMessageExists(String message_id) {
        mRealm = Realm.getDefaultInstance();
        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
                .equalTo("message_id", message_id);
        return query.count() != 0;
    }


    //////////////////////////// messages listener ends ///////////////////////////////////////

    public void setConfessionsListener(ConfessionsListenersInterface listsner) {
        mConfessionsInterface = listsner;
    }

    public void setConfessionListener(String userId) {
        mConfessionsReference.orderByChild(userId).equalTo(userId).addChildEventListener(mConfesChildListener);
        confessionsListenerMap.put(userId, mConfesChildListener);
    }

    public void setConfesMessageListener(String messageId) {
        mMessagesReference.orderByChild("message_id").equalTo(messageId).addChildEventListener(mConfesMessagesChildListener);
        messagesListenerMap.put(messageId, mConfesMessagesChildListener);
    }

    public void removeConfessionListener(String userId) {
        ChildEventListener childEventListener = confessionsListenerMap.get(userId);
        if (childEventListener != null && mConfessionsReference != null) {
            mConfessionsReference.removeEventListener(childEventListener);
            confessionsListenerMap.remove(userId);
        }
    }

    public void removeConfesMessageListener() {
        Collection<ChildEventListener> childEventListener = messagesListenerMap.values();
        if (mMessagesReference != null) {
            Iterator<ChildEventListener> iterator = childEventListener.iterator();
            while (iterator.hasNext()) {

                mMessagesReference.removeEventListener(iterator.next());
            }

            messagesListenerMap.clear();
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

    private void getConfessionMessages(final Message message) {
        mFirebaseAnonNamesList.add(message.getMessage_theme().get(mSharedprePreferences.getString(Config.USER_ID, "")));

        HashMap<String, String> friendsMap = friendsUserId();

        Log.d(TAG, "outside friendsmap");

        if (message.getSender_id().equals(mUserId)) {
            confessionType = Config.MY_CONFESSIONS;
        } else {
            confessionType = Config.OTHERS_CONFESSIONS;
        }

        if (friendsMap != null && friendsMap.size() > 0) {
            if (friendsMap.containsKey(message.getSender_id())) {
                confessionType = Config.FRIENDS_CONFESSIONS;
            }
        }
            Log.d(TAG, "confession type: " + confessionType);

            if (!message.getSender_id().equals(mUserId) && message.isUser_type()) {

                final String userId = mSharedprePreferences.getString(Config.USER_ID, "");
                final String messageId = message.getMessage_id();
                if (message.getMessage_theme().get(mSharedprePreferences.getString(Config.USER_ID, "")).equals("")) {

                    RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
                    ArrayList<String> randomNameList = randomNameGenerator.add();

                    randomNameList.removeAll(mFirebaseAnonNamesList);

                    Log.d("randomnameListSize", String.valueOf(randomNameList.size()));
                    Log.d("mFirebaseAnonNamesList", String.valueOf(mFirebaseAnonNamesList.size()));

                    final String themeName = randomNameGenerator.get(randomNameList);
                    mMessagesReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
                                    .equalTo("message_type", true).equalTo("message_id", messageId).findAll();
                            mRealm.beginTransaction();

                            for (int i = 0; i < msgResults.size(); i++) {
                                MessageRealm messageRealm = msgResults.get(i);
                                messageRealm.setMessage_theme(themeName);
                            }
                            mRealm.commitTransaction();
//                            updateConfessionType(messageId);
                            mConfessionsInterface.onMessageAdd(message, confessionType);
                        }
                    });
                } else {
                    updateConfessionType(messageId);
                    mConfessionsInterface.onMessageAdd(message, confessionType);
                }
            } else {
                updateConfessionType(message.getMessage_id());
                mConfessionsInterface.onMessageAdd(message, confessionType);
            }
//            realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//                @Override
//                public void onChange(RealmResults<MessageRealm> realms) {
//                    if (friendsConfessionsList != null)
//                        friendsConfessionsList.clear();
//
//                    if (firebaseNameList != null)
//                        firebaseNameList.clear();
//
//
//                    String[] friendsArray = new String[friendsRealmsResults.size()];
//
//                    int j = 0;
//                    for (FriendsModelRealm friendsModelRealm : friendsRealmsResults) {
//                        friendsArray[j] = friendsModelRealm.getUser_id();
//                        j++;
//                    }
//
//                    friendsRealmResults = mRealm.where(MessageRealm.class)
//                            .equalTo("message_type", true).in("sender_id", friendsArray).findAll();
//
//
//                    for (MessageRealm messageRealm : friendsRealmResults.sort("message_time", Sort.DESCENDING)) {
//                        Message message = new Message();
//                        message.setAudio_length(messageRealm.getAudio_length());
//                        message.setMessage_time(messageRealm.getMessage_time());
//                        message.setAudio_url(messageRealm.getAudio_url());
//                        message.setAudio_pitch(messageRealm.getAudio_pitch());
//                        message.setAudio_rate(messageRealm.getAudio_rate());
//                        message.setSender_id(messageRealm.getSender_id());
//                        message.setMessage_id(messageRealm.getMessage_id());
//                        message.setGuess_status(messageRealm.getGuess_status());
//                        message.setSender_number(messageRealm.getSender_number());
//                        message.setSender_appname(messageRealm.getSender_appname());
//                        message.setLocal_url(messageRealm.getLocal_url());
//                        message.setUser_type(messageRealm.isUser_type());
//                        message.setMessageIcon(messageRealm.getMessageIcon());
//
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put(sp.getString(Config.USER_ID, ""), messageRealm.getMessage_theme());
//                        message.setMessage_theme(hashMap);
//                        friendsConfessionsList.add(message);
//                    }
//
//
//                    RandomNameGenerator randomNameGenerator = new RandomNameGenerator();
//                    ArrayList<String> randomNameList = randomNameGenerator.add();
//
//                    randomNameList.removeAll(firebaseNameList);
//
//
//                    for (int i = 0; i < friendsConfessionsList.size(); i++) {
//                        final int index = i;
//                        final Message message = friendsConfessionsList.get(i);
//                        final String userId = sp.getString(Config.USER_ID, "");
//                        final String messageId = message.getMessage_id();
//                        if (message.getMessage_theme().get(userId).equals("")) {
//                            final String themeName = randomNameGenerator.get(randomNameList);
//                            mDatabaseReference.child(messageId).child("message_theme").child(userId).setValue(themeName).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    RealmResults<MessageRealm> msgResults = mRealm.where(MessageRealm.class)
//                                            .equalTo("message_type", true).equalTo("message_id", messageId).findAll();
//
//                                    mRealm.beginTransaction();
//
//                                    for (int i = 0; i < msgResults.size(); i++) {
//                                        MessageRealm messageRealm = msgResults.get(i);
//                                        messageRealm.setMessage_theme(themeName);
//                                    }
//
//                                    mRealm.commitTransaction();
//                                    mRealm.close();
//
//                                    HashMap<String, String> hashMap = new HashMap<String, String>();
//                                    hashMap.put(userId, themeName);
//                                    message.setMessage_theme(hashMap);
//                                    friendsConfessionsList.set(index, message);
//                                    confessionsCardPagerAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//                        }
//
//
//                        // ((ConfessionsActivity) mcActivity).getFriendsCount();
//                        confessionsCardPagerAdapter.notifyDataSetChanged();
//                    }
//
//                }
//            });
//        } else {
//            updateConfessionType(message.getMessage_id());
//            mConfessionsInterface.onMessageAdd(message, confessionType);
//        }
    }

    void updateConfessionType(String messageId) {
        RealmResults<MessageRealm> msgRealm = mRealm.where(MessageRealm.class)
                .equalTo("message_type", true).equalTo("message_id", messageId).findAll();

        mRealm.beginTransaction();

        for (int i = 0; i < msgRealm.size(); i++) {
            MessageRealm mr = msgRealm.get(i);
            mr.setConfession_type(confessionType);
        }
        mRealm.commitTransaction();
        mRealm.close();
    }

    public interface MessageListenerInterface {
        void onMessageAdd(Message message);

        void onMessageChange(Message message);
    }

    public interface ProfileListenersInterface {
        void onProfileChange(boolean profileChanged);
    }


    public interface RemovedConfesListenersInterface {
        void onMessageRemoved(String messageId, String confessionType);
    }


    public interface ConfessionsListenersInterface {
        void onMessageAdd(Message message, String confessionType);

        void onMessageRemove(Message message, String confessionType);
    }

}

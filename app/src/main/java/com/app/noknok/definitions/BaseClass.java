package com.app.noknok.definitions;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.app.noknok.activities.ConfessionsActivity;
import com.app.noknok.activities.HomeActivity;
import com.app.noknok.activities.MessageActivity;
import com.app.noknok.activities.MessageTimelineActivity;
import com.app.noknok.fragments.HomeFragment;
import com.app.noknok.interfaces.ChatDialogueInterface;
import com.app.noknok.interfaces.ConfesFragmentRefreshInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.models.Message;
import com.app.noknok.services.FirebaseListeners;
import com.app.noknok.services.FirebaseNotificationService;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by dev on 26/6/17.
 */

public class BaseClass extends Application {


    public static String currentChatDialogue = "";
    public static String currentMessageType = "";
    public static int mBadgeCount = 0;
    public static BaseClass mBaseClass;
    public Realm mRealm;
    String mMessageType = "";
    ConfesFragmentRefreshInterface confesFragmentRefreshInterface;
    ConfesFragmentRefreshInterface homeConfesFragmentRefreshInterface;
    ConfesFragmentRefreshInterface mNotificationConfessionInterface;
    MessagesFragRefreshInterface messagesFragRefreshInterface;
    MessagesFragRefreshInterface homeMessagesFragRefreshInterface;
    MessagesFragRefreshInterface mMessageTimelineRefreshInterface;
    MessagesFragRefreshInterface mNotificationMessageInterface;
    FirebaseListeners.ProfileListenersInterface mProfileChangeInterface;
    ChatDialogueInterface mChatDialogueInterface;
    ChatDialogueInterface homeChatDialogueInterface;
    SharedPreferences sp;
    Context mContext;


    @Override
    public void onCreate() {

        super.onCreate();
        mBaseClass = this;
        mContext = this;

        Fabric.with(this, new Crashlytics());
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);


        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (sp.getBoolean(Config.LOGGED_IN, false)) {
            startFirebase();
        }


    }


    private void startFirebase() {
        receiveMessages();
        getConfessions();
        checkLogin();
    }




    public void receiveMessages() {

        FirebaseListeners.MessageListenerInterface messageListenerInterface = new FirebaseListeners.MessageListenerInterface() {
            @Override
            public void onMessageAdd(Message message) {


                if (messagesFragRefreshInterface != null) {
                    messagesFragRefreshInterface.addMessage(message);
                }

                if (mMessageTimelineRefreshInterface != null) {
                    mMessageTimelineRefreshInterface.addMessage(message);
                }
                if (homeMessagesFragRefreshInterface != null) {
                    homeMessagesFragRefreshInterface.addMessage(message);
                }
                if (mNotificationMessageInterface != null) {
                    mNotificationMessageInterface.addMessage(message);
                }
            }

            @Override
            public void onMessageChange(Message message) {

                if (messagesFragRefreshInterface != null) {
                    messagesFragRefreshInterface.onMessageChange(message);
                }

                if (mMessageTimelineRefreshInterface != null) {
                    mMessageTimelineRefreshInterface.onMessageChange(message);
                }

                if (homeMessagesFragRefreshInterface != null) {
                    homeMessagesFragRefreshInterface.onMessageChange(message);
                }

//                getMessagesCount();
            }
        };


        ChatDialogueInterface chatDialogueInterface = new ChatDialogueInterface() {
            @Override
            public void onUpdate() {

                if (mChatDialogueInterface != null) {
                    mChatDialogueInterface.onUpdate();
                }

                if (homeChatDialogueInterface != null) {
                    homeChatDialogueInterface.onUpdate();
                }

            }
        };
        FirebaseListeners.getListenerClass(mContext).setProfileChatDialogueListener(sp.getString(Config.USER_ID, ""));
        FirebaseListeners.getListenerClass(mContext).setMessagesListenerInterface(messageListenerInterface);
        FirebaseListeners.getListenerClass(mContext).setmChatDialogueInterface(chatDialogueInterface);
    }


    public void getConfessions() {

        FirebaseListeners.ConfessionsListenersInterface confessionsListenersInterface = new FirebaseListeners.ConfessionsListenersInterface() {
            @Override
            public void onMessageAdd(Message message, String confessionType) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Config.CONFESSION_TYPE, confessionType);
                editor.apply();

//                setConfessionType();


                if (confesFragmentRefreshInterface != null) {
                    confesFragmentRefreshInterface.addMessage(message, confessionType);
                }

                if (homeConfesFragmentRefreshInterface != null) {
                    homeConfesFragmentRefreshInterface.addMessage(message, confessionType);
                }
                if (mNotificationConfessionInterface != null) {
                    mNotificationConfessionInterface.addMessage(message, confessionType);
                }
//                getConfessionsCount();
//                        setNotificationConfessionListener();
                //getNotificationConfession(message, confessionType);

            }

            @Override
            public void onMessageRemove(Message message, String confessionType) {

                if (confesFragmentRefreshInterface != null) {
                    confesFragmentRefreshInterface.removeMessage(message, confessionType);
                }

                if (homeConfesFragmentRefreshInterface != null) {
                    homeConfesFragmentRefreshInterface.removeMessage(message, confessionType);
                }
//                getConfessionsCount();

            }
        };


        FirebaseListeners.RemovedConfesListenersInterface removedConfesListenersInterface = new FirebaseListeners.RemovedConfesListenersInterface() {
            @Override
            public void onMessageRemoved(String messageId, String confessionType) {

                if (confesFragmentRefreshInterface != null) {
                    confesFragmentRefreshInterface.removedMessage(messageId, confessionType);
                }

                if (homeConfesFragmentRefreshInterface != null) {
                    homeConfesFragmentRefreshInterface.removedMessage(messageId, confessionType);
                }


//                getConfessionsCount();


            }
        };

        FirebaseListeners.getListenerClass(mContext).setConfessionsListener(confessionsListenersInterface);
        FirebaseListeners.getListenerClass(mContext).setConfessionListener(sp.getString(Config.USER_ID, ""));

        FirebaseListeners.getListenerClass(mContext).setRemoveConfesListener(sp.getString(Config.USER_ID, ""));
        FirebaseListeners.getListenerClass(mContext).setmRemovedConfesListenersInterface(removedConfesListenersInterface);


    }


    public void checkLogin() {


        FirebaseListeners.ProfileListenersInterface profileListenersInterface = new FirebaseListeners.ProfileListenersInterface() {
            @Override
            public void onProfileChange(boolean profileChanged) {
                if (profileChanged) {
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean(Config.SESSION_EXPIRE, true);
//                    editor.apply();
//                    sessionExpireDialog.showDialog();
                    if (mProfileChangeInterface != null)
                        mProfileChangeInterface.onProfileChange(profileChanged);
                }
            }
        };

        FirebaseListeners.getListenerClass(mContext).setProfileListener(sp.getString(Config.USER_ID, ""));
        FirebaseListeners.getListenerClass(mContext).setmProfileListenersInterface(profileListenersInterface);

    }

    public void setPorfileChangeListener(HomeActivity con) {
        mProfileChangeInterface = con;
    }


    public void setConfessionsListener(ConfessionsActivity con) {
        confesFragmentRefreshInterface = con;
    }

    public void setHomeConfessionsListener(HomeFragment con) {
        homeConfesFragmentRefreshInterface = con;
    }

    public void setMessageTimelineListener(MessageTimelineActivity con) {
        mMessageTimelineRefreshInterface = con;
    }

    public void setMessagesListener(MessageActivity con) {
        messagesFragRefreshInterface = con;
    }

    public void setChatDialogueListener(MessageActivity con) {
        mChatDialogueInterface = con;
    }

    public void setHomeMessagesListener(HomeFragment con) {
        homeMessagesFragRefreshInterface = con;
    }

    public void setHomeChatDialogueListener(HomeFragment con) {
        homeChatDialogueInterface = con;
    }

    public void setNotifcationMessageListener(FirebaseNotificationService con) {
        mNotificationMessageInterface = con;
    }

    public void setNotificationConfessionListener(FirebaseNotificationService con) {
        mNotificationConfessionInterface = con;
    }


//    void getNotificationMessage(Message message){
//
//        Intent intent = null;
//        if(message!=null) {
//            getMessageType(message.getMessage_id(), message.getChat_dialogue_id());
//
//            if (currentChatDialogue.equals(message.getChat_dialogue_id())) {
//                if (!mMessageType.equals(currentMessageType)) {
//
//                    intent = new Intent(this, MessageTimelineActivity.class);
//                    intent.putExtra(Config.COMING_FROM, true);
//                    intent.putExtra(Config.CHAT_DIALOGUE_ID, message.getChat_dialogue_id());
//                    intent.putExtra(Config.MESSAGE_ID, message.getMessage_id());
//                    sendNotification("New Message Received", intent);
////                    setBadge(this, mBadgeCount);
//                }
//            } else {
//                intent = new Intent(this, MessageTimelineActivity.class);
//                intent.putExtra(Config.COMING_FROM, true);
//                intent.putExtra(Config.CHAT_DIALOGUE_ID, message.getChat_dialogue_id());
//                intent.putExtra(Config.MESSAGE_ID, message.getMessage_id());
//                sendNotification("New Message Received", intent);
////                setBadge(this, mBadgeCount);
//            }
//        }
//
//    }
//
//    void getNotificationConfession(Message message, String confessionType){
//
//        Intent intent = null;
//
//        if(message!=null) {
//            if (!message.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
//
//                intent = new Intent(this, ConfessionsChatActivity.class);
//                intent.putExtra(Config.COMING_FROM, true);
//                intent.putExtra(Config.CONFESSION_TYPE, confessionType);
//                intent.putExtra(Config.FULL_NAME, message.getSender_appname());
//                sendNotification("New Confession Received", intent);
////                setBadge(this, mBadgeCount);
//
//            }
//        }
//    }
//
//    private void sendNotification(String titleText, Intent intent) {
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(convertImage())
//                .setContentTitle(titleText)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//
//    }
//
//    Bitmap convertImage() {
//        Bitmap bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        return bitmapIcon;
//    }
//
//    public void getMessageType(String messageId, String chatDialogueId) {
//
//        mRealm = Realm.getDefaultInstance();
//
//        if (checkIfMessageExists(messageId) && ) {
//            Log.d("NOTIFTAG", "notification in getmessagetype");
//
//            ChatRealm chatRealm = mRealm.where(ChatRealm.class)
//                    .equalTo("chatDialogueId", chatDialogueId).findFirst();
//
//
//            if (chatRealm.getGuessId().equals("1")) {
//
//                mMessageType = Config.MESSAGE_TYPE_GUESSED;
//
//            } else if (chatRealm.getGuessId().equals("0")) {
//                MessageRealm messageRealm = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", false).equalTo("message_id", messageId).findFirst();
//
//                if (messageRealm.getGuess_status().equals("0")) {
//                    mMessageType = Config.MESSAGE_TYPE_RECEIVED;
//                } else if (messageRealm.getGuess_status().equals("1")) {
//                    mMessageType = Config.MESSAGE_TYPE_SENT;
//                }
//
//            }
//
//            Log.d("NOTIFTAG", "notification in getmessagetype: " + mMessageType);
//            Log.d("NOTIFTAG", "notification in getmessagetype messageid: " + messageId);
//
//        }
//    }
//
//
//    private boolean checkIfMessageExists(String message_id) {
//        mRealm = Realm.getDefaultInstance();
//        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
//                .equalTo("message_id", message_id);
//        return query.count() != 0;
//    }
}
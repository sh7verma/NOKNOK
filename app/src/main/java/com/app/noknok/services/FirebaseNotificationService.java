package com.app.noknok.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.noknok.R;
import com.app.noknok.activities.ConfessionsActivity;
import com.app.noknok.activities.ConfessionsChatActivity;
import com.app.noknok.activities.MessageTimelineActivity;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.ConfesFragmentRefreshInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import io.realm.Realm;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.app.noknok.definitions.BaseClass.mBaseClass;

/**
 * Created by dev on 23/8/17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService implements MessagesFragRefreshInterface, ConfesFragmentRefreshInterface {

    private static final String TAG = "NOTIFTAG";
    static SharedPreferences sp;
    String mCurrentChatDialogueId = "", mCurrentMessageType = "";
    Realm mRealm;

    String mUserId = "";
    //    int mBadgeCount = 0;
    String mMessageType = "";


    public static void setBadge(Context context) {

        sp = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(Config.BADGE, String.valueOf(count));
//
//        editor.apply();
        int count = sp.getInt(Config.BADGE, 0);
        count++;
        sp.edit().putInt(Config.BADGE, count).apply();
        ShortcutBadger.applyCount(context, count);
//
//
//       String launcherClassName = getLauncherClassName(context);
//        Log.d(TAG, "laumcher class name: " + launcherClassName);
//        if (launcherClassName == null) {
//            return;
//        }
//        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
//        intent.putExtra("badge_count", count);
//        intent.putExtra("badge_count_package_name", context.getPackageName());
//        intent.putExtra("badge_count_class_name", launcherClassName);
//        context.sendBroadcast(intent);
//        sp = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(Config.BADGE, String.valueOf(count));
//        editor.apply();
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG,"in oncreate ");
//
////        BaseClass.mBaseClass.setNotifcationMessageListener(this);
////        BaseClass.mBaseClass.setNotificationConfessionListener(this);
//
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mBaseClass.setNotifcationMessageListener(this);
        mBaseClass.setNotificationConfessionListener(this);

        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mUserId = sp.getString(Config.USER_ID, "");
//        setBadge(this);

//        int count = sp.getInt(Config.BADGE, 0);
//        count++;
//        sp.edit().putInt(Config.BADGE, count).apply();

        //currentChatDialogueId = BaseClass.currentChatDialogue;
        //currentMessageType = currentMessageType;

        Intent intent = null;
        Log.d(TAG, "remotemessage: " + remoteMessage.getData());
//        Log.d(TAG, "currentchatdialogue in baseclass: " + currentChatDialogueId);
//        Log.d(TAG, "currentmessagtype in baseclass: " + currentMessageType);

//        Map<String, String> data = remoteMessage.getData();

//        mBadgeCount = sp.getInt(Config.BADGE, 0);

//        if (data.get(Config.NOTIFICATION_TYPE).equals("1")) {  // 1 for messages
////            mBadgeCount = Integer.parseInt(data.get(Config.BADGE));
//
//
////            getMessageType(data.get("message_id"), data.get("dialogue_id"));
////
////            if (currentChatDialogueId.equals(data.get("dialogue_id"))) {
////
////                if (!mMessageType.equals(currentMessageType)) {
////
////                    intent = new Intent(this, MessageTimelineActivity.class);
////                    intent.putExtra(Config.COMING_FROM, true);
////                    intent.putExtra(Config.CHAT_DIALOGUE_ID, data.get("dialogue_id"));
////                    intent.putExtra(Config.MESSAGE_ID, data.get("message_id"));
////                    sendNotification("New Message Received", intent);
////
////                    setBadge(this, Integer.parseInt(data.get(Config.BADGE)));
////                }
////            } else {
////                intent = new Intent(this, MessageTimelineActivity.class);
////                intent.putExtra(Config.COMING_FROM, true);
////                intent.putExtra(Config.CHAT_DIALOGUE_ID, data.get("dialogue_id"));
////                intent.putExtra(Config.MESSAGE_ID, data.get("message_id"));
////                sendNotification("New Message Received", intent);
////                setBadge(this, Integer.parseInt(data.get(Config.BADGE)));
////            }
//        } else if (data.get(Config.NOTIFICATION_TYPE).equals("2")) {    //2 for confessions
//            mBadgeCount = Integer.parseInt(data.get(Config.BADGE));
//        }
    }


    public void getMessageType(String messageId, String chatDialogueId) {

        mRealm = Realm.getDefaultInstance();

        Log.d("NOTIFTAG", "notification in getmessagetype");
        Log.d("accessToken", sp.getString(Config.ACCESS_TOKEN, ""));

        ChatRealm chatRealm = mRealm.where(ChatRealm.class)
                .equalTo("chatDialogueId", chatDialogueId).findFirst();

        if (chatRealm.getGuessId().equals("1")) {
            mMessageType = Config.MESSAGE_TYPE_GUESSED;
        } else if (chatRealm.getGuessId().equals("0")) {
            Log.d("CHATGUESSSTATUS", messageId);
            MessageRealm messageRealm = mRealm.where(MessageRealm.class)
                    .equalTo("message_read", false).equalTo("message_type", false)
                    .equalTo("message_id", messageId).findFirst();

            Log.d("CHATGUESSSTATUS", chatRealm.getGuessId());
//            try{
            if (messageRealm.getGuess_status().equals("0")) {

                Log.d("CHATGUESSSTATUS", messageRealm.getGuess_status());
                mMessageType = Config.MESSAGE_TYPE_RECEIVED;

            } else if (messageRealm.getGuess_status().equals("1")) {
                Log.d("CHATGUESSSTATUS", chatRealm.getGuessId());
                mMessageType = Config.MESSAGE_TYPE_SENT;
            }
        }

        Log.d("NOTIFTAG", "notification in getmessagetype: " + mMessageType);
        Log.d("NOTIFTAG", "notification in getmessagetype messageid: " + messageId);
    }

    private void sendNotification(String titleText, Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (sp.getString(Config.NOTIFICATION_ON, "true").equals("true")) {

            if (titleText.equals("New Confession Received")) {

                PendingIntent pendingIntentConfession = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(convertImage())
                        .setContentTitle(titleText)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntentConfession);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

            } else {
                PendingIntent pendingIntentMessage = PendingIntent.getActivity(this, 1, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(convertImage())
                        .setContentTitle(titleText)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntentMessage);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());

            }
        }
    }

//    int smallIconImage() {
//        int icon;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            icon = R.mipmap.ic_launcher;
//        } else {
//            icon = R.drawable.notification_ic_launcher;
//        }
//        return icon;
//    }

    Bitmap convertImage() {
        Bitmap bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        return bitmapIcon;
    }


    @Override
    public void addMessage(Message message) {

        mCurrentChatDialogueId = BaseClass.currentChatDialogue;
        mCurrentMessageType = BaseClass.currentMessageType;

        Intent intent = null;
        if (message != null) {
            if (!message.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {

                getMessageType(message.getMessage_id(), message.getChat_dialogue_id());

                if (mCurrentChatDialogueId.equals(message.getChat_dialogue_id())) {
                    if (!mMessageType.equals(mCurrentMessageType)) {
                        intent = new Intent(this, MessageTimelineActivity.class);
                        intent.putExtra(Config.COMING_FROM, true);
                        intent.putExtra(Config.CHAT_DIALOGUE_ID, message.getChat_dialogue_id());
                        intent.putExtra(Config.MESSAGE_ID, message.getMessage_id());

                        Log.d("CHATDIALOGUEID", message.getChat_dialogue_id());
                        Log.d("CHATDIALOGUEID", "MESSAGEID" + message.getMessage_id());
                        sendNotification("New Message Received", intent);
                        setBadge(this);
                    }
                } else {
                    intent = new Intent(this, MessageTimelineActivity.class);
                    intent.putExtra(Config.COMING_FROM, true);
                    intent.putExtra(Config.CHAT_DIALOGUE_ID, message.getChat_dialogue_id());
                    intent.putExtra(Config.MESSAGE_ID, message.getMessage_id());
                    sendNotification("New Message Received", intent);

                    Log.d("CHATDIALOGUEID", message.getChat_dialogue_id());
                    Log.d("CHATDIALOGUEID", "MESSAGEID" + message.getMessage_id());

                    setBadge(this);
                }
            }
        }
    }

    @Override
    public void onMessageChange(Message message) {

    }

    @Override
    public void removeMessage(Message message, String confessionType) {

    }

    @Override
    public void addMessage(Message message, String confessionType) {

        Intent intent = null;
        mRealm = Realm.getDefaultInstance();
        long results = mRealm.where(MessageRealm.class).equalTo("message_type", true).equalTo("message_read", false).count();
        if (message != null) {
//            if (!(new ConnectionDetector(this).isConnectingToInternet())) {

            if (!message.getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
//                    intent = new Intent(this, ConfessionsActivity.class);
//                    if (results > 1) {
//                        intent = new Intent(this, HomeActivity.class);
//                    } else {
                if (results > 1) {
                    intent = new Intent(this, ConfessionsActivity.class);
                } else {
                    intent = new Intent(this, ConfessionsChatActivity.class);
                }

                SharedPreferences.Editor editor = this.getSharedPreferences(this.getPackageName(),
                        Context.MODE_PRIVATE).edit();
                editor.putString(Config.DELETED_CONFESSION, message.getMessage_id());
                editor.apply();

                intent.putExtra(Config.COMING_FROM, true);
                intent.putExtra(Config.CONFESSION_TYPE, confessionType);
                intent.putExtra(Config.MESSAGE_ID, message.getMessage_id());
                intent.putExtra(Config.FULL_NAME, message.getSender_appname());
                sendNotification("New Confession Received", intent);
                setBadge(this);

            }
//            }
        }
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {

    }
}
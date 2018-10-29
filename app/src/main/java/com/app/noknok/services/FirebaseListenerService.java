package com.app.noknok.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.noknok.definitions.Config;
import com.app.noknok.models.MessageRealm;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by dev on 11/8/17.
 */

public class FirebaseListenerService extends Service {

    Context mContext;

    public static RemovedConfesListenersInterface mDeleteConfesListenersInterface;
    ArrayList<ChildEventListener> mDeleteConfesListenerList = new ArrayList<>();
    Realm mRealm;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDeleteConfReference = firebaseDatabase.getReference("RemovedConfessions").getRef();
    ChildEventListener mDeleteConfesListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.d("FIREBASESERVICE", "inside onchild added");
            String messageId = dataSnapshot.getKey();
            String confessionType;

            if (checkIfMessageExists(messageId)) {

                Log.d("FIREBASESERVICE", "messag deleted");

                mRealm.beginTransaction();
                RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class)
                        .equalTo("message_id", messageId).findAll();
                confessionType = messageRealm.get(0).getConfession_type();
                messageRealm.deleteFirstFromRealm();
                mRealm.commitTransaction();



        //        mDeleteConfesListenersInterface.onMessageRemoved(messageId, confessionType);
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

    public void setDeleteConfesListener(String userId) {
        mRealm = Realm.getDefaultInstance();
        mDeleteConfReference.orderByChild(userId).equalTo(userId).addChildEventListener(mDeleteConfesListener);
        mDeleteConfesListenerList.add(mDeleteConfesListener);
    }

    private boolean checkIfMessageExists(String message_id) {
        mRealm = Realm.getDefaultInstance();
        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
                .equalTo("message_id", message_id);
        return query.count() != 0;
    }

    public void removeDeleteConfesListeners() {

        for (int i = 0; i < mDeleteConfesListenerList.size(); i++) {

            if (mDeleteConfesListenerList.get(i) != null && mDeleteConfReference != null) {
                mDeleteConfReference.removeEventListener(mDeleteConfesListenerList.get(i));
                mDeleteConfesListenerList.remove(mDeleteConfesListenerList.get(i));
            }

        }

    }

    public void setmDeleteConfesListenersInterface(RemovedConfesListenersInterface listenersInterface) {
        mDeleteConfesListenersInterface = listenersInterface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FIREBASESERVICE", "intent");
       // if (intent != null) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        String userId = sharedPreferences.getString(Config.USER_ID,"");

            Log.d("FIREBASESERVICE", "intent: "+userId);
            setDeleteConfesListener(userId);


        //}

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public interface RemovedConfesListenersInterface {
        void onMessageRemoved(String messageId, String confessionType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeDeleteConfesListeners();
    }
}

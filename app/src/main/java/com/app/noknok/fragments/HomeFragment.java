package com.app.noknok.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.ConfessionsActivity;
import com.app.noknok.activities.FriendsListActivity;
import com.app.noknok.activities.HomeActivity;
import com.app.noknok.activities.MessageActivity;
import com.app.noknok.activities.SettingsActivity;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.ConfessionTypeDialog;
import com.app.noknok.dialogs.OnBoardTipsDialog;
import com.app.noknok.dialogs.RecordAudioTypeDialog;
import com.app.noknok.interfaces.AsyncResponse;
import com.app.noknok.interfaces.ChatDialogueInterface;
import com.app.noknok.interfaces.ConfesFragmentRefreshInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.ConfessionsContact;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.services.GetContacts;
import com.app.noknok.services.LocationUpdaterService;
import com.app.noknok.utils.ConnectionDetector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.app.noknok.services.FirebaseNotificationService.setBadge;

/**
 * Created by dev on 20/6/17.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        MessagesFragRefreshInterface, ChatDialogueInterface,
        ConfesFragmentRefreshInterface {

    private static final int REQUEST_LOCATION_PERMISSIONS = 102;
    private static final int REQUEST_PERMISSIONS = 101;
    private static final String TAG = "HOMEFRAGMENTTAG";
    private static final int RECORD_AUDIO_DIALOG = 112;
    public static Boolean mPermission = false;
    public static Boolean mLocationPermission = false;
    public static HomeFragment mHomeFragment;
    public static Boolean mRecordTypeDialogOpen = true;

    LinearLayout ivLogo, llYellowBackgroundHeader, llMainHeaderContainer,
            llRightGuessFriendsContainer, llRightGuessContainer,
            llFriendsContainer, llMicContainer, llMessagesCountContainer,
            llConfessionsCountContainer;
    RelativeLayout rlHomeMainContainer;
    ImageView ivSettings, ivMessages, ivConfessions, ivMic;
    TextView tvName, tvNameSurname, tvRightGuessCount, tvRightGuessText, tvFriendsCount,
            tvFriendsText, tvMessagesCount, tvMessagesText,
            tvConfessionsCount, tvConfessionsText, tvLogoText;
    View mView;
    String mContacts;
    Realm mRealm;
    ContentObserver contentObserver;
    String mUserId;
    SharedPreferences mSharedPreferences;
    ConfesFragmentRefreshInterface confesFragmentRefreshInterface;
    String mConfessionType;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messageReference = firebaseDatabase.getReference("Messages").getRef();
    DatabaseReference confessionsReference = firebaseDatabase.getReference("Confessions").getRef();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mRealm = Realm.getDefaultInstance();

        mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
        mUserId = mSharedPreferences.getString(Config.USER_ID, "");

        mHomeFragment = this;
        initUI();
        permissionCheck();
        locationPermissionCheck();
        setConfessionType();
        contentObserver = new MyContentObserver(new Handler());
        getActivity().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);

        initListeners();

        BaseClass.mBaseClass.setHomeMessagesListener(this);
        BaseClass.mBaseClass.setHomeChatDialogueListener(this);
        BaseClass.mBaseClass.setHomeConfessionsListener(this);
        deleteConfessionAfterHours();

        return mView;
    }

    @Override
    public void onResume() {

        super.onResume();
        resetBadgeCount();

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mPermission = true;
            //  getContacts();
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
        }

        long unreadMessageCount = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", true).notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        tvConfessionsCount.setText(String.valueOf(unreadMessageCount));
        tvRightGuessCount.setText(sp.getString(Config.RIGHT_GUESS_COUNT, "0").toString());
//        long friendsCount=mRealm.where(FriendsModelRealm.class).count();
//        tvFriendsCount.setText((int) friendsCount);

//        mUnreadMessageCount = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type",false).notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        deleteConfessionAfterHours();
//        tvMessagesCount.setText(String.valueOf(mUnreadMessageCount));
    }

    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission
                            .READ_CONTACTS},
                    REQUEST_PERMISSIONS);
        } else {
            mPermission = true;
            getContacts();
        }
    }

    public void locationPermissionCheck() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission
                            .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS);

        } else {
            if (!isMyServiceRunning(LocationUpdaterService.class)) {
                getActivity().startService(new Intent(getActivity(), LocationUpdaterService.class));
            }
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void getContacts() {

        new GetContacts(getActivity(), new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("responsss", output);
                mContacts = output;
                if (!sp.getBoolean(Config.SESSION_EXPIRE, false)) {
                    loadUserDetail();
                    getFriends();
                }
            }
        }).execute();

    }

    private void loadUserDetail() {

        String access_token = sp.getString(Config.ACCESS_TOKEN, "");

        if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.get_user_detail(access_token, mContacts, new Callback<RetroHelper.GetUserDetail>() {
                @Override
                public void success(RetroHelper.GetUserDetail getUserDetail, Response response) {
                    //   LoadingDialog.getLoader().dismissLoader();
                    if (getUserDetail.response != null) {

//                        tvFriendsCount.setText(getUserDetail.response.friends_count);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Config.RIGHT_GUESS_COUNT, getUserDetail.response.right_guesses_count);
                        editor.apply();

                        tvRightGuessCount.setText(getUserDetail.response.right_guesses_count);
//                        tvMessagesCount.setText(getUserDetail.response.new_messages_count);
//                        tvConfessionsCount.setText(getUserDetail.response.confession_count);
                    } else {
                        ServerAPIs.showServerErrorSnackbar(rlHomeMainContainer, getUserDetail.error);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
//                    ServerAPIs.noInternetConnection(rlHomeMainContainer);
                }
            });
        } else {
            ServerAPIs.noInternetConnection(rlHomeMainContainer);
        }
    }

    private void initListeners() {

        llFriendsContainer.setOnClickListener(this);
        ivMic.setOnClickListener(this);
        ivSettings.setOnClickListener(this);
        ivMessages.setOnClickListener(this);
        llRightGuessContainer.setOnClickListener(this);
        ivConfessions.setOnClickListener(this);

    }

    private void initUI() {


        rlHomeMainContainer = (RelativeLayout) mView.findViewById(R.id.rl_home_main_container);
        llYellowBackgroundHeader = (LinearLayout) mView.findViewById(R.id.ll_yellow_background_header);
        tvLogoText = (TextView) mView.findViewById(R.id.tv_home_logo);
        llMainHeaderContainer = (LinearLayout) mView.findViewById(R.id.ll_main_header_container);
        llRightGuessFriendsContainer = (LinearLayout) mView.findViewById(R.id.ll_rightguess_friends_container);
        llRightGuessContainer = (LinearLayout) mView.findViewById(R.id.ll_right_guess_container);
        llFriendsContainer = (LinearLayout) mView.findViewById(R.id.ll_friends_container);
        llMicContainer = (LinearLayout) mView.findViewById(R.id.ll_mic_container);
        ivLogo = (LinearLayout) mView.findViewById(R.id.iv_logo);
        ivSettings = (ImageView) mView.findViewById(R.id.iv_settings);
        ivMessages = (ImageView) mView.findViewById(R.id.iv_messages);
        ivConfessions = (ImageView) mView.findViewById(R.id.iv_confessions);
        llMessagesCountContainer = (LinearLayout) mView.findViewById(R.id.ll_messages_count_container);
        llConfessionsCountContainer = (LinearLayout) mView.findViewById(R.id.ll_confessions_count_container);
        ivMic = (ImageView) mView.findViewById(R.id.iv_mic);
        tvName = (TextView) mView.findViewById(R.id.tv_name);
        tvNameSurname = (TextView) mView.findViewById(R.id.tv_name_2);
        tvRightGuessCount = (TextView) mView.findViewById(R.id.tv_right_guess_count);
        tvRightGuessText = (TextView) mView.findViewById(R.id.tv_right_guess_text);
        tvFriendsCount = (TextView) mView.findViewById(R.id.tv_friends_count);
        tvFriendsText = (TextView) mView.findViewById(R.id.tv_friends_text);
        tvMessagesCount = (TextView) mView.findViewById(R.id.tv_messages_count);
        tvMessagesText = (TextView) mView.findViewById(R.id.tv_messages_text);
        tvConfessionsCount = (TextView) mView.findViewById(R.id.tv_confessions_count);
        tvConfessionsText = (TextView) mView.findViewById(R.id.tv_confessions_text);

        float boldTextSize = (int) (mScreenwidth * 0.050);
        float regularTextSize = (int) (mScreenwidth * 0.035);

        tvLogoText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mScreenwidth * 0.035));

        RelativeLayout.LayoutParams yellowBackgroundHeaderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                mScreenheight / 3);
        llYellowBackgroundHeader.setLayoutParams(yellowBackgroundHeaderParams);

        llMainHeaderContainer.setPadding((int) (mScreenwidth * 0.05), (int) (mScreenheight * 0.025),
                (int) (mScreenwidth * 0.05), 0);


        tvRightGuessCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        tvRightGuessText.setTextSize(TypedValue.COMPLEX_UNIT_PX, regularTextSize);
        tvFriendsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        tvFriendsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, regularTextSize);


        final ViewTreeObserver observer = llMicContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int micContainerHeight = (int) (llMicContainer.getHeight() * 0.7);
                        LinearLayout.LayoutParams micParams = new LinearLayout.LayoutParams(micContainerHeight, micContainerHeight);
                        ivMic.setLayoutParams(micParams);

                    }
                });

        int countTextSize = (int) (mScreenheight * 0.1);
        Log.d("MEASURE", "countextsie: " + countTextSize);
        tvMessagesCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        tvConfessionsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize);
        tvMessagesText.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);
        tvConfessionsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);

        String[] userName = sp.getString(Config.FULL_NAME, "").split(" ");

        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);
        tvName.setText(userName[0]);

        if (userName.length > 1) {
            try {
                tvNameSurname.setTextSize(TypedValue.COMPLEX_UNIT_PX, countTextSize / 3);
                tvNameSurname.setText(userName[1]);
            } catch (Exception e) {
                Log.d("USERNNMA0", userName[1]);
            }
        } else {

            LinearLayout.LayoutParams textNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textNameParams.setMargins(0, mScreenheight / 60, 0, 0);
            tvName.setLayoutParams(textNameParams);
            tvNameSurname.setVisibility(View.GONE);

        }
        int logoHeight = mScreenwidth / 8;
        RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(logoHeight, logoHeight);
        logoParams.setMargins(0, 0, logoHeight / 5, 0);
        ivLogo.setLayoutParams(logoParams);


        int settingsHeight = (int) (logoHeight * 0.8);
        LinearLayout.LayoutParams settingsParams = new LinearLayout.LayoutParams(settingsHeight, settingsHeight);
        ivSettings.setLayoutParams(settingsParams);

        llMessagesCountContainer.setPadding((int) (mScreenwidth * 0.18), 0, 0, 0);
        llConfessionsCountContainer.setPadding((int) (mScreenwidth * 0.18), 0, 0, 0);


        int paddingForMicContainer = (int) (mScreenwidth * 0.02);
        llMicContainer.setPadding(paddingForMicContainer, paddingForMicContainer,
                paddingForMicContainer, paddingForMicContainer);

        LinearLayout.LayoutParams rightGuessFriendParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int margin = (int) (mScreenwidth * 0.03);
        rightGuessFriendParams.setMargins(margin, margin, margin, margin);
        rightGuessFriendParams.gravity = Gravity.CENTER;
        rightGuessFriendParams.weight = 1;
        llRightGuessContainer.setLayoutParams(rightGuessFriendParams);
        llFriendsContainer.setLayoutParams(rightGuessFriendParams);


//
//        final ViewTreeObserver observer1 = llRightGuessFriendsContainer.getViewTreeObserver();
//        observer1.addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        int guessFriendsContainer = (int) (llRightGuessFriendsContainer.getHeight() / 2.5);
//                        llRightGuessFriendsContainer.setPadding(0, guessFriendsContainer / 2, 0, guessFriendsContainer / 2);
//
//                    }
//
//                });


        if (sp.getBoolean(Config.FIRSTLOGIN, false))
            new OnBoardTipsDialog(getActivity()).showDialog(mScreenheight, mScreenwidth, sp.getString(Config.FULL_NAME, ""));
        getConfessionsCount();
        getMessagesCount();
    }

    public void deleteConfessionAfterHours() {

        RealmResults<MessageRealm> realResult = mRealm.where(MessageRealm.class).equalTo("message_type", true).findAll();
        if (!realResult.equals(null)) {
            for (final MessageRealm messageRealm : realResult) {
                final String messageid = messageRealm.getMessage_id();

                // 24 hrs=86400000
                // 1 hr =3600000

                if ((getNetworkTime() - Long.parseLong(messageRealm.getMessage_time())) > 86400000) {

                    if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
//                        LoadingDialog.getLoader().showLoader(getActivity());
                        confessionsReference.child(messageid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                messageReference.child(messageid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(getActivity(), "Confession Deleted by time", Toast.LENGTH_SHORT).show();
//                                        LoadingDialog.getLoader().dismissLoader();
                                    }
                                });
                            }
                        });

                    }
                }
            }
        }
    }

    public long getNetworkTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time = cal.getTimeInMillis();

        return time;

    }

//    void getConfessions() {
//
//        FirebaseListeners.ConfessionsListenersInterface confessionsListenersInterface = new FirebaseListeners.ConfessionsListenersInterface() {
//            @Override
//            public void onMessageAdd(Message message, String confessionType) {
//                Log.d(TAG, "new conefssion received: " + message.getMessage_id());
//
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString(Config.CONFESSION_TYPE, confessionType);
//                editor.apply();
//
//                setConfessionType();
//
////                confessionMessageIdList.add(message.getMessage_id());
//
//
//                if (confesFragmentRefreshInterface != null) {
//                    confesFragmentRefreshInterface.addMessage(message, confessionType);
//                }
//
//                getConfessionsCount();
//
//
//            }
//
//            @Override
//            public void onMessageRemove(Message message, String confessionType) {
//
////                if (checkIfMessageExists(message.getMessage_id())) {
////                    mRealm.beginTransaction();
////                    RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", message.getMessage_id()).findAll();
////                    messageRealm.deleteFirstFromRealm();
////                    mRealm.commitTransaction();
//
//                //  Log.d(TAG, "message remove in confessiona activity from realm");
//
//                if (confesFragmentRefreshInterface != null) {
//                    confesFragmentRefreshInterface.removeMessage(message, confessionType);
//                }
//
//                getConfessionsCount();
//
////                }
//            }
//        };
//
//
//        FirebaseListeners.RemovedConfesListenersInterface removedConfesListenersInterface = new FirebaseListeners.RemovedConfesListenersInterface() {
//            @Override
//            public void onMessageRemoved(String messageId, String confessionType) {
//
//                if (confesFragmentRefreshInterface != null) {
//                    confesFragmentRefreshInterface.removedMessage(messageId, confessionType);
//                }
//
//
//                getConfessionsCount();
//
//                //   removedMessageIdList.add(messageId);
//
//            }
//        };
//
//        FirebaseListeners.getListenerClass(mContext).setConfessionsListener(confessionsListenersInterface);
//        FirebaseListeners.getListenerClass(mContext).setConfessionListener(sp.getString(Config.USER_ID, ""));
//
//        FirebaseListeners.getListenerClass(mContext).setRemoveConfesListener(sp.getString(Config.USER_ID, ""));
//        FirebaseListeners.getListenerClass(mContext).setmRemovedConfesListenersInterface(removedConfesListenersInterface);
//
//
//    }

    private void getConfessionsCount() {
        mRealm = Realm.getDefaultInstance();
        long unreadMessageCount = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", true).notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        tvConfessionsCount.setText(String.valueOf(unreadMessageCount));
    }

//    public void receiveMessages() {
//
//        FirebaseListeners.MessageListenerInterface messageListenerInterface = new FirebaseListeners.MessageListenerInterface() {
//            @Override
//            public void onMessageAdd(Message message) {
//
////                if (!messageIdList.contains(message.getChat_dialogue_id()))
////                    messageIdList.add(message.getChat_dialogue_id());
//
//                if (messagesFragRefreshInterface != null) {
//                    messagesFragRefreshInterface.addMessage();
//                }
//                getMessagesCount();
//
//            }
//
//            @Override
//            public void onMessageChange(Message message) {
//                Log.d("MessageCheck", "in homefragment onchange");
//
//                if (messagesFragRefreshInterface != null) {
//                    messagesFragRefreshInterface.onMessageChange(message);
//                }
//                getMessagesCount();
//            }
//        };
//
//
//        ChatDialogueInterface chatDialogueInterface = new ChatDialogueInterface() {
//            @Override
//            public void onUpdate() {
//                Log.d("CHATDIALOGUEUPDATE","in homefragment onupdate");
//                if(mChatDialogueInterface !=null){
//                    mChatDialogueInterface.onUpdate();
//                }
//            }
//        };
//        FirebaseListeners.getListenerClass(mContext).setProfileChatDialogueListener(sp.getString(Config.USER_ID, ""));
//        FirebaseListeners.getListenerClass(mContext).setMessagesListenerInterface(messageListenerInterface);
//        FirebaseListeners.getListenerClass(mContext).setmChatDialogueInterface(chatDialogueInterface);
//    }

    private void getMessagesCount() {

        long unreadMessageCount = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", false).notEqualTo("sender_id", sp.getString(Config.USER_ID, "")).count();
        tvMessagesCount.setText(String.valueOf(unreadMessageCount));
    }


    @Override
    public void onClick(View v) {

        if (mPermission && mLocationPermission) {
            switch (v.getId()) {
                case R.id.ll_friends_container:
                    Intent intent = new Intent(getActivity(), FriendsListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    break;

                case R.id.iv_mic:
//                    new RecordAudioTypeDialog((HomeActivity)this).getActivity()).showmDialog();
                    if (mRecordTypeDialogOpen) {
                        mRecordTypeDialogOpen = false;
                        startActivityForResult(new Intent(getActivity(), RecordAudioTypeDialog.class), RECORD_AUDIO_DIALOG);
                    }
                    break;

                case R.id.iv_settings:
                    // new LogoutDialog(getActivity()).showDialog();
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;

                case R.id.iv_messages:
                    startActivity(new Intent(getActivity(), MessageActivity.class));
                    break;

                case R.id.ll_right_guess_container:
//                    Toast.makeText(getActivity(), "work in progress", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.iv_confessions:
                    Intent intent1 = new Intent(getActivity(), ConfessionsActivity.class);
                    intent1.putExtra(Config.CONFESSION_TYPE, mConfessionType);
                    startActivity(intent1);
                    break;
            }
        } else {
//            permissionCheck();
            ((HomeActivity) getActivity()).snackView();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RECORD_AUDIO_DIALOG:
                    String confessionContact = data.getStringExtra("model");
                    ArrayList<ConfessionsContact> list = new Gson().fromJson(confessionContact, new TypeToken<ArrayList<ConfessionsContact>>() {
                    }.getType());

                    new ConfessionTypeDialog(getActivity(), list).showDialog();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    void setConfessionType() {
        mConfessionType = sp.getString(Config.CONFESSION_TYPE, "");
    }

    private void getFriends() {

        Log.d("mCONTACTSfrnds", mContacts);
        if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
            //   LoadingDialog.getLoader().showLoader(getActivity());
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
            final RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.get_friends_list(sp.getString(Config.ACCESS_TOKEN, ""), mContacts, new Callback<RetroHelper.GetFriendsList>() {
                @Override
                public void success(RetroHelper.GetFriendsList getfriends, Response response) {
                    if (getfriends.response != null) {
                        mRealm.beginTransaction();
                        mRealm.delete(FriendsModelRealm.class);
                        mRealm.commitTransaction();
                        for (int i = 0; i < getfriends.response.size(); i++) {

                            RetroHelper.GetFriendsList.Response res = getfriends.response.get(i);
                            if (!checkIfFriendsExists(res.user_id)) {

                                mRealm.beginTransaction();
                                FriendsModelRealm friendsModelRealm = mRealm.createObject(FriendsModelRealm.class);
                                friendsModelRealm.setUser_id(res.user_id);
                                friendsModelRealm.setName_guessed(res.name_guessed);
                                friendsModelRealm.setName_on_app(res.name_on_app);
                                friendsModelRealm.setRight_guess(res.right_guess);
                                friendsModelRealm.setNumber(res.number);
                                friendsModelRealm.setName_in_contact(res.name_in_contact);
                                mRealm.commitTransaction();

                                Log.d("FRIENDSRESPONSE", String.valueOf(getfriends.response));

                                Log.d("FRIENDSRESPONSE", String.valueOf(res.user_id + "" + res.name_guessed + "" + res.name_on_app + "" + res.right_guess + "" + res.number + "" + res.name_in_contact));

                            }
                        }


                        tvFriendsCount.setText(getfriends.response.size() + "");
                        BaseClass.mBaseClass.receiveMessages();
                        BaseClass.mBaseClass.getConfessions();
                        //    LoadingDialog.getLoader().dismissLoader();
                        //receiveMessages();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    //  LoadingDialog.getLoader().dismissLoader();
                    ServerAPIs.showServerErrorSnackbar(rlHomeMainContainer, error.getMessage());
                    error.printStackTrace();
                }
            });
        } else {
            ServerAPIs.noInternetConnection(rlHomeMainContainer);
        }
    }

    private boolean checkIfFriendsExists(String user_id) {
        RealmQuery<FriendsModelRealm> query = mRealm.where(FriendsModelRealm.class)
                .equalTo("user_id", user_id);
        return query.count() != 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(contentObserver);

//        if (FirebaseListeners.getListenerClass(mContext) != null) {
//            FirebaseListeners.getListenerClass(mContext).removeConfessionListener(mUserId);
//            FirebaseListeners.getListenerClass(mContext).removeRemovedConfesListeners();
////            for (int i = 0; i < confessionMessageIdList.size(); i++) {
//                FirebaseListeners.getListenerClass(mContext).removeConfesMessageListener();
////            }
//
////            for (int i = 0; i < messageIdList.size(); i++) {
//                FirebaseListeners.getListenerClass(mContext).removeChatMessageListener();
////            }
//
//        }

    }

//    public void setConfessionsListener(ConfessionsActivity con) {
//        confesFragmentRefreshInterface = con;
//    }
//
//    public void setMessagesListener(MessageActivity con) {
//        messagesFragRefreshInterface = con;
//    }
//
//    public void setChatDialogueListener(MessageActivity con){
//        mChatDialogueInterface = con;
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }

    @Override
    public void addMessage(Message message) {
        getMessagesCount();
    }

    @Override
    public void onMessageChange(Message message) {
        getMessagesCount();
    }

    @Override
    public void onUpdate() {
        getMessagesCount();
    }

    public void resetBadgeCount() {
        String access_token = sp.getString(Config.ACCESS_TOKEN, "");
        int badgeCount = Integer.parseInt(sp.getInt(Config.BADGE, 0)+"");
        if (badgeCount > 0) {
            if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
                RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
                RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
                mRetroInterface.reset_badge_count(access_token, new Callback<RetroHelper.ResetBadgeCount>() {
                    @Override
                    public void success(RetroHelper.ResetBadgeCount resetBadgeCount, Response response) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(Config.BADGE, 0);
                        ShortcutBadger.applyCount(getActivity(), 0);
                        editor.apply();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            } else {
                ServerAPIs.noInternetConnection(rlHomeMainContainer);
            }
        }
    }

    @Override
    public void addMessage(Message message, String confessionType) {
        Log.d(TAG, "new conefssion received: " + message.getMessage_id());

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Config.CONFESSION_TYPE, confessionType);
        editor.apply();

        setConfessionType();

//                confessionMessageIdList.add(message.getMessage_id());


        if (confesFragmentRefreshInterface != null) {
            confesFragmentRefreshInterface.addMessage(message, confessionType);
        }

        getConfessionsCount();


    }

    @Override
    public void onPause() {
        super.onPause();
        resetBadgeCount();
    }

    @Override
    public void removeMessage(Message message, String confessionType) {

//                if (checkIfMessageExists(message.getMessage_id())) {
//                    mRealm.beginTransaction();
//                    RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", message.getMessage_id()).findAll();
//                    messageRealm.deleteFirstFromRealm();
//                    mRealm.commitTransaction();

        //  Log.d(TAG, "message remove in confessiona activity from realm");

        if (confesFragmentRefreshInterface != null) {
            confesFragmentRefreshInterface.removeMessage(message, confessionType);
        }

        getConfessionsCount();

//                }
    }

    @Override
    public void removedMessage(String messageId, String confessionType) {

        if (confesFragmentRefreshInterface != null) {
            confesFragmentRefreshInterface.removedMessage(messageId, confessionType);
        }


        getConfessionsCount();

        //   removedMessageIdList.add(messageId);

    }

//    @Override
//    public void removeMessage(Message message, String confessionType) {
//
//    }
//
//    @Override
//    public void addMessage(Message message, String confessionType) {
//
//    }
//
//    @Override
//    public void removedMessage(String messageId, String confessionType) {
//
//    }

    public class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            permissionCheck();
            Log.d("contactobserver", String.valueOf(selfChange));
        }
    }
}
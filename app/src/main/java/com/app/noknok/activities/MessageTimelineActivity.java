package com.app.noknok.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.adapters.MessageTimeLineAdapter;
import com.app.noknok.definitions.BaseClass;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.GuessNoticeDialog;
import com.app.noknok.dialogs.NewAudioRecordingDialog;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.interfaces.AsyncResponse;
import com.app.noknok.interfaces.MessageTimelineInterface;
import com.app.noknok.interfaces.MessagesFragRefreshInterface;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.services.GetContacts;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smp.soundtouchandroid.AudioDuration;
import com.smp.soundtouchandroid.OnProgressChangedListener;
import com.smp.soundtouchandroid.SoundStreamAudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;
import pl.droidsonroids.gif.GifImageView;
import rm.com.audiowave.AudioWaveView;

import static android.view.View.GONE;
import static com.app.noknok.fragments.NewMessagesFragment.fixRandomName;
import static com.app.noknok.services.FirebaseNotificationService.cancelNotification;

/**
 * Created by dev on 14/7/17.
 */


public class MessageTimelineActivity extends BaseActivity implements View.OnClickListener, MessagesFragRefreshInterface {

    private static final String TAG = "MESSAGETIMELINETAG";
    private static final int REQUEST_PERMISSIONS = 101;
    private static final int GUESS_REQUEST_CODE = 104;
    public static Activity mcActivity;
    public static int mAudioIndex = 0;
    SessionExpireDialog sessionExpireDialog;
    RecyclerView rvMessagesRecyclerView;
    ArrayList<MessageRealm> mMessageList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    MessageTimeLineAdapter messageTimeLineAdapter;
    LinearLayout llAudioWaveContainer, llAudioButtonContainer, llClose, llGuess, llMainContainer;
    ImageView ivReply, ivPrevious, ivNext, ivPlayPause;
    GifImageView pbProgressBar;
    AudioWaveView waveView;
    TextView tvName, tvConversationWith;
    String mChatDialogueId, mSenderName, mMessageType, mRecieverId;
    ImageView imgMessageThreadCartoon;
    LinearLayout llMessageTimelineFirstContainer;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference, mChatReference;
    StorageReference mStorageReference;
    FirebaseStorage mFirebaseStorage;
    String mAudioFilePath;
    boolean isPlaying = false;
    boolean mChecKStoragePermission = false;
    int mMessageColor, mMessageIcon;
    long mLastGuessTime = 0;
    String mGuessStatus;
    RandomIllustrator randomIllustrator = new RandomIllustrator();
    InputStream inputStream;
    long mTotalDuration, mCurrentDuration;
    SoundStreamAudioPlayer soundStreamAudioPlayer;
    AudioDuration audioDuration;
    MessageTimelineInterface messageTimelineInterface;
    File path;
    float rate = 1.0f;
    float defaultPitch = 0f;
    Realm mRealm;
    int mTotalMessages = 0;

    ObjectAnimator mProgressAnim;

    boolean showToast = false;
    boolean isReplying = false;

    Handler mHandler = new Handler();
    RealmResults<MessageRealm> realmResults;
    boolean comingFromNotification = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isPlaying = false;
            ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
        }
    };

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_timeline);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRealm = Realm.getDefaultInstance();
        if (getIntent().getBooleanExtra(Config.COMING_FROM, false)) {
            comingFromNotification = true;
        }
        mcActivity = this;
        mcActivity = this;
        cancelNotification(this);

        BaseClass.mBaseClass.setMessageTimelineListener(this);

        if (!comingFromNotification) {

            mChatDialogueId = getIntent().getStringExtra(Config.CHAT_DIALOGUE_ID);
            String s = getIntent().getStringExtra(Config.FULL_NAME);

            if (GetContacts.mContactMap.containsKey(s)) {
                mSenderName = GetContacts.mContactMap.get(s);
            } else {
                mSenderName = s;
            }
            mMessageType = getIntent().getStringExtra(Config.MESSAGE_TYPE);
            mRecieverId = getIntent().getStringExtra(Config.RECEIVER_ID);
            mMessageColor = Integer.parseInt(getIntent().getStringExtra(Config.MESSAGE_COLOR));
            mMessageIcon = Integer.parseInt(getIntent().getStringExtra(Config.MESSAGE_ICON));

        } else {
            String chatDialogueId = getIntent().getStringExtra(Config.CHAT_DIALOGUE_ID);
            String messageId = getIntent().getStringExtra(Config.MESSAGE_ID);
            getNofificationMessage(chatDialogueId, messageId);
        }

        BaseClass.currentChatDialogue = mChatDialogueId;
        BaseClass.currentMessageType = mMessageType;

        mDatabase = FirebaseDatabase.getInstance();
        mChatReference = mDatabase.getReference("Chats").getRef();
        mDatabaseReference = mDatabase.getReference("Messages");
        mFirebaseStorage = FirebaseStorage.getInstance();
        getGuessTimeAndStatus();

        getNetworkTime();
        initUI();

        initListeners();

    }

    public void getContacts() {
        new GetContacts(this, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("responsss", output);

            }
        }).execute();
    }

    private void initListeners() {
        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
        llClose.setOnClickListener(this);
        ivReply.setOnClickListener(this);
        llGuess.setOnClickListener(this);
    }


    private void initUI() {

        ivPrevious = (ImageView) findViewById(R.id.iv_message_timeline_previous);
        ivNext = (ImageView) findViewById(R.id.iv_message_timeline_next);
        ivPlayPause = (ImageView) findViewById(R.id.iv_message_timeline_pause);
        pbProgressBar = (GifImageView) findViewById(R.id.pb_message_timeline_progress);

        ivReply = (ImageView) findViewById(R.id.iv_message_timeline_reply);
        waveView = (AudioWaveView) findViewById(R.id.aw_message_timeline_wave);
        llAudioWaveContainer = (LinearLayout) findViewById(R.id.ll_message_thread_musicbt_audiowave_container);
        llAudioButtonContainer = (LinearLayout) findViewById(R.id.ll_message_thread_music_button_container);
        rvMessagesRecyclerView = (RecyclerView) findViewById(R.id.rv_message_timeline_messages);
        tvName = (TextView) findViewById(R.id.tv_message_timeline_name);
        tvConversationWith = (TextView) findViewById(R.id.txt_message_thread_Conversation_with);
        llClose = (LinearLayout) findViewById(R.id.ll_messages_timeline_close);
        llGuess = (LinearLayout) findViewById(R.id.ll_messages_timeline_guess);
        llMainContainer = (LinearLayout) findViewById(R.id.ll_activity_messages_timeline_main_container);
        ivNext.setVisibility(View.INVISIBLE);

        imgMessageThreadCartoon = (ImageView) findViewById(R.id.img_message_thread_cartoon);
        llMessageTimelineFirstContainer = (LinearLayout) findViewById(R.id.ll_message_timeline_first_container);
        RandomColor randomColor = new RandomColor();
        GradientDrawable drawable = new GradientDrawable();
        int colors[] = {Color.parseColor(randomColor.getDrawable(mMessageColor).getGradient1()), Color.parseColor(randomColor.getDrawable(mMessageColor).getGradient2())};
        drawable.setColors(colors);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        llMessageTimelineFirstContainer.setBackgroundDrawable(drawable);
        imgMessageThreadCartoon.setImageResource(randomIllustrator.getDrawable(mMessageIcon));

        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenwidth / 15);
        tvConversationWith.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenwidth / 20);

        RelativeLayout.LayoutParams paramsmicReply =
                new RelativeLayout.LayoutParams(mScreenwidth / 5, mScreenwidth / 5);
        paramsmicReply.setMargins(0, 0, 0, mScreenheight / 60);
        paramsmicReply.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsmicReply.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ivReply.setLayoutParams(paramsmicReply);

        waveView.setTouched(false);

        messageTimelineInterface = new MessageTimelineInterface() {

            @Override
            public void itemClicked(int position, LinearLayout holder) {

                mAudioIndex = position;
                mHandler.removeCallbacks(runnable);
                isReplying = false;
                playByDefault();

                if (mMessageList.size() == 1) {

                    ivNext.setVisibility(View.INVISIBLE);
                    ivPrevious.setVisibility(View.INVISIBLE);
                } else {
                    if (mAudioIndex == (mMessageList.size() - 1)) {
                        ivNext.setVisibility(View.INVISIBLE);
                        ivPrevious.setVisibility(View.VISIBLE);
                    } else if (mAudioIndex == 0) {
                        ivPrevious.setVisibility(View.INVISIBLE);
                        ivNext.setVisibility(View.VISIBLE);
                    } else {
                        ivNext.setVisibility(View.VISIBLE);
                        ivPrevious.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        messageTimeLineAdapter = new MessageTimeLineAdapter(this, mMessageList, mSenderName, messageTimelineInterface);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rvMessagesRecyclerView.setLayoutManager(mLayoutManager);
        rvMessagesRecyclerView.setAdapter(messageTimeLineAdapter);

        tvName.setText(mSenderName);

        if (mMessageType.equals(Config.MESSAGE_TYPE_SENT)) {
            realmResults = mRealm.where(MessageRealm.class)
                    .equalTo("guess_status", "1").equalTo("message_type", false).equalTo("chat_dialogue_id", mChatDialogueId).findAll();
            llGuess.setVisibility(View.INVISIBLE);
            getMessages();

        } else if (mMessageType.equals(Config.MESSAGE_TYPE_RECEIVED)) {
            realmResults = mRealm.where(MessageRealm.class)
                    .equalTo("guess_status", "0").equalTo("message_type", false).equalTo("chat_dialogue_id", mChatDialogueId).findAll();
            getMessages();

        } else if (mMessageType.equals(Config.MESSAGE_TYPE_GUESSED)) {
            realmResults = mRealm.where(MessageRealm.class).equalTo("chat_dialogue_id", mChatDialogueId)
                    .equalTo("message_type", false).findAll();
            getMessages();
        }
        permissionCheck();
    }


    @Override
    public void onClick(View v) {
        if (mChecKStoragePermission) {
            switch (v.getId()) {
                case R.id.iv_message_timeline_previous:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;

                    if (isPlaying) {
                        soundStreamAudioPlayer.stop();
                    }

                    if (mProgressAnim != null && mProgressAnim.isStarted()) {
                        mProgressAnim.end();
                    }

                    isPlaying = false;

                    previousAudio();

                    break;
                case R.id.iv_message_timeline_next:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;

                    if (isPlaying) {
                        soundStreamAudioPlayer.stop();
                    }

                    if (mProgressAnim != null && mProgressAnim.isStarted()) {
                        mProgressAnim.end();
                    }

                    isPlaying = false;
                    nextAudio();
                    break;
                case R.id.iv_message_timeline_pause:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;
                    togglePlayPause();
                    break;
                case R.id.ll_messages_timeline_close:
                    onBackPressed();
                    break;

                case R.id.iv_message_timeline_reply:
                    replyMessage();
                    break;

                case R.id.ll_messages_timeline_guess:
                    getGuessTimeAndStatus();
                    if (mGuessStatus.equals("0")) {
                        if (getNetworkTime() >= (mLastGuessTime + 21600000)) {
                            Intent intent = new Intent(this, GuessActivity.class);
                            intent.putExtra(Config.RECEIVER_ID, mRecieverId);
                            intent.putExtra(Config.CHAT_DIALOGUE_ID, mChatDialogueId);
                            startActivityForResult(intent, GUESS_REQUEST_CODE);
                            break;
                        } else {
                            String time = "";
                            long diff = (mLastGuessTime + 21600000) - getNetworkTime();
                            int hours = (int) (diff / (1000 * 60 * 60));
                            int minutes = (int) (diff / (1000 * 60) % 60);
                            int seconds = (int) (diff / (1000) % 60);

                            if (hours != 0 && minutes != 0) {
                                time = hours + " hours and " + minutes + " minutes.";
                            } else if (hours == 0 && minutes != 0) {
                                time = minutes + " minutes.";
                            } else if (minutes == 0) {
                                time = seconds + " seconds.";
                            }


                            new GuessNoticeDialog(MessageTimelineActivity.this, 1, time).showDialog();
//                            Snackbar.make(llMainContainer, "New guess will be available after 6 hours of your previous guess.", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        llGuess.setVisibility(View.INVISIBLE);
                    }
            }
        } else {
            permissionCheck();
        }

    }

    private void replyMessage() {
        mHandler.removeCallbacks(runnable);
        isReplying = true;
        if (isPlaying) {
            soundStreamAudioPlayer.stop();
        }

        if (mProgressAnim != null && mProgressAnim.isStarted()) {
            mProgressAnim.end();

        }
        isPlaying = false;

        ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
        new NewAudioRecordingDialog(this, mChatDialogueId, mRecieverId, mMessageType).showDialog();
    }


    @Override
    public void onBackPressed() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        //  mRealm.removeAllChangeListeners();
        super.onBackPressed();


    }

    public void getMessages() {

        if (mMessageList != null) {
            mMessageList.clear();
        }

        for (MessageRealm messageRealm : realmResults) {
            mMessageList.add(messageRealm);
            messageTimeLineAdapter.notifyDataSetChanged();
        }

        rvMessagesRecyclerView.smoothScrollToPosition(mMessageList.size());

        //mTotalMessages = realmResults.size();

//        realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//
//                Log.d(TAG, "new message received in realm");
//
//                if (messageRealms.size() > mTotalMessages) {
//                    mMessageList.clear();
//
//                    for (MessageRealm messageRealm : messageRealms) {
//
//                        mMessageList.add(messageRealm);
//                        messageTimeLineAdapter.notifyDataSetChanged();
//
//                    }
//                    messageTimeLineAdapter.notifyDataSetChanged();
//                    mTotalMessages = messageRealms.size();
//
//                }
//            }
//        });

        mAudioIndex = mMessageList.size() - 1;


    }

    void playAudio() {

        Log.d(TAG, "playaudio: " + isPlaying);

        if (!isReplying) {

            if (mProgressAnim != null && mProgressAnim.isStarted())
                mProgressAnim.end();

            try {
                inputStream = new FileInputStream(mAudioFilePath);
                byte[] byt = convertStreamToByteArray(inputStream);
                waveView.setRawData(byt);
                waveView.setTouched(false);

            } catch (IOException e) {
                e.printStackTrace();
            }


            audioDuration = new AudioDuration() {
                @Override
                public void getTotalDuration(long totalDuration) {
                    mTotalDuration = totalDuration;
                    Log.d("DURFINAL", "total duration: " + totalDuration);
                }

                @Override
                public void getCurrentDuration(long currentDuration) {
                    //  mCurrentDuration = currentDuration;

                    //   Log.d("DURFINAL", "current duration: " + currentDuration);
                }

                @Override
                public void getAudioEndStatus(boolean status) {
                    //   Log.d("DURFINAL","audio ended" + status);
                }
            };


            if (isPlaying)
                soundStreamAudioPlayer.stop();

            try {
                soundStreamAudioPlayer = new SoundStreamAudioPlayer(0, mAudioFilePath, rate, defaultPitch, audioDuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (soundStreamAudioPlayer != null) {
                new Thread(soundStreamAudioPlayer).start();
                soundStreamAudioPlayer.start();
            }

            mProgressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0, 100F).setDuration((mTotalDuration / 1000));
            mProgressAnim.start();


            soundStreamAudioPlayer.setOnProgressChangedListener(new OnProgressChangedListener() {
                @Override
                public void onProgressChanged(int track, double currentPercentage, long position) {
                    Log.d("DURFINAL", "current percen: " + currentPercentage + ", position: " + position);
                }

                @Override
                public void onTrackEnd(int track) {
//                ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
//                isPlaying = false;
                    Log.d("DURFINAL", "track ended: " + track);
                    //  Toast.makeText(sessionExpireDialog, "track ended", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onExceptionThrown(String string) {

                }
            });

            // totalDuration = soundStreamAudioPlayer.getDuration();
            // Log.d("MUSICPOS", "musicduration: " + soundStreamAudioPlayer.getDuration());

            isPlaying = true;

            ivPlayPause.setImageResource(R.drawable.ic_pause_record_copy);
//        if (mAudioIndex < mMessageList.size()) {
//            ivNext.setVisibility(View.INVISIBLE);
//        } else {
//            ivNext.setVisibility(View.VISIBLE);
//        }

            pbProgressBar.setVisibility(GONE);
            ivPlayPause.setVisibility(View.VISIBLE);

            int Duration = (int) (soundStreamAudioPlayer.getDuration() / 1000);

            mHandler.postDelayed(runnable, Duration);

        }

    }

    void seekMusic(long time) {

        try {
            soundStreamAudioPlayer = new SoundStreamAudioPlayer(0, mAudioFilePath, rate, defaultPitch, audioDuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (soundStreamAudioPlayer != null) {
            new Thread(soundStreamAudioPlayer).start();
            soundStreamAudioPlayer.start();
        }

        soundStreamAudioPlayer.seekTo(time);

    }

    private void togglePlayPause() {
        if (isPlaying) {
            soundStreamAudioPlayer.stop();
            mHandler.removeCallbacks(runnable);
            if (mProgressAnim != null && mProgressAnim.isStarted()) {
                mProgressAnim.end();
            }

            ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);

            //     mCurrentDuration = soundStreamAudioPlayer.getPlayedDuration();
            //    soundStreamAudioPlayer.stop();
            //  Log.d("MUSICPOS", "paused duration: " + soundStreamAudioPlayer.getPlayedDuration());

            isPlaying = false;

        } else {
            ivPlayPause.setImageResource(R.drawable.ic_pause_record_copy);

            playAudio();
            //   Log.d("MUSICPOS","currentpos: "+currentDuration);
            //    seekMusic(mCurrentDuration);
            isPlaying = true;
        }
    }

    void previousAudio() {

        if (mAudioIndex > 0) {
            mAudioIndex--;
            playByDefault();
        }

        if (mAudioIndex == 0) {
            ivPrevious.setVisibility(View.INVISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        } else if (mAudioIndex == (mMessageList.size() - 1)) {
            ivNext.setVisibility(View.INVISIBLE);
            ivPrevious.setVisibility(View.VISIBLE);
        } else {
            ivPrevious.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        }
        messageTimeLineAdapter.notifyDataSetChanged();
        rvMessagesRecyclerView.scrollToPosition(mAudioIndex);
        Log.d(TAG, "previousaudio: " + mAudioIndex);
    }

    void nextAudio() {
        if (mMessageList.size() > 1) {
            if (mAudioIndex < mMessageList.size() - 1) {

                mAudioIndex++;

                playByDefault();
            }
        }
        if (mAudioIndex == 0) {
            ivPrevious.setVisibility(View.INVISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        } else if (mAudioIndex == (mMessageList.size() - 1)) {
            ivNext.setVisibility(View.INVISIBLE);
            ivPrevious.setVisibility(View.VISIBLE);
        } else {
            ivPrevious.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        }
        messageTimeLineAdapter.notifyDataSetChanged();
        rvMessagesRecyclerView.scrollToPosition(mAudioIndex);
        Log.d(TAG, "nextaudio: " + mAudioIndex);
    }

    void playByDefault() {

        if (mMessageList.size() == 1) {
            ivNext.setVisibility(View.INVISIBLE);
            ivPrevious.setVisibility(View.INVISIBLE);

        } else {
            if (mAudioIndex == (mMessageList.size() - 1)) {
                ivNext.setVisibility(View.INVISIBLE);
                ivPrevious.setVisibility(View.VISIBLE);
            } else if (mAudioIndex == 0) {
                ivPrevious.setVisibility(View.INVISIBLE);
                ivNext.setVisibility(View.VISIBLE);
            } else {
                ivNext.setVisibility(View.VISIBLE);
                ivPrevious.setVisibility(View.VISIBLE);
            }
        }

//        <<<<----- moved this realm method to New/Old-MessageFragment --------->>>>

//        RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class)
//                .equalTo("chat_dialogue_id", mChatDialogueId)
//                .equalTo("message_id", mMessageList.get(mAudioIndex).getMessage_id()).findAll();
//
//        mRealm.beginTransaction();
//
//        for (int i = 0; i < results.size(); i++) {
//            MessageRealm messageRealm = results.get(i);
//            messageRealm.setMessage_read(true);
//        }
//
//        mRealm.commitTransaction();

        mDatabaseReference.child(mMessageList.get(mAudioIndex).getMessage_id())
                .child("read_ids")
                .child(sp.getString(Config.USER_ID, ""))
                .setValue(sp.getString(Config.USER_ID, ""));
        Log.d("TIMELINECHECK", "read status changed");

        defaultPitch = Float.parseFloat(mMessageList.get(mAudioIndex).getAudio_pitch());
        if (defaultPitch != 0) {
            defaultPitch = defaultPitch / 100;
        }


        rate = Float.parseFloat(mMessageList.get(mAudioIndex).getAudio_rate());
        String localUrl = mMessageList.get(mAudioIndex).getLocal_url();


        Log.d(TAG, "local url: " + localUrl + ", download url: " + mMessageList.get(mAudioIndex).getAudio_url());
        if (localUrl == null || localUrl.equals("")) {
            if ((new ConnectionDetector(this).isConnectingToInternet())) {
                startdownload(mMessageList.get(mAudioIndex).getAudio_url(), mMessageList.get(mAudioIndex).getMessage_id());
            } else {
                ivPlayPause.setVisibility(View.VISIBLE);
                pbProgressBar.setVisibility(View.GONE);
                ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
                ServerAPIs.noInternetConnection(llMainContainer);
            }
        } else {
            mAudioFilePath = mMessageList.get(mAudioIndex).getLocal_url();
            File file = new File(mAudioFilePath);
            if (file.exists()) {
                playAudio();
            } else {
                startdownload(mMessageList.get(mAudioIndex).getAudio_url(), mMessageList.get(mAudioIndex).getMessage_id());
            }
        }

    }

    public void startdownload(String audio_url, final String messageId) {

        pbProgressBar.setVisibility(View.VISIBLE);
        ivPlayPause.setVisibility(View.GONE);

        mStorageReference = mFirebaseStorage.getReferenceFromUrl(audio_url);

        boolean isFileCreated = false;
        path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NOKNOK/Messages/");

        path.mkdirs();

        String filePath = mStorageReference.getName();
        final File downloadFile = new File(path, filePath);
        try {
            isFileCreated = downloadFile.createNewFile();

        } catch (IOException e) {
            Log.e("downloadfileerror", e.getMessage());
            isFileCreated = false;
        }

        if (showToast)
            Toast.makeText(this, "file created or not: " + isFileCreated, Toast.LENGTH_SHORT).show();

        if (isFileCreated) {
            mStorageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    if (showToast)
                        Toast.makeText(getApplicationContext(), "File Download Complete", Toast.LENGTH_SHORT).show();

                    RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class).equalTo("message_id", messageId).findAll();

                    mRealm.beginTransaction();

                    for (int i = 0; i < results.size(); i++) {
                        MessageRealm messageRealm = results.get(i);
                        messageRealm.setLocal_url(downloadFile.getAbsolutePath());
                    }

                    mRealm.commitTransaction();

                    pbProgressBar.setVisibility(GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                    if (isReplying)
                        ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
                    Log.d(TAG, "" + isReplying);

                    playByDefault();


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    pbProgressBar.setVisibility(GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                    if (showToast)
                        Toast.makeText(getApplicationContext(), "File Download Failed", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    void permissionCheck() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            playByDefault();
            mChecKStoragePermission = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    playByDefault();
                    mChecKStoragePermission = true;

                } else if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {

                        permissionCheck();

                    }
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    snakbarShow();
                }
                return;
            }
        }
    }


    public void snakbarShow() {

        Snackbar.make(findViewById(android.R.id.content), "Enable Storage Permissions from settings",
                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);


                    }
                }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GUESS_REQUEST_CODE && data != null) {
            if (data.getBooleanExtra(Config.MESSAGE_REPLY, false)) {
                replyMessage();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelNotification(this);
//        try {
//            inputStream = new FileInputStream(mAudioFilePath);
//            byte[] byt = convertStreamToByteArray(inputStream);
//            waveView.setRawData(byt);
//            waveView.setTouched(false);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        sessionExpireDialog = new SessionExpireDialog(MessageTimelineActivity.this);

    }

    @Override
    protected void onPause() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();

        BaseClass.currentMessageType = "";
        BaseClass.currentChatDialogue = "";
        //  mRealm.removeAllChangeListeners();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        //  mRealm.removeAllChangeListeners();
        super.onDestroy();
    }


    void getGuessTimeAndStatus() {
        RealmResults<ChatRealm> results = mRealm.where(ChatRealm.class)
                .equalTo("chatDialogueId", mChatDialogueId).findAll();

        for (int i = 0; i < results.size(); i++) {
            ChatRealm messageRealm = results.get(i);
            mLastGuessTime = Long.parseLong(messageRealm.getLastGuessTime());
            mGuessStatus = messageRealm.getGuessId();
        }
    }

    public long getNetworkTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time = cal.getTimeInMillis();

        return time;

    }

    @Override
    public void addMessage(Message message) {
        getMessages();

        //   Toast.makeText(mcActivity, "new message received in timeline", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageChange(Message message) {

        RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class)
                .equalTo("chat_dialogue_id", message.getChat_dialogue_id())
                .equalTo("message_id", message.getMessage_id()).findAll();


        String unreadMessageCount = "";
        mRealm.beginTransaction();

        for (int i = 0; i < results.size(); i++) {
            MessageRealm messageRealm = results.get(i);
            messageRealm.setMessage_read(true);

            unreadMessageCount = String.valueOf(mRealm.where(MessageRealm.class).equalTo("chat_dialogue_id", message.getChat_dialogue_id())
                    .equalTo("message_read", false).count());
            mChatReference.child(message.getChat_dialogue_id()).child("unread_map").child(sp.getString(Config.USER_ID, "")).setValue(unreadMessageCount);
        }

        mRealm.commitTransaction();
        if (mMessageList != null) {
            mMessageList.clear();
        }

        for (MessageRealm messageRealm : realmResults) {
            mMessageList.add(messageRealm);
            messageTimeLineAdapter.notifyDataSetChanged();
        }

        rvMessagesRecyclerView.smoothScrollToPosition(mMessageList.size());

//        getMessages();
        //   Toast.makeText(mcActivity, "message change in timeline", Toast.LENGTH_SHORT).show();
    }


    private void getNofificationMessage(String chatDialogueId, String messageId) {

        LoadingDialog.getLoader().showLoader(this);

        ChatRealm chatRealm = mRealm.where(ChatRealm.class)
                .equalTo("chatDialogueId", chatDialogueId).findFirst();

        try {

            if (chatRealm.getGuessId().equals("1")) {

                mChatDialogueId = chatDialogueId;
                String s = getIntent().getStringExtra(Config.FULL_NAME);

                if (GetContacts.mContactMap.containsKey(s)) {
                    mSenderName = GetContacts.mContactMap.get(s);
                } else {
                    mSenderName = s;
                }
                mMessageType = Config.MESSAGE_TYPE_GUESSED;
                mRecieverId = chatRealm.getReceiverId();
                mMessageColor = chatRealm.getMessageColor();
                mMessageIcon = chatRealm.getMessageIcon();

            } else if (chatRealm.getGuessId().equals("0")) {
                MessageRealm messageRealm = mRealm.where(MessageRealm.class).equalTo("message_read", false).equalTo("message_type", false).equalTo("message_id", messageId).findFirst();

                if (messageRealm.getGuess_status().equals("0")) {
                    mChatDialogueId = chatDialogueId;
                    mSenderName = fixRandomName(chatRealm.getRandomName());
                    mMessageType = Config.MESSAGE_TYPE_RECEIVED;
                    mRecieverId = chatRealm.getReceiverId();
                    mMessageColor = chatRealm.getMessageColor();
                    mMessageIcon = chatRealm.getMessageIcon();
                } else if (messageRealm.getGuess_status().equals("1")) {

                    mChatDialogueId = chatDialogueId;
                    String s = getIntent().getStringExtra(Config.FULL_NAME);

                    if (GetContacts.mContactMap.containsKey(s)) {
                        mSenderName = GetContacts.mContactMap.get(s);
                    } else {
                        mSenderName = s;
                    }
                    mMessageType = Config.MESSAGE_TYPE_SENT;
                    mRecieverId = chatRealm.getReceiverId();
                    mMessageColor = chatRealm.getMessageColor();
                    mMessageIcon = chatRealm.getMessageIcon();
                }
            }
            LoadingDialog.getLoader().dismissLoader();
        } catch (NullPointerException e) {
            Snackbar.make(findViewById(android.R.id.content), "Try to Reload",
                    Snackbar.LENGTH_INDEFINITE).setAction("RELOAD",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MessageTimelineActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }).show();


        }
    }

}
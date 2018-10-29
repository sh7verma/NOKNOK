package com.app.noknok.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.definitions.Config;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.models.MessageRealm;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smp.soundtouchandroid.AudioDuration;
import com.smp.soundtouchandroid.SoundStreamAudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rm.com.audiowave.AudioWaveView;

import static android.view.View.GONE;

/**
 * Created by dev on 13/7/17.
 */


public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS = 101;
    private static final String TAG = "CHATACTIVITYTAG";
    SessionExpireDialog sessionExpireDialog;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    String mChatDialogueId, mUserName;
    ArrayList<MessageRealm> mMessageList = new ArrayList<>();
    ImageView ivNext, ivPrevious, ivMicReply;
    TextView tvSenderName, tvMessageTime, tvNewMessageFrom;
    ProgressBar pbProgressBar;
    LinearLayout llClose, llReply, llGuess;
    String mAudioFilePath;
    boolean isPlaying = false;
    AudioWaveView waveView;
    InputStream inputStream;
    long mTotalDuration, mCurrentDuration;
    SoundStreamAudioPlayer soundStreamAudioPlayer;
    AudioDuration audioDuration;
    Realm mRealm;
    File path;
    float currentWaveLength = 0f;
    ImageView ivPlayPause;
    float rate = 1.0f;
    float defaultPitch = 0f;
    int mAudioIndex = 0;
    int mTotalMessages = 0;

    boolean mChecKStoragePermission = false;
    boolean isFileCreated = false;

    FrameLayout flChatCardMainContainer;
    LinearLayout llChatCardContainer;

    ObjectAnimator mProgressAnim;

    boolean showToast = false;
    boolean isReplying = false;


    Handler mHandler = new Handler();

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

    public static long getAvailableSpaceInBytes() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();

        return availableSpace;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRealm = Realm.getDefaultInstance();

        mChatDialogueId = getIntent().getStringExtra(Config.CHAT_DIALOGUE_ID);
        mUserName = getIntent().getStringExtra(Config.FULL_NAME);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Messages");

        mFirebaseStorage = FirebaseStorage.getInstance();


        initUI();
        initListeners();
    }

//    @Override
//    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//        Message message = dataSnapshot.getValue(Message.class);
//
//        if (!checkIfMessageExists(message.getMessage_id())) {
//
//            mRealm.beginTransaction();
//            MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
//            messageRealm.setAudio_length(message.getAudio_length());
//            messageRealm.setAudio_pitch(message.getAudio_pitch());
//            messageRealm.setAudio_rate(message.getAudio_rate());
//            messageRealm.setAudio_url(message.getAudio_url());
//            messageRealm.setUser_type(message.isUser_type());
//            boolean messageRead = false;
//
//            if (message.getRead_ids().size() < 2) {
//                messageRead = false;
//            } else {
//                messageRead = true;
//            }
//            messageRealm.setMessage_read(messageRead);
//            messageRealm.setMessage_id(message.getMessage_id());
//            messageRealm.setSender_id(message.getSender_id());
//            messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
//            messageRealm.setSender_appname(message.getSender_appname());
//            messageRealm.setMessage_time(message.getMessage_time());
//            messageRealm.setMessage_type(message.isMessage_type());
//            mRealm.commitTransaction();
//
//        }
//
//        getMessages();
//
//    }
//
//    @Override
//    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//    }
//
//    @Override
//    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//    }
//
//    @Override
//    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//
//    }

//    private boolean checkIfMessageExists(String message_id) {
//        RealmQuery<MessageRealm> query = mRealm.where(MessageRealm.class)
//                .equalTo("message_id", message_id);
//        return query.count() != 0;
//    }

    private void initListeners() {

        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
        llClose.setOnClickListener(this);
        llReply.setOnClickListener(this);
        llGuess.setOnClickListener(this);

    }

    private void initUI() {


        ivPrevious = (ImageView) findViewById(R.id.iv_chat_audio_back_button);
        ivNext = (ImageView) findViewById(R.id.iv_chat_audio_next_button);
        ivPlayPause = (ImageView) findViewById(R.id.iv_chat_audio_pause_button);
        waveView = (AudioWaveView) findViewById(R.id.aw_chat_audio_wave);
        pbProgressBar = (ProgressBar) findViewById(R.id.pb_chat_progress);
        llClose = (LinearLayout) findViewById(R.id.ll_activity_chat_close);
        llReply = (LinearLayout) findViewById(R.id.ll_activity_chat_reply);
        tvSenderName = (TextView) findViewById(R.id.tv_activity_chat_name);
        tvMessageTime = (TextView) findViewById(R.id.tv_activity_chat_time);
        llGuess = (LinearLayout) findViewById(R.id.ll_activity_chat_guess);

        ivMicReply = (ImageView) findViewById(R.id.iv_chat_activity_mic_reply);
        llChatCardContainer = (LinearLayout) findViewById(R.id.ll_chat_activity_card_container);
        flChatCardMainContainer = (FrameLayout) findViewById(R.id.fl_chat_activity_main_card_container);
        tvNewMessageFrom = (TextView) findViewById(R.id.tv_activity_chat_new_message_from);
        ivPrevious.setVisibility(View.INVISIBLE);

        LinearLayout.LayoutParams flmainCardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        flmainCardParams.setMargins(0, 0, 0, mScreenheight / 80);
        flChatCardMainContainer.setLayoutParams(flmainCardParams);

        FrameLayout.LayoutParams llCardParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        llCardParams.setMargins(mScreenwidth / 15, mScreenheight / 40, mScreenwidth / 15, mScreenheight / 11);
        llChatCardContainer.setLayoutParams(llCardParams);

        LinearLayout.LayoutParams ivMicParams = new LinearLayout.LayoutParams(mScreenwidth / 5, mScreenwidth / 5);
        ivMicReply.setLayoutParams(ivMicParams);

        FrameLayout.LayoutParams llchatReplyParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llchatReplyParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        llReply.setLayoutParams(llchatReplyParams);

        tvNewMessageFrom.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 50);
        tvMessageTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 40);
        tvSenderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 25);

        getMessages();

//        valueEventListener = mMessageReference.orderByChild(Config.CHAT_DIALOGUE_ID).equalTo(mChatDialogueId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
//                Iterator<DataSnapshot> iterator = iterable.iterator();
//                while (iterator.hasNext()) {
//
//                    DataSnapshot dataSnapshot1 = iterator.next();
//                    Message message = dataSnapshot1.getValue(Message.class);
//                    if (!checkIfMessageExists(message.getMessage_id())) {
//
//                        mRealm.beginTransaction();
//                        MessageRealm messageRealm = mRealm.createObject(MessageRealm.class);
//                        messageRealm.setAudio_length(message.getAudio_length());
//                        messageRealm.setAudio_pitch(message.getAudio_pitch());
//                        messageRealm.setAudio_rate(message.getAudio_rate());
//                        messageRealm.setAudio_url(message.getAudio_url());
//                        messageRealm.setUser_type(message.isUser_type());
//                        boolean messageRead = false;
//
//                        if (message.getRead_ids().size() < 2) {
//                            messageRead = false;
//                        } else {
//                            messageRead = true;
//                        }
//                        messageRealm.setMessage_read(messageRead);
//                        messageRealm.setMessage_id(message.getMessage_id());
//                        messageRealm.setSender_id(message.getSender_id());
//                        messageRealm.setChat_dialogue_id(message.getChat_dialogue_id());
//                        messageRealm.setSender_appname(message.getSender_appname());
//                        messageRealm.setMessage_time(message.getMessage_time());
//                        messageRealm.setMessage_type(message.isMessage_type());
//                        mRealm.commitTransaction();
//
//                    }
//                }
//                getMessages();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//
//            }
//        });


    }

    private void getMessages() {

        RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class)
                .equalTo("chat_dialogue_id", mChatDialogueId)
                .notEqualTo("sender_id", sp.getString(Config.USER_ID, ""))
                .equalTo("message_read", false)
                .findAll();

        for (MessageRealm messageRealm : realmResults) {

            mMessageList.add(messageRealm);
        }

        mTotalMessages = realmResults.size();

        realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
            @Override
            public void onChange(RealmResults<MessageRealm> messageRealms) {


                if (messageRealms.size() > mTotalMessages) {
                    mMessageList.clear();

                    for (MessageRealm messageRealm : messageRealms) {

                        mMessageList.add(messageRealm);

                    }
                    mTotalMessages = messageRealms.size();

                }


            }
        });


        if (mMessageList.size() == 1) {
            ivNext.setVisibility(View.INVISIBLE);
            ivPrevious.setVisibility(View.INVISIBLE);
        }

        permissionCheck();

        //  playByDefault();

    }

    @Override
    public void onClick(View v) {

        if (mChecKStoragePermission) {

            switch (v.getId()) {
                case R.id.iv_chat_audio_back_button:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;
                    previousAudio();
                    break;
                case R.id.iv_chat_audio_next_button:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;
                    nextAudio();
                    break;
                case R.id.iv_chat_audio_pause_button:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;
                    togglePlayPause();
                    break;
                case R.id.ll_activity_chat_close:
                    onBackPressed();
                    break;

//                case R.id.ll_activity_chat_reply:
//
//                    mHandler.removeCallbacks(runnable);
//                    isReplying = true;
//                    if (isPlaying) {
//                        soundStreamAudioPlayer.stop();
//                    }
//
//                    if (mProgressAnim != null && mProgressAnim.isStarted()) {
//                        mProgressAnim.end();
//
//                    }
//                    isPlaying = false;
//
//                    ivPlayPause.setImageResource(R.drawable.ic_play_record_copy);
//
//                    new NewAudioRecordingDialog(this, mChatDialogueId).showDialog();
//                    break;

                case R.id.ll_activity_chat_guess:
                    Toast.makeText(this, "work in progress", Toast.LENGTH_SHORT).show();
                    break;

            }

        } else {
            permissionCheck();
        }

    }

    void playAudio() {

        currentWaveLength = 0f;

        if (!isReplying) {

            if (mProgressAnim != null && mProgressAnim.isStarted())
                mProgressAnim.end();

            try {
                inputStream = new FileInputStream(mAudioFilePath);
                byte[] byt = convertStreamToByteArray(inputStream);
                waveView.setRawData(byt);

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

                    Log.d("DURFINAL", "current duration: " + currentDuration);
                }

                @Override
                public void getAudioEndStatus(boolean status) {

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

            mProgressAnim = ObjectAnimator.ofFloat(waveView, "progress", currentWaveLength, 100F).setDuration((mTotalDuration / 1000) + 1000);
            mProgressAnim.start();

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

            // mCurrentDuration = soundStreamAudioPlayer.getPlayedDuration();


//            currentWaveLength = 100f;
            //  Log.d("MUSICPOS", "paused duration: " + soundStreamAudioPlayer.getPlayedDuration());

            isPlaying = false;
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_pause_record_copy);
            //   Log.d("MUSICPOS","currentpos: "+currentDuration);
            playAudio();

            //  seekMusic(mCurrentDuration);
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


        Log.d(TAG, "nextaudio: " + mAudioIndex);
    }

    void playByDefault() {


        RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class)
                .equalTo("chat_dialogue_id", mChatDialogueId)
                .equalTo("message_id", mMessageList.get(mAudioIndex).getMessage_id()).findAll();

        mRealm.beginTransaction();

        for (int i = 0; i < results.size(); i++) {
            MessageRealm messageRealm = results.get(i);
            messageRealm.setMessage_read(true);
        }

        mRealm.commitTransaction();

        mDatabaseReference.child(mMessageList.get(mAudioIndex).getMessage_id())
                .child("read_ids")
                .child(sp.getString(Config.USER_ID, ""))
                .setValue(sp.getString(Config.USER_ID, ""));


        try {
            tvMessageTime.setText(timeConvert(Long.parseLong(mMessageList.get(mAudioIndex).getMessage_time())));
        } catch (ParseException e) {
            tvMessageTime.setText(mMessageList.get(mAudioIndex).getMessage_time());
        }

        tvSenderName.setText(mUserName);

        defaultPitch = Float.parseFloat(mMessageList.get(mAudioIndex).getAudio_pitch());
        if (defaultPitch != 0) {
            defaultPitch = defaultPitch / 100;
        }


        rate = Float.parseFloat(mMessageList.get(mAudioIndex).getAudio_rate());
        String localUrl = mMessageList.get(mAudioIndex).getLocal_url();

        if (localUrl == null || localUrl.equals("")) {
            startdownload(mMessageList.get(mAudioIndex).getAudio_url(), mMessageList.get(mAudioIndex).getMessage_id());
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


        path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NOKNOK/Messages/");

        path.mkdirs();

        final String filePath = mStorageReference.getName();
//        mStorageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//            @Override
//            public void onSuccess(StorageMetadata storageMetadata) {
//               long fileSize =  storageMetadata.getSizeBytes();
//                if(fileSize>getAvailableSpaceInBytes()){
//                    Toast.makeText(sessionExpireDialog, "Not enough memory to store audio", Toast.LENGTH_SHORT).show();
//                }else{


        final File downloadFile = new File(path, filePath);
        try {
            isFileCreated = downloadFile.createNewFile();

        } catch (IOException e) {
            Log.e("downloadfileerror", e.getMessage());
            isFileCreated = false;
        }

        if (showToast)
            Toast.makeText(ChatActivity.this, "file created or not: " + isFileCreated, Toast.LENGTH_SHORT).show();

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

                    playByDefault();


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "file download error: " + exception.getMessage());

                    pbProgressBar.setVisibility(GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                    if (showToast)
                        Toast.makeText(getApplicationContext(), "File Download Failed", Toast.LENGTH_SHORT).show();

                }
            });
        }

//                }
//            }
//        });

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


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSIONS: {
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//
//                    playByDefault();
//
//                } else {
//                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
//                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                                    intent.setData(Uri.parse("package:" + getPackageName()));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                    startActivity(intent);
//                                }
//                            }).show();
//                }
//                return;
//            }
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSIONS: {
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//
//                    playByDefault();
//
//                } else {
//                    snakbarShow();
//                }
//                return;
//            }
//        }
//
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

    String timeConvert(long t) throws ParseException {
        SimpleDateFormat dayTime = new SimpleDateFormat("dd MMM" + ", " + "hh:mm a", Locale.US);
        String str = dayTime.format(new Date(t));
        return str;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionExpireDialog = new SessionExpireDialog(ChatActivity.this);
    }

    @Override
    public void onBackPressed() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        mRealm.removeAllChangeListeners();
        super.onBackPressed();


    }

    @Override
    protected void onPause() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        mRealm.removeAllChangeListeners();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        mRealm.removeAllChangeListeners();
        super.onDestroy();
    }

    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mMessageReference.removeEventListener(valueEventListener);
//    }
}
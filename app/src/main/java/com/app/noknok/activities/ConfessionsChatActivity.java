package com.app.noknok.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.ConfessionDeleteDialog;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.interfaces.ConfessionRemoveInterface;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.services.FirebaseListeners;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smp.soundtouchandroid.AudioDuration;
import com.smp.soundtouchandroid.SoundStreamAudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import rm.com.audiowave.AudioWaveView;

import static android.view.View.GONE;
import static com.app.noknok.dialogs.SessionExpireDialog.mContext;
import static com.app.noknok.services.FirebaseNotificationService.cancelNotification;

/**
 * Created by dev on 28/7/17.
 */
public class ConfessionsChatActivity extends BaseActivity implements View.OnClickListener, ConfessionRemoveInterface {

    private static final int REQUEST_PERMISSIONS = 101;
    private static final String TAG = "CHATACTIVITYTAG";
    SessionExpireDialog sessionExpireDialog;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMessageReference;
    DatabaseReference mConfessionReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    String mChatDialogueId, mUserName;
    ImageView ivNext, ivPrevious, ivConfessionImage;
    TextView tvSenderName, tvMessageTime, tvNewMessageFrom;
    ProgressBar pbProgressBar;
    LinearLayout llClose;
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
    ImageView ivPlayPause, ivDelete;
    float rate = 1.0f;
    float defaultPitch = 0f;
    RandomIllustrator randomIllustrator;
    int mAudioIndex = 0;
    int mTotalMessages = 0;
    String confessionType;

    boolean mChecKStoragePermission = false;
    boolean isFileCreated = false;

    FrameLayout flChatCardMainContainer;
    LinearLayout llChatCardContainer, llMainContainer;
    RandomColor randomColor;
    ObjectAnimator mProgressAnim;

    boolean showToast = false;
    boolean isReplying = false;
    boolean comingFromNotification = false;

    ArrayList<MessageRealm> mMessageList = new ArrayList<>();

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
        setContentView(R.layout.activity_confessions_chat);

        FirebaseListeners.setInterRefer(this);
        cancelNotification(this);

        mRealm = Realm.getDefaultInstance();
        randomIllustrator = new RandomIllustrator();
        randomColor = new RandomColor();

        if (getIntent().getBooleanExtra(Config.COMING_FROM, false)) {
            comingFromNotification = true;
        }

        confessionType = getIntent().getStringExtra(Config.CONFESSION_TYPE);
        mUserName = getIntent().getStringExtra(Config.FULL_NAME);

        if (!comingFromNotification) {

            String confessionList = getIntent().getStringExtra(Config.CONFESSIONS_LIST);
            int currentPos = getIntent().getIntExtra(Config.CURRENT_POSITION, 0);
            mAudioIndex = currentPos;
            jsonConversion(confessionList);

        } else {

            RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
                    .equalTo("confession_type", confessionType).findAll();

            if (mMessageList != null) {
                mMessageList.clear();
            }

            System.out.println("Realmresults:: " +realmResults);

            mMessageList.addAll(realmResults);

            if (mMessageList.size() > 0)
                mAudioIndex = mMessageList.size() - 1;

        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageReference = mFirebaseDatabase.getReference("Messages");
        mConfessionReference = mFirebaseDatabase.getReference("Confessions").getRef();

        mFirebaseStorage = FirebaseStorage.getInstance();


        initUI();
        initListeners();
    }

    public void jsonConversion(String confessionsList) {
        ArrayList<Message> messageArrayList;
        Gson gson = new Gson();
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        messageArrayList = gson.fromJson(confessionsList, type);

        for (int i = 0; i < messageArrayList.size(); i++) {
            MessageRealm messageRealm = new MessageRealm();
            messageRealm.setAudio_pitch(messageArrayList.get(i).getAudio_pitch());
            messageRealm.setAudio_rate(messageArrayList.get(i).getAudio_rate());
            messageRealm.setAudio_url(messageArrayList.get(i).getAudio_url());
            messageRealm.setAudio_length(messageArrayList.get(i).getAudio_length());
            messageRealm.setMessage_icon(messageArrayList.get(i).getMessage_icon());
            messageRealm.setMessage_time(messageArrayList.get(i).getMessage_time());
            messageRealm.setMessage_id(messageArrayList.get(i).getMessage_id());
            messageRealm.setSender_number(messageArrayList.get(i).getSender_number());
            messageRealm.setSender_id(messageArrayList.get(i).getSender_id());
            messageRealm.setLocal_url(messageArrayList.get(i).getLocal_url());
            messageRealm.setMessage_color(messageArrayList.get(i).getMessage_color());
//            messageRealm.setGuess_status(messageArrayList.get(i).getGuess_status().get(sp.getString(Config.USER_ID,"")));
            messageRealm.setMessage_read(messageArrayList.get(i).isMessage_read());
            messageRealm.setMessage_theme(messageArrayList.get(i).getMessage_theme().get(sp.getString(Config.USER_ID, "")));
            messageRealm.setUser_type(messageArrayList.get(i).isUser_type());
            messageRealm.setSender_appname(messageArrayList.get(i).getSender_appname());
            mMessageList.add(messageRealm);
        }
    }

    private void initListeners() {

        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
        llClose.setOnClickListener(this);
        ivDelete.setOnClickListener(this);

    }

    private void initUI() {
        ivConfessionImage = (ImageView) findViewById(R.id.iv_activity_confessions_chat_image);
        ivPrevious = (ImageView) findViewById(R.id.iv_chat_audio_back_button);
        ivNext = (ImageView) findViewById(R.id.iv_chat_audio_next_button);
        ivPlayPause = (ImageView) findViewById(R.id.iv_chat_audio_pause_button);
        waveView = (AudioWaveView) findViewById(R.id.aw_chat_audio_wave);
        pbProgressBar = (ProgressBar) findViewById(R.id.pb_chat_progress);
        llClose = (LinearLayout) findViewById(R.id.ll_activity_chat_close);
        tvSenderName = (TextView) findViewById(R.id.tv_activity_chat_name);
        tvMessageTime = (TextView) findViewById(R.id.tv_activity_chat_time);
        ivDelete = (ImageView) findViewById(R.id.iv_activity_confessions_chat_delete);
        llMainContainer = (LinearLayout) findViewById(R.id.ll_activity_confessions_main_container);
        tvNewMessageFrom = (TextView) findViewById(R.id.tv_activity_chat_new_message_from);
        flChatCardMainContainer = (FrameLayout) findViewById(R.id.fl_chat_activity_main_card_container);
        llChatCardContainer = (LinearLayout) findViewById(R.id.ll_chat_activity_card_container);


        if (confessionType.equals(Config.MY_CONFESSIONS)) {
            ivDelete.setVisibility(View.VISIBLE);
            tvNewMessageFrom.setText("Save As");
        } else {
            ivDelete.setVisibility(View.INVISIBLE);
        }
        if (mAudioIndex == 0)
            ivPrevious.setVisibility(View.INVISIBLE);
        else if (mAudioIndex == mMessageList.size() - 1)
            ivNext.setVisibility(View.INVISIBLE);

        LinearLayout.LayoutParams flmainCardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        flmainCardParams.setMargins(0, 0, 0, mScreenheight / 80);
        flChatCardMainContainer.setLayoutParams(flmainCardParams);

        FrameLayout.LayoutParams llCardParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        llCardParams.setMargins(mScreenwidth / 15, mScreenheight / 40, mScreenwidth / 15, mScreenheight / 11);
        llChatCardContainer.setLayoutParams(llCardParams);

        tvNewMessageFrom.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 50);
        tvMessageTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 40);
        tvSenderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 25);

        getMessages();

    }

    private void getMessages() {

//        RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class)
//                .equalTo("message_type", true)
//                .equalTo("message_id", mMessageList.get(mAudioIndex).getMessage_id())
//                .findAll();
//
////        for (MessageRealm messageRealm : realmResults) {
////
////            mMessageList.add(messageRealm);
////        }
//
//        mTotalMessages = realmResults.size();
//
//        realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//
//
//                if (messageRealms.size() > mTotalMessages) {
//                    mMessageList.clear();
//
//                    for (MessageRealm messageRealm : messageRealms) {
//
//                        mMessageList.add(messageRealm);
//
//                    }
//                    mTotalMessages = messageRealms.size();
//
//                }
//
//            }
//        });
        if (mMessageList.size() == 1) {
            ivNext.setVisibility(View.INVISIBLE);
            ivPrevious.setVisibility(View.INVISIBLE);
        }
        permissionCheck();
    }

    @Override
    public void onClick(View v) {

        if (mChecKStoragePermission) {

            switch (v.getId()) {
                case R.id.iv_chat_audio_back_button:
                    mHandler.removeCallbacks(runnable);
                    isReplying = false;

                    if (isPlaying) {
                        soundStreamAudioPlayer.stop();
                    }

                    if (mProgressAnim != null && mProgressAnim.isStarted()) {
                        mProgressAnim.end();
                    }

                    isPlaying = false;
                    try {
                        previousAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.iv_chat_audio_next_button:
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
                case R.id.iv_chat_audio_pause_button:

                    mHandler.removeCallbacks(runnable);
                    isReplying = false;

                    togglePlayPause();

                    break;
                case R.id.ll_activity_chat_close:

                    onBackPressed();

                    break;


                case R.id.ll_activity_chat_guess:
                    Toast.makeText(this, "work in progress", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.iv_activity_confessions_chat_delete:
                    mHandler.removeCallbacks(runnable);
                    if (isPlaying) {
                        soundStreamAudioPlayer.stop();
                    }

                    if (mProgressAnim != null && mProgressAnim.isStarted()) {
                        mProgressAnim.end();
                    }

                    isPlaying = false;
                    deleteConfession(mAudioIndex);
                    break;

            }

        } else {
            permissionCheck();
        }
    }


    void deleteConfession(int position) {
        final String messageId = mMessageList.get(position).getMessage_id();
        new ConfessionDeleteDialog(mContext, messageId, mConfessionReference, mMessageReference, true).showDialog();
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

            Log.d(TAG, "total duration: " + mTotalDuration + ", duration: " + Duration);

            mHandler.postDelayed(runnable, Duration);

            mProgressAnim = ObjectAnimator.ofFloat(waveView, "progress", currentWaveLength, 100F).setDuration((mTotalDuration / 1000));
            mProgressAnim.start();

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

        if (!(new ConnectionDetector(this).isConnectingToInternet())) {
            ServerAPIs.noInternetConnectionIndefinite(llMainContainer);
        }

        try {
            ivConfessionImage.setImageResource(randomIllustrator.getDrawable(mMessageList.get(mAudioIndex).getMessage_icon()));

            RandomColor randomColor = new RandomColor();
            GradientDrawable drawable = new GradientDrawable();

            int colors[] = {Color.parseColor(randomColor.getDrawable(mMessageList.get(mAudioIndex).getMessage_color()).getGradient1()),
                    Color.parseColor(randomColor.getDrawable(mMessageList.get(mAudioIndex).getMessage_color()).getGradient2())};

            drawable.setColors(colors);
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            drawable.setCornerRadius(20);

            llChatCardContainer.setBackgroundDrawable(drawable);

            tvMessageTime.setText(timeConvert(Long.parseLong(mMessageList.get(mAudioIndex).getMessage_time())));
        } catch (ParseException e) {
            tvMessageTime.setText(mMessageList.get(mAudioIndex).getMessage_time());
        }


        if (mMessageList.get(mAudioIndex).getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
            tvNewMessageFrom.setText("Sent as");
        } else {
            tvNewMessageFrom.setText("New Confession from");
        }

        if (!mMessageList.get(mAudioIndex).getSender_id().equals(sp.getString(Config.USER_ID, ""))) {
            if (mMessageList.get(mAudioIndex).isUser_type()) {
                tvSenderName.setText(fixRandomName(mMessageList.get(mAudioIndex).getMessage_theme()));
            } else {
                tvSenderName.setText(mMessageList.get(mAudioIndex).getSender_appname());
            }
        } else {
            if (mMessageList.get(mAudioIndex).isUser_type()) {
                tvSenderName.setText("ANONYMOUS");
            } else {
                tvSenderName.setText(mMessageList.get(mAudioIndex).getSender_appname());
            }
        }

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


        path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NOKNOK/Confessions/");

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
        if (!downloadFile.exists()) {
            try {
                isFileCreated = downloadFile.createNewFile();

            } catch (IOException e) {
                Log.e("downloadfileerror", e.getMessage());
                isFileCreated = false;
            }
        } else {
            isFileCreated = true;
        }
        if (showToast)
            Toast.makeText(ConfessionsChatActivity.this, "file created or not: " + isFileCreated, Toast.LENGTH_SHORT).show();

        if (isFileCreated) {
            mStorageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (showToast)
                        Toast.makeText(getApplicationContext(), "File Download Complete", Toast.LENGTH_SHORT).show();

                    mMessageReference.child(mMessageList.get(mAudioIndex).getMessage_id())
                            .child("read_ids")
                            .child(sp.getString(Config.USER_ID, ""))
                            .setValue(sp.getString(Config.USER_ID, ""));

                    RealmResults<MessageRealm> results = mRealm.where(MessageRealm.class).equalTo("message_id", messageId).findAll();

                    mRealm.beginTransaction();

                    for (int i = 0; i < results.size(); i++) {
                        MessageRealm messageRealm = results.get(i);
                        messageRealm.setLocal_url(downloadFile.getAbsolutePath());
                        messageRealm.setMessage_read(true);
                        mMessageList.set(mAudioIndex, messageRealm);
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

        String s = "";
        for (int i = 0; i < str.length(); i++) {
            s += Character.toUpperCase(str.charAt(i));
        }
        return s;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionExpireDialog = new SessionExpireDialog(ConfessionsChatActivity.this);
    }

    @Override
    public void onBackPressed() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        //   mRealm.removeAllChangeListeners();
        super.onBackPressed();


    }

    @Override
    protected void onPause() {
        isReplying = true;
        if (isPlaying){
            togglePlayPause();
        }
//            soundStreamAudioPlayer.stop();
        //    mRealm.removeAllChangeListeners();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isReplying = true;
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        //   mRealm.removeAllChangeListeners();
        super.onDestroy();
    }

    String fixRandomName(String s) {
        String res = "";

        if (s != null && !s.equals("")) {
            String[] a = s.split(",");
            String str = a[1] + " " + a[0];
            for (int i = 0; i < str.length(); i++) {
                res += Character.toUpperCase(str.charAt(i));
            }
        }
        return res;
    }

    @Override
    public void confessionRemoved(boolean deleted, String localUrl) {

        RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class).equalTo("message_type", true)
                .equalTo("confession_type", confessionType).findAll();

        if (mMessageList != null) {
            mMessageList.clear();
        }
        mMessageList.addAll(realmResults);
        if (mMessageList.size() > 0) {
            mAudioIndex = mMessageList.size() - 1;
        }

        if (mAudioFilePath.equals(localUrl)) {
            Toast.makeText(this, "Confession deleted by the sender", Toast.LENGTH_SHORT).show();

            FirebaseListeners.setInterRefer(null);
            finish();
        }
    }
}
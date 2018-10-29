package com.app.noknok.dialogs;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.adapters.AudioFiltersAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.interfaces.FilterListInterface;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.Chat;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.ConfessionsContact;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.models.MutualFriendsModel;
import com.app.noknok.models.Notification;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.app.noknok.utils.RandomNameGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smp.soundtouchandroid.AudioDuration;
import com.smp.soundtouchandroid.SoundStreamAudioPlayer;
import com.smp.soundtouchandroid.SoundTouch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.Sort;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rm.com.audiowave.AudioWaveView;

/**
 * Created by dev on 27/6/17.
 */

public class SendAudioDialog extends BaseDialog implements View.OnClickListener {

    private static final String TAG = "SENDAUDIODIALOGTAG";
    Dialog mDialog;
    Context mContext;
    String mAudioFilePath;
    File recordingFile;
    SoundTouch soundTouch;

    boolean isPlaying = false;

    AudioWaveView waveView;
    InputStream inputStream;

    RecyclerView rvAudioFitlers;

    long mTotalDuration, mCurrentDuration, tempDuration;
    float waveStartTime = 0F;
    // MediaPlayer mediaPlayer;

    SoundStreamAudioPlayer soundStreamAudioPlayer;
    AudioDuration audioDuration;

    SharedPreferences mSharedPreferences;
    //   int totalDuration, currentDuration;

    FilterListInterface filterListInterface;

    ImageView ivPlayPause, ivCancel, sendAudioMeme;
    LinearLayout llAudioRecordContainer;
    TextView tvPostButton;

    float[] pitch = new float[]{0, -100, -450, -250, 450, 950, 1400};
    float rate = 1.0f;
    float defaultPitch = 0f;
    boolean userType = false;
    MutualFriendsModel mMutualFriendsModel;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    String mChatDialogueId = " ";

    String mRecordType;
    ArrayList<ConfessionsContact> confessionsContactsList;

    ArrayList<HashMap<String, String>> confessionHashMapList = new ArrayList<>();


    ObjectAnimator mProgressAnim;

    String mRecieverId, mMessageType;

    Realm mRealm;

    Handler mHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isPlaying = false;
            ivPlayPause.setImageResource(R.drawable.ic_play_record);
        }
    };


    //constructor for direct message send
    public SendAudioDialog(Context context, File recordingFile, MutualFriendsModel mutualFriendsModel) {
        super(context);
        mContext = context;
        this.recordingFile = recordingFile;
        mRecordType = Config.MESSAGE_SEND;
        mMutualFriendsModel = mutualFriendsModel;
        mAudioFilePath = recordingFile.getAbsolutePath();
    }

    //constructor for message reply
//    public SendAudioDialog(Context context, File recordingFile, String chatDialogueId) {
//        super(context);
//        mContext = context;
//        this.recordingFile = recordingFile;
//        mRecordType = Config.MESSAGE_REPLY;
//        mChatDialogueId = chatDialogueId;
//        mAudioFilePath = recordingFile.getAbsolutePath();
//    }

    //constructor for message reply
    public SendAudioDialog(Context context, File recordingFile, String chatDialogueId, String recieverId, String messageType) {
        super(context);
        mContext = context;
        mRealm = Realm.getDefaultInstance();
        this.recordingFile = recordingFile;
        mRecordType = Config.MESSAGE_REPLY;
        mChatDialogueId = chatDialogueId;
        mRecieverId = recieverId;
        mMessageType = messageType;
        mAudioFilePath = recordingFile.getAbsolutePath();
    }

    //constructor for confessions
    public SendAudioDialog(Context context, ArrayList<ConfessionsContact> confessionsContactsList, File recordingFile, boolean userType) {
        super(context);
        mContext = context;
        this.recordingFile = recordingFile;
        mAudioFilePath = recordingFile.getAbsolutePath();
        this.userType = userType;
        this.confessionsContactsList = confessionsContactsList;

        for (int i = 0; i < confessionsContactsList.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(confessionsContactsList.get(i).getUser_id(), confessionsContactsList.get(i).getUser_id());
            confessionHashMapList.add(hashMap);
        }
        mRecordType = Config.CONFESSIONS;
    }

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    public void showDialog() {
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), MODE_PRIVATE);
        init();
        initListeners();
    }

    private void init() {


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_send_audio);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(false);
        mDialog.show();

        llAudioRecordContainer = (LinearLayout) mDialog.findViewById(R.id.ll_send_audio_record_container);
        waveView = (AudioWaveView) mDialog.findViewById(R.id.aw_send_audio_wave);
        ivPlayPause = (ImageView) mDialog.findViewById(R.id.iv_send_audio_play_pause);
        ivCancel = (ImageView) mDialog.findViewById(R.id.iv_send_audio_cancel);
        tvPostButton = (TextView) mDialog.findViewById(R.id.tv_send_audio_post_button);
        rvAudioFitlers = (RecyclerView) mDialog.findViewById(R.id.rv_send_audio_filters);
        sendAudioMeme = (ImageView) mDialog.findViewById(R.id.iv_send_audio_record_face);

        int containerHeight = height - (height / 3);
        RelativeLayout.LayoutParams newAudioContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                containerHeight);

        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        llAudioRecordContainer.setLayoutParams(newAudioContainerParams);

        LinearLayout.LayoutParams memeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        memeParams.setMargins(0, (int) (containerHeight * 0.03), 0, (int) (containerHeight * 0.02));
        memeParams.gravity = Gravity.CENTER;
        sendAudioMeme.setLayoutParams(memeParams);


        RelativeLayout.LayoutParams postButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        postButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        postButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        tvPostButton.setLayoutParams(postButtonParams);

        if (mRecordType.equals(Config.CONFESSIONS)) {
            tvPostButton.setText(R.string.post);
        }

        tvPostButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (width * 0.040));

        tvPostButton.setPadding((int) (width * 0.08), (int) (height * 0.02),
                (int) (width * 0.08), (int) (height * 0.02));


        filterListInterface = new FilterListInterface() {
            @Override
            public void onFilterClicked(int position) {

//                Log.d(TAG,"audiopath: "+auth)

                mHandler.removeCallbacks(runnable);
                defaultPitch = pitch[position] / 100;

                playAudio();
            }
        };

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvAudioFitlers.setLayoutManager(layoutManager);

        rvAudioFitlers.setAdapter(new AudioFiltersAdapter(mContext, filterListInterface));


        try {
            inputStream = new FileInputStream(mAudioFilePath);
            byte[] byt = convertStreamToByteArray(inputStream);
            waveView.setRawData(byt);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        waveView.setOnProgressListener(new OnProgressListener() {
//            @Override
//            public void onStartTracking(float v) {
//                waveStartTime = v;
//                ObjectAnimator progressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0, 100F).setDuration((long) mTotalDuration);
//                progressAnim.start();

        //   Log.d("MUSICTIMING", "wavestartime: " + waveStartTime + ", musicduration: " + totalDuration);


//                seekMusic((v * totalDuration) / 100);
        Log.d("SEEKING", "onstarttracking");
//            }
//
//            @Override
//            public void onStopTracking(float v) {
//
//                Log.d("SEEKING", "onstopTracking");
//            }
//
//            @Override
//            public void onProgressChanged(float v, boolean b) {
//                Log.d("SEEKING", "onprogresschanged");
//            }
//        });

        audioDuration = new AudioDuration() {
            @Override
            public void getTotalDuration(long totalDuration) {
                //   mTotalDuration = totalDuration;
                Log.d("DURFINAL", "total duration: " + totalDuration);
            }

            @Override
            public void getCurrentDuration(long currentDuration) {
                mCurrentDuration = currentDuration;

                Log.d("DURFINAL", "current duration: " + currentDuration);
            }

            @Override
            public void getAudioEndStatus(boolean status) {

            }
        };

        playAudio();


//        soundStreamAudioPlayer.setOnProgressChangedListener(new OnProgressChangedListener() {
//            @Override
//            public void onProgressChanged(int track, double currentPercentage, long position) {
//
//                if(currentPercentage == 1.0){
//                    ivPlayPause.setImageResource(R.drawable.ic_play_record);
//                    isPlaying = false;
//                }
//                Log.d("MUSICPOS","per: "+currentPercentage+", position: "+position);
//                currentDuration = position/soundStreamAudioPlayer.getDuration();
//
//          ///      Log.d("MUSICPOS","currentpos: "+currentDuration);
//            }
//
//            @Override
//            public void onTrackEnd(int track) {
//             //   ivPlayPause.setImageResource(R.drawable.ic_play_record);
//
//            }
//
//            @Override
//            public void onExceptionThrown(String string) {
//
//            }
//        });
    }

//    void playMusic() {
//
//
//        try {
//            inputStream = new FileInputStream(mAudioFilePath);
//            byte[] byt = convertStreamToByteArray(inputStream);
//            waveView.setRawData(byt);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        mediaPlayer = MediaPlayer.create(this, Uri.parse(mAudioFilePath));
//
//        mediaPlayer.start();
//        totalDuration = mediaPlayer.getDuration();
//
//        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0F, 100F).setDuration(totalDuration);
//        progressAnim.start();
//
//        isPlaying = true;
//        isRecording = false;
//
//
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                isPlaying = false;
//                waveStartTime = 0F;
//            }
//        });
//    }

    private void initListeners() {
        ivCancel.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
        tvPostButton.setOnClickListener(this);
    }

//    void playAudio(float tempo, float pitch) {
//
//        try {
//            soundStreamAudioPlayer = new SoundStreamAudioPlayer(0, mAudioFilePath, tempo, pitch);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        new Thread(soundStreamAudioPlayer).start();
//        soundStreamAudioPlayer.start();
//        totalDuration = soundStreamAudioPlayer.getDuration();
//
//        try {
//            inputStream = new FileInputStream(mAudioFilePath);
//            byte[] byt = convertStreamToByteArray(inputStream);
//            waveView.setRawData(byt);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0F, 100F).setDuration((long) totalDuration);
//        progressAnim.start();
//
//        isPlaying = true;
//    }

//    void seekMusic(long time) {
//
//        try {
//            soundStreamAudioPlayer = new SoundStreamAudioPlayer(0, mAudioFilePath, rate, defaultPitch, audioDuration);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (soundStreamAudioPlayer != null) {
//            new Thread(soundStreamAudioPlayer).start();
//            soundStreamAudioPlayer.start();
//        }
//
//        soundStreamAudioPlayer.seekTo(time);
//    }

    void playAudio() {

        if (isPlaying)
            soundStreamAudioPlayer.stop();

        if (mProgressAnim != null && mProgressAnim.isStarted()) {
            mProgressAnim.end();
        }


        try {
            soundStreamAudioPlayer = new SoundStreamAudioPlayer(0, mAudioFilePath, rate, defaultPitch, audioDuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (soundStreamAudioPlayer != null) {
            new Thread(soundStreamAudioPlayer).start();
            soundStreamAudioPlayer.start();
        }

        mTotalDuration = (int) (soundStreamAudioPlayer.getDuration() / 1000);

//        mProgressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0, 100F).setDuration((mTotalDuration /1000));
        mProgressAnim = ObjectAnimator.ofFloat(waveView, "progress", 0, 100F).setDuration((mTotalDuration));
        mProgressAnim.start();

        // totalDuration = soundStreamAudioPlayer.getDuration();
        // Log.d("MUSICPOS", "musicduration: " + soundStreamAudioPlayer.getDuration());

        isPlaying = true;

        ivPlayPause.setImageResource(R.drawable.ic_pause_record);


        mHandler.postDelayed(runnable, mTotalDuration);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_audio_cancel:
                if (isPlaying)
                    soundStreamAudioPlayer.stop();

                mHandler.removeCallbacks(runnable);
                if (mProgressAnim != null && mProgressAnim.isStarted()) {
                    mProgressAnim.end();

                }
                isPlaying = false;
                ivPlayPause.setImageResource(R.drawable.ic_play_record);

                new RecordingDiscardDialog(mContext, mDialog).showDialog();
                break;

            case R.id.iv_send_audio_play_pause:
                mHandler.removeCallbacks(runnable);
                if (mProgressAnim != null && mProgressAnim.isStarted()) {
                    mProgressAnim.end();
                    Log.d(TAG, "progressanim ended");
                    // mProgressAnim.cancel();
                }
                togglePlayPause();
                break;

            case R.id.tv_send_audio_post_button:
                mHandler.removeCallbacks(runnable);

                if (mProgressAnim != null && mProgressAnim.isStarted()) {
                    mProgressAnim.end();

                }
                ivPlayPause.setImageResource(R.drawable.ic_play_record);

                if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                    soundStreamAudioPlayer.stop();
                    isPlaying = false;

                    Uri uri = Uri.fromFile(recordingFile);
                    uploadFile(uri);


                } else {
                    Snackbar.make(mDialog.findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void togglePlayPause() {
        if (isPlaying) {
            soundStreamAudioPlayer.stop();
            mHandler.removeCallbacks(runnable);
            if (mProgressAnim != null && mProgressAnim.isStarted()) {
                mProgressAnim.end();
                //     mProgressAnim.cancel();
            }
            //   Log.d("MUSICPOS", "paused duration: " + soundStreamAudioPlayer.getPlayedDuration());
            ivPlayPause.setImageResource(R.drawable.ic_play_record);

            isPlaying = false;
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_pause_record);
            playAudio();
            //   Log.d("MUSICPOS","currentpos: "+currentDuration);
//            seekMusic(mCurrentDuration);
            isPlaying = true;
        }
    }

    public void uploadFile(Uri uri) {

        LoadingDialog.getLoader().showLoader(mContext);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference("audio_files");

        final UploadTask uploadTask = storageReference.child(String.valueOf(new Date().getTime()) + ".m4a").putFile(uri);

//        if (!(new ConnectionDetector(mContext).isConnectingToInternet())) {
//            LoadingDialog.getLoader().dismissLoader();
//            uploadTask.cancel();
//            mDialog.dismiss();
//        }

        uploadTask.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                LoadingDialog.getLoader().dismissLoader();
                uploadTask.cancel();
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                mDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                LoadingDialog.getLoader().dismissLoader();
                uploadTask.cancel();
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                mDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                if (mRecordType.equals(Config.MESSAGE_REPLY)) {
                    sendMessageReply(downloadUrl);
                } else if (mRecordType.equals(Config.MESSAGE_SEND)) {
                    sendMessage(downloadUrl);
                } else if (mRecordType.equals(Config.CONFESSIONS)) {
                    sendConfession(downloadUrl);
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                //   LoadingDialog.getLoader().showLoader(mContext);
                //  double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            }
        });
    }


    void sendMessageReply(final String downloadUrl) {

        String receiverGuessStatus = "", myGuessStatus = "";

        int receiverUnreadCount = 0;

        MessageRealm lastMessage = mRealm.where(MessageRealm.class)
                .equalTo("message_type", false).equalTo("chat_dialogue_id", mChatDialogueId).findAllSorted("message_time", Sort.DESCENDING).first();

        ChatRealm lastChat = mRealm.where(ChatRealm.class).equalTo("chatDialogueId", mChatDialogueId).findFirst();

        String chatGuessStatus = lastChat.getGuessId().equals("1") ? "1" : "0";
        String lastMessageGuessStatus = lastMessage.getGuess_status();

        if (chatGuessStatus.equals("1")) {
            if (lastMessageGuessStatus.equals("1")) {
                myGuessStatus = "1";
                receiverGuessStatus = "0";
            } else {
                myGuessStatus = "0";
                receiverGuessStatus = "1";
            }
        } else {
            if (mMessageType.equals(Config.MESSAGE_TYPE_SENT)) {
                receiverGuessStatus = "0";
                myGuessStatus = "1";
            } else {
                receiverGuessStatus = "1";
                myGuessStatus = "0";
            }
        }


        final DatabaseReference messageReference = mDatabaseReference.child("Messages").getRef();
        final DatabaseReference chatReference = mDatabaseReference.child("Chats").getRef();

        final String messageKey = messageReference.push().getKey();

        final String userId = mSharedPreferences.getString(Config.USER_ID, "");

        final String currentTimeDate = String.valueOf(System.currentTimeMillis());

        Message message = new Message();
        // message.setAudio_length(String.valueOf(mTotalDuration / 1000000));
        message.setAudio_length(String.valueOf(mTotalDuration / 1000));
        message.setSender_appname(mSharedPreferences.getString(Config.FULL_NAME, ""));
        message.setMessage_type(false);  //false for message
        message.setAudio_url(downloadUrl);
        HashMap<String, String> guessStatus = new HashMap<>();


        guessStatus.put(mRecieverId, receiverGuessStatus);
        guessStatus.put(userId, myGuessStatus);
        message.setGuess_status(guessStatus);

        message.setAudio_pitch(String.valueOf((int) (defaultPitch * 100)));
        message.setAudio_rate(String.valueOf((int) rate));
        message.setChat_dialogue_id(mChatDialogueId);
        message.setSender_number(mSharedPreferences.getString(Config.PHONE_NUMBER, ""));
        message.setSender_id(userId);
        message.setMessage_id(messageKey);
        message.setUser_type(userType);

        HashMap<String, String> readIdMap = new HashMap<>();
        readIdMap.put(userId, userId);
        message.setRead_ids(readIdMap);
        message.setMessage_time(currentTimeDate);
        messageReference.child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotification(mChatDialogueId, downloadUrl, mRecieverId, userId, messageKey);
            }
        });

        chatReference.child(mChatDialogueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int receiverUnreadCount = 0;

                Chat getChat = dataSnapshot.getValue(Chat.class);
                if (getChat.getUnread_map().get(mRecieverId) != null) {
                    receiverUnreadCount = Integer.parseInt(getChat.getUnread_map().get(mRecieverId));
                }

                chatReference.child(mChatDialogueId).child("last_message_id").setValue(messageKey);
                chatReference.child(mChatDialogueId).child("last_message_time").setValue(currentTimeDate);
                chatReference.child(mChatDialogueId).child("last_message_time").setValue(currentTimeDate);
                chatReference.child(mChatDialogueId).child("unread_map")
                        .child(mRecieverId)
                        .setValue(String.valueOf(receiverUnreadCount + 1));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        chatReference.child(mChatDialogueId).child("anonymous_ids").child(userId).setValue("1");
//        chatReference.child(mChatDialogueId).child("direct_ids").child(mMutualFriendsModel.getUser_id()).setValue("1");

        dismissDialog();
    }


    void sendMessage(final String downloadUrl) {

        final DatabaseReference messageReference = mDatabaseReference.child("Messages").getRef();
        final DatabaseReference profileReference = mDatabaseReference.child("profile").getRef();
        final DatabaseReference chatReference = mDatabaseReference.child("Chats").getRef();

        //   final String dialogueKey = messageReference.push().getKey();
        final String messageKey = messageReference.push().getKey();

        final String userId = mSharedPreferences.getString(Config.USER_ID, "");
        String[] participantIds = new String[]{mMutualFriendsModel.getUser_id(), userId};
        Arrays.sort(participantIds);
        final String commonID = participantIds[0] + "" + participantIds[1];

        final String currentTimeDate = String.valueOf(System.currentTimeMillis());

        chatReference.child(commonID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int receiverUnreadCount = 0;

                boolean dataAlreadyExists = false;

                if (dataSnapshot.exists()) {
                    dataAlreadyExists = true;
                }

                Chat getChat = dataSnapshot.getValue(Chat.class);

                if (dataAlreadyExists) {
                    receiverUnreadCount = Integer.parseInt(getChat.getUnread_map().get(mMutualFriendsModel.getUser_id()));
                    //  myUnreadCount = Integer.parseInt(getChat.getUnread_map().get(userId));
                }

                Message message = new Message();
                // message.setAudio_length(String.valueOf(mTotalDuration / 1000000));
                message.setAudio_length(String.valueOf(mTotalDuration / 1000));
                message.setSender_appname(mSharedPreferences.getString(Config.FULL_NAME, ""));
                message.setMessage_type(false);  //false for message
                message.setAudio_url(downloadUrl);

                HashMap<String, String> guessStatus = new HashMap<>();
                guessStatus.put(mMutualFriendsModel.getUser_id(), "0");
                guessStatus.put(userId, "1");
                message.setGuess_status(guessStatus);

                message.setAudio_pitch(String.valueOf((int) (defaultPitch * 100)));
                message.setAudio_rate(String.valueOf((int) rate));
                message.setChat_dialogue_id(commonID);
                message.setSender_number(mSharedPreferences.getString(Config.PHONE_NUMBER, ""));
                message.setSender_id(userId);
                message.setMessage_id(messageKey);
                message.setUser_type(userType);

//        HashMap<String, String> themeMap = new HashMap<>();
//        themeMap.put(userId, "Red Panther, Red");
//        message.setMessage_theme(themeMap);

                HashMap<String, String> readIdMap = new HashMap<>();
                readIdMap.put(userId, userId);
                message.setRead_ids(readIdMap);
                message.setMessage_time(currentTimeDate);
                messageReference.child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //    sendNotification(Config.MESSAGE);
                    }
                });

                Chat chat = new Chat();
                chat.setChat_dialogue_id(commonID);
                chat.setLast_message_id(messageKey);
                chat.setLast_message_time(currentTimeDate);

                HashMap<String, String> unreadMap = new HashMap<>();
                unreadMap.put(mMutualFriendsModel.getUser_id(), String.valueOf(receiverUnreadCount + 1));
                unreadMap.put(userId, "0");
                chat.setUnread_map(unreadMap);

                HashMap<String, String> anonymousIds = new HashMap<>();
                anonymousIds.put(mMutualFriendsModel.getUser_id(), "1");
                anonymousIds.put(userId, "0");
                chat.setAnonymous_ids(anonymousIds);

                HashMap<String, String> directIds = new HashMap<>();
                directIds.put(mMutualFriendsModel.getUser_id(), "0");
                directIds.put(userId, "1");
                chat.setDirect_ids(directIds);

                HashMap<String, String> guessIds = new HashMap<>();
                if (dataAlreadyExists) {

                    guessIds.put(mMutualFriendsModel.getUser_id(), getChat.getGuess_ids().get(mMutualFriendsModel.getUser_id()));
                    guessIds.put(userId, getChat.getGuess_ids().get(userId));

                } else {
                    guessIds.put(mMutualFriendsModel.getUser_id(), "0");
                    guessIds.put(userId, "0");
                }

                chat.setGuess_ids(guessIds);
                HashMap<String, String> pushTokenMap = new HashMap<>();
                pushTokenMap.put(mMutualFriendsModel.getUser_id(), mMutualFriendsModel.device_token);
                pushTokenMap.put(userId, mSharedPreferences.getString(Config.DEVICE_TOKEN, ""));
                chat.setPush_tokens(pushTokenMap);

                HashMap<String, String> deleteDialogueTime = new HashMap<>();
                deleteDialogueTime.put(mMutualFriendsModel.getUser_id(), "time");
                deleteDialogueTime.put(userId, "time");
                chat.setDelete_dialogue_time(deleteDialogueTime);

                HashMap<String, String> accessTokenMap = new HashMap<>();
                accessTokenMap.put(userId, mSharedPreferences.getString(Config.ACCESS_TOKEN, ""));
                accessTokenMap.put(mMutualFriendsModel.getUser_id(), mMutualFriendsModel.getAccess_token());
                chat.setAccess_tokens(accessTokenMap);

                HashMap<String, String> lastGuessTime = new HashMap<>();
                lastGuessTime.put(userId, "0");
                lastGuessTime.put(mMutualFriendsModel.getUser_id(), "0");
                chat.setLast_guess_time(lastGuessTime);

                HashMap<String, String> contactNumbersMap = new HashMap<>();
                contactNumbersMap.put(userId, mSharedPreferences.getString(Config.COUNTRY_CODE, "") + mSharedPreferences.getString(Config.PHONE_NUMBER, ""));
                contactNumbersMap.put(mMutualFriendsModel.getUser_id(), mMutualFriendsModel.getPhone_number());
                chat.setContact_numbers(contactNumbersMap);
                chatReference.child(commonID).setValue(chat);

                final ArrayList<String> FirebaseNames = new ArrayList<>();
                profileReference.child(userId).child("chat_dialogue_id").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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


                                profileReference.child(userId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames)).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        LoadingDialog.getLoader().dismissLoader();
                                    }
                                });
                            }

                            // dismissDialog();

                        } else {

                            //          arrayList.add(s);

                            //   Log.d("dta", dataSnapshot.getValue().toString());


                            //   Log.d("nam33e", String.valueOf(TotalNames.size()));

                            // dialogueMap.put(commonID, randomNameGenerator.get(TotalNames));
                            profileReference.child(userId).child("chat_dialogue_id").child(commonID).setValue(randomNameGenerator.get(TotalNames)).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    LoadingDialog.getLoader().dismissLoader();
                                }
                            });
                        }
                        profileReference.child(mMutualFriendsModel.getUser_id()).child("chat_dialogue_id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //   HashMap<String, String> receiverDialogueMap = new HashMap<String, String>();
                                //   receiverDialogueMap.put(commonID, "");
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                    HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                                    if (!hashMap.containsKey(commonID) || hashMap.get(commonID).equals("")) {
                                        profileReference.child(mMutualFriendsModel.getUser_id()).child("chat_dialogue_id").child(commonID).setValue("").addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                LoadingDialog.getLoader().dismissLoader();
                                            }
                                        });
//                                    if (!hashMap.get(commonID).equals("")) {
//                                        //  profileReference.child(mMutualFriendsModel.getUser_id()).child("chat_dialogue_id").setValue(dialogueMap);
//                                    }
                                    }
                                } else {
                                    profileReference.child(mMutualFriendsModel.getUser_id()).child("chat_dialogue_id").child(commonID).setValue("").addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            LoadingDialog.getLoader().dismissLoader();
                                        }
                                    });
                                }

                                sendNotification(commonID, downloadUrl, mMutualFriendsModel.getUser_id(), userId, messageKey);
                                dismissDialog();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                LoadingDialog.getLoader().dismissLoader();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        LoadingDialog.getLoader().dismissLoader();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        dialogueMap.remove(userId);
//
//        dialogueMap.put(mMutualFriendsModel.getUser_id(), "");
//
//        profileReference.child(mMutualFriendsModel.getUser_id()).child("chat_dialogue_id").setValue(dialogueMap);
//        profileReference.child(commonID).child("chat_dialogue_id").setValue(dialogueMap);

    }

    private void sendConfession(final String downloadUrl) {
        final DatabaseReference messageReference = mDatabaseReference.child("Messages").getRef();
        final DatabaseReference confessionReference = mDatabaseReference.child("Confessions").getRef();
        final String messageKey = messageReference.push().getKey();

        final String userId = mSharedPreferences.getString(Config.USER_ID, "");

        String currentTimeDate = String.valueOf(System.currentTimeMillis());

        Message message = new Message();
        // message.setAudio_length(String.valueOf(mTotalDuration / 1000000));
        message.setAudio_length(String.valueOf(mTotalDuration / 1000));
        message.setSender_appname(mSharedPreferences.getString(Config.FULL_NAME, ""));
        message.setMessage_type(true);  // true for confession
        message.setAudio_url(downloadUrl);

//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put(mMutualFriendsModel.getUser_id(),"0");
//        hashMap.put(userId,"0");
//        message.setGuess_status(hashMap);

        message.setAudio_pitch(String.valueOf((int) (defaultPitch * 100)));
        message.setAudio_rate(String.valueOf((int) rate));
        message.setChat_dialogue_id(mChatDialogueId);
        message.setSender_number(mSharedPreferences.getString(Config.PHONE_NUMBER, ""));
        message.setSender_id(userId);
        message.setMessage_id(messageKey);
        message.setUser_type(userType);

        HashMap<String, String> readIdMap = new HashMap<>();
        readIdMap.put(userId, userId);
        message.setRead_ids(readIdMap);


        HashMap<String, String> themeMap = new HashMap<>();
        for (int i = 0; i < confessionsContactsList.size(); i++) {
            String id = confessionsContactsList.get(i).getUser_id();
            themeMap.put(id, "");
            confessionReference.child(messageKey).child(id).setValue(id);
        }

        message.setMessage_theme(themeMap);

        message.setMessage_time(currentTimeDate);
        messageReference.child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendConfessionNotification(confessionsContactsList, downloadUrl, messageKey, String.valueOf(userType));
                dismissDialog();
            }
        });
    }

    private void sendConfessionNotification(ArrayList<ConfessionsContact> confessionsContactsList, String downloadUrl, String messageKey, String userType) {

        final String userId = mSharedPreferences.getString(Config.USER_ID, "");
        final String latitude = mSharedPreferences.getString(Config.LATITUDE, "");
        final String longitude = mSharedPreferences.getString(Config.LONGITUDE, "");

        Log.d("cccrrfrf", userId);

        String receiversIds = "";
        if (confessionsContactsList.size() > 0) {
            for (int i = 0; i < confessionsContactsList.size(); i++) {
                receiversIds += (confessionsContactsList.get(i).getUser_id() + ",");
            }
            receiversIds = receiversIds.substring(0, receiversIds.length() - 1);
        }

        if ((new ConnectionDetector(mContext).isConnectingToInternet())) {
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.confession_push(receiversIds, downloadUrl, Integer.parseInt(userId), messageKey, userType, new Callback<RetroHelper.ConfessionPush>() {
                @Override
                public void success(RetroHelper.ConfessionPush confessionPush, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("cccrrfrferror", userId);
                }
            });

        } else {
            Snackbar.make(mDialog.findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
        }

    }


    void dismissDialog() {
        LoadingDialog.getLoader().dismissLoader();
        new AudioSentDialog(mContext, mDialog).showDialog();
        finish();
    }


    @Override
    protected void onPause() {
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (isPlaying)
            soundStreamAudioPlayer.stop();
        super.onBackPressed();


    }


    public void sendNotification(String dialogueId, String audioUrl, String receiverId, String myUserId, String messageId) {

        DatabaseReference notificationReference = mDatabaseReference.child("notification").getRef();

        String notificationId = notificationReference.push().getKey();

        Notification notification = new Notification();
        notification.setAudio_url(audioUrl);
        notification.setDialogue_id(dialogueId);
        notification.setReceiver_id(receiverId);
        notification.setSender_id(myUserId);
        notification.setMessage_id(messageId);
        notification.setNotification_id(notificationId);
        notificationReference.child(notificationId).setValue(notification);

    }
}
package com.app.noknok.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.activities.BaseActivity;
import com.app.noknok.activities.DirectMessageActivity;
import com.app.noknok.customviews.RippleBackground;
import com.app.noknok.definitions.Config;
import com.app.noknok.models.ConfessionsContact;
import com.app.noknok.models.MutualFriendsModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by dev on 27/6/17.
 */

public class NewAudioRecordingDialog extends BaseActivity implements View.OnClickListener, View.OnTouchListener, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSIONS = 101;
    private static final String TAG = "AUDIORECORDINGTAG";
    Context mContext;
    Dialog mDialog;

    LinearLayout llNewAudioRecordContainer, llCircle1, llCircle2, llCircle3;
    ImageView ivNewAudioRecordMeme, ivRecordMic, ivCancel;
    TextView tvNewAudioRecordHeaderText;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    File recordingFile;
    boolean isRecording = false;
    MediaRecorder recorder;
    File path;
    int recordingTimeInSeconds = 0;
    int maxRecordTimeInSeconds = 300;
    boolean userType = false;
    String mRecordType;
    String messagesPath = "/NOKNOK/Messages/";
    String confessionsPath = "/NOKNOK/Confessions/";
    String mChatDialogueId;
    MutualFriendsModel mutualFriendsModel;
    ArrayList<ConfessionsContact> confessionsContactsList;
    boolean CheckPosition = true;
    String mReceiverId, mMessageType;
    RippleBackground rippleBackground;
    RelativeLayout rlMic;
    RelativeLayout.LayoutParams layputParam;
    private int yDelta;
    private long startTime = 0L;
    private Timer timer;

    // constructor for message reply
    public NewAudioRecordingDialog(Context context, String chatDialogueId, String mReceiverId, String mMessageType) {  //for message reply
        mContext = context;
        mRecordType = Config.MESSAGE_REPLY;
        mChatDialogueId = chatDialogueId;
        this.mMessageType = mMessageType;
        this.mReceiverId = mReceiverId;
    }

    // constructor for confessions
    public NewAudioRecordingDialog(Context context, ArrayList<ConfessionsContact> confessionsContactsList, boolean userType) {   //for confessions
        mContext = context;
        this.userType = userType;
        this.confessionsContactsList = confessionsContactsList;
        mRecordType = Config.CONFESSIONS;
    }

    // constructor for message send
    public NewAudioRecordingDialog(Context context, MutualFriendsModel mutualFriendsModel) {  //for messages
        mContext = context;
        this.mutualFriendsModel = mutualFriendsModel;
        mRecordType = Config.MESSAGE_SEND;
    }


    public void showDialog() {

        mDialog = new Dialog(mContext);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_new_audio_recording);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        mDialog.show();

        init();
        initListeners();

    }

    private void initListeners() {

        ivRecordMic.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        ivRecordMic.setOnTouchListener(new MyTouchListener());
//        rlMic.setOnTouchListener(new MyTouchListener());
    }

    private void init() {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        llNewAudioRecordContainer = (LinearLayout) mDialog.findViewById(R.id.ll_new_audio_record_container);
        llCircle1 = (LinearLayout) mDialog.findViewById(R.id.ll_new_audio_record_circle1);
        llCircle2 = (LinearLayout) mDialog.findViewById(R.id.ll_new_audio_record_circle2);
        llCircle3 = (LinearLayout) mDialog.findViewById(R.id.ll_new_audio_record_circle3);
        ivNewAudioRecordMeme = (ImageView) mDialog.findViewById(R.id.iv_new_audio_record_face);
        ivRecordMic = (ImageView) mDialog.findViewById(R.id.iv_new_audio_record_mic);
        ivCancel = (ImageView) mDialog.findViewById(R.id.iv_new_audio_record_cancel);
        tvNewAudioRecordHeaderText = (TextView) mDialog.findViewById(R.id.tv_new_audio_record_text);
        rippleBackground = (RippleBackground) mDialog.findViewById(R.id.rippleBackground1);

        ivRecordMic.setScaleX(0.8f);
        ivRecordMic.setScaleY(0.8f);

        int containerHeight = height - (height / 3);
        RelativeLayout.LayoutParams newAudioContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                containerHeight);

        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

//        newAudioContainerParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        llNewAudioRecordContainer.setLayoutParams(newAudioContainerParams);

//        RelativeLayout.LayoutParams newContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        newContainerParams.setMargins(0,0,0,(int) (containerHeight * 0.02));
//        ivCancel.setLayoutParams(newContainerParams);

        rlMic = (RelativeLayout) mDialog.findViewById(R.id.rl_mic);

        LinearLayout.LayoutParams memeParams = new LinearLayout.LayoutParams((int) (width * 0.25), (int) (width * 0.25));
        memeParams.setMargins(0, (int) (containerHeight * 0.03), 0, (int) (containerHeight * 0.02));
        memeParams.gravity = Gravity.CENTER;
        ivNewAudioRecordMeme.setLayoutParams(memeParams);

        tvNewAudioRecordHeaderText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (width * 0.05));

        llCircle1.setAlpha(0.4f);
        llCircle2.setAlpha(0.6f);
        llCircle3.setAlpha(0.8f);

        llCircle1.setLayoutParams(getCircleParams((int) (width * 0.02), (int) (width * 0.03)));
        llCircle2.setLayoutParams(getCircleParams((int) (width * 0.04), (int) (width * 0.03)));
        llCircle3.setLayoutParams(getCircleParams((int) (width * 0.06), (int) (width * 0.01)));

        RelativeLayout.LayoutParams micParams = new RelativeLayout.LayoutParams((int) (width * 0.22), (int) (width * 0.22));
        micParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivRecordMic.setLayoutParams(micParams);

//        RelativeLayout.LayoutParams micRParams = new RelativeLayout.LayoutParams((int) (width * 0.4), (int) (width * 0.4));
//        micRParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        rlMic.setLayoutParams(micParams);

//        rippleBackground.startRippleAnimation();


//        RelativeLayout.LayoutParams ivCancelParams = new RelativeLayout.LayoutParams((int) (width * 0.1), (int) (width * 0.1));
//        ivCancelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        ivCancelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        ivCancelParams.setMargins(0, 0, 0, (int) (mScreenheight * 0.9));
//
//        ivCancel.setLayoutParams(ivCancelParams);

    }

    LinearLayout.LayoutParams getCircleParams(int size, int mar) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, 0, mar);
        return params;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_new_audio_record_mic:

                break;

            case R.id.iv_new_audio_record_cancel:
                mDialog.dismiss();
                if (mContext instanceof DirectMessageActivity) {
                    ((DirectMessageActivity) mContext).finish();
                }
                break;

        }
    }


    void permissionCheck() {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.RECORD_AUDIO) + ContextCompat
                .checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    ((Activity) mContext, Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale
                    ((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(mDialog.findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions((Activity) mContext,
                                        new String[]{Manifest.permission
                                                .RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission
                                .RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {

            startrecord();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    startrecord();
                } else {
                    Snackbar.make(mDialog.findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            permissionCheck();

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (recordingTimeInSeconds < 1) {
                tvNewAudioRecordHeaderText.setText(R.string.tap_hold_to_record);
            }
            stoprecord();
        }
        return false;
    }

    private void stoprecord() {
        // TODO Auto-generated method stub

        ivRecordMic.setScaleX(0.8f);
        ivRecordMic.setScaleY(0.8f);

        llCircle1.setVisibility(View.VISIBLE);
        llCircle2.setVisibility(View.VISIBLE);
        llCircle3.setVisibility(View.VISIBLE);

        ivNewAudioRecordMeme.setImageResource(R.drawable.ill_recording);
        ivCancel.setEnabled(true);
        ivCancel.setImageResource(R.drawable.ic_close);

        if (timer != null) {
            timer.cancel();
        }

        String s = mContext.getString(R.string.tap_hold_to_record);
        if (tvNewAudioRecordHeaderText.getText().toString().equals(s)) {
            return;
        }
        if (isRecording) {
            isRecording = false;
            if (recorder != null) {
                try {
                    //recorder.stop();
                    recorder.stop();
                    recorder.release();
                } catch (RuntimeException ex) {

                }
            }
            vibrate();
            String tempPath = new File(path, "cent.mp3").getAbsolutePath();
            if (recordingTimeInSeconds < 2) {
                ivRecordMic.setEnabled(false);
                tvNewAudioRecordHeaderText.setText(s);
                ivCancel.setEnabled(true);
                ivCancel.setScaleX(1.0f);
                ivCancel.setScaleY(1.0f);
                Snackbar.make(mDialog.findViewById(android.R.id.content), "Audio should be more than 2 seconds", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        ivRecordMic.setEnabled(true);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                    }
                }).show();


            } else if (recordingTimeInSeconds == maxRecordTimeInSeconds) {

                ivRecordMic.setEnabled(false);
                Snackbar.make(mDialog.findViewById(android.R.id.content), "You have reached maximum time limit to record.", Snackbar.LENGTH_INDEFINITE).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        sendingAudioDialog();

                    }
                }, 3000);


            } else {
                if (CheckPosition) {
                    sendingAudioDialog();
                }
            }
        }
    }


    void sendingAudioDialog() {

        if (mRecordType.equals(Config.MESSAGE_SEND)) {
            new SendAudioDialog(mContext, recordingFile, mutualFriendsModel).showDialog();
        } else if (mRecordType.equals(Config.MESSAGE_REPLY)) {
            new SendAudioDialog(mContext, recordingFile, mChatDialogueId, mReceiverId, mMessageType).showDialog();
        } else if (mRecordType.equals(Config.CONFESSIONS)) {
            new SendAudioDialog(mContext, confessionsContactsList, recordingFile, userType).showDialog();
        }

        mDialog.dismiss();

    }

    private void startrecord() {

        ivRecordMic.setScaleX(1.0f);
        ivRecordMic.setScaleY(1.0f);

        llCircle1.setVisibility(View.INVISIBLE);
        llCircle2.setVisibility(View.INVISIBLE);
        llCircle3.setVisibility(View.INVISIBLE);


        ivNewAudioRecordMeme.setImageResource(R.drawable.ill_record_2);
        ivCancel.setImageResource(R.drawable.ic_delete_record);

        recordingTimeInSeconds = 0;
        recordAudio();
        vibrate();
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
    }

    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void recordAudio() {
        boolean isFileCreated = false;
        if (!isRecording) {
            rippleBackground.startRippleAnimation();
            tvNewAudioRecordHeaderText.setText("00:00");

            if (mRecordType.equals(Config.CONFESSIONS)) {
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + confessionsPath);
            } else {
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + messagesPath);
            }
            path.mkdirs();


            String filePath = String.valueOf(new Date().getTime());

            recordingFile = new File(path, filePath + ".m4a");
            try {
                isFileCreated = recordingFile.createNewFile();

            } catch (IOException e) {
                isFileCreated = false;
            }


            if (!isFileCreated) {
                ivRecordMic.setEnabled(false);
                if (timer != null) {
                    timer.cancel();
                }

                Snackbar.make(mDialog.findViewById(android.R.id.content), "Please insert sdcard to record audio", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        mDialog.dismiss();
                        if (mContext instanceof DirectMessageActivity) {
                            ((DirectMessageActivity) mContext).finish();
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {

                    }
                }).show();


            }


            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(recordingFile.getAbsolutePath());

            try {
                recorder.prepare();
                recorder.start();

                isRecording = true;
                ivCancel.setEnabled(false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            isRecording = true;
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    void changeDataValues() {
        isRecording = false;
        ivCancel.setEnabled(true);
        ivCancel.setScaleX(1.0f);
        ivCancel.setScaleY(1.0f);

        CheckPosition = true;

//        RelativeLayout.LayoutParams micParams = new RelativeLayout.LayoutParams((int) (width * 0.22), (int) (width * 0.22));
//        micParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        ivRecordMic.setLayoutParams(micParams);
        ivRecordMic.setVisibility(View.VISIBLE);
        ivRecordMic.setScaleX(0.8f);
        ivRecordMic.setScaleY(0.8f);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlMic.setLayoutParams(rlParam);

        tvNewAudioRecordHeaderText.setText(mContext.getString(R.string.tap_hold_to_record));
    }

    private final class MyTouchListener implements View.OnTouchListener {
        int prevY;

        public boolean onTouch(final View view, MotionEvent motionEvent) {

            final int y = (int) motionEvent.getRawY();

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                rippleBackground.startRippleAnimation();
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                        rlMic.getLayoutParams();
                layputParam = lParams;
                yDelta = y - lParams.topMargin;
                permissionCheck();
//                rippleBackground.startRippleAnimation();

            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                view.setVisibility(View.VISIBLE);
//                rippleBackground.startRippleAnimation();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlMic
                        .getLayoutParams();

                layoutParams.topMargin = y - yDelta;
                layoutParams.bottomMargin = 0;
                if (layoutParams.topMargin > 0 /*&& layoutParams.topMargin < 160*/) {

                    view.setVisibility(View.VISIBLE);
                    rlMic.setLayoutParams(layoutParams);

                    float cal = (float) (150 - layoutParams.topMargin) / 100;
                    float cal2 = (float) (50 + layoutParams.topMargin) / 100;
                    Log.d(TAG, "onTouch:CAL " + cal);
                    if (cal < 0.8) {
                        ivCancel.setScaleX(cal2);
                        ivCancel.setScaleY(cal2);
                    } else {
                        ivCancel.setScaleX(1.0f);
                        ivCancel.setScaleY(1.0f);
                    }
                    Log.d(TAG, "onTouch: " + layoutParams.topMargin);
                    if (cal < 0.8) {
                        ivRecordMic.setScaleX(cal);
                        ivRecordMic.setScaleY(cal);
                    } else {
                        ivRecordMic.setScaleX(1.0f);
                        ivRecordMic.setScaleY(1.0f);
                    }
//                    if (layoutParams.topMargin > 150 && CheckPosition) {
//
//
//                        Toast.makeText(mContext, "Audio Removed.", Toast.LENGTH_SHORT).show();
//
//                        if (mRecordType.equals(Config.MESSAGE_SEND)) {
//                            mDialog.dismiss();
//                            ((DirectMessageActivity) mContext).finish();
//
//                        }else if(mRecordType.equals(Config.MESSAGE_REPLY)){
//                            mDialog.dismiss();
//                        }
//
//                        CheckPosition = false;
//                    }

                } else {
                    if (layoutParams.topMargin < 0) {
                        layoutParams.topMargin = 0;
                        layoutParams.bottomMargin = 0;
                        rlMic.setLayoutParams(layoutParams);
                    } else if (layoutParams.topMargin > 160) {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlMic
                        .getLayoutParams();
                rippleBackground.stopRippleAnimation();
                float cal = (float) (150 - layoutParams.topMargin) / 100;
                Log.d(TAG, "onTouch:CAL " + cal);
                Log.d(TAG, "onTouch: " + layoutParams.topMargin);

                if (layoutParams.topMargin > 120 && CheckPosition) {

                    CheckPosition = false;

                    stoprecord();
                    changeDataValues();
                    Toast.makeText(mContext, "Audio Removed.", Toast.LENGTH_SHORT).show();
//                    if (mRecordType.equals(Config.MESSAGE_SEND)) {
//                        mDialog.dismiss();
//                        ((DirectMessageActivity) mContext).finish();
//                    } else if (mRecordType.equals(Config.MESSAGE_REPLY)) {
//                        mDialog.dismiss();
//                    } else if (mRecordType.equals(Config.CONFESSIONS)) {
//                        mDialog.dismiss();
//                    }

                } else {
                    stoprecord();
                    ivRecordMic.setScaleX(0.8f);
                    ivRecordMic.setScaleY(0.8f);
                    layoutParams.topMargin = 0;
                    layoutParams.bottomMargin = 0;
                    rlMic.setLayoutParams(layoutParams);
                }
                return true;
            } else {
                return false;
            }
            return false;
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            //  recordingTimeInSeconds = (int) lastsec;

            //   Log.d("TIMING", lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (tvNewAudioRecordHeaderText != null)
                            tvNewAudioRecordHeaderText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    recordingTimeInSeconds++;
                    Log.d(TAG, "recording time: " + recordingTimeInSeconds);

                    if (recordingTimeInSeconds >= maxRecordTimeInSeconds) {
                        stoprecord();
                    }

                }
            });

        }
    }

}
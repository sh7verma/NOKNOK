package com.app.noknok.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.definitions.Config;

/**
 * Created by dev on 22/8/17.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    RelativeLayout rlCancel, rlSettingsAboutus, rlSettingsTerms, rlSettingsPolicy, rlSettingsFeedback, rlSettingsTerlafriend, rlSettingsRateus;
    Switch swSettingNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        initListeners();
        switchNotificationChecked();
    }

    private void initUI() {

        rlCancel = (RelativeLayout) findViewById(R.id.relative_cancel);
        rlSettingsAboutus = (RelativeLayout) findViewById(R.id.rl_activity_settings_aboutus);
        rlSettingsTerms = (RelativeLayout) findViewById(R.id.rl_activity_settings_terms);
        rlSettingsPolicy = (RelativeLayout) findViewById(R.id.rl_activity_settings_policy);
        rlSettingsFeedback = (RelativeLayout) findViewById(R.id.rl_activity_settings_feedback);
        rlSettingsTerlafriend = (RelativeLayout) findViewById(R.id.rl_activity_settings_tellafriend);
        rlSettingsRateus = (RelativeLayout) findViewById(R.id.rl_activity_settings_rateus);
        swSettingNotification = (Switch) findViewById(R.id.sw_setting_notification);
    }

    private void initListeners() {
        rlCancel.setOnClickListener(this);
        rlSettingsAboutus.setOnClickListener(this);
        rlSettingsTerms.setOnClickListener(this);
        rlSettingsPolicy.setOnClickListener(this);
        rlSettingsFeedback.setOnClickListener(this);
        rlSettingsTerlafriend.setOnClickListener(this);
        rlSettingsRateus.setOnClickListener(this);
    }

    public void switchNotificationChecked() {

        if (sp.getString(Config.NOTIFICATION_ON, "true").equals("true")) {
            swSettingNotification.setChecked(true);
        } else if (sp.getString(Config.NOTIFICATION_ON, "false").equals("false")) {
            swSettingNotification.setChecked(false);
        }

        swSettingNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(getPackageName(),
                        Context.MODE_PRIVATE).edit();
                if (isChecked) {
                    editor.putString(Config.NOTIFICATION_ON, "true");
                } else {
                    editor.putString(Config.NOTIFICATION_ON, "false");
                }
                editor.apply();
            }
        });
        Log.d("NOTIFFFFFIICATIONONON", sp.getString(Config.NOTIFICATION_ON, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_cancel:
                onBackPressed();
                break;
            case R.id.rl_activity_settings_aboutus:
//                openGoogle();
                Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_activity_settings_terms:
//                openGoogle();
                Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();

                break;
            case R.id.rl_activity_settings_policy:
//                openGoogle();
                Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();

                break;
            case R.id.rl_activity_settings_feedback:
//                openGoogle();
                Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();

                break;
//            case R.id.rl_activity_settings_terlafriend:
//                onBackPressed();
//                break;
//            case R.id.rl_activity_settings_rateus:
//                onBackPressed();
//                break;
        }
    }

    void openGoogle() {
        Uri uri = Uri.parse("https://google.com");
        Intent inAbout = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(inAbout);
        } catch (Exception e) {
            Toast.makeText(this, "Browser not Instarled", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
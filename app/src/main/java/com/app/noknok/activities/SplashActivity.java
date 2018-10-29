package com.app.noknok.activities;

import android.content.Intent;
import android.os.Bundle;


import com.app.noknok.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dev on 7/7/17.
 */

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 100);
    }
}

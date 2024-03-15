package com.codehemu.banglanewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;


import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ProgressBar;


import com.google.android.gms.ads.MobileAds;
import com.codehemu.banglanewslivetv.models.Common;
import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.Preferences;
import com.codehemu.banglanewslivetv.services.ShortsDataService;


import java.util.Objects;


public class LauncherActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Preferences preferences;
    ShortsDataService service;
    int themeNo;
    ThemeConstant themeConstant;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(LauncherActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_loading);
        Objects.requireNonNull(getSupportActionBar()).hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        }

        progressBar = findViewById(R.id.determinateBar);
        progressBar.setScaleY(2f);
        progressBar.setMax(10);
        progressBar.setProgress(0);

        MobileAds.initialize(this);
        Application application = getApplication();
        ((MyApplication) application).loadAd(this);

        createTimer();

        if (!Common.isConnectToInternet(LauncherActivity.this)) {
            progressBar.setIndeterminate(true);
            return;
        }

        service = new ShortsDataService(this);
        service.getRssData(new ShortsDataService.OnDataResponse() {
            @Override
            public void onError(String error) {
                progressBar.setIndeterminate(true);
                Log.d(TAG, "1onError:" + error);
            }

            @Override
            public void onProcess(int i) {
                progressBar.setProgress(i);
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1onResponse:" + response);
            }

            @Override
            public void onPostExecute() {
                progressBar.setIndeterminate(true);
                service.getShortsData(new ShortsDataService.OnShortDataResponse() {
                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "1onError:" + error);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "1onResponse:" + response);
                    }

                    @Override
                    public void onPostExecute() {

                    }
                });

            }
        });


    }


    private void createTimer(){
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                Application application = getApplication();
                ((MyApplication) application).showAdIfAvailable(LauncherActivity.this, () -> {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    finish();
                });
            }
        };
        countDownTimer.start();
    }




}

package com.codehemu.banglanewslivetv;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codehemu.banglanewslivetv.models.Common;
import com.codehemu.banglanewslivetv.services.ShortDataAsync;


import java.util.Objects;


public class Loading extends AppCompatActivity {
    Handler handler;

    TextView textView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String versionName = BuildConfig.VERSION_NAME;
        textView = findViewById(R.id.textView);
        textView.setText("Version "+versionName);
        handler = new Handler();
        handler.postDelayed(() -> {
            Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
            finish();
        },1500);

        if (Common.isConnectToInternet(Loading.this)) {
            new ShortDataAsync(Loading.this).execute();
        }
    }


}
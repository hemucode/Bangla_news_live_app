package com.codehemu.banglanewslivetv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.codehemu.banglanewslivetv.models.Common;

public class Loading extends AppCompatActivity {
    Handler handler;
    CardView cardView;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cardView = findViewById(R.id.InternetAlert);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
                finish();
            }
        },2000);



    }
    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Common.isConnectToInternet(context)) {
                cardView.setVisibility(View.VISIBLE);
            } else {
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}
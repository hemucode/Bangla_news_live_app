package com.codehemu.banglanewslivetv;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codehemu.banglanewslivetv.models.Channel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class details extends AppCompatActivity {
    PlayerView playerView;
    ImageView fbLink, youtubeLink, webLink, fullScreen;
    TextView Description;
    boolean isFullScreen = false;
    ExoPlayer player;
    ProgressBar progressBar;
    private AdView adView,adView1,adView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        loadFacebookAds();

        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        getSupportActionBar().setTitle(channel.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playerView = findViewById(R.id.playerView);
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);
        progressBar = findViewById(R.id.progressBar);
        

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen){
                    loadFacebookAds();
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    if (getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);


                    isFullScreen = false;
                }else {
                    if (adView != null) {
                        adView.destroy();
                    }
                    if (adView2 != null) {
                        adView2.destroy();
                    }
                    if (adView1 != null) {
                        adView1.destroy();
                    }
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    if (getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    isFullScreen = true;
                }
            }
        });

        fbLink = findViewById(R.id.fbLink);
        fbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(channel.getFacebook());
            }
        });
        youtubeLink = findViewById(R.id.youtubeLink);
        youtubeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(channel.getYoutube());
            }
        });

        webLink = findViewById(R.id.webLink);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(channel.getWebsite());
            }
        });

        Description = findViewById(R.id.channelDes);
        Description.setText(channel.getDescription());
        Description.setSelected(true);

        playChannel(channel.getLive_url());

    }


    public void openLink(String url){
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void playChannel(String LiveUrl){
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(LiveUrl));

        player.setMediaSource(mediaSource);

        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }else if (playbackState == player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }

    public void loadFacebookAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        adView1 = findViewById(R.id.adView1);
        adView2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView1.loadAd(adRequest);
        adView2.loadAd(adRequest);

    }

}
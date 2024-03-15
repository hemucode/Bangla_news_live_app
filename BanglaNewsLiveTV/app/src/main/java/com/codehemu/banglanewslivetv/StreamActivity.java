package com.codehemu.banglanewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.ActivityInfo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.codehemu.banglanewslivetv.adopters.ChannelAdopters;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.models.Common;
import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.ChannelDataService;
import com.codehemu.banglanewslivetv.services.Preferences;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class StreamActivity extends AppCompatActivity {
    PlayerView playerView;
    ImageView fbLink, youtubeLink, webLink, fullScreen;
    TextView Description;
    boolean isFullScreen = false;
    boolean menuCondition = false;
    ExoPlayer player;
    ProgressBar progressBar;
    WebView web;
    ConstraintLayout MediaBox;
    RecyclerView newsChannelList;
    ChannelAdopters newsChannelAdopters;
    List<Channel> newsChannels;
    ChannelDataService service;
    String script =
            "var css = document.createElement('style');" +
                    "css.type = 'text/css';"+
                    "var head = document.head;" +
                    "css.innerText = `" +
                    ".ytp-show-cards-title," +
                    ".ytp-pause-overlay," +
                    ".branding-img," +
                    ".ytp-large-play-button," +
                    ".ytp-youtube-button," +
                    ".ytp-menuitem:nth-child(1)," +
                    ".ytp-small-redirect," +
                    ".ytp-menuitem:nth-child(4)" +
                    "{display:none !important;}`;" +
                    "head.appendChild(css);" +
                    "document.querySelector('.ytp-play-button').click();" +
                    "let ytpFullscreenButton = document.querySelector('.ytp-fullscreen-button');" +
                    "ytpFullscreenButton.addEventListener('click', function() { Android.showToast(`fullScreen`); });" +
                    "if(document.querySelector('.ytp-error-content-wrap-reason')){Android.showToast(`error`);}else{Android.showToast(`work`);}";

    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
    int themeNo;
    ThemeConstant themeConstant;
    boolean dark = false;
    String color;
    Preferences preferences;

    String title,youtubeID,category,ChannelLink,website,facebook,description;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(StreamActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_stream);

        Bundle extras = getIntent().getExtras();
        if (getIntent() != null && extras != null){
            this.title = extras.getString("name");
            this.youtubeID = extras.getString("youtube");
            this.category = extras.getString("category");
            this.ChannelLink = extras.getString("live_url");
            this.website = extras.getString("website");
            this.facebook = extras.getString("facebook");
            this.description = extras.getString("description");
            if (category.isEmpty() && ChannelLink.isEmpty()) return;

        }else {
            return;
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        service = new ChannelDataService(this);

        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });



        fbLink = findViewById(R.id.fbLink);
        fbLink.setOnClickListener(v -> openLink("https://www.facebook.com/"+facebook));

        youtubeLink = findViewById(R.id.youtubeLink);
        youtubeLink.setOnClickListener(v -> openLink("https://www.youtube.com/channel/"+ youtubeID));

        webLink = findViewById(R.id.webLink);
        webLink.setOnClickListener(v -> openLink(website));

        Description = findViewById(R.id.channelDes);
        Description.setText(description);
        Description.setSelected(true);

        playerView = findViewById(R.id.playerView);
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);


        progressBar = findViewById(R.id.progressBar);
        web =  findViewById(R.id.webView);
        MediaBox = findViewById(R.id.playerBox);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);

        if (category.equals("m3u8")) {
            menuCondition = true;
            web.setVisibility(View.INVISIBLE);
            playChannel(ChannelLink);
            fullScreen.setOnClickListener(v -> setFullScreen());


        }else if (category.equals("api")){
            playerView.setVisibility(View.INVISIBLE);
            playChannel("");
            progressBar.setVisibility(View.VISIBLE);
            service.getYoutubeData("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + youtubeID + "&eventType=live&maxResults=1&order=date&type=video&key=" + ChannelLink, new ChannelDataService.OnYTDataResponse() {
                @Override
                public void onError(String error) {
                    checkYouTubeLive(youtubeID);
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (!jsonObject.getString("id").isEmpty()) {
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("id"));
                            web.loadUrl("https://www.youtube.com/embed/" + jsonObject1.getString("videoId"));
                            web.setWebViewClient(new WebViewClient(){
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    web.evaluateJavascript(script,null);
                                    progressBar.setVisibility(View.GONE);
                                    super.onPageFinished(view, url);
                                }
                            });
                            web.addJavascriptInterface(new WebAppInterface(StreamActivity.this), "Android");
                        } else {
                            checkYouTubeLive(youtubeID);
                        }

                    } catch (JSONException e) {
                        checkYouTubeLive(youtubeID);
                    }
                }
            });

        }else {
            playerView.setVisibility(View.INVISIBLE);
            playChannel("");
            progressBar.setVisibility(View.VISIBLE);
            checkYouTubeLive(youtubeID);
        }


        newsChannelList = findViewById(R.id.recyclerView);
        newsChannelList.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false));
        getValueActivity(this);

    }

    public void getValueActivity(Context ctx){
        if (preferences.getFastChannelJson().equals("noValue") && Common.isConnectToInternet(ctx)) {
            service.getChannelData(getString(R.string.Bengali_news_json), new ChannelDataService.OnDataResponse() {
                @Override
                public void onError(String error) {
                }

                @Override
                public void onResponse(JSONArray response) {
                    getListActivity(response.toString(),ctx);
                    preferences.setFastChannelJson(response.toString());
                }
            });
        }else {
            getListActivity(preferences.getFastChannelJson(),ctx);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListActivity(String JsonValue, Context ctx){
        newsChannels = new ArrayList<>();
        newsChannelAdopters = new ChannelAdopters(ctx, newsChannels, "medium");
        newsChannelList.setAdapter(newsChannelAdopters);
        try {
            JSONArray jsonArray = new JSONArray(JsonValue);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    Channel c = new Channel();
                    c.setId(channelData.getInt("id"));
                    c.setName(channelData.getString("name"));
                    c.setDescription(channelData.getString("description"));
                    c.setLive_url(channelData.getString("live_url"));
                    c.setThumbnail(channelData.getString("thumbnail"));
                    c.setFacebook(channelData.getString("facebook"));
                    c.setYoutube(channelData.getString("youtube"));
                    c.setWebsite(channelData.getString("website"));
                    c.setCategory(channelData.getString("category"));
                    c.setLiveTvLink(channelData.getString("liveTvLink"));
                    c.setContact(channelData.getString("contact"));
                    newsChannels.add(c);
                    newsChannelAdopters.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.d(TAG, "1onError: " + e);

                }

            }

        }catch (JSONException e) {
            Log.d(TAG, "1onError: " + e);
        }

    }



    @SuppressLint("SourceLockedOrientationActivity")
    public void setFullScreen(){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) MediaBox.getLayoutParams();
        if (isFullScreen){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setDecorFitsSystemWindows(true);
                Objects.requireNonNull(MediaBox.getWindowInsetsController())
                        .show(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.statusBars()
                        | WindowInsets.Type.navigationBars());

                MediaBox.getRootWindowInsets().getInsets(WindowInsets.Type.statusBars()
                        | WindowInsets.Type.navigationBars());

            }else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }

            if (getSupportActionBar() != null){getSupportActionBar().show();}
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
            MediaBox.setLayoutParams(params);
            isFullScreen = false;
        }else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            MediaBox.setLayoutParams(params);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setDecorFitsSystemWindows(false);
                Objects.requireNonNull(MediaBox.getWindowInsetsController())
                        .hide(WindowInsets.Type.systemBars()
                                | WindowInsets.Type.statusBars()
                                | WindowInsets.Type.navigationBars());

                MediaBox.getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

                MediaBox.getRootWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.statusBars()
                                | WindowInsets.Type.navigationBars());

            }else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (getSupportActionBar() != null){getSupportActionBar().hide();}
            isFullScreen = true;

        }
    }

    public void openLink(String url){
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuCondition){
            getMenuInflater().inflate(R.menu.yt_live,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressedDispatcher.onBackPressed();
        }

        if (item.getItemId() == R.id.ytLive) {
            if (!youtubeID.isEmpty()){
                Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();
                checkYouTubeLive(youtubeID);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkYouTubeLive(String youtubeID) {
        web.loadUrl(" https://www.youtube.com/embed/live_stream?channel="+ youtubeID);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                web.evaluateJavascript(script,null);
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        web.addJavascriptInterface(new WebAppInterface(this), "Android");

    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {mContext = c;}
        @JavascriptInterface
        public void showToast(String toast) {
            if (toast.equals("fullScreen")){
                StreamActivity.this.runOnUiThread(StreamActivity.this::setFullScreen);
            }

            if (toast.equals("work")){
                StreamActivity.this.runOnUiThread(() -> {
                    if (player != null) {
                        player.setPlayWhenReady(false);
                        player.stop();
                        player.seekTo(0);
                    }
                    web.setVisibility(View.VISIBLE);
                    playerView.setVisibility(View.INVISIBLE);

                });
            }
            if (toast.equals("error")){
                StreamActivity.this.runOnUiThread(() -> {
                    final Dialog dialog = new Dialog(StreamActivity.this);
                    dialog.setContentView(R.layout.go_yt_live);
                    Button back = dialog.findViewById(R.id.back);
                    back.setOnClickListener(v -> dialog.cancel());
                    Button Go_live = dialog.findViewById(R.id.go_live);
                    Go_live.setOnClickListener(v -> openLink("https://www.youtube.com/channel/"+youtubeID));
                    dialog.show();
                });
            }

        }
    }


    @OptIn(markerClass = UnstableApi.class) public void playChannel(String LiveUrl){
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
                if (category.equals("m3u8")) {
                    if (playbackState == player.STATE_READY) {
                        progressBar.setVisibility(View.GONE);
                        player.setPlayWhenReady(true);
                    } else if (playbackState == player.STATE_BUFFERING) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        player.setPlayWhenReady(true);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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

}
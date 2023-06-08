package com.codehemu.banglanewslivetv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codehemu.banglanewslivetv.models.Channel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class ytVideoPlay extends AppCompatActivity {
    ImageView fbLink, youtubeLink, webLink, fullScreen;
    TextView Description;
    private WebView web;
    ProgressBar progressBar;
    boolean isFullScreen = false;
    private InterstitialAd interstitialAd;
    private AdView adView,adView1,adView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yt_video_play);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        loadFacebookAds();

        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        getSupportActionBar().setTitle(channel.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        interstitialAd = new InterstitialAd(this, getString(R.string.InterstitialAd));

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {}

            @Override
            public void onInterstitialDismissed(Ad ad) {}

            @Override
            public void onError(Ad ad, AdError adError) {}

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {}

            @Override
            public void onLoggingImpression(Ad ad) {}
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());


        web =  findViewById(R.id.webView);
        web.setVisibility(View.INVISIBLE);


        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);

        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String Script = "var css = document.createElement('style');" +
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
                        "css.type = 'text/css';"+
                        "let ytpFullscreenButton = document.querySelector('.ytp-fullscreen-button');" +
                        "ytpFullscreenButton.addEventListener('click', function() { Android.showToast(`toast`); });";

                web.evaluateJavascript(Script,null);
                try{
                    Thread.sleep(1000);
                    web.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }


            }
        });

        web.loadUrl(channel.getLive_url());

        web.addJavascriptInterface(new WebAppInterface(this), "Android");

        Description = findViewById(R.id.channelDes);
        Description.setSelected(true);
        Description.setText(channel.getDescription());



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

    }

    public void setFullScreen(){
        if (isFullScreen){
            if (adView != null) {
                adView.destroy();
            }
            if (adView2 != null) {
                adView2.destroy();
            }
            if (adView1 != null) {
                adView1.destroy();
            }
            if (getSupportActionBar() != null){
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) web.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) (300 * getApplicationContext().getResources().getDisplayMetrics().density);
            web.setLayoutParams(params);

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
            if (getSupportActionBar() != null){
                getSupportActionBar().hide();
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) web.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            web.setLayoutParams(params);

            isFullScreen = true;
        }
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

    public class WebAppInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void showToast(String toast) {
            ytVideoPlay.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFullScreen();
                }
            });
        }
    }
    public void loadFacebookAds() {
        if (adView != null) {
            adView.destroy();
        }
        if (adView2 != null) {
            adView2.destroy();
        }
        if (adView1 != null) {
            adView1.destroy();
        }
        adView = new AdView(this, getString(R.string.Banner_Ads_1), AdSize.BANNER_HEIGHT_50);

        adView1 = new AdView(this, getString(R.string.Banner_Ads_2), AdSize.RECTANGLE_HEIGHT_250);

        adView2 = new AdView(this, getString(R.string.Banner_Ads_2), AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        LinearLayout adContainer2 = (LinearLayout) findViewById(R.id.banner_container1);
        LinearLayout adContainer3 = (LinearLayout) findViewById(R.id.banner_container2);

        adContainer.addView(adView);
        adContainer2.addView(adView1);
        adContainer3.addView(adView2);

        adView.loadAd();
        adView1.loadAd();
        adView2.loadAd();

    }

}


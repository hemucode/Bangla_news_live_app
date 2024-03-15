package com.codehemu.banglanewslivetv;


import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import android.widget.ProgressBar;


import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.Preferences;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {
    WebView web;
    ProgressBar progressBar;
    Preferences preferences;
    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
    int themeNo;
    ThemeConstant themeConstant;
    boolean dark = false;
    String color;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(WebActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_web);
        web = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            progressBar.setVisibility(View.VISIBLE);

            String title = extras.getString("title");
            String url = extras.getString("url");


            getSupportActionBar().setTitle("AdBlocker");

            CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    Objects.requireNonNull(getSupportActionBar()).setTitle(title);
                }
            };
            countDownTimer.start();

            assert url != null;
            web.loadUrl(url);

            new AdBlockerWebView.init(this).initializeWebView(web);
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setBuiltInZoomControls(true);
            setDesktopMode(web,preferences.getSwitchState());
            web.setWebChromeClient(new MyChrome());
            web.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
                

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setProgress(100);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    return AdBlockerWebView.blockAds(view,url) ? AdBlocker.createEmptyResource() :
                            super.shouldInterceptRequest(view, url);
                }

            });

        }

    }
    public void setDesktopMode(WebView webView,boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            } catch (Exception ignored) {

            }
        } else {
            newUserAgent = null;
        }

        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed();
        }
        if (item.getItemId() == R.id.back) {
            web.goBack();
        }
        if (item.getItemId() == R.id.menu) {
            web.loadUrl("javascript:window.location.reload( true )");
        }
        if (item.getItemId() == R.id.forward) {
            web.goForward();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }

}

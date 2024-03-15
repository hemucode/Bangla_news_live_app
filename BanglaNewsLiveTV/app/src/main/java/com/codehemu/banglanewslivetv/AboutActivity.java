package com.codehemu.banglanewslivetv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.Preferences;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {
    TextView website,email,policy,version;
    Button button;
    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
    int themeNo;
    ThemeConstant themeConstant;
    boolean dark = false;
    String color;
    Preferences preferences;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(AboutActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.about_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        website = findViewById(R.id.webSite);
        email = findViewById(R.id.Email);
        policy = findViewById(R.id.policy);
        version = findViewById(R.id.version);
        button = findViewById(R.id.contain);

        button.setOnClickListener(v -> startActivity(new Intent(AboutActivity.this, ListingActivity.class).
                putExtra("ListType","containing")));

        website.setOnClickListener(v -> {
            String url = getString(R.string.my_website);
            openLink(url);
        });
        email.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String emailID = getString(R.string.my_email);
            String AppNAME = getString(R.string.app_name);
            Uri data = Uri.parse("mailto:"
                    + emailID
                    + "?subject=" +AppNAME+ " Feedback" + "&body=" + " ");
            intent.setData(data);
            startActivity(intent);
        });
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = info.versionName;
            version.setText("Version "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        policy.setOnClickListener(v -> WebViewPage(getString(R.string.policy_url)));



    }
    private void WebViewPage(String url) {
        Intent t = new Intent(AboutActivity.this, WebActivity.class);
        t.putExtra("title", "Privacy Policy");
        t.putExtra("url",url);
        startActivity(t);
    }
    public void openLink(String url){
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressedDispatcher.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


}
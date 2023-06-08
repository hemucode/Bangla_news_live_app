package com.codehemu.banglanewslivetv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class about extends AppCompatActivity {
    TextView website,email,policy,version;
    Button button;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.about_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_about);
        website = findViewById(R.id.webSite);
        email = findViewById(R.id.Email);
        policy = findViewById(R.id.policy);
        version = findViewById(R.id.version);
        button = findViewById(R.id.contain);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(about.this, contain.class);
                startActivity(i);
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.my_website);
                openLink(url);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String emailID = getString(R.string.my_email);
                String AppNAME = getString(R.string.app_name);
                Uri data = Uri.parse("mailto:"
                        + emailID
                        + "?subject=" +AppNAME+ " Feedback" + "&body=" + "");
                intent.setData(data);
                startActivity(intent);
            }
        });
        String versionName = BuildConfig.VERSION_NAME;
        version.setText("Version "+versionName);

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewPage("Privacy Policy", getString(R.string.policy_url));
            }
        });

        loadFacebookAds();

    }
    private void WebViewPage(String title,String url) {
        Intent cp = new Intent(about.this, webPage.class);
        cp.putExtra("title",title);
        cp.putExtra("url",url);
        startActivity(cp);
    }
    public void openLink(String url){
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }

    public void loadFacebookAds() {
        adView = new AdView(this, getString(R.string.Banner_Ads_1), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
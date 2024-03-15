package com.codehemu.banglanewslivetv;


import android.os.Bundle;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codehemu.banglanewslivetv.adopters.ShortsAdopters;

import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.models.VerticalViewPager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.codehemu.banglanewslivetv.services.Preferences;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.Objects;

public class ShortActivity extends AppCompatActivity {
    Preferences preferences;
    private InterstitialAd mInterstitialAd;
    ShortsAdopters shortsAdopters;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> link = new ArrayList<>();
    int themeNo;
    ThemeConstant themeConstant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(ShortActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_short);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(ShortActivity.this,getString(R.string.InterstitialAd), adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
        });

        String JsonValue = preferences.getShortDesAsync();

        if (!JsonValue.equals("noValue")){
            final VerticalViewPager verticalViewPages = findViewById(R.id.VerticalViewPage);

            shortsAdopters = new ShortsAdopters(ShortActivity.this,title,desc,image,link){
                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    if (position==5){
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(ShortActivity.this);
                        }
                    }
                    return super.instantiateItem(container, position);
                }

            };
            verticalViewPages.setAdapter(shortsAdopters);
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    title.add(channelData.getString("title"));
                    desc.add(channelData.getString("desc"));
                    image.add(channelData.getString("thumbnail"));
                    link.add(channelData.getString("link"));
                    shortsAdopters.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
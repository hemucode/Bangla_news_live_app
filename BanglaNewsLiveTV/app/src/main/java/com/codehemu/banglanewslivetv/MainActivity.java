package com.codehemu.banglanewslivetv;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
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


public class MainActivity extends AppCompatActivity{
    Preferences preferences;
    LinearLayout linearLayout;
    RecyclerView newsChannelList,newsChannelList2,newsChannelList3;
    ChannelAdopters newsChannelAdopters,newsChannelAdopters2,newsChannelAdopters3;
    List<Channel> newsChannels,newsChannels2,newsChannels3;
    ChannelDataService service;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    String appsName, packageName;
    TextView more_bengali,more_hindi;
    Button ePaper,englishNews,topNews,RateBtn,aboutBtn,shareBtn,setting,moreApp;
    Integer ItemList;
    boolean oneTime = true;
    boolean  dark = false;
    int themeNo;
    ThemeConstant themeConstant;
    String color;
    ReviewManager manager;
    ReviewInfo reviewInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(MainActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        service = new ChannelDataService(MainActivity.this);

        newsChannelList = findViewById(R.id.SliderList_1);
        newsChannelList2 = findViewById(R.id.SliderList_2);
        newsChannelList3 = findViewById(R.id.SliderList_3);

        newsChannelList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newsChannelList2.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannelList3.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));

        linearLayout = findViewById(R.id.DownloadLayout);
        if (Common.isConnectToInternet(this)){linearLayout.setVisibility(View.VISIBLE);}

        setAllChannelList(false);
        SwipeRefreshLayout mSwipe = findViewById(R.id.refresh_app);
        mSwipe.setOnRefreshListener(() -> {
            mSwipe.setRefreshing(false);
            setAllChannelList(true);
        });

        Application application = getApplication();
        ((MyApplication) application).loadAd(this);


        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();

        more_bengali = findViewById(R.id.more_bengali);
        more_bengali.setOnClickListener(v -> openListingActivity("bengaliNews"));

        more_hindi = findViewById( R.id.moreHindi);
        more_hindi.setOnClickListener(v -> openListingActivity("hindiNews"));

        //Button
        ePaper = findViewById(R.id.ePaper);
        ePaper.setOnClickListener(v -> openListingActivity("bengaliPaper"));

        englishNews = findViewById(R.id.englishBtn);
        englishNews.setOnClickListener(v -> openListingActivity("englishNews"));

        topNews = findViewById(R.id.topNews);
        topNews.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShortActivity.class)));

        RateBtn = findViewById(R.id.rateBtn);
        RateBtn.setOnClickListener(v -> openLink("https://play.google.com/store/apps/details?id=" + this.packageName));

        aboutBtn = findViewById(R.id.aboutAppBtn);
        aboutBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));

        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(v -> ShareAppLink());

        moreApp = findViewById(R.id.moreApp);
        moreApp.setOnClickListener(v -> openLink("https://play.google.com/store/apps/dev?id=7464231534566513633"));

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
        });


        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else {
                Toast.makeText(this, "Not Review", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItem) {startActivity(new Intent(MainActivity.this, MenuActivity.class));}
        if (item.getItemId() == R.id.rateHeart) {RateMe();}
        if (item.getItemId() == R.id.shorts) {startActivity(new Intent(MainActivity.this, ShortActivity.class));}
        return super.onOptionsItemSelected(item);
    }

    private void RateMe(){
        if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);
            flow.addOnCompleteListener(task -> {});
        }
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


    private void openListingActivity(String listType) {
        startActivity(new Intent(MainActivity.this, ListingActivity.class).
                putExtra("ListType",listType));
    }


    private void ShareAppLink() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", this.appsName);
        share.putExtra("android.intent.extra.TEXT", this.appsName + getString(R.string.download_it) + " https://play.google.com/store/apps/details?id=" + this.packageName);
        this.startActivity(Intent.createChooser(share, getString(R.string.share_it)));
    }

    public void openLink(String url) {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));}


    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView imageViewClose = findViewById(R.id.imageViewClose);
            CardView InternetAlert = findViewById(R.id.InternetAlert);
            if (!Common.isConnectToInternet(context)) {
                InternetAlert.setVisibility(View.VISIBLE);
                imageViewClose.setOnClickListener(v -> InternetAlert.setVisibility(View.GONE));
            }else {
                InternetAlert.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    public void setAllChannelList(boolean refresh) {
        getValueActivity(getString(R.string.Bengali_news_json),preferences.getFastChannelJson(),refresh,"1");
        getValueActivity(getString(R.string.Bengali_news_json),preferences.getFastChannelJson(),refresh,"2");
        getValueActivity(getString(R.string.Hindi_news_json),preferences.getSecondChannelJson(),refresh,"3");
    }

    public void getValueActivity(String url,String JsonValue,Boolean refresh,String ListNumber){
        if (JsonValue.equals("noValue") || refresh && Common.isConnectToInternet(MainActivity.this)) {
            service.getChannelData(url, new ChannelDataService.OnDataResponse() {
                @Override
                public void onError(String error) {
                    Log.d(TAG, "1onError:" + error);
                }

                @Override
                public void onResponse(JSONArray response) {
                    getListActivity(response.toString(),ListNumber);
                    switch (ListNumber) {
                        case "1":
                            preferences.setFastChannelJson(response.toString());
                            break;
                        case "3":
                            preferences.setSecondChannelJson(response.toString());
                            break;
                    }
                }
            });
        }else {
            getListActivity(JsonValue,ListNumber);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListActivity(String JsonValue, String ListNumber) {
        linearLayout.setVisibility(View.GONE);
        if (oneTime){nativeAd();oneTime = false;}
        try {
            JSONArray jsonArray = new JSONArray(JsonValue);
            switch (ListNumber) {
                case "1":
                    ItemList = jsonArray.length();
                    newsChannels = new ArrayList<>();
                    newsChannelAdopters = new ChannelAdopters(this, newsChannels, "big");
                    newsChannelList.setAdapter(newsChannelAdopters);
                    break;
                case "2":
                    ItemList = 8;
                    newsChannels2 = new ArrayList<>();
                    newsChannelAdopters2 = new ChannelAdopters(this, newsChannels2, "small");
                    newsChannelList2.setAdapter(newsChannelAdopters2);
                    break;
                case "3":
                    ItemList = 8;
                    newsChannels3 = new ArrayList<>();
                    newsChannelAdopters3 = new ChannelAdopters(this, newsChannels3, "small");
                    newsChannelList3.setAdapter(newsChannelAdopters3);
                    break;
            }

            for (int i = 0; i < ItemList; i++) {
                try {
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    Channel c = new Channel();
                    c.setId(channelData.getInt("id"));
                    c.setName(channelData.getString("name"));
                    c.setDescription(channelData.getString("description"));
                    c.setLive_url(channelData.getString("live_url"));
                    c.setFacebook(channelData.getString("facebook"));
                    c.setYoutube(channelData.getString("youtube"));
                    c.setWebsite(channelData.getString("website"));
                    c.setCategory(channelData.getString("category"));
                    c.setLiveTvLink(channelData.getString("liveTvLink"));
                    c.setContact(channelData.getString("contact"));
                    switch (ListNumber) {
                        case "1":
                            if (!channelData.getString("big_thumbnail").isEmpty()){
                                c.setThumbnail(channelData.getString("big_thumbnail"));
                                newsChannels.add(c);
                                newsChannelAdopters.notifyDataSetChanged();
                            }
                            break;

                        case "2":
                            c.setThumbnail(channelData.getString("thumbnail"));
                            newsChannels2.add(c);
                            newsChannelAdopters2.notifyDataSetChanged();
                            break;

                        case "3":
                            c.setThumbnail(channelData.getString("thumbnail"));
                            newsChannels3.add(c);
                            newsChannelAdopters3.notifyDataSetChanged();
                            break;
                    }

                } catch (JSONException e) {
                    throw new IllegalStateException("This is not Possible",e);
                }

            }

        }catch (JSONException e) {
            throw new IllegalStateException("This is not Possible",e);
        }
    }

    private void nativeAd(){
        MobileAds.initialize(this);
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.native_ads))
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().build();
                    TemplateView template = findViewById(R.id.my_template);
                    template.setVisibility(View.VISIBLE);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }


}
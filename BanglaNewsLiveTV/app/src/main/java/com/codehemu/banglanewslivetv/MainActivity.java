package com.codehemu.banglanewslivetv;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codehemu.banglanewslivetv.adopters.ChannelAdopters;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.models.Common;
import com.codehemu.banglanewslivetv.services.ChannelDataService;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AdView adView, adView2,adView3;
    public static final String TAG = "TAG";
    RecyclerView bigSliderList, newsChannelList, nationalChannelList;
    ChannelAdopters bigSliderAdopter, newsChannelAdopters, nationalChannelAdopters;
    List<Channel> channelList, newsChannels, nationalChannels;
    ChannelDataService service;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    CardView cardView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    String appsName, packageName;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    TextView more_bengali,more_hindi;
    Button ePaper,englishNews,topNews,RateBtn,aboutBtn,shareBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();

        more_bengali = findViewById(R.id.more_bengali);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        service = new ChannelDataService(this);

        mSwipeRefreshLayout = findViewById(R.id.refresh_app);
        cardView = findViewById(R.id.InternetAlert);

        LoadEverything();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                LoadEverything();
            }
        });


        more_bengali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more_channel("channel","Bengali Channel List",getString(R.string.all_bengali_channel_json));
            }
        });
        more_hindi = findViewById(R.id.moreHindi);
        more_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more_channel("channel","National Channel List",getString(R.string.all_hindi_channel_json));
            }
        });

        ePaper = findViewById(R.id.ePaper);
        ePaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more_channel("ePaper","e-Paper",getString(R.string.all_bengali_paper_json));
            }
        });
        englishNews = findViewById(R.id.englishBtn);
        englishNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more_channel("channel","English Channel List",getString(R.string.all_english_channel));
            }
        });

        topNews = findViewById(R.id.topNews);
        topNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewPage("Top News", getString(R.string.google_top_news));
            }
        });

        RateBtn = findViewById(R.id.rateBtn);
        RateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkRateUs();
            }
        });
        aboutBtn = findViewById(R.id.aboutAppBtn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cp = new Intent(MainActivity.this, about.class);
                startActivity(cp);
            }
        });
        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkShareApp();
            }
        });

        RequestReviewInfo();

    }

    private void more_channel(String type, String title, String url) {
        Intent cp = new Intent(MainActivity.this, more.class);
        cp.putExtra("type",type);
        cp.putExtra("title",title);
        cp.putExtra("url",url);
        startActivity(cp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rateHeart) {
            RateMe();
        }
        return super.onOptionsItemSelected(item);
    }

    private void RateMe(){
        if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);
            flow.addOnCompleteListener(task -> {
            });
        }

    }

    private void RequestReviewInfo(){
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

    public void LoadEverything() {
        loadJsonChannel();
        if (adView != null) {
            adView.destroy();
        }
        if (adView2 != null) {
            adView2.destroy();
        }
        if (adView3 != null) {
            adView3.destroy();
        }
        loadFacebookAds();
    }

    public void loadJsonChannel() {
          getSliderData(getString(R.string.Big_banner_json));
          getNewsChannels(getString(R.string.all_bengali_channel_json));
          getNationalChannels(getString(R.string.all_hindi_channel_json));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.home) {
        }

        if (item.getItemId() == R.id.contain) {
            Intent i = new Intent(MainActivity.this, contain.class);
            startActivity(i);
        }

        if (item.getItemId() == R.id.policy) {
            WebViewPage("Privacy Policy", getString(R.string.policy_url));
        }

        if (item.getItemId() == R.id.share) {
            LinkShareApp();
        }

        if (item.getItemId() == R.id.rate) {
           LinkRateUs();
        }
        if (item.getItemId() == R.id.about) {
            Intent cp = new Intent(MainActivity.this, about.class);
            startActivity(cp);
        }
        if (item.getItemId() == R.id.website) {
            openLink("https://www.codehemu.com/");
        }
        if (item.getItemId() == R.id.fb) {
            openLink("https://www.facebook.com/codehemu/");
        }
        if (item.getItemId() == R.id.yt) {
            openLink("https://www.youtube.com/c/HemantaGayen");
        }
        return false;
    }

    private void WebViewPage(String title,String url) {
        Intent cp = new Intent(MainActivity.this, webPage.class);
        cp.putExtra("title",title);
        cp.putExtra("url",url);
        startActivity(cp);
    }

    private void LinkShareApp() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", MainActivity.this.appsName);
        String APP_Download_URL = "https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName;
        share.putExtra("android.intent.extra.TEXT", MainActivity.this.appsName + " - এপ্সটি ডাউনলোড করতে নিচের লিংকে যান\n\n" + APP_Download_URL);
        MainActivity.this.startActivity(Intent.createChooser(share, "শেয়ার করুন"));
    }

    private void LinkRateUs() {
        try {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent2);}
        catch (Exception e){
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent);
        }
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

    public void loadFacebookAds() {
        adView = new AdView(this, getString(R.string.Banner_Ads_1), AdSize.BANNER_HEIGHT_90);
        adView3 = new AdView(this, getString(R.string.Banner_Ads_1), AdSize.BANNER_HEIGHT_90);

        adView2 = new AdView(this, getString(R.string.Banner_Ads_2), AdSize.BANNER_HEIGHT_90);

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        LinearLayout adContainer2 = (LinearLayout) findViewById(R.id.banner_container2);
        LinearLayout adContainer3 = (LinearLayout) findViewById(R.id.banner_container3);

        adContainer.addView(adView);
        adContainer2.addView(adView2);
        adContainer3.addView(adView3);

        adView.loadAd();
        adView2.loadAd();
        adView3.loadAd();

    }


    public void getSliderData(String url) {
        bigSliderList = findViewById(R.id.bigSliderList);
        channelList = new ArrayList<>();
        bigSliderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bigSliderAdopter = new ChannelAdopters(this, channelList, "slider");
        bigSliderList.setAdapter(bigSliderAdopter);
        service.getChannelData(url, new ChannelDataService.OnDataResponse() {
            @Override
            public Void onError(String error) {
                Log.d(TAG, "onErrorResponse: " + error);
                return null;
            }

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject channelData =  response.getJSONObject(i);
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
                        channelList.add(c);
                        bigSliderAdopter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }


    public void getNewsChannels(String url) {
        newsChannelList = findViewById(R.id.Bengoli_News_Channel_List);
        newsChannels = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        newsChannelList.setLayoutManager(gridLayoutManager);
        newsChannelList.setHasFixedSize(true);

        newsChannelAdopters = new ChannelAdopters(this, newsChannels, "category");
        newsChannelList.setAdapter(newsChannelAdopters);
        service.getChannelData(url, new ChannelDataService.OnDataResponse() {
            @Override
            public Void onError(String error) {
                Log.d(TAG, "onErrorResponse: " + error);
                return null;
            }

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < 12; i++) {
                    try {
                        JSONObject channelData =  response.getJSONObject(i);
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
                        newsChannels.add(c);
                        newsChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public void getNationalChannels(String url) {
        nationalChannelList = findViewById(R.id.National_News_Channel_List);
        nationalChannels = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        nationalChannelList.setLayoutManager(gridLayoutManager);
        nationalChannelList.setHasFixedSize(true);

        nationalChannelAdopters = new ChannelAdopters(this, nationalChannels, "category");
        nationalChannelList.setAdapter(nationalChannelAdopters);
        service.getChannelData(url, new ChannelDataService.OnDataResponse() {
            @Override
            public Void onError(String error) {
                Log.d(TAG, "onErrorResponse: " + error);
                return null;
            }

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < 8; i++) {
                    try {
                        JSONObject channelData =  response.getJSONObject(i);
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
                        nationalChannels.add(c);
                        nationalChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public void openLink(String url) {
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }


}
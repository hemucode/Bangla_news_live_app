package com.codehemu.banglanewslivetv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codehemu.banglanewslivetv.adopters.ChannelAdopters;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.services.ChannelDataService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class more extends AppCompatActivity {
   private AdView adView;
   public static final String TAG = "TAG";
    RecyclerView newsChannelList;
    ChannelAdopters newsChannelAdopters;
    List<Channel> newsChannels;
    ChannelDataService service;
    String type;
    int spanCount;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadFacebookAds();

        progressBar = findViewById(R.id.progressBar4);

        service = new ChannelDataService(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String type = extras.getString("type");
            String title = extras.getString("title");
            String url = extras.getString("url");
            getSupportActionBar().setTitle(title);

            if (type.equals("channel")){
                this.type = "category";
                this.spanCount = 4;
                getNewsChannels(url);
            }
            if (type.equals("ePaper")){
                this.type = "ePaper";
                this.spanCount = 2;
                getNewsChannels(url);
            }

        }else {
            Intent i = new Intent(more.this, MainActivity.class);
            startActivity(i);
        }
    }

    public void loadFacebookAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    public void getNewsChannels(String url) {
        newsChannelList = findViewById(R.id.Channel_List);
        newsChannels = new ArrayList<>();
        newsChannelList.setLayoutManager(new GridLayoutManager(this,spanCount,LinearLayoutManager.VERTICAL,false));
        newsChannelAdopters = new ChannelAdopters(this, newsChannels, type){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                progressBar.setVisibility(View.GONE);
            }
        };
        newsChannelList.setAdapter(newsChannelAdopters);
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
                        newsChannels.add(c);
                        newsChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
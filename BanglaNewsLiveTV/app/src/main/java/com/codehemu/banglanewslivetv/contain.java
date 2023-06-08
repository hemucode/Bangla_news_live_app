package com.codehemu.banglanewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codehemu.banglanewslivetv.adopters.InfoChannelAdopters;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.services.ChannelDataService;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class contain extends AppCompatActivity {
    RecyclerView nationalChannelList;
    InfoChannelAdopters nationalChannelAdopters;
    List<Channel> nationalChannels;
    ChannelDataService service;
    ProgressBar progressBar;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contain);
        loadFacebookAds();

        getSupportActionBar().setTitle(R.string.containing_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar2);

        service = new ChannelDataService(this);
        getNationalChannels(getString(R.string.all_bengali_channel_json));
        getNationalChannels(getString(R.string.all_hindi_channel_json));
        getNationalChannels(getString(R.string.all_english_channel));

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


    public void getNationalChannels(String url) {
        nationalChannelList = findViewById(R.id.nationalChannelList);
        nationalChannels = new ArrayList<>();
        nationalChannelList.setLayoutManager(new GridLayoutManager(this,1, LinearLayoutManager.VERTICAL,false));

        nationalChannelAdopters = new InfoChannelAdopters(this,nationalChannels,"list"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                progressBar.setVisibility(View.GONE);
            }
        };
        nationalChannelList.setAdapter(nationalChannelAdopters);
        service.getChannelData(url, new ChannelDataService.OnDataResponse() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject channelData = response.getJSONObject(i);
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

                        nationalChannels.add(c);
                        nationalChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return;
            }

            @Override
            public Void onError(String error) {
                Log.d(TAG, "onErrorResponse: " + error);
                return null;
            }
        });

    }


}
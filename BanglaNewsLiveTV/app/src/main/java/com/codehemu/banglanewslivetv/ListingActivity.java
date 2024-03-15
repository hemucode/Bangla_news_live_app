package com.codehemu.banglanewslivetv;




import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class ListingActivity extends AppCompatActivity {
    Preferences preferences;
    RecyclerView newsChannelList;
    ChannelAdopters newsChannelAdopters;
    List<Channel> newsChannels;
    ChannelDataService service;
    ProgressBar progressBar;
    SwipeRefreshLayout mSwipe;
    String ListType;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
    int themeNo;
    ThemeConstant themeConstant;
    boolean dark = false;
    String color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(ListingActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_listing);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });


        service = new ChannelDataService(this);
        mSwipe = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ListType = extras.getString("ListType");
            assert ListType != null;
            newsChannelList = findViewById(R.id.recyclerView);

            switch (ListType) {
                case "containing":
                    getSupportActionBar().setTitle(R.string.containing_information);
                    final Dialog dialog = getDialog();
                    dialog.show();
                    break;

                case "bengaliNews":
                    getSupportActionBar().setTitle(R.string.bengali_channel);
                    setAllChannelList("1",false);
                    break;

                case "hindiNews":
                    getSupportActionBar().setTitle(R.string.national_news_channel);
                    setAllChannelList("2",false);
                    break;

                case "englishNews":
                    getSupportActionBar().setTitle(R.string.english_news_channel);
                    setAllChannelList("3",false);
                    break;

                case "bengaliPaper":
                    getSupportActionBar().setTitle(R.string.e_paper);
                    setAllChannelList("4",false);
                    break;

            }
        }

    }

    @NonNull
    private Dialog getDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.containing);
        dialog.setCanceledOnTouchOutside(false);
        Button button = dialog.findViewById(R.id.bengali);
        Button button2 = dialog.findViewById(R.id.hindi);
        Button button3 = dialog.findViewById(R.id.english);
        button.setOnClickListener(v -> {
            setAllChannelList("1",false);
            dialog.cancel();
        });
        button2.setOnClickListener(v -> {
            setAllChannelList("2",false);
            dialog.cancel();
        });
        button3.setOnClickListener(v -> {
            setAllChannelList("3",false);
            dialog.cancel();
        });
        return dialog;
    }

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


    private void setAllChannelList(String no,Boolean refresh){
        mSwipe.setOnRefreshListener(() -> {
            mSwipe.setRefreshing(false);
            setAllChannelList(no,true);
        });
        switch (no) {
            case "1":
                getValueActivity(getString(R.string.Bengali_news_json),preferences.getFastChannelJson(),refresh,1);
                break;
            case "2":
                getValueActivity(getString(R.string.Hindi_news_json),preferences.getSecondChannelJson(),refresh,2);
                break;
            case "3":
                getValueActivity(getString(R.string.English_news_json),preferences.getThirdChannelJson(),refresh,3);
                break;
            case "4":
                getValueActivity(getString(R.string.Bengali_paper_json),preferences.getPaperJson(),refresh,4);
                break;
        }
    }

    public void getValueActivity(String url,String JsonValue,Boolean refresh, int number){

        if (JsonValue.equals("noValue") || refresh && Common.isConnectToInternet(this)) {
            service.getChannelData(url, new ChannelDataService.OnDataResponse() {
                @Override
                public void onError(String error) {

                }

                @Override
                public void onResponse(JSONArray response) {
                    getListActivity(response.toString());
                    switch (number){
                        case 1:
                            preferences.setFastChannelJson(response.toString());
                            break;
                        case 2:
                            preferences.setSecondChannelJson(response.toString());
                            break;
                        case 3:
                            preferences.setThirdChannelJson(response.toString());
                            break;
                        case 4:
                            preferences.setPaperJson(response.toString());
                            break;
                    }
                }
            });
        }else {
          getListActivity(JsonValue);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressedDispatcher.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressLint("NotifyDataSetChanged")
    private void getListActivity(String JsonValue) {
        progressBar.setVisibility(View.GONE);
        newsChannels = new ArrayList<>();
        String listData;
        switch (ListType) {
            case "containing":
                listData = "details";
                break;
            case "bengaliPaper":
                listData = "paper";
                break;
            default:
                listData = "length";
        }

        newsChannelList.setLayoutManager(new GridLayoutManager(this,1, LinearLayoutManager.VERTICAL,false));
        newsChannelAdopters = new ChannelAdopters(this, newsChannels, listData);
        newsChannelList.setAdapter(newsChannelAdopters);

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(JsonValue);
       
        for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    Channel c = new Channel();
                    c.setId(channelData.getInt("id"));
                    c.setName(channelData.getString("name"));
                    c.setLive_url(channelData.getString("live_url"));
                    c.setThumbnail(channelData.getString("thumbnail"));
                    c.setWebsite(channelData.getString("website"));
                    c.setCategory(channelData.getString("category"));
                    if (!ListType.equals("bengaliPaper")){
                        c.setFacebook(channelData.getString("facebook"));
                        c.setYoutube(channelData.getString("youtube"));
                        c.setLiveTvLink(channelData.getString("liveTvLink"));
                        c.setContact(channelData.getString("contact"));
                        c.setDescription(channelData.getString("description"));
                    }
                    newsChannels.add(c);
                    newsChannelAdopters.notifyDataSetChanged();


                } catch (JSONException ignored) {
                 
                }
            }

        } catch (JSONException ignored) {
   
        }

    }

    @Override
    protected void onResume() {
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onResume();
    }

}
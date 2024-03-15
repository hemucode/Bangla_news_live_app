package com.codehemu.banglanewslivetv.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences extends Activity {
    Context context;
    SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("PREF", MODE_PRIVATE);
    }
    public int getRssArrayLinkNumber(){
        return sharedPreferences.getInt("rss_link",  0);
    }
    public void setRssArrayLinkNumber(int i){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rss_link", i).apply();
    }

    public String getShortDataAsync(){
        return sharedPreferences.getString("shorts_data",  "noValue");
    }

    public void setShortDataAsync(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("shorts_data", s).apply();
    }

    public String getShortDesAsync(){
        return sharedPreferences.getString("shorts_des",  "noValue");
    }

    public void setShortDesAsync(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("shorts_des", s).apply();
    }

    public String getFastChannelJson(){
        return sharedPreferences.getString("fast_json",  "noValue");
    }

    public void setFastChannelJson(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fast_json", s).apply();
    }

    public String getSecondChannelJson(){
        return sharedPreferences.getString("second_json",  "noValue");
    }

    public void setSecondChannelJson(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("second_json", s).apply();
    }

    public String getThirdChannelJson(){
        return sharedPreferences.getString("third_json",  "noValue");
    }

    public void setThirdChannelJson(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("third_json", s).apply();
    }

    public String getPaperJson(){
        return sharedPreferences.getString("paper_json",  "noValue");
    }

    public void setPaperJson(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("paper_json", s).apply();
    }
    public boolean getMode() {
        return sharedPreferences.getBoolean("mode", false);
    }

    public void setMode(boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mode", bool).apply();
    }

    public boolean getSwitchState(){
        return sharedPreferences.getBoolean("switch", false);
    }
    public void setSwitchState(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switch", b).apply();
    }

    public int getThemeNo() {
        return sharedPreferences.getInt("theme", 0);
    }

    public void setThemeNo(int i) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", i).apply();
    }

    public String getCircleColor() {
        return sharedPreferences.getString("circlecolor", "#04a8f5");
    }

    public void setCircleColor(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("circlecolor", s).apply();
    }


}

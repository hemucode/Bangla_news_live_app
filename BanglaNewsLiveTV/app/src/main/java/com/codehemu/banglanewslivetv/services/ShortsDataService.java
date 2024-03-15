package com.codehemu.banglanewslivetv.services;

import android.app.Activity;
import android.content.Context;

import com.codehemu.banglanewslivetv.R;
import com.codehemu.banglanewslivetv.models.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShortsDataService {
    Context mContext;
    Preferences preferences;
    boolean requestJson;

    public ShortsDataService(Context mContext){
        this.mContext = mContext;
        this.preferences = new Preferences(mContext);
    }

    public interface OnDataResponse{
        void onError(String error);
        void onProcess(int i);
        void onResponse(String response);
        void onPostExecute();
    }
    public interface OnShortDataResponse{
        void onError(String error);
        void onResponse(String response);
        void onPostExecute();
    }

    public void getRssData(ShortsDataService.OnDataResponse onDataResponse){
        if (!Common.isConnectToInternet(mContext)) return;
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            //doInBackground Method of AsyncTask
            Document document;
            Element titleE,linksE,thumbnailE;
            String title,links,thumbnail;
            try {
                document = Jsoup.connect(mContext.getResources().getStringArray(R.array.rssURL)[preferences.getRssArrayLinkNumber()]).get();
                JSONArray arr = new JSONArray();
                HashMap<String, JSONObject> map = new HashMap<>();
                Elements ntmAmount = document.select("item");
                if (ntmAmount.isEmpty()) {
                    onDataResponse.onError("RSS Item Element not Found");
                    return;
                }

                for (int i =0; i< Math.min(ntmAmount.size(), 10); i++){
                    titleE = document.select("item").select("title").get(i);
                    linksE = document.select("item").select("link").get(i);
                    thumbnailE = document.select("item").select("media|content").get(i);

                    if (titleE!=null && linksE!=null && thumbnailE!=null){
                        title = titleE.text();
                        links = linksE.text();
                        thumbnail = thumbnailE.attr("url");
                        JSONObject json = new JSONObject();
                        json.put("id",i);
                        json.put("title",title);
                        json.put("link",links);
                        json.put("thumbnail",thumbnail);
                        map.put("json" + i, json);
                        arr.put(map.get("json" + i));
                    }else {
                        onDataResponse.onError("RSS Feed Element not Found..");
                    }
                    onDataResponse.onProcess(i);

                }

                preferences.setShortDataAsync(arr.toString());
                onDataResponse.onResponse(arr.toString());

            } catch (IOException e) {
                onDataResponse.onError("RSS Feed Url Connect Error =" + e);
            } catch (JSONException e) {
                onDataResponse.onError("RSS Feed Json Load Error =" + e);
            }
            //change View Data
            ((Activity)mContext).runOnUiThread(onDataResponse::onPostExecute);
        });
    }

    public void getShortsData(OnShortDataResponse OnShortDataResponse){
        if (!Common.isConnectToInternet(mContext)) return;
        ExecutorService services = Executors.newSingleThreadExecutor();
        services.execute(() -> {
                String ShotsJson = preferences.getShortDataAsync();
                String ShotsDes = preferences.getShortDesAsync();
                if (!ShotsJson.equals("noValue")) {
                    if (ShotsDes.equals("noValue")) {
                        requestJson = true;
                    } else {
                        JSONArray jsonArray, jsonArrayEdit;
                        JSONObject row, edit;
                        try {
                            jsonArray = new JSONArray(ShotsJson);
                            jsonArrayEdit = new JSONArray(ShotsDes);
                            row = jsonArray.getJSONObject(0);
                            edit = jsonArrayEdit.getJSONObject(0);
                            if (!row.getString("title").equals(edit.getString("title"))) {
                                requestJson = true;
                            }
                        } catch (JSONException e) {
                            OnShortDataResponse.onError("No Shorts");
                        }
                    }
                }

                if (requestJson) {
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(ShotsJson);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    JSONArray arr = new JSONArray();
                    HashMap<String, JSONObject> map = new HashMap<>();

                    String desc;
                    Document documents;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject channelData = jsonArray.getJSONObject(i);

                            try {
                                documents = Jsoup.connect(channelData.getString("link")).get();
                                Elements rightSec = documents.select(".khbr_rght_sec").select("p");
                                Elements container = documents.select(".container").select("p");
                                Elements slider_con = documents.select(".slider_con").select("p");
                                Elements all_p = documents.select("p");
                                if (rightSec.first() != null) {
                                    if (rightSec.text().length() > 400) {
                                        desc = rightSec.text().substring(0, 400);
                                    } else {
                                        desc = rightSec.text();
                                    }
                                } else if (container.first() != null) {
                                    if (container.text().length() > 400) {
                                        desc = container.text().substring(0, 400);
                                    } else {
                                        desc = container.text();
                                    }
                                } else if (slider_con.first() != null) {
                                    if (slider_con.text().length() > 401) {
                                        desc = slider_con.text().substring(0, 400);
                                    } else {
                                        desc = slider_con.text();
                                    }
                                } else if (all_p.first() != null) {
                                    if (all_p.text().length() > 400) {
                                        desc = all_p.text().substring(0, 400);
                                    } else {
                                        desc = all_p.text();
                                    }
                                } else {
                                    desc = "NOT FOUND...";
                                    OnShortDataResponse.onError("Shorts description not found");
                                }
                            } catch (IOException e) {
                                OnShortDataResponse.onError("RSS link error");
                                continue;

                            }

                            JSONObject json = new JSONObject();
                            json.put("id", i);
                            json.put("title", channelData.getString("title"));
                            json.put("desc", desc);
                            json.put("link", channelData.getString("link"));
                            json.put("thumbnail", channelData.getString("thumbnail"));
                            map.put("json" + i, json);
                            arr.put(map.get("json" + i));
                            OnShortDataResponse.onResponse(desc);
                        } catch (JSONException e) {
                            OnShortDataResponse.onError("Json error");
                        }
                    }
                    preferences.setShortDesAsync(arr.toString());
                    ((Activity)mContext).runOnUiThread(OnShortDataResponse::onPostExecute);

                }

            });
    }



}

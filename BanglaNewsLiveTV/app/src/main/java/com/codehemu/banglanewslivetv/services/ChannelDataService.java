package com.codehemu.banglanewslivetv.services;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codehemu.banglanewslivetv.models.Common;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChannelDataService {
    Context ctx;
    public static final String TAG = "TAG";

    public ChannelDataService(Context ctx) {
        this.ctx = ctx;
    }

    public interface OnDataResponse{
        void onError(String error);
        void onResponse(JSONArray response);
    }
    public interface OnYTDataResponse{
        void onError(String error);
        void onResponse(JSONObject response);
    }

    public void getChannelData(String url, OnDataResponse onDataResponse){
        if (!Common.isConnectToInternet(ctx)) return;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onDataResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDataResponse.onError(error.getLocalizedMessage());
                Log.d(TAG, "1onError: " + error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);

    }

    public void getYoutubeData(String url, ChannelDataService.OnYTDataResponse onYTDataResponse){
        if (!Common.isConnectToInternet(ctx)) return;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        onYTDataResponse.onResponse(response);
                        Log.d(TAG,"1onResponse: " + response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onYTDataResponse.onError(error.getLocalizedMessage());
            }
        });
        queue.add(jsonObjectRequest);

    }

}
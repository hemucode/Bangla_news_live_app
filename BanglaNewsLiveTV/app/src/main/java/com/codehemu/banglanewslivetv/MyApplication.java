package com.codehemu.banglanewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;


public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager(this);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
    public void loadAd(@NonNull Activity activity){
        appOpenAdManager.loadAd(activity);
    }

    public interface OnShowAdCompleteListener{
        void onAdShown();
    }
    public void showAdIfAvailable(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener){
        appOpenAdManager.showAdIfAvailable(activity,onShowAdCompleteListener);
    }
    private static class AppOpenAdManager{
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        public AppOpenAdManager(MyApplication myApplication){

        }

        private void loadAd(Context context){
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();

            AppOpenAd.load(context, context.getString(R.string.open_ads), request, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoadingAd = false;
                    Log.d(TAG, "1on: Ad Loading Error");

                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd openAd) {
                    super.onAdLoaded(openAd);
                    appOpenAd = openAd;
                    isLoadingAd = false;
                    Log.d(TAG, "1on: AdLoad");
                }

            });

        }

        private boolean isAdAvailable(){
            return appOpenAd != null;
        }

        private void showAdIfAvailable(Activity activity){
            showAdIfAvailable(activity, new OnShowAdCompleteListener() {
                @Override
                public void onAdShown() {

                }
            });
        }

        private void showAdIfAvailable(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener){
            if(isShowingAd){
                return;
            }
            if (!isAdAvailable()){
                onShowAdCompleteListener.onAdShown();
                return;
            }

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    isShowingAd = false;
                    onShowAdCompleteListener.onAdShown();
                    appOpenAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    isShowingAd = false;
                    onShowAdCompleteListener.onAdShown();
                    appOpenAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);

        }


    }
}

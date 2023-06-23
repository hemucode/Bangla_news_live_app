package com.codehemu.banglanewslivetv.adopters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codehemu.banglanewslivetv.R;
import com.codehemu.banglanewslivetv.details;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.services.CacheImageManager;
import com.codehemu.banglanewslivetv.webPage;
import com.codehemu.banglanewslivetv.ytVideoPlay;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelAdopters extends RecyclerView.Adapter<ChannelAdopters.ViewHolder> {
    List<Channel> channels;
    private InterstitialAd mInterstitialAd;
    String type;
    private Context mContext;
    private Map<String, Bitmap> mBitmaps = new HashMap<>();


    public ChannelAdopters(Context mContext, List<Channel> channels, String type) {
        this.channels = channels;
        this.type = type;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (type.equals("slider")) {
            v = LayoutInflater.from(mContext).inflate(R.layout.big_slider_view, parent, false);
        } else if(type.equals("ePaper")) {
            v = LayoutInflater.from(mContext).inflate(R.layout.paper_slider_view, parent, false);
        }else {
            v = LayoutInflater.from(mContext).inflate(R.layout.category_slider_view, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelAdopters.ViewHolder holder, int position) {
        int positions = position;
        Channel ChannelDataItem = channels.get(position);
        String Category = channels.get(position).getCategory();

        holder.textView.setText(channels.get(position).getName());

        setAds();


        try {
            Bitmap bitmap = CacheImageManager.getImage(mContext, ChannelDataItem);
            if (bitmap == null) {
                MyImageTask task = new MyImageTask();
                task.setViewHolder(holder);
                task.execute(ChannelDataItem);
            } else {
                holder.imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Category.equals("yt")) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show((Activity) v.getContext());
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                Intent t = new Intent(v.getContext(), ytVideoPlay.class);
                                t.putExtra("channel", channels.get(positions));
                                v.getContext().startActivity(t);
                                mInterstitialAd = null;
                                setAds();
                            }
                        });
                    }else {
                        Intent t = new Intent(v.getContext(), ytVideoPlay.class);
                        t.putExtra("channel", channels.get(positions));
                        v.getContext().startActivity(t);
                    }

                }else if (Category.equals("m3u8")) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show((Activity) v.getContext());
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                Intent p = new Intent(v.getContext(), details.class);
                                p.putExtra("channel", channels.get(positions));
                                v.getContext().startActivity(p);
                                mInterstitialAd = null;
                                setAds();
                            }
                        });
                    }else {
                        Intent p = new Intent(v.getContext(), details.class);
                        p.putExtra("channel", channels.get(positions));
                        v.getContext().startActivity(p);
                    }

                } else {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show((Activity) v.getContext());
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                Intent t = new Intent(v.getContext(), webPage.class);
                                t.putExtra("title",channels.get(positions).getName());
                                t.putExtra("url",channels.get(positions).getLive_url());
                                v.getContext().startActivity(t);
                                mInterstitialAd = null;
                                setAds();
                            }
                        });
                    }else {
                        Intent t = new Intent(v.getContext(), webPage.class);
                        t.putExtra("title",channels.get(positions).getName());
                        t.putExtra("url",channels.get(positions).getLive_url());
                        v.getContext().startActivity(t);
                    }

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return channels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.channelThumbnail);
            textView = itemView.findViewById(R.id.channelName);
        }
    }

    class MyImageTask extends AsyncTask<Channel, Void, Bitmap> {

        private Channel mChannel;
        private ViewHolder mViewHolder;
        private static final String PHOTO_IMAGE_URL = "https://hemucode.github.io/LiveTV/thumbnail/";

        public void setViewHolder(ViewHolder myViewHolder) {
            this.mViewHolder = myViewHolder;
        }

        @Override
        protected Bitmap doInBackground(Channel... channels) {
            Bitmap bitmap = null;
            mChannel = channels[0];

            String url = PHOTO_IMAGE_URL + mChannel.getThumbnail();

            InputStream inputStream = null;

            try {
                URL imageUrl = new URL(url);
                inputStream = (InputStream) imageUrl.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mViewHolder.imageView.setImageBitmap(bitmap);
            CacheImageManager.putImage(mContext, mChannel, bitmap);
        }
    }

    public void setAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mContext,mContext.getString(R.string.InterstitialAd), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }


}

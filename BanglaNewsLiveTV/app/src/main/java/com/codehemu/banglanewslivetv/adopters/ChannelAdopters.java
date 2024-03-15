package com.codehemu.banglanewslivetv.adopters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;
import com.codehemu.banglanewslivetv.R;
import com.codehemu.banglanewslivetv.StreamActivity;
import com.codehemu.banglanewslivetv.WebActivity;
import com.codehemu.banglanewslivetv.models.Channel;

import java.util.List;



public class ChannelAdopters extends RecyclerView.Adapter<ChannelAdopters.ViewHolder> {
    List<Channel> channels;
    String type;
    private final Context mContext;
    private static final String PHOTO_IMAGE_URL = "https://hemucode.github.io/LiveTV/thumbnail/";



    public ChannelAdopters(Context mContext, List<Channel> channels, String type) {
        this.channels = channels;
        this.type = type;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ChannelAdopters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (type) {
            case "small":
                v = LayoutInflater.from(mContext).inflate(R.layout.item_small, parent, false);
                break;
            case "paper":
                v = LayoutInflater.from(mContext).inflate(R.layout.item_paper, parent, false);
                break;
            case "length":
                v = LayoutInflater.from(mContext).inflate(R.layout.item_length, parent, false);
                break;
            case "medium":
                v = LayoutInflater.from(mContext).inflate(R.layout.item_medium, parent, false);
                break;
            case "big":
                v = LayoutInflater.from(mContext).inflate(R.layout.item_big, parent, false);
                break;
            default:
                v = LayoutInflater.from(mContext).inflate(R.layout.item_details, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String Category = channels.get(position).getCategory();
        Channel ChannelDataItem = channels.get(position);

        if (holder.textView!=null){
            holder.textView.setText(channels.get(position).getName());
        }

        if (holder.channelDes!= null) {
            holder.channelDes.setText(ChannelDataItem.getDescription());
        }

        if (holder.website!= null){
            holder.website.setText(ChannelDataItem.getWebsite());
            holder.website.setOnClickListener(v -> linkOpen(ChannelDataItem.getWebsite()));
        }
        if (holder.liveUrl != null){
            holder.liveUrl.setText(ChannelDataItem.getLiveTvLink());
            holder.liveUrl.setOnClickListener(v -> linkOpen(ChannelDataItem.getLiveTvLink()));
        }

        if (holder.yt!= null){
            String ytLink =  "https://www.youtube.com/channel/"+ ChannelDataItem.getYoutube();
            holder.yt.setText(ytLink);
            holder.yt.setOnClickListener(v -> linkOpen(ytLink));

        }

        if (holder.fb!= null){
            String fbLink  ="https://www.facebook.com/"+ChannelDataItem.getFacebook();
            holder.fb.setText(fbLink);
            holder.fb.setOnClickListener(v -> linkOpen(fbLink));
        }

        if (holder.email!= null){
            holder.email.setText(ChannelDataItem.getContact());
            holder.email.setOnClickListener(v -> linkOpen("mailto:" + ChannelDataItem.getContact()));
        }

        if (holder.button!= null){
            holder.button.setOnClickListener(v -> v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class)
                    .putExtra("name", ChannelDataItem.getName())
                    .putExtra("description", ChannelDataItem.getDescription())
                    .putExtra("live_url", ChannelDataItem.getLive_url())
                    .putExtra("facebook", ChannelDataItem.getFacebook())
                    .putExtra("youtube",ChannelDataItem.getYoutube())
                    .putExtra("website",ChannelDataItem.getWebsite())
                    .putExtra("category",ChannelDataItem.getCategory())

            ));
        }
        if (holder.cardView!= null){
            Animation animation = AnimationUtils.loadAnimation(holder.cardView.getContext(), android.R.anim.slide_in_left);
            holder.cardView.startAnimation(animation);
            holder.cardView.setOnClickListener(v -> {
                if (Category.equals("ePaper") || Category.equals("web")){
                    Intent t = new Intent(v.getContext(), WebActivity.class);
                    t.putExtra("title",ChannelDataItem.getName());
                    t.putExtra("url",ChannelDataItem.getLive_url());
                    v.getContext().startActivity(t);
                }else {
                    v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class)
                            .putExtra("name", ChannelDataItem.getName())
                            .putExtra("description", ChannelDataItem.getDescription())
                            .putExtra("live_url", ChannelDataItem.getLive_url())
                            .putExtra("facebook", ChannelDataItem.getFacebook())
                            .putExtra("youtube",ChannelDataItem.getYoutube())
                            .putExtra("website",ChannelDataItem.getWebsite())
                            .putExtra("category",ChannelDataItem.getCategory())

                    );
                    if (type.equals("medium")){
                        ((Activity)mContext).finish();
                    }
                }
            });
        }


        String url = PHOTO_IMAGE_URL + ChannelDataItem.getThumbnail();
        Glide.with(mContext).load(url).error(R.drawable.inf).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

    }

    public void linkOpen(String url) {mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));}

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        TextView textView,channelDes,website,liveUrl,yt,fb,email;
        Button button;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.channelThumbnail);
            textView = itemView.findViewById(R.id.channelName);
            cardView = itemView.findViewById(R.id.CardView_item);
            channelDes = itemView.findViewById(R.id.item_channelDes);
            website = itemView.findViewById(R.id.item_website);
            liveUrl = itemView.findViewById(R.id.item_liveUrl);
            yt = itemView.findViewById(R.id.item_yt);
            fb = itemView.findViewById(R.id.item_fb);
            email = itemView.findViewById(R.id.item_email);
            button = itemView.findViewById(R.id.button2);

        }
    }



}
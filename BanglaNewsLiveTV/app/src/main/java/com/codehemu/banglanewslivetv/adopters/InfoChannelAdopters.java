package com.codehemu.banglanewslivetv.adopters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codehemu.banglanewslivetv.R;
import com.codehemu.banglanewslivetv.details;
import com.codehemu.banglanewslivetv.models.Channel;
import com.codehemu.banglanewslivetv.services.CacheImageManager;
import com.codehemu.banglanewslivetv.ytVideoPlay;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class InfoChannelAdopters extends RecyclerView.Adapter<InfoChannelAdopters.ViewHolder> {
    List<Channel> channels;
    String type;
    private Context mContext;

    public InfoChannelAdopters(Context mContext, List<Channel> channels, String type){
        this.channels = channels;
        this.type = type;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public InfoChannelAdopters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.contian_slide_view, parent, false);
        return new InfoChannelAdopters.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoChannelAdopters.ViewHolder holder, int position) {
        int positions = position;
        Channel ChannelDataItem = channels.get(position);
        String Category = channels.get(position).getCategory();
        holder.channelName.setText(ChannelDataItem.getName());
        holder.channelDes.setText(ChannelDataItem.getDescription());
        holder.website.setText(ChannelDataItem.getWebsite());

        holder.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getWebsite()));
                v.getContext().startActivity(linkOpen);
            }
        });
        holder.liveUrl.setText(ChannelDataItem.getLiveTvLink());
        holder.liveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getLiveTvLink()));
                v.getContext().startActivity(linkOpen);
            }
        });
        holder.yt.setText(ChannelDataItem.getYoutube());
        holder.yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getYoutube()));
                v.getContext().startActivity(linkOpen);
            }
        });
        holder.fb.setText(ChannelDataItem.getFacebook());
        holder.fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getFacebook()));
                v.getContext().startActivity(linkOpen);
            }
        });
        holder.email.setText(ChannelDataItem.getContact());
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+ChannelDataItem.getContact()));
                v.getContext().startActivity(linkOpen);
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Category.equals("yt")) {
                    Intent t = new Intent(v.getContext(), ytVideoPlay.class);
                    t.putExtra("channel", channels.get(positions));
                    v.getContext().startActivity(t);
                } else {
                    Intent p = new Intent(v.getContext(), details.class);
                    p.putExtra("channel", channels.get(positions));
                    v.getContext().startActivity(p);
                }

            }
        });



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


    }



    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView channelName,channelDes,website,liveUrl,yt,fb,email;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imageView);
            channelName = itemView.findViewById(R.id.item_channelName);
            channelDes = itemView.findViewById(R.id.item_channelDes);
            website = itemView.findViewById(R.id.item_website);
            liveUrl = itemView.findViewById(R.id.item_liveUrl);
            yt = itemView.findViewById(R.id.item_yt);
            fb = itemView.findViewById(R.id.item_fb);
            email = itemView.findViewById(R.id.item_email);
            button = itemView.findViewById(R.id.button2);
        }
    }


    class MyImageTask extends AsyncTask<Channel, Void, Bitmap> {

        private Channel mChannel;
        private InfoChannelAdopters.ViewHolder mViewHolder;
        private static final String PHOTO_IMAGE_URL = "https://hemucode.github.io/LiveTV/thumbnail/";

        public void setViewHolder(InfoChannelAdopters.ViewHolder myViewHolder) {
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
}


package com.codehemu.banglanewslivetv.adopters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;

import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import com.codehemu.banglanewslivetv.R;

import com.codehemu.banglanewslivetv.WebActivity;
import com.codehemu.banglanewslivetv.services.Preferences;
import com.codehemu.banglanewslivetv.services.ShortsDataService;

import java.util.ArrayList;

public class ShortsAdopters extends PagerAdapter {
    Preferences preferences;
    ShortsDataService service;
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> titles;
    ArrayList<String> des;
    ArrayList<String> images;
    ArrayList<String> links;

    public ShortsAdopters(Context context,  ArrayList<String> title, ArrayList<String> desc, ArrayList<String> image, ArrayList<String> link) {
        this.context = context;
        this.titles = title;
        this.des = desc;
        this.images = image;
        this.links = link;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.preferences = new Preferences(context);
        this.service = new ShortsDataService(context);


    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_short,container,false);
        ImageView imageView  = itemView.findViewById(R.id.imageView);
        ImageView imageView1  = itemView.findViewById(R.id.imageView2);
        TextView textView =itemView.findViewById(R.id.headline);
        TextView textView1 =itemView.findViewById(R.id.desc);
        TextView textView2 =itemView.findViewById(R.id.textView6);

        ImageView imageView4  = itemView.findViewById(R.id.imageView4);
        imageView4.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = layoutInflater.inflate(R.layout.rss,null);
            builder.setIcon(R.drawable.whatshot);
            builder.setTitle(R.string.short_categories);
            Spinner spinner = view.findViewById(R.id.spinner);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,context.getResources().getStringArray(R.array.rssList));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            Button button = view.findViewById(R.id.save);
            Button button1 = view.findViewById(R.id.back);
            spinner.setSelection(preferences.getRssArrayLinkNumber());
            builder.setView(view);
            AlertDialog mDialog =  builder.create();
            mDialog.show();

            button.setOnClickListener(v1 -> {
                if (spinner.getSelectedItemPosition()!=0){
                    preferences.setRssArrayLinkNumber(spinner.getSelectedItemPosition());
                    Toast.makeText(context, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    service.getRssData(new ShortsDataService.OnDataResponse() {
                        @Override
                        public void onError(String error) {
                            Log.d(TAG, "1onError:" + error);
                        }

                        @Override
                        public void onProcess(int i) {

                        }

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "1onResponse:" + response);
                        }

                        @Override
                        public void onPostExecute() {
                            Toast.makeText(context, "Please wait..", Toast.LENGTH_SHORT).show();
                            service.getShortsData(new ShortsDataService.OnShortDataResponse() {
                                @Override
                                public void onError(String error) {
                                    Log.d(TAG, "1onError:" + error);
                                }

                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "1onResponse:" + response);
                                }

                                @Override
                                public void onPostExecute() {
                                    ((Activity)context).recreate();
                                }
                            });
                        }
                    });

                    mDialog.dismiss();
                }
            });
            button1.setOnClickListener(v12 -> mDialog.dismiss());

        });

        textView.setText(titles.get(position));
        textView1.setText(des.get(position) + ".....");

        Glide.with(context).load(images.get(position))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.inf)
                .centerCrop().into(imageView);
        Glide.with(context).load(images.get(position))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.inf)
                .centerCrop().override(12,12).into(imageView1);

        textView2.setOnClickListener(v -> {
            Intent t = new Intent(v.getContext(), WebActivity.class);
            t.putExtra("title","Short");
            t.putExtra("url",links.get(position));
            v.getContext().startActivity(t);
        });

        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}

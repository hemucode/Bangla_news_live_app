package com.codehemu.banglanewslivetv;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.Preferences;

import java.util.Objects;

public class MenuActivity extends AppCompatActivity {
    ImageView imageView;
    Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,button11,button12,button13;
    String appsName,packageName;
    Preferences preferences;
    boolean  dark = false;
    int themeNo;
    ThemeConstant themeConstant;
    String color;
    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(MenuActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_menu);

        Objects.requireNonNull(getSupportActionBar()).hide();

        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });


        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        button10 = findViewById(R.id.button10);
        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);

        imageView = findViewById(R.id.imageView5);
        imageView.setOnClickListener(v -> onBackPressedDispatcher.onBackPressed());
        button1.setOnClickListener(v -> onBackPressedDispatcher.onBackPressed());
        button2.setOnClickListener(v -> openLink(getString(R.string.policy_url)));
        button3.setOnClickListener(v -> openListingActivity("containing"));
        button4.setOnClickListener(v -> disclaimerOpen());
        button5.setOnClickListener(v -> ShareAppLink());
        button6.setOnClickListener(v -> openLink("https://play.google.com/store/apps/details?id=" + this.packageName));
        button7.setOnClickListener(v -> openLink("https://play.google.com/store/apps/dev?id=7464231534566513633"));
        button8.setOnClickListener(v -> startActivity(new Intent(MenuActivity.this, AboutActivity.class)));
        button9.setOnClickListener(v -> openLink("https://www.codehemu.com/"));
        button10.setOnClickListener(v -> openLink("https://www.facebook.com/codehemu/"));
        button11.setOnClickListener(v -> openLink("https://www.youtube.com/c/HemantaGayen"));
        button12.setOnClickListener(v -> openListingActivity("bengaliPaper"));
        button13.setOnClickListener(v -> openListingActivity("englishNews"));

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

    private void openListingActivity(String listType) {
        startActivity(new Intent(MenuActivity.this, ListingActivity.class).
                putExtra("ListType",listType));
    }

    private void ShareAppLink() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", this.appsName);
        share.putExtra("android.intent.extra.TEXT", this.appsName + getString(R.string.download_it) + " https://play.google.com/store/apps/details?id=" + this.packageName);
        this.startActivity(Intent.createChooser(share, getString(R.string.share_it)));
    }
    public void openLink(String url) {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));}

    private void disclaimerOpen(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_disclaimer);
        LinearLayout linearLayout = dialog.findViewById(R.id.dismiss);
        linearLayout.setOnClickListener(v -> dialog.cancel());
        TextView email_click = dialog.findViewById(R.id.email_click);
        email_click.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String emailID = getString(R.string.my_email);
            String AppNAME = getString(R.string.app_name);
            Uri data = Uri.parse("mailto:"
                    + emailID
                    + "?subject=" +AppNAME+ " Feedback" + "&body=" + AppNAME);
            intent.setData(data);
            startActivity(intent);
        });
        dialog.show();
    }
}
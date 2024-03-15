package com.codehemu.banglanewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.codehemu.banglanewslivetv.models.ThemeConstant;
import com.codehemu.banglanewslivetv.services.Preferences;
import com.codehemu.banglanewslivetv.services.ShortsDataService;
import com.monstertechno.adblocker.BuildConfig;

import java.util.ArrayList;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    Preferences preferences;
    TextView text_themename, text_version;
    RelativeLayout rel_main, rel_theme, rel_showext, rel_color, rel_convention, rel_rate, rel_removeads, rel_feedback, rel_app_version;
    private final OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
    SwitchCompat switchbtn;
    String packageName,appsName;
    boolean flag = false;
    ThemeConstant themeConstant;
    int themeNo;
    ArrayList<String> colors = new ArrayList<>();

    ShortsDataService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(SettingsActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.circle);
        assert unwrappedDrawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (themeNo != 0) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(preferences.getCircleColor()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#0063B3"));
        }

        service = new ShortsDataService(this);

        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        colors.add("#f44236");
        colors.add("#ea1e63");
        colors.add("#9d27b2");
        colors.add("#673bb7");
        colors.add("#1029AD");
        colors.add("#0063B3");
        colors.add("#04a8f5");
        colors.add("#00bed2");
        colors.add("#009788");
        colors.add("#00D308");
        colors.add("#ff9700");
        colors.add("#FFC000");
        colors.add("#D2E41D");
        colors.add("#fe5722");
        colors.add("#5E4034");

        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });

        rel_color = findViewById(R.id.rel_color);
        rel_convention = findViewById(R.id.rel_convention);
        rel_rate = findViewById(R.id.rel_rate);
        rel_removeads = findViewById(R.id.rel_removeads);
        rel_theme = findViewById(R.id.rel_theme);
        rel_feedback = findViewById(R.id.rel_feedback);
        rel_app_version = findViewById(R.id.rel_app_version);
        text_version = findViewById(R.id.text_version);
        switchbtn = findViewById(R.id.switchbtn);
        text_themename = findViewById(R.id.text_themename);
        rel_showext = findViewById(R.id.rel_showext);
        rel_main = findViewById(R.id.rel_main);

        rel_showext.setOnClickListener(this);
        rel_color.setOnClickListener(this);
        rel_convention.setOnClickListener(this);
        rel_rate.setOnClickListener(this);
        rel_removeads.setOnClickListener(this);
        rel_theme.setOnClickListener(this);
        rel_feedback.setOnClickListener(this);
        rel_app_version.setOnClickListener(this);

        this.packageName = getApplication().getPackageName();
        this.appsName = getApplication().getString(R.string.app_name);

        if (preferences.getMode()) {
            flag = true;
            text_themename.setText(getResources().getString(R.string.dark));
        }
        switchbtn.setChecked(preferences.getSwitchState());
        switchbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchbtn.setChecked(true);
                preferences.setSwitchState(true);
            } else {
                switchbtn.setChecked(false);
                preferences.setSwitchState(false);
            }
        });

        text_version.setText(BuildConfig.VERSION_NAME);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressedDispatcher.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_color:
                showColorDialog();
                break;

            case R.id.rel_theme:
                showDialogBox();
                break;

            case R.id.rel_convention:
                RssSpinner();
                break;

            case R.id.rel_showext:
                if (switchbtn.isChecked()) {
                    switchbtn.setChecked(false);
                    preferences.setSwitchState(false);
                } else {
                    switchbtn.setChecked(true);
                    preferences.setSwitchState(true);
                }
                break;

            case R.id.rel_removeads:
                disclaimerOpen();
                break;
            case R.id.rel_rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)));
                break;
            case R.id.rel_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for "+appsName+" App");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.my_email)});
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Send Feedback Email"));
                break;

            case R.id.rel_app_version:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.appversionis) + " " + BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
                break;


        }

    }

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

    public void showColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(SettingsActivity.this);
        colorPicker.setColors(colors).setColumns(5).setDefaultColorButton(Color.parseColor(preferences.getCircleColor())).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                preferences.setThemeNo(position + 1);
                preferences.setCircleColor(colors.get(position));
                recreate();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }
    public void showDialogBox() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        Button btn_cancel;
        RadioButton radio1, radio2;
        dialog.setContentView(R.layout.dialog_mode);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        radio1 = dialog.findViewById(R.id.radio1);
        radio2 = dialog.findViewById(R.id.radio2);
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        if (flag) {
            radio2.setChecked(true);
        } else {
            radio1.setChecked(true);
        }
        radio1.setOnClickListener(v -> {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            preferences.setMode(false);
            recreate();
            dialog.dismiss();

        });
        radio2.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            preferences.setMode(true);
            recreate();
            dialog.dismiss();

        });
        dialog.show();
    }

    public void RssSpinner(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.rss,null);
        builder.setIcon(R.drawable.whatshot);
        builder.setTitle(R.string.short_categories);
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.rssList));
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
                Toast.makeText(this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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

                           }
                       });
                   }
               });

                mDialog.dismiss();
            }
        });
        button1.setOnClickListener(v12 -> mDialog.dismiss());
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


}
package com.pk.developer.downloader.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.pk.developer.downloader.R;


public class SplashScreen extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "IsFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String userID = prefs.getString("isFirstTime", "0");
//                if (userID.equals("0")) {
//                    startActivity(new Intent(SplashScreen.this, OnBoardingScreenActivity.class));
//                    finish();
//                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
//                }
            }
        }, 2500);  // Give
    }
}

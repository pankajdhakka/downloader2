package com.pk.developer.downloader.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pk.developer.downloader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class Twitter extends AppCompatActivity {

    EditText inputURl;
    Button BtnDownload;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiktok);
        inputURl = findViewById(R.id.input_TiktokURL);


        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        BtnDownload = findViewById(R.id.btn_downloadTiktokVideo);
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }

                    final String URL = inputURl.getText().toString();
                    TwitterVideoDownloader downloader = new TwitterVideoDownloader(Twitter.this, URL);
                    downloader.DownloadVideo();
                }catch (Exception e){
                }
            }
        });
        final Button btnPaste = (Button) findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (inputURl.getText().toString().length()!=0){
                        inputURl.getText().clear();
                    }else {
                        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData abc = myClipboard.getPrimaryClip();
                        if (abc !=null){
                            ClipData.Item item = abc.getItemAt(0);
                            String text = item.getText().toString();
                            inputURl.setText(text);
                        }
                    }
                }catch (Exception e){
                }
            }
        });

        Button open_tiktok=findViewById(R.id.open_tiktok);
        open_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.twitter.android"));
                        startActivity(intent);
                    }
                }catch (Exception e){
                }
            }
        });
    }

    private boolean pasteDataFromURL(String obj) {
        if (obj.toLowerCase().contains("tiktok") || obj.toLowerCase().contains("musical.ly")) {

            return true;
        }
        return  false;
    }
}

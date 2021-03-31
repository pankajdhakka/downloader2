package com.pk.developer.downloader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.pk.developer.downloader.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.tabs.TabLayout;

public class WaActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;
    view_pager_adapter pager_adapter;
    InterstitialAd mInterstitialAd;
    int backpress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wa);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        pager_adapter = new view_pager_adapter(getSupportFragmentManager());
        pager_adapter.add_fragments(new PicturesFragment(), "Pictures");
        pager_adapter.add_fragments(new VideosFragment(), "Videos");
        viewPager.setAdapter(pager_adapter);
        tabLayout.setupWithViewPager(viewPager);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        viewPager.getAdapter().notifyDataSetChanged();
        super.onResume();
    }

}

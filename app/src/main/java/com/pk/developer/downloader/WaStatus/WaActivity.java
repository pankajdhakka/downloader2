package com.pk.developer.downloader.WaStatus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pk.developer.downloader.R;
import com.cuberto.bubbleicontabbarandroid.TabBubbleAnimator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WaActivity extends AppCompatActivity {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private TabBubbleAnimator tabBubbleAnimator;
    private String[] titles = new String[]{"Image Saver", "Video Status"};
    private int[] colors = new int[]{R.color.home, R.color.clock};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tik_tok_download);
        mFragmentList.add(new NewPictureFragment(titles[0], colors[0]));
        mFragmentList.add(new NewVideosFragment(titles[1], colors[1]));
        ViewPager viewPager = findViewById(R.id.viewPager);

        TextView nameTitle = findViewById(R.id.nameTitle);
        nameTitle.setText("Whatsapp Status");

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabBubbleAnimator = new TabBubbleAnimator(tabLayout);
        tabBubbleAnimator.addTabItem(titles[0], R.drawable.ic_grid, colors[0]);
        tabBubbleAnimator.addTabItem(titles[1], R.drawable.ic_clock,colors[1]);
        tabBubbleAnimator.highLightTab(0);
        viewPager.addOnPageChangeListener(tabBubbleAnimator);


        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tabBubbleAnimator.onStart((TabLayout) findViewById(R.id.tabLayout));
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabBubbleAnimator.onStop();
    }

}
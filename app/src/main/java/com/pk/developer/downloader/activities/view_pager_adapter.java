package com.pk.developer.downloader.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class view_pager_adapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments=new ArrayList<>();
    ArrayList<String> tab_titles=new ArrayList<>();

    public void add_fragments(Fragment fragments,String tab_titles)
    {
        this.fragments.add(fragments);
        this.tab_titles.add(tab_titles);
    }
    public  view_pager_adapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles.get(position);
    }
}


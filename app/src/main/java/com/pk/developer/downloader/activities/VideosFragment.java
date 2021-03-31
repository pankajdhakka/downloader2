package com.pk.developer.downloader.activities;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pk.developer.downloader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;

public class VideosFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private static final String WHATSAPP_STATUSES_LOCATION = "WhatsApp/Media/.Statuses";

    public VideosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_videos, container, false);
        MobileAds.initialize(getActivity(),
                getString(R.string.adid));
        AdView mAdView = (AdView)v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recyclerView=(RecyclerView)v.findViewById(R.id.videos_recyclerView);
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),2));
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath()+"/WhatsApp/Media/.Statuses/";
        getListFiles(new File(path));
        VideoRecyclerAdapter ra = new VideoRecyclerAdapter(this.getListFiles(new File(path)), getActivity());
        recyclerView.setAdapter(ra);
        return v;
    }
    private ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4") ||
                        file.getName().endsWith(".gif")) {
                    if (!inFiles.contains(file))
                        inFiles.add(file);
                }
            }
        }
        return inFiles;
    }


}

